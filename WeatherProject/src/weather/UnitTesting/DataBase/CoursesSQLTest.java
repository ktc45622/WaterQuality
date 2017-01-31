/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package weather.UnitTesting.DataBase;

import java.sql.Date;
import java.util.Vector;
import weather.common.data.Course;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

/**
 *
 * @author jjh35893
 */
public class CoursesSQLTest {

    public static void main(String [] args){
        boolean isSuccessfull = false;
        try {
            MySQLImpl dbms = MySQLImpl.getMySQLDMBSSystem();

            // can be build with a string or by passing in a date method
            // or
            // http://www.jguru.com/faq/view.jsp?EID=422110
            Date date = java.sql.Date.valueOf("2011-01-16");

            Vector<Course> courses = dbms.getCourseManager().obtainInactiveCourses(date);
            // iterate through the vector to test that each value is there
            for(Course course : courses)
            {
                Debug.println(course.getClassName() + " " + course.getCreationDateInPrettyFormat());
            }

            if(dbms.getCourseManager().removeCoursesBeforeDate(date)){
                Debug.println("successully deleted before " + date);
            }


        } catch (Exception e) {
            // Terrible error catching, only used in unit testing.
            Debug.println("Error in unit test.");
            Debug.println(e.toString());
        } finally {
            // all conections should be closed
        }

    } // end of main
}
