package weather.common.data.forecasterlesson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import weather.common.data.User;

/**
 * This class is a collection of the student's <Response>s to the 
 * ForecasterLesson.
 * 
 * This is a basic class that does nothing more than hold an ArrayList<Response>.
 * 
 * Iteration of this collection is the responsibility of the implementer. If
 * one wants to edit a <Response>, the list must be pulled out, searched
 * through, and edited.
 * 
 * @author Joshua Whiteman
 */
public class Attempt {
    
    private String attemptID;
    private String stationCode;
    private User student;
    private ArrayList<Response> responses;
    private Calendar attemptDate;
    private boolean isScoreModified;
    
    /**
     * The score object for this instance will initially reflect that the lesson
     * is ungraded.  To change it, calculateAttemptScore() must be called after
     * all responses are added.
     */
    private Score score = new Score(-1, -1);
    
    /**
     * Used to creates a new Attempt in the database. Sets the initial attemptID
     * value to the required for the DB manager. All responses should be set
     * along with the station code being used.
     * 
     * @param responses list of responses to be placed in the database
     * @param student the login information
     * @param stationCode the four char code for the station
     */
    public Attempt(ArrayList<Response> responses, User student, String stationCode) {
        this.attemptID = "1A";
        this.responses = responses;
        this.student = student;
        this.attemptDate = Calendar.getInstance();
        this.stationCode = stationCode;
    }
    
    /**
     * Constructor that will be used by the DBManager to create a Attempt 
     * object. 
     * 
     * @param attemptID the database ID for the attempt
     * @param stationCode the station code
     * @param u the current user
     * @param attemptDate the date the attempt was made
     */
    public Attempt(String attemptID, String stationCode, User u, 
            Date attemptDate) {
        this.attemptID = attemptID;
        this.stationCode = stationCode;
        this.responses = new ArrayList<>();
        this.student = u;
        this.attemptDate = Calendar.getInstance();
        this.attemptDate.setTime(attemptDate);
    }

    /**
     * Gives read access to the caller of the list of Responses per Attempt.
     * 
     * @return the list of responses
     */
    public ArrayList<Response> getResponses() {
        return responses;
    }
    
    /**
     * Returns the <code>Score</code> of this attempt.
     * 
     * @return The <code>Score</code> of this attempt.
     */
    public Score getScore() {
        return score;
    }
    
    /**
     * Checks if attempt object has ever been graded by checking if amount of
     * points earned in its Score object is set to the default value of -1.
     *
     * @return true if attempt has received a grade, false otherwise.
     */
    public boolean hasBeenGraded() {
        if (score.getPointsEarned() < 0) {
            return false;
        }
        return true;
    }

    /**
     * Calculates and sets score of this.Attempt object by adding up all points
     * earned on every individual response.
     */
    public void calculateAttemptScore() {
        int ptsEarned = 0;
        int ptsPossible = responses.get(0).getResponseScore()
                .getPointsPossible() * responses.size();

        for (Response r : responses) {
            ptsEarned += r.getResponseScore().getPointsEarned();
        }

        if (ptsPossible < 0) {
            ptsPossible = -1;
            ptsEarned = -1;
        }

        score.setPointsEarned(ptsEarned);
        score.setpointsPossible(ptsPossible);
    }
    
    /**
     * Gives the database identifier for this Attempt.
     * 
     * @return gives the database identifier for this Attempt
     */
    public String getAttemptID() {
        return attemptID;
    }
    
    /**
     * Allows the user to set the attempt ID so it may be updated in the 
     * database with new values.
     * 
     * @param attemptID database ID. Should be directly retrieved from the
     * database
     */
    public void setAttemptID(String attemptID) {
        this.attemptID = attemptID;
    }
    
    /**
     * Sets the date for the Attempt.
     * 
     * @param attemptDate a Calendar date for the attempt
     */
    public void setAttemptDate(Calendar attemptDate) {
        this.attemptDate = attemptDate;
    }
    
    /**
     * Adds a <Response> at the specified index. The Response at the current
     * index will be moved forward. To place response at the end of the list,
     * index = -1.
     * 
     * @param index specifies the position to add the <Response>. -1 for end of
     * list.
     * @param response the <Response> to be added to the list
     */
    public void addResponse(int index, Response response) {
        responses.add(index, response);
    }
    
    /**
     * Gets the date the Attempt was taken.
     * @return a Calendar date
     */
    public Calendar getAttemptDate() {
        return attemptDate;
    }
    
    /**
     * Gets the date for which the attempt was made.
     * @return a Calendar date
     */
    public Calendar getForecastedDate() {
        Calendar forecastedDate = new GregorianCalendar();
        forecastedDate.setTimeInMillis(attemptDate.getTimeInMillis());
       
        //Desired date is the day after the attempt was made.
        forecastedDate.add(Calendar.DATE, 1);
        return forecastedDate;
    }

    /**
     * Gets the station code for the Attempt
     * @return a four char station code
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * Gets the user that made a Forecast
     * @return the User that made the forecast
     */
    public User getStudent() {
        return student;
    }

    /**
     * A String representation of the Attempt.
     * @return a String representation of the Attempt.
     */
    @Override
    public String toString() {
        String responseText = "";
        for(int i = 0; i < responses.size(); i++)
        {
            responseText += "     - "+responses.get(i) + "\n";
        }
        return "Attempt{" + "attemptID=" + attemptID + ", stationCode=" + 
                stationCode + "attemptDate=" + attemptDate + 
                ", isScoreModified=" + isScoreModified + "\n" + responseText;
    }
}
