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

## Development plan

Please create an issue to add to this list.

* ~~Login~~
* ~~List folders~~
* ~~List emails~~
* ~~Read email~~
* ~~Send email~~
* ~~List recent recipients~~
* Reply (all) to email
* Forward email
* Delete email
* List all contacts
* Move/copy email
* Add attachment
* View and set (un)read status
* View and set flags
* Mark as spam
* View calendar
* View appointment/meeting
* Add appointment
* Send meeting request
* Email threads
* Search address books
