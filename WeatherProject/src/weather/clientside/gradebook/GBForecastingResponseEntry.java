package weather.clientside.gradebook;

import java.text.DecimalFormat;
import weather.common.data.forecasterlesson.Response;

/**
 * Contains data about a student's response to a particular question.
 * 
 * @author Nikita Maizet
 */
public class GBForecastingResponseEntry implements GBQAEntry{
    
    private Response response;
    
    /**
     * Creates instance of class.
     * 
     * @param response Response object containing student's answer data
     */
    public GBForecastingResponseEntry(Response response) {
        this.response = response;
    }
    
    /**
     * Determines if a response has been graded by checking if its Score object
     * is null.
     * 
     * @return boolean - true if has been graded, false otherwise
     */
    public boolean hasBeenGraded() {
        if(response.getResponseScore() == null) {
            return false;
        }
        return true;
    }
    
    @Override
    public Object getEntryObject() {
        return response;
    }
    
    /**
     * Gets Response object.
     * 
     * @return Response
     */
    public Response getResponse() {
        return response;
    }

    @Override
    public String getEntryText() {
        return response.getAnswers().get(0).getAnswerText();
    }
    
    @Override
    public int getPointsEarned() {
        return response.getResponseScore().getPointsEarned();
    }

    @Override
    public double getPercentage() {
        return Double.parseDouble(new DecimalFormat("#.#").format(
                (((double) response.getResponseScore().getPointsEarned())
                / ((double) response.getResponseScore().getPointsPossible())) * 100));
    }

    /**
     * Gets Response objects database ID.
     * 
     * @return String
     */
    @Override
    public String getEntryID() {
        return response.getResponseID();
    }
    
    @Override
    public void setPointsEarned(int pointsEarned) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPointsPossible(int pointsPossible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
