package weather.clientside.gui.administrator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.*;
import weather.ApplicationControlSystem;
import weather.GeneralService;
import weather.clientside.gui.client.ForecasterChooseLesson;
import weather.clientside.gui.client.MainApplicationWindow;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.clientside.utilities.TimedLoader;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.dbms.DBMSSystemManager;
import weather.common.gui.component.BUDialog;

/**
 * The
 * <code>ViewAsStudentWindow</code> class allows an administrator or an
 * instructor to view how a student's layout will be.
 *
 * @author Eric Lowrie
 * @version 2012
 */
public class ViewAsStudentWindow extends BUDialog {
    private DBMSSystemManager dbms;
    private User selected;
    private JTable userTable = new JTable();
    private Vector<User> studentList;
    private Vector<Course> courseList;
    private MainApplicationWindow parent;
    /**
     * This flag will be made false, causing the form to not show if there are 
     * open external windows or if an instructor has no students.
     */
    private boolean showForm = true; 

    /**
     * Creates new form ViewAsStudentWindow.
     *
     * @param appControl The ApplicationControlSystem.
     * @param isAdmin Boolean holding value regarding if user is an
     * administrator (true) or instructor (false)
     * @param parent JFrame of parent window. In all cases, the parent window
     * is the Main Application Window of the user.
     */
    public ViewAsStudentWindow(final ApplicationControlSystem appControl,
            boolean isAdmin, final MainApplicationWindow parent) {
        super(appControl);
        
        //Make sure there are no problematic external windows.
        this.parent = parent;
        checkForExternalWindows();
        if (!showForm) {
            return;
        }
        
        initComponents();
        this.dbms = appControl.getDBMSSystem();
        parent.setVisible(false);
        userTable.setAutoCreateRowSorter(true);
        userTable.setUpdateSelectionOnSort(true);
        setTitle("Weather Viewer - View As Student");
        this.requestFocus();
        userTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        studentList = dbms.getUserManager().obtainAllUsers();

        //Costumize form for user type.
        if (isAdmin) {
            chooseLabel.setText("Choose the student you want to view from.");
            for (int i = 0; i < studentList.size(); i++) {
                if (!studentList.get(i).
                        getUserType().equals(UserType.student)) {
                    studentList.remove(i);
                    i--;
                }
            }
            semesterComboBox.setVisible(false);
            studentList = sortStudents(studentList);
            initializeTableAdmin();
        } else {
            courseList = dbms.getEnrollmentManager().
                    getCoursesForInstructor(appControl.getGeneralService().getUser());
            courseList = sortClasses(courseList);
            
            //Displays JOptionPane if the instructor has no students.
            prepareComboBox();
            
            semesterComboBox.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    updateTableInstructor((String)semesterComboBox.getSelectedItem());
                }
            });
        }

        MouseAdapter doubleClick = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    GeneralService generalService = appControl.getGeneralService();
                    selected = getUser((String)
                            userTable.getValueAt(userTable.getSelectedRow(), 2),
                            studentList);
                    generalService.setOverrideUser(selected);
                    parent.saveSettingsForReturn(parent.getControllerTime(),
                            generalService.getCurrentWeatherCameraResource(),
                            generalService.getCurrentWeatherMapLoopResource(),
                            generalService.getCurrentWeatherStationResource(),
                            parent.getSelectedNotePanelIndex());
                    
                    //Hide what a student souldn't see
                    checkResources(generalService);
                    
                    //Start TimedLoader
                    TimedLoader loader = new TimedLoader() {
                        @Override
                        protected String getLabelText() {
                            return "Bloomsburg Weather Viewer (Student Mode)";
                        }

                        @Override
                        protected void doLoading() {
                            //Actually show window
                            new MainApplicationWindow(appControl, true,
                                    ViewAsStudentWindow.this);
                        }
                    };
                    loader.execute();
                }
            }
            private User getUser(String loginID, Vector<User> activeList) {
                User lookingfor = null;
                for (int i = 0; i < activeList.size(); i++) {
                    if (activeList.get(i).getLoginId().equals(loginID)) {
                        lookingfor = activeList.get(i);
                        break;
                    }
                }
                return lookingfor;
            }
        };
        userTable.addMouseListener(doubleClick);
    }
    
    /**
     * To check if form should be shown
     */
    public void checkToShow(){
        if (showForm) {
            super.postInitialize(true);
        } else {
            parent.setVisible(true);
            this.dispose();
        }
    }
    
    /**
     * Method called by constructor to set up the table layout for an
     * administrator.
     */
    private void initializeTableAdmin() {
        userTable.setModel(new MyDefaultTableModel(studentList.size(), 3));
        userTable.getColumnModel().getColumn(0).setHeaderValue("Last Name");
        userTable.getColumnModel().getColumn(1).setHeaderValue("First Name");
        userTable.getColumnModel().getColumn(2).setHeaderValue("User Name");
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        for (int i = 0; i < studentList.size(); i++) {
            userTable.setValueAt(studentList.get(i).getLastName(), i, 0);
            userTable.setValueAt(studentList.get(i).getFirstName(), i, 1);
            userTable.setValueAt(studentList.get(i).getLoginId(), i, 2);
        }
        userTabbedPane.addTab("All Students", new JScrollPane(userTable));
    }

    /**
     * Method called by constructor and by combo box action listener to list
     * classes for a given semester.
     *
     * @param semester Formatted string representing semester to be listed
     */
    private void updateTableInstructor(String semester) {
        userTabbedPane.removeAll();
        if (semester == null && !courseList.isEmpty()) {
            semester = getFullSemester(courseList.firstElement());
        }

        Vector<Course> thisSemesterCourses = new Vector<Course>();
        final Vector<Vector<User>> masterUserList = new Vector<Vector<User>>();
        for (Course c : courseList) {
            if (getFullSemester(c).equals(semester)) {
                thisSemesterCourses.add(c);
                masterUserList.add(sortStudents(dbms.getEnrollmentManager().getStudentsInCourse(c)));
            }
        }

        int resize = 0, i = 0;
        for (Course c : thisSemesterCourses) {
            JTable newTable = new JTable();
            if (masterUserList.get(i).size() > 0) {
                // This class has students, so put a live table in it.
                newTable.setModel(new MyDefaultTableModel(masterUserList.get(i).size(), 3));
                newTable.getColumnModel().getColumn(0).setHeaderValue("Last Name");
                newTable.getColumnModel().getColumn(1).setHeaderValue("First Name");
                newTable.getColumnModel().getColumn(2).setHeaderValue("User Name");
                newTable.setAutoCreateRowSorter(true);
                newTable.setUpdateSelectionOnSort(true);
                newTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                for (int j = 0; i < masterUserList.size() && j < masterUserList.get(i).size(); j++) {
                    newTable.setValueAt(masterUserList.get(i).get(j).getLastName(), j, 0);
                    newTable.setValueAt(masterUserList.get(i).get(j).getFirstName(), j, 1);
                    newTable.setValueAt(masterUserList.get(i).get(j).getLoginId(), j, 2);
                }
                MouseAdapter doubleClick = new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JTable target = (JTable) e.getSource();
                        if (target.getUpdateSelectionOnSort()) {
                            System.err.println("Sort the table");
                        }
                        if (e.getClickCount() == 2) {
                            GeneralService gs = appControl.getGeneralService();
                            //User originalUser = gs.getUser();

                            selected = getUser((String) target.getValueAt(target.getSelectedRow(), 2), masterUserList.get(userTabbedPane.getSelectedIndex()));
                            /*
                            //This is when masterUserList helps out a lot.
                            selected = (User) masterUserList.get(
                                    userTabbedPane.getSelectedIndex()).
                                    get(target.getSelectedRow());
                            //SO MANY GETS AND METHOD CALLS IN THIS LISTENER
                             */
                            gs.setOverrideUser(selected);
                            parent.saveSettingsForReturn(parent.getControllerTime(),
                                    gs.getCurrentWeatherCameraResource(),
                                    gs.getCurrentWeatherMapLoopResource(),
                                    gs.getCurrentWeatherStationResource(),
                                    parent.getSelectedNotePanelIndex());

                            //Hide what a student souldn't see
                            checkResources(gs);

                            //Start TimedLoader
                            TimedLoader loader = new TimedLoader() {
                                @Override
                                protected String getLabelText() {
                                    return "Bloomsburg Weather Viewer (Student Mode)";
                                }

                                @Override
                                protected void doLoading() {
                                    //Actually show window
                                    new MainApplicationWindow(appControl, true,
                                            ViewAsStudentWindow.this);
                                }
                            };
                            loader.execute();
                        }
                    }

                    private User getUser(String loginID, Vector<User> activeList) {
                        User lookingfor = null;
                        for (int i = 0; i < activeList.size(); i++) {
                            if (activeList.get(i).getLoginId().equals(loginID)) {
                                lookingfor = activeList.get(i);
                                break;
                            }
                        }
                        return lookingfor;
                    }
                };
                newTable.addMouseListener(doubleClick);
            } else {
                // This class is empty, so put a dead table in it.
                newTable.setModel(new MyDefaultTableModel(1, 3));
                newTable.getColumnModel().getColumn(0).setHeaderValue("User Name");
                newTable.getColumnModel().getColumn(1).setHeaderValue("Last Name");
                newTable.getColumnModel().getColumn(2).setHeaderValue("First Name");
                newTable.setValueAt("This class currently has no", 0, 0);
                newTable.setValueAt("students enrolled. You must", 0, 1);
                newTable.setValueAt("add students to this class.", 0, 2);
            }
            userTabbedPane.addTab("<html><p align = \"left\">"
                    + c.getClassIdentifier() + " Section " + c.getSection()
                    + "<br/>" + c.getClassName() + "</p></html>",
                    new JScrollPane(newTable));

            //The following helps resize the window in case of a large class name.
            if (c.getClassName().length() > 15 && c.getClassName().length() > resize) {
                resize = c.getClassName().length() - 15;
            }
            i++;
        }
        // The following helps resize the window in case of a large class name.
        this.setSize(600, 400);
        this.setSize(this.getWidth() + resize * 5, this.getHeight());
        userTabbedPane.setSize(userTabbedPane.getWidth() + resize * 5,
                userTabbedPane.getHeight());

    }

    /**
     * A helper function to check if any windows are open that would prevent 
     * view the program as a student.
     */
    private void checkForExternalWindows() {
        //Find all forms that can't be open.
        String openForms = "";
        if (parent.getNotesManager().isExternal()) {
            //Can not enter student mode if diary window is external.
            openForms += "\nThe daily diary";
        }
        if (ForecasterChooseLesson.areInstancesOpen()) {
            //Can not enter student mode if diary window is external.
            openForms += "\nAny open forecaster lessons";
        }
        if (!openForms.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "You can't view as a studnnt with the following item(s)"
                    + " open:" + openForms + "\nPlease close these items and"
                    + " try again.", "Please Close Items",
                    JOptionPane.INFORMATION_MESSAGE);
            showForm = false;
        }
    }
    
    /**
     * Method called by constructor to set up the combo box shown in the window.
     * Each item in the combo box is a year and semester, and is unique to all
     * other items. It is also responsible for checking if an instructor has any
     * students.
     */
    private void prepareComboBox() {
        String semester;
        boolean contains = false;

        //Gather all unique semesters
        Vector<Course> semList = new Vector<Course>();
        for(Course c : courseList) {
            semester = getFullSemester(c);
            for(int i = 0; i < semList.size(); i++) {
                if(getFullSemester(semList.get(i)).equals(semester)) {
                    contains = true;
                }
            }
            if(!contains)
                semList.add(c);
            contains = false;
        }
        //Order by year
        for(int i = 1; i < semList.size(); i++) {
            int j = i;
            Course checker = semList.get(i);
            while(j > 0 && checker.getYear() < semList.get(i).getYear()) {
                semList.set(i, semList.get(j - 1));
                j--;
            }
            semList.set(j, checker);
        }
        //Order by chronological semester
        Vector<Course> modsemList = new Vector<Course>();
        Vector<Course> collector = new Vector<Course>();
        while(!semList.isEmpty()) {
            if(modsemList.isEmpty()) {
                modsemList.add(semList.remove(0));
            } else {
                // Get each Course of one year
                while(!semList.isEmpty() && modsemList.get(0).getYear() == 
                        semList.get(0).getYear()) {
                    modsemList.add(semList.remove(0));
                }
                // Order by SemesterType based on number of Courses
                if(modsemList.size() == 1) {
                    collector.add(modsemList.remove(0));
                } else if(modsemList.size() == 2) {
                    if(modsemList.get(0).getSemester().compareTo(
                            modsemList.get(1).getSemester()) > 0) {
                        collector.add(modsemList.remove(1));
                        collector.add(modsemList.remove(0));
                    } else {
                        collector.add(modsemList.remove(0));
                        collector.add(modsemList.remove(0));
                        // Notice that it's 0-0 not 0-1 since after the
                        // first remove there will only be one element!
                    }
                } else if(modsemList.size() == 3) {
                    for(int i = 0; i < 2; i++) {
                        if(modsemList.get(i).getSemester().compareTo(
                            modsemList.get(i + 1).getSemester()) > 0) {
                            Course temp = modsemList.get(i);
                            modsemList.set(i, modsemList.get(i + 1));
                            modsemList.set(i + 1, temp);
                        }
                    }
                    for(int i = 0; i < 3; i++)
                        collector.add(modsemList.remove(0));
                } else {
                    for(int i = 0; i < 3; i++) {
                        if(modsemList.get(i).getSemester().compareTo(
                            modsemList.get(i + 1).getSemester()) > 0) {
                            Course temp = modsemList.get(i);
                            modsemList.set(i, modsemList.get(i + 1));
                            modsemList.set(i + 1, temp);
                        }
                    }
                    for(int i = 0; i < 4; i++)
                        collector.add(modsemList.remove(0));
                }
            }
        }
        while(!modsemList.isEmpty()) {
            collector.add(modsemList.remove(0));
        }
        semesterComboBox.removeAllItems();
        for(Course c : collector) {
            semesterComboBox.addItem(getFullSemester(c));
        }
        semesterComboBox.setSelectedIndex(semesterComboBox.getItemCount() - 1);
        if (courseList.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "You currently have no classes and cannot change view mode.",
                    "Weather Viewer - View Mode", JOptionPane.ERROR_MESSAGE);
            showForm = false;
        } else {
            updateTableInstructor((String)semesterComboBox.getItemAt(
                semesterComboBox.getItemCount() - 1));
        }
    }
    
    /**
     * Simple getter method that gets a formatted string for use in the
     * semester combo box.
     * @param c Course to have formatted string given
     * @return Formatted string showing year and semester. Example: "2012 Fall"
     */
    private String getFullSemester(Course c) {
        return c.getYear() + " " + c.getSemester().name();
    }
    
    /**
     * Sorting method that helps put an order to listed classes in the display
     * for instructors. Order is set from lowest class ID from the left to
     * highest class ID to the right. In the case of matching class IDs, course
     * section number chooses with lowest starting at the left again.
     *
     * @param sorting Vector to be sorted.
     * @return QuickSorted Vector, sorted as stated above.
     */
    private Vector<Course> sortClasses(Vector<Course> sorting) {
        if (sorting.size() < 2) {
            return sorting;
        }
        Course pivot = sorting.get(sorting.size() / 2);
        Vector<Course> less = new Vector<Course>();
        Vector<Course> greater = new Vector<Course>();
        for (Course c : sorting) {
            if (c.getClassIdentifier().compareTo(pivot.getClassIdentifier()) < 0) {
                less.add(c);
            } else if (c.getClassIdentifier().equals(pivot.getClassIdentifier())) {
                if (c.getCourseNumber() < pivot.getCourseNumber()) {
                    less.add(c);
                }
            } else {
                greater.add(c);
            }
        }
        Vector<Course> totalLess = sortClasses(less);
        Vector<Course> totalGreater = sortClasses(greater);
        totalLess.add(pivot);
        totalLess.addAll(totalGreater);
        return totalLess;
    }

    /**
     * Sorting method that helps put an order to listed students in the display
     * for both instructors and administrators. Order is set for last name, then
     * first if matching, in alphabetical order.
     *
     * @param sorting Vector to be sorted.
     * @return QuickSorted Vector, sorted as stated above.
     */
    private Vector<User> sortStudents(Vector<User> sorting) {
        if (sorting.size() < 2) {
            return sorting;
        }
        User pivot = sorting.get(sorting.size() / 2);
        Vector<User> less = new Vector<User>();
        Vector<User> greater = new Vector<User>();
        for (User u : sorting) {
            if (u.getLastName().compareToIgnoreCase(pivot.getLastName()) < 0) {
                less.add(u);
            } else if (u.getLastName().equals(pivot.getLastName())) {
                if (u.getFirstName().compareToIgnoreCase(pivot.getFirstName()) < 0) {
                    less.add(u);
                }
            } else {
                greater.add(u);
            }
        }
        Vector<User> totalLess = sortStudents(less);
        Vector<User> totalGreater = sortStudents(greater);
        totalLess.add(pivot);
        totalLess.addAll(totalGreater);
        return totalLess;
    }
    
    /**
     * Checks it see which of the current resources are visible to a student and
     * sets any panel showing an invisible <code>Resource</code> to "None" by
     * changing the fields of the program's <code>GeneralService</code>.
     * @param gs The fields of the program's <code>GeneralService</code>.
     */
    private void checkResources(GeneralService gs) {
        if(gs.getCurrentWeatherCameraResource() != null
                && !gs.getCurrentWeatherCameraResource().isVisible()) {
            gs.setCurrentWeatherCameraResource(null);
        }
        if(gs.getCurrentWeatherMapLoopResource() != null
                && !gs.getCurrentWeatherMapLoopResource().isVisible()) {
            gs.setCurrentWeatherMapLoopResource(null);
        }
        if(gs.getCurrentWeatherStationResource() != null
                && !gs.getCurrentWeatherStationResource().isVisible()) {
            gs.setCurrentWeatherStationResource(null);
        }
    }

    /**
     * Returns the <code>MainApplicationWindow</code> that called this window.
     * @return The <code>MainApplicationWindow</code> that called this window.
     */
    public MainApplicationWindow getCallingWindow() {
        return parent;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userTabbedPane = new javax.swing.JTabbedPane();
        doubleClickLabel = new javax.swing.JLabel();
        chooseLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        semesterComboBox = new javax.swing.JComboBox<String>();

        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        userTabbedPane.setTabPlacement(javax.swing.JTabbedPane.RIGHT);

        doubleClickLabel.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        doubleClickLabel.setText("Double-click on a user to view their login mode.");

        chooseLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        chooseLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chooseLabel.setText("Choose your semester, course, then student you want to view from.");

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        semesterComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(doubleClickLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeButton))
                    .addComponent(userTabbedPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chooseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(semesterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chooseLabel)
                    .addComponent(semesterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(doubleClickLabel)
                    .addComponent(closeButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        parent.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        parent.setVisible(true);
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel chooseLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel doubleClickLabel;
    private javax.swing.JComboBox<String> semesterComboBox;
    private javax.swing.JTabbedPane userTabbedPane;
    // End of variables declaration//GEN-END:variables
}
