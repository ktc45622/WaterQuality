package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.Course;
import weather.common.data.SemesterType;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.DBMSEnrollmentManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.DataFilter;
import weather.common.utilities.PasswordEmailer;
import weather.common.utilities.WeatherLogger;

/**
 * The
 * <code>ManageStudentEnrollment</code> class creates a form that allows for
 * editing enrolled students.
 *
 *
 * @author Bloomsburg University Software Engineering
 * @author Mike Graboske (2008)
 * @author Ora Merkel (2009)
 * @author Mike Nacko (2011)
 * @author Nate Hartzler (2012)
 * @author Luke Stine (2012)
 * @author Xiang Li (2014)
 * @version 2012
 */
public class ManageStudentEnrollment extends BUDialog {

    private DBMSCourseManager courseMgr;
    private DBMSEnrollmentManager enrollmentManager;
    private DBMSSystemManager dbms;
    private DBMSUserManager userManager;
    private User currentUser;
    private boolean isAdmin;
    private DefaultTableModel allStudents;
    private DefaultTableModel studentsInClass;
    private File file;
    private Course course;
    private Vector<User> notInCourse;
    private Vector<User> inCourse;

    /**
     * Creates new form ManageStudentEnrollment window.
     *
     * @param appControl The implementation to be used to create the window.
     * @param course The course displayed when the from opens (null defaults
     * to the first alphabetically)
     * @param isAdmin Determines if the current User has administrator rights.
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    public ManageStudentEnrollment(ApplicationControlSystem appControl, 
            Course course, boolean isAdmin, boolean shouldCenter) {
        super(appControl);
        this.dbms = appControl.getGeneralService().getDBMSSystem();
        this.isAdmin = isAdmin;
        courseMgr = dbms.getCourseManager();
        enrollmentManager = dbms.getEnrollmentManager();
        userManager = dbms.getUserManager();
        currentUser = appControl.getGeneralService().getUser();
        allStudents = new DefaultTableModel();
        allStudents.addColumn("Students Not In Your Class");
        studentsInClass = new DefaultTableModel();
        studentsInClass.addColumn("Students In Your Class");

        initComponents();

        /**
         * This call loads the control classComboBox.  In doing so,
         * the first addItem call fires its listener.  This means
         * the class variable course in the first alphabetically.
         */
        generateClassListComboBoxNameOrder();

        if(course != null){
            //set to given course by firing handler.
            classComboBox.setSelectedItem(course);
        }

        int width = 1000 + this.getInsets().left + this.getInsets().right;
        int height = 562 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();

        super.postInitialize(shouldCenter);
    }



    /**
     * Gets all the current classes and create a ComboBox based on the classes.
     * List the classes by year and semester
     */
    private void generateClassListComboBoxTimeOrder() {
        classComboBox.removeAllItems();

        Vector<Course> courses;

        if (isAdmin) {
            courses = courseMgr.obtainAllCourses();
        } else {
            courses = courseMgr.obtainAllCoursesTaughyByUser(currentUser);
        }
        Collections.sort(courses, new Comparator<Course>() {

            /**
             * Helper function to compute the number of semesters that would
             * have passed, including the current one since the beginning of a
             * theoretical year 0.  Here, "current one" means the semester of
             * the given course.
             * @param thisCourse The course to be assigned an ordering number.
             * @return The ordering number.
             */
            private int getCourseTime(Course thisCourse){
                int result = thisCourse.getYear() * 4;

                //Add 0 for Spring, so do nothing
                if (thisCourse.getSemester().equals(SemesterType.Summer)) {
                    result++;
                }
                if (thisCourse.getSemester().equals(SemesterType.Fall)) {
                    result += 2;
                }
                if (thisCourse.getSemester().equals(SemesterType.Winter)) {
                    result += 3;
                }
                return result;
            }

            @Override
            public int compare(Course course1, Course course2) {
                int course1number = getCourseTime(course1);
                int course2number = getCourseTime(course2);

                //Negate for decending time list.
                return -new Integer(course1number)
                        .compareTo(new Integer(course2number));
            }
        });

        for (Course c : courses) {
            classComboBox.addItem(c);
        }
    }

    /**
     * Gets all the current classes and create a ComboBox based on the classes.
     * List the classes by the order of the English character
     */
    private void generateClassListComboBoxNameOrder() {

        classComboBox.removeAllItems();

        Vector<Course> courses;

        if (isAdmin) {
            courses = courseMgr.obtainAllCourses();
        } else {
            courses = courseMgr.obtainAllCoursesTaughyByUser(currentUser);
        }
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course course1, Course course2) {
                return course1.getClassName().toLowerCase()
                        .compareTo(course2.getClassName().toLowerCase());
            }
        });

        for (Course c : courses) {
            classComboBox.addItem(c);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sortingButtonGroup = new javax.swing.ButtonGroup();
        headerButtonGroup = new javax.swing.ButtonGroup();
        classComboBox = new javax.swing.JComboBox<Course>();
        classNameTextField = new javax.swing.JTextField();
        instructor_JLabel = new javax.swing.JLabel();
        course_Name_JLabel = new javax.swing.JLabel();
        department_JLabel = new javax.swing.JLabel();
        course_JLabel = new javax.swing.JLabel();
        section_JLabel = new javax.swing.JLabel();
        departmentTextField = new javax.swing.JTextField();
        courseTextField = new javax.swing.JTextField();
        sectionTextField = new javax.swing.JTextField();
        closeButton = new javax.swing.JButton();
        select_Course_JLabel = new javax.swing.JLabel();
        instructorTextBox = new javax.swing.JTextField();
        manage_JTabbedPane = new javax.swing.JTabbedPane();
        manage_Individual_Students_JPanel = new javax.swing.JPanel();
        moveStudentButtonPanel = new javax.swing.JPanel();
        moveToClass = new javax.swing.JButton();
        removeFromClass = new javax.swing.JButton();
        addNewStudent = new javax.swing.JButton();
        inClassPane = new javax.swing.JScrollPane();
        inClass = new javax.swing.JTable();
        notInClassPane = new javax.swing.JScrollPane();
        notInClass = new javax.swing.JTable();
        mange_Entire_Class_JPanel = new javax.swing.JPanel();
        addStudentsFromFileButton = new javax.swing.JButton();
        removeAllStudentsButton = new javax.swing.JButton();
        fileWithoutHeadersRadio = new javax.swing.JRadioButton();
        fileWithHeadersRadio = new javax.swing.JRadioButton();
        DescriptiveLabel = new javax.swing.JLabel();
        exampleButton1 = new javax.swing.JButton();
        exampleButton2 = new javax.swing.JButton();
        step1 = new javax.swing.JLabel();
        step2Label = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        step3Label = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        removeStudentsJLabel = new javax.swing.JLabel();
        jLabelFilePath = new javax.swing.JLabel();
        csvPanel = new javax.swing.JPanel();
        csvInstructionsLabel = new javax.swing.JLabel();
        jLabelYear = new javax.swing.JLabel();
        yearTextField = new javax.swing.JTextField();
        jLabelSemester = new javax.swing.JLabel();
        semesterTextField = new javax.swing.JTextField();
        yearAndSemesterRadioButton = new javax.swing.JRadioButton();
        nameRadioButton = new javax.swing.JRadioButton();
        sortChoiceLabel = new javax.swing.JLabel();
        emailAllButton = new javax.swing.JButton();
        emailSelectedButton = new javax.swing.JButton();

        setTitle("Weather Viewer - Course Editor");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        classComboBox.setToolTipText("The course name you are modifying");
        classComboBox.setDoubleBuffered(true);
        classComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classComboBoxActionPerformed(evt);
            }
        });
        getContentPane().add(classComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, 12, 719, 22));

        classNameTextField.setEditable(false);
        classNameTextField.setToolTipText("The course name");
        getContentPane().add(classNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 78, 862, 22));

        instructor_JLabel.setText("Instructor:");
        getContentPane().add(instructor_JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(506, 183, 108, 22));

        course_Name_JLabel.setText("Course Name:");
        getContentPane().add(course_Name_JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 78, 108, 22));

        department_JLabel.setText("Department Prefix:");
        department_JLabel.setToolTipText("");
        getContentPane().add(department_JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 113, 108, 22));

        course_JLabel.setText("Course Number:");
        getContentPane().add(course_JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(506, 113, 108, 22));

        section_JLabel.setText("Section:");
        getContentPane().add(section_JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 183, 108, 22));

        departmentTextField.setEditable(false);
        departmentTextField.setToolTipText("The department the course belongs to");
        getContentPane().add(departmentTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 113, 368, 22));

        courseTextField.setEditable(false);
        courseTextField.setToolTipText("The course");
        getContentPane().add(courseTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 113, 368, 22));

        sectionTextField.setEditable(false);
        sectionTextField.setToolTipText("The section number");
        getContentPane().add(sectionTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 183, 368, 22));

        closeButton.setText("Close");
        closeButton.setToolTipText("Close this window");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(871, 525, 117, 25));

        select_Course_JLabel.setText("Select the course you would like to modify:");
        getContentPane().add(select_Course_JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 246, 16));

        instructorTextBox.setEditable(false);
        instructorTextBox.setToolTipText("The name of the instructor");
        getContentPane().add(instructorTextBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 183, 368, 22));

        manage_JTabbedPane.setMinimumSize(new java.awt.Dimension(149, 100));
        manage_JTabbedPane.setPreferredSize(new java.awt.Dimension(605, 300));
        manage_JTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                manage_JTabbedPaneStateChanged(evt);
            }
        });

        manage_Individual_Students_JPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        moveStudentButtonPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 32));

        moveToClass.setIcon(IconProperties.getArrowRightSmallIcon());
        moveToClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveToClassActionPerformed(evt);
            }
        });
        moveStudentButtonPanel.add(moveToClass);

        removeFromClass.setIcon(IconProperties.getArrowLeftSmallIcon());
        removeFromClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromClassActionPerformed(evt);
            }
        });
        moveStudentButtonPanel.add(removeFromClass);

        addNewStudent.setText("<html><center>Add New<br/>Student</center></html>");
        addNewStudent.setToolTipText("Add a new student to the course");
        addNewStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewStudentActionPerformed(evt);
            }
        });
        moveStudentButtonPanel.add(addNewStudent);

        manage_Individual_Students_JPanel.add(moveStudentButtonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(438, 12, 100, 246));

        inClassPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inClassPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        inClassPane.setMaximumSize(new java.awt.Dimension(160, 160));
        inClassPane.setMinimumSize(new java.awt.Dimension(160, 160));
        inClassPane.setPreferredSize(new java.awt.Dimension(160, 160));

        inClass.setModel(this.studentsInClass);
        inClass.setColumnSelectionAllowed(true);
        inClassPane.setViewportView(inClass);
        inClass.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        manage_Individual_Students_JPanel.add(inClassPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 12, 414, 246));

        notInClassPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notInClassPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        notInClassPane.setMaximumSize(new java.awt.Dimension(160, 160));
        notInClassPane.setMinimumSize(new java.awt.Dimension(160, 160));
        notInClassPane.setPreferredSize(new java.awt.Dimension(160, 160));

        notInClass.setModel(this.allStudents);
        notInClassPane.setViewportView(notInClass);

        manage_Individual_Students_JPanel.add(notInClassPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 414, 246));

        manage_JTabbedPane.addTab("Add/Remove Individual Students", manage_Individual_Students_JPanel);

        mange_Entire_Class_JPanel.setPreferredSize(new java.awt.Dimension(600, 300));
        mange_Entire_Class_JPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addStudentsFromFileButton.setText("Add Students");
        addStudentsFromFileButton.setToolTipText("Adds all users from the file above to the class and database");
        addStudentsFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStudentsFromFileButtonActionPerformed(evt);
            }
        });
        mange_Entire_Class_JPanel.add(addStudentsFromFileButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 117, -1, 19));

        removeAllStudentsButton.setText("Remove All Students");
        removeAllStudentsButton.setToolTipText("Removes all students from the course");
        removeAllStudentsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllStudentsButtonActionPerformed(evt);
            }
        });
        mange_Entire_Class_JPanel.add(removeAllStudentsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(302, 151, -1, 18));

        headerButtonGroup.add(fileWithoutHeadersRadio);
        fileWithoutHeadersRadio.setText("File Without Headers");
        fileWithoutHeadersRadio.setToolTipText("Imports a file that does not have headers in each column, click the example button for an example");
        fileWithoutHeadersRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileWithoutHeadersRadioActionPerformed(evt);
            }
        });
        mange_Entire_Class_JPanel.add(fileWithoutHeadersRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 62, -1, -1));

        headerButtonGroup.add(fileWithHeadersRadio);
        fileWithHeadersRadio.setSelected(true);
        fileWithHeadersRadio.setText("File With Headers");
        fileWithHeadersRadio.setToolTipText("Imports a file that has headers in each column, click the example button for an example");
        fileWithHeadersRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileWithHeadersRadioActionPerformed(evt);
            }
        });
        mange_Entire_Class_JPanel.add(fileWithHeadersRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 62, -1, -1));

        DescriptiveLabel.setText("To add a group of students to the database and the class, follow these steps");
        mange_Entire_Class_JPanel.add(DescriptiveLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, -1, -1));

        exampleButton1.setText("Sample");
        exampleButton1.setToolTipText("An example of a file with headers");
        exampleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exampleButton1ActionPerformed(evt);
            }
        });
        mange_Entire_Class_JPanel.add(exampleButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(179, 65, -1, 19));

        exampleButton2.setText("Sample");
        exampleButton2.setToolTipText("An example of a file without headers");
        exampleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exampleButton2ActionPerformed(evt);
            }
        });
        mange_Entire_Class_JPanel.add(exampleButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(414, 65, -1, 18));

        step1.setText("1) Create a comma-separated value (.csv) file with one of the following formats:");
        mange_Entire_Class_JPanel.add(step1, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 42, -1, -1));

        step2Label.setText("2) Browse to the file:");
        step2Label.setToolTipText("");
        mange_Entire_Class_JPanel.add(step2Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 91, -1, -1));

        browseButton.setText("Browse");
        browseButton.setToolTipText("Browse to the csv file containg your students");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        mange_Entire_Class_JPanel.add(browseButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 91, -1, 19));

        step3Label.setText("3) Add students to the database and class:");
        step3Label.setToolTipText("");
        mange_Entire_Class_JPanel.add(step3Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 117, -1, -1));
        mange_Entire_Class_JPanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 143, 950, 1));

        removeStudentsJLabel.setText("You can also remove all students from the class:");
        mange_Entire_Class_JPanel.add(removeStudentsJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 151, -1, -1));

        jLabelFilePath.setText("ex) C:\\Path\\File.csv");
        jLabelFilePath.setToolTipText("ex) C:\\Path\\File.csv");
        jLabelFilePath.setName("jLabelPath"); // NOI18N
        mange_Entire_Class_JPanel.add(jLabelFilePath, new org.netbeans.lib.awtextra.AbsoluteConstraints(254, 91, 705, -1));

        manage_JTabbedPane.addTab("Manage Multiple Students", mange_Entire_Class_JPanel);

        csvPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        csvInstructionsLabel.setText("<html>\nA sample of the .csv formatted text is displayed below.<br/>\n<br/>\n<table border = 1>\n<tr>\n<td>Last Name</td>\n<td>First Name</td>\n<td>Email</td>\n</tr>\n<tr>\n<td>Smith</td>\n<td>Bob</td>\n<td>abc12345@huskies.bloomu.edu</td>\n</tr>\n</table>\n<br/>\nPlease note the following:<br/>\n<ul>\n<li>Use this format when creating a .csv file to import multiple students into a class.</li>\n<li>The only difference between \"files with headers\" and \"files without headers\" is that a \"file with headers\" will contain the first line of text above, while the \"file without headers\" will not.</li>\n<li>You can create a .csv file in Microsoft Excel by clicking \"Save As\" and changing the \"Save As Type\" to \"CSV File\" or \"Comma-Separated Values\", depending on your version of Excel.</li>\n<li>Usernames and passwords for new users will be based on their email addresses, but passwords must be changed to send password emails.</li>\n<li>A results file will be created in the same directory as the imported file.</li>\n</ul>\n</html>");
        csvInstructionsLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        csvPanel.add(csvInstructionsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 12, 916, 246));

        manage_JTabbedPane.addTab("CSV Help", csvPanel);

        getContentPane().add(manage_JTabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 218, 976, 300));
        manage_JTabbedPane.getAccessibleContext().setAccessibleName("Add/Remove Individual Students");

        jLabelYear.setText("Year:");
        jLabelYear.setToolTipText("The Year");
        jLabelYear.setMaximumSize(new java.awt.Dimension(61, 14));
        jLabelYear.setMinimumSize(new java.awt.Dimension(61, 14));
        jLabelYear.setPreferredSize(new java.awt.Dimension(61, 14));
        getContentPane().add(jLabelYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 148, 108, 22));

        yearTextField.setEditable(false);
        yearTextField.setToolTipText("The year");
        getContentPane().add(yearTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 148, 368, 22));

        jLabelSemester.setText("Semester:");
        jLabelSemester.setToolTipText("The Semester");
        getContentPane().add(jLabelSemester, new org.netbeans.lib.awtextra.AbsoluteConstraints(506, 148, 108, 22));

        semesterTextField.setEditable(false);
        semesterTextField.setToolTipText("The semester");
        getContentPane().add(semesterTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 148, 368, 22));

        sortingButtonGroup.add(yearAndSemesterRadioButton);
        yearAndSemesterRadioButton.setText("List By Year and Semester");
        yearAndSemesterRadioButton.setToolTipText("List classes by time");
        yearAndSemesterRadioButton.setActionCommand("");
        yearAndSemesterRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearAndSemesterRadioButtonActionPerformed(evt);
            }
        });
        getContentPane().add(yearAndSemesterRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 40, 179, 25));

        sortingButtonGroup.add(nameRadioButton);
        nameRadioButton.setSelected(true);
        nameRadioButton.setText("List By Name");
        nameRadioButton.setToolTipText("List classes by Name");
        nameRadioButton.setActionCommand("");
        nameRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameRadioButtonActionPerformed(evt);
            }
        });
        getContentPane().add(nameRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 40, 101, 25));

        sortChoiceLabel.setText("Select how to sort courses:");
        getContentPane().add(sortChoiceLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, 157, 25));

        emailAllButton.setText("Send Passwords to All Students in Class");
        emailAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailAllButtonActionPerformed(evt);
            }
        });
        getContentPane().add(emailAllButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(315, 525, 261, 25));

        emailSelectedButton.setText("Send Passwords to Selected Students in Class");
        emailSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailSelectedButtonActionPerformed(evt);
            }
        });
        getContentPane().add(emailSelectedButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 525, 297, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Generates what happens when the updateClassButton is pressed.
     *
     * @param evt The updateClassButton is pressed.
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * Removes all students from the current course. This also does some user
     * clean up in the sense that when the users are deleted if a specific user
     * is not enrolled in another class the user will be deleted from the
     * system.
     *
     * @param evt The event that removeAllStudentsButton was pressed.
     */
    private void removeAllStudentsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllStudentsButtonActionPerformed
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove all students?\n",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) {
            return;
        }

        enrollmentManager.removeAllStudentsFromCourse(course);
        JOptionPane.showMessageDialog(this, "All students have been removed from the class.");
        fillStudentModel();
}//GEN-LAST:event_removeAllStudentsButtonActionPerformed

    /**
     * Adds students into the selected course from a CSV file.
     *
     * @param evt The event that the add studentsFromFileButton was pressed.
     */
    private void addStudentsFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStudentsFromFileButtonActionPerformed
        //Variables for data input
        FileInputStream inputFile;
        DataInputStream data;
        BufferedReader br;

        //Variables for results output.
        Writer output;
        String resultsFileName;
        File outFile;

        if (file == null) {
            JOptionPane.showMessageDialog(this, "Please browse to a file using the browse button above.");
            return;
        }

        try {
            inputFile = new FileInputStream(file);
            data = new DataInputStream(inputFile);
            br = new BufferedReader(new InputStreamReader(data));

            //Result Counts
            int addedUsers = 0;
            int existingEnrollments = 0;
            int alreadyInCourse = 0;
            int errorLines = 0;

            //Start of results file text
            String results = "Last Name,First Name,User Name,Password,Email,Results\n";

            //To hold each line of date read from the input file.
            String inputLine;

            //Fields to be read from file
            String firstName;
            String lastName;
            String email;

            //Assume there is a header to read.
            boolean headerMarker = true;

            while ((inputLine = br.readLine()) != null) {
                /*
                 * If choose the option that includes headers in the csv, simply
                 * skip to the next line becuase we don't need them
                 */
                if (fileWithHeadersRadio.isSelected() && headerMarker == true) {
                    inputLine = br.readLine();
                    headerMarker = false;
                }
                
                //Make sure tokens are pressent.
                inputLine = inputLine.replace(",", " ,");
                
                //Final strings for results file.
                final String missingData = "<Missing Data>";
                final String notAssigned = "<Not Assigned>";

                //Strings to hold additional user data which start as unassigned.
                String userName = notAssigned;
                String password = notAssigned;

                //Result string for output file for this user
                String userResult;
                
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                //Clear old data
                lastName = "";
                firstName = "";
                email = "";

                //Tey to retrive data
                if (st.hasMoreTokens()) {
                    lastName = st.nextToken().trim();
                }
                if (st.hasMoreTokens()) {
                    firstName = st.nextToken().trim();
                }
                if (st.hasMoreTokens()) {
                    email = st.nextToken().trim();
                }
                
                //Get result of reading input
                if(firstName.equals("") || lastName.equals("") || email.equals("")){
                    userResult = "<Student Not Added - Missing Data>";
                    if(firstName.equals("")){
                        firstName = missingData;
                    }
                    if(lastName.equals("")){
                        lastName = missingData;
                    }
                    if(email.equals("")){
                        email = missingData;
                    }
                    errorLines++;
                }else if(!DataFilter.isEmail(email)){
                    userResult = "<Student Not Added - Email Not Valid>";
                    errorLines++;
                }else{
                    //Check if user is in database (look for email match)
                    Vector<User> usersWithEmail = userManager.obtainUsersByEmail(email);
                    if(usersWithEmail.isEmpty()){
                        //Determine new user's username and password
                        //(based on email substring before @ symbol
                        password = email.substring(0, email.lastIndexOf("@"));
                        
                        //If necessary, add _X, where X is an integer, to the 
                        //password so the username is unique
                        userName = firstAvailableUserName(password);
                        
                        //New user must be added to database
                        User newUser = new User();
                        newUser.setUserType(UserType.student);
                        newUser.setFirstName(firstName);
                        newUser.setLastName(lastName);
                        newUser.setEmailAddress(email);
                        newUser.setPassword(password);
                        newUser.setLoginId(userName);
                        userManager.addUser(newUser);
                        
                        //Add user to class
                        enrollmentManager.insertStudentIntoCourse(newUser, course);
                        
                        //Prepare result
                        addedUsers++;
                        userResult = "<Student Added To Database And Course>"; 
                    }else{
                        //Get existing user (since array max size is
                        //one, we know it is the first array element) 
                        User existingUser = usersWithEmail.firstElement();
                        
                        //Get user's data for result string.
                        firstName = existingUser.getFirstName();
                        lastName = existingUser.getLastName();
                        userName = existingUser.getLoginId();
                        password = "<Encrypted Password>";
                        
                        //Check if existing user is already in class
                        if(isUserInCourse(existingUser)){
                            userResult = "<Student Already In Course>";
                            alreadyInCourse++;
                        } else {
                            //Add user to class
                            enrollmentManager.insertStudentIntoCourse(existingUser, course);
                            userResult = "<Existing Student Added To Course>";
                            existingEnrollments++;
                        }
                    }
                }
                
                //Add this user's data and result to results file string.
                String spacer = ",";
                results += lastName + spacer;
                results += firstName + spacer;
                results += userName + spacer;
                results += password + spacer;
                results += email + spacer;
                results += userResult + "\n"; 
            }

            //Send results to file.
            
            //Make new resultsFileName
            String path = file.getAbsolutePath();
            resultsFileName = path.substring(0, path.lastIndexOf("."))
                    + "_results.csv";
            
            //Make output file
            outFile = new File(resultsFileName);
            output = new BufferedWriter(new FileWriter(outFile));
            output.append(results);
            output.close();
            
            //Show results summary
            int totalEnrollments = addedUsers + existingEnrollments;
            int totalFileCount = totalEnrollments + alreadyInCourse
                    + errorLines;
            String message = "Results Summary:\n"
                    + "- Total Students Records Listed in File: " + totalFileCount + "\n\n" 
                    + "- Newly Created Students Added to Course: " + addedUsers + "\n"
                    + "- Existing Students Added to Course: " + existingEnrollments + "\n"
                    + "- Total Enrollments: " + totalEnrollments + "\n\n"
                    + "- Students Already Enrolled in Course: " + alreadyInCourse + "\n\n"
                    + "- Students Unable to Enroll: " + errorLines + "\n\n"
                    + "An enrollment log file was created in the input file's directory.\n"
                    + "Passwords emails were not sent to the students.  Use the \"Send "
                    + "Passwords\"\ntools in the Course Editor window to email "
                    +"passwords to students.";
            JOptionPane.showMessageDialog(this, message, "Results Summary", 
                    JOptionPane.INFORMATION_MESSAGE);
            

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "The file you selected could not be found.");
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "An error has occurred while reading the file.");
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
        }
        
        //Test for remaining space in application home, which has no effect
        //if the save was not there.
        StorageSpaceTester.testApplicationHome();
        
        //Clear for next input file.
        file = null;
        jLabelFilePath.setText("ex) C:\\Path\\File.csv");
        
        //Update tables.
        fillStudentModel();
}//GEN-LAST:event_addStudentsFromFileButtonActionPerformed

    /**
     * Checks if a given user is in the selected course.
     * @param user The user to be checked.
     * @return True if the user is in the course, false otherwise.
     */
    private boolean isUserInCourse(User user){
        Vector<User> courseList = enrollmentManager.getStudentsInCourse(course);
        for(User student : courseList) {
            if(student.getLoginId().equals(user.getLoginId())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Finds the first available username based on a given password.  If the
     * password cannot be used, "_X" is added where X is the first available
     * integer.
     * @param password The password
     * @return The first available username based on a given password
     */
    private String firstAvailableUserName(String password) {
        String result = password;   //Assume password as is.
        int counter = 1;
        //Test current would-be username in while clause
        while (!userManager.obtainUsersByUsername(result).isEmpty()) {
            //username found, so prepare for next test.
            result = password + "_" + counter;
            counter++;  
        }
        
        //Available username found in last while test.
        return result;
    }

    /**
     * Fills the two tables in the add user tab with names of students in
     * the class.
     */
    private void fillStudentModel(){
        notInCourse = alphabetizeStudents(userManager.obtainAllUsers());
        inCourse = alphabetizeStudents(enrollmentManager.getStudentsInCourse(course));
        allStudents.setRowCount(notInCourse.size());
        studentsInClass.setRowCount(inCourse.size());

        String name;
        for(int i = 0; i < notInCourse.size(); i++) {
            User current = notInCourse.get(i);
            // Are we a student?
            if(current.getUserType() == UserType.student)
            {
                // Are we already enrolled in the course?
                boolean found = false;
                name = current.getFirstName()+" "+current.getLastName();
                for(int j = 0; j < inCourse.size(); j++) {
                    if(inCourse.get(j).getLoginId().equals(current.getLoginId())) {
                        found = true;
                    }
                }
                if(found)
                {
                    allStudents.removeRow(i);
                    notInCourse.removeElementAt(i);
                    i--;
                }
                else if(allStudents != null) {
                    allStudents.setValueAt(name, i, 0);
                }
            }
            //remove user from list so only students exist
            else
            {
                allStudents.removeRow(i);
                notInCourse.removeElementAt(i);
                i--;
            }
        }
        for(int i=0; i<inCourse.size(); i++)
        {
            String name2=inCourse.get(i).getFirstName()+" "+inCourse.get(i).getLastName();
            studentsInClass.setValueAt(name2, i, 0);//addElement(name2);
        }
        //At this point there will be a vector called notInCourse that has the
        //same amount of elements (Users) as the allStudents vector (names)
        //There is also a vector called inCourse that contains same amount of
        //elements (Users) as the studentsInClass Vector (names)
    }

    /**
     * Helper function to alphabetize a vector of students.
     * @param sorting A vector of students.
     * @return The alphabetized vector of students.
     */
    private Vector<User> alphabetizeStudents(Vector<User> sorting) {
        if(sorting.size() < 2) {
            return sorting;
        }
        User pivot = sorting.get(sorting.size() / 2);
        Vector<User> less = new Vector<User>();
        Vector<User> greater = new Vector<User>();
        sorting.remove(sorting.size() / 2);
        for(int i = 0; i < sorting.size(); i++) {
            User u = sorting.get(i);
            if(pivot.compareTo(u) < 0) {
                greater.add(u);
            } else {
                less.add(u);
            }
        }
        less = alphabetizeStudents(less);
        less.add(pivot);
        less.addAll(alphabetizeStudents(greater));
        return less;
    }

    private void exampleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exampleButton1ActionPerformed
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + System.getProperty("user.dir") + File.separator + "documents" + File.separator + "csvExampleHeaders.csv");
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
        }
}//GEN-LAST:event_exampleButton1ActionPerformed

    private void exampleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exampleButton2ActionPerformed
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + System.getProperty("user.dir") + File.separator + "documents" + File.separator + "csvExampleNoHeaders.csv");
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
        }
}//GEN-LAST:event_exampleButton2ActionPerformed

    private void fileWithHeadersRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileWithHeadersRadioActionPerformed
    }//GEN-LAST:event_fileWithHeadersRadioActionPerformed

    private void fileWithoutHeadersRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileWithoutHeadersRadioActionPerformed
    }//GEN-LAST:event_fileWithoutHeadersRadioActionPerformed

    /**
     * Allows user to browse to the location of the .csv file. Displays the
     * path in a label. Sets file location to be used incase results.csv is
     * generated.
     *
     * @param evt Event that button is clicked.
     */
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                new File(CommonLocalFileManager.getRootDirectory()), 
                null, "Load CSV File", null, ".csv", this);
        if (file == null) {
            return;
        }
        jLabelFilePath.setText(file.getAbsolutePath());
        jLabelFilePath.setToolTipText(file.getAbsolutePath());
    }//GEN-LAST:event_browseButtonActionPerformed

    /**
     * Adds a new User to the system and to the current class.
     *
     * @param evt Event that button is clicked.
     */
    private void addNewStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewStudentActionPerformed
        new AddEditUserWindow(appControl.getAdministratorControlSystem(), course);
        fillStudentModel();
    }//GEN-LAST:event_addNewStudentActionPerformed
    /**
     * This method is called when the Move To Class button is clicked. It will
     * move a student to a class.
     *
     * @param evt The event that the Move To Class button is clicked.
     */
    private void moveToClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveToClassActionPerformed
        int[] index=notInClass.getSelectedRows();
        User tempUser;
        for (int i=0; i<index.length; i++)
        {
            tempUser=notInCourse.get(index[i]);
            enrollmentManager.insertStudentIntoCourse(tempUser, course);
        }
        fillStudentModel();
        //reset current selected userIndexes to first selected location
        if(index.length>0)
        {
            if(index[0]<notInClass.getRowCount())
                notInClass.setRowSelectionInterval(index[0], index[0]);
            else if(notInClass.getRowCount()>0)
                notInClass.setRowSelectionInterval(notInClass.getRowCount()-1, notInClass.getRowCount()-1);
        }
    }//GEN-LAST:event_moveToClassActionPerformed
    /**
     * This method is called when the Remove From Class button is clicked. It
     * will remove a student from the class.
     *
     * @param evt The event that the Remove From Class button is clicked.
     */
    private void removeFromClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromClassActionPerformed
        int[] index=inClass.getSelectedRows();

        User tempUser;
        for (int i=0; i<index.length; i++)
        {
            tempUser=inCourse.get(index[i]);
            enrollmentManager.removeStudentFromCourse(tempUser, course);
        }
        fillStudentModel();
        if(index.length>0)
        {
            if(index[0]<inClass.getRowCount())
                inClass.setRowSelectionInterval(index[0], index[0]);
            else if(inClass.getRowCount()>0)
                inClass.setRowSelectionInterval(inClass.getRowCount()-1, inClass.getRowCount()-1);
        }
    }//GEN-LAST:event_removeFromClassActionPerformed

    private void classComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classComboBoxActionPerformed
        course = (Course)classComboBox.getSelectedItem();
        if (course != null) {
            classNameTextField.setText(course.getClassName());
            departmentTextField.setText(String.valueOf(course.getDepartmentName()));
            courseTextField.setText(String.valueOf(course.getClassIdentifier()));
            sectionTextField.setText(String.valueOf(course.getSection()));
            instructorTextBox.setText(String.valueOf(course.getInstructor()));
            yearTextField.setText(String.valueOf(course.getYear()));
            semesterTextField.setText(String.valueOf(course.getSemester()));
            fillStudentModel();
        }
    }//GEN-LAST:event_classComboBoxActionPerformed

    /**
     * When name RadioButton is selected, the list of the classes will be sorted by the order of English Characters
     * @param evt the action event
     */
    private void nameRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameRadioButtonActionPerformed
        //Save current coursr to reset after change
        Course currentCourse = course;

        generateClassListComboBoxNameOrder();

        //Reset course
        classComboBox.setSelectedItem(currentCourse);
    }//GEN-LAST:event_nameRadioButtonActionPerformed

    /**
     * When the year and semester button is selected, the list of the classes will be sorted by the order of the year and semester
     * @param evt the action event
     */
    private void yearAndSemesterRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearAndSemesterRadioButtonActionPerformed
        //Save current coursr to reset after change
        Course currentCourse = course;

        generateClassListComboBoxTimeOrder();

         //Reset course
        classComboBox.setSelectedItem(currentCourse);
    }//GEN-LAST:event_yearAndSemesterRadioButtonActionPerformed

    private void manage_JTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_manage_JTabbedPaneStateChanged
         emailAllButton.setVisible(manage_JTabbedPane.getSelectedIndex() == 0);
         emailSelectedButton.setVisible(manage_JTabbedPane.getSelectedIndex() == 0);
    }//GEN-LAST:event_manage_JTabbedPaneStateChanged

    private void emailSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailSelectedButtonActionPerformed
        //Get indexes of users.
        int[] userIndexes = inClass.getSelectedRows();

        //checks if the user actually selected anything.
        if(userIndexes.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "You must select at least one student is this class.",
                    "No Students Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        //Store selected users in a vector.
        Vector<User> userList = new Vector<>();
        User user;

        for (int i = 0; i < userIndexes.length; i++) {
            user = inCourse.get(userIndexes[i]);
            userList.add(user);
        }

        //Send Emails
        PasswordEmailer.sendNewPasswords(userList, userManager, this);
    }//GEN-LAST:event_emailSelectedButtonActionPerformed

    private void emailAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailAllButtonActionPerformed
        //Checks if there are students in the class
        if(inClass.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "You must have at least one student is this class.",
                    "No Students in Class", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        //Store selected users in a vector.
        Vector<User> userList = new Vector<>();
        User user;

        for (int i = 0; i < inClass.getRowCount(); i++) {
            user = inCourse.get(i);
            userList.add(user);
        }

        //Send Emails
        PasswordEmailer.sendNewPasswords(userList, userManager, this);
    }//GEN-LAST:event_emailAllButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel DescriptiveLabel;
    private javax.swing.JButton addNewStudent;
    private javax.swing.JButton addStudentsFromFileButton;
    private javax.swing.JButton browseButton;
    private javax.swing.JComboBox<Course> classComboBox;
    private javax.swing.JTextField classNameTextField;
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField courseTextField;
    private javax.swing.JLabel course_JLabel;
    private javax.swing.JLabel course_Name_JLabel;
    private javax.swing.JLabel csvInstructionsLabel;
    private javax.swing.JPanel csvPanel;
    private javax.swing.JTextField departmentTextField;
    private javax.swing.JLabel department_JLabel;
    private javax.swing.JButton emailAllButton;
    private javax.swing.JButton emailSelectedButton;
    private javax.swing.JButton exampleButton1;
    private javax.swing.JButton exampleButton2;
    private javax.swing.JRadioButton fileWithHeadersRadio;
    private javax.swing.JRadioButton fileWithoutHeadersRadio;
    private javax.swing.ButtonGroup headerButtonGroup;
    private javax.swing.JTable inClass;
    private javax.swing.JScrollPane inClassPane;
    private javax.swing.JTextField instructorTextBox;
    private javax.swing.JLabel instructor_JLabel;
    private javax.swing.JLabel jLabelFilePath;
    private javax.swing.JLabel jLabelSemester;
    private javax.swing.JLabel jLabelYear;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel manage_Individual_Students_JPanel;
    private javax.swing.JTabbedPane manage_JTabbedPane;
    private javax.swing.JPanel mange_Entire_Class_JPanel;
    private javax.swing.JPanel moveStudentButtonPanel;
    private javax.swing.JButton moveToClass;
    private javax.swing.JRadioButton nameRadioButton;
    private javax.swing.JTable notInClass;
    private javax.swing.JScrollPane notInClassPane;
    private javax.swing.JButton removeAllStudentsButton;
    private javax.swing.JButton removeFromClass;
    private javax.swing.JLabel removeStudentsJLabel;
    private javax.swing.JTextField sectionTextField;
    private javax.swing.JLabel section_JLabel;
    private javax.swing.JLabel select_Course_JLabel;
    private javax.swing.JTextField semesterTextField;
    private javax.swing.JLabel sortChoiceLabel;
    private javax.swing.ButtonGroup sortingButtonGroup;
    private javax.swing.JLabel step1;
    private javax.swing.JLabel step2Label;
    private javax.swing.JLabel step3Label;
    private javax.swing.JRadioButton yearAndSemesterRadioButton;
    private javax.swing.JTextField yearTextField;
    // End of variables declaration//GEN-END:variables
}
