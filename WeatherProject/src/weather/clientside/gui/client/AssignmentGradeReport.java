package weather.clientside.gui.client;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.FGBLessonViewANode;
import weather.clientside.utilities.FGBLessonViewLNode;
import weather.clientside.utilities.ForecasterJTableCellRenderer;
import weather.clientside.utilities.ForecasterJTreeNode;
import weather.clientside.utilities.StudentListTableModel;
import weather.clientside.utilities.ToolTipTable;
import weather.common.data.forecasterlesson.Answer;
import static weather.common.data.forecasterlesson.ForecasterLessonGrader.NO_ANSWER_VALUE;
import weather.clientside.gradebook.GBForecastingAttemptEntry;
import weather.clientside.gradebook.GBForecastingLessonEntry;
import weather.clientside.gradebook.GBForecastingQuestionEntry;
import weather.clientside.gradebook.GBForecastingResponseEntry;
import weather.clientside.gradebook.GBStudentEntry;
import weather.common.data.forecasterlesson.InstructorResponse;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.Response;
import weather.common.data.forecasterlesson.Station;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.WeatherLogger;

/**
 * Displays details for every lesson attempt entry. Shows details for all questions,
 * specifically: question #, question content, station code used, 
 * points scored on question, correct answer for question, 
 * and student answer for question.
 *
 * @author Nikita Maizet
 */
public class AssignmentGradeReport extends BUDialog {

    private GBStudentEntry student;
    private ArrayList<GBForecastingLessonEntry> lessons;
    private int[] selectedNodePath;

    /**
     * Creates new grade report form.
     * 
     * @param appControl ApplicationControlSystem instance
     * @param student GBStudentEntry object from which assignment data is obtained
     * @param selectedNodePath path of lesson node selected in calling form
     */
    public AssignmentGradeReport(ApplicationControlSystem appControl,
            GBStudentEntry student, int[] selectedNodePath) {
        super(appControl);
        initComponents();

        this.student = student;
        this.selectedNodePath = selectedNodePath;
        lessons = student.getLessons();

        initialize();

        //Set size and focus
        int width = 1000 + this.getInsets().left + this.getInsets().right;
        int height = 605 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        this.setTitle("Instructor Gradebook - Assignment View");
        pack();
        this.setModal(true);
        super.postInitialize(false);
    }

    private void initialize() {
        studentNameLabel.setText(student.getLastName() + ", "
                + student.getFirstName());

        populateLessonData();
        createMouseClickListener();
        loadSelectedNode();
    }

    /**
     * Displays all lessons for student in selected course.
     */
    private void populateLessonData() {
        String[] topEntry = {"Entries:"};
        String[] headerVals = {"Assignment:"};
        ForecasterJTreeNode top = new ForecasterJTreeNode(topEntry);
        ForecasterJTreeNode headerNode = new ForecasterJTreeNode(headerVals);
        // String arrays holding data for each lesson row
        ArrayList<ForecasterJTreeNode> lessonNodes = new ArrayList<>();
        // String arrays holding data for each lesson's attempt row
        ArrayList<ForecasterJTreeNode> attemptNodes = new ArrayList<>();

        // create information arrays for nodes:
        for (GBForecastingLessonEntry l : lessons) {
            // add lesson node String array
            lessonNodes.add(new ForecasterJTreeNode(new FGBLessonViewLNode(l)));

            for (GBForecastingAttemptEntry a : l.getAttempts()) {
                attemptNodes.add(new ForecasterJTreeNode(new FGBLessonViewANode(a)));
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
    }

    private void loadSelectedNode() {
        ForecasterJTreeNode root = (ForecasterJTreeNode) assignmentTree.getModel().getRoot();
        ForecasterJTreeNode assignmentNode;
        ForecasterJTreeNode attemptNode;

        if (selectedNodePath[0] > 0) {
            assignmentNode = (ForecasterJTreeNode) root.getChildAt(selectedNodePath[0]);

            if (selectedNodePath[1] > -1) {
                attemptNode = (ForecasterJTreeNode) assignmentNode.getChildAt(selectedNodePath[1]);

                Object object = attemptNode.getUserObject();

                if (object instanceof FGBLessonViewANode) {
                    FGBLessonViewANode node = (FGBLessonViewANode) object;
                    GBForecastingAttemptEntry entry = (GBForecastingAttemptEntry) node.getSourceObject();

                    Object object2 = assignmentNode.getUserObject();

                    if (object2 instanceof FGBLessonViewLNode) {
                        FGBLessonViewLNode node2 = (FGBLessonViewLNode) object2;
                        GBForecastingLessonEntry entry2 = (GBForecastingLessonEntry) node2.getSourceObject();
                        updateAssignmentLabel(entry2);

                        loadQuestionsList(entry2, entry);
                    }

                    updateAttemptLabel(entry);
                }
            } else {
                // object at that node
                Object object = assignmentNode.getUserObject();

                if (object instanceof FGBLessonViewLNode) {
                    FGBLessonViewLNode node = (FGBLessonViewLNode) object;
                    GBForecastingLessonEntry entry = (GBForecastingLessonEntry) node.getSourceObject();

                    updateAssignmentLabel(entry);

                    // load first attempt if exists
                    if (assignmentNode.getChildCount() > 0) {
                        ForecasterJTreeNode childNode
                                = (ForecasterJTreeNode) assignmentNode.getChildAt(0);

                        Object object2 = childNode.getUserObject();

                        if (object2 instanceof FGBLessonViewANode) {
                            FGBLessonViewANode node2 = (FGBLessonViewANode) object2;
                            GBForecastingAttemptEntry entry2
                                    = (GBForecastingAttemptEntry) node2.getSourceObject();

                            updateAttemptLabel(entry2);
                            loadQuestionsList(entry, entry2);
                        }
                    } else {
                        attemptLabel.setText("");
                        StudentListTableModel model = getBlankTableModel();
                        gradeTable.setModel(model);
                        setGradeTableProperties();
                    }

                }
            }

        }

    }

    private void createMouseClickListener() {
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

                    // object at that node
                    Object object = targetNode.getUserObject();

                    /*
                     When single click lesson will automatically load question
                     data for first attempt on that lesson if available.
                     */
                    if (object instanceof FGBLessonViewLNode) {
                        FGBLessonViewLNode node = (FGBLessonViewLNode) object;
                        GBForecastingLessonEntry entry = (GBForecastingLessonEntry) node.getSourceObject();

                        updateAssignmentLabel(entry);

                        // load first attempt if exists
                        if (targetNode.getChildCount() > 0) {
                            ForecasterJTreeNode childNode
                                    = (ForecasterJTreeNode) targetNode.getChildAt(0);
                            Object object2 = childNode.getUserObject();

                            if (object2 instanceof FGBLessonViewANode) {
                                FGBLessonViewANode node2 = (FGBLessonViewANode) object2;
                                GBForecastingAttemptEntry entry2
                                        = (GBForecastingAttemptEntry) node2.getSourceObject();

                                updateAttemptLabel(entry2);
                                loadQuestionsList(entry, entry2);
                            }
                        } else {
                            attemptLabel.setText("");
                            StudentListTableModel model = getBlankTableModel();
                            gradeTable.setModel(model);
                            setGradeTableProperties();
                        }

                    }

                    /*
                     When click on a lesson node will set the lesson the attempt
                     was for in the appropriate labels.
                     */
                    if (object instanceof FGBLessonViewANode) {
                        FGBLessonViewANode node = (FGBLessonViewANode) object;
                        GBForecastingAttemptEntry entry = (GBForecastingAttemptEntry) node.getSourceObject();

                        // get parent lesson node:
                        ForecasterJTreeNode parentLessonNode
                                = (ForecasterJTreeNode) targetNode.getParent();

                        Object obj = parentLessonNode.getUserObject();

                        if (obj instanceof FGBLessonViewLNode) {
                            FGBLessonViewLNode node2 = (FGBLessonViewLNode) obj;
                            GBForecastingLessonEntry entry2 = (GBForecastingLessonEntry) node2.getSourceObject();
                            updateAssignmentLabel(entry2);

                            loadQuestionsList(entry2, entry);
                        }

                        updateAttemptLabel(entry);
                    }
                }
            }
        };

        assignmentTree.addMouseListener(openLessonClick);
    }

    @SuppressWarnings("empty-statement")
    private void loadQuestionsList(GBForecastingLessonEntry lesson, GBForecastingAttemptEntry attempt) {
        ArrayList<GBForecastingQuestionEntry> questions = lesson.getQuestions();
        ArrayList<GBForecastingResponseEntry> responses = attempt.getResponses();

        String stationCode = attempt.getAttemptObj().getStationCode();
        Calendar attemptDate = (Calendar) attempt.getStationDate().clone();

        Station station;  // To hold data for the day in question
        HashMap<String, String> answerMap = null;
        boolean stationDataAvailable = false;

        if (attempt.hasBeenGraded()) {
            stationDataAvailable = true;
            station = appControl.getDBMSSystem().getForecasterStationDataManager()
                    .getStation(stationCode, new Date(attemptDate.getTimeInMillis()));

            // fail safe for if station data no longer exists for graded lesson
            if (station == null) {
                stationDataAvailable = false;
            } else // get hash map containing answer data, indexed by data key coming from corresponding questions
            {
                answerMap = station.getData();
            }
        }

        // Gets blank table model and fills with student info
        StudentListTableModel model = getBlankTableModel();

        Object[] temp = {Integer.toString((1)),
            "Station Code",
            " ",
            attempt.getAttemptObj().getStationCode(),
            " "};

        model.addRow(temp);

        // Contents of table row (besides question text fron above ArrayList)
        String responsePercentage;
        String correctAnswerText;
        String userResponseText;

        for (int i = 0; i < questions.size(); i++) {
            // Get value of responsePercentage for this table row
            responsePercentage = "0.0";
            if (attempt.hasBeenGraded()) {
                responsePercentage = Double.toString(responses.get(i).getPercentage());
            }

            // Get value of correctAnswerText for this table row
            
            // String to hold the answers from the web
            String webAnswerString = null;
            
            // Get current question
            Question currentQuestion = (Question) questions.get(i)
                    .getEntryObject();
            
            // Get instuctor responses
            ArrayList<InstructorResponse> instructorResponses = appControl
                    .getDBMSSystem().getInstructorResponseManager()
                    .getResponsesByQuestionAndDateAndStation(currentQuestion, 
                    new Date(attemptDate.getTimeInMillis()), stationCode);
            
            if (stationDataAvailable) {
                // Get string from web
                webAnswerString = answerMap.get(questions.get(i).getDataKey());
                // Correct webAnswerString if necessary
                if (webAnswerString.indexOf(":") != -1) {
                    webAnswerString = standardizeWebStringBange(webAnswerString, 
                            currentQuestion);
                }
            }
            
            // Build intermediate string with number in place of phrase 
            // answers
            StringBuilder sbCorrect = new StringBuilder();
            sbCorrect.append("<html>");
            
            // Add web responses to answer string if pressent
            if (webAnswerString != null) {
                sbCorrect.append(webAnswerString);
            }
            
            // Add instructor responses to answer string if pressent
            int count = 0;
            for (InstructorResponse ir : instructorResponses) {
                if (count > 0 || webAnswerString != null) {
                    sbCorrect.append("|");
                }
                sbCorrect.append("<font color = \"green\">");
                sbCorrect.append(ir.getAnswer()).append("</font>");
                count++;
            }
            
            // Finish builder
            sbCorrect.append("</html>");

            // Store this result as a String
            correctAnswerText = sbCorrect.toString();

            // Replace numbers if answer is not a range
            if (correctAnswerText.indexOf(":") == -1) {
                // Replace numbers
                Question question = (Question) questions.get(i)
                        .getEntryObject();
                for (Answer answer : question.getAnswers()) {
                    correctAnswerText = correctAnswerText
                            .replaceAll(answer.getAnswerValue(),
                            answer.getAnswerText());
                }
            }

            // Impove grammer and spacing
            correctAnswerText = correctAnswerText.replaceAll(",", ", ");
            correctAnswerText = correctAnswerText.replaceAll("\\|", " OR ");
            correctAnswerText = correctAnswerText.replaceAll(":", " TO ");

            // Get value of userResponseText for this table row
            Response r = (Response) responses.get(i).getEntryObject();
            StringBuilder sbStudent = new StringBuilder();
            
            // Show in red if the user gave no answer
            boolean showInRed = r.getAnswers().get(0).getAnswerValue()
                    .equals(NO_ANSWER_VALUE);
            if (showInRed) {
                sbStudent.append("<html><font color = \"red\">");
            }
            
            // Add answers to builder
            count = 1;
            for (Answer a : r.getAnswers()) {
                sbStudent.append(a.getAnswerText());
                if (count < r.getAnswers().size()) {
                    sbStudent.append(", ");
                }
                count++;
            }
            
            // Fininh html if text is in red
            if (showInRed) {
                sbStudent.append("</html>");
            }
            
            // Get string from builder
            userResponseText = sbStudent.toString();

            // Add table row to model
            Object[] temp2 = {Integer.toString((i + 2)), // number of question 
                questions.get(i).getEntryText(),
                responsePercentage,
                correctAnswerText,
                userResponseText};
            model.addRow(temp2);
        }

        gradeTable.setModel(model);

        setGradeTableProperties();
    }

    private void setGradeTableProperties() {
        if (gradeTable.getModel().getColumnCount() > 0) {
            gradeTable.getColumn(gradeTable.getColumnModel().getColumn(0).getIdentifier()).setPreferredWidth(25);
            gradeTable.getColumn(gradeTable.getColumnModel().getColumn(1).getIdentifier()).setPreferredWidth(365);
            gradeTable.getColumn(gradeTable.getColumnModel().getColumn(2).getIdentifier()).setPreferredWidth(55);
            gradeTable.getColumn(gradeTable.getColumnModel().getColumn(3).getIdentifier()).setPreferredWidth(240);
            gradeTable.getColumn(gradeTable.getColumnModel().getColumn(4).getIdentifier()).setPreferredWidth(240);
            this.setResizable(false);
            // set true to prevent table rows from changing location & data content BUG
            //agradeTable.setEnabled(false);
        }
    }

    /**
     * This function standardizes answers from the web so they comply with 
     * acceptable input ranges.  It should only be called to check answers of 
     * the form "low:high".  If correction is necessary, the corrected string is
     * returned.  Otherwise, it input string is returned.
     * 
     * @param webAnswerString An answer form the web of the form "low:high" to 
     * be checked.
     * @param currentQuestion the <code>Question</code> for which the string
     * parameter is intended to provide an answer.  It is used to check the
     * database for the acceptable answer range.
     * @return A <code>String</code> such that, if correction is necessary, the
     * corrected string is returned. Otherwise, it input string is returned.
     */
    private String standardizeWebStringBange(String webAnswerString,
            Question currentQuestion) {
        int colonIndex = webAnswerString.indexOf(":");

        // To store range form database.
        int databaseMinimumValue;
        int databaseMaximumValue;

        // To store values from webAnswerString.
        int stringMinimumValue;
        int stringMaximumValue;

        // Parse webAnswerString.
        try {
            // Try to parse integers.
            String lowString = webAnswerString.substring(0, colonIndex);
            String highString = webAnswerString.substring(colonIndex + 1);
            stringMinimumValue = Integer.parseInt(lowString);
            stringMaximumValue = Integer.parseInt(highString);
        } catch (Exception e) {
            WeatherLogger.log(Level.SEVERE, "Could not parse web answer.");
            return webAnswerString;
        }

        // Get the desired values from the database.
        // The "answers" are the minimum and maximum values that can be shown.
        ArrayList<Answer> answers = currentQuestion.getAnswers();

        try {
            databaseMinimumValue = Integer.parseInt(answers.get(0)
                    .getAnswerValue());
            databaseMaximumValue = Integer.parseInt(answers.get(1)
                    .getAnswerValue());
        } catch (Exception ex) {
            databaseMinimumValue = 0;
            databaseMaximumValue = 9999;
        }

        // Change webAnswerString if nescessary.
        boolean changed = false;
        if (stringMinimumValue < databaseMinimumValue) {
            stringMinimumValue = databaseMinimumValue;
            changed = true;
        }
        if (stringMaximumValue > databaseMaximumValue) {
            stringMaximumValue = databaseMaximumValue;
            changed = true;
        }
        if (changed) {
            return "" + stringMinimumValue + ":"
                    + stringMaximumValue;
        } else {
            return webAnswerString;
        }
    }
    
    /**
     * Updates the values of assignment name and grade label according to most
     * recent selection.
     *
     * @param entry
     */
    private void updateAssignmentLabel(GBForecastingLessonEntry entry) {
        assignmentLabel.setText(entry.getEntryName());
        assignmentGradeLabel.setText(Double.toString(entry.getPercentage()) + "%");
    }

    private void updateAttemptLabel(GBForecastingAttemptEntry entry) {
        attemptLabel.setText(entry.getEntryName());
        assignmentGradeLabel.setText(Double.toString(entry.getPercentage()) + "%");
    }

    private StudentListTableModel getBlankTableModel() {
        String[] columnNames = {"#", "Question", "Score(%)", "Correct Answer", "Student Answer"};
        Object[][] data = {{}};

        StudentListTableModel model = new StudentListTableModel(data, columnNames);
        // row count to zero to eliminate blank first row
        model.setRowCount(0);

        return model;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topJPanel = new javax.swing.JPanel();
        studentNameLabel = new javax.swing.JLabel();
        assignmentScrollPane = new javax.swing.JScrollPane();
        assignmentTree = new javax.swing.JTree();
        bottomJPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        gradeTableScrollPane = new javax.swing.JScrollPane();
        gradeTable = new ToolTipTable();
        middleJPanel = new javax.swing.JPanel();
        assignmentLabel = new javax.swing.JLabel();
        attemptLabel = new javax.swing.JLabel();
        assignmentGradeTextLabel = new javax.swing.JLabel();
        assignmentGradeLabel = new javax.swing.JLabel();

        setResizable(false);

        topJPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        studentNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        studentNameLabel.setText("student name");
        studentNameLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout topJPanelLayout = new javax.swing.GroupLayout(topJPanel);
        topJPanel.setLayout(topJPanelLayout);
        topJPanelLayout.setHorizontalGroup(
            topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(studentNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        topJPanelLayout.setVerticalGroup(
            topJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(studentNameLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        assignmentScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        assignmentScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        assignmentScrollPane.setViewportView(assignmentTree);

        bottomJPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bottomJPanelLayout = new javax.swing.GroupLayout(bottomJPanel);
        bottomJPanel.setLayout(bottomJPanelLayout);
        bottomJPanelLayout.setHorizontalGroup(
            bottomJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomJPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomJPanelLayout.setVerticalGroup(
            bottomJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomJPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );

        gradeTableScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gradeTableScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        gradeTable.setAutoscrolls(false);
        gradeTable.setDragEnabled(true);
        gradeTableScrollPane.setViewportView(gradeTable);

        middleJPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        assignmentLabel.setText("Assignment -");

        attemptLabel.setText("Attempt");

        assignmentGradeTextLabel.setText("Grade:");

        assignmentGradeLabel.setText("-");

        javax.swing.GroupLayout middleJPanelLayout = new javax.swing.GroupLayout(middleJPanel);
        middleJPanel.setLayout(middleJPanelLayout);
        middleJPanelLayout.setHorizontalGroup(
            middleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(middleJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(assignmentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attemptLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(assignmentGradeTextLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(assignmentGradeLabel)
                .addContainerGap())
        );
        middleJPanelLayout.setVerticalGroup(
            middleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(middleJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(middleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(assignmentLabel)
                    .addComponent(attemptLabel)
                    .addComponent(assignmentGradeTextLabel)
                    .addComponent(assignmentGradeLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(middleJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gradeTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 976, Short.MAX_VALUE)
                    .addComponent(assignmentScrollPane)
                    .addComponent(topJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bottomJPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(assignmentScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(middleJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gradeTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel assignmentGradeLabel;
    private javax.swing.JLabel assignmentGradeTextLabel;
    private javax.swing.JLabel assignmentLabel;
    private javax.swing.JScrollPane assignmentScrollPane;
    private javax.swing.JTree assignmentTree;
    private javax.swing.JLabel attemptLabel;
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JTable gradeTable;
    private javax.swing.JScrollPane gradeTableScrollPane;
    private javax.swing.JPanel middleJPanel;
    private javax.swing.JLabel studentNameLabel;
    private javax.swing.JPanel topJPanel;
    // End of variables declaration//GEN-END:variables
}
