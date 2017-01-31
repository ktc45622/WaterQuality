/**
 * The point of this testing file is to ensure the the DBMS method to removes
 * resources return a correct value if the resource does not have data if all of
 * the possible tables.
 */

package BBTest;

import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

public class TestResourceManagerDeletionMethod {

    public static void main(String[] args) {
        //Setup DBMS.
        DBMSSystemManager dbms = null;

        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        
        //Test function with non-existant resource.
        if(dbms.getResourceManager().removeResource(500)) {
            Debug.println("TRUE returned.");
        } else {
            Debug.println("FALSE returned.");
        }
    }
}
