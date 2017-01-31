package weather.common.data.forecasterlesson;

import java.util.ArrayList;

/**
 * This class contains the contents and details of a <code>Question</code> an
 * instructor will add to a <code>ForecasterLesson</code>. It uses a
 * <code>QuestionTemplate</code> which defines the format of the
 * <code>Question</code> and the test for the <code>Question</code>. The
 * <code>Question</code> specifies the details the <code>QuestionTemplate</code>
 * leaves to the <code>Question</code>. The <code>QuestionTemplate</code> of a
 * given <code>Question</code> cannot be changed or replaced once the
 * <code>Question</code> is created but methods in this class do allow a given
 * <code>Question</code> to provide access to the information in its
 * <code>QuestionTemplate</code>.
 *
 * @author Joshua Whiteman
 */

public class Question {

    /**
     * The database identifier for the question.
     */
    private String questionID;
    /**
     * The number of the question within its <code>ForecasterLesson</code>.
     * The first question of a lesson is numbered 1.
     */
    private int questionNumber;
    
    /**
     * The time of the question given in zulu (UTC).
     */
    private String questionZulu;
    
    /**
     * The answers permitted for the question, with can be maximum and minimum
     * values for text input questions.
     */
    private ArrayList<Answer> answers;
   
    /**
     * The <code>QuestionTemplate</code> that wraps all of the final fields of
     * the question.
     */
    private final QuestionTemplate questionTemplate;

    /**
     * Constructor that will be used by the DBManager to create a Question
     * object. Do not use this as a while building the GUIs. This will not store
     * the data within the database.
     * 
     * @param questionZulu the zulu time for the question
     * @param questionID the database identifier for question
     * @param questionTemplate a <code>QuestionTemplate</code> containing
     * necessary fields for the class
     */
    public Question(String questionZulu, String questionID, 
            QuestionTemplate questionTemplate) {
        this.questionZulu = questionZulu;
        this.questionID = questionID;
        this.questionTemplate = questionTemplate;
    }

    /**
     * Gets the database identifier for this instance of a Question.
     * 
     * @return database identifier for a <code>Question</code>
     */
    public String getQuestionID() {
        return questionID;
    }

    /**
     * Sets the <code>Question</code> database identifier.
     * 
     * @param questionID the database identifier.
     */
    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    /**
     * Gets the <code>ArrayList</code> of type <code>Answer</code> that holds
     * all possible choices for the <code>Question</code>.
     * 
     * @return The <code>ArrayList</code> of type <code>Answer</code> that holds
     * all possible choices for the <code>Question</code>.
     */
    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    /**
     * Returns the data key value for question. This is where to look in grading
     * data for the right answer. It is made of a keyword form the
     * <code>QuestionTemplate</code> and a zulu time when needed. Ex: A CLOUD_11 
     * data key specifies the question type and the zulu time for it.
     * 
     * @return The data key value for question. This is where to look in grading
     * data for the right answer. It is made of a keyword form the
     * <code>QuestionTemplate</code> and a zulu time when needed. Ex: A CLOUD_11 
     * data key specifies the question type and the zulu time for it.
     */
    public String getDataKey() {
        if(questionTemplate.getDataKey().equals("PRECIP") 
                || questionTemplate.getDataKey().equals("FAVOR_PRECIP")) {
            return questionTemplate.getDataKey();
        }
        if(questionZulu != null) {
            return questionTemplate.getDataKey() + "_" + questionZulu;
        } else {
            return questionTemplate.getDataKey();
        }
    }
    
    /**
     * Sets the <code>ArrayList</code> of type <code>Answer</code> to that which
     * is provided.  These are all the possible choices for the 
     * <code>Question</code>.
     * 
     * @param answers The provided <code>ArrayList</code>.
     */
    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
    
    /**
     * Returns the question text from the <code>QuestionTemplate</code> with the
     * correct zulu time substituted for the placeholder "%s" if the zulu time 
     * time is not null.
     * 
     * @return The question text from the <code>QuestionTemplate</code> with the
     * correct zulu time substituted for the placeholder "%s" if the zulu time
     * time is not null.
     */
    public String getQuestionText() {
        if(questionZulu != null)
        {
            String s = questionZulu + "Z";
            return questionTemplate.getText().replace("%s", s);
        } else {
            return questionTemplate.getText();
        }
    }
    
    /**
     * Returns the question name from the <code>QuestionTemplate</code> with the
     * correct zulu time substituted for the placeholder "%s" if the zulu time 
     * time is not null.
     * 
     * @return The question name from the <code>QuestionTemplate</code> with the
     * correct zulu time substituted for the placeholder "%s" if the zulu time
     * time is not null.
     */
    public String getQuestionName() {
        if(questionZulu != null)
        {
            String s = questionZulu + "Z";
            return questionTemplate.getQuestionName().replace("%s", s);
        } else {
            return questionTemplate.getQuestionName();
        }
    }
    
    /**
     * Returns the hyperlink text from the <code>QuestionTemplate</code> with
     * the correct zulu time substituted for the placeholder "%s" if the zulu
     * time time is not null. This is designed to be text a user can click on to
     * get more information.
     * 
     * @return The hyperlink text from the <code>QuestionTemplate</code> with
     * the correct zulu time substituted for the placeholder "%s" if the zulu
     * time time is not null. This is designed to be text a user can click on to
     * get more information.
     */
    public String getURLText() {
        if(questionZulu != null)
        {
            String s = questionZulu + "Z";
            return questionTemplate.getURLText().replace("%s", s);
        } else {
            return questionTemplate.getURLText();
        }
    }
    
    /**
     * Returns the location on a preset web page where the user can get more
     * information about the <code>Question</code>. It is obtained for the 
     * <code>QuestionTemplate</code>.
     * 
     * @return The location on a preset web page where the user can get more
     * information about the <code>Question</code>. It is obtained for the 
     * <code>QuestionTemplate</code>.
     */
    public String getURLLocation() {
        return questionTemplate.getURLLocation();
    }
    
    /**
     * Gets the <code>AnswerType</code> for the <code>Question</code> which 
     * specifies how the question how the <code>Question</code> will be 
     * answered. This is obtained for the <code>QuestionTemplate</code>.
     * 
     * @return The <code>AnswerType</code> for the <code>Question</code>.
     */
    public AnswerType getAnswerType() {
        return questionTemplate.getAnswerType();
    }

    /**
     * Gets the Zulu time for the <code>Question</code>.
     * 
     * @return the Zulu time for the <code>Question</code>.
     */
    public String getQuestionZulu() {
        return questionZulu;
    }

    /**
     * Gets the Zulu time for the <code>Question</code>.
     * 
     * @param questionZulu the Zulu time for the <code>Question</code>.
     */
    public void setQuestionZulu(String questionZulu) {
        this.questionZulu = questionZulu;
    }

    /**
     * Gets the <code>QuestionTemplate</code> for the <code>Question</code>.
     * 
     * @return the <code>QuestionTemplate</code> for the <code>Question</code>.
     */
    public QuestionTemplate getQuestionTemplate() {
        return questionTemplate;
    }

    /**
     * Gets the number indicating where this <code>Question</code> is in the
     * <code>Question</code> list of its <code>ForecasterLesson</code>. The
     * first number used by the list is 1.
     * 
     * @return The number indicating where this <code>Question</code> is in the
     * <code>Question</code> list of its <code>ForecasterLesson</code>. The
     * first number used by the list is 1.
     */
    public int getQuestionNumber() {
        return questionNumber;
    }

    /**
     * Sets the number indicating where this <code>Question</code> is in the
     * <code>Question</code> list of its <code>ForecasterLesson</code>. The
     * first number used by the list is 1.
     * 
     * @param questionNumber The number indicating where this
     * <code>Question</code> is in the <code>Question</code> list of its
     * <code>ForecasterLesson</code>. The first number used by the list is 1.
     */
    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }
    
    /**
     * Gets a String representation for the <code>Question</code> to be used
     * during testing.
     *
     * @return a String representation for the <code>Question</code> to be used
     * during testing.
     */
    @Override
    public String toString() {
        String answersText = "";
        for(int i = 0; i < answers.size(); i++)
        {
            answersText += "     - "+answers.get(i)+"\n";
        }
        return "Question{" + "questionID=" + questionID + ", questionTemplate=" 
                + questionTemplate + ", " + getQuestionText() + '}' +
                "\n" + answersText;
    }
}
