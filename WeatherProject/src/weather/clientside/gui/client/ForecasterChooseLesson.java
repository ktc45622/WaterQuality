package weather.clientside.gui.client;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.YEAR;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JEditorPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.tree.DefaultTreeModel;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.BarebonesBrowser;
import weather.clientside.utilities.ForecasterJTableCellRenderer;
import weather.clientside.utilities.ForecasterJTreeNode;
import weather.common.data.Course;
import weather.common.data.UserType;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.dbms.DBMSForecasterAttemptManager;
import weather.common.dbms.DBMSForecasterLessonManager;
import weather.common.gui.component.BUJFrame;
import weather.common.utilities.Debug;

/**
 * This is part of a series of 4 windows coded to act as one. The sequence is
 * as follows:
 * 1.  ForecasterChooseLesson 
 * 2.  ForecasterInstructions 
 * 3.  ForecasterLessonWindow
 * 4.  ForecasterOverview
 * 
 * A fifth class is used to provide the above four with easier access to
 * <code>JOptionPane</code>. It is named 
 * <code>ForecasterJOptionPaneFactory</code>.
 * 
 * This window was created for the user to select a lesson from a list sorted by
 * the courses they were enrolled in. It is called from the Weather Viewer menu
 * bar and it calls either a gradebook window or the instructions window for the
 * user. Note that the lesson and any attempt made for the day is passed on to
 * the next window.
 * 
 * For rendering of the dynamic list see <code>ForecasterJTableCellRenderer<code/>
 *  and <code>ForecasterJTreeNode<code/>.
 * 
 * @author Joshua Whiteman
 */
public class ForecasterChooseLesson extends BUJFrame {

    private final DBMSForecasterLessonManager lessonManager;
    private final ArrayList<Course> courses;
    private ForecasterLesson selectedLesson;
    
    //This reference to the program's application control system must be added
    //because this is a BUJFrame and not a BUDialog.
    private final ApplicationControlSystem appControl;
    
    //Static array to keep track of lessons that are being taken, so thay can't 
    //be taken twice.  (Tracks with lesson id's.)
    private static ArrayList<String> lessonIdsBeingTaken = new ArrayList<>();
    
    //Tracks the number instances of ForecasterChooseLesson that are open.
    private static int numWindows = 0;
    
    /**
     * Must be overridden to track the number instances of
     * ForecasterChooseLesson that are open.
     */
    @Override
    public void dispose() {
        numWindows--;
        super.dispose();
    }
    
    /**
     * Removes a lesson id from the list of lessons being taken.
     * 
     * @param id the id to remove
     */
    public void markLessonAsNotBeingTaken(String id) {
        if (lessonIdsBeingTaken.remove(id)) {
            Debug.println("Lesson removed from taken list");
        }
    }

    /**
     * Static method to get whether or not any instances of 
     * <code>ForecasterChooseLesson</code> are open.  They may be invisible 
     * behind other forms that are part of the forecaster lesson sequence.
     * 
     * @return True if any instances of <code>ForecasterChooseLesson</code> are
     * open; False otherwise.
     */
    public static boolean areInstancesOpen() {
        return numWindows > 0;
    }
    
    /**
     * Creates new dialog box that allows the student to choose a lesson.
     *
     * @param appControl used to access all common data
     * @param courses the courses from which the user can choose a lesson
     */
    public ForecasterChooseLesson(ApplicationControlSystem appControl,
            ArrayList<Course> courses) {
        super();
        this.setTitle("Weather Viewer - Daily Weather Forecasting Lesson - "
                + "Introduction");

        this.appControl = appControl;
        lessonManager = appControl.getDBMSSystem().getForecasterLessonManager();
        this.courses = courses;
        initComponents();
        initialize();
        
        // Center the window
        super.postInitialize(true);
        
        //uesd to track the number instances of ForecasterChooseLesson that are 
        //open.
        numWindows++;
    }

    private void initialize() {
        createTreeNodes();
        openLessonListener();
        openLinkListener();
        
        selectedLesson = null;
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

        chooseLessonTree.setModel(new DefaultTreeModel(topNode));

        ForecasterJTableCellRenderer cellRenderer = new ForecasterJTableCellRenderer();

        chooseLessonTree.setCellRenderer(cellRenderer);
        chooseLessonTree.setRootVisible(false);
        chooseLessonTree.putClientProperty("JTree.lineStyle", "None");

    }

    /**
     * @param courseNode the course node that will be populated with lessons
     * @return returns the course node that has been populated with lessons
     */
    private ForecasterJTreeNode loadCourseLessons(ForecasterJTreeNode courseNode) {
        ArrayList<ForecasterLesson> lessons;
        lessons = lessonManager.getForecasterLessonsByCourse(
                ((Course) courseNode.getUserObject()).getCourseNumber());

        Iterator<ForecasterLesson> i = lessons.iterator();
        while (i.hasNext()) {
            //Exclude if lesson is not open.
            ForecasterLesson lesson = i.next();
            if(lesson.getLessonStartDate().getTime() > Calendar.getInstance().getTimeInMillis()) {
                continue;
            }
            
            ForecasterJTreeNode lessonNode = new ForecasterJTreeNode(lesson);
            courseNode.add(lessonNode);
        }

        return courseNode;
    }
    
    /**
     * A function to make this object available to the inner class.
     * @return This object as a <code>Component</code>.
     */
    private Component getThisWindow() {
        return this;
    }

    /**
     * Checks for a mouse click within the tree and handles appropriately.
     */
    @SuppressWarnings("unchecked")
    private void openLessonListener() {
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
                    JTree targetTree = (JTree) e.getSource();

                    // The node that was selected
                    ForecasterJTreeNode targetNode
                            = (ForecasterJTreeNode) targetTree.getLastSelectedPathComponent();

                    // Make sure a node was selected.
                    if (targetNode == null) {
                        return;
                    }
                    
                    // The object/class belonging to that Node (Course, Lesson)
                    Object object = targetNode.getUserObject();

                    // Changes the selection in the table
                    if (object instanceof ForecasterLesson) {
                        selectedLesson = (ForecasterLesson) object;
                        nextButton.setEnabled(true);
                    }

                    // Changes the selection in the table
                    if (object instanceof Course) {
                        selectedLesson = null;
                        nextButton.setEnabled(false);
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
                        nextButton.setEnabled(true);
                        openLesson();
                    }
                }
            }
        };

        chooseLessonTree.addMouseListener(openLessonClick);
    }

    private void openLinkListener() {
        jTextPane1.addHyperlinkListener(new HyperlinkListener() {
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

        bodyPanel = new javax.swing.JPanel();
        choseLessonScrollPane = new javax.swing.JScrollPane();
        chooseLessonTree = new javax.swing.JTree();
        footerPanel = new javax.swing.JPanel();
        nextButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        headerButton = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        menuBar = new javax.swing.JMenuBar();
        openMenu = new javax.swing.JMenu();
        gradebookItem = new javax.swing.JMenuItem();
        instructionsItem = new javax.swing.JMenuItem();
        Overview = new javax.swing.JMenuItem();
        linksMenu = new javax.swing.JMenu();
        testMenuItem = new javax.swing.JMenuItem();
        glossaryMenuItem = new javax.swing.JMenuItem();

        setResizable(false);

        bodyPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        choseLessonScrollPane.setHorizontalScrollBar(null);

        chooseLessonTree.setRootVisible(false);
        choseLessonScrollPane.setViewportView(chooseLessonTree);

        javax.swing.GroupLayout bodyPanelLayout = new javax.swing.GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
            bodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(choseLessonScrollPane)
                .addContainerGap())
        );
        bodyPanelLayout.setVerticalGroup(
            bodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(choseLessonScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addContainerGap())
        );

        footerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        nextButton.setText("Next");
        nextButton.setEnabled(false);
        nextButton.setPreferredSize(new java.awt.Dimension(85, 23));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.setPreferredSize(new java.awt.Dimension(85, 23));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        backButton.setText("Back");
        backButton.setEnabled(false);
        backButton.setPreferredSize(new java.awt.Dimension(85, 23));

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(148, 148, 148)
                .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        headerButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Daily Weather Forecasting Lesson");

        javax.swing.GroupLayout headerButtonLayout = new javax.swing.GroupLayout(headerButton);
        headerButton.setLayout(headerButtonLayout);
        headerButtonLayout.setHorizontalGroup(
            headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerButtonLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(jLabel2)
                .addContainerGap(135, Short.MAX_VALUE))
        );
        headerButtonLayout.setVerticalGroup(
            headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextPane1.setEditable(false);
        jTextPane1.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        jTextPane1.setText("Welcome to the daily weather forecasting lesson. For this lesson you will forecast daily changes in temperature, wind speed and direction, fronts, cloud conditions and precipitation using the data available in the Bloomsburg Weather Viewer. The lesson is designed to introduce and reinforce introductory course concepts by engaging students in the analysis of real-time meteorological data. The lesson is modeled based on the Dynamic Weather Forecaster (DWF) program developed at Iowa State University. For additional information on the DWF, please click <a href=\"http://en.wikipedia.org/wiki/Iowa_State_University\">here</a>.\n<br><br>\n\nTo begin, double click your course listing below and select the appropriate lesson. Then, click next to start the lesson.");
        jScrollPane1.setViewportView(jTextPane1);

        menuBar.setPreferredSize(new java.awt.Dimension(72, 25));

        openMenu.setText("Resources");
        openMenu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        gradebookItem.setText("Gradebook");
        gradebookItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradebookItemActionPerformed(evt);
            }
        });
        openMenu.add(gradebookItem);

        instructionsItem.setText("Instructions");
        instructionsItem.setEnabled(false);
        openMenu.add(instructionsItem);

        Overview.setText("Overview");
        Overview.setEnabled(false);
        openMenu.add(Overview);

        menuBar.add(openMenu);

        linksMenu.setText("Links");
        linksMenu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        testMenuItem.setText("Forecasting Checklist");
        testMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testMenuItemActionPerformed(evt);
            }
        });
        linksMenu.add(testMenuItem);

        glossaryMenuItem.setText("Weather Glossary");
        glossaryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glossaryMenuItemActionPerformed(evt);
            }
        });
        linksMenu.add(glossaryMenuItem);

        menuBar.add(linksMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(footerPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bodyPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(headerButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(bodyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        openLesson();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void openLesson() {
        if (selectedLesson == null) {
            ForecasterJOptionPaneFactory.showInfoPane(
                    "You have not selected a lesson. Please select a lesson "
                            + "from the provided list.",
                    "No Lesson Selected", this);
            return;
        }
        
        if (lessonIdsBeingTaken.contains(selectedLesson.getLessonID())) {
            ForecasterJOptionPaneFactory.showInfoPane(
                    "You have already opened this lesson. Please complete this "
                            + "lesson using its open window.",
                    "Lesson Already Selected", this);
            return;
        }
        
        DBMSForecasterAttemptManager attemptManager = 
                appControl.getDBMSSystem().getForecasterAttemptManager();
        ArrayList<Attempt> attempts = attemptManager.getAttempts(
                selectedLesson, appControl.getGeneralService().getUser());
        
        Calendar endDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        
        currentDate.setTime(new Date());
        endDate.setTime(selectedLesson.getLessonEndDate());
        
        if (currentDate.after(endDate)) {
            ForecasterJOptionPaneFactory.showInfoPane(
                    "This lesson is currently inactive.",
                    "Lesson Inactive", this);
            return;
        }

        Attempt attempt = null;
        int numAttempts = 0;

        for (Attempt a : attempts) {
            if (a.getAttemptDate().get(YEAR) == Calendar.getInstance().get(YEAR) &&
                    a.getAttemptDate().get(DAY_OF_YEAR) == Calendar.getInstance().get(DAY_OF_YEAR)) {
                attempt = a;
                break;
            }
            numAttempts++;
        }
        
        /*Check if there is a forecast to edit.*/
        
        //If no questions need to be asked, we want the same effect as if user 
        //answers yes.
        boolean response = true;
        
        if (attempt != null) {
            //When using a hardcoded date, uncomment this line
            //(Also see ForecasterOverview)
//           attempt = null;
            
            //When using a hardcoded date, comment out the following statement.
            response = ForecasterJOptionPaneFactory.askUserQuestion(
                    "You have already submitted a forecast for tomorrow. "
                    + "If you continue, you may edit the choices you have"
                    + " made.\n\nAre you sure you would like to continue?",
                            "Edit Forecast", this);
        }
        
        if (numAttempts == selectedLesson.getMaximumTries()) {
            ForecasterJOptionPaneFactory.showInfoPane(
                    "You have taken the maximum number of attempts for this lesson.",
                    "Maximum Number of Attempts", this);
            return;
        }
        
        //If user can take lesson, mark lesson as being taken and open it.
        if (response) {
            lessonIdsBeingTaken.add(selectedLesson.getLessonID());
            new ForecasterInstructions(appControl, selectedLesson, attempt, this);
        }
    }
    
    
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void gradebookItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradebookItemActionPerformed
        if (appControl.getGeneralService().getUser().getUserType() 
                == UserType.administrator) {
            ForecasterJOptionPaneFactory.showInfoPane(
                    "This functionality is for student users only. "
                    + "To open instructor gradebook select \nInstructor > "
                    + "Manage Lessons > Forecasting Lesson Gradebook"
                            + " in the main program menu bar.",
                    "For Student Use", this);
        }
        else {
            new GradebookStudentEntryWindow(appControl, appControl
                    .getGeneralService().getUser(), this, false);
        }
    }//GEN-LAST:event_gradebookItemActionPerformed

    private void testMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMenuItemActionPerformed

        String url = "http://organizations.bloomu.edu/weather/viewer/ProgramWebPages/Forecasting_Checklist.htm";

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }//GEN-LAST:event_testMenuItemActionPerformed

    private void glossaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glossaryMenuItemActionPerformed
        String url = "http://organizations.bloomu.edu/weather/viewer/ProgramWebPages/Glossary/glossary.html";

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_glossaryMenuItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Overview;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel bodyPanel;
    private javax.swing.JTree chooseLessonTree;
    private javax.swing.JScrollPane choseLessonScrollPane;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JMenuItem glossaryMenuItem;
    private javax.swing.JMenuItem gradebookItem;
    private javax.swing.JPanel headerButton;
    private javax.swing.JMenuItem instructionsItem;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JMenu linksMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton nextButton;
    private javax.swing.JMenu openMenu;
    private javax.swing.JMenuItem testMenuItem;
    // End of variables declaration//GEN-END:variables

}
