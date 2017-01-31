package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.Course;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.gui.component.BUDialog;

/**
 * The <code>ManageClassesWindow</code> creates a form that 
 * lists all classes available.
 * Created on Feb 18, 2011, 4:20:13 PM
 * @author jjh35893
 * @author Mike Nacko (2011)
 * @author Nate Hartzler(2012)
 * 
 * @version 2012
 */
public class ManageClassesWindow extends BUDialog {
    private DBMSSystemManager dbms;
    private DBMSCourseManager courseMgr;
    private String department, classID, semester, instructor;
    private int year, section;
    private boolean isAdmin;
    
    private static int NUMBER_OF_COLUMNS = 9;
    private static int COURSE_NUMBER_COLUMN = 0;
    private static int DEPARTMENT_COLUMN = 1;
    private static int CLASS_ID_COLUMN = 2;
    private static int SECTION_COLUMN = 3;
    private static int COURSE_COLUMN = 4;
    private static int SEMESTER_COLUMN = 5;
    private static int YEAR_COLUMN = 6;
    private static int INSTRUCTOR_COLUMN = 7;
    private static int CREATION_DATE_COLUMN = 8;
    private static int DEPARTMENT_COLUMN_WIDTH = 150;
    private static int CLASS_ID_COLUMN_WIDTH = 100;
    private static int SECTION_COLUMN_WIDTH = 75;
    private static int COURSE_COLUMN_WIDTH = 200;
    private static int SEMESTER_COLUMN_WIDTH = 75;
    private static int YEAR_COLUMN_WIDTH = 50;
    private static int INSTRUCTOR_COLUMN_WIDTH = 125;
    private static int DATE_COLUMN_WIDTH = 125;

    /**
     * Creates new form OpenListClassesWindow.
     *
     * @param app Control system for gaining resources.
     * @param isAdmin True if a user is an administrator, false if they are an
     * instructor.
     * @param dept Department in university.
     * @param classID A certain ID for a specific class.
     * @param section Class courseSection number.
     * @param semester String representing the semester to search for or "Any."
     * @param year Year of a class.
     * @param instructor Class instructor.
     */
    public ManageClassesWindow(ApplicationControlSystem app, boolean isAdmin,
            String dept, String classID, int section,
            String semester, int year, String instructor) {
        super(app);
        this.dbms = appControl.getGeneralService().getDBMSSystem();
        this.isAdmin = isAdmin;
        courseMgr = dbms.getCourseManager();
        department = dept;
        this.classID = classID;
        this.section = section;
        this.semester = semester;
        this.year = year;
        this.instructor = instructor;
        initComponents();
        classTable.setRowSelectionAllowed(true);
        classTable.setAutoCreateRowSorter(true);
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.getTableHeader().setResizingAllowed(true);
        classTable.getTableHeader().setReorderingAllowed(false);
        classTable.addMouseListener(doubleClick);
        classTable.addMouseMotionListener(toolTop);
        
        //Hide unwated buttons.
        addButton.setVisible(false);
        searchButton.setVisible(false);

        selectionErrorMessage.setVisible(false);
        updateTable();
        this.setTitle("Weather Viewer - Search Class Results");

        int width = 1000 + this.getInsets().left + this.getInsets().right;
        int height = 403 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();

        super.postInitialize(false);
    }
    
    /**
     * Creates the form used for listing classes.
     * @param appControl Allows use of <code>DBMSSystem</code>
     * @param isAdmin True if the user is an administrator, 
     *                  false if they are an instructor.
     */
    public ManageClassesWindow(ApplicationControlSystem appControl, boolean isAdmin) {
        super(appControl);
        this.dbms = appControl.getGeneralService().getDBMSSystem();
        this.isAdmin = isAdmin;
        courseMgr = dbms.getCourseManager();
        
        //These settings nullify filtering.
        department = "";
        this.classID = "";
        this.section = 0;
        this.semester = "Any";
        this.year = 0;
        this.instructor = "";
        initComponents();
        
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.setRowSelectionAllowed(true);
        classTable.setAutoCreateRowSorter(true);
        classTable.getTableHeader().setResizingAllowed(true);
        classTable.getTableHeader().setReorderingAllowed(false);
        classTable.addMouseListener(doubleClick);
        classTable.addMouseMotionListener(toolTop);

        selectionErrorMessage.setVisible(false);
        updateTable();
        
        int width = 1000 + this.getInsets().left + this.getInsets().right;
        int height = 403 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        super.postInitialize(true);
    }
    
    /**
     * Method used to figure out what courses fill the criteria of a search.
     * @param index Course number to be looked at.
     * @param courses Courses available.
     * @return True if the search matched the criteria , false otherwise.
     */
    private boolean searchFilter(int index, Vector<Course> courses) {
        Course checkingCourse = courses.get(index);
        /* TODO: Figure out SQL commands that can do this job more efficiently
         */
        if (department.equals("")) {
            // Helps reduce unnecessary method calls
        } else if (!checkingCourse.getDepartmentName().equals(department)) {
            return false;
        }
        if (classID.equals("")) {
            // Helps reduce unnecessary method calls
        } else if (!checkingCourse.getClassIdentifier().equals(classID)) {
            return false;
        }
        if (section == 0) {
            // Helps reduce unnecessary method calls
        } else if (checkingCourse.getSection() != section) {
            return false;
        }
        if (!semester.equals("Any")
                && !checkingCourse.getSemester().toString().equals(semester)) {
            return false;
        }
        if (year == 0) {
            // Helps reduce unnecessary method calls
        } else if (checkingCourse.getYear() != year) {
            return false;
        }
        if (instructor.equals("")) {
            // Helps reduce unnecessary method calls
        } else {
            String first = checkingCourse.getInstructor().getFirstName();
            String last = checkingCourse.getInstructor().getLastName();
            String fullName = first + " " + last;
            if (!fullName.equals(instructor)) {
                return false;
            }
        }
        return true;

    }
    
    /**
     * Helper function to return the selected course.
     * @return The selected course.
     */
    private Course getSelectedCourse() {
        //Make sure a course is selected. 
        int rows[] = classTable.getSelectedRows();
        if (rows.length == 0) {
            selectionErrorMessage.setVisible(true);
            return null;
        }

        //Course number is the first column, represented by 0
        int index = classTable.getSelectedRow();
        int courseNumber = Integer.parseInt(classTable.getValueAt(index, 0).toString());
        return courseMgr.obtainCourse(courseNumber);
    }
    
    /**
     * Adapter to edit selected course.
     */
    MouseAdapter doubleClick = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            selectionErrorMessage.setVisible(false);
            if(e.getClickCount() == 2) {
                new AddEditClassWindow(appControl, isAdmin, getSelectedCourse());
                updateTable();
            }
        }
    };
    
    /**
     * Adapted to show tool tips.
     */
    MouseMotionAdapter toolTop = new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            selectionErrorMessage.setVisible(false);
            try {
                /* Department = 95 to 210
                 * Course 335 to 535
                 * Instructor 635 to 755
                 */
                int x = e.getX();
                if(x > 95 && x < 210) {
                    classTable.setToolTipText(classTable.getValueAt(e.getY() / classTable.getRowHeight(), 1).toString());
                } else if(x > 335 && x < 535) {
                    classTable.setToolTipText(classTable.getValueAt(e.getY() / classTable.getRowHeight(), 4).toString());
                } else if(x > 635 && x < 755) {
                    classTable.setToolTipText(classTable.getValueAt(e.getY() / classTable.getRowHeight(), 7).toString());
                } else {
                    classTable.setToolTipText(null);
                }
            } catch (IndexOutOfBoundsException ex) {
                System.err.println("Toop tip isn't working.");
            }
        }
    };
    
    /**
     * Formats a <code>JTable</code> to display a list of classes.
     * @param table The table to be formated.
     * @param numRows The number of classes to be shown.
     */
    private void formatTableForClasses(javax.swing.JTable table, int numRows){
        table.setModel(new MyDefaultTableModel(numRows, NUMBER_OF_COLUMNS));
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);

        //Hide key.
        table.getColumnModel().getColumn(COURSE_NUMBER_COLUMN).setMinWidth(0);
        table.getColumnModel().getColumn(COURSE_NUMBER_COLUMN).setMaxWidth(0);
        table.getColumnModel().getColumn(COURSE_NUMBER_COLUMN).setWidth(0);

        table.getColumnModel().getColumn(DEPARTMENT_COLUMN).setHeaderValue("Department Prefix");
        table.getColumnModel().getColumn(DEPARTMENT_COLUMN).setPreferredWidth(DEPARTMENT_COLUMN_WIDTH);
        table.getColumnModel().getColumn(DEPARTMENT_COLUMN).setMinWidth(DEPARTMENT_COLUMN_WIDTH);

        table.getColumnModel().getColumn(CLASS_ID_COLUMN).setHeaderValue("Course Number");
        table.getColumnModel().getColumn(CLASS_ID_COLUMN).setPreferredWidth(CLASS_ID_COLUMN_WIDTH);
        table.getColumnModel().getColumn(CLASS_ID_COLUMN).setMinWidth(CLASS_ID_COLUMN_WIDTH);

        table.getColumnModel().getColumn(SECTION_COLUMN).setHeaderValue("Section");
        table.getColumnModel().getColumn(SECTION_COLUMN).setPreferredWidth(SECTION_COLUMN_WIDTH);
        table.getColumnModel().getColumn(SECTION_COLUMN).setMinWidth(SECTION_COLUMN_WIDTH);

        table.getColumnModel().getColumn(COURSE_COLUMN).setHeaderValue("Course Name");
        table.getColumnModel().getColumn(COURSE_COLUMN).setPreferredWidth(COURSE_COLUMN_WIDTH);
        table.getColumnModel().getColumn(COURSE_COLUMN).setMinWidth(COURSE_COLUMN_WIDTH);

        table.getColumnModel().getColumn(SEMESTER_COLUMN).setHeaderValue("Semester");
        table.getColumnModel().getColumn(SEMESTER_COLUMN).setPreferredWidth(SEMESTER_COLUMN_WIDTH);
        table.getColumnModel().getColumn(SEMESTER_COLUMN).setMinWidth(SEMESTER_COLUMN_WIDTH);

        table.getColumnModel().getColumn(YEAR_COLUMN).setHeaderValue("Year");
        table.getColumnModel().getColumn(YEAR_COLUMN).setPreferredWidth(YEAR_COLUMN_WIDTH);
        table.getColumnModel().getColumn(YEAR_COLUMN).setMinWidth(YEAR_COLUMN_WIDTH);

        table.getColumnModel().getColumn(INSTRUCTOR_COLUMN).setHeaderValue("Instructor");
        table.getColumnModel().getColumn(INSTRUCTOR_COLUMN).setPreferredWidth(INSTRUCTOR_COLUMN_WIDTH);
        table.getColumnModel().getColumn(INSTRUCTOR_COLUMN).setMinWidth(INSTRUCTOR_COLUMN_WIDTH);

        table.getColumnModel().getColumn(CREATION_DATE_COLUMN).setHeaderValue("Creation Date");
        table.getColumnModel().getColumn(CREATION_DATE_COLUMN).setPreferredWidth(DATE_COLUMN_WIDTH);
        table.getColumnModel().getColumn(CREATION_DATE_COLUMN).setMinWidth(DATE_COLUMN_WIDTH);
    }

    /**
     * Updates the table listing the classes.
     */
    private void updateTable() {
        Vector<Course> courses;
        Vector<Course> coursesSearchMatch = new Vector<>();

        if (isAdmin) {
            courses = courseMgr.obtainAllCourses();
        } else {
            courses = courseMgr.obtainAllCoursesTaughyByUser(appControl.getGeneralService().getUser());
        }
        
        
        //Do filtering (has no effect on unfiltered version of window.)
        for (int i = 0; i < courses.size(); i++) {
            if (searchFilter(i, courses)) {
                coursesSearchMatch.add(courses.get(i));
            }
        }

        if (coursesSearchMatch.size() > 0) {
            //Format table
            formatTableForClasses(classTable, coursesSearchMatch.size());

            for (int i = 0; i < coursesSearchMatch.size(); i++) {
                classTable.setValueAt(coursesSearchMatch.get(i).getCourseNumber(), i, COURSE_NUMBER_COLUMN);
                classTable.setValueAt(coursesSearchMatch.get(i).getDepartmentName(), i, DEPARTMENT_COLUMN);
                classTable.setValueAt(coursesSearchMatch.get(i).getClassIdentifier(), i, CLASS_ID_COLUMN);
                classTable.setValueAt(coursesSearchMatch.get(i).getSection(), i, SECTION_COLUMN);
                classTable.setValueAt(coursesSearchMatch.get(i).getClassName(), i, COURSE_COLUMN);
                classTable.setValueAt(coursesSearchMatch.get(i).getSemester(), i, SEMESTER_COLUMN);
                classTable.setValueAt(coursesSearchMatch.get(i).getYear(), i, YEAR_COLUMN);
                classTable.setValueAt(coursesSearchMatch.get(i).getInstructor().getFirstName() + " " + coursesSearchMatch.get(i).getInstructor().getLastName(), i, INSTRUCTOR_COLUMN);
                classTable.setValueAt(coursesSearchMatch.get(i).getCreationDateInPrettyFormat(), i, CREATION_DATE_COLUMN);
            }
        }
        classTable.revalidate();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closejButton = new javax.swing.JButton();
        doubleClickLabel = new javax.swing.JLabel();
        selectionErrorMessage = new javax.swing.JLabel();
        classScrollPane = new javax.swing.JScrollPane();
        classTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        studentsEnrolledButton = new javax.swing.JButton();
        manageStudentButton = new javax.swing.JButton();

        setTitle("Weather Viewer - Manage Classes");
        setMinimumSize(new java.awt.Dimension(400, 300));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        closejButton.setText("Close");
        closejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closejButtonAction(evt);
            }
        });
        getContentPane().add(closejButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(925, 339, 63, 25));

        doubleClickLabel.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        doubleClickLabel.setText("To edit a specific class, double-click on its name.");
        getContentPane().add(doubleClickLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 376, -1, -1));

        selectionErrorMessage.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        selectionErrorMessage.setForeground(new java.awt.Color(255, 51, 51));
        selectionErrorMessage.setText("You must select a class to perform this action.");
        getContentPane().add(selectionErrorMessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 376, -1, -1));

        classScrollPane.setPreferredSize(new java.awt.Dimension(840, 350));

        classTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        classTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        classTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        classTable.getTableHeader().setReorderingAllowed(false);
        classScrollPane.setViewportView(classTable);

        getContentPane().add(classScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 976, 315));

        ButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        addButton.setText("Add Class");
        addButton.setToolTipText("Adds a new class to the database.");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(addButton);

        searchButton.setText("Search");
        searchButton.setToolTipText("Search for a group of classes, or a single class");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(searchButton);

        deleteButton.setText("Delete Selected Class");
        deleteButton.setToolTipText("Delete the class currently highlighted in the above table.");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(deleteButton);

        studentsEnrolledButton.setText("List Students Enrolled In Selected Class");
        studentsEnrolledButton.setToolTipText("Lists all students enrolled in the selected class");
        studentsEnrolledButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentsEnrolledButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(studentsEnrolledButton);

        manageStudentButton.setText("Add/Remove Students In Selected Class");
        manageStudentButton.setToolTipText("Add or Remove students from the selected class");
        manageStudentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageStudentButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(manageStudentButton);

        getContentPane().add(ButtonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 339, 901, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closejButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closejButtonAction
        this.dispose();
    }//GEN-LAST:event_closejButtonAction

    /**
     * Adds a class to system.
     * @param evt Event that "Add Course" button is clicked.
     */
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        new AddEditClassWindow(appControl, isAdmin);
        updateTable();
    }//GEN-LAST:event_addButtonActionPerformed

    /**
     * Searches for a course in the system.
     * @param evt Event that "Search" button is clicked.
     */
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        new SearchClassesWindow(appControl, isAdmin, this);
    }//GEN-LAST:event_searchButtonActionPerformed

    /**
     * Deletes the selected course(s) from the database.
     * @param evt Event that "Delete" button is clicked.
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        Course c = getSelectedCourse();
        if (c == null) {
            selectionErrorMessage.setVisible(true);
            return;
        }
        selectionErrorMessage.setVisible(false);

        String deleted = "";
        int choice = JOptionPane.NO_OPTION;
        String className = c.getClassName();
        int courseSection = c.getSection();

        if (JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + className + ", "
                + "section " + courseSection + "?\nThis does not delete the students "
                + "enrolled in the course.",
                "Delete Class", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            courseMgr.removeCourse(c);
            choice = JOptionPane.YES_OPTION;
            //updates the list of which classes were deleted
            deleted = deleted.concat(className + ", section " + courseSection + "\n");
        }
        if (choice == JOptionPane.YES_OPTION) {
            updateTable();
        }
        //will only show if the user deleted at least one class
        if (deleted.length() > 0) {
            JOptionPane.showMessageDialog(this, "The following courses have been deleted:\n" + deleted,
                    "Deleted Classes", JOptionPane.PLAIN_MESSAGE);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void studentsEnrolledButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentsEnrolledButtonActionPerformed
        Course c = getSelectedCourse();
        if (c == null) {
            selectionErrorMessage.setVisible(true);
            return;
        }
        selectionErrorMessage.setVisible(false);
        new ListOfStudentsInClassWindow(appControl, c);
    }//GEN-LAST:event_studentsEnrolledButtonActionPerformed

    private void manageStudentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageStudentButtonActionPerformed
        Course c = getSelectedCourse();
        if (c == null) {
            selectionErrorMessage.setVisible(true);
            return;
        }
        selectionErrorMessage.setVisible(false);
        new ManageStudentEnrollment(appControl, c, isAdmin, false);
    }//GEN-LAST:event_manageStudentButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton addButton;
    private javax.swing.JScrollPane classScrollPane;
    private javax.swing.JTable classTable;
    private javax.swing.JButton closejButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel doubleClickLabel;
    private javax.swing.JButton manageStudentButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel selectionErrorMessage;
    private javax.swing.JButton studentsEnrolledButton;
    // End of variables declaration//GEN-END:variables
}
