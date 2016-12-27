package chrisbaume.owa;

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

// http
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.methods.HttpGet;

public class Email {

  private Page page;
  private String id;
  private String from;
  private String subject;
  private Date date;
  private int sizeKB;
  private String type;
  private String body;
  private String url;
  private ArrayList<String> to;
  private ArrayList<String> cc;
  
  Email(String url)
  {
    loadEmail();
  }
  
  Email(Page page_in, String id_in, String from_in, String subject_in, Date date_in, int sizeKB_in, String type_in, String url_in)
  {
    page = page_in;
    id = id_in;
    from = from_in;
    subject = subject_in;
    date = date_in;
    sizeKB = sizeKB_in;
    type = type_in;
    url = url_in;
  }
  
  public String getBody()
  {
    if (body == null) loadEmail();
    return body;
  }

  protected void loadEmail() {
    Document doc =  page.getFolder().getAccount().getDocument(new HttpGet(url));
    to = new ArrayList<String>();
    cc = new ArrayList<String>();

    subject = doc.select("td.sub").text();
    from = doc.select("td.frm").text();
    body = doc.select("div.bdy").html();
    date = Utils.parseDate(doc.select("td.hdtxnr:eq(0)").text());
    Elements toList = doc.select("#divTo a");
    for (Element toItem: toList) {
      to.add(toItem.text());
    }
    Elements ccList = doc.select("#divCc a");
    for (Element ccItem: ccList) {
      cc.add(ccItem.text());
    }
  }

}
