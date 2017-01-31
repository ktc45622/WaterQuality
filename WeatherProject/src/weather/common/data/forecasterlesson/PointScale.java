package weather.common.data.forecasterlesson;

/**
 * This class will only be used by the <code>ForecasterLesson<code/>. It will be
 * used to encapsulate all of the point data for the Lesson.
 *
 * @author Joshua Whiteman
 */
public class PointScale {
    private String id;
    private int correctPoints;  // full credit
    private int incorrectPoints;  // answered incorrectly
    private int unansweredPoints; // left blank
    private int topScoreCounted; // amount of attempts counted towards grade
    private boolean requireAnswers; //determines if answers can be left blank


    /**
     * Constructor that will be used by the DBManager to create a PointScale
     * object. Do not use this as a while building the GUIs. This will not store
     * the data within the database..
     *
     * @param id The database id of the object (1A if not in database).
     * @param correctPoints The points for a correct answer.
     * @param incorrectPoints The points for an incorrect answer.
     * @param unansweredPoints The points for no answer.
     * @param topScoreCounted The amount of attempts counted towards the grade.
     * @param requireAnswers True if answers are required; false otherwise.
     */
    public PointScale(String id, int correctPoints, int incorrectPoints,
            int unansweredPoints, int topScoreCounted, boolean requireAnswers) {
        this.id = id;
        this.correctPoints = correctPoints;
        this.incorrectPoints = incorrectPoints;
        this.unansweredPoints = unansweredPoints;
        this.topScoreCounted = topScoreCounted;
        this.requireAnswers = requireAnswers;
    }

    /**
     * Gets the amount of points for a correct answer.
     * @return The correct points for an answer.
     */
    public int getCorrectPoints() {
        return correctPoints;
    }

    /**
     * Gets the amount of points for an incorrect answer.
     * @return The incorrect points for an answer.
     */
    public int getIncorrectPoints() {
        return incorrectPoints;
    }

    /**
     * Gets the amount of points for an unanswered answer.
     * @return The points for no answer.
     */
    public int getUnansweredPoints() {
        return unansweredPoints;
    }

    /**
     * Gets the top scores counted for the lesson.
     * @return The number of top scores counted.
     */
    public int getTopScoreCounted() {
        return topScoreCounted;
    }

    /**
     * Gets the db id for the PointScale object.
     * @return A database identifier.
     */
    public String getPointScaleId() {
        return id;
    }

    /**
     * Gets whether or not answers are required.
     * @return True if answers are required; false otherwise.
     */
    public boolean getRequireAnswers() {
        return requireAnswers;
    }

    /**
     * Gets a String representation of the PointScale object used in testing.
     * @return A String representation of the PointScale object used in testing.
     */
    @Override
    public String toString() {
        return "PointScale{" + "correctPoints=" + correctPoints
                + ", partialPoints=" + incorrectPoints
                + ", unansweredPoints=" + unansweredPoints
                + ", topScoreCounted=" + topScoreCounted
                + ", requireAnswers=" + requireAnswers + '}';
    }


}
