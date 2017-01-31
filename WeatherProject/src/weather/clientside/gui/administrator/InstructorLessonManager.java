package weather.clientside.gui.administrator;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.tree.DefaultTreeModel;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.BarebonesBrowser;
import weather.clientside.utilities.ForecasterJTableCellRenderer;
import weather.clientside.utilities.ForecasterJTreeNode;
import weather.common.data.Course;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.DBMSForecasterLessonManager;
import weather.common.gui.component.BUDialog;


/**
 * This GUI is used when the instructor wants to edit or add a lesson for one
 * of his/her courses.  The instructor will be provided with a list of their 
 * courses that they can click on to expand and show the lessons in the course.
 * If the instructor wants to add a new lesson, they have to have the course
 * they wish to add the lesson to highlighted.  
 * To edit a current lesson, they have to expand a course and highlight the 
 * lesson and click the edit lesson button.  
 * Both choices will provide the instructor with a GUI with the editable 
 * features for lesson creation.
 * @author Brandon True
 */
public class InstructorLessonManager extends BUDialog {
    
    private final String checkListURL;
    private final String glossaryURL;
    private DBMSCourseManager courseManager;
    private DBMSForecasterLessonManager lessonManager;
    private ArrayList<Course> courses;
    private Course selectedCourse;
    private ForecasterLesson selectedLesson;
    private boolean selected = false;
    
    /**
     * Creates new form InstructorLessonManager.
     * @param appControl the program's <code>ApplicationControlSystem</code>.
     * @param initialPosition The initial screen position of this form. The form
     * will be centered on the screen with the focused window if this is null.
     */
    public InstructorLessonManager(ApplicationControlSystem appControl,
            Point initialPosition) {
        super(appControl);
        this.setModal(true);
        this.setTitle("Weather Viewer - Forecasting Lesson Manager");
        checkListURL = "http://organizations.bloomu.edu"
                + "/weather/viewer/ProgramWebPages/Forecasting_Checklist.htm";
        glossaryURL = "http://organizations.bloomu.edu"
                + "/weather/viewer/ProgramWebPages/Glossary/glossary.html";
        courseManager = appControl.getDBMSSystem().getCourseManager();
        lessonManager = appControl.getDBMSSystem().getForecasterLessonManager();
        courses = new ArrayList<Course>(courseManager.obtainAllCoursesTaughyByUser(
            appControl.getGeneralService().getUser()));
        
        initComponents();      
        initialize(appControl);
        editLessonButton.setEnabled(false);
        editResponsesButton.setEnabled(false);
        deleteLessonButton.setEnabled(false);
        
        //Center on screen if not given an initial position.
        if (initialPosition == null){
            super.postInitialize(true);
        } else {
            this.setLocation(initialPosition);
            this.setVisible(true);
        }
    }
    
    private void initialize(final ApplicationControlSystem appControl){        
        createTreeNodes();
        openLessonListener();
        openLinkListener();
    }
    
    
    /**
     * Creates the tree of Courses and Lessons within the GUI.
     * 
     * Entering this data in looks sloppy, because all of the data is being hard
     * coded in at this point. It won't look like this once it is being pulled
     * from the database.
     */
    private void createTreeNodes() {
        ForecasterJTreeNode topNode
                = new ForecasterJTreeNode();

        Iterator<Course> i = courses.iterator();

        while (i.hasNext()) {
            // The course for this iteration
            Course course = i.next();

            // The course node for this iteration
            ForecasterJTreeNode courseNode = new ForecasterJTreeNode(course);

            //To be used when lessons are loaded against courses
            courseNode = loadCourseLessons(courseNode);
            // Add this course to the top node
            topNode.add(courseNode);
        }
        
        coursesTree.setModel(new DefaultTreeModel(topNode));
        
        // Render the tree cells using a defined cell renderer
        // Not need on the chooseLesson window. Not sure why it needs this here.
        ForecasterJTableCellRenderer cellRenderer 
                = new ForecasterJTableCellRenderer();
        
        coursesTree.setCellRenderer(cellRenderer);        
        coursesTree.setRootVisible(false);
        coursesTree.putClientProperty("JTree.lineStyle", "None");        
        
    }
    
    /**
     *
     * @param courseNode
     * @return A JTreeNode providing the lessons for a particular course.
     */
    private ForecasterJTreeNode loadCourseLessons(ForecasterJTreeNode courseNode) {
        ArrayList<ForecasterLesson> lessons;
        lessons = lessonManager.getForecasterLessonsByCourse(
                ((Course) courseNode.getUserObject()).getCourseNumber());

        Iterator<ForecasterLesson> i = lessons.iterator();
        while (i.hasNext()) {
            ForecasterJTreeNode lessonNode = new ForecasterJTreeNode(i.next());
            courseNode.add(lessonNode);
        }

        return courseNode;
    }
    
    
    /**
     * Checks for a mouse click within the tree and handles appropriately.
     */
    @SuppressWarnings("unchecked")
    private void openLessonListener(){
        MouseAdapter openLessonClick = new MouseAdapter() {
            /**
             * This method is called when the user double-clicks on a row within
             * the tree. If the node selected contains a Lesson object, it is
             * opened. Otherwise, nothing happens.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                
                /**
                 * Used for the selection of the cells within the tree 
                 * structure. Only one object may be selected at a time, so the
                 * other will be set to null if. The bulk of this code is class
                 * checking and casting types.
                 */
                if (e.getClickCount() == 1) {
                    // The tree that was selected
                    JTree targetTree = (JTree)e.getSource();
                    
                    // The node that was selected
                    ForecasterJTreeNode targetNode = 
                            (ForecasterJTreeNode)
                            targetTree.getLastSelectedPathComponent();
                    
                    // Make sure a node was selected.
                    if (targetNode == null) {
                        return;
                    }
                    
                    // The object/class belonging to that Node (Course, Lesson)
                    Object object = targetNode.getUserObject();
                    
                    // Changes the selection in the table
                    if (object instanceof ForecasterLesson) {
                        selectedLesson = (ForecasterLesson) object;
                        selectedCourse = selectedLesson.getCourse();
                        editLessonButton.setEnabled(true);
                        editResponsesButton.setEnabled(true);
                        deleteLessonButton.setEnabled(true);
                    }
                    
                    // Changes the selection in the table
                    if (object instanceof Course) {
                        selectedCourse = (Course) object;
                        selectedLesson = null;
                        editLessonButton.setEnabled(false);
                        editResponsesButton.setEnabled(false);
                        deleteLessonButton.setEnabled(false);
                    }
                }
                
                
                /* The next 4 steps could be written in a line, but I extracted
                 each part for clarity */
                if (e.getClickCount() == 2) {
                    // The tree that was selected
                    JTree targetTree = (JTree) e.getSource();

                    // The node that was selected
                    ForecasterJTreeNode targetNode = (ForecasterJTreeNode) 
                            targetTree.getLastSelectedPathComponent();
                    
                    // Make sure a node was selected.
                    if (targetNode == null) {
                        return;
                    }

                    // The object/class belonging to that Node (Course, Lesson)
                    Object object = targetNode.getUserObject();

                    if (object instanceof ForecasterLesson) {
                        selectedLesson = (ForecasterLesson) object;
                        selectedCourse = selectedLesson.getCourse();
                        editLessonButton.setEnabled(true);
                        editResponsesButton.setEnabled(true);
                        deleteLessonButton.setEnabled(true);
                        openEditLesson();
                    }
                }
            }
        };
        
        coursesTree.addMouseListener(openLessonClick);
    }
    
    /**
     * A function to make this object available to the inner class.
     * @return This object as a <code>Component</code>.
     */
    private Component getThisWindow() {
        return this;
    }
    
    private void openLinkListener() {
        topTextPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    BarebonesBrowser.openURL(e.getDescription(), 
                            getThisWindow());
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instructorLessonManagerScrollPane = new javax.swing.JScrollPane();
        coursesTree = new javax.swing.JTree();
        buttonPanel = new javax.swing.JPanel();
        createNewLessonButton = new javax.swing.JButton();
        editLessonButton = new javax.swing.JButton();
        editResponsesButton = new javax.swing.JButton();
        deleteLessonButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        headerButton = new javax.swing.JPanel();
        topScrollPane = new javax.swing.JScrollPane();
        topTextPane = new javax.swing.JTextPane();
        menuBar = new javax.swing.JMenuBar();
        linksMenu1 = new javax.swing.JMenu();
        testMenuItem = new javax.swing.JMenuItem();
        glossaryMenuItem = new javax.swing.JMenuItem();

        setResizable(false);

        coursesTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                coursesTreeMouseClicked(evt);
            }
        });
        instructorLessonManagerScrollPane.setViewportView(coursesTree);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        createNewLessonButton.setText("Create New Lesson");
        createNewLessonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewLessonButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(createNewLessonButton);

        editLessonButton.setText("Edit Lesson");
        editLessonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLessonButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(editLessonButton);

        editResponsesButton.setText("Edit Responses");
        editResponsesButton.setToolTipText("Manage Responses Supplied by the Instructor");
        editResponsesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editResponsesButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(editResponsesButton);

        deleteLessonButton.setText("Delete Lesson");
        deleteLessonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteLessonButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(deleteLessonButton);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(closeButton);

        headerButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        topTextPane.setEditable(false);
        topTextPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        topTextPane.setText("Welcome to the daily weather forecasting lesson. For this lesson  your students will forecast daily changes in temperature, wind speed and direction, fronts, cloud conditions and precipitation using the data available in the Bloomsburg Weather Viewer. The lesson is designed to introduce and reinforce introductory course concepts by engaging students in the analysis of real-time meteorological data. The lesson is modeled based on the Dynamic Weather Forecaster (DWF) program developed at Iowa State University. For additional information on the DWF, please click <a href=\"http://en.wikipedia.org/wiki/Iowa_State_University\">here</a>.\n<br><br>\n\nTo begin, double click a course to choose a lesson to edit, or click create new lesson to create a new lesson.  You can also delete a lesson by clicking on the lesson and clicking the 'Delete Lesson' button.  You can alse mange any correct answers you may want to add to lesson questions by clicking the 'Edit Responses' button. ");
        topScrollPane.setViewportView(topTextPane);

        javax.swing.GroupLayout headerButtonLayout = new javax.swing.GroupLayout(headerButton);
        headerButton.setLayout(headerButtonLayout);
        headerButtonLayout.setHorizontalGroup(
            headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
            .addGroup(headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(headerButtonLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(topScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        headerButtonLayout.setVerticalGroup(
            headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
            .addGroup(headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(headerButtonLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(topScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        linksMenu1.setText("Links");
        linksMenu1.setToolTipText("Useful links that will help you make your forecast.");
        linksMenu1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        testMenuItem.setText("Forecasting Checklist");
        testMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testMenuItemActionPerformed(evt);
            }
        });
        linksMenu1.add(testMenuItem);

        glossaryMenuItem.setText("Weather Glossary");
        glossaryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glossaryMenuItemActionPerformed(evt);
            }
        });
        linksMenu1.add(glossaryMenuItem);

        menuBar.add(linksMenu1);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(instructorLessonManagerScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(headerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(instructorLessonManagerScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void coursesTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_coursesTreeMouseClicked
        this.selected = !selected;
        ForecasterJTableCellRenderer cellRenderer = new ForecasterJTableCellRenderer();
        coursesTree.setCellRenderer(cellRenderer);
    }//GEN-LAST:event_coursesTreeMouseClicked

    private void createNewLessonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewLessonButtonActionPerformed
        this.setVisible(false);
        new InstructorLessonCreator(appControl, selectedCourse, getLocation());
        this.dispose();
    }//GEN-LAST:event_createNewLessonButtonActionPerformed

    private void openEditLesson(){
        new InstructorLessonCreator(appControl, selectedLesson, getLocation());
        this.dispose();
    }
    
    private void deleteLessonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLessonButtonActionPerformed
        int response
                = JOptionPane.showConfirmDialog(this,
                        "This will delete a lesson permanently, are you sure"
                                + " you want to do this?",
                        "Exit Forecast", JOptionPane.YES_NO_OPTION);
        if (response == 0) {
            lessonManager.removeForecasterLesson(selectedLesson);
            new InstructorLessonManager(appControl, getLocation());
            dispose();
        }
    }//GEN-LAST:event_deleteLessonButtonActionPerformed

    private void editLessonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLessonButtonActionPerformed
        openEditLesson();
    }//GEN-LAST:event_editLessonButtonActionPerformed

    private void testMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMenuItemActionPerformed

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(checkListURL));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + checkListURL);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_testMenuItemActionPerformed

    private void glossaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glossaryMenuItemActionPerformed

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(glossaryURL));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + glossaryURL);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_glossaryMenuItemActionPerformed

    private void editResponsesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editResponsesButtonActionPerformed
        SelectInstructorResponseDateAndStationDialog dialog
                = new SelectInstructorResponseDateAndStationDialog(appControl, 
                selectedLesson);
        dialog.showFormOrNoDatesMessage();
    }//GEN-LAST:event_editResponsesButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JTree coursesTree;
    private javax.swing.JButton createNewLessonButton;
    private javax.swing.JButton deleteLessonButton;
    private javax.swing.JButton editLessonButton;
    private javax.swing.JButton editResponsesButton;
    private javax.swing.JMenuItem glossaryMenuItem;
    private javax.swing.JPanel headerButton;
    private javax.swing.JScrollPane instructorLessonManagerScrollPane;
    private javax.swing.JMenu linksMenu1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem testMenuItem;
    private javax.swing.JScrollPane topScrollPane;
    private javax.swing.JTextPane topTextPane;
    // End of variables declaration//GEN-END:variables

    
}
