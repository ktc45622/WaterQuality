package weather.UnitTesting.DataBase;

import java.sql.Date;
import java.util.Vector;
import weather.common.data.User;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

/**
 *
 * @author jjh35893 - Joseph Horro
 * tested obtainInactiveStudents and removeStudentsBeforeDate on 2011-2-21 jjh
 */
public class UserSQLTest {

    public static void main(String [] args){
        boolean isSuccessfull = false;
        try {
            MySQLImpl dbms = MySQLImpl.getMySQLDMBSSystem();

            // can be build with a string or by passing in a date method
            // or
            // http://www.jguru.com/faq/view.jsp?EID=422110
            Date date = java.sql.Date.valueOf("2011-02-16");

            Vector<User> users = dbms.getUserManager().obtainInactiveStudents(date);
            // iterate through the vector to test that each value is there
            for(User user : users)
            {
                Debug.println(user.getEmailAddress());
            }

//            Date deletionDate = java.sql.Date.valueOf("2002-01-01");
//
//            if(dbms.getUserManager().removeStudentsBeforeDate(deletionDate)){
//                Debug.println("successully deleted before " + deletionDate);
//            }


        } catch (Exception e) {
            // Terrible error catching, only used in unit testing.
            Debug.println("Error in unit test.");
            Debug.println(e.toString());
        } finally {
            // all conections should be closed
        }

    } // end of main
}
