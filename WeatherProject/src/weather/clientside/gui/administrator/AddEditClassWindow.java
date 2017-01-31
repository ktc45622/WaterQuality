package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.JSpinner.NumberEditor;
import weather.ApplicationControlSystem;
import weather.common.data.Course;
import weather.common.data.SemesterType;
import weather.common.data.User;
import weather.common.dbms.DBMSSystemManager;
import weather.common.gui.component.BUDialog;

/**
 * The <code>AddEditClassWindow</code> allows an administrator to add a new 
 * course to the database and to edit existing courses.
 * @author Bloomsburg University Software Engineering
 * @author Joe Van Lente (2010)
 * @author Andrew Bennett (2010)
 * @author Lucas Stine(2012)
 * 
 * @version Spring 2012
 */
public class AddEditClassWindow extends BUDialog {
    private DBMSSystemManager dbms;
    private final ApplicationControlSystem finalAppControl;
    private boolean isAdmin;
    
    //Stays null if we are adding a new course.
    private Course existingCourse = null;
    
    //To check for changes
    private String originalClassName;
    private String originalClassID;
    private String originalDepartment;
    private String originalSection;
    private int originalYear;
    private int originalInstructorIndex;
    private int originalSemesterIndex;

    /**
     * Creates new form AddEditClassWindow for adding a new course.
     * @param app The application control system.
     * @param isAdmin Checks to see if the user is an administrator.
     */
    public AddEditClassWindow(ApplicationControlSystem app, boolean isAdmin) {
        super(app);
        this.isAdmin = isAdmin;
        this.dbms = appControl.getDBMSSystem();
        this.finalAppControl = app;
        initComponents();
        
        //Set up non-text inputs
        populateInstructorBox();
        
        //Set up the spinner
        NumberEditor editor = new NumberEditor(spinYear, "#");
        editor.getTextField().setEditable(false);
        spinYear.setEditor(editor);
        //Populates the year box, sets the current year as selected
        spinYear.setValue(Calendar.getInstance().get(Calendar.YEAR));
        
        //Store defaut values for testing when closing.
        originalClassName = "";
        originalClassID = "";
        originalDepartment = "";
        originalSection = "";
        originalYear = Integer.parseInt(spinYear.getValue().toString());
        originalInstructorIndex = instructorBox.getSelectedIndex();
        originalSemesterIndex = semComboBox.getSelectedIndex();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                checkClose();
            }
        });
        
        int width = 444 + this.getInsets().left + this.getInsets().right;
        int height = 323 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }
    
    /**
     * Creates new form AddEditClassWindow for editing an existing course.
     * @param app The application control system.
     * @param isAdmin Checks to see if the user is an administrator.
     * @param existingCourse The course to be edited.
     */
    public AddEditClassWindow(ApplicationControlSystem app, boolean isAdmin,
            Course existingCourse) {
        super(app);
        this.isAdmin = isAdmin;
        this.dbms = appControl.getDBMSSystem();
        this.finalAppControl = app;
        this.existingCourse = existingCourse;
        initComponents();
        
        //Set control texts.
        setTitle("Weather Viewer - Edit Class");
        informTextField.setText("Please edit the following class information:");
        addEditClassButton.setText("Update");
        
        //Set up text inputs
        classNameField.setText(existingCourse.getClassName());
        classNumberField.setText(existingCourse.getClassIdentifier());
        departmentField.setText(existingCourse.getDepartmentName());
        sectionField.setText(String.valueOf(existingCourse.getSection()));
        
        //Set up non-text inputs
        populateInstructorBox();
        
        //Set semester box
        semComboBox.setSelectedItem(existingCourse.getSemester().toString());
        
        //Set up the spinner
        NumberEditor editor = new NumberEditor(spinYear, "#");
        editor.getTextField().setEditable(false);
        spinYear.setEditor(editor);
        //Populates the year box, sets the current year as selected
        spinYear.setValue(existingCourse.getYear());
        
        //Store default values for testing when closing.
        originalClassName = classNameField.getText();
        originalClassID = classNumberField.getText();
        originalDepartment = departmentField.getText();
        originalSection = sectionField.getText();
        originalYear = Integer.parseInt(spinYear.getValue().toString());
        originalInstructorIndex = instructorBox.getSelectedIndex();
        originalSemesterIndex = semComboBox.getSelectedIndex();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                checkClose();
            }
        });
        
        int width = 444 + this.getInsets().left + this.getInsets().right;
        int height = 323 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }
    
    private boolean changed(){
        if (!classNameField.getText().trim().equals(originalClassName)) {
            return true;
        }
        if (!classNumberField.getText().trim().equals(originalClassID)) {
            return true;
        }
        if (!departmentField.getText().trim().equals(originalDepartment)) {
            return true;
        }
        if (!sectionField.getText().trim().equals(originalSection)) {
            return true;
        }
        if (originalYear != Integer.parseInt(spinYear.getValue().toString())) {
            return true;
        }
        if (originalInstructorIndex != instructorBox.getSelectedIndex()) {
            return true;
        }
        if (originalSemesterIndex != semComboBox.getSelectedIndex()) {
            return true;
        }
        return false;
    }
    
    private void checkClose(){
        if(!changed()){
            dispose();
        } else if (finalAppControl.getGeneralService().leaveWithoutSaving(this) 
                == true) {
            dispose();
        }
    }

    /**
     * Populates the instructor combo box with User objects of instructor type and
     * administrator type when there is an administrator logged in, otherwise it
     * just has instructor type.
     */
    private void populateInstructorBox() {
        //Find instructor to select when form opens.
        User firstToShow;
        if (existingCourse == null) {
            //Entering a new course, so celect the current user.
            firstToShow = appControl.getGeneralService().getUser();
        } else {
            //Find instructor of existing course.
            firstToShow = existingCourse.getInstructor();
        }
        
        //Populate box and find index of first instructor to show. 
        int initialIndex = 0;
        for(User inst: dbms.getUserManager().obtainAllInstructorsAndAdministrators()){
           instructorBox.addItem(inst);
           if(inst.getUserNumber() == firstToShow.getUserNumber()){
               //correct instuctor is currently last in list
               initialIndex = instructorBox.getItemCount() - 1;
           }
        }
        
        //Find first instructor to show. 
        instructorBox.setSelectedIndex(initialIndex);
        
        //Chesk access rights.
        if(!isAdmin) {
            instructorBox.setEnabled(false);
        }  
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        departmentField = new javax.swing.JTextField();
        departmentNameLabel = new javax.swing.JLabel();
        semComboBox = new javax.swing.JComboBox();
        sectionField = new javax.swing.JTextField();
        sectionLabel = new javax.swing.JLabel();
        yearLabel = new javax.swing.JLabel();
        classNameLabel = new javax.swing.JLabel();
        semesterLabel = new javax.swing.JLabel();
        classIdentifierLabel = new javax.swing.JLabel();
        informTextField = new javax.swing.JTextField();
        classNumberField = new javax.swing.JTextField();
        addEditClassButton = new javax.swing.JButton();
        instructorBox = new javax.swing.JComboBox<User>();
        spinYear = new javax.swing.JSpinner();
        instructorLabel = new javax.swing.JLabel();
        classNameField = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Weather Viewer - Add Class");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainPanel.setFocusable(false);
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        departmentField.setToolTipText("Prefix for course when students schedule (e. g. EGGS, Math, Meteorology, METRO)");
        departmentField.setMaximumSize(new java.awt.Dimension(400, 2147483647));
        mainPanel.add(departmentField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 46, 282, -1));

        departmentNameLabel.setText("Department Prefix:");
        departmentNameLabel.setToolTipText("The department name (max 100 characters)");
        mainPanel.add(departmentNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 49, -1, -1));

        semComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Spring", "Summer", "Fall", "Winter" }));
        semComboBox.setToolTipText("The semester the class will be taught");
        mainPanel.add(semComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 193, -1, -1));

        sectionField.setToolTipText("The class section (Must be an integer from 1 to 255)");
        sectionField.setMaximumSize(new java.awt.Dimension(200, 2147483647));
        mainPanel.add(sectionField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 133, 151, -1));

        sectionLabel.setText("Section:");
        sectionLabel.setToolTipText("The class section");
        mainPanel.add(sectionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 136, -1, -1));

        yearLabel.setText("Year:");
        yearLabel.setToolTipText("The year the class is being taught");
        mainPanel.add(yearLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 228, -1, -1));

        classNameLabel.setText("Course Name:");
        classNameLabel.setToolTipText("The class name (max 100 characters)");
        mainPanel.add(classNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 107, -1, -1));

        semesterLabel.setText("Semester:");
        semesterLabel.setToolTipText("The semester the class will be taught");
        mainPanel.add(semesterLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 196, -1, -1));

        classIdentifierLabel.setText("Course Number:");
        classIdentifierLabel.setToolTipText("The integer course number (example: 480) (max 25 characters)");
        mainPanel.add(classIdentifierLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 78, -1, -1));

        informTextField.setEditable(false);
        informTextField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        informTextField.setText("Please enter the following class information:");
        informTextField.setBorder(null);
        mainPanel.add(informTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(73, 13, -1, -1));

        classNumberField.setToolTipText("The integer course number for this clss (e. g. 480)");
        classNumberField.setMaximumSize(new java.awt.Dimension(200, 2147483647));
        mainPanel.add(classNumberField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 75, 153, -1));

        addEditClassButton.setText("Add Class");
        addEditClassButton.setToolTipText("Creates the new class");
        addEditClassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEditClassButtonActionPerformed(evt);
            }
        });
        mainPanel.add(addEditClassButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 262, 113, 25));

        instructorBox.setToolTipText("The classes instructor");
        mainPanel.add(instructorBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 162, 282, -1));

        spinYear.setToolTipText("The year the class is being taught");
        spinYear.setFocusable(false);
        spinYear.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinYearStateChanged(evt);
            }
        });
        mainPanel.add(spinYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 222, -1, -1));

        instructorLabel.setText("Instructor:");
        instructorLabel.setToolTipText("The classes instructor");
        mainPanel.add(instructorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 165, -1, -1));

        classNameField.setToolTipText("The class name (max 100 characters)");
        classNameField.setMaximumSize(new java.awt.Dimension(400, 2147483647));
        mainPanel.add(classNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 104, 282, -1));

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Closes this window without saving any changes");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        mainPanel.add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(333, 262, 75, 25));

        getContentPane().add(mainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 420, 299));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Cancels the creation of the course.
     * @param evt Event that button is clicked.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        checkClose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Adds the class described in the form when the add class button is clicked.
     * @param evt The action event generated by the add button.
     */
    private void addEditClassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEditClassButtonActionPerformed
       Course thisCourse = this.getNewCourse();
       
       //If condition checks for ptoper input.
        if (thisCourse != null) {
            //if condition check for edit mode
            if(existingCourse != null){
                //Update existing course
                dbms.getCourseManager().updateCourse(existingCourse);
                JOptionPane.showMessageDialog(this,
                        "Class has been updated successfully.",
                        "Weather Viewer", JOptionPane.INFORMATION_MESSAGE);
            }else{
                //Add new course
                dbms.getCourseManager().insertCourse(thisCourse);
                JOptionPane.showMessageDialog(this,
                        "Class has been added successfully.",
                        "Weather Viewer", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        }
    }//GEN-LAST:event_addEditClassButtonActionPerformed

    private boolean isPositiveInteger(String possiblePosInt){
        try {
            int integer = Integer.parseInt(possiblePosInt);
            if (integer < 1) {
                throw new Exception();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Creates a new course with the information provided in the window.
     * Validates the fields as well.  The new course is also the existing
     * course in edit mode provided the input is valid.
     *
     * @return The new course.
     */
    private Course getNewCourse() {
        Course tempCourse = null;
        String errors = "";
        int section;    //To hold section after parsing
        section = 0;    //Compiler thinks this line is neccessary, but the
                        //algorithym doesn't need it.

        //Get data to build course.
        String className = classNameField.getText();
        String department = departmentField.getText().trim();
        String classNumber = classNumberField.getText().trim();
        String sectionAsString = sectionField.getText().trim();
        User instructor = (User) instructorBox.getSelectedItem();
        SemesterType semester = SemesterType.valueOf(semComboBox.
                getSelectedItem().toString());
        int year = Integer.parseInt(spinYear.getValue().toString());
        
        //Verify input
        if (department.isEmpty()) {
            errors += "The department prefix must be entered.\n";
        }
        if (department.length() > 100) {
            errors += "The department prefix must be no more than 100 characters.\n";
        }
        
        if (classNumber.isEmpty()) {
            errors += "The course number must be entered.\n";
        } else if(!isPositiveInteger(classNumber)){
            errors += "The course number must be a positive integer.\n";
        }
        
        if (className.isEmpty()) {
            errors += "The course name must be entered.\n";
        }
        if (className.length() > 100) {
            errors += "The course name must be no more than 100 characters.\n";
        }

        if (sectionAsString.isEmpty()) {
            errors += "The secion must be entered.\n";
        } else {
            try {
                section = Integer.parseInt(sectionAsString);
                if (section < 1 || section > 255) {
                    errors += "The section must be an integer between 1 and 255.";
                }
            } catch (Exception ex) {
                errors += "The section must be an integer between 1 and 255.";
            }
        }

        //Either show error or save course.
        if(!errors.equals("")){
            errors = "The following errors must be corrected:\n" + errors.trim();
            JOptionPane.showMessageDialog(this, errors,
                    "Unable To Save Course", JOptionPane.ERROR_MESSAGE);
        } else {
            //if condition check for edit mode
            if(existingCourse != null){
                //Update existing course
                tempCourse = existingCourse;
            } else {
                tempCourse = new Course();
            }
            tempCourse.setClassName(className);
            tempCourse.setDepartmentName(department);
            tempCourse.setClassIdentifier(classNumber);
            tempCourse.setSection(section);
            tempCourse.setInstructor(instructor);
            tempCourse.setSemester(semester);
            tempCourse.setYear(year);
        }

        return tempCourse;
    }
    
    private void spinYearStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinYearStateChanged
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        int value = Integer.parseInt(spinYear.getValue().toString());

        // Bound by -2 years and +10 years
        if(value < curYear - 2) {
            spinYear.setValue(curYear - 2);
        }
        if(value > curYear + 3) {
            spinYear.setValue(curYear + 3);
        }
    }//GEN-LAST:event_spinYearStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEditClassButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel classIdentifierLabel;
    private javax.swing.JTextField classNameField;
    private javax.swing.JLabel classNameLabel;
    private javax.swing.JTextField classNumberField;
    private javax.swing.JTextField departmentField;
    private javax.swing.JLabel departmentNameLabel;
    private javax.swing.JTextField informTextField;
    private javax.swing.JComboBox<User> instructorBox;
    private javax.swing.JLabel instructorLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField sectionField;
    private javax.swing.JLabel sectionLabel;
    private javax.swing.JComboBox semComboBox;
    private javax.swing.JLabel semesterLabel;
    private javax.swing.JSpinner spinYear;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables

}
