package weather.clientside.gradebook;

import java.util.ArrayList;
import weather.ApplicationControlSystem;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.dbms.DBMSCourseManager;

/**
 * This class holds information about the course entries in the gradebook. 
 * An instructor or administrator will have access to all of their
 * courses and all students in those courses along with a list of each student's
 * lesson information. Details about each student's lesson include: grade,
 * points earned, total points possible, date of assignment, whether it will
 * count for a grade, and also the ability to see each individual question and
 * answer along with the amount of points scored on each question. For a student
 * user the amount of detail is abstracted to just their individual lesson
 * information.
 *
 * @author Nikita Maizet
 */
public class GB {

    private ApplicationControlSystem appControl;
    private ArrayList<GBCourseEntry> gradebookCourses;
    private final DBMSCourseManager courseManager;
    private final User user;

    /**
     * General constructor - used when creating a GB object for a user with high
     * privileges to view all students in all courses - such as admin or
     * instructor.
     *
     * @param appControl
     */
    public GB(ApplicationControlSystem appControl) {
        this.appControl = appControl;

        courseManager = appControl.getDBMSSystem().getCourseManager();
        user = appControl.getGeneralService().getUser();

        populateCourseList(null);
    }

    /**
     * Used when creating a gradebook object for a specific user - such as
     * student.
     *
     * @param appControl
     * @param user
     */
    public GB(ApplicationControlSystem appControl, User user) {
        this.appControl = appControl;

        courseManager = appControl.getDBMSSystem().getCourseManager();
        this.user = user;

        populateCourseList(user);
    }

    /**
     * Creates a new GBCourseEntry for each course user participates in.
     */
    private void populateCourseList(User user) {
        ArrayList<Course> courseList;

        if (user == null) // if not user specified get all courses
        {
            courseList = new ArrayList<>(courseManager.obtainAllCoursesTaughyByUser(this.user));
        } else // otherwise get courses pertaining only to specified user
        {
            courseList = new ArrayList<>(courseManager.obtainCoursesByStudent(user));
        }

        gradebookCourses = new ArrayList<>(courseList.size());

        if (user == null) // if not user specified get all courses
        {
            for (int i = 0; i < courseList.size(); i++) {
                gradebookCourses.add(new GBCourseEntry(appControl, courseList.get(i)));
            }
        } else // otherwise get courses pertaining only to specified user
        {
            for (int i = 0; i < courseList.size(); i++) {
                gradebookCourses.add(new GBCourseEntry(appControl, courseList.get(i), user));
            }
        }

    }

    /**
     * Returns course entry by its course num.
     *
     * @param courseNum course number to use as index
     * @return GBCourseEntry object of requested course
     */
    public GBCourseEntry getCourseEntryByNumber(int courseNum) {
        for (int i = 0; i < gradebookCourses.size(); i++) {
            if (gradebookCourses.get(i).getCourseNumber() == courseNum) {
                return gradebookCourses.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the number of ForcasterCourseEntry objects.
     *
     * @return Size of gradebook.
     */
    public int getNumberCourseEntries() {
        return gradebookCourses.size();
    }

    /**
     * Returns ArrayList of all GBCourseEntry objects.
     *
     * @return array list of gradebook's course entries
     */
    public ArrayList<GBCourseEntry> getAllCourseEntries() {
        return gradebookCourses;
    }
}
