package chrisbaume.owa;

import java.util.ArrayList;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import java.util.Map;
import java.util.HashMap;
import org.apache.http.cookie.Cookie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

public class Account {

  private String baseUrl;
  private String userContext;
  private HttpClientContext session;
  private Map<String,Folder> folders;
  private ArrayList<Recipient> recentRecipients;
  
  protected CloseableHttpResponse sendRequest(HttpRequestBase request) {
    CloseableHttpResponse response = null;
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      response = httpclient.execute(request, session);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (response.containsHeader("X-OWA-Error")) {
      System.err.println(response.getFirstHeader("X-OWA-Error").getValue());
    }
    return response;
  }
  
  public void sendEmail(String to, String cc, String bcc, String subject, String body)
  {
    HttpPost httpPost = new HttpPost(baseUrl+"?ae=PreFormAction&t=IPM.Note&a=Send");
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("txtto", to));
    nvps.add(new BasicNameValuePair("txtcc", cc));
    nvps.add(new BasicNameValuePair("txtbcc", bcc));
    nvps.add(new BasicNameValuePair("txtsbj", subject));
    nvps.add(new BasicNameValuePair("txtbdy", body));
    nvps.add(new BasicNameValuePair("hidunrslrcp", "0"));
    nvps.add(new BasicNameValuePair("hidmsgimp", "1"));
    nvps.add(new BasicNameValuePair("hidpid", "EditMessage"));
    nvps.add(new BasicNameValuePair("hidcanary", getUserContext()));
    nvps.add(new BasicNameValuePair("hidcmdpst", "snd"));
    try {
      httpPost.setEntity(new UrlEncodedFormEntity(nvps));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    sendRequest(httpPost);
  }
  
  public void loadRecentRecipients() {
    Document doc = getDocument(new HttpGet(baseUrl+"?ae=Item&t=IPM.Note&a=New"));
    Elements rows = doc.select("#selmrr option");
    recentRecipients = new ArrayList<Recipient>();
    for (Element row: rows) {
      recentRecipients.add(new Recipient(row.text(), row.attr("title")));
      System.out.println(row.text()+"\t"+row.attr("title"));
    }    
  }
  
  public Folder getFolder(String name) {
    if (folders == null) loadFolders();
    return folders.get(name);
  }
  
  public Document getDocument(HttpRequestBase request)
  {
    Document doc = null;
    CloseableHttpResponse response = sendRequest(request);
    try {
      doc = Jsoup.parse(response.getEntity().getContent(), null, request.getURI().toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return doc;
  }
  
  public void loadFolders() {
    HttpPost httpPost = new HttpPost(baseUrl+"?ae=Folder");
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("hidactbrfld", "1"));
    nvps.add(new BasicNameValuePair("hidpid", "MessageView"));
    nvps.add(new BasicNameValuePair("hidcanary", getUserContext()));
    try {
      httpPost.setEntity(new UrlEncodedFormEntity(nvps));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    Document doc = getDocument(httpPost);
    
    folders = new HashMap<>();
    Elements rows = doc.select("#selbrfld option");
    for (Element row: rows) {
      String folderUrl = baseUrl+"?ae=Folder&t="+row.attr("value");
      String folderName = row.attr("title");
      folders.put(folderName, new Folder(this, folderUrl, folderName));
    }
  }
  
  protected String getUserContext() {
    if (userContext != null) return userContext;
    
    HttpGet request = new HttpGet(baseUrl);
    CloseableHttpResponse response = sendRequest(request);
    
    List<Cookie> cookies = session.getCookieStore().getCookies();
    for (Cookie cookie: cookies) {
      if (cookie.getName().matches("UserContext")) {
        userContext = cookie.getValue();
        return userContext;
      }
    }
    // System.err.println(cookies);
    // System.err.println(response);
    return null;
  }
  
  public Account(String url, String username, String password)
  {
    session = HttpClientContext.create();
    
    HttpPost httpPost = new HttpPost(url);
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("curl", "Z2FowaZ2F"));
    nvps.add(new BasicNameValuePair("flags", "0"));
    nvps.add(new BasicNameValuePair("forcedownlevel", "0"));
    nvps.add(new BasicNameValuePair("formdir", "1"));
    nvps.add(new BasicNameValuePair("trusted", "4"));
    nvps.add(new BasicNameValuePair("chkBsc", "1"));
    nvps.add(new BasicNameValuePair("username", username));
    nvps.add(new BasicNameValuePair("password", password));
    
    try {
      httpPost.setEntity(new UrlEncodedFormEntity(nvps));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    CloseableHttpResponse response = sendRequest(httpPost);
    
    if (response.getStatusLine().getStatusCode() == 302) {    
      baseUrl = response.getFirstHeader("Location").getValue();
    } else {
      System.err.println("Login failed");
    }
  }
}
