package weather.common.data.forecasterlesson;

/**
 * This interface is a representation of an answer to a <code>Question</code> 
 * for a lesson. This class contains all the information needed to display the
 * choices a student will have when responding to a Question.
 * 
 * @author bjt32865
 * @author Joshua Whiteman
 */
public class Answer{

   
    private String answerID;
    private String answerText;        // actual answer
    private String answerValue;       // compare to data key in database
    private Question question;

    /**
     * Constructor that will be used to create a Answer object.
     * 
     * @param answerID database id
     * @param answerText text to display to student
     * @param answerValue compare value against database values for correctness
     * @param q question for which this is an answer.
     */
    public Answer(String answerID, 
             String answerText, String answerValue, Question q) {
        this.answerID = answerID;
        this.answerText = answerText;
        this.answerValue = answerValue;
        this.question = q;
    }
    
    /**
     * DB identifier for the current answer.
     * 
     * @return an int representing the ID of the answer stored in the database.
     */
    public String getAnswerID(){
        return answerID;
    }

    /**
     * The text that will be displayed to the student.
     * @return String text
     */
    public String getAnswerText() {
        return answerText;
    }

    /**
     * Value representation of the Answer that will be compared to the actual
     * Answer during grading.
     * @return the answer value in String form
     */
    public String getAnswerValue() {
        return answerValue;
    }

    /**
     * The <code>Question</code> belonging to the Answer.
     * @return a Question object belonging to the Answer
     */
    public Question getQuestion() {
        return question;
    }
    
    /**
     * String representation of the Answer used during testing.
     * @return String representation of the Answer used during testing.
     */
     @Override
    public String toString() {
        return "Answer{" + "answerID=" + answerID
                + ", answerText=" + answerText + ", answerValue=" + answerValue 
                + '}';
    }
}
