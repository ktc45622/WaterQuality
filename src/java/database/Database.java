
package database;

import utilities.PropertyManager;

/**
 *
 * @author cjones
 */
public  class Database {
    private static DatabaseManagement database=null;
    
        
    public static synchronized DatabaseManagement getDatabaseManagement(){
       if(database == null) intialize();
       return database;
   }

    private static void intialize() {

            database = new mysql.DatabaseManagement();

        
        // database.initializeDatabaseManagement(); // may or may not be needed by your application
    }
    
}
