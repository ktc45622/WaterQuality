package weather.clientside.gui.client;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.HOUR;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import weather.ApplicationControlSystem;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import static weather.common.data.forecasterlesson.ForecasterLessonGrader.NO_ANSWER_VALUE;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.Response;
import weather.common.data.forecasterlesson.Station;
import weather.common.dbms.DBMSForecasterAttemptManager;
import weather.common.gui.component.BUJFrame;
import weather.common.utilities.PropertyManager;

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
 * The is an overview of the lesson that was just taken. Here the user will be
 * able to review their responses and submit the forecast. The student will be
 * unable to submit a lesson if not all of the questions are answered. The 
 * responses will be updated by the <code>ForecasterLessonWindow<code/> if they
 * get update.
 *
 * @author Joshua Whiteman
 */
public class ForecasterOverview extends BUJFrame {

    private final DBMSForecasterAttemptManager attemptManager;
    private final ForecasterInstructions instructionsFrame;
    private final ForecasterLessonWindow questionFrame;
    private final ForecasterLesson lesson;
    private final ArrayList<Question> questions;
    private final ArrayList<Response> responses;
    private Station station;
    private final Attempt attempt;
    
    //This reference to the program's application control system must be added
    //because this is a BUJFrame and not a BUDialog.
    private final ApplicationControlSystem appControl;
    
    //One day is added to each value because counting starts on the day of the
    //forecast.
    private final static int MINIMUM_WAITING_DAYS = Integer.parseInt( 
            PropertyManager.getGeneralProperty("MINIMUM_GRADING_DAYS")) + 1;
    private final static int MAXIMUM_WAITING_DAYS = Integer.parseInt( 
            PropertyManager.getGeneralProperty("MAXIMUM_GRADING_DAYS")) + 1;

    /**
     * Creates new form ForecasterOverview
     *
     * @param appControl all the data needed from the database and main app.
     * @param questionFrame gives access to the <code>ForecasterLessonWindow<code/>
     * @param instructionsFrame gives access to the <code>ForecasterInstructions<code/>
     * @param lesson the ForecasterLesson object that contains the text for the
     * instructions.
     * @param responses the responses the student has selected. They may not all
     * be loaded at this point.
     * @param station The location on which the lesson is based.
     * @param attempt answers that will be loaded if a student is editing a
     *  forecast they have made for the day.
     */
    public ForecasterOverview(ApplicationControlSystem appControl,
            ForecasterLessonWindow questionFrame,
            ForecasterInstructions instructionsFrame,
            ForecasterLesson lesson, ArrayList<Response> responses,
            Station station, Attempt attempt) {
        super();
        this.appControl = appControl;
        this.instructionsFrame = instructionsFrame;
        this.questionFrame = questionFrame;
        this.lesson = lesson;
        this.questions = lesson.getQuestions();
        this.responses = responses;
        this.station = station;
        this.attempt = attempt;

        attemptManager = appControl.getDBMSSystem().getForecasterAttemptManager();
        initComponents();

        initialize();

        this.questionFrame.setVisible(false);

        //Keep in same spot as parent
        this.setLocation(questionFrame.getLocation().x, questionFrame.getLocation().y);
        this.setVisible(true);
    }
    
    /**
     * Correctly shows to form if its visible property has been previously set 
     * to false.
     */
    public void unhide() {
        //Keep in same spot as parent
        this.setLocation(questionFrame.getLocation().x, questionFrame.getLocation().y);
        this.setVisible(true);
    }

    private void initialize() {
        super.setTitle("Weather Viewer - Forecaster Lesson Overview");

        courseName.setText(lesson.getCourse().getClassName());
        lessonName.setText(lesson.getLessonName());

        updateOverviewText();

        openLinkListener();
        
        if (attempt != null) {
            submitButton.setText("Resubmit");
        }

    }

    /**
     * Function to update overview text.
     */
    public void updateOverviewText() {
        String overview = "";

        overview += "<p><b>Item 1</b></p>";
        if (station != null) {
            overview += "<p>Station State: " + station.getState() + "</p>";
            overview += "<p>Station Location: " + station.getStationName() + "</p>";
            overview += "<p>Station Code: " + station.getStationId() + "</p>";
        } else {
            overview += "<p style=\"color:red\">No station selected</p>";
        }
        overview += "<hr>";
        
        //Point scrollPosition = overviewScrollPane.getViewport().getViewPosition();
        int i = 1;
        Iterator<Response> rIterator = responses.iterator();
        for (Question q : questions) {
            Response r = rIterator.next();
            overview += "<p><b>Item " + (i + 1) + "</b>";
            overview += " <a href=\"" + i + "\">edit</a></p>";
            overview += "<p><b> Q: </b>" + q.getQuestionText() + "</p>";
            if (r.getAnswers() != null) {
                for (Answer a : r.getAnswers()) {
                    overview += "<p><b> A: </b>" + a.getAnswerText() + "</p>";
                }
                
            } else {
                overview += "<p style=\"color:red\"><b> A: </b>" + "no answer given" + "</p>";
            }

            overview += "<hr>";
            i++;
        }
        overviewTextPane.setText(overview);
    }
    
    public void setStation(Station station) {
        this.station = station;
    }

    private void openLinkListener() {
        overviewTextPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    int i = Integer.parseInt(e.getDescription());
                    questionFrame.setCurrentQuestion(i - 1);
                    returnToQuestions();
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

        footerPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        submitButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        overviewScrollPane = new javax.swing.JScrollPane();
        overviewTextPane = new javax.swing.JTextPane();
        headerButton = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        courseName = new javax.swing.JLabel();
        lessonName = new javax.swing.JLabel();
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
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        footerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        backButton.setText("Back");
        backButton.setPreferredSize(new java.awt.Dimension(85, 23));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        submitButton.setText("Submit");
        submitButton.setPreferredSize(new java.awt.Dimension(85, 23));
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
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
                .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(footerPanelLayout.createSequentialGroup()
                    .addGap(245, 245, 245)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(246, Short.MAX_VALUE)))
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, footerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(footerPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        overviewTextPane.setEditable(false);
        overviewTextPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        overviewTextPane.setText("<p><b> Question 1 </b></p>\n<p> <b> Q: </b> What will be the 12Z temperature (°F) tomorrow? </p>\n<p> <b> A: </b> 12 </p>\n<hr>\n \n<p><b> Question 2 </b></p>\n<p> <b> Q: </b> Cloud cover to inhibit radiational cooling tonight will </p>\n<p> <b> A: </b> be significant </p>\n<hr>\n \n<p><b> Question 3 </b></p>\n<p> <b> Q: </b> A frontal passage by 12Z tomorrow may also affect the temperature. I predict there will have been the passage (within about 140 miles of the reporting station) </p>\n<p> <b> A: </b> a warm front </p>\n<hr>\n \n<p><b> Question 4 </b></p>\n<p> <b> Q: </b> Advection may also change temperature. I predict 12Z temperature change will </p>\n<p> <b> A: </b> be affected by warm air advection. </p>\n<hr>\n \n. . .");
        overviewScrollPane.setViewportView(overviewTextPane);

        headerButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Daily Weather Forecasting Lesson");

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
                    .addComponent(jLabel2)
                    .addComponent(courseName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        headerButtonLayout.setVerticalGroup(
            headerButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(courseName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lessonName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
        instructionsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                instructionsItemActionPerformed(evt);
            }
        });
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
                    .addComponent(footerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(overviewScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(headerButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(overviewScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        windowClosing();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void gradebookItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradebookItemActionPerformed
        ForecasterJOptionPaneFactory
                .showInfoPane("Gradebook not available during forecast."
                + "\nPlease complete forecast or return to the main Forecasting Lesson menu.",
                "Feature Unavailable", this);
    }//GEN-LAST:event_gradebookItemActionPerformed

    private void instructionsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instructionsItemActionPerformed
        instructionsFrame.showOverLessonWindow(this, questionFrame);
    }//GEN-LAST:event_instructionsItemActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        boolean response;
        
        Calendar endDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        
        currentDate.setTime(new Date());
        endDate.setTime(lesson.getLessonEndDate());
        
        if (currentDate.after(endDate)) {
            ForecasterJOptionPaneFactory.showInfoPane(
                    "The assignment was due at " + endDate.get(HOUR) 
                            + ". Please plan ahead next time.",
                    "Past Due", this);
            dispose();
            return;
        }
        
        //Check for unanswered questions, assuming none.
        boolean hasUnansweredQuestions = false;
        
        for (Response r : responses) {
            if (r.getAnswers() == null) {
                hasUnansweredQuestions = true;
                break;
            }
        }

        //Ask to proceed unless questions must be answered.
        if (hasUnansweredQuestions) {
            if (lesson.getPointScale().getRequireAnswers()) {
                ForecasterJOptionPaneFactory.showInfoPane(
                        "You have not anwered all of the questions. Please try again",
                        "Submit Forecast Error", this);
                return;
            } else {
                response = ForecasterJOptionPaneFactory.askUserQuestion(
                        "You have left at least one question unanswered.\n"
                        + "Are you sure you want to submit your forecast?",
                        "Submit Forecast", this);
            }
        } else {
            response = ForecasterJOptionPaneFactory.askUserQuestion(
                    "Are you sure you want to submit your forecast?",
                    "Submit Forecast", this);
        }

        if (response) {
            //Fill unanswered questions with a blank answer.
            if (hasUnansweredQuestions) {
                for (int i = 0; i < questions.size(); i++) {
                    if (responses.get(i).getAnswers() == null) {
                        Answer blankAnswer = new Answer("1A", "No Answer Given",
                                NO_ANSWER_VALUE, questions.get(i));
                        ArrayList<Answer> blankAnswerInList = new ArrayList<>();
                        blankAnswerInList.add(blankAnswer);
                        responses.get(i).setAnswers(blankAnswerInList);
                    }
                }
            }
            
            //Declare attempt.
            Attempt newAttempt = new Attempt(responses,
                    appControl.getGeneralService().getUser(), station.getStationId());
            
            //Add to or edit database.
            if (attempt != null) {
                newAttempt.setAttemptID(attempt.getAttemptID());
                newAttempt.setAttemptDate(attempt.getAttemptDate());
                attemptManager.updateAttempt(newAttempt);
            } else {
                //Uncomment these lines to hardcode in a forecast date.
                //(Also see ForecasterChooseLesson)
                //CHANGE DATE FOR TESTING
//                Calendar testDate = Calendar.getInstance();
//                testDate.set(2015, Calendar.JULY, 26);  //Day BEFORE forecast.
//                newAttempt.setAttemptDate(testDate);
                
                attemptManager.insertAttempt(newAttempt, lesson);
            }
            
            //Lesson is no longer being taken.
            instructionsFrame.getChooseWindow().
                    markLessonAsNotBeingTaken(lesson.getLessonID());

            //Dispose all forms used to show lesson.
            questionFrame.dispose();
            instructionsFrame.dispose();
            instructionsFrame.getChooseWindow().dispose();;
            dispose();
            
            ForecasterJOptionPaneFactory.showInfoPane(
                    "Your forecast has been submitted. \n"
                    + "You may view the results in " + MINIMUM_WAITING_DAYS 
                    + " to " + MAXIMUM_WAITING_DAYS + " days",
                    "Forecast Submitted", this);
        }
    }//GEN-LAST:event_submitButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        windowClosing();
    }//GEN-LAST:event_formWindowClosing

    private void testMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMenuItemActionPerformed

        String url = "http://organizations.bloomu.edu/weather/viewer/ProgramWebPages/Forecasting_Checklist.htm";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
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

    /**
     * Used to return to questions.
     */
    private void returnToQuestions() {
        questionFrame.setLocation(this.getLocation());
        this.setVisible(false);
        questionFrame.setVisible(true);
    }
    
    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        questionFrame.setCurrentQuestion(questionFrame.getLastQuestionIndex());
        returnToQuestions();
    }//GEN-LAST:event_backButtonActionPerformed

    private void windowClosing() {
        boolean response = ForecasterJOptionPaneFactory.askUserQuestion(
                "You have not submitted your forecast. Your "
                + "selections will not be saved.\n Are you sure"
                + " you want to continue?",
                "Exit Forecast", this);
        if (!response) {
            return; //Don't leave unfinished lesson.
        }
        
        //Lesson is no longer being taken.
        instructionsFrame.getChooseWindow().
                markLessonAsNotBeingTaken(lesson.getLessonID());

        //Close all windows that make lesson.
        this.dispose();
        instructionsFrame.getChooseWindow().dispose();
        instructionsFrame.dispose();
        questionFrame.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel courseName;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JMenuItem glossaryMenuItem;
    private javax.swing.JMenuItem gradebookItem;
    private javax.swing.JPanel headerButton;
    private javax.swing.JMenuItem instructionsItem;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lessonName;
    private javax.swing.JMenu linksMenu1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu openMenu;
    private javax.swing.JMenuItem overviewMenu;
    private javax.swing.JScrollPane overviewScrollPane;
    private javax.swing.JTextPane overviewTextPane;
    private javax.swing.JButton submitButton;
    private javax.swing.JMenuItem testMenuItem;
    // End of variables declaration//GEN-END:variables
}
