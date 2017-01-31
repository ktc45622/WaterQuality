package weather.clientside.gradebook;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import weather.ApplicationControlSystem;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.Response;
import weather.common.data.forecasterlesson.Score;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;

/**
 * This class contains details about a gradebook lesson attempt entry. Contains
 * responses to this attempt by student. Getters for various grade data.
 * 
 * @author Nikita Maizet
 */
public class GBForecastingAttemptEntry implements GBLessonEntry{
    private Attempt attempt;
    private String parentLessonDateDue;
    private int attemptNumber;
    private ArrayList<GBForecastingResponseEntry> responses;
    
    /**
     * Creates instance of class.
     * 
     * @param appControl ApplicationControlSystem instance
     * @param attempt Attempt object for this attempt
     * @param parentLessonDate due date of lesson this attempt is for
     * @param attemptNumber attempt number
     */
    public GBForecastingAttemptEntry(ApplicationControlSystem appControl, Attempt attempt, 
            String parentLessonDate, int attemptNumber) {
        this.attempt = attempt;
        this.parentLessonDateDue = parentLessonDate;
        this.attemptNumber = attemptNumber + 1;
        this.responses = new ArrayList<>();
        
        loadData();
    }
    
    @Override
    public final void loadData() {
        ArrayList<Response> tempResponse = attempt.getResponses();
        
        for(Response r : tempResponse) {
            responses.add(new GBForecastingResponseEntry(r));
        }
    }
    
    /**
     * Returns the possible points for this attempt.
     */
    public int getPossiblePoints() {
        return ((Response) responses.get(0).getEntryObject()).getResponseScore()
                .getPointsPossible() * responses.size();
    }
    
    /**
     * Gets Attempt object. 
     * 
     * @return Attempt
     */
    public Attempt getAttemptObj() {
        return attempt;
    }
    
    /**
     * Gets list of all responses by student for this attempt.
     * 
     * @return ArrayList<GBForecastingResponseEntry>
     */
    public ArrayList<GBForecastingResponseEntry> getResponses() {
        return responses;
    }
    
    /**
     * Gets id of Attempt object.
     * 
     * @return String
     */
    public String getAttemptID() {
        return attempt.getAttemptID();
    }
    
    /**
     * Checks if attempt object has ever been graded by checking if amount
     * of points earned in its Score object is set to the default value of -1.
     * 
     * @return true if attempt has received a grade, false otherwise. 
     */
    public boolean hasBeenGraded() {
        return attempt.hasBeenGraded();
    }

    @Override
    public String getEntryName() {
        return "Forecast " + attemptNumber;
    }

    @Override
    public int getPointsEarned() {
        if(hasBeenGraded()) {
            return attempt.getScore().getPointsEarned();
        }
        return 0;
    }

    @Override
    public double getPercentage() {
        Score score = attempt.getScore();
        if(score.getPointsEarned() > 0) {
            return Double.parseDouble(new DecimalFormat("#.#")
                    .format((((double)score.getPointsEarned()) 
                    / ((double) score.getPointsPossible())) * 100.0));
        }
        return 0.0;
        
    }

    @Override
    public String getDateSubmitted() {
        return parseDate(attempt.getAttemptDate());
    }
    
    /**
     * Returns the date forecasted as a <code>String</code>.
     * @return the date forecasted as a <code>String</code>.
     */
    public String GetDateForecasted() {
        return parseDate(attempt.getForecastedDate());
    }
    
    /**
     * Gets date attempt was submitted.
     * 
     * @return Calendar
     */
    public Calendar getDateSubmittedCalendar() {
        return attempt.getAttemptDate();
    }
    
    /**
     * Gets date to be used when searching for station data used to grade attempt.
     * Since forecasts are made for the following day's weather, and extra 
     * day is added to the date attempt was completed.
     * 
     * @return Calendar
     */
    public Calendar getStationDate() {
        Calendar cal = (Calendar) attempt.getAttemptDate().clone();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        
        return cal;
    }

    @Override
    public String getDateDue() {
        return parentLessonDateDue;
    }

    @Override
    public void setDateDue(Calendar calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAssignmentStatus() {
        if(hasBeenGraded()) {
            return "Completed";
        }
        Calendar dateToCheck = attempt.getForecastedDate();
        long daysOld = (ResourceTimeManager
                .getStartOfDayFromMilliseconds(System.currentTimeMillis(),
                TimeZone.getDefault()) - dateToCheck.getTimeInMillis())
                / ResourceTimeManager.MILLISECONDS_PER_DAY;
        if (daysOld <= Integer.parseInt(
                PropertyManager.getGeneralProperty("MAXIMUM_GRADING_DAYS"))) {
            return "Grading Pending";
        } else {
            return "Unable To Grade";
        }
    }
    
    /**
     * Parses date to format ex: Jan 21 2013.
     * 
     * @param cal object to be parsed
     * @return String containing date in specified format.
     */
    public static String parseDate(Calendar cal) {
        String date = "";
        
        switch (cal.get(Calendar.MONTH)) {
            case 0 : date += "Jan";
                break;
            case 1 : date += "Feb";
                break;    
            case 2 : date += "Mar";
                break;  
            case 3 : date += "Apr";
                break;
            case 4 : date += "May";
                break;
            case 5 : date += "Jun";
                break;
            case 6 : date += "Jul";
                break;
            case 7 : date += "Aug";
                break;
            case 8 : date += "Sep";
                break;
            case 9 : date += "Oct";
                break;
            case 10 : date += "Nov";
                break;
            case 11 : date += "Dec";
                break;
            default : date += "";
                break;
        }
        
        date += " "+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.YEAR);
        
        return date;
    }
}
