/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'Chris' at '19/11/16 14:56' with Gradle 3.2
 *
 * @author Chris, @date 19/11/16 14:56
 */
package chrisbaume;

// parsing
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

// login
import org.apache.http.client.methods.HttpPost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import java.util.List;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

public class OWA {

  public Date parseDate(String string) {
    Date result = new Date();
    DateFormat format = new SimpleDateFormat("M/d/y h:m a");
    try {
      result = format.parse(string);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  public ArrayList<Map> parseList(String html) {
    ArrayList<Map> results = new ArrayList<Map>();
    Document doc = Jsoup.parse(html);
    Elements rows = doc.select("div.cntnt table tr:gt(2)");
    for (Element row: rows) {
      Elements cols = row.select("td");
      Map result = new HashMap();
      result.put("id", cols.eq(3).select("input").attr("value"));
      result.put("name", cols.eq(4).text().replace("\u00a0",""));
      result.put("subject", cols.eq(5).text().replace("\u00a0",""));
      result.put("date", parseDate(cols.eq(6).text().replace("\u00a0"," ")));
      result.put("size", cols.eq(7).text().replace("\u00a0",""));
      results.add(result);
    }
    return results;
  }

  public Map parseEmail (String html) {
    Document doc = Jsoup.parse(html);
    Map result = new HashMap();
    ArrayList<String> toArray = new ArrayList<String>();
    ArrayList<String> ccArray = new ArrayList<String>();

    result.put("subject", doc.select("td.sub").text());
    result.put("from", doc.select("td.frm").text());
    result.put("date", doc.select("td.hdtxnr:eq(0)").text());
    Elements toList = doc.select("#divTo a");
    for (Element toItem: toList) {
      toArray.add(toItem.text());
    }
    Elements ccList = doc.select("#divCc a");
    for (Element ccItem: ccList) {
      ccArray.add(ccItem.text());
    }
    result.put("to", toArray);
    result.put("cc", ccArray);
    return result;
  }

  public ArrayList<String> parseRecentRecipients(String html) {
    Document doc = Jsoup.parse(html);
    ArrayList<String> emails = new ArrayList<String>(); 
    Elements rows = doc.select("#selmrr option");
    for (Element row: rows) {
      emails.add(row.text()+" <"+row.attr("title")+">");
    }
    return emails;
  }
  
  public CookieStore login(String url, String username, String password) {
    CookieStore cookieStore = new BasicCookieStore();
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpClientContext context = HttpClientContext.create();
    
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
      CloseableHttpResponse response1 = httpclient.execute(httpPost, context);
      cookieStore = context.getCookieStore();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cookieStore;
  }
}
