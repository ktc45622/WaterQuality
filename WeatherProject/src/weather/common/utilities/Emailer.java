package weather.common.utilities;

import com.sun.mail.smtp.SMTPTransport;
import java.util.Properties;
import java.util.Vector;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.dbms.mysql.MySQLImpl;

/**
 *
 * A simple class used to send email. Loads the host name, port, username, and 
 * password from the properties file.
 *
 * Basic usage - to send a simple email:
 *
 *  <CODE>Emailer.email(user@host.com, message, subject);</CODE>
 *
 * Also can be used to send the same email to multiple participants, using the
 * same method, but supplying a vector of recipients:
 * 
 *  <CODE>Emailer.email(Vector<String> users, message, subject);</CODE>
 *
 * @author Bloomsburg University Software Engineering
 * @author Joe Sharp (2008)
 * @author Dave Moser (2008)
 * @version Spring 2009
 */
public class Emailer {
    private static final String SMTP_HOST_NAME = PropertyManager.getGeneralProperty("SMTP_HOST_NAME");
    private static final String SMTP_HOST_PORT = PropertyManager.getGeneralProperty("SMTP_HOST_PORT");
    private static final String SMTP_AUTH_USER = PropertyManager.getGeneralProperty("SMTP_AUTH_USER");
    private static final String SMTP_AUTH_PWD  = PropertyManager.getGeneralProperty("SMTP_AUTH_PWD");
    

    /**
     * A simple method used to send an automated email to the given recipients 
     * from the default web server, using the default account and password found
     * in the general properties file.
     *
     * TODO: Determine appropriate from email address for this function.
     * @param recipients a vector containing 1 or more email addresses to which
     * this email will be sent.
     * @param mailMessage the message to be contained in the body of the email
     * @param subject the subject line of the email
     * @throws weather.common.utilities.WeatherException
     */
    public static void email(Vector<String> recipients, String mailMessage, String subject) throws WeatherException {
        try {

            Properties props = new Properties();
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
            
            props.put("mail.smtp.starttls.enable", "true");
            //Create a new mail session with the set properties
            Session mailSession = Session.getDefaultInstance(props);
            


            mailSession.setDebug(true);

            //Create new SMTPTransport for sending the message.
            SMTPTransport transport = (SMTPTransport)mailSession.getTransport("smtp");

            //Build the message.
            MimeMessage message = new MimeMessage(mailSession);
            //Set the email message to originate from this default address
            if(SMTP_AUTH_USER == null || !SMTP_AUTH_USER.isEmpty())
                    message.setFrom(new InternetAddress("WeatherProject@bloomu.edu"));
            else message.setFrom(new InternetAddress(SMTP_AUTH_USER));
            message.setSubject(subject);
            message.setText(mailMessage);
            Debug.println("SMTP_AUTH_USER ="+SMTP_AUTH_USER+"\n SMTP_AUTH_PWD="+SMTP_AUTH_PWD+
                    "\nSMTP_HOST_NAME ="+SMTP_HOST_NAME+"\nPort="+SMTP_HOST_PORT+
                    "\nSent from ="+new InternetAddress(SMTP_AUTH_USER).toString());
            //Set up the mailer
           // message.setHeader("X-Mailer","smtpsend");
            //Add all given recipients.
            for(String toAddress:recipients) {
                Debug.println("Adding #"+toAddress+"#");
                 message.addRecipients(Message.RecipientType.TO,
				InternetAddress.parse(toAddress));
            }

            //If a user name and password were specified in the propertied file,
            //use them to connect to the mail server.
            if(auth)
                transport.connect(SMTP_HOST_NAME, SMTP_AUTH_USER, SMTP_AUTH_PWD);
            else//Otherwise just connect without authentication.
                transport.connect();

            //Send the message.
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));

            transport.close();

        } catch (MessagingException ex) {
            throw new WeatherException(6,ex);
        }
    }
    
    public static void main(String[] args) throws WeatherException{

        email(   "cjones@bloomu.edu",
                "This is a message generated by Emailer.java",
                "Emailer.java - test functionality");
        Debug.println("No errors thrown");       
        emailAdmin("This is a message to all administrators from our email system to" +
                " check functionality.  Please do not reply to this address."
                , "Admin Email Message");
    }

    /**
     * Given a single email recipient, this method will send an email containing
     * the given body message and subject line to that recipient.
     * @param recipient a standard email address representing the recipient.
     * @param mailMessage the body of the email.
     * @param subject the subject line of the email.
     * @throws weather.common.utilities.WeatherException
     */
    public static void email(String recipient, String mailMessage, String subject) throws WeatherException {
        Vector<String> recipients = new Vector<String>();
        recipients.add(recipient);
        email(recipients, mailMessage, subject);
    }

    /**
     * This method will retrieve all administrators from the database, as well
     * as the administrator defined in the general properties file and send an
     * email to each administrator with the given message and subject line.
     * @param mailMessage the body of the message
     * @param subject the subject line for the email.
     * @throws weather.common.utilities.WeatherException
     */
    public static void emailAdmin(String mailMessage, String subject) throws WeatherException {
        //Get a user manager to retrieve admins from the database.
        DBMSUserManager userManager = null;
        try{
            DBMSSystemManager dbms = MySQLImpl.getMySQLDMBSSystem();
            userManager = dbms.getUserManager();
        }catch(Exception ex) {
            //Do nothing, we just won't be able to get any admins from the
            //database
        }

        //Retrieve the default admin from the general properties file.
        String default_admin = PropertyManager.getGeneralProperty("ADMIN_EMAIL");
        
        //attempt to retrieve all users from the database.
        Vector<User> allUsers = null;
        if(userManager != null)
                allUsers = userManager.obtainAllUsers();
        
        Vector<String> admins = new Vector<String>();

        if(!admins.contains(default_admin))
            admins.add(default_admin);
         //Debug.println("Length of vector is "+ admins.size());
        email(admins, mailMessage, subject);
       // Debug.println("First email to admins sent");
        /*
         * If we got any users from the database, cycle through them and
         * add any administrators to the recipient list
         */
        if(allUsers != null) {
            for(User user:allUsers) {
                if(user.getUserType() == UserType.administrator)
                    if(!admins.contains(user.getEmailAddress())){
                        admins.add(user.getEmailAddress());
                    }
            }
        }
        
        //If the default admin is not already in the recipient list, add them
        if(!admins.contains(default_admin)){admins.add(default_admin);}
       // Debug.println("Length of vector for second try  is "+ admins.size());
        email(admins, mailMessage, subject);
    }
}
