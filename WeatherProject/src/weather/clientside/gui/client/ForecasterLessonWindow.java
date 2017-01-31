package weather.clientside.gui.client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.BarebonesBrowser;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.AnswerType;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import static weather.common.data.forecasterlesson.ForecasterLessonGrader.NO_ANSWER_VALUE;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.Response;
import weather.common.data.forecasterlesson.Station;
import weather.common.dbms.DBMSStationManager;
import weather.common.gui.component.BUJFrame;
import weather.common.utilities.Debug;
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
 * This is the main window where a user will make a forecast. A lesson must be
 * loaded in order for this window to be opened. This window doesn't submit the
 * forecast. It only displays the questions and stores the responses of the
 * user.
 *
 * The bulk of the code comes from the logic of moving back and forth between
 * questions. A card layout is used to display either radio buttons, text
 * fields, or check boxes depending on the question.
 *
 * TODO: Some data is still hard coded into the program. Once deployment is
 * achieved, these need to be placed in the db or as property files.
 *
 * @author Joshua Whiteman
 */
public class ForecasterLessonWindow extends BUJFrame {

    private final ForecasterInstructions instructionsFrame;
    private final DBMSStationManager stationManager;
    private ForecasterOverview overviewFrame;
    private final ForecasterLesson lesson;
    private ArrayList<Response> responses;
    private ArrayList<Question> questions;
    private Iterator<Question> questionIterator;
    
    //This reference to the program's application control system must be added
    //because this is a BUJFrame and not a BUDialog.
    private final ApplicationControlSystem appControl;
    
    //-1 is station code, 0 is first question.
    private int currentQuestionNum;
   
    private DefaultListModel<String> listModel;
    private CardLayout answerLayout;
    private String cardShown;
    private Station station;
    private final ArrayList<Integer> answeredQuestionNumbers;
    private Vector<Station> stations;
    private String[] states;
    private final String checkListURL;
    private final String glossaryURL;
    private final Attempt attempt;
    
    //String to hold "None Of The Above."
    private final String noneString = PropertyManager
            .getGeneralProperty("noneString");
    
    //These variables hold the minimum and maximum values when the text box card
    //is showing.  These value are retrieved as "answers" from the database.
    private int textBoxMinimumValue;
    private int textBoxMaximumValue;
    
    //This variable hold whether or not the current contents of the text 
    //respone box is valid.
    private boolean isTextAnswerValid;
    

    /**
     * Creates a new window where the student will make a forecast for the next
     * day.
     *
     * @param appControl all the data needed from the database and main app.
     * @param instructionsFrame this window has access to the instructions
     * frame. the relationship is based on setting its visible property on and
     * off and repositioning it.
     * @param lesson the lesson object for this attempt
     * @param attempt this only stores the data of the previous attempt. data is
     * pulled off of it and loaded into new objects which can be changed by the
     * user.
     */
    public ForecasterLessonWindow(ApplicationControlSystem appControl,
            ForecasterInstructions instructionsFrame, ForecasterLesson lesson,
            Attempt attempt) {
        super();
        this.instructionsFrame = instructionsFrame;
        this.lesson = lesson;
        this.attempt = attempt;
        this.appControl = appControl;

        checkListURL = "http://organizations.bloomu.edu"
                + "/weather/viewer/ProgramWebPages/Forecasting_Checklist.htm";
        glossaryURL = "http://organizations.bloomu.edu"
                + "/weather/viewer/ProgramWebPages/Glossary/glossary.html";
        answeredQuestionNumbers = new ArrayList<>();
        stationManager = appControl.getDBMSSystem().getStationManager();

        states = (new String[] {"Select a State", "ALABAMA (AL) ", "ARKANSAS (AR)  ", 
            "ARIZONA (AZ) ", "CALIFORNIA (CA) ", "COLORADO (CO) ", "CONNECTICUT (CT)  ", 
            "DELAWARE (DE)  ", "FLORIDA (FL)  ", "GEORGIA (GA) ", "IDAHO (ID) ", 
            "ILLINOIS (IL) ", "INDIANA (IN)  ", "IOWA (IA)  ", "KANSAS (KS)  ", 
            "KENTUCKY (KY)  ", "LOUISIANA (LA)  ", "MAINE (ME)  ", "MARYLAND (MD)  ", 
            "MASSACHUSETTS (MA)  ", "MICHIGAN (MI)  ", "MINNESOTA (MN)  ", 
            "MISSISSIPPI (MS)  ", "MISSOURI (MO) ", "MONTANA (MT)  ", "NEBRASKA (NE)  ", 
            "NEVADA (NV)  ", "NEW HAMPSHIRE (NH)  ", "NEW JERSEY (NJ)  ", 
            "NEW MEXICO (NM)  ", "NEW YORK (NY)  ", "NORTH CAROLINA (NC)  ", 
            "NORTH DAKOTA (ND)  ", "OHIO (OH)  ", "OKLAHOMA (OK)  ", "OREGON (OR)  ", 
            "PENNSYLVANIA (PA)  ", "RHODE ISLAND (RI) ", "SOUTH CAROLINA (SC)  ", 
            "SOUTH DAKOTA (SD)  ", "TENNESSEE (TN)  ", "TEXAS (TX)  ", "UTAH (UT)  ", 
            "VERMONT (VT)  ", "VIRGINIA (VA)  ", "WASHINGTON (WA) ", "WISCONSIN (WI) ", 
            "WEST VIRGINIA (WV)  ", "WYOMING (WY)" });

        initComponents();
        initialize();
        this.instructionsFrame.setVisible(false);

        //Keep in same spot as parent
        this.setLocation(instructionsFrame.getLocation().x, instructionsFrame.getLocation().y);
        this.setVisible(true);
    }

    /**
     * Sets the question that will be displayed to the given index. Made into a
     * method for access by the <code>ForecasterOverview<code/> and 
     * the <code>ForecasterInstructions</code>.
     *
     * @param i the index to set the question and answer display to.
     */
    public void setCurrentQuestion(int i) {
        nextButton.setText("Next");
        currentQuestionNum = i;
        questionIterator = questions.listIterator(currentQuestionNum);
        updateQuestionDisplay(questionIterator.next());
    }
    
    /**
     * Gets the index of the question that was last displayed. Made into a
     * method for access by the <code>ForecasterInstructions</code>.
     * @return the index of the question that was last displayed
     */
    public int getCurrentQuestion() {
        return currentQuestionNum;
    }

    /**
     * Initializes all of the components and values for the window.
     */
    private void initialize() {
        setTitle("Weather Viewer - Forecaster Lesson");

        courseName.setText(lesson.getCourse().getClassName());
        lessonName.setText(lesson.getLessonName());

        disableCheckBoxes();
        disableRadioButtons();

        answerLayout = (CardLayout) responsePanel.getLayout();

        questions = lesson.getQuestions();
        responses = new ArrayList<>();
        questionIterator = questions.iterator();
        station = null;

        listModel = new DefaultListModel<>();

        questionsList.setModel(listModel);

        listModel.addElement("Overview");
        listModel.addElement("Item 1 : Station Code");

        /**
         * This code assumes a new attempt.
         */
        
        int i = 0;
        for (Question q : questions) {
            responses.add(new Response("1A", null));
            //First question is 2, so add 2 to counter number.
            listModel.addElement("Item " + (i + 2) + " : " 
                    + q.getQuestionName());
            i++;
        }

        for (int j = 0; j < responses.size(); j++) {
            if (!(responses.get(j).getAnswers() == null
                    || responses.get(j).getAnswers().isEmpty())) {
                answeredQuestionNumbers.add(j);
            }
        }

        initializeListeners();
        
        /**
         * Now check for existing attempt and load data.
         */
        
        if (attempt != null) {
            responses = attempt.getResponses();
            station = stationManager.obtainStation(attempt.getStationCode());
            stateComboBox.setSelectedItem(station.getState());
            stationList.setSelectedValue(station.getStationName(), true);

            int count = 0;
            for (Question q : questions) {
                //Check if an answer was given.
                if (responses.get(count).getAnswers().get(0).getAnswerValue()
                       .equals(NO_ANSWER_VALUE)) {
                    //Clear blank answer.
                    responses.get(count).setAnswers(null);
                } else {
                    answeredQuestionNumbers.add(count + 2);
                }
                questionsList.setCellRenderer(new ListRenderer(answeredQuestionNumbers));
                count++;
            }
        }
        
        /**
         * Show Station code.
         */
        
        displayStationCode();
    }

    /**
     * A function to make this object available to the inner class.
     * @return This object as a <code>Component</code>.
     */
    private Component getThisWindow() {
        return this;
    }
    
    private void initializeListeners() {

        changeQuestionListener();
        changeStationListener();

        urlTextPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    BarebonesBrowser.openURL(e.getDescription(), 
                            getThisWindow());
                }
            }
        });

        ItemListener comboBoxListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateStationCodeList();
                }
            }
        };

        stateComboBox.addItemListener(comboBoxListener);
    }

    class ListRenderer extends DefaultListCellRenderer {

        private ArrayList<Integer> rows;

        public ListRenderer(ArrayList<Integer> rows) {
            this.rows = rows;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            for (int i = 0; i < rows.size(); i++) {                
                if (index == rows.get(i)) {
                    setBackground(Color.LIGHT_GRAY);
                }
            }
            return this;
        }
    }

    /**
     * Displays the station code and all of the information pertaining to it.
     * This is a special case and falls out of the question scope. The data is
     * retrieved and stored in a different way. It was carefully inserted in a
     * way that does not impede the already implemented questions and their
     * cycling.
     *
     * Postcondition: currentQuestionNum is set to -1 so the text is not stored
     * in a response. textResponsePane is disabled and must be set back.
     */
    public void displayStationCode() {
        questionNumberLabel.setText("Item 1: Station Code");

        cardShown = "card5";
        answerLayout.show(responsePanel, cardShown);
        stationCodeText.setEnabled(false);
        urlTextPane.setVisible(false);

        if (lesson.getStationCode() == null) {
            questionText.setText("Enter the station code for the site for "
                    + "which you will be forecasting.");
            stationList.setEnabled(true);
            stateComboBox.setEnabled(true);

        } else {
            questionText.setText("Use the following station code to make your"
                    + "forecast.");
            station = stationManager.obtainStation(lesson.getStationCode());
            stateComboBox.setSelectedItem(station.getState());
            stationList.setSelectedValue(station.getStationName(), true);
            stationList.setEnabled(false);
            stateComboBox.setEnabled(false);
        }
        if (station != null) {
            stationCodeText.setText(station.getStationId());
            updateStationCode();
        }
        questionsList.setSelectedIndex(1);
        currentQuestionNum = -1;
    }

    /**
     * Helper function to update the station list after a state is selected.
     */
    private void updateStationCodeList() {
        if(stateComboBox.getSelectedIndex() == 0) {
            return;
        }
        String state = stateComboBox.getSelectedItem().toString().trim();
        int position = state.indexOf("(");
        state = state.substring(position + 1, position + 3);
        stations = stationManager.getAllStationsByState(state);
        listModel = new DefaultListModel<>();

        stationList.setModel(listModel);

        for (Station s : stations) {
            listModel.addElement(s.getStationName());
        }
    }

    /**
     * Helper function to change the station code.
     */
    private void updateStationCode() {
        if (station != null) {
            answeredQuestionNumbers.add(1);
            questionsList.setCellRenderer(new ListRenderer(answeredQuestionNumbers));
        }

        if (overviewFrame != null) {
            overviewFrame.setStation(station);
            overviewFrame.updateOverviewText();
        }
    }

    /**
     * Attaches listener to station list.
     */
    private void changeStationListener() {
        MouseAdapter changeStation;
        changeStation = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                station = stationManager.obtainStation(
                        stationList.getSelectedValue().toString().trim());
                stationCodeText.setText(station.getStationId());

                updateStationCode();
            }
        };

        stationList.addMouseListener(changeStation);
    }

    /**
     * Makes all of the radio button invisible. They need to be turned on
     * whenever they are used.
     */
    private void disableRadioButtons() {
        radioButton1.setVisible(false);
        radioButton2.setVisible(false);
        radioButton3.setVisible(false);
        radioButton4.setVisible(false);
        radioButton5.setVisible(false);
        radioButton6.setVisible(false);
        radioButton7.setVisible(false);
        radioButton8.setVisible(false);
    }

    /**
     * Makes all of the check boxes invisible. They need to be turned on
     * whenever they are used.
     */
    private void disableCheckBoxes() {
        checkBox1.setVisible(false);
        checkBox2.setVisible(false);
        checkBox3.setVisible(false);
        checkBox4.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        radioButtonGroup = new javax.swing.ButtonGroup();
        footerPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        headerButton = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        courseName = new javax.swing.JLabel();
        lessonName = new javax.swing.JLabel();
        bodyPanel = new javax.swing.JPanel();
        questionScrollPane = new javax.swing.JScrollPane();
        questionsList = new javax.swing.JList<>();
        questionPanel = new javax.swing.JPanel();
        questionNumberLabel = new javax.swing.JLabel();
        responsePanel = new javax.swing.JPanel();
        textFieldPanel = new javax.swing.JPanel();
        answerTextField = new javax.swing.JTextField();
        inputCheckLabel = new javax.swing.JLabel();
        radioButtonPanel = new javax.swing.JPanel();
        radioButton1 = new javax.swing.JRadioButton();
        radioButton2 = new javax.swing.JRadioButton();
        radioButton3 = new javax.swing.JRadioButton();
        radioButton4 = new javax.swing.JRadioButton();
        radioButton5 = new javax.swing.JRadioButton();
        radioButton6 = new javax.swing.JRadioButton();
        radioButton7 = new javax.swing.JRadioButton();
        radioButton8 = new javax.swing.JRadioButton();
        checkBoxPanel = new javax.swing.JPanel();
        checkBox1 = new javax.swing.JCheckBox();
        checkBox2 = new javax.swing.JCheckBox();
        checkBox3 = new javax.swing.JCheckBox();
        checkBox4 = new javax.swing.JCheckBox();
        stationCodePanel = new javax.swing.JPanel();
        stateComboBox = new javax.swing.JComboBox();
        stationCodeText = new javax.swing.JTextField();
        stationCodeScrollPane = new javax.swing.JScrollPane();
        stationList = new javax.swing.JList();
        questionTextScrollPane = new javax.swing.JScrollPane();
        questionText = new javax.swing.JTextPane();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        urlScrollPane = new javax.swing.JScrollPane();
        urlTextPane = new javax.swing.JTextPane();
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

        nextButton.setText("Next");
        nextButton.setPreferredSize(new java.awt.Dimension(85, 23));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionEvent(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(footerPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

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
                .addContainerGap(149, Short.MAX_VALUE))
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

        bodyPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        questionsList.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        questionScrollPane.setViewportView(questionsList);

        questionNumberLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        questionNumberLabel.setText("Question #");

        responsePanel.setLayout(new java.awt.CardLayout());

        answerTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                answerTextFieldKeyReleased(evt);
            }
        });

        inputCheckLabel.setText("Answer Status");

        javax.swing.GroupLayout textFieldPanelLayout = new javax.swing.GroupLayout(textFieldPanel);
        textFieldPanel.setLayout(textFieldPanelLayout);
        textFieldPanelLayout.setHorizontalGroup(
            textFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textFieldPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(textFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(answerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputCheckLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        textFieldPanelLayout.setVerticalGroup(
            textFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textFieldPanelLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(answerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputCheckLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );

        responsePanel.add(textFieldPanel, "card4");

        radioButtonGroup.add(radioButton1);
        radioButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioButton1.setText("Choice 1");

        radioButtonGroup.add(radioButton2);
        radioButton2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioButton2.setText("Choice 2");

        radioButtonGroup.add(radioButton3);
        radioButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioButton3.setText("Choice 3");

        radioButtonGroup.add(radioButton4);
        radioButton4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioButton4.setText("Choice 4");

        radioButtonGroup.add(radioButton5);
        radioButton5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioButton5.setText("Choice 5");

        radioButtonGroup.add(radioButton6);
        radioButton6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioButton6.setText("Choice 6");

        radioButtonGroup.add(radioButton7);
        radioButton7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioButton7.setText("Choice 7");

        radioButtonGroup.add(radioButton8);
        radioButton8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioButton8.setText("Choice 8");

        javax.swing.GroupLayout radioButtonPanelLayout = new javax.swing.GroupLayout(radioButtonPanel);
        radioButtonPanel.setLayout(radioButtonPanelLayout);
        radioButtonPanelLayout.setHorizontalGroup(
            radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButton3)
                    .addComponent(radioButton4)
                    .addComponent(radioButton1)
                    .addComponent(radioButton2))
                .addGap(80, 80, 80)
                .addGroup(radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButton8)
                    .addComponent(radioButton7)
                    .addComponent(radioButton6)
                    .addComponent(radioButton5))
                .addContainerGap(106, Short.MAX_VALUE))
        );
        radioButtonPanelLayout.setVerticalGroup(
            radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(radioButton1)
                    .addComponent(radioButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(radioButtonPanelLayout.createSequentialGroup()
                        .addComponent(radioButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioButton8))
                    .addGroup(radioButtonPanelLayout.createSequentialGroup()
                        .addComponent(radioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioButton4)))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        responsePanel.add(radioButtonPanel, "card2");

        checkBox1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkBox1.setText("Choice 1");
        checkBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox1ActionPerformed(evt);
            }
        });

        checkBox2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkBox2.setText("Choice 2");
        checkBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox2ActionPerformed(evt);
            }
        });

        checkBox3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkBox3.setText("Choice 3");
        checkBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox3ActionPerformed(evt);
            }
        });

        checkBox4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkBox4.setText("Choice 4");
        checkBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout checkBoxPanelLayout = new javax.swing.GroupLayout(checkBoxPanel);
        checkBoxPanel.setLayout(checkBoxPanelLayout);
        checkBoxPanelLayout.setHorizontalGroup(
            checkBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(checkBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBox3)
                    .addComponent(checkBox2)
                    .addComponent(checkBox1)
                    .addComponent(checkBox4))
                .addContainerGap(267, Short.MAX_VALUE))
        );
        checkBoxPanelLayout.setVerticalGroup(
            checkBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBox4)
                .addContainerGap(72, Short.MAX_VALUE))
        );

        responsePanel.add(checkBoxPanel, "card3");

        stateComboBox.setModel(new javax.swing.DefaultComboBoxModel(states));

        stationList.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stationList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        stationCodeScrollPane.setViewportView(stationList);

        javax.swing.GroupLayout stationCodePanelLayout = new javax.swing.GroupLayout(stationCodePanel);
        stationCodePanel.setLayout(stationCodePanelLayout);
        stationCodePanelLayout.setHorizontalGroup(
            stationCodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stationCodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(stationCodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(stationCodePanelLayout.createSequentialGroup()
                        .addComponent(stationCodeText, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 99, Short.MAX_VALUE))
                    .addComponent(stateComboBox, 0, 171, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stationCodeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        stationCodePanelLayout.setVerticalGroup(
            stationCodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stationCodePanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(stationCodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stationCodeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addGroup(stationCodePanelLayout.createSequentialGroup()
                        .addComponent(stationCodeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(stateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        responsePanel.add(stationCodePanel, "card5");

        questionTextScrollPane.setBorder(null);
        questionTextScrollPane.setEnabled(false);

        questionText.setEditable(false);
        questionText.setBackground(new java.awt.Color(240, 240, 240));
        questionText.setBorder(null);
        questionText.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        questionText.setText("Question Text");
        questionText.setToolTipText("");
        questionTextScrollPane.setViewportView(questionText);

        urlScrollPane.setBorder(null);

        urlTextPane.setEditable(false);
        urlTextPane.setBackground(new java.awt.Color(240, 240, 240));
        urlTextPane.setBorder(null);
        urlTextPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        urlTextPane.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        urlTextPane.setText("URL link and text go here");
        urlScrollPane.setViewportView(urlTextPane);

        javax.swing.GroupLayout questionPanelLayout = new javax.swing.GroupLayout(questionPanel);
        questionPanel.setLayout(questionPanelLayout);
        questionPanelLayout.setHorizontalGroup(
            questionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(questionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(questionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(questionPanelLayout.createSequentialGroup()
                        .addComponent(questionNumberLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, questionPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator3)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, questionPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, questionPanelLayout.createSequentialGroup()
                        .addGroup(questionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(urlScrollPane, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(questionTextScrollPane))
                        .addGap(18, 18, 18))))
            .addGroup(questionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, questionPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(responsePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        questionPanelLayout.setVerticalGroup(
            questionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(questionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(questionNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(questionTextScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(urlScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(questionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, questionPanelLayout.createSequentialGroup()
                    .addContainerGap(195, Short.MAX_VALUE)
                    .addComponent(responsePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout bodyPanelLayout = new javax.swing.GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
            bodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(questionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(questionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addContainerGap())
        );
        bodyPanelLayout.setVerticalGroup(
            bodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(questionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(questionPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        overviewMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overviewMenuActionPerformed(evt);
            }
        });
        openMenu.add(overviewMenu);

        menuBar.add(openMenu);

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
                    .addComponent(headerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(footerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Performs the necessary operations once the next button is selected. 1.)
     * Store the current response 2.) Open the Overview window if on the last
     * question 3.) Increment the question 4.) Update the display
     *
     * @param evt
     */
    private void nextButtonActionEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionEvent
        //Must check for a station when leaving station code screen.
        if (currentQuestionNum == -1) {
            if (station == null) {
                //With no station, the lesson cannot proceed.
                ForecasterJOptionPaneFactory.showInfoPane(
                        "You must select a weather station.",
                        "Select A Station", this);
                return;
            } else {
                //With station code, enable navigation.
                overviewMenu.setEnabled(true);
            }
        }
        storeResponse();
        // Review the questions
        if (nextButton.getText().equals("Review")) {
            openOverview();
        }

        if (questionIterator.hasNext()) {
            currentQuestionNum++;
            updateQuestionDisplay(questionIterator.next());
        }
    }//GEN-LAST:event_nextButtonActionEvent

    /**
     * Close button selected, see closingWindow.
     *
     * @param evt
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        closingWindow();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * Opens up the gradebook for the student.
     *
     * @param evt
     */
    private void gradebookItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradebookItemActionPerformed
        ForecasterJOptionPaneFactory
                .showInfoPane("Gradebook not available during forecast."
                + "\nPlease complete forecast or return to the main Forecasting Lesson menu.",
                "Feature Unavailable", this);
    }//GEN-LAST:event_gradebookItemActionPerformed

    /**
     * Instructions menu item selected. Opens up an instruction window in front
     * of the current frame.
     *
     * @param evt
     */
    private void instructionsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instructionsItemActionPerformed
        if (currentQuestionNum != -1) {
            storeResponse();
        }
        instructionsFrame.showOverLessonWindow(this);
    }//GEN-LAST:event_instructionsItemActionPerformed

    /**
     * Performs the necessary operations once the back button is selected. 1.)
     * Store the current response 2.) Decrement the question 3.) Update the
     * display
     *
     * @param evt
     */
    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        storeResponse();

        if (currentQuestionNum > 0) {
            currentQuestionNum--;
            questionIterator = questions.listIterator(currentQuestionNum);
            updateQuestionDisplay(questionIterator.next());
        } else if (currentQuestionNum == 0) {
            displayStationCode();
            questionIterator = questions.listIterator(0);
        } else if (currentQuestionNum == -1) {
            instructionsFrame.showOverLessonWindow(this);
        }
    }//GEN-LAST:event_backButtonActionPerformed

    /**
     * Helper function that updates what is displayed based on the given
     * question. It first sets the text for the appropriate components. It
     * updates which item is selected in the list. It sets all of the buttons to
     * the default configuration. It updates the answers that are displayed. It
     * loads responses if they were already given.
     *
     * @param question
     */
    private void updateQuestionDisplay(Question question) {
        //First question is 2, so add 2 to currentQuestionNum.
        questionNumberLabel.setText("Item " + (currentQuestionNum + 2)
                + " : " + question.getQuestionName());
        questionText.setText(question.getQuestionText());
        questionsList.setSelectedIndex(currentQuestionNum + 2);
        urlTextPane.setText(
                " <a href=\"" + glossaryURL + question.getURLLocation()
                + "\">" + question.getURLText() + "</a>");

        urlTextPane.repaint();

        resetButtons();
        updateAnswerDisplay(question);
        loadResponse();

    }

    /**
     * Displays the answer text and the appropriate JComponent for corresponding
     * to the AnswerType.
     *
     * TODO: This function contains a lot of code because each radio button is a
     * separate case. Since the GUI builder initializes the variables, they
     * can't be stored in a container and indexed. Copying them and placing them
     * in a container doesn't work because there are alignment settings that
     * aren't copied.
     *
     * @param question
     */
    private void updateAnswerDisplay(Question question) {
        if (question.getAnswerType() == AnswerType.RadioButton) {
            ArrayList<Answer> answers = question.getAnswers();

            cardShown = "card2";
            answerLayout.show(responsePanel, cardShown);

            int i = 1;
            for (Answer a : answers) {
                switch (i) {

                    case 1:
                        radioButton1.setText(a.getAnswerText());
                        radioButton1.setVisible(true);
                        radioButton1.setActionCommand(a.getAnswerValue());
                        break;
                    case 2:
                        radioButton2.setText(a.getAnswerText());
                        radioButton2.setVisible(true);
                        radioButton2.setActionCommand(a.getAnswerValue());
                        break;
                    case 3:
                        radioButton3.setText(a.getAnswerText());
                        radioButton3.setVisible(true);
                        radioButton3.setActionCommand(a.getAnswerValue());
                        break;
                    case 4:
                        radioButton4.setText(a.getAnswerText());
                        radioButton4.setVisible(true);
                        radioButton4.setActionCommand(a.getAnswerValue());
                        break;
                    case 5:
                        radioButton5.setText(a.getAnswerText());
                        radioButton5.setVisible(true);
                        radioButton5.setActionCommand(a.getAnswerValue());
                        break;
                    case 6:
                        radioButton6.setText(a.getAnswerText());
                        radioButton6.setVisible(true);
                        radioButton6.setActionCommand(a.getAnswerValue());
                        break;
                    case 7:
                        radioButton7.setText(a.getAnswerText());
                        radioButton7.setVisible(true);
                        radioButton7.setActionCommand(a.getAnswerValue());
                        break;
                    case 8:
                        radioButton8.setText(a.getAnswerText());
                        radioButton8.setVisible(true);
                        radioButton8.setActionCommand(a.getAnswerValue());
                        break;
                    default:
                        break;

                }
                i++;
            }
        }
        if (question.getAnswerType() == AnswerType.CheckBox) {
            ArrayList<Answer> answers = question.getAnswers();
            
            cardShown = "card3";
            answerLayout.show(responsePanel, cardShown);

            int i = 1;
            for (Answer a : answers) {
                switch (i) {
                    case 1:
                        checkBox1.setText(a.getAnswerText());
                        checkBox1.setVisible(true);
                        checkBox1.setActionCommand(a.getAnswerValue());
                        break;
                    case 2:
                        checkBox2.setText(a.getAnswerText());
                        checkBox2.setVisible(true);
                        checkBox2.setActionCommand(a.getAnswerValue());
                        break;
                    case 3:
                        checkBox3.setText(a.getAnswerText());
                        checkBox3.setVisible(true);
                        checkBox3.setActionCommand(a.getAnswerValue());
                        break;
                    case 4:
                        checkBox4.setText(a.getAnswerText());
                        checkBox4.setVisible(true);
                        checkBox4.setActionCommand(a.getAnswerValue());
                        break;
                    default:
                        break;
                }
                i++;
            }
        }
        if (question.getAnswerType() == AnswerType.TextField) {
            cardShown = "card4";
            answerLayout.show(responsePanel, cardShown);
            
            //assume no answer
            isTextAnswerValid = false;
            
            //hide label
            inputCheckLabel.setVisible(false);
            
            //"answers" are the minimum and maximum values the user may enter
            ArrayList<Answer> answers = question.getAnswers();
            
            //set mix and max vales
            try {
                textBoxMinimumValue = Integer.parseInt(answers.get(0)
                        .getAnswerValue());
                textBoxMaximumValue = Integer.parseInt(answers.get(1)
                        .getAnswerValue());
            } catch (Exception ex) {
                Debug.println("Unable to parse database range!");
                textBoxMinimumValue = 0;
                textBoxMaximumValue = 9999;
            }
        }
    }

    /**
     * Stores the response the student entered in. Based on what is currently
     * displayed on the screen. The radio, check, and text options must be set
     * up properly for this method to know what is going on.
     */
    private void storeResponse() {
        questionsList.setEnabled(false);
        if (radioButtonGroup.getSelection() != null
                || checkButtonSelected() 
                || ("card4".equals(cardShown) && isTextAnswerValid)) {

            if (!answeredQuestionNumbers.contains(currentQuestionNum + 2)) {
                answeredQuestionNumbers.add(currentQuestionNum + 2);
            }

            switch (cardShown) {
                case "card4": {// text field
                    ArrayList<Answer> answers = new ArrayList<>();
                    Answer a = new Answer("1A", answerTextField.getText(),
                            answerTextField.getText(),
                            questions.get(currentQuestionNum));
                    answers.add(a);
                    responses.get(currentQuestionNum).setAnswers(answers);
                }
                break;
                case "card2": {// radio button
                    ButtonModel m = radioButtonGroup.getSelection();
                    String radioID = m.getActionCommand();
                    // initialize a
                    Answer a = null;

                    for (Answer a2
                            : questions.get(currentQuestionNum).getAnswers()) {
                        if (a2.getAnswerValue().equals(radioID)) {
                            a = a2;
                            break;
                        }
                    }

                    ArrayList<Answer> answers = new ArrayList<>();
                    answers.add(a);
                    responses.get(currentQuestionNum).setAnswers(answers);
                    radioButtonGroup.clearSelection();
                }
                break;
                case "card3": { // check box
                    ArrayList<Answer> answers = new ArrayList<>();
                    Answer a;
                    if (checkBox1.isSelected()) {
                        a = questions.get(currentQuestionNum)
                                .getAnswers().get(0);
                        answers.add(a);
                        checkBox1.setSelected(false);
                    }
                    if (checkBox2.isSelected()) {
                        a = questions.get(currentQuestionNum)
                                .getAnswers().get(1);
                        answers.add(a);
                        checkBox2.setSelected(false);
                    }
                    if (checkBox3.isSelected()) {
                        a = questions.get(currentQuestionNum)
                                .getAnswers().get(2);
                        answers.add(a);
                        checkBox3.setSelected(false);
                    }
                    if (checkBox4.isSelected()) {
                        a = questions.get(currentQuestionNum)
                                .getAnswers().get(3);
                        answers.add(a);
                        checkBox4.setSelected(false);
                    }
                    responses.get(currentQuestionNum).setAnswers(answers);

                }
                default:
                    break;
            } //end of switch
            
            //update overview
            if (overviewFrame != null) {
                overviewFrame.updateOverviewText();
            }
        } //end of if
        
        //always clear text box
        answerTextField.setText("");
        
        questionsList.setCellRenderer(new ListRenderer(answeredQuestionNumbers));
        questionsList.setEnabled(true);
    }

    /**
     * Load the responses (if any) that a student inputed for that question.
     * Will be used if a student is editing a forecast after it was submitted.
     */
    private void loadResponse() {
        Response r = responses.get(currentQuestionNum);
        //System.out.println("Response: " + r.getAnswers().get(0));
        if (r.getAnswers() != null) {
            switch (cardShown) {
                case "card4": {// text field
                    answerTextField.setText(r.getAnswers().get(0).getAnswerValue());
                    //any saved answer would be valid
                    isTextAnswerValid = !answerTextField.getText().trim().isEmpty();
                }
                break;
                case "card2": {// radio button
                    int i = 0;

                    int j = 0;
                    for (Answer a2
                            : questions.get(currentQuestionNum).getAnswers()) {
                        if (a2.getAnswerValue().equals(r.getAnswers().get(0).getAnswerValue())) {
                            i = j + 1;
                            break;
                        }
                        j++;
                    }

                    switch (i) {
                        case 1:
                            radioButtonGroup.setSelected(
                                    radioButton1.getModel(), true);
                            break;
                        case 2:
                            radioButtonGroup.setSelected(
                                    radioButton2.getModel(), true);
                            break;
                        case 3:
                            radioButtonGroup.setSelected(
                                    radioButton3.getModel(), true);
                            break;
                        case 4:
                            radioButtonGroup.setSelected(
                                    radioButton4.getModel(), true);
                            break;
                        case 5:
                            radioButtonGroup.setSelected(
                                    radioButton5.getModel(), true);
                            break;
                        case 6:
                            radioButtonGroup.setSelected(
                                    radioButton6.getModel(), true);
                            break;
                        case 7:
                            radioButtonGroup.setSelected(
                                    radioButton7.getModel(), true);
                            break;
                        case 8:
                            radioButtonGroup.setSelected(
                                    radioButton8.getModel(), true);
                            break;
                        default:
                            break;

                    }
                }
                break;
                case "card3": { // check box
                    for (Answer a : r.getAnswers()) {
                        int i = 0;

                        int j = 0;
                        for (Answer a2
                                : questions.get(currentQuestionNum).getAnswers()) {
                            if (a2.getAnswerValue().equals(a.getAnswerValue())) {
                                i = j + 1;
                                break;
                            }
                            j++;
                        }

                        switch (i) {
                            case 1:
                                checkBox1.setSelected(true);
                                break;
                            case 2:
                                checkBox2.setSelected(true);
                                break;
                            case 3:
                                checkBox3.setSelected(true);
                                break;
                            case 4:
                                checkBox4.setSelected(true);
                                break;
                            default:
                                break;
                        }
                    }

                }
                break;
                default:
                    break;
            }
        }
    }
    
    /**
     * Returns the number of the last question when the first question after the
     * station code is indexed to 0.
     * @return The number of the last question when the first question after the
     * station code is indexed to 0.
     */
    public int getLastQuestionIndex() {
        return questions.size() - 1;
    }

    /**
     * Resets all of the buttons so they can be properly configured and the user
     * doesn't go out of array bounds.
     */
    private void resetButtons() {
        if (currentQuestionNum == questions.size() - 1) {
            nextButton.setText("Review");
        } else {
            nextButton.setText("Next");
        }

        urlTextPane.setVisible(true);

        disableCheckBoxes();
        disableRadioButtons();
    }

    /**
     * Handles when a user selects one of the options in the list on the side.
     */
    private void changeQuestionListener() {
        MouseAdapter changeQuestion = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 1) {
                    //Must check for a station when leaving station code screen.
                    if (currentQuestionNum == -1) {
                        if (station == null) {
                            //With no station, the lesson cannot proceed.
                            ForecasterJOptionPaneFactory.showInfoPane(
                                    "You must select a weather station.",
                                    "Select A Station", getThis());
                            questionsList.setSelectedIndex(1);
                            return;
                        } else {
                            //With station code, enable navigation.
                            overviewMenu.setEnabled(true);
                        }
                    }
                    
                    storeResponse();
                    // The tree that was selected
                    JList targetList = (JList) e.getSource();

                    int index = targetList.getAnchorSelectionIndex();
                    if (index == 0) {
                        openOverview();
                        questionsList.setSelectedIndex(currentQuestionNum + 2);

                    } else if (index == 1) {
                        displayStationCode();
                    } else {
                        // The node that was selected
                        setCurrentQuestion(
                                targetList.getAnchorSelectionIndex() - 2);
                    }

                }
            }
        };

        questionsList.addMouseListener(changeQuestion);
    }
    
    /**
     * Returns this object as a <code>Component</code> for use by inner classes.
     * @return This object as a <code>Component</code> for use by inner classes.
     */
    private Component getThis() {
        return this;
    }
    
    private void overviewMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overviewMenuActionPerformed
        storeResponse();
        openOverview();
    }//GEN-LAST:event_overviewMenuActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closingWindow();
    }//GEN-LAST:event_formWindowClosing

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

    private void checkBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox1ActionPerformed
        //If this checkbox is "None Of The Above" and is selected;
        //we must deselect the other checkboxes.
        if (checkBox1.isSelected() && checkBox1.getText()
                .equals(noneString)) {
            checkBox2.setSelected(false);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
        } else if (checkBox1.isSelected()) {
            //Unselect any "None Of The Above" checkboxes.
            if (checkBox2.getText().equals(noneString)) {
                checkBox2.setSelected(false);
            }
            if (checkBox3.getText().equals(noneString)) {
                checkBox3.setSelected(false);
            }
            if (checkBox4.getText().equals(noneString)) {
                checkBox4.setSelected(false);
            }
        }
        //Make sure user can't unselect last box.
        if (!checkBox1.isSelected() && !checkBox2.isSelected()
                && !checkBox3.isSelected() && !checkBox4.isSelected()) {
            checkBox1.setSelected(true);
        }
    }//GEN-LAST:event_checkBox1ActionPerformed

    private void checkBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox2ActionPerformed
        //If this checkbox is "None Of The Above" and is selected;
        //we must deselect the other checkboxes.
        if(checkBox2.isSelected() && checkBox2.getText()
                .equals(noneString)) {
            checkBox1.setSelected(false);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
        } else if (checkBox2.isSelected()) {
            //Unselect any "None Of The Above" checkboxes.
            if (checkBox1.getText().equals(noneString)) {
                checkBox1.setSelected(false);
            }
            if (checkBox3.getText().equals(noneString)) {
                checkBox3.setSelected(false);
            }
            if (checkBox4.getText().equals(noneString)) {
                checkBox4.setSelected(false);
            }
        }
        //Make sure user can't unselect last box.
        if (!checkBox1.isSelected() && !checkBox2.isSelected()
                && !checkBox3.isSelected() && !checkBox4.isSelected()) {
            checkBox2.setSelected(true);
        }
    }//GEN-LAST:event_checkBox2ActionPerformed
        
    private void checkBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox3ActionPerformed
        //If this checkbox is "None Of The Above" and is selected;
        //we must deselect the other checkboxes.
        if (checkBox3.isSelected() && checkBox3.getText()
                .equals(noneString)) {
            checkBox2.setSelected(false);
            checkBox1.setSelected(false);
            checkBox4.setSelected(false);
        } else if (checkBox3.isSelected()) {
            //Unselect any "None Of The Above" checkboxes.
            if (checkBox2.getText().equals(noneString)) {
                checkBox2.setSelected(false);
            }
            if (checkBox1.getText().equals(noneString)) {
                checkBox1.setSelected(false);
            }
            if (checkBox4.getText().equals(noneString)) {
                checkBox4.setSelected(false);
            }
        }
        //Make sure user can't unselect last box.
        if (!checkBox1.isSelected() && !checkBox2.isSelected()
                && !checkBox3.isSelected() && !checkBox4.isSelected()) {
            checkBox3.setSelected(true);
        }
    }//GEN-LAST:event_checkBox3ActionPerformed

    private void checkBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox4ActionPerformed
        //If this checkbox is "None Of The Above" and is selected;
        //we must deselect the other checkboxes.
        if(checkBox4.isSelected() && checkBox4.getText()
                .equals(noneString)) {
            checkBox2.setSelected(false);
            checkBox3.setSelected(false);
            checkBox1.setSelected(false);
        } else if (checkBox4.isSelected()) {
            //Unselect any "None Of The Above" checkboxes.
            if (checkBox2.getText().equals(noneString)) {
                checkBox2.setSelected(false);
            }
            if (checkBox3.getText().equals(noneString)) {
                checkBox3.setSelected(false);
            }
            if (checkBox1.getText().equals(noneString)) {
                checkBox1.setSelected(false);
            }
        }
        //Make sure user can't unselect last box.
        if (!checkBox1.isSelected() && !checkBox2.isSelected()
                && !checkBox3.isSelected() && !checkBox4.isSelected()) {
            checkBox4.setSelected(true);
        }
    }//GEN-LAST:event_checkBox4ActionPerformed

    private void answerTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_answerTextFieldKeyReleased
        if (answerTextField.getText().trim().isEmpty()) {
            //it is valid to leave answer blank
            isTextAnswerValid = false;
            inputCheckLabel.setText("<html>A blank answer will not be saved."
                    + "</html>");
            inputCheckLabel.setForeground(Color.RED);
            inputCheckLabel.setVisible(true);
            return;
        }
        try {
            String answerString = answerTextField.getText().trim();
            int answer = Integer.parseInt(answerString);
            if (answer < textBoxMinimumValue) {
                throw new Exception();
            }
            if (answer > textBoxMaximumValue) {
                throw new Exception();
            }
        } catch (Exception ex) {
            //answer is valid
            isTextAnswerValid = false;
            inputCheckLabel.setText("<html>Your answer is not valid and will "
                    + "not be saved.  You must enter an integer between "
                    + textBoxMinimumValue + " and " + textBoxMaximumValue
                    + ".</html>");
            inputCheckLabel.setForeground(Color.RED);
            inputCheckLabel.setVisible(true);
            return;
        }
        //answer is valid
        isTextAnswerValid = true;
        inputCheckLabel.setText("<html>Your answer is valid and will be saved."
                + "</html>");
        inputCheckLabel.setForeground(Color.GREEN);
        inputCheckLabel.setVisible(true);
    }//GEN-LAST:event_answerTextFieldKeyReleased
        
    private void closingWindow() {
        boolean response;
        if (attempt == null) {
            response = ForecasterJOptionPaneFactory.askUserQuestion(
                    "You have not submitted your forecast. Your "
                    + "selections will not be saved.\n Are you sure"
                    + " you want to close?",
                    "Exit Forecast", this);
        } else {
            response = ForecasterJOptionPaneFactory.askUserQuestion(
                    "Your edits have not been saved. You must resubmit your"
                    + " answers to save all your answers.\n Are you sure"
                    + " you want to close?",
                    "Exit Forecast", this);
        }
        
        if (response) {
            //Lesson is no longer being taken.
            instructionsFrame.getChooseWindow().
                    markLessonAsNotBeingTaken(lesson.getLessonID());
        
            //Close all windows that make lesson.
            if (overviewFrame != null) {
                overviewFrame.dispose();
            }
            instructionsFrame.getChooseWindow().dispose();
            instructionsFrame.dispose();
            dispose();
        }
    }

    /**
     * Displays the answers that have been given by the student.
     */
    private void openOverview() {
        if (overviewFrame == null) {
            overviewFrame = new ForecasterOverview(
                    appControl, this, instructionsFrame, lesson, responses,
                    station, attempt);
        } else {
            overviewFrame.unhide();
        }
        this.setVisible(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField answerTextField;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel bodyPanel;
    private javax.swing.JCheckBox checkBox1;
    private javax.swing.JCheckBox checkBox2;
    private javax.swing.JCheckBox checkBox3;
    private javax.swing.JCheckBox checkBox4;
    private javax.swing.JPanel checkBoxPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel courseName;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JMenuItem glossaryMenuItem;
    private javax.swing.JMenuItem gradebookItem;
    private javax.swing.JPanel headerButton;
    private javax.swing.JLabel inputCheckLabel;
    private javax.swing.JMenuItem instructionsItem;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lessonName;
    private javax.swing.JMenu linksMenu1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton nextButton;
    private javax.swing.JMenu openMenu;
    private javax.swing.JMenuItem overviewMenu;
    private javax.swing.JLabel questionNumberLabel;
    private javax.swing.JPanel questionPanel;
    private javax.swing.JScrollPane questionScrollPane;
    private javax.swing.JTextPane questionText;
    private javax.swing.JScrollPane questionTextScrollPane;
    private javax.swing.JList<String> questionsList;
    private javax.swing.JRadioButton radioButton1;
    private javax.swing.JRadioButton radioButton2;
    private javax.swing.JRadioButton radioButton3;
    private javax.swing.JRadioButton radioButton4;
    private javax.swing.JRadioButton radioButton5;
    private javax.swing.JRadioButton radioButton6;
    private javax.swing.JRadioButton radioButton7;
    private javax.swing.JRadioButton radioButton8;
    private javax.swing.ButtonGroup radioButtonGroup;
    private javax.swing.JPanel radioButtonPanel;
    private javax.swing.JPanel responsePanel;
    private javax.swing.JComboBox stateComboBox;
    private javax.swing.JPanel stationCodePanel;
    private javax.swing.JScrollPane stationCodeScrollPane;
    private javax.swing.JTextField stationCodeText;
    private javax.swing.JList stationList;
    private javax.swing.JMenuItem testMenuItem;
    private javax.swing.JPanel textFieldPanel;
    private javax.swing.JScrollPane urlScrollPane;
    private javax.swing.JTextPane urlTextPane;
    // End of variables declaration//GEN-END:variables

    private boolean checkButtonSelected() {
        return checkBox1.isSelected()
                || checkBox2.isSelected()
                || checkBox3.isSelected()
                || checkBox4.isSelected();
    }

}
