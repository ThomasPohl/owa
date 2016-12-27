package chrisbaume.owa;


import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.select.Elements;

public class Folder {
  private Account account;
  private String url;
  private String name;
  private int numPages;
  private ArrayList<Page> pages;
  
  public Page getPage(int pageNum) {
    if (pages == null) loadPages();
    return pages.get(pageNum-1);
  }
  
  public int getNumPages() {
    if (numPages == 0) loadPages();
    return numPages;
  }
  
  public String getName() {
    return name;
  }
  
  public Account getAccount() {
    return account;
  }
  
  public void loadPages() {
    Document doc = account.getDocument(new HttpGet(url));
    
    Elements lastPageLink = doc.select("#lnkLstPg");
    if (lastPageLink.isEmpty()) {
      numPages = 1;
    } else {
      String lastPage = lastPageLink.attr("onclick");
      int numStart = lastPage.indexOf("'")+1;
      int numEnd = lastPage.indexOf("'", numStart);
      numPages = Integer.parseInt(lastPage.substring(numStart, numEnd));
    }
    
    pages = new ArrayList<Page>();
    for (int i=0; i<numPages; i++) {
      String pageUrl = url+"&pg="+Integer.toString(i+1);
      pages.add(new Page(this, i+1, pageUrl));
    }
  }
  
  public String getBaseUrl() {
    return url.substring(0, url.lastIndexOf("/")+1);
  }
  
  public Folder(Account account_in, String url_in, String name_in) {
    account = account_in;
    url = url_in;
    name = name_in;
  }
}