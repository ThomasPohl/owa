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

import org.apache.http.cookie.Cookie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

public class Account {

  private String baseUrl;
  private String userContext;
  private HttpClientContext session;
  private ArrayList<Folder> folders;
  private ArrayList<Recipient> recentRecipients;
  
  // public ArrayList<String> parseRecentRecipients(InputStream html, String url) {
    // Document doc = parseHtml(html, url);
    // ArrayList<String> emails = new ArrayList<String>(); 
    // Elements rows = doc.select("#selmrr option");
    // for (Element row: rows) {
      // emails.add(row.text()+" <"+row.attr("title")+">");
    // }
    // return emails;
  // }
  
  public Document getDocument(HttpRequestBase request)
  {
    Document doc = null;
    CloseableHttpResponse response = null;
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      response = httpclient.execute(request, session);
      try {
        doc = Jsoup.parse(response.getEntity().getContent(), null, request.getURI().toString());
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        response.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return doc;
  }
  
  public void loadFolders() {
    System.out.println(getUserContext());
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
    
    folders = new ArrayList<Folder>();
    Elements rows = doc.select("#selbrfld option");
    for (Element row: rows) {
      String folderUrl = baseUrl+"?ae=Folder&t="+row.attr("value");
      String folderName = row.attr("title");
      System.out.println(folderUrl);
      System.out.println(folderName);
      folders.add(new Folder(this, folderUrl, folderName));
    }
  }
  
  protected String getUserContext() {
    if (userContext != null) return userContext;
    
    CloseableHttpResponse response = null;
    HttpGet request = new HttpGet(baseUrl);
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      response = httpclient.execute(request, session);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    List<Cookie> cookies = session.getCookieStore().getCookies();
    for (Cookie cookie: cookies) {
      if (cookie.getName().matches("UserContext")) {
        userContext = cookie.getValue();
        return userContext;
      }
    }
    
    System.err.println("Could not fetch user context");
    System.err.println(cookies);
    return null;
  }
  
  public Account(String url, String username, String password)
  {
    CloseableHttpResponse response = null;
    CloseableHttpClient httpclient = HttpClients.createDefault();
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
      response = httpclient.execute(httpPost, session);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        response.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    if (response.getStatusLine().getStatusCode() == 302) {    
      baseUrl = response.getFirstHeader("Location").getValue();
    }
  }
}
