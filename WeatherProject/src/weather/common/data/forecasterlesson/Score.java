package weather.common.data.forecasterlesson;

/**
 * This is a score that is created by grading an attempt with a 
 * ForecasterLesson. It will be used by the <code>Gradebook</code> to store 
 * answers.
 * 
 * @author Nikita Maizet
 */
public class Score {
    private String scoreID;
    private int pointsEarned;
    private int pointsPossible;

    /**
     * Initializes the score to the pointsEarned and possiblePoints for
     * the lesson. Used when creating a lesson.
     * 
     * @param pointsEarned The points earned for this lesson.
     * @param pointsPossible The maximum possible points for this lesson.
     */
    public Score(int pointsEarned, int pointsPossible) {
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
        this.scoreID = "1A";
    }

    /**
     * Constructor that will be used to create a Score object.
     * 
     * @param scoreID
     * @param pointsEarned
     * @param pointsPossible 
     */
    public Score(String scoreID, int pointsEarned, int pointsPossible) {
        this.scoreID = scoreID;
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
    }

    /**
     * Gets the amount points earned.
     * 
     * @return The total points earned.
     */
    public int getPointsEarned() {
        return pointsEarned;
    }

    /**
     * Gets the maximum amount of points possible.
     * 
     * @return The maximum total points that can be earned.
     */
    public int getPointsPossible() {
        return pointsPossible;
    }
    
    /**
     * Gets the score percentage by dividing the points that the 
     * student earned by the maximum possible points for the lesson.
     * 
     * @return The percentage grade for the lesson.
     */
    public double getPercentage() {
        return ((double) pointsEarned / pointsPossible) * 100;
    }
    
    /**
     * Sets points earned for lesson to specified value.
     * 
     * @param pointsEarned the points that the student will receive for the
     * Attempt / Response.
     */
    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
    
    /**
     * Sets total points possible to specified value.
     * 
     * @param pointsPossible the maximum amount of points a student may get on
     * the Attempt / Response.
     */
    public void setpointsPossible(int pointsPossible) {
        this.pointsPossible = pointsPossible;
    }

    /**
     * Gets the database id for the score.
     * @return the database id for the score.
     */
    public String getScoreID() {
        return scoreID;
    }

    /**
     * A String representation of the Score used for testing.
     * @return a String representation of the Score used for testing.
     */
    @Override
    public String toString() {
        return "Score{" + "scoreID=" + scoreID + ", pointsEarned=" + 
                pointsEarned + ", pointsPossible=" + pointsPossible + '}';
    }


    
}
