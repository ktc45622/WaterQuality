package weather.clientside.gui.client;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import weather.ApplicationControlSystem;
import weather.clientside.gradebook.GB;
import weather.clientside.gradebook.GBCourseEntry;
import weather.clientside.gradebook.GBForecastingAttemptEntry;
import weather.clientside.gradebook.GBForecastingLessonEntry;
import weather.clientside.gradebook.GBStudentEntry;
import weather.clientside.utilities.FGBStudentViewANode;
import weather.clientside.utilities.FGBStudentViewLNode;
import weather.clientside.utilities.ForecasterJTableCellRenderer;
import weather.clientside.utilities.ForecasterJTreeNode;
import weather.clientside.utilities.TimedLoader;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.CommonLocalFileManager;

/**
 * Displays score information about a particular student's assignments. Every
 * row represents an assignment available to the student along with the highest
 * grade received. If an assignment is double clicked it expands to show all the
 * attempts made by the student on that assignment. Functionality to export the
 * students information to CSV is also available.
 *
 * @author Nikita Maizet
 */
public class GradebookStudentEntryWindow extends BUDialog {

    private GB gb;
    private GBStudentEntry student;
    private ArrayList<GBForecastingLessonEntry> lessons;
    
    //Variable to hold parent if called form ForecasterChooseLesson; It should
    //remain null if the form is called feom elsewhere.
    private ForecasterChooseLesson lessonDialog = null;

    /**
     * Creates new student view gradebook form. This constructor is used when
     * a student user open their gradebook.
     *
     * @param appControl ApplicationControlSystem instance.
     * @param user User object used to get and display lesson data.
     * @param lessonDialog The instance of ForecasterChooseLesson that called
     * this form, or null if this form was called from elsewhere.
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    public GradebookStudentEntryWindow(final ApplicationControlSystem appControl, 
            final User user, final ForecasterChooseLesson lessonDialog, 
            boolean shouldCenter) {
        super(appControl);
        this.setModal(true);
        initComponents();
        this.lessonDialog = lessonDialog;
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Gradebook for " + user.getFirstName() + " "
                        + user.getLastName();
            }

            @Override
            protected void doLoading() {
                gb = new GB(appControl, user);
                student = null;

                initialize();

                //Hide lessonDialog if not null. (Needed because lesssonDialog is always
                //on top.)
                if (lessonDialog != null) {
                    lessonDialog.setVisible(false);
                } else {
                    //Change close button text.
                    closeButton.setText("Close");
                }
            }
        };
        loader.execute();

        //Set size and focus
        int width = 970 + this.getInsets().left + this.getInsets().right;
        int height = 650 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        this.setTitle("Instructor Gradebook - Course View");

        pack();
        this.toFront();

        super.postInitialize(shouldCenter);
    }

    /**
     * Creates new student view gradebook form. This constructor is only used
     * by GradebookWindow when loading instructor gradebook.
     *
     * @param appControl ApplicationControlSystem instance
     * @param student GBStudentEntry object used to get and display lesson data
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    public GradebookStudentEntryWindow(ApplicationControlSystem appControl,
            GBStudentEntry student, boolean shouldCenter) {
        initComponents();

        this.student = student;
        this.appControl = appControl;

        initialize();
        
        //Change close button text.
        this.closeButton.setText("Close");

        //Set size and focus
        int width = 970 + this.getInsets().left + this.getInsets().right;
        int height = 650 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        this.setTitle("Instructor Gradebook - Course View");

        pack();
        this.toFront();

        super.postInitialize(shouldCenter);
    }

    /**
     * Runs methods to populate data and modify any information when form first
     * loaded.
     */
    private void initialize() {
        // load courses for student user:
        if (appControl.getGeneralService().getUser().getUserType() == UserType.student) {
            loadStudentUserComponents();
        } else if (appControl.getGeneralService().getUser().getUserType() == UserType.guest) {
            JOptionPane.showMessageDialog(this, "Functionality not available for guest user.");
        } else {
            loadInstructorUserComponents();
        }

        if (appControl.getGeneralService().getUser().getUserType() != UserType.student) {
            populateLessonData();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadStudentUserComponents() {
        //Hide instructor control.
        exportToCSVButton.setVisible(false);
        
        optionsLabel.setText("My Courses: ");
        
        courseSelectionComboBox.setMaximumRowCount(15);

        courseSelectionComboBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                courseSelectionComboBoxActionPerformed(evt);
            }
        });

        // populate course combo box:
        ArrayList<GBCourseEntry> courses = gb.getAllCourseEntries();
        for (int i = 0; i < courses.size(); i++) {
            courseSelectionComboBox.addItem(courses.get(i));
        }

        // automatically have first course selected and corresponding info displayed
        if (courseSelectionComboBox.getItemCount() > 1) {
            courseSelectionComboBox.setSelectedIndex(1);
        }
    }

    private void loadInstructorUserComponents() {
        //Hide student control.
        courseSelectionComboBox.setVisible(false);
        
        exportToCSVButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportStudentToExcel();
            }
        });

    }

    private void exportStudentToExcel() {
        Calendar cal = Calendar.getInstance();
        String filename = student.getLastName() + "_" + student.getFirstName()
                + "_" + cal.get(Calendar.MONTH) + "."
                + cal.get(Calendar.DAY_OF_MONTH) + "."
                + cal.get(Calendar.YEAR) + ".xlsx";

        student.exportToExcel(filename);

        JOptionPane.showMessageDialog(this, "Student successfully exported to: "
                + CommonLocalFileManager.getForecastingLessonExcelFilesDirectory());
    }

    /**
     * Displays all lessons for student in selected course.
     */
    private void populateLessonData() {
        updateStudentInfoLabels();
        String[] topEntry = {"Entries:"};
        String[] headerVals = {"Assignment:", "Total Points:", "Grade(%):", "Date Submitted:", "Date of Forecast:", "Status:"};
        ForecasterJTreeNode top = new ForecasterJTreeNode(topEntry);
        ForecasterJTreeNode headerNode = new ForecasterJTreeNode(headerVals);
        // String arrays holding data for each lesson row
        ArrayList<ForecasterJTreeNode> lessonNodes = new ArrayList<>();
        // String arrays holding data for each lesson's attempt row
        ArrayList<ForecasterJTreeNode> attemptNodes = new ArrayList<>();

        lessons = student.getLessons();

        // create information arrays for nodes:
        for (GBForecastingLessonEntry l : lessons) {
            // add lesson node String array
            lessonNodes.add(new ForecasterJTreeNode(new FGBStudentViewLNode(l)));

            for (GBForecastingAttemptEntry a : l.getAttempts()) {
                attemptNodes.add(new ForecasterJTreeNode(new FGBStudentViewANode(a)));
            }
        }

        // add column values nodes:
        top.add(headerNode);

        // add lesson nodes and corresponding attempt nodes to tree
        int masterCount = 0;
        for (int i = 0; i < lessonNodes.size(); i++) {
            top.add(lessonNodes.get(i));

            for (int j = 0; j < lessons.get(i).getAttempts().size(); j++) {
                lessonNodes.get(i).add(attemptNodes.get(j + masterCount));
            }

            masterCount += lessons.get(i).getAttempts().size();
        }

        DefaultTreeModel model = new DefaultTreeModel(top);

        assignmentTree.setModel(model);

        // tree node icon:
        DefaultTreeCellRenderer icons = new DefaultTreeCellRenderer();
        icons.setLeafIcon(null);
        icons.setOpenIcon(null);
        icons.setClosedIcon(null);

        ForecasterJTableCellRenderer cellRenderer = new ForecasterJTableCellRenderer();

        assignmentTree.setCellRenderer(cellRenderer);
        assignmentTree.setRootVisible(false);
        assignmentTree.putClientProperty("JTree.lineStyle", "None");

        // disable horizontal scrolling for scroll pane displaying
        assignmentScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
    }

    private void updateStudentInfoLabels() {
        studentLastNameLabel.setText(student.getLastName() + ",");
        studentFirstNameLabel.setText(student.getFirstName());
        studentUserNameLabel.setText(student.getUserName() + " / "
                + student.getEmail());

        //set student grade label
        gradePercentageLabel.setText(student.getStudentTotalPoints() + " / "
                + student.getStudentPercentage() + "%");
    }

    /**
     * Gets index of highlighted selection in assignmentTreee if one exists. If
     * no selection made coordinates will be set to negative ints.
     *
     * @return array of two integers, first specifying assignment row, second
     * specifying attempt row for that assignment (if one was selected.) If
     * either value was not selected value will default to -1;
     */
    private int[] getSelectedNode() {
        int[] selectionPath = {-1, -1};
        // get path of selected node
        ForecasterJTreeNode pathNode
                = (ForecasterJTreeNode) assignmentTree.getLastSelectedPathComponent();

        if (pathNode == null) // means nothing selected yet
        {
            return selectionPath;
        }

        ForecasterJTreeNode root = (ForecasterJTreeNode) assignmentTree.getModel().getRoot();

        ForecasterJTreeNode assignmentNode;
        ForecasterJTreeNode attemptNode;

        Object object = pathNode.getUserObject();

        // search all lesson nodes
        if (object instanceof FGBStudentViewLNode) {
            FGBStudentViewLNode node = (FGBStudentViewLNode) object;
            // get lesson id currently selected
            String lessonID = ((GBForecastingLessonEntry) node.getSourceObject()).getEntryID();

            // for every lesson node entry
            for (int i = 1; i < root.getChildCount(); i++) {
                assignmentNode = (ForecasterJTreeNode) root.getChildAt(i);
                Object object2 = assignmentNode.getUserObject();
                FGBStudentViewLNode node2 = (FGBStudentViewLNode) object2;

                if (((GBForecastingLessonEntry) node2.getSourceObject()).getEntryID().equals(lessonID)) {
                    selectionPath[0] = i;
                }
            }
        }

        // search all lesson attempt nodes
        if (object instanceof FGBStudentViewANode) {

            FGBStudentViewANode node = (FGBStudentViewANode) object;
            // get attempt id currently selected
            String attemptID = ((GBForecastingAttemptEntry) node.getSourceObject()).getAttemptID();

            // for every lesson node entry
            for (int i = 1; i < root.getChildCount(); i++) {
                assignmentNode = (ForecasterJTreeNode) root.getChildAt(i);

                // check every attempt 
                for (int j = 0; j < assignmentNode.getChildCount(); j++) {
                    attemptNode = (ForecasterJTreeNode) assignmentNode.getChildAt(j);
                    Object object2 = attemptNode.getUserObject();
                    FGBStudentViewANode node2 = (FGBStudentViewANode) object2;

                    if (((GBForecastingAttemptEntry) node2.getSourceObject()).getAttemptID().equals(attemptID)) {
                        selectionPath[0] = i;
                        selectionPath[1] = j;
                    }
                }
            }
        }

        return selectionPath;
    }

    private void courseSelectionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        JComboBox cb = (JComboBox) evt.getSource();

        // load lessons for current user pertaining to selected course
        if (cb.getSelectedIndex() > 0) {
            GBCourseEntry selection = (GBCourseEntry) cb.getSelectedItem();
            GBCourseEntry gbCourseSelection = selection;
            if (gbCourseSelection != null) // course should have only one student with lessons
            {
                this.student = gbCourseSelection.getAllStudentEntries().get(0);
            }
            populateLessonData();
        }
    }
    
    /**
     * Function to close form.
     */
    private void close() {
        dispose();
        if (lessonDialog != null) {
            lessonDialog.setVisible(true);
        } 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        assignmentScrollPane = new javax.swing.JScrollPane();
        assignmentTree = new javax.swing.JTree();
        topJPanel = new javax.swing.JPanel();
        gradePercentageLabel = new javax.swing.JLabel();
        gradeTextLabel = new javax.swing.JLabel();
        studentFirstNameLabel = new javax.swing.JLabel();
        studentLastNameLabel = new javax.swing.JLabel();
        studentUserNameLabel = new javax.swing.JLabel();
        openAssignmentDetailsButton = new javax.swing.JButton();
        bottomJPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        optionsLabel = new javax.swing.JLabel();
        courseSelectionComboBox = new javax.swing.JComboBox();
        exportToCSVButton = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        assignmentScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        assignmentScrollPane.setViewportView(assignmentTree);

        getContentPane().add(assignmentScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 79, 937, 478));

        topJPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        gradePercentageLabel.setText("0 / 0.0%");

        gradeTextLabel.setText("Grade:");

        studentFirstNameLabel.setText("Julia:");

        studentLastNameLabel.setText("Markson,");

        studentUserNameLabel.setText("jm02@bloomu.edu");

        openAssignmentDetailsButton.setText("Open Assignment Details");
        openAssignmentDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAssignmentDetailsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout topJPanelLayout = new javax.swing.GroupLayout(topJPanel);
        topJPanel.setLayout(topJPanelLayout);
        topJPanelLayout.setHorizontalGroup(
            topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(studentLastNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentFirstNameLabel)
                .addGap(18, 18, 18)
                .addComponent(studentUserNameLabel)
                .addGap(18, 18, 18)
                .addComponent(gradeTextLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gradePercentageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 453, Short.MAX_VALUE)
                .addComponent(openAssignmentDetailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        topJPanelLayout.setVerticalGroup(
            topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topJPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(studentLastNameLabel)
                    .addComponent(studentFirstNameLabel)
                    .addComponent(studentUserNameLabel)
                    .addComponent(gradeTextLabel)
                    .addComponent(gradePercentageLabel)
                    .addComponent(openAssignmentDetailsButton))
                .addContainerGap())
        );

        getContentPane().add(topJPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, 937, -1));

        bottomJPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bottomJPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        closeButton.setText("Return To Lessons");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        bottomJPanel.add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 5, -1, -1));

        optionsLabel.setText("Excel Options:");
        bottomJPanel.add(optionsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 9, 81, 16));

        courseSelectionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "- Select Course -" }));
        bottomJPanel.add(courseSelectionComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 5, 300, 22));

        exportToCSVButton.setText("Export Student");
        bottomJPanel.add(exportToCSVButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 5, 117, 25));

        getContentPane().add(bottomJPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 570, 937, 35));
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        close();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void openAssignmentDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAssignmentDetailsButtonActionPerformed
        int[] selection = getSelectedNode();

        if (selection[0] == -1 || selection[1] == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select an attempt to open.",
                    "No Attempt Selected", JOptionPane.INFORMATION_MESSAGE);
        } else {
            new AssignmentGradeReport(appControl, student, getSelectedNode());
        }

    }//GEN-LAST:event_openAssignmentDetailsButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.requestFocus();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane assignmentScrollPane;
    private javax.swing.JTree assignmentTree;
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JComboBox courseSelectionComboBox;
    private javax.swing.JButton exportToCSVButton;
    private javax.swing.JLabel gradePercentageLabel;
    private javax.swing.JLabel gradeTextLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton openAssignmentDetailsButton;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.JLabel studentFirstNameLabel;
    private javax.swing.JLabel studentLastNameLabel;
    private javax.swing.JLabel studentUserNameLabel;
    private javax.swing.JPanel topJPanel;
    // End of variables declaration//GEN-END:variables

}
