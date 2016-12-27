# Outlook Web App parser

Java library which wraps OWA

## Example usage

    import chrisbaume.owa.Account;
    import chrisbaume.owa.Email;
    import java.util.ArrayList;
    
    Account acc = new Account("https://path.to/email-login", "username", "password");
    ArrayList<Email> emails = acc.getFolder("Inbox").getPage(1).getEmails();
    for (Email email: email) {
      System.out.println(email.getSubject()+"  "+email.getFrom());
    }

## Build

    gradle build