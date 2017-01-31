package weather.clientside.gradebook;

/**
 * This interface is the template for a gradebook lesson entry's question and/or
 * answer classes. 
 * It has methods to retrieve information about it including: grade information,
 * whether the question counted for a grade, entry number, the entry
 * object.
 * 
 * @author Nikita Maizet
 */
public interface GBQAEntry {
    
    /**
     * Gets object containing data for this entry.
     * 
     * @return Object
     */
    public Object getEntryObject();
    
    /**
     * Gets raw score earned for this lesson.
     * 
     * @return double
     */
    public int getPointsEarned();

    /**
     * Gets score earned for this lesson as a percentage.
     * 
     * @return double
     */
    public double getPercentage();
    
    /**
     * Returns what number this question is in assignment.
     * 
     * @return String
     */
    public String getEntryID();
    
    
    /**
     * Returns the text making up the entry, such as a the question itsself.
     * 
     * @return String 
     */
    public String getEntryText();
    

    /**
     * Sets the lesson points earned earned to specified value.
     * 
     * @param pointsEarned 
     */
    public void setPointsEarned(int pointsEarned);
    
    /**
     * Sets total possible points to earn for this lesson.
     * 
     * @param pointsPossible 
     */
    public void setPointsPossible(int pointsPossible);

}
