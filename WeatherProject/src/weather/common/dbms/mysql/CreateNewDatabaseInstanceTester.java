
package weather.common.dbms.mysql;

import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class creates a new database identical to the WeatherProject database.
 *
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public class CreateNewDatabaseInstanceTester {

    public static void main(String[] args) {
        try {
            CreateNewDatabaseInstanceImpl database = new CreateNewDatabaseInstanceImpl();
            if(database.createDatabase()) {
                JOptionPane.showMessageDialog(null,"The database has been created.",
                        "Message", JOptionPane.PLAIN_MESSAGE);
            }
        }catch(SQLException e) {
             WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
             new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation. There may be an error in SQL syntax. "
                    +"Please try again.").show();
        }
    }
}
