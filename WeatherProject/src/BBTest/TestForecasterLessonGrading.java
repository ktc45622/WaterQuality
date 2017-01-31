package BBTest;

import weather.common.data.forecasterlesson.ForecasterLessonGrader;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

/**
 * A program to test the grading function written for the server-side grading
 * thread.
 * 
 * @author Brian Bankes
 */

public class TestForecasterLessonGrading {

    public static void main(String[] args) {
        //Setup access to dbms.
        DBMSSystemManager dbms = null;
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }

        //Test grading method.
        Debug.println("Testing...");
        ForecasterLessonGrader.tryToGradeAllAttempts(dbms);
        Debug.println("Done.");
    }
}
