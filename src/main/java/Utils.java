package chrisbaume.owa;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Utils {
  
  public static Date parseDate(String string)
  {
    Date result = new Date();
    DateFormat format = new SimpleDateFormat("M/d/y h:m a");
    try {
      result = format.parse(string);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }
}