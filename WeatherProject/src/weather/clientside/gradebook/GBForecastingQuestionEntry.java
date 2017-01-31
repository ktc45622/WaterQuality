package weather.clientside.gradebook;

import weather.common.data.forecasterlesson.Question;

/**
 * This class contains information about an assignment's question. Contains the
 * question as displayed in a Forecasting Lesson.
 *
 * @author Nikita Maizet
 */
public class GBForecastingQuestionEntry implements GBQAEntry {

    int questionNumber;
    boolean questionCountsForGrade;
    Question question;

    /**
     * Creates instance of class.
     *
     * @param question Question object containing data for question
     */
    public GBForecastingQuestionEntry(Question question) {
        this.question = question;
    }

    @Override
    public Object getEntryObject() {
        return question;
    }

    /**
     * Gets data key for question.
     *
     * @return String
     */
    public String getDataKey() {
        return question.getDataKey();
    }

    @Override
    public int getPointsEarned() {
        return 3;
    }

    @Override
    public double getPercentage() {
        return 0.0;
    }

    @Override
    public String getEntryID() {
        return question.getQuestionID();
    }

    @Override
    public String getEntryText() {
        return question.getQuestionText();
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
