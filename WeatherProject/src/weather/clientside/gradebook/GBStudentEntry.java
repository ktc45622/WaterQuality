package weather.clientside.gradebook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weather.ApplicationControlSystem;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.dbms.DBMSForecasterLessonManager;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.WeatherLogger;

/**
 * This class contains information about a student entry in the gradebook.
 * Contains an array list of all assignments available to this student. Has the 
 * functionality to obtain grade information about the student's individual
 * assignments and overall assignment average. 
 * 
 * @author Nikita Maizet
 */
public class GBStudentEntry {
    
    private ApplicationControlSystem appControl;
    private Course course;
    private User student;
    private ArrayList<GBForecastingLessonEntry> lessons;
    
    
    public GBStudentEntry(ApplicationControlSystem appControl) {
        this.appControl = appControl;
    }

    /**
     * Creates instance of class.
     * 
     * @param appControl ApplicationControlSystem instance
     * @param student Student object for this entry
     * @param course course this student belongs to
     */
    public GBStudentEntry(ApplicationControlSystem appControl, User student, Course course) {
        this.appControl = appControl;
        this.course = course;
        this.student = student;
        this.lessons = new ArrayList<>();
        
        populateStudentEntry();
    }
    
    /**
     * Gets all lessons this student has available and creates 
     * GBForecastingAttemptObjects from them.
     */
    private void populateStudentEntry() {
        // get manager:
        DBMSForecasterLessonManager lessonManager = appControl.getDBMSSystem()
                .getForecasterLessonManager();
        
        ArrayList<ForecasterLesson> tempLessons = new ArrayList<>();
        tempLessons.addAll(lessonManager.getForecasterLessonsByCourse(course
                .getCourseNumber()));
        
        // create GBForecastingLessonEntry objects for each lesson:
        for (int i = 0; i < tempLessons.size(); i++) {
            lessons.add(new GBForecastingLessonEntry(appControl, 
                    tempLessons.get(i), student));
        }
        
    }
    
    /**
     * Gets all lessons for this student.
     * 
     * @return ArrayList<GBForecastingLessonEntry>
     */
    public ArrayList<GBForecastingLessonEntry> getLessons() {
        return lessons;
    }
    
    /**
     * Returns student's first name.
     * 
     * @return String 
     */
    public String getFirstName() {
        return student.getFirstName();
    }
    
    /**
     * Returns student's last name.
     * 
     * @return String
     */
    public String getLastName() {
        return student.getLastName();
    }
    
    /**
     * Returns login name of student.
     * 
     * @return String 
     */
    public String getUserName() {
        return student.getLoginId();
    }
    
    /**
     * Returns student's email.
     * 
     * @return String
     */
    public String getEmail() {
        return student.getEmailAddress();
    }
    
    /**
     * Returns number of completed assignments by checking if each assignment
     * has at least one attempt containing a score object that is >= 0.
     * 
     * @return int
     */
    public int getNumberAssignmentCompleted() {
        int assignmentsCompleted = 0;
        
        for (int i = 0; i < lessons.size(); i++) {
            if(lessons.get(i).getAttempts().size() > 0) {
                for(GBForecastingAttemptEntry a : lessons.get(i).getAttempts()) {
                    if(a.hasBeenGraded()) {
                        assignmentsCompleted++;
                    }
                }
            }
        }
        
        return assignmentsCompleted;
    }

    /**
     * Will return the total points earned thus far by student. 
     * 
     * @return double 
     */
    public int getStudentTotalPoints() {
        return getLessonsPointsEarned();
    }

    /**
     * Will return student's current score for all graded assignments as a
     * percentage. If no graded assignments, return zero.
     * 
     * @return double
     */
    public double getStudentPercentage() {
        int totalPossiblePoints = 0;
        
        for(GBForecastingLessonEntry l : lessons) {
            for(GBForecastingAttemptEntry a : l.getAttempts()) {
                if(a.hasBeenGraded()) {
                    totalPossiblePoints += a.getPossiblePoints();
                }
            }
        }
        
        if(getLessonsPointsEarned() < 1) {
            return 0;
        }
        
        return (Double.parseDouble(new DecimalFormat("#.#").format((((double) getLessonsPointsEarned())
                / ((double) totalPossiblePoints)) * 100.0)));
    }
    
    
    private int getLessonsPointsEarned() {
        int score = 0;
        
        for (int i = 0; i < lessons.size(); i++) {
            if(lessons.get(i).hasGradedAttempts()) {
                score += lessons.get(i).getPointsEarned();
            }
        }
        
        return score;
    }
    
    /**
     * Exports lesson details for this student to an excel file with the s
     * specified filename. 
     * 
     * @param filename 
     */
    public void exportToExcel(String filename) {
        FileOutputStream out = null;

        try {
            //GBStudentEntry student;
            GBForecastingLessonEntry lesson;

            // create workbook
            XSSFWorkbook wb = new XSSFWorkbook();
            // create sheet
            XSSFSheet sheet;
            // create row specify row #
            XSSFRow row;
            // create cell specify cell #
            XSSFCell cell;

            String[] headerVals = {"Assignment:", "Points Earned:", "% Earned:", "Date Submitted:", "Date Due:", "Status:"};


            // create sheet titled with student's name
            sheet = wb.createSheet(getLastName() + ", "
                    + getFirstName());

            // create row containing column header values:
            row = sheet.createRow(0);

            for (int j = 0; j < headerVals.length; j++) {
                cell = row.createCell(j);
                cell.setCellValue(headerVals[j]);
            }


            // for every lesson
            for (int i = 0; i < lessons.size(); i++) {
                lesson = (GBForecastingLessonEntry) lessons.get(i);
                row = sheet.createRow(i + 1);

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
                            cell.setCellValue(" ");
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
}
