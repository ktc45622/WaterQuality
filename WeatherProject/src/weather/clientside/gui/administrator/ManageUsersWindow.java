package weather.clientside.gui.administrator;

import java.awt.event.*;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableRowSorter;
import weather.AdministratorControlSystem;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.PasswordEmailer;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>ManageUsersWindow</code> class creates a form that will only 
 * appear when the user hits the search button from the edit user screen.
 * There will be a table that will be produced holding the values for 
 * whatever user type the user has selected.  From this window the user 
 * can select a user to edit and will be taken to the appropriate screen.
 * @author Bloomsburg University Software Engineering
 * @author Ora Merkel (2009)
 * @author Joe Van Lente (2010)
 * @author Andrew Bennett (2010)
 * @author Mike Nacko (2011)
 * @author Eric Lowrie (2012)
 * @author Ty Vanderstappen (2012)
 * 
 * @version Spring 2012
 */
public class ManageUsersWindow extends BUDialog {
    private Vector<User> userList;
    private boolean isAdmin;
    private static int TABLE_LENGTH = 6;
    private DBMSSystemManager dbms;
    private MyDefaultTableModel studentModel, instructorModel,adminModel,guestModel;
    private TableRowSorter<MyDefaultTableModel> studentSorter, instructorSorter,adminSorter,guestSorter;
    
    final Vector<User> students = new Vector<>();
    final Vector<User> instructors = new Vector<>();
    final Vector<User> admins = new Vector<>();
    final Vector<User> guests = new Vector<>();
    private Vector<Course> courseList;
    private DBMSCourseManager courseMgr;

    // Note: AdministratorControlSystem admin must be final, its needed in subclasses.
    //The same is true for boolean isAdmin.
    /**
     * Constructor
     * @param adminService The program's instance of 
     * <code>AdministratorControlSystem</code>
     * @param isAdmin True if listing should have administrative ability to see
     * and edit more than students and guests, false otherwise.
     */
    public ManageUsersWindow(final AdministratorControlSystem adminService,
            final boolean isAdmin) {
        super(adminService);
        this.isAdmin = isAdmin;
        courseMgr = this.adminService.getGeneralService().getDBMSSystem().getCourseManager();
        dbms = this.adminService.getGeneralService().getDBMSSystem();
        initComponents();
        
        updateUserList();
        
        //Restrict rights of instructors
        mainDisplayjTabbedPane.setEnabledAt(1, isAdmin);
        mainDisplayjTabbedPane.setEnabledAt(2, isAdmin);
        
        getRootPane().setDefaultButton(closeButton);
        
        /*
         *  This is the mouse click listener.  This class listens for several different
         *  types of mouse clicks.  If the user double clicks on on a cell with user information
         *  in it the listener will bring up the edit user window for that user.  IF the user clicks
         *  in the first column the table will update a checkbox that is used to mark users that
         *  are to be deleted from the list.  The table was made in a strange way due to the fact
         *  that th table would not show its headers in a tabbed canvas, to solve this I just added
         *  an extra row and added the titles in manually.  This allowed me to put a listener in the
         *  first cell in the first column that when clicked on will select/deselect all checkboxes
         *  for each row.
         */
        MouseAdapter doubleClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JTable target = (JTable) evt.getSource();

                if (evt.getClickCount() == 2) {
                    Vector<User> current;
                    // Figures out what table the action will be performed on based off of table index.
                    if (mainDisplayjTabbedPane.getSelectedIndex() == 0) {
                        current = students;
                    } else if (mainDisplayjTabbedPane.getSelectedIndex() == 1) {
                        current = instructors;
                    } else if (mainDisplayjTabbedPane.getSelectedIndex() == 2) {
                        current = admins;
                    } else {
                        current = guests;
                    }
                    User value = (User) current.get(target.getSelectedRow());
                    User user = dbms.getUserManager().obtainUser(value.getLoginId());
                    if (user != null) {
                        new AddEditUserWindow(adminService, isAdmin, user);
                        updateUserList();
                    }
                }
            }
        };

        studentTable.addMouseListener(doubleClick);
        instructorTable.addMouseListener(doubleClick);
        adminTable.addMouseListener(doubleClick);
        guestTable.addMouseListener(doubleClick);
        
        closeButton.setFocusable(false);
        errorLabel.setVisible(false);
        
        pack();
        super.postInitialize(true);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        deleteSelectedUsersButton = new javax.swing.JButton();
        searchByUserButton = new javax.swing.JButton();
        addUserButton = new javax.swing.JButton();
        editLabel = new javax.swing.JLabel();
        mainDisplayjTabbedPane = new javax.swing.JTabbedPane();
        studentScrollPane = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable();
        instructorScrollPane = new javax.swing.JScrollPane();
        instructorTable = new javax.swing.JTable();
        adminScrollPane = new javax.swing.JScrollPane();
        adminTable = new javax.swing.JTable();
        guestScrollPane = new javax.swing.JScrollPane();
        guestTable = new javax.swing.JTable();
        errorLabel = new javax.swing.JLabel();
        PasswordButton = new javax.swing.JButton();

        setTitle("Weather Viewer - User List Window");
        setMinimumSize(new java.awt.Dimension(900, 300));
        setResizable(false);

        closeButton.setText("Close");
        closeButton.setToolTipText("Close this window");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        deleteSelectedUsersButton.setText("Delete Selected Users");
        deleteSelectedUsersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectedUsersButtonActionPerformed(evt);
            }
        });

        searchByUserButton.setText("Search By User");
        searchByUserButton.setToolTipText("Search for a specific user based on specific parameters.");
        searchByUserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchForUserMenuItemActionPerformed(evt);
            }
        });

        addUserButton.setText("Add User");
        addUserButton.setToolTipText("Adds a new user to the program.");
        addUserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserButtonActionPerformed(evt);
            }
        });

        editLabel.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        editLabel.setText("To edit a specific user, double-click on their name.");
        editLabel.setToolTipText("");

        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "User Name", "Last Name", "First Name", "Email Address", "Last Login", "Login Count"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        studentScrollPane.setViewportView(studentTable);

        mainDisplayjTabbedPane.addTab("Students", studentScrollPane);

        instructorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "User Name", "Last Name", "First Name", "Email Address", "Last Login", "Login Count"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        instructorScrollPane.setViewportView(instructorTable);

        mainDisplayjTabbedPane.addTab("Instructor", instructorScrollPane);

        adminTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "User Name", "Last Name", "First Name", "Email Address", "Last Login", "Login Count"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        adminScrollPane.setViewportView(adminTable);

        mainDisplayjTabbedPane.addTab("Administrators", adminScrollPane);

        guestTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "User Name", "Last Name", "First Name", "Email Address", "Last Login", "Login Count"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        guestTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        guestScrollPane.setViewportView(guestTable);

        mainDisplayjTabbedPane.addTab("Guests", guestScrollPane);

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText("At least one row must be selected.");

        PasswordButton.setText("Send Passwords To Selected Users");
        PasswordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PasswordButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mainDisplayjTabbedPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, buttonPanelLayout.createSequentialGroup()
                        .addComponent(editLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(errorLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, buttonPanelLayout.createSequentialGroup()
                        .addComponent(addUserButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchByUserButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteSelectedUsersButton)
                        .addGap(12, 12, 12)
                        .addComponent(PasswordButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
                        .addComponent(closeButton)))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainDisplayjTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchByUserButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteSelectedUsersButton)
                            .addComponent(PasswordButton))
                        .addGap(20, 20, 20))
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(addUserButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(editLabel)
                            .addComponent(errorLabel))
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This is called when the close button is clicked.
     * Disposes the Manage Users window.
     * @param evt The event that called this method.
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
}//GEN-LAST:event_closeButtonActionPerformed
    
    /**
     * Deletes multiple users from the database.
     */
    private void deleteUsers() {
        JTable target = studentTable;

        // Figures out what table the action will be performed on based off of table index.
        switch (mainDisplayjTabbedPane.getSelectedIndex()) {
            case 0:
                target = studentTable;
                break;
            case 1:
                target = instructorTable;
                break;
            case 2:
                target = adminTable;
                break;
            case 3:
                target = guestTable;
                break;
            default:
                break;
        }

        String userName;
        String deleted = "";

        // Checks if the user actually selected anything.
        if (target.getSelectedRowCount() == 0) {
            errorLabel.setVisible(true);
            return;
        }
        errorLabel.setVisible(false);

        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to"
                + " delete the selected user(s)?", "Delete Selected User(s)",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ans == JOptionPane.YES_OPTION) {

            userLoop:
            for (int i = 0; i < target.getSelectedRowCount(); i++) {

                // Gets the next selected row number.
                int j = target.getSelectedRows()[i];

                // Substring is used to remove the spacing.
                userName = target.getValueAt(j, 0).toString().substring(1);
                DBMSUserManager userManager
                        = adminService.getGeneralService().getDBMSSystem()
                        .getUserManager();

                // Obtain user object for the given row.
                User user;
                user = userManager.obtainUser(userName);

                // Stop the user ftom deleting himself or herself.
                if (user.getUserNumber() == adminService.getGeneralService()
                        .getUser().getUserNumber()) {
                    JOptionPane.showMessageDialog(null, "You cannot delete"
                            + " yourself.", "Cannot Delete User",
                            JOptionPane.INFORMATION_MESSAGE);
                    continue;
                }

                // Make sure the users "admin" and "guest" are not deleted.
                if (user.getLoginId().equals("admin") || user.getLoginId()
                        .equals("guest")) {
                    JOptionPane.showMessageDialog(this, "User " + user
                            .getLoginId() + " cannot be deleted.",
                            "Cannot Delete User",
                            JOptionPane.INFORMATION_MESSAGE);
                    continue;
                }

                // Checks to see if the user to be deleted is instructing 
                // any courses and, if so, give the user a message.
                courseList = courseMgr.obtainAllCourses();
                for (int k = 0; k < courseList.size(); k++) {
                    if (courseList.get(k).getInstructor().getLoginId()
                            .equals(userName.trim())) {
                        JOptionPane.showMessageDialog(this,
                                courseList.get(k).getInstructor()
                                + " can not be deleted."
                                + "\nThe user is currently the "
                                + "instructor for the class  "
                                + courseList.get(k).getClassName()
                                + ".", "Cannot Delete User",
                                JOptionPane.INFORMATION_MESSAGE);
                        continue userLoop;
                    }
                }

                // Confirm if you are trying to remove an administrator.
                if (user.getUserType() == UserType.administrator) {
                    ans = JOptionPane.showConfirmDialog(this,
                            "You are going to delete the following "
                            + "administrator:\n" + user.getLastName() + ", "
                            + user.getFirstName() + "\nContinue?", "Deleting"
                            + " Administrator User", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (ans == JOptionPane.NO_OPTION) {
                        continue;
                    }
                }

                // Try to delete user.
                if (!userManager.removeUser(userName)) {
                    JOptionPane.showMessageDialog(this, "The user "
                            + user.getFirstName() + " " + user.
                            getLastName() + " could not be deleted.",
                            "Error Deleting User", JOptionPane.WARNING_MESSAGE);
                } else {
                    deleted = deleted.concat(user.getFirstName() + " "
                            + user.getLastName() + "\n");
                }
            }
            
            // Show results.
            updateUserList();
            if (!deleted.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "The following users have been deleted:\n" + deleted,
                    "Selected Users Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No users were deleted.", "No Selected Users Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Deletes all users who are displayed in the table whose
     * checkBox in the left hand column is checked.
     * @param evt The event that the delete selected users button is clicked.
     */
    private void deleteSelectedUsersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedUsersButtonActionPerformed
        deleteUsers();
        updateUserList();
}//GEN-LAST:event_deleteSelectedUsersButtonActionPerformed

    /**
     * 
     * @param evt 
     */
    private void searchForUserMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchForUserMenuItemActionPerformed
        try {
            adminService.searchForUser(isAdmin);
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Failed to open the edit user screen.", ex);
            ex.show();
        }
        updateUserList();
    }//GEN-LAST:event_searchForUserMenuItemActionPerformed

    /**
     * 
     * @param evt 
     */
    private void addUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserButtonActionPerformed
        new AddEditUserWindow(adminService, isAdmin);
        updateUserList();
    }//GEN-LAST:event_addUserButtonActionPerformed

    private void PasswordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PasswordButtonActionPerformed
        JTable target = studentTable;

        // Figures out what table the action will be performed on based off of table index.
        if (mainDisplayjTabbedPane.getSelectedIndex() == 0) {
            target = studentTable;
        } else if (mainDisplayjTabbedPane.getSelectedIndex() == 1) {
            target = instructorTable;
        } else if (mainDisplayjTabbedPane.getSelectedIndex() == 2) {
            target = adminTable;
        } else if (mainDisplayjTabbedPane.getSelectedIndex() == 3) {
            target = guestTable;
        }

        String userName;

        //checks if the user actually selected anything
        if (target.getSelectedRowCount() == 0) {
            errorLabel.setVisible(true);
            return;
        }
        errorLabel.setVisible(false);
        
        //Store selected users in a vector.
        Vector<User> userList = new Vector<>();
        
        //Store users in vector.
        DBMSUserManager userManager =
                adminService.getGeneralService().getDBMSSystem().getUserManager();
        for (int i = 0; i < target.getSelectedRowCount(); i++) {
            //gets the next selected row number
            int j = target.getSelectedRows()[i];

            //substring is used to remove the spacing
            userName = target.getValueAt(j, 0).toString().substring(1);

            User user;
            user = userManager.obtainUser(userName);
            userList.add(user);
        }
        
        //Send Emails
        PasswordEmailer.sendNewPasswords(userList, userManager, this);
    }//GEN-LAST:event_PasswordButtonActionPerformed

    /**
     * This method generates the user list that is used to populate the tables.
     */
    private void generateUserList() {
        userList = dbms.getUserManager().obtainAllUsers();

        students.clear();
        admins.clear();
        instructors.clear();
        guests.clear();

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserType().equals(UserType.student)) {
                students.add(userList.get(i));
            } else if (userList.get(i).getUserType().equals(UserType.administrator)) {
                admins.add(userList.get(i));
            } else if (userList.get(i).getUserType().equals(UserType.instructor)) {
                instructors.add(userList.get(i));
            } else {
                guests.add(userList.get(i));
            }
        }
    }

    /**
     * This method is used to create the basic structure of the table.  Since
     * the headers do not appear for an unknown reason inside a tabbed canvas
     * I was forced to add an extra row and insert them myself.
     */
    private void initializeTables() {
        
        //Creates the Table Model that will have the correct amount of rows
        //needed for each set of data.  Also creates the row sorter and headers
        //for each table.
        studentModel = new MyDefaultTableModel(0, 0);
        studentModel.setColumnCount(TABLE_LENGTH);
        studentModel.setRowCount(students.size());
        studentTable.setModel(studentModel);
        studentSorter = new TableRowSorter<>(studentModel);
        studentTable.setRowSorter(studentSorter);
        studentTable.getColumnModel().getColumn(0).setHeaderValue("User Name");
        studentTable.getColumnModel().getColumn(1).setHeaderValue("Last Name");
        studentTable.getColumnModel().getColumn(2).setHeaderValue("First Name");
        studentTable.getColumnModel().getColumn(3).setHeaderValue("Email Address");
        studentTable.getColumnModel().getColumn(4).setHeaderValue("Last Login");
        studentTable.getColumnModel().getColumn(5).setHeaderValue("Login Count");
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        studentTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        studentTable.getTableHeader().setReorderingAllowed(false);
        
        instructorModel = new MyDefaultTableModel(0, 0);
        instructorModel.setRowCount(instructors.size());
        instructorModel.setColumnCount(TABLE_LENGTH);
        instructorTable.setModel(instructorModel);
        instructorSorter = new TableRowSorter<>(instructorModel);
        instructorTable.setRowSorter(instructorSorter);
        instructorTable.getColumnModel().getColumn(0).setHeaderValue("User Name");
        instructorTable.getColumnModel().getColumn(1).setHeaderValue("Last Name");
        instructorTable.getColumnModel().getColumn(2).setHeaderValue("First Name");
        instructorTable.getColumnModel().getColumn(3).setHeaderValue("Email Address");
        instructorTable.getColumnModel().getColumn(4).setHeaderValue("Last Login");
        instructorTable.getColumnModel().getColumn(5).setHeaderValue("Login Count");
        instructorTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        instructorTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        instructorTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        instructorTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        instructorTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        instructorTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        instructorTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        instructorTable.getTableHeader().setReorderingAllowed(false);
        
        adminModel = new MyDefaultTableModel(0, 0);
        adminModel.setRowCount(admins.size());
        adminModel.setColumnCount(TABLE_LENGTH);
        adminTable.setModel(adminModel);
        adminSorter = new TableRowSorter<>(adminModel);
        adminTable.setRowSorter(adminSorter);        
        adminTable.getColumnModel().getColumn(0).setHeaderValue("User Name");
        adminTable.getColumnModel().getColumn(1).setHeaderValue("Last Name");
        adminTable.getColumnModel().getColumn(2).setHeaderValue("First Name");
        adminTable.getColumnModel().getColumn(3).setHeaderValue("Email Address");
        adminTable.getColumnModel().getColumn(4).setHeaderValue("Last Login");
        adminTable.getColumnModel().getColumn(5).setHeaderValue("Login Count");
        adminTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        adminTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        adminTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        adminTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        adminTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        adminTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        adminTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        adminTable.getTableHeader().setReorderingAllowed(false);
        
        guestModel = new MyDefaultTableModel(0, 0);
        guestModel.setRowCount(guests.size());
        guestModel.setColumnCount(TABLE_LENGTH);
        guestTable.setModel(guestModel);
        guestSorter = new TableRowSorter<>(guestModel);
        guestTable.setRowSorter(guestSorter);
        guestTable.getColumnModel().getColumn(0).setHeaderValue("User Name");
        guestTable.getColumnModel().getColumn(1).setHeaderValue("Last Name");
        guestTable.getColumnModel().getColumn(2).setHeaderValue("First Name");
        guestTable.getColumnModel().getColumn(3).setHeaderValue("Email Address");
        guestTable.getColumnModel().getColumn(4).setHeaderValue("Last Login");
        guestTable.getColumnModel().getColumn(5).setHeaderValue("Login Count");
        guestTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        guestTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        guestTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        guestTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        guestTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        guestTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        guestTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        guestTable.getTableHeader().setReorderingAllowed(false);
    }

    /**
     *  This method fills the tables with the user information.
     */
    private void fillTableData() {
        // fills values for the jtables
        for (int i = 0, j = 0; j < students.size(); i++, j++) {
            //sTable.setValueAt(false, i, 0);
            studentTable.setValueAt(" "+students.get(j).getLoginId(), i, 0);
            studentTable.setValueAt(" "+students.get(j).getLastName(), i, 1);
            studentTable.setValueAt(" "+students.get(j).getFirstName(), i, 2);
            studentTable.setValueAt(" "+students.get(j).getEmailAddress(), i, 3);
            studentTable.setValueAt(" "+students.get(j).getLastLogInDateInPrettyFormat(), i, 4);
            studentTable.setValueAt(" "+students.get(j).getNumberOfLogins(), i, 5);      
        }
        for (int i = 0, j = 0; j < instructors.size(); i++, j++) {
           // iTable.setValueAt(false, i, 0);

            instructorTable.setValueAt(" "+instructors.get(j).getLoginId(), i, 0);
            instructorTable.setValueAt(" "+instructors.get(j).getLastName(), i, 1);
            instructorTable.setValueAt(" "+instructors.get(j).getFirstName(), i, 2);
            instructorTable.setValueAt(" "+instructors.get(j).getEmailAddress(), i, 3);
            instructorTable.setValueAt(" "+instructors.get(j).getLastLogInDateInPrettyFormat(), i, 4);
            instructorTable.setValueAt(" "+instructors.get(j).getNumberOfLogins(), i, 5);
        }
        for (int i = 0, j = 0; j < admins.size(); i++, j++) {
           // aTable.setValueAt(false, i, 0);

            adminTable.setValueAt(" "+admins.get(j).getLoginId(), i, 0);
            adminTable.setValueAt(" "+admins.get(j).getLastName(), i, 1);
            adminTable.setValueAt(" "+admins.get(j).getFirstName(), i, 2);
            adminTable.setValueAt(" "+admins.get(j).getEmailAddress(), i, 3);
            adminTable.setValueAt(" "+admins.get(j).getLastLogInDateInPrettyFormat(), i,4);
            adminTable.setValueAt(" "+admins.get(j).getNumberOfLogins(), i, 5);
        }
        for (int i = 0, j = 0; j < guests.size(); i++, j++) {
           // oTable.setValueAt(false, i, 0);
            guestTable.setValueAt(" "+guests.get(j).getLoginId(), i, 0);
            guestTable.setValueAt(" "+guests.get(j).getLastName(), i, 1);
            guestTable.setValueAt(" "+guests.get(j).getFirstName(), i, 2);
            guestTable.setValueAt(" "+guests.get(j).getEmailAddress(), i, 3);
            guestTable.setValueAt(" "+guests.get(j).getLastLogInDateInPrettyFormat(), i, 4);
            guestTable.setValueAt(" "+guests.get(j).getNumberOfLogins(), i, 5);
        }
        studentTable.revalidate();
        instructorTable.revalidate();
        adminTable.revalidate();
        guestTable.revalidate();
    }

    /**
     *  This method updates the tables.
     */
    private void updateUserList() {
        generateUserList();
        initializeTables();
        fillTableData();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton PasswordButton;
    private javax.swing.JButton addUserButton;
    private javax.swing.JScrollPane adminScrollPane;
    private javax.swing.JTable adminTable;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteSelectedUsersButton;
    private javax.swing.JLabel editLabel;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JScrollPane guestScrollPane;
    private javax.swing.JTable guestTable;
    private javax.swing.JScrollPane instructorScrollPane;
    private javax.swing.JTable instructorTable;
    private javax.swing.JTabbedPane mainDisplayjTabbedPane;
    private javax.swing.JButton searchByUserButton;
    private javax.swing.JScrollPane studentScrollPane;
    private javax.swing.JTable studentTable;
    // End of variables declaration//GEN-END:variables
}
