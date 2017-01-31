package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import weather.ApplicationControlSystem;
import weather.clientside.gradebook.GB;
import weather.clientside.gradebook.GBCourseEntry;
import weather.clientside.gradebook.GBStudentEntry;
import weather.clientside.gui.client.GradebookStudentEntryWindow;
import weather.clientside.utilities.StudentListTableModel;
import weather.clientside.utilities.TimedLoader;
import weather.common.gui.component.BUDialog;

/**
 * Displays student's grade information for a particular course taught by an
 * instructor. The course to draw students from may be selected from a drop down
 * menu.
 *
 * @author Nikita Maizet
 */
public class GradebookWindow extends BUDialog {

    private GB gradebook;
    private ArrayList<GBStudentEntry> currentStudentList;

    /**
     * Creates new gradebook form
     *
     * @param appControl ApplicationControlSystem instance
     */
    public GradebookWindow(final ApplicationControlSystem appControl) {
        super(appControl);
        this.setModal(true);
        initComponents();

        //Instantiate gradebook object with a TimedLoader.
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Gradebook";
            }

            @Override
            protected void doLoading() {
                gradebook = new GB(appControl);
                initialize();
            }
        };
        loader.execute();

        //Set size and focus
        int width = 875 + this.getInsets().left + this.getInsets().right;
        int height = 667 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        this.setTitle("Forecasting Lesson - Instructor Gradebook");
        pack();
        this.toFront();

        // final step after all content has been initialized:
        super.postInitialize(true);
    }

    /**
     * Code to set up some basic components for the form and initialize
     * variables. All actions only need be performed once.
     */
    private void initialize() {
        loadGradebookCourses();

        currentStudentList = new ArrayList<>();

        // sets up an empty table
        studentsTable.setModel(getBlankTableModel());
        studentsTable.setAutoCreateRowSorter(true);

        // set up mouse click listener to open student report on double click
        // as well as manipulate row selection focus (lose focus when click
        // on non-jtable components)
        MouseAdapter mouseClickListener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                /*
                 This block causes the studentsTable to unselect all selected
                 items when any other component other than the table in the 
                 form is clicked.
                 */
                if (e.getClickCount() == 1) {
                    if (e.getComponent() instanceof JTable) {
                        // do nothing
                    } else {
                        studentsTable.clearSelection();
                        exportSelectionsToCSVButton.setEnabled(false);
                    }
                }

                /*
                 * This block is called when the user double-clicks on a row in the
                 * table. Double clicking an entry will open a student details
                 * window.
                 */
                if (e.getClickCount() == 2) {
                    if (e.getSource() instanceof JTable) {
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow();
                        int column = target.getSelectedColumn();

                        // opens student gradebook window for selected student:
                        new GradebookStudentEntryWindow(appControl, 
                                getSelectedStudentEntry(row), false);

                        // TODO: implement this functionality
                        updateAverageScoreLabels(row, column);
                    }
                }
            }
        };

        studentsTable.addMouseListener(mouseClickListener);
        topJPanel.addMouseListener(mouseClickListener);
        bottomJPanel.addMouseListener(mouseClickListener);
        courseSelectionComboBox.addMouseListener(mouseClickListener);
        averageScoreLabel.addMouseListener(mouseClickListener);
        averageScoreValueLabel.addMouseListener(mouseClickListener);
        myCoursesLabel.addMouseListener(mouseClickListener);
        excelOptionsLabel.addMouseListener(mouseClickListener);
        studentsScrollPane.addMouseListener(mouseClickListener);

        // selection change listener for exportSelectionsToCSVButton
        studentsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                exportSelectionsToCSVButton.setEnabled(true);
            }

        });

    }

    /**
     * Loads all courses for current user from GB object and places them into
     * courseSelectionComboBox.
     */
    private void loadGradebookCourses() {
        ArrayList<GBCourseEntry> courses = gradebook.getAllCourseEntries();

        for (int i = 0; i < courses.size(); i++) {
            courseSelectionComboBox.addItem(courses.get(i));
        }
    }

    /**
     * Loads list of students in course selected from courses drop down menu.
     *
     * @param courseNum
     */
    private void loadGradebookStudents(int courseNum) {
        currentStudentList = gradebook.
                getCourseEntryByNumber(courseNum).getAllStudentEntries();

        // Gets blank table model and fills with student info
        StudentListTableModel model = getBlankTableModel();
        for (int i = 0; i < currentStudentList.size(); i++) {
            Object[] temp = {currentStudentList.get(i).getLastName(),
                currentStudentList.get(i).getFirstName(),
                currentStudentList.get(i).getUserName(),
                currentStudentList.get(i).getNumberAssignmentCompleted(),
                currentStudentList.get(i).getStudentTotalPoints(),
                currentStudentList.get(i).getStudentPercentage()};
            model.addRow(temp);
        }
        studentsTable.setModel(model);
    }

    /**
     * Returns GBStudentEntry for student selected by user. Determines student
     * by provided row number of selection.
     *
     * @param row - row number of selected student
     * @return GBStudentEntry object for selected student.
     */
    private GBStudentEntry getSelectedStudentEntry(int row) {
        return currentStudentList.get(row);
    }

    /**
     * Clears all rows in studentTable to make it empty.
     */
    private void emptyTable() {
        DefaultTableModel model = (DefaultTableModel) studentsTable.getModel();
        model.setRowCount(0);
        studentsTable.setModel(model);
    }

    private StudentListTableModel getBlankTableModel() {
        String[] columnNames = {"Last Name", "First Name", "User Name",
            "Assignments Completed", "Points", "Average (Percent)"};
        Object[][] data = {{}};

        StudentListTableModel model = new StudentListTableModel(data, columnNames);
        // row count to zero to eliminate blank first row
        model.setRowCount(0);

        return model;
    }

    /**
     * Updates averageScoreLabel and averageScoreValueLabel to display average
     * grade of the student last clicked on.
     *
     * @param row
     * @param column
     */
    private void updateAverageScoreLabels(int row, int column) {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        studentsScrollPane = new javax.swing.JScrollPane();
        studentsTable = new javax.swing.JTable();
        topJPanel = new javax.swing.JPanel();
        openStudentButton = new javax.swing.JButton();
        courseSelectionComboBox = new javax.swing.JComboBox<weather.clientside.gradebook.GBCourseEntry>();
        myCoursesLabel = new javax.swing.JLabel();
        averageScoreValueLabel = new javax.swing.JLabel();
        averageScoreLabel = new javax.swing.JLabel();
        bottomJPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        exportSelectionsToCSVButton = new javax.swing.JButton();
        exportCourseToCSVButton = new javax.swing.JButton();
        excelOptionsLabel = new javax.swing.JLabel();

        setResizable(false);

        studentsScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        studentsScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        studentsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        studentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Roberts", "Brian", "br01@bloomu.edu", "3", "301", "78.5%"},
                {"Markson", "Julia", "jm02@bloomu", "4", "371", "94.6%"},
                {"Hammer", "Mark", "md03@bloomu.edu", "0", "5", "12.1%"},
                {"...", "...", "...", "...", "...", "..."},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Last Name", "First Name", "User Name", "Assignments Completed", "Total Points Earned", "Average (Percent)"
            }
        ));
        studentsScrollPane.setViewportView(studentsTable);

        topJPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        openStudentButton.setText("Open Student");
        openStudentButton.setPreferredSize(new java.awt.Dimension(61, 23));
        openStudentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openStudentButtonActionPerformed(evt);
            }
        });

        courseSelectionComboBox.setMaximumRowCount(15);
        courseSelectionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "- Select Course -" }));
        courseSelectionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                courseSelectionComboBoxActionPerformed(evt);
            }
        });

        myCoursesLabel.setText("My Courses: ");

        averageScoreValueLabel.setText("0.0%");

        averageScoreLabel.setText("Course Average: ");
        averageScoreLabel.setToolTipText("");

        javax.swing.GroupLayout topJPanelLayout = new javax.swing.GroupLayout(topJPanel);
        topJPanel.setLayout(topJPanelLayout);
        topJPanelLayout.setHorizontalGroup(
            topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(myCoursesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(courseSelectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(averageScoreLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(averageScoreValueLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 165, Short.MAX_VALUE)
                .addComponent(openStudentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        topJPanelLayout.setVerticalGroup(
            topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(myCoursesLabel)
                        .addComponent(courseSelectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(averageScoreLabel)
                        .addComponent(averageScoreValueLabel))
                    .addComponent(openStudentButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bottomJPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bottomJPanel.setPreferredSize(new java.awt.Dimension(716, 47));

        closeButton.setText("Close");
        closeButton.setPreferredSize(new java.awt.Dimension(61, 23));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        exportSelectionsToCSVButton.setText("Export Selected Students");
        exportSelectionsToCSVButton.setEnabled(false);
        exportSelectionsToCSVButton.setPreferredSize(new java.awt.Dimension(61, 23));
        exportSelectionsToCSVButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSelectionsToCSVButtonActionPerformed(evt);
            }
        });

        exportCourseToCSVButton.setText("Export Entire Course");
        exportCourseToCSVButton.setPreferredSize(new java.awt.Dimension(61, 23));
        exportCourseToCSVButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportCourseToCSVButtonActionPerformed(evt);
            }
        });

        excelOptionsLabel.setText("Export Options:");

        javax.swing.GroupLayout bottomJPanelLayout = new javax.swing.GroupLayout(bottomJPanel);
        bottomJPanel.setLayout(bottomJPanelLayout);
        bottomJPanelLayout.setHorizontalGroup(
            bottomJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(excelOptionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exportSelectionsToCSVButton, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportCourseToCSVButton, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 252, Short.MAX_VALUE)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomJPanelLayout.setVerticalGroup(
            bottomJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomJPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(bottomJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bottomJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(exportCourseToCSVButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(exportSelectionsToCSVButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(excelOptionsLabel))
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(studentsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(topJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bottomJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 906, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(studentsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bottomJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void courseSelectionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_courseSelectionComboBoxActionPerformed
        JComboBox cb = (JComboBox) evt.getSource();

        // only attempt to load student list if selection other than first one
        // was made, first one acts as prompt.
        if (cb.getSelectedIndex() > 0) {
            GBCourseEntry selection = (GBCourseEntry) cb.getSelectedItem();
            // sets course average label
            averageScoreValueLabel.setText(selection.getCourseAveragePercentage() + "%");
            loadGradebookStudents(selection.getCourseNumber());
        } else {
            studentsTable.setModel(getBlankTableModel());
        }
    }//GEN-LAST:event_courseSelectionComboBoxActionPerformed

    private void openStudentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openStudentButtonActionPerformed
        int[] rows = studentsTable.getSelectedRows();
        if (courseSelectionComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to view student list.");
        } else {
            // clears selections in studentsTable:
            studentsTable.clearSelection();
            exportSelectionsToCSVButton.setEnabled(false);

            if (rows.length == 0) {
                JOptionPane.showMessageDialog(this, "No students selected.");
            } else if (rows.length > 1) {
                JOptionPane.showMessageDialog(this, "Cannot open multiple students.\nPlease select only one entry.");
            } else {
                new GradebookStudentEntryWindow(appControl, 
                        getSelectedStudentEntry(rows[0]), false);
            }
        }
    }//GEN-LAST:event_openStudentButtonActionPerformed

    private void exportCourseToCSVButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportCourseToCSVButtonActionPerformed
        // clears selections in studentsTable:
        studentsTable.clearSelection();
        exportSelectionsToCSVButton.setEnabled(false);

        if (courseSelectionComboBox.getSelectedIndex() < 1) {
            JOptionPane.showMessageDialog(this, "Please select a course from the drop down menu to export.");
        } else {
            GBCourseEntry courseEntry = (GBCourseEntry) courseSelectionComboBox.getSelectedItem();

            int[] selectedStudents = new int[currentStudentList.size()];
            for (int i = 0; i < currentStudentList.size(); i++) {
                selectedStudents[i] = i;
            }

            new ExportToCSVDialog(appControl, courseEntry, selectedStudents);
        }
    }//GEN-LAST:event_exportCourseToCSVButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void exportSelectionsToCSVButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSelectionsToCSVButtonActionPerformed
        int[] rows = studentsTable.getSelectedRows();
        GBCourseEntry courseEntry = (GBCourseEntry) courseSelectionComboBox.getSelectedItem();

        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "No students selected.");
        } else {
            new ExportToCSVDialog(appControl, courseEntry, rows);
        }

        // clears selections in studentsTable: 
        studentsTable.clearSelection();
        exportSelectionsToCSVButton.setEnabled(false);
    }//GEN-LAST:event_exportSelectionsToCSVButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel averageScoreLabel;
    private javax.swing.JLabel averageScoreValueLabel;
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JComboBox<weather.clientside.gradebook.GBCourseEntry> courseSelectionComboBox;
    private javax.swing.JLabel excelOptionsLabel;
    private javax.swing.JButton exportCourseToCSVButton;
    private javax.swing.JButton exportSelectionsToCSVButton;
    private javax.swing.JLabel myCoursesLabel;
    private javax.swing.JButton openStudentButton;
    private javax.swing.JScrollPane studentsScrollPane;
    private javax.swing.JTable studentsTable;
    private javax.swing.JPanel topJPanel;
    // End of variables declaration//GEN-END:variables
}
