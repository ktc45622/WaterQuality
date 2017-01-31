package weather.clientside.gradebook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weather.ApplicationControlSystem;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.dbms.DBMSEnrollmentManager;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.WeatherLogger;

/**
 * This class contains information about a course entry in the gradebook. A
 * Course object must be provided and the list of studentEntries will
 * automatically be populated. This class also provides general information
 * about a course's grades such as class average grade, percentage.
 *
 * @author Xiang Li
 * @author Nikita Maizet
 */
public class GBCourseEntry implements Comparable {

    private ApplicationControlSystem appControl;
    private Course course;
    private ArrayList<GBStudentEntry> studentEntries;
    private DBMSEnrollmentManager enrollmentManager;

    /**
     * Generates an instance of the class for privileged users.
     *
     * @param appControl application control for the program
     * @param course The course for this entry into the database.
     */
    public GBCourseEntry(ApplicationControlSystem appControl, Course course) {
        this.appControl = appControl;
        this.course = course;
        enrollmentManager = appControl.getDBMSSystem().getEnrollmentManager();
        studentEntries = new ArrayList<>();

        populateCourseEntry(null);
    }

    /**
     * Constructor user when creating GB object for a user with lesser
     * privileges.
     *
     * @param appControl ApplicationControlSystem instance
     * @param course Course object used to get information
     * @param user User object to load data for (current user)
     */
    public GBCourseEntry(ApplicationControlSystem appControl, Course course, User user) {
        this.appControl = appControl;
        this.course = course;
        enrollmentManager = appControl.getDBMSSystem().getEnrollmentManager();
        user = appControl.getGeneralService().getUser();
        studentEntries = new ArrayList<>();

        populateCourseEntry(user);
    }

    /**
     * Finds all users in this course and uses them to generate GBStudentEntry
     * objects to add to student list. If a user object that is not null is
     * supplied, this means a student is the current user, therefore that is the
     * only user added to student list in order to protect other student's
     * information.
     *
     * @param user the person currently using the program if a student, null if
     * that person is an instructor.
     */
    public final void populateCourseEntry(User user) {
        ArrayList<User> studentList;

        if (user == null) { // This means an instructor is running the program.
            studentList = new ArrayList<>(enrollmentManager.getStudentsInCourse(course));
        } else { // if populating gradebook for single user only
            studentList = new ArrayList<>();
            studentList.add(user);
        }

        for (int i = 0; i < studentList.size(); i++) {
            studentEntries.add(new GBStudentEntry(appControl, 
                    studentList.get(i), course));
        }

        if (user == null) { // This means an instructor is running the program.
            sortStudentsAlphabetically();
            //Intuctor can also be in class.
            studentEntries.add(0, new GBStudentEntry(appControl,
                    course.getInstructor(), course));
        }
    }

    private void sortStudentsAlphabetically() {
        Collections.sort(studentEntries, new Comparator<GBStudentEntry>() {
            @Override
            public int compare(GBStudentEntry s1, GBStudentEntry s2) {
                return s1.getLastName().compareToIgnoreCase(s2.getLastName());
            }
        });
    }

    /**
     * Gets course object for this instance.
     *
     * @return Course object for this course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Return course number of the Course object the instance of this class
     * contains.
     *
     * @return int
     */
    public int getCourseNumber() {
        return course.getCourseNumber();
    }

    /**
     * Will calculate the average percentage of points earned by all
     * studentEntries in this course as a whole.
     *
     * @return double
     */
    public double getCourseAveragePercentage() {
        int totalPoints = 0;
        int possiblePoints = 0;

        for (GBStudentEntry s : studentEntries) {
            //Add points counted for student.
            totalPoints += s.getStudentTotalPoints();

            //Add possible points for student.
            lessonLoop:
            for (GBForecastingLessonEntry l : s.getLessons()) {
                int topScoresCounted = l.getTopScoresCounted();
                int attemptsCounted = 0;
                for (GBForecastingAttemptEntry a : l.getAttempts()) {
                    if (a.hasBeenGraded()) {
                        possiblePoints += a.getPossiblePoints();
                        attemptsCounted++;
                        //Check if the maximium points have bee counted.
                        if (attemptsCounted == topScoresCounted) {
                            //The maximum possible points for the lesson have
                            //been counted.
                            continue lessonLoop;
                        }
                    }
                }
            }
        }

        if (totalPoints == 0) {
            return 0.0;
        }

        return Double.parseDouble(new DecimalFormat("#.#").format((((double) totalPoints)
                / ((double) possiblePoints) * 100.0)));
    }

    /**
     * Getter for this course's course ID.
     *
     * @return String
     */
    public String getCourseName() {
        return course.getClassName();
    }

    /**
     * Returns number of student entries in this course.
     *
     * @return int
     */
    public int getNumberStudentEnties() {
        return studentEntries.size();
    }

    /**
     * Return studentEntries list sorted alphabetically by individual student's
     * last names.
     *
     * @return list of studentEntries sorted alphabetically.
     */
    public ArrayList<GBStudentEntry> getAllStudentEntries() {
        return studentEntries;
    }

    /**
     * Writes grade summary of selected studentEntries to an excel file.
     *
     * @param selectedStudents indexes of selected studentEntries to export
     * @param filename name of file
     */
    public void exportToCSVCourseSummaryOnly(int[] selectedStudents, String filename) {
        FileOutputStream out = null;

        try {
            GBStudentEntry student;

            // create workbook
            XSSFWorkbook wb = new XSSFWorkbook();
            // create sheet
            XSSFSheet sheet;
            // create row specify row #
            XSSFRow row;
            // create cell specify cell #
            XSSFCell cell;

            String[] headerVals = {"Student:", "Total Points:", "Grade(%):"};

            // generate course summary sheet:
            sheet = wb.createSheet("Course Summary");

            row = sheet.createRow(0);

            // set column header values:
            for (int i = 0; i < headerVals.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(headerVals[i]);
            }

            // write each student's name and final grade:
            for (int i = 0; i < selectedStudents.length; i++) {
                student = studentEntries.get(selectedStudents[i]);

                row = sheet.createRow(i + 1);

                for (int j = 0; j < 2; j++) {
                    cell = row.createCell(j);

                    switch (j) {
                        case 0:
                            cell.setCellValue(student.getLastName() + ", " + student.getFirstName());
                            break;
                        case 1:
                            cell.setCellValue(student.getStudentTotalPoints());
                            break;
                        case 2:
                            cell.setCellValue(student.getStudentPercentage());
                            break;
                    }
                }
            }

            // write workbook to excel file
            File filepath = new File(CommonLocalFileManager.
                    getForecastingLessonExcelFilesDirectory() + "\\" + filename);

            out = new FileOutputStream(filepath);
            wb.write(out);
            out.close();

        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Writes assignment details of selected studentEntries to an excel file.
     *
     * @param selectedStudents indexes of selected studentEntries to export
     * @param filename name of file
     */
    public void exportToCSVStudentDetailsOnly(int[] selectedStudents, String filename) {
        FileOutputStream out = null;

        try {
            GBStudentEntry student;
            GBForecastingLessonEntry lesson;
            ArrayList<GBForecastingLessonEntry> lessons;

            // create workbook
            XSSFWorkbook wb = new XSSFWorkbook();
            // create sheet
            XSSFSheet sheet;
            // create row specify row #
            XSSFRow row;
            // create cell specify cell #
            XSSFCell cell;

            String[] headerVals = {"Assignment:", "Points Earned:", "% Earned:", "Date Submitted:", "Date Due:", "Status:"};

            // for every student
            for (int i = 0; i < selectedStudents.length; i++) {
                // get student and all student's lessons
                student = studentEntries.get(selectedStudents[i]);;
                lessons = student.getLessons();

                // create sheet titled with student's name
                sheet = wb.createSheet(student.getLastName() + ", "
                        + student.getFirstName());

                // create row containing column header values:
                row = sheet.createRow(0);

                for (int j = 0; j < headerVals.length; j++) {
                    cell = row.createCell(j);
                    cell.setCellValue(headerVals[j]);
                }


                // for every lesson
                for (int j = 0; j < lessons.size(); j++) {
                    lesson = (GBForecastingLessonEntry) lessons.get(j);
                    row = sheet.createRow(j + 1);

                    // write lesson details on a row
                    for (int k = 0; k < headerVals.length; k++) {
                        cell = row.createCell(k);

                        switch (k) {
                            case 0:
                                cell.setCellValue(lesson.getEntryName());
                                break;
                            case 1:
                                cell.setCellValue(lesson.getPointsEarned());
                                break;
                            case 2:
                                cell.setCellValue(lesson.getPercentage());
                                break;
                            case 3:
                                cell.setCellValue(" ");// date of submission, requested blank
                                break;
                            case 4:
                                cell.setCellValue(lesson.getDateDue());
                                break;
                            case 5:
                                cell.setCellValue(lesson.getAssignmentStatus());
                                break;
                        }
                    }
                }
            }

            // write workbook to excel file
            File filepath = new File(CommonLocalFileManager.
                    getForecastingLessonExcelFilesDirectory() + "\\" + filename);

            out = new FileOutputStream(filepath);
            wb.write(out);
            out.close();

        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Writes grade summary and assignment details of selected studentEntries to
     * an excel file.
     *
     * @param selectedStudents indexes of selected studentEntries to export
     * @param filename name of file
     */
    public void exportToCSVWithCourseSummary(int[] selectedStudents, String filename) {
        FileOutputStream out = null;

        try {
            GBStudentEntry student;
            GBForecastingLessonEntry lesson;
            ArrayList<GBForecastingLessonEntry> lessons;

            // create workbook
            XSSFWorkbook wb = new XSSFWorkbook();
            // create sheet
            XSSFSheet sheet;
            // create row specify row #
            XSSFRow row;
            // create cell specify cell #
            XSSFCell cell;

            String[] headerVals1 = {"Assignment:", "Points Earned:", "% Earned:", "Date Submitted:", "Date Due:", "Status:"};
            String[] headerVals2 = {"Student:", "Total Points:", "Grade(%):"};

            // generate course summary sheet:
            sheet = wb.createSheet("Course Summary");

            row = sheet.createRow(0);

            // set column header values:
            for (int i = 0; i < headerVals2.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(headerVals2[i]);
            }

            // write each student's name and final grade:
            for (int i = 0; i < selectedStudents.length; i++) {
                student = studentEntries.get(selectedStudents[i]);

                row = sheet.createRow(i + 1);

                for (int j = 0; j < 3; j++) {
                    cell = row.createCell(j);

                    switch (j) {
                        case 0:
                            cell.setCellValue(student.getLastName() + ", " + student.getFirstName());
                            break;
                        case 1:
                            cell.setCellValue(student.getStudentTotalPoints());
                            break;
                        case 2:
                            cell.setCellValue(student.getStudentPercentage());
                            break;
                    }
                }
            }

            // for every student
            for (int i = 0; i < selectedStudents.length; i++) {
                // get student and all student's lessons
                student = studentEntries.get(selectedStudents[i]);;
                lessons = student.getLessons();

                // create sheet titled with student's name
                sheet = wb.createSheet(student.getLastName() + ", "
                        + student.getFirstName());

                // create row containing column header values:
                row = sheet.createRow(0);

                for (int j = 0; j < headerVals1.length; j++) {
                    cell = row.createCell(j);
                    cell.setCellValue(headerVals1[j]);
                }


                // for every lesson
                for (int j = 0; j < lessons.size(); j++) {
                    lesson = (GBForecastingLessonEntry) lessons.get(j);
                    row = sheet.createRow(j + 1);

                    // write lesson details on a row
                    for (int k = 0; k < headerVals1.length; k++) {
                        cell = row.createCell(k);

                        switch (k) {
                            case 0:
                                cell.setCellValue(lesson.getEntryName());
                                break;
                            case 1:
                                cell.setCellValue(lesson.getPointsEarned());
                                break;
                            case 2:
                                cell.setCellValue(lesson.getPercentage());
                                break;
                            case 3:
                                cell.setCellValue(" "); // date of submission, requested blank
                                break;
                            case 4:
                                cell.setCellValue(lesson.getDateDue());
                                break;
                            case 5:
                                cell.setCellValue(lesson.getAssignmentStatus());
                                break;

                        }
                    }
                }

            }
            // write workbook to excel file
            File filepath = new File(CommonLocalFileManager.
                    getForecastingLessonExcelFilesDirectory() + "\\" + filename);

            out = new FileOutputStream(filepath);
            wb.write(out);
            out.close();

        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }

    public static Calendar getMostRecentAttemptDate(ArrayList<GBForecastingAttemptEntry> attempts) {
        Calendar cal = attempts.get(0).getDateSubmittedCalendar();

        for (int i = 0; i < attempts.size(); i++) {
            if (attempts.get(i).getDateSubmittedCalendar().compareTo(cal) < 0) {
                cal = attempts.get(i).getDateSubmittedCalendar();
            }
        }

        return cal;
    }

    @Override
    public String toString() {
        return getCourseName() + " " + course.getClassIdentifier() + " 0" + course.getSection();
    }

    @Override
    public int compareTo(Object o) {
        if (course.getCourseNumber() == ((GBCourseEntry) o).getCourse().getCourseNumber()) {
            if (course.getSection() < ((GBCourseEntry) o).getCourse().getSection()) {
                return -1;
            } else if (course.getSection() == ((GBCourseEntry) o).getCourse().getSection()) {
                return 0;
            } else {
                return 1;
            }
        } else if (course.getCourseNumber() < ((GBCourseEntry) o).getCourse().getCourseNumber()) {
            return -1;
        } else {
            return 1;
        }
    }
}
