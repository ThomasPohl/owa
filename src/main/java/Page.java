package chrisbaume.owa;


import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.apache.http.client.methods.HttpGet;
import java.util.Date;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class Page {
  private int number;
  private String url;
  private Folder folder;
  private ArrayList<Email> emails;
  
  public int getNumEmails() {
    if (emails == null) loadEmails();
    return emails.size();
  }
  
  public Folder getFolder() {
    return folder;
  }
  
  protected void loadEmails() {
    Document doc = folder.getAccount().getDocument(new HttpGet(url));
    Elements rows = doc.select("div.cntnt table tr:gt(1)");
    emails = new ArrayList<Email>();
    for (Element row: rows) {
      Elements cols = row.select("td");
      String id = cols.eq(3).select("input").attr("value");
      String from = cols.eq(4).text().replace("\u00a0","");
      String type = cols.eq(5).select("a").attr("onclick");
      int typeStart = type.indexOf("'")+1;
      int typeEnd = type.indexOf("'", typeStart);
      type = type.substring(typeStart, typeEnd);
      String subject = cols.eq(5).text().replace("\u00a0","");
      Date date = Utils.parseDate(cols.eq(6).text().replace("\u00a0"," "));
      int sizeKB = Integer.parseInt(cols.eq(7).text().replace(" KB\u00a0",""));
      String emailUrl = null;
      try {
        emailUrl = URLEncoder.encode(folder.getBaseUrl()+"?ae=Item&t=" + type + "&id=" + id, "UTF-8");
        System.out.println(emailUrl);
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      emails.add(new Email(this, id, from, subject, date, sizeKB, type, emailUrl));
    }
  }
  
  public ArrayList<Email> getEmails() {
    if (emails == null) loadEmails();
    return emails;
  }
  
  Page(Folder folder_in, int number_in, String url_in) {
    folder = folder_in;
    number = number_in;
    url = url_in;
  }
}