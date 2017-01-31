package weather.common.data.forecasterlesson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A Response is a representation of an attempt a student makes on an Answer.
 * While an <code>Answer</code> has different types, a Response will only have 
 * one. The reasoning is, the response data is stored in a list of answers. 
 * 
 * @author Joshua Whiteman
 */
public class Response {
    
    private final String responseID;
    private ArrayList<Answer> answers;
    private Score responseScore;

    /**
     * Constructor that will be used by the DBManager to create a Response 
     * object. Do not use this as a while building the GUIs. This will not store
     * the data within the database.
     * 
     * @param responseID the Id of the response in the database
     * @param answerList a list of the answers for the response. A list for
     * answers with checkboxes
     */
    public Response(String responseID, ArrayList<Answer> answerList) {
        this.responseID = responseID;
        this.answers = answerList;
        this.responseScore = new Score(-1, -1);
    }
    
    /**
     * Returns the ID of this response.
     * 
     * @return the ID of the response
     */
    public String getResponseID() {
        return responseID;
    }

    /**
     * Gets the score for the Attempt.
     * 
     * Example: 3 correct, 1 partially correct, 0 not attempted
     * 
     * @return a <Score> representing how well the Response matched the <Answer>
     */
    public Score getResponseScore() {
        return responseScore;
    }
    
    /**
     * Returns answer objects for this response sorted by their value;
     * 
     * @return sorted Answers
     */
    public ArrayList<Answer> getAnswers() {
        if (answers != null) {
            Collections.sort(answers, new Comparator<Answer>() {

                @Override
                public int compare(Answer answer1, Answer answer2) {
                    return answer1.getAnswerValue().compareTo(answer2
                            .getAnswerValue());
                }
                
            });
        }
        
        return answers;
    }
    
    /**
     * Sets the list of answers for the response.
     * 
     * @param answers 
     */
    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    /**
     * Sets the score for the Attempt. This should occur during the grading
     * process.
     * 
     * @param s the score for the response
     */
    public void setResponseScore(Score s) {
        this.responseScore = s;
    }

    /**
     * Public method used for testing only that displays the response values.
     * 
     * @return a String representing the Response
     */
    @Override
    public String toString() {
        return "Response{" + "responseID=" + responseID + ", answer=" + answers 
                + ", responseScore=" + responseScore + '}';
    }
}
