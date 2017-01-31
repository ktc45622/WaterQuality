package weather.common.data.forecasterlesson;

import java.util.ArrayList;
import java.util.Date;
import weather.common.data.*;

/**
 * A representation of all of the data needed for a lesson that will be given
 * to a student. The larger collections of data have been abstracted into
 * smaller classes.
 * 
 * Provides setters and getters for all of the information needed for each
 * lesson.
 *
 * @author Nikita Maizet
 * @author Jeremy Benscoter (2014)
 * @author Joshua Whiteman
 */
public class ForecasterLesson {

    private String lessonID;
    private String lessonName;
    private String studentEditType;
    private String stationCode;
    private Date lessonStartDate;
    private Date lessonEndDate;
    private int maximumTries;
    private boolean active; 

    private boolean fromArchivedData;
    private Date dateArchived;

    private PointScale pointScale; //Add this class
    private Instructions instructions;
    private Course course;
    private ArrayList<Question> questions;

    /**
     * Constructor that will be used to create a ForecasterLesson object. This
     * sets all of the fields used in the object. Some values may be unknown at
     * initial creation, so be sure to set the to something that won't cause
     * issues with the DB manager.
     * 
     * @param lessonID the database identifier for the lesson. 1A if creating.
     * @param lessonName the name of the <code>ForecasterLesson<code/> as 
     * viewed by the student.
     * @param studentEditType whether the student can 
     * @param stationCode the code retrieved from the iowa state for a station
     * @param lessonStartDate the date the <code>ForecasterLesson<code/> 
     * can be viewed by the student.
     * @param lessonEndDate the date the <ForecasterLesson> will no longer by
     * viewed by the student
     * @param maximumTries number of tries a student will have at the lesson
     * @param isDataArchived true if the questions can be graded instantly
     * @param dateArchived the date that the data was archived
     * @param instructions the instructions the instructor will give the 
     * students for the lesson
     * @param course the course the lesson belongs to
     * @param questions an ArrayList of the questions belonging to the lesson 
     * @param ps the scale that will be used for the lesson
     */
    public ForecasterLesson(String lessonID, String lessonName, 
            String studentEditType, String stationCode, 
            Date lessonStartDate, Date lessonEndDate, int maximumTries, 
            boolean isDataArchived, Date dateArchived, 
            Instructions instructions, Course course, 
            ArrayList<Question> questions, PointScale ps) {
        this.lessonID = lessonID;
        this.lessonName = lessonName;
        this.studentEditType = studentEditType;
        this.stationCode = stationCode;
        this.lessonStartDate = lessonStartDate;
        this.lessonEndDate = lessonEndDate;
        this.maximumTries = maximumTries;
        this.fromArchivedData = isDataArchived;
        this.dateArchived = dateArchived;
        this.instructions = instructions;
        this.course = course;
        this.questions = questions;
        this.pointScale = ps;
    }
    
    /**
     * Gets the db identifier for the Lesson.
     * @return db identifier.
     */
    public String getLessonID() {
        return lessonID;
    }

    /**
     * Gets the name for the Lesson as set by the Instructor.
     * @return the Lesson name
     */
    public String getLessonName() {
        return lessonName;
    }

    /**
     * TODO: Used during testing, Remove.
     * @param lessonName the name of the Lesson.
     */
    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    /**
     * Gets the start date of the Lesson.
     * @return a Date date for the day the lesson started
     */
    public Date getLessonStartDate() {
        return lessonStartDate;
    }

    /**
     * TODO: Used during testing, Remove.
     * @param lessonStartDate a Date date for the day the lesson started
     */
    public void setLessonStartDate(Date lessonStartDate) {
        this.lessonStartDate = lessonStartDate;
    }

    /**
     * Gets the end date of the Lesson.
     * @return a Date date for the day the lesson ended or will end
     */
    public Date getLessonEndDate() {
        return lessonEndDate;
    }

    /**
     * TODO: Used during testing, Remove.
     * @param lessonEndDate a Date date for the day the lesson ended
     */
    public void setLessonEndDate(Date lessonEndDate) {
        this.lessonEndDate = lessonEndDate;
    }

    /**
     * TODO: This field may be set, however the feature for it has not been
     * implemented yet.
     * @return if there is saved data for the Lesson.
     */
    public boolean isFromArchivedData() {
        return fromArchivedData;
    }

    /**
     * TODO: This field may be set, however the feature for it has not been
     * implemented yet.
     * @return if the load was successful or not
     */
    public boolean loadArchivedData() {
        return false;
    }

    /**
     * TODO: This field may be set, however the feature for it has not been
     * implemented yet.
     * @return if the upload was successful or not
     */
    public boolean upLoadCurrentData() {
        return false;
    }

    /**
     * TODO: This field may be set, however the feature for it has not been
     * implemented yet.
     * @return if the upload was successful or not
     */
    public boolean upLoadData() {
        return false;  
    }
    
    /**
     * Gets the Instructions for the Lesson as set by the Instructor during
     * creation.
     * @return an Instructions object
     */
    public Instructions getInstructions() {
        return this.instructions;
    }

    /**
     * Sets the Instructions for the Lesson.
     * @param instructions the Instructions the field will be set to.
     */
    public void setInstructions(Instructions instructions) {
        this.instructions = instructions;
    }
    
    /**
     * Gets the Course for the Lesson.
     * @return a Course object for the lesson
     */
    public Course getCourse() {
        return course;
    }
    
    /**
     * TODO: Used during testing, Remove.
     * @param course the course for the lesson
     */
    public void setCourse(Course course) {
        this.course = course;
    }
    
    /**
     * Gets the StudentEditType.
     * @return a String representation of the StudentEditType
     */
    public String getStudentEditType() {
        return studentEditType;
    }

    /**
     * TODO: Used during testing, Remove.
     * @param studentEditType the studentEditType for the lesson
     */
    public void setStudentEditType(String studentEditType) {
        this.studentEditType = studentEditType;
    }

    /**
     * Gets the station code for the lesson
     * @return a four char station code
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * TODO: This field may be set, however the feature for it has not been
     * implemented yet.
     * @return the date the data was archived.
     */
    public Date getDateArchived() {
        return dateArchived;
    }

    /**
     * Gets the <code>PointScale</code> for the lesson.
     * @return the PointScale for the lesson.
     */
    public PointScale getPointScale() {
        return pointScale;
    }

    /**
     * Sets the <code>PointScale</code> for the lesson.
     * @param pointScale the PointScale for the lesson.
     */
    public void setPointScale(PointScale pointScale) {
        this.pointScale = pointScale;
    }
    
    /**
     * If the lesson is active or not.
     * @return true if the lesson is active. Otherwise, false.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the list of Questions for the lesson.
     * @return the list of Questions for the lesson.
     */
    public ArrayList<Question> getQuestions() {
        return this.questions;
    }
    
    /**
     * Sets the list of Questions for the lesson.
     * @param questions the list of Questions for the lesson 
     */
    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    /**
     * Adds a Question.
     * @param question a Question that will be added to the lesson
     * @return if the addition was successful or not.
     */
    public boolean addQuestion(Question question) {
        this.questions.add(question);
        return true;
    }
    
    /**
     * TODO: Used during testing, Remove.
     * @param tries the maximum number of tries a student may attempt.
     */
    public void setMaximumTries(int tries){
        maximumTries = tries;
    }
    
    /**
     * Gets the maximum number of tries for the lesson as specified by the
     * Instructor.
     * @return an int for the maximum number of tries a student may attempt.
     */
    public int getMaximumTries(){
        return maximumTries;
    }

    /**
     * A String representation of the Lesson used for testing.
     * @return a String representation of the Lesson used for testing.
     */
    @Override
    public String toString() {
        String questionsText = "";
        for(int i = 0; i < questions.size(); i++)
        {
            questionsText += "    "+questions.get(i).toString()+"\n";
        }
        return "ForecasterLesson{" + "lessonID=" + lessonID + ", lessonName=" 
                + lessonName + ", studentEditType=" + studentEditType 
                + ", stationCode=" + stationCode + ", lessonStartDate=" 
                + lessonStartDate + ", lessonEndDate=" + lessonEndDate 
                + ", maximumTries=" + maximumTries +  ", active=" + active 
                + ", fromArchivedData=" + fromArchivedData + ", dateArchived=" 
                + dateArchived + ", pointScale=" + pointScale 
                + ", instructions=" + instructions + ", course=" + course 
                + '}' + "\n" + questionsText;
    }
}
