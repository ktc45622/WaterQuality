package weather.clientside.gradebook;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import weather.ApplicationControlSystem;
import weather.common.data.User;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.PointScale;
import weather.common.data.forecasterlesson.Question;
import weather.common.dbms.DBMSForecasterAttemptManager;

/**
 * This class contains details about a gradebook lesson entry. This class
 * implements the GBLessonEntry interface and has methods for retrieving grade
 * and general information about an assignment. It contains all the attempts
 * (GBForecastingLessonAttemptEntry) of this lesson by the student
 * (GBStudentEntry) object that instantiated.
 *
 * @author Nikita Maizet
 */
public class GBForecastingLessonEntry implements GBLessonEntry {

    private ApplicationControlSystem appControl;
    private User student;
    private ForecasterLesson lesson;
    private ArrayList<GBForecastingAttemptEntry> attempts; // all attempts for this lesson
    private ArrayList<GBForecastingQuestionEntry> questions;

    /**
     * Using the supplied ForecasterLesson object will get information about
     * this lesson by calling the loadData method.
     * 
     * @param appControl ApplicationControlSystem instance
     * @param lesson ForecasterLesson used to obtain data for this instance
     * @param student Student loading lesson data for
     */
    public GBForecastingLessonEntry(ApplicationControlSystem appControl, ForecasterLesson lesson, User student) {
        this.appControl = appControl;
        this.student = student;
        this.lesson = lesson;
        attempts = new ArrayList<>();
        questions = new ArrayList<>();
        
        loadData();
    }

    /**
     * Returns the lesson ID of this lesson.
     *
     * @return String
     */
    public String getEntryID() {
        return lesson.getLessonID();
    }
    
    /**
     * Return the <code>ForecasterLesson</code> object.
     * 
     * @return The entry object 
     */
    public ForecasterLesson getLessonbj() {
        return lesson;
    }

    @Override
    public final void loadData() {
        DBMSForecasterAttemptManager attemptManager = appControl.getDBMSSystem()
                .getForecasterAttemptManager();

        ArrayList<Attempt> tempAttempts = new ArrayList<>();
        ArrayList<Question> tempQuestions = new ArrayList<>();

        // get all attempts by the student who this lesson entry class belongs
        // to for this lesson
        tempAttempts.addAll(attemptManager.getAttempts(lesson, student));
        tempQuestions.addAll(lesson.getQuestions());

        for (int i = 0; i < tempAttempts.size(); i++) {
            Attempt thisAttempt = tempAttempts.get(i);
            attempts.add(new GBForecastingAttemptEntry(appControl, 
                    thisAttempt, getDateDue(), i));
        }

        for (Question q : tempQuestions) {
            questions.add(new GBForecastingQuestionEntry(q));
        }
    }

    public PointScale getPointScale() {
        return lesson.getPointScale();
    }

    /**
     * Gets array list of attempts for this lesson.
     *
     * @return array list containing GRForecastingAttemptEntry objects
     */
    public ArrayList<GBForecastingAttemptEntry> getAttempts() {
        return attempts;
    }

    /**
     * Checks whether the lesson has any graded attempts or not.
     *
     * @return true if the lesson has any graded attempts; false otherwise
     */
    public boolean hasGradedAttempts() {
        if (attempts.size() > 0) {
            for (GBForecastingAttemptEntry a : attempts) {
                if (a.hasBeenGraded()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Number of attempts counted towards grade.
     *
     * @return integer of number of attempts counted towards grade
     */
    public int getTopScoresCounted() {
        return lesson.getPointScale().getTopScoreCounted();
    }

    @Override
    public String getEntryName() {
        return lesson.getLessonName();
    }

    /**
     * Returns the highest score of an attempt for this lesson if available.
     *
     * @return int
     */
    @Override
    public int getPointsEarned() {
        return getHighestAttemptsScore(attempts);
    }

    @Override
    public double getPercentage() {
        return getHighestAttemptsPercentage(attempts);
    }

    @Override
    public String getDateSubmitted() {
        return " "; // requested to not display date of recent submission
    }

    @Override
    public String getDateDue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(lesson.getLessonEndDate());
        return GBForecastingAttemptEntry.parseDate(cal);
    }

    /**
     * Gets questions for lesson.
     *
     * @return array list of GBForecastingQuestionEntry objects
     */
    public ArrayList<GBForecastingQuestionEntry> getQuestions() {
        return questions;
    }

    @Override
    public void setDateDue(Calendar calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Displays the number of attempts user has left for this lesson.
     * 
     * @return String
     */
    @Override
    public String getAssignmentStatus() {
        return (lesson.getMaximumTries()
                - attempts.size()) + " Tries Remaining";
    }

    private int getHighestAttemptsScore(ArrayList<GBForecastingAttemptEntry> attempts) {
        int score = 0;
        ArrayList<Integer> allScores = new ArrayList<>();

        // add all scores to arrayList
        for (int i = 0; i < attempts.size(); i++) {
            if (attempts.get(i).hasBeenGraded()) {
                allScores.add(attempts.get(i).getPointsEarned());
            }
        }

        // sort by ascending order
        Collections.sort(allScores);

        int n = 0;

        // if there are more attempt scores than the number counted for grade
        if (allScores.size() > getTopScoresCounted()) {
            n = allScores.size()
                    - getTopScoresCounted(); // set new stopping point, counting from top
        }
        // add up the n largest elements to return as score
        for (int i = allScores.size() - 1; i >= n; i--) {
            score += allScores.get(i);
        }

        return score;
    }

    private double getHighestAttemptsPercentage(ArrayList<GBForecastingAttemptEntry> attempts) {
        int pointsEarned = getHighestAttemptsScore(attempts);
        int pointsPossible;
        int gradedAttempts = 0;

        for (GBForecastingAttemptEntry a : attempts) {
            if (a.hasBeenGraded()) {
                gradedAttempts++;
            }
        }
        
        if (attempts.isEmpty()) {
            return 0.0;
        }
        
        int possiblePointsPerAttempt = lesson.getPointScale()
                .getCorrectPoints() * lesson.getQuestions().size();

        if (gradedAttempts > getTopScoresCounted()) {
            pointsPossible = possiblePointsPerAttempt * getTopScoresCounted();
        } else {
            pointsPossible = possiblePointsPerAttempt * gradedAttempts;
        }

        if (pointsEarned == 0 || pointsPossible == 0) {
            return 0.0;
        }

        return (Double.parseDouble(new DecimalFormat("#.#").format((((double) pointsEarned)
                / ((double) pointsPossible)) * 100.0)));

    }

}
