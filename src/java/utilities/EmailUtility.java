
package utilities;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * A simple class used to send email. Loads the host name, port, username, and 
 * password from the properties file.
 *
 * Basic usage - to send a simple email:
 *
 *  <CODE>EmailUtility.email(user@host.com, message, subject);</CODE>
 *
 * Also can be used to send the same email to multiple participants, using the
 * same method, but supplying a vector of recipients:
 * 
 *  <CODE>EmailUtility.email(Collection{@literal <}String{@literal >} users, message, subject);</CODE>
 *
 * @author Bloomsburg University Software Engineering
 * @author Joe Sharp (2008)
 * @author Dave Moser (2008)
 * @author Curt Jones (2015) -- Rewritten for Spring 2015
 * @version Spring 2015
 */
public class EmailUtility {

    /**
     * A simple method used to send an automated email to the given recipients 
     * from the default web server, using the default account and password found
     * in the general properties file.
     *
     * @param recipients a vector containing 1 or more email addresses to which
     * this email will be sent.
     * @param mailMessage the message to be contained in the body of the email
     * @param subject the subject line of the email
     */
    public static void email(Collection<String> recipients, String mailMessage, String subject) {
        email(recipients, null, mailMessage, subject);
    }
    
    /**
     * A simple method used to send an automated email to the given recipients 
     * and cc recipients from the default web server, using the default account
     * and password found in the general properties file
     *
     * @param recipients a vector containing 1 or more email addresses to which
     * this email will be sent.
     * @param ccrecipients a vector containing 1 or more email addresses to which
     * this email will be cc too.
     * @param mailMessage the message to be contained in the body of the email
     * @param subject the subject line of the email
     */
    public static void email(Collection<String> recipients, Collection<String> ccrecipients, String mailMessage, String subject) {
     //Use static varaibles above when property manager is loaded first
     String SMTP_HOST_NAME = PropertyManager.getProperty("SMTP_HOST_NAME");
     String SMTP_HOST_PORT = PropertyManager.getProperty("SMTP_HOST_PORT");
     String SMTP_AUTH_USER = PropertyManager.getProperty("SMTP_AUTH_USER");
     String SMTP_AUTH_PWD  = PropertyManager.getProperty("SMTP_AUTH_PWD");
     String SMTP_USER_ACCOUNT = PropertyManager.getProperty("SMTP_USER_ACCOUNT");
     try {

            Properties props = new Properties();
       //Set Protocol  
            props.put("mail.transport.protocol","smtp");
            
      //Set the smtp host to the host provided in the general properties file
            props.put("mail.smtp.host", SMTP_HOST_NAME);

       //If a port is specified in the properties file, set the port property
            if(SMTP_HOST_PORT != null && !SMTP_HOST_PORT.isEmpty())
                      props.put("mail.smtp.port", SMTP_HOST_PORT);
            
      //Determine if username and password have been set, if so set auth to be true.
            boolean auth = SMTP_AUTH_USER != null && !SMTP_AUTH_USER.isEmpty() &&
                    SMTP_AUTH_PWD != null && !SMTP_AUTH_PWD.isEmpty();

     //If auth is true, set the appropriate property to true.
            props.put("mail.smtp.auth", auth);
            
            props.put("mail.smtps.quitwait","false");//prevents ssl execption if GMail is used
   //         props.put("mail.smtp.starttls.enable", "true");// See documentation
            
            //Create a new mail session with the set properties
            Session mailSession = Session.getDefaultInstance(props);
 //           mailSession.setDebug(true); // Gives detailed output

            //Build the message.
            MimeMessage message = new MimeMessage(mailSession);
            //Set the email message to originate from this default address
            if(SMTP_AUTH_USER == null || !SMTP_AUTH_USER.isEmpty())
                    message.setFrom(new InternetAddress("cjones@bloomu.edu"));
            else message.setFrom(new InternetAddress(SMTP_AUTH_USER));
            message.setSubject(subject);
            message.setText(mailMessage);
            
        //Add all given recipients.
            for(String toAddress:recipients) {
        //   Debug.println("Adding #"+toAddress+"#");
                 message.addRecipients(Message.RecipientType.TO,
				InternetAddress.parse(toAddress));
            }
            
            if(ccrecipients != null){
                for(String ccAddress:ccrecipients) {
                    message.addRecipients(Message.RecipientType.CC,
				InternetAddress.parse(ccAddress));
                }
            }
            
            Transport transport=mailSession.getTransport();

            //If a user name and password were specified in the propertied file,
            //use them to connect to the mail server.
            if(auth)
                transport.connect(SMTP_HOST_NAME, SMTP_AUTH_USER, SMTP_AUTH_PWD);
            else//Otherwise just connect without authentication.
                transport.connect();

            message.saveChanges(); //very important according to the documentation
            
            transport.sendMessage(message, message.getAllRecipients());

            transport.close();

        } catch (MessagingException ex) {
           WebErrorLogger.log(Level.SEVERE, 
             "Could not send mail in email(Collection<String> recipients, Collection<String> ccrecipients, String mailMessage, String subject)", ex);
        }
    }
    

    /**
     * Given a single email recipient, this method will send an email containing
     * the given body message and subject line to that recipient.
     * @param recipient a standard email address representing the recipient.
     * @param mailMessage the body of the email.
     * @param subject the subject line of the email.

     */
    public static void email(String recipient, String mailMessage, String subject)  {
        ArrayList<String> recipients = new ArrayList<>(1);
        recipients.add(recipient);
        email(recipients, null, mailMessage, subject);
    }
    
    /**
     * Given a single email recipient and a single cc recipient, this method will send an email containing
     * the given body message and subject line to that recipient and the cc recipient.
     * @param recipient a standard email address representing the recipient.
     * @param ccrecipient a standard email address representing the recipient to cc.
     * @param mailMessage the body of the email.
     * @param subject the subject line of the email.

     */
    public static void email(String recipient, String ccrecipient, String mailMessage, String subject)  {
        ArrayList<String> recipients = new ArrayList<>(1);
        ArrayList<String> ccrecipients = new ArrayList<>(1);
        recipients.add(recipient);
        ccrecipients.add(ccrecipient);
        email(recipients, ccrecipients, mailMessage, subject);
    }

    /**
     * This method will retrieve all administrators from the database and send an
     * email to each administrator with the given message and subject line.
     * @param mailMessage the body of the message
     * @param subject the subject line for the email.
     */
    public static void emailAdmin(String mailMessage, String subject)  {
       // Place all the administrator emails into an Arraylsit and use our email utility
       ArrayList<String> administratorList = new ArrayList<>();
       
       /* The following shows sample code and will need to be midified for a particular application
       //Get a user manager to retrieve administratorList from the database.
       database.UserManager userManager = database.Database.getDatabaseManagement().getUserManager();

       //attempt to retrieve all users from the database.
        Collection<User> allUsers = null;
        if(userManager != null)
                allUsers = userManager.getAllUsers();
        
        

        if(allUsers != null) {
            for(User user:allUsers) {
                if(user.getUserRole() == UserRole.SystemAdmin)
                    if(!administratorList.contains(user.getEmailAddress())){
                        administratorList.add(user.getEmailAddress());
                    }
            }
        }
        */
        email(administratorList, mailMessage, subject);
    }
    
    public static void main(String[] args) {
       // PropertyManager.configure("P:/CSProjects/ISIS/web/WEB-INF/config/General.properties");
       PropertyManager.configure("c:/WebProjects/SampleWebApplication/web/WEB-INF/config/General.properties");
        email(   "cjones@bloomu.edu",
                "This is a message generated by Emailer.java\n",
                "Emailer.java - test functionality");
        Debug.println("No errors thrown");       
    /*
            emailAdmin("This is a message to all administrators from our email system to" +
                " check functionality.  Please do not reply to this address."
                , "Admin Email Message");
            */
    }

}
