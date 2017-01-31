package weather.clientside.gui.administrator;

import java.awt.Dimension;
import javax.swing.JOptionPane;
import weather.AdministratorControlSystem;
import weather.common.data.*;
import weather.common.dbms.DBMSUserManager;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.PasswordEmailer;

/**
 * The
 * <code>AddEditUserWindow</code> class allows for users on the database to have
 * their information created and modified.
 *
 * @author Bloomsburg University Software Engineering
 * @author Paul Zipko(2007)
 * @author Matt Rhoades(2007)
 * @author Mike Graboske
 * @author Ora Merkel (2009)
 * @author Joe Van Lente (2010)
 * @author Mike Nacko (2011)
 * @author Lucas Stine(2012)
 *
 * @version Spring 2012
 */
public class AddEditUserWindow extends BUDialog {
    //Stays null if we are adding a new user.
    private User existingUser = null;
    private DBMSUserManager manager;
    
    //For adding a new user to a course, 
    //Stays null if we are adding to a course. 
    private Course course = null;
    
    //To check for changes
    private String originalUserName;
    private String originalFirstName;
    private String originalLastName;
    private String originalEmail;
    private String originalPassword;
    private int originalUserTypeIndex;
    
    /**
     * Creates new form AddEditUserWindow to add a new user.
     * @param adminService The GeneralService object used to communicate with
     * other parts of the program.
     * @param isAdmin True if the user is an administrator, false otherwise.
     */
    public AddEditUserWindow(AdministratorControlSystem adminService, boolean isAdmin) {
        super(adminService);
        manager = adminService.getGeneralService().getDBMSSystem().
                getUserManager();
        initComponents();
        fillUserTypeComboBox();
        this.setTitle("Weather Viewer - Add a New User");
        updateButton.setText("Add User");
        
        //Make sure an instructor can't change user type.
        userTypeComboBox.setEnabled(isAdmin);
        
        //Store default values for testing when closing.
        originalUserName = "";
        originalFirstName = "";
        originalLastName = "";
        originalEmail = "";
        originalPassword = "";
        originalUserTypeIndex = userTypeComboBox.getSelectedIndex();
        
        getRootPane().setDefaultButton(updateButton);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                checkClose();
            }
        });
        
        int width = 443 + this.getInsets().left + this.getInsets().right;
        int height = 316 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }
    
    /**
     * Creates new form AddEditUserWindow to add a new student enrolled in a given
     * course.
     * @param adminService The GeneralService object used to communicate with
     * other parts of the program.
     * @param course The <code>Course</code> in which the student is enrolled.
     */
    public AddEditUserWindow(AdministratorControlSystem adminService, Course course) {
        super(adminService);
        manager = adminService.getGeneralService().getDBMSSystem().
                getUserManager();
        this.course = course;
        initComponents();
        fillUserTypeComboBox();
        this.setTitle("Weather Viewer - Add a New User");
        updateButton.setText("Add User");

        //Make sure the new user is a student.
        userTypeComboBox.setEnabled(false);

        //Store default values for testing when closing.
        originalUserName = "";
        originalFirstName = "";
        originalLastName = "";
        originalEmail = "";
        originalPassword = "";
        originalUserTypeIndex = userTypeComboBox.getSelectedIndex();

        getRootPane().setDefaultButton(updateButton);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                checkClose();
            }
        });
        
        
        int width = 443 + this.getInsets().left + this.getInsets().right;
        int height = 316 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }

    /**
     * Creates new form AddEditUserWindow to edit an existing user.
     * @param adminService The GeneralService object used to communicate with
     * other parts of the program.
     * @param isAdmin True if the user is an administrator, false otherwise.
     * @param existingUser The user to be edited.
     */
    public AddEditUserWindow(AdministratorControlSystem adminService, boolean isAdmin,
            User existingUser) {
        super(adminService);
        manager = adminService.getGeneralService().getDBMSSystem().
                getUserManager();
        this.existingUser = existingUser;
        initComponents();
        fillUserTypeComboBox();
        
        //Set up text inputs
        userNameText.setText(existingUser.getLoginId());
        firstNameText.setText(existingUser.getFirstName());
        lastNameText.setText(existingUser.getLastName());
        emailAddressText.setText(existingUser.getEmailAddress());
        passwordField.setText(existingUser.getPassword());
        confirmPasswordField.setText(existingUser.getPassword());
        userTypeComboBox.setSelectedItem(existingUser.getUserType().toString());
        
        //Make user name unchangeable.
        userNameText.setEnabled(false);
        
        //Hide option to send password (will check after save).
        emailCheckBox.setVisible(false);
        
        //Hide password shortcut instructions.
        passwordShortcutLabel.setVisible(false);
        
        //Make sure an instructor can't change user type.
        userTypeComboBox.setEnabled(isAdmin);
        
        //Store default values for testing when closing.
        originalUserName = userNameText.getText();
        originalFirstName = firstNameText.getText();
        originalLastName = lastNameText.getText();
        originalEmail = emailAddressText.getText();
        originalPassword = new String(passwordField.getPassword());
        originalUserTypeIndex = userTypeComboBox.getSelectedIndex();
        
        getRootPane().setDefaultButton(updateButton);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                checkClose();
            }
        });
        
        int width = 443 + this.getInsets().left + this.getInsets().right;
        int height = 316 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }
    
    private boolean changed() {
        if (!userNameText.getText().trim().equals(originalUserName)) {
            return true;
        }
        if (!firstNameText.getText().trim().equals(originalFirstName)) {
            return true;
        }
        if (!lastNameText.getText().trim().equals(originalLastName)) {
            return true;
        }
        if (!emailAddressText.getText().trim().equals(originalEmail)) {
            return true;
        }
        if (!new String(passwordField.getPassword()).equals(originalPassword)) {
            return true;
        }
        if (!new String(confirmPasswordField.getPassword()).equals(originalPassword)) {
            return true;
        }
        if (originalUserTypeIndex != userTypeComboBox.getSelectedIndex()) {
            return true;
        }
        return false;
    }
    
    private void checkClose(){
        if(!changed()){
            dispose();
        } else if (adminService.getGeneralService().leaveWithoutSaving(this) 
                == true) {
            dispose();
        }
    }

    /**
     * Fills the UserType comboBox with the appropriate values it needs and set
     * it to student by default.
     */
    private void fillUserTypeComboBox() {
        //initial index of 0 only needed for compiler.
        int studentIndex = 0;
        for (UserType u : UserType.values()) {
            userTypeComboBox.addItem(u.name());
            if (u == UserType.student) {
                studentIndex = u.ordinal();
            }
        }
        userTypeComboBox.setSelectedIndex(studentIndex);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        userNameText = new javax.swing.JTextField();
        firstNameText = new javax.swing.JTextField();
        lastNameText = new javax.swing.JTextField();
        emailAddressText = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        confirmPasswordField = new javax.swing.JPasswordField();
        userTypeComboBox = new javax.swing.JComboBox<String>();
        updateButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        confirmPasswordLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        emailAddressLabel = new javax.swing.JLabel();
        lastNameLabel = new javax.swing.JLabel();
        firstNameLabel = new javax.swing.JLabel();
        userTypeLabel = new javax.swing.JLabel();
        textToButtonSeperator = new javax.swing.JSeparator();
        emailCheckBox = new javax.swing.JCheckBox();
        userNameLabel = new javax.swing.JLabel();
        passwordShortcutLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Weather Viewer - Edit User");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainPanel.setPreferredSize(new java.awt.Dimension(282, 260));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        userNameText.setToolTipText("The username for the new user");
        mainPanel.add(userNameText, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 13, 305, 24));

        firstNameText.setToolTipText("The user's first name");
        mainPanel.add(firstNameText, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 76, 305, 25));

        lastNameText.setToolTipText("The user's last name");
        mainPanel.add(lastNameText, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 108, 305, 25));

        emailAddressText.setToolTipText("The email address of the user");
        mainPanel.add(emailAddressText, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 140, 305, 25));

        passwordField.setToolTipText("The password for the user");
        mainPanel.add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 172, 305, 25));

        confirmPasswordField.setToolTipText("The password for the user again, just to verify you typed it correctly");
        mainPanel.add(confirmPasswordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 204, 305, 25));

        userTypeComboBox.setToolTipText("The user's account type");
        mainPanel.add(userTypeComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 44, 305, 25));

        updateButton.setText("Update User");
        updateButton.setToolTipText("Updated selected user account");
        updateButton.setFocusPainted(false);
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtononEdit(evt);
            }
        });
        mainPanel.add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 278, 102, 25));

        closeButton.setText("Close");
        closeButton.setToolTipText("Exit this window");
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtononCancel(evt);
            }
        });
        mainPanel.add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(368, 278, 63, 25));

        confirmPasswordLabel.setText("Confirm Password:");
        confirmPasswordLabel.setToolTipText("The password for the user again, just to verify you typed it correctly");
        mainPanel.add(confirmPasswordLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 208, -1, -1));

        passwordLabel.setText("Password:");
        passwordLabel.setToolTipText("The password for the user");
        mainPanel.add(passwordLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(61, 176, -1, -1));

        emailAddressLabel.setText("Email Address:");
        emailAddressLabel.setToolTipText("The email address of the user");
        mainPanel.add(emailAddressLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 144, -1, -1));

        lastNameLabel.setText("Last Name:");
        lastNameLabel.setToolTipText("The user's last name");
        mainPanel.add(lastNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(56, 112, -1, -1));

        firstNameLabel.setText("First Name:");
        firstNameLabel.setToolTipText("The user's first name");
        mainPanel.add(firstNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 80, -1, -1));

        userTypeLabel.setText("User Type:");
        userTypeLabel.setToolTipText("The user's account type");
        mainPanel.add(userTypeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 48, -1, -1));
        mainPanel.add(textToButtonSeperator, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 236, 419, 9));

        emailCheckBox.setText("Send Password Email");
        mainPanel.add(emailCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 240, -1, -1));

        userNameLabel.setText("User Name:");
        userNameLabel.setToolTipText("The username for the new user");
        mainPanel.add(userNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(53, 17, -1, -1));

        passwordShortcutLabel.setText("<html>To make the password the same as the user<br/>name, leave both password fields blank.</html>");
        mainPanel.add(passwordShortcutLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 257, 32));

        getContentPane().add(mainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 443, 316));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This method is called when the cancel button is clicked. The user is
     * returned to the user list window.
     * @param evt The event that the cancel button is clicked.
     */
    private void closeButtononCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtononCancel
        checkClose();
}//GEN-LAST:event_closeButtononCancel
    
    /**
     * This method is called when the update button is clicked. The user is
     * prompted before editing an administrator.
     *
     * @param evt the event that the updateButton is pressed.
     */
    private void updateButtononEdit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtononEdit
        User thisUser = this.getNewUser();
       
       //If condition checks for ptoper input.
        if (thisUser != null) {
            //if condition check for edit mode
            if(existingUser != null){
                //Update existing user
                manager.updateUser(thisUser, thisUser.getUserNumber());
                JOptionPane.showMessageDialog(this,
                        "User has been updated successfully.",
                        "Weather Viewer", JOptionPane.INFORMATION_MESSAGE);
                //Check if password has been changed.
                if (!new String(passwordField.getPassword()).equals(originalPassword)) {
                    //Ask if we want to notify the user.
                    int ans = JOptionPane.showConfirmDialog(this, "You have "
                            + "changed this user's password.\nWould you like to "
                            + "send a notification email to this user?", 
                            "Password Change", JOptionPane.YES_NO_OPTION);
                    if (ans == JOptionPane.YES_OPTION) {
                        // Attempts to send email.
                        if (PasswordEmailer.sendPassword(
                                new String(passwordField.getPassword()).trim(), 
                                thisUser, this)) {
                            JOptionPane.showMessageDialog(this, "The password has been sent.");
                        }
                    }
                }
            }else{
                //Add new user
                manager.addUser(thisUser);
                //If we have a course, enroll this user, which must be a student, in it.
                if (course != null) {
                    adminService.getGeneralService().getDBMSSystem().getEnrollmentManager()
                            .insertStudentIntoCourse(thisUser, course);
                }
                JOptionPane.showMessageDialog(this,
                        "User has been added successfully.",
                        "Weather Viewer", JOptionPane.INFORMATION_MESSAGE);
                //Send Email if requested by checkbox
                if(emailCheckBox.isSelected()){
                    // Attempts to send email.
                    if (PasswordEmailer.sendPassword(
                            new String(passwordField.getPassword()).trim(), 
                            thisUser, this)) {
                        JOptionPane.showMessageDialog(this, "The password has been sent.");
                    }
                }
            }
            dispose();
        }
    }//GEN-LAST:event_updateButtononEdit

    /**
     * Creates a new user with the information provided in the window  Validates
     * the fields and checks for unwanted administrative-level changes and email
     * reuse as well.  The new user is also the existing user in edit mode
     * provided the input is valid.  When adding a new user, this method allows
     * the user name to be substituted for a blank password.
     *
     * @return The new user or null if errors are present.
     */
    private User getNewUser() {
        User newUser;
        
        /*Check for errors that will stop save.*/
        String errors = "";
        
        //Get data to build course.
        String firstName = this.firstNameText.getText().trim();
        String lastName = this.lastNameText.getText().trim();
        String email = this.emailAddressText.getText().trim();
        String userName = this.userNameText.getText().trim();
        String pass1 = new String(passwordField.getPassword()).trim();
        String pass2 = new String(confirmPasswordField.getPassword()).trim();
        UserType userType = UserType.valueOf(userTypeComboBox.getSelectedItem().toString());
        
        //Verify input
        if (userName.isEmpty()) {
            errors += "The user name must be entered.\n";
        }
        if (userName.length() > 100) {
            errors += "The user name must be no more than 100 characters.\n";
            
        }
        if(existingUser == null && !manager.obtainUsersByUsername(userName).isEmpty()) {
            errors += "The user name must be changed to one that is not in use.\n";
        }
        
        //Check for empty password fields whan adding a new user.
        if (pass1.isEmpty() && pass2.isEmpty() && errors.equals("")
                && existingUser == null) {
            passwordField.setText(userName);
            confirmPasswordField.setText(userName);
            pass1 = userName;
            pass2 = userName;
        }
        
        if (firstName.isEmpty()) {
            errors += "The first name must be entered.\n";
        }
        if (firstName.length() > 30) {
            errors += "The first name must be no more than 30 characters.\n";
        }
  
        if (lastName.isEmpty()) {
            errors += "The last name must be entered.\n";
        }
        if (lastName.length() > 50) {
            errors += "The last name must be no more than 50 characters.\n";
        }

        if (email.isEmpty()) {
            errors += "The email address must be entered.\n";
        }
        if (!email.isEmpty()){
            //Check for a valid email addres
            if(!weather.common.utilities.DataFilter.isEmail(email)) {
                errors += "The email address is not valid.\n";
            } else if (!email.equals(originalEmail)) {
                //Chack for email that is already used.
                if(!manager.obtainUsersByEmail(email).isEmpty()){
                    errors += "The email address must be changed to one that is not in use.\n";
                }
            }
        }

        if (pass1.isEmpty()) {
            errors += "The password must be entered.\n";
        }
        if (pass1.length() > 100) {
            errors += "The password must be no more than 100 characters.\n";
        }
        //Check for passord mismatch
        if (!pass1.isEmpty() && !pass1.equals(pass2)) {
            errors += "The password must match what is typed in the confirm password box.\n";
        } 

        //Either show error or move to questions.
        if (!errors.equals("")) {
            errors = "The following errors must be corrected:\n" + errors.trim();
            JOptionPane.showMessageDialog(this, errors,
                    "Unable To Save User", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        /*Now, ask questions that could stop saxe.*/
        
        //Check for umwanted administrative-level changes.
        if (existingUser != null) {
            //Checks to see if user being changed is admin
            if (existingUser.getUserType().equals(UserType.administrator)
                    && userType.equals(UserType.administrator)) {
                if (changed()) {
                    int ans = JOptionPane.showConfirmDialog(this, "Warning! You are "
                            + "editing an admin level user. Continue?", "Warning",
                            JOptionPane.YES_NO_OPTION);
                    if (ans == JOptionPane.NO_OPTION) {
                        showCancelledSave();
                        return null;
                    }
                }
            } //If the user is being updated to an administrator
            else if (userType.equals(UserType.administrator)) {
                int ans = JOptionPane.showConfirmDialog(this, "Warning! You are "
                        + "updating to an admin level user. Continue?", "Warning",
                        JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.NO_OPTION) {
                    showCancelledSave();
                    return null;
                }
            } //If removing admin privileges
            else if (existingUser.getUserType().equals(UserType.administrator)) {
                int ans = JOptionPane.showConfirmDialog(this, "Warning! You are "
                        + "removing admin level privileges. Continue?", "Warning",
                        JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.NO_OPTION) {
                    showCancelledSave();
                    return null;
                }
            }      
        }
        
        /*Now, perform save.*/
        
        //if condition check for edit mode
        if (existingUser != null) {
            //Update existing user
            newUser = existingUser;
        } else {
            newUser = new User();
        }
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmailAddress(email);
        newUser.setLoginId(userName);
        
        //Make sure we aren't encrypting the already encrypted password
        if (existingUser == null || !pass1.equals(manager.obtainUser(
                newUser.getLoginId()).getPassword())) {
            newUser.setEncryptedPassword(pass1);
        } else {
            //The password has not been changed. so set the newUser password to 
            //the one already stored in the database.
            newUser.setPassword(pass1);
        }
        newUser.setUserType(userType);
        return newUser;
    }
    
    private void showCancelledSave(){
        String message;
        if(existingUser == null){
            message = "The user was not added.";
        } else {
            message = "The changes were not saved.";
        }
        JOptionPane.showMessageDialog(this, message,
                "Weather Viewer", JOptionPane.INFORMATION_MESSAGE);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JPasswordField confirmPasswordField;
    private javax.swing.JLabel confirmPasswordLabel;
    private javax.swing.JLabel emailAddressLabel;
    private javax.swing.JTextField emailAddressText;
    private javax.swing.JCheckBox emailCheckBox;
    private javax.swing.JLabel firstNameLabel;
    private javax.swing.JTextField firstNameText;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JTextField lastNameText;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel passwordShortcutLabel;
    private javax.swing.JSeparator textToButtonSeperator;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField userNameText;
    private javax.swing.JComboBox<String> userTypeComboBox;
    private javax.swing.JLabel userTypeLabel;
    // End of variables declaration//GEN-END:variables
}
