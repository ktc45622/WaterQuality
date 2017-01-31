package weather.common.utilities;

import java.awt.Component;
import java.util.Vector;
import javax.swing.JOptionPane;
import weather.common.data.User;
import weather.common.dbms.DBMSUserManager;
/**
 * A class with static methods to email passwords to users.
 * @author Brian Bankes
 */
public class PasswordEmailer {
    /**
     * The method to email a password to a user.
     * @param newPassword The password to be mailed.
     * @param user The user to receive the email.
     * @param parent The parent component of all dialogs to be displayed.
     * @return True if the email was sent, False if not.
     */
    public static boolean sendPassword(String newPassword, User user,
            Component parent){
        String message = "This ia a message from the BU Weather Program to "
                + "notify you that your login infomation has been set as follows:\n\n"
                + "ID: " + user.getLoginId() + "\nPassword: " + newPassword;
        if(user.getNumberOfLogins() > 0){
            message += "\n\nPlease follow the instructions below to change " 
                + "your password once you login to the weather viewer:"
                + "\nUnder the File menu, choose Settings and then Change Password";
        } else {
            message += "\n\nYou will be prompted to change your password when you first login.";
        }

        // Attempts to send email; exits method if unsuccessful.
        try {
            Emailer.email(user.getEmailAddress(), message, "Bloomsburg Weather "
                    + "Project Login Infomation");
            return true;
        } catch (WeatherException ex) {
            JOptionPane.showMessageDialog(parent, 
                    "The email message could not be sent.");
            return false;
        }
    }
    
    /**
     * Sends new passwords to a vector of users after verifying that it's alright
     * to send new passwords.  Also updates the program's user manager.
     * @param userList The list of users to receive new passwords.
     * @param userMgr The program's user manager.
     * @param parent The parent component of all dialogs to be displayed.
     */
    public static void sendNewPasswords(Vector<User> userList, 
            DBMSUserManager userMgr, Component parent){
        int sentPasswordCount = 0;
        String message = "<html>Clicking 'yes' will send an email with a new (<i>"
                +"reset</i>)<br/>password to each of the selected users.";
        int ans = JOptionPane.showConfirmDialog(parent, message, 
                "Send New Passwords?", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(ans == JOptionPane.YES_OPTION) {
            for (User user : userList){
                //The new password is a random string with 8 characters. The user can login with this new password
                String newPassword = Integer.toHexString(new java.util.Random().nextInt());

                // Attempts to send email; exits method if unsuccessful.
                if (PasswordEmailer.sendPassword(newPassword, user, parent)) {
                    sentPasswordCount++;
                    userMgr.updatePassword(user.getLoginId(), newPassword);
                }
            }
            //Show end result.
            JOptionPane.showMessageDialog(parent,
                    "Total number of reset and emailed passwords: "
                    + sentPasswordCount, "Changed Passwords",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
