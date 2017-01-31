package weather.clientside.gui.client;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import weather.ApplicationControlSystem;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.gui.component.BUJFrame;


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
 * A frame that displays the instructions as specified by the instructor during
 * the creation of a ForecasterLesson. This frame must be displayed before
 * starting a new <code>Attempt<code/> on the <code>ForecasterLesson<code/>. All
 * lesson and attempt data is passed onto the main <code>ForecasterLessonWindow
 * <code/>
 * 
 * @author Joshua Whiteman
 */
public class ForecasterInstructions extends BUJFrame {

    private final ForecasterLesson lesson;
    private final ForecasterChooseLesson chooseWindow;
    
    //This reference to the program's application control system must be added
    //because this is a BUJFrame and not a BUDialog.
    private final ApplicationControlSystem appControl;
    
    //If this from is called from the lesson or overview forms, we must get
    //thier handles.
     private ForecasterLessonWindow questionFrame = null;
     private ForecasterOverview overviewFrame = null;
    
    private final Attempt attempt;


    /**
     * Creates new frame for ForecasterInstructions. Currently the instructions
     * are just stored as a String, but the may be stored as an HTML if time
     * permits.
     * 
     * @param appControl all the data needed from the database and main app.
     * @param lesson the ForecasterLesson object that contains the text for the
     * instructions.
     * @param attempt answers that will be loaded if a student is editing a
     *  forecast they have made for the day.
     * @param chooseWindow the window that called this one.
     */
    public ForecasterInstructions(ApplicationControlSystem appControl, 
            ForecasterLesson lesson, Attempt attempt, 
            ForecasterChooseLesson chooseWindow) {
        super();
        this.appControl = appControl;
        this.lesson = lesson;
        this.chooseWindow = chooseWindow;
        this.attempt = attempt;
        
        initComponents();
        initialize();
        this.chooseWindow.setVisible(false);
        
        //Keep in same spot as parent
        this.setLocation(chooseWindow.getLocation().x, chooseWindow.getLocation().y);
        this.setVisible(true);
    }
    
    /**
     * Used to show this form over the instance of <code>ForecasterLessonWindow</code>
     * that it creates.
     * @param questionFrame The <code>ForecasterLessonWindow</code> which must 
     * be passed back.
     */
    public void showOverLessonWindow(ForecasterLessonWindow questionFrame) {
        this.questionFrame = questionFrame;
        this.overviewFrame = null;
        this.setLocation(questionFrame.getLocation().x, questionFrame.getLocation().y);
        questionFrame.setVisible(false);
        
        //Change buttons
        backButton.setEnabled(false);
        nextButton.setText("Return");
        
        this.setVisible(true);
    }
    
    /**
     * Used to show this form over the instance of <code>ForecasterOverview</code>
     * that is created by the instance of <code>ForecasterLessonWindow</code>
     * that this form creates.
     * @param overviewFrame The <code>ForecasterOverview</code> which must 
     * be passed back.
     * @param questionFrame The <code>ForecasterLessonWindow</code> which must 
     * be passed back.
     */
    public void showOverLessonWindow(ForecasterOverview overviewFrame,
            ForecasterLessonWindow questionFrame) {
        this.questionFrame = questionFrame;
        this.overviewFrame = overviewFrame;
        this.setLocation(overviewFrame.getLocation().x, overviewFrame.getLocation().y);
        overviewFrame.setVisible(false);
        
        //Change buttons
        backButton.setEnabled(false);
        nextButton.setText("Ruturn");
        
        this.setVisible(true);
    }
    
    /**
     * Function to give child windows access to parent window.
     * @return The parent window of this form.
     */
    public ForecasterChooseLesson getChooseWindow(){
        return this.chooseWindow;
    }
    
    /**
     * Helper function to do initialization.
     */
    private void initialize() { 
        super.setTitle("Weather Viewer - Forecaster Lesson Instructions");
        
        instructionsTextPane.setText
            (lesson.getInstructions().getInstructionsText());
        
        courseName.setText(lesson.getCourse().getClassName());
        lessonName.setText(lesson.getLessonName());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                close();
            }
        });
    }
    
    /**
     * Helper method is called when closing to ensure parent is disposed also.
     */
    private void close() {
        //If lesson window has been created there is an unsubmitted lesson.
        if (questionFrame != null) {
            boolean response = ForecasterJOptionPaneFactory.askUserQuestion(
                    "You have not submitted your forecast. Your "
                    + "selections will not be saved.\n Are you sure"
                    + " you want to continue?",
                    "Exit Forecast", this);
            if (!response) {
                return; //Don't leave unfinished lesson.
            }
        }
        
        //Lesson is no longer being taken.
        chooseWindow.markLessonAsNotBeingTaken(lesson.getLessonID());
        
        //Close all parts of lesson.
        this.dispose();
        chooseWindow.dispose();
        if(overviewFrame != null) {
            overviewFrame.dispose();
        }
        if(questionFrame != null) {
            questionFrame.dispose();
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

        footerPanel = new javax.swing.JPanel();
        nextButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        headerButton = new javax.swing.JPanel();
        forecastingTitle = new javax.swing.JLabel();
        courseName = new javax.swing.JLabel();
        lessonName = new javax.swing.JLabel();
        instructionsScrollPane = new javax.swing.JScrollPane();
        instructionsTextPane = new javax.swing.JTextPane();
        menuBar = new javax.swing.JMenuBar();
        openMenu = new javax.swing.JMenu();
        gradebookItem = new javax.swing.JMenuItem();
        instructionsItem = new javax.swing.JMenuItem();
        overviewMenu = new javax.swing.JMenuItem();
        linksMenu1 = new javax.swing.JMenu();
        testMenuItem = new javax.swing.JMenuItem();
        glossaryMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        footerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        nextButton.setText("Next");
        nextButton.setPreferredSize(new java.awt.Dimension(85, 23));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        backButton.setText("Back");
        backButton.setPreferredSize(new java.awt.Dimension(85, 23));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.setPreferredSize(new java.awt.Dimension(85, 23));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 386, Short.MAX_VALUE)
                .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(footerPanelLayout.createSequentialGroup()
                    .addGap(245, 245, 245)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(246, Short.MAX_VALUE)))
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(footerPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        headerButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        forecastingTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        forecastingTitle.setText("Daily Weather Forecasting Lesson");

        courseName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        courseName.setText("Course Name");

        lessonName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lessonName.setText("Lesson Name");

        javax.swing.GroupLayout headerButtonLayout = new javax.swing.GroupLayout(headerButton);
        headerButton.setLayout(headerButtonLayout);
        headerButtonLayout.setHorizontalGroup(
            headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerButtonLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addGroup(headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lessonName)
                    .addComponent(forecastingTitle)
                    .addComponent(courseName))
                .addContainerGap(135, Short.MAX_VALUE))
        );
        headerButtonLayout.setVerticalGroup(
            headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(forecastingTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(courseName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lessonName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        instructionsTextPane.setEditable(false);
        instructionsTextPane.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        instructionsTextPane.setText("This page gives access to the Forecasting assignments.  You are to do the Daily Forecasting Assignment ONLY. \n \n1. Daily Forecasting Assignment\nUnder Options, select Assignments, and then the Forecasting assignment link to submit forecasts. \n\nYou will forecast for Athens, GA (station name: KAHN) and use your best judgment to answer 12 questions about the weather in Athens the following day.\n\nClick Save Answers as you move along and Submit when you are done.\n\nAfter making your forecast on a particular day, you can change your forecast at any time until midnight, but the assignment will automatically close at midnight prior to the day you are forecasting for.\n\nBecause I will only count your 20 best forecasts, there is no penalty for trying.  You can attempt up to 50 forecasts total from Oct. 10-Dec. 5, but only your best 20 will count.\n\nScoring: You will get 3 points for a correct answer, one point for trying, and no points if you don't participate.  Each forecast is worth a maximum of 36 points. \n");
        instructionsTextPane.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        instructionsScrollPane.setViewportView(instructionsTextPane);

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

        overviewMenu.setText("Overview");
        overviewMenu.setEnabled(false);
        openMenu.add(overviewMenu);

        menuBar.add(openMenu);

        linksMenu1.setText("Links");
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
                    .addComponent(footerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(headerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(instructionsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(headerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(instructionsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.requestFocus();
    }//GEN-LAST:event_formWindowOpened

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        //Action differs based on how this form was last made visible.
        if(this.overviewFrame != null) { //made visible by overview form
            overviewFrame.setLocation(this.getLocation().x, this.getLocation().y);
            this.dispose();
            overviewFrame.setVisible(true);    
        } else if(this.questionFrame != null) { //made visible by question form
            questionFrame.setLocation(this.getLocation().x, this.getLocation().y);
            //These lines ensure the answer for the current question is shown.
            if(questionFrame.getCurrentQuestion() != -1) {
                questionFrame.setCurrentQuestion(questionFrame.getCurrentQuestion());
            } else {
                questionFrame.displayStationCode();
            }
            
            this.dispose();
            questionFrame.setVisible(true);    
        } else { //made visible by choose lesson form (first showing in lesson)
            new ForecasterLessonWindow(appControl, this, lesson, attempt);
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        chooseWindow.setLocation(this.getLocation());
        
        //Lesson is no longer being taken.
        chooseWindow.markLessonAsNotBeingTaken(lesson.getLessonID());
        
        this.dispose();
        chooseWindow.setVisible(true);        
    }//GEN-LAST:event_backButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        close();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void gradebookItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradebookItemActionPerformed
        ForecasterJOptionPaneFactory.
                showInfoPane("Gradebook not available during forecast."
                + "\nPlease complete forecast or return to the main Forecasting Lesson menu.",
                "Feature Unavailable", this);
    }//GEN-LAST:event_gradebookItemActionPerformed

    private void testMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMenuItemActionPerformed

        String url = "http://organizations.bloomu.edu/weather/viewer/ProgramWebPages/Forecasting_Checklist.htm";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
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

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
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
    private javax.swing.JButton backButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel courseName;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JLabel forecastingTitle;
    private javax.swing.JMenuItem glossaryMenuItem;
    private javax.swing.JMenuItem gradebookItem;
    private javax.swing.JPanel headerButton;
    private javax.swing.JMenuItem instructionsItem;
    private javax.swing.JScrollPane instructionsScrollPane;
    private javax.swing.JTextPane instructionsTextPane;
    private javax.swing.JLabel lessonName;
    private javax.swing.JMenu linksMenu1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton nextButton;
    private javax.swing.JMenu openMenu;
    private javax.swing.JMenuItem overviewMenu;
    private javax.swing.JMenuItem testMenuItem;
    // End of variables declaration//GEN-END:variables
}
