package weather.clientside.gradebook;

import java.util.Calendar;

/**
 * This interface is the template for an assignment or lesson entry in the 
 * gradebook. It has methods to set and retrieve basic grade information about
 * an entry include the amount of points the entry is worth, points earned,
 * the letter grade, and score as a percentage.
 * 
 * @author Nikita Maizet
 */

public interface GBLessonEntry {

    /**
     * Loads data for this entry that will be needed.
     */
    public void loadData();
    
    /**
     * Gets the name of lesson.
     * 
     * @return String
     */
    public String getEntryName();
    
    /**
     * Gets raw score earned for this lesson.
     * 
     * @return int
     */
    public int getPointsEarned();

    /**
     * Gets score earned for this lesson as a percentage.
     * 
     * @return double
     */
    public double getPercentage();
    
    /**
     * Gets date assignment was submitted.
     * 
     * @return String
     */
    public String getDateSubmitted();
    
    /**
     * Gets date assignment is due.
     * 
     * @return String
     */
    public String getDateDue();
    
    /**
     * Sets the date assignment is due.
     * 
     * @param calendar 
     */
    public void setDateDue(Calendar calendar);
    
    /**
     * Gets this assignment's status.
     * 
     * @return String
     */
    public String getAssignmentStatus();
}
