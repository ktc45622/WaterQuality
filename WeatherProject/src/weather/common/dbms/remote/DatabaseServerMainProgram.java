
package weather.common.dbms.remote;

import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.WeatherTracer;

/**
 *
 * @author Curt Jones
 */
public class DatabaseServerMainProgram {
  
   // private static ServerSocket serverSocket; 

    private static WeatherTracer log;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        log = WeatherTracer.getRetrievalLog ();

        DBMSSystemManager databaseImplementationSystem = null;

        try {
            databaseImplementationSystem =  MySQLImpl.getMySQLDMBSSystem();
        } catch (IllegalAccessException ex){
            WeatherLogger.log(Level.SEVERE, "Error getting MySQLImpl", ex);
            log.severe ("Error getting MySQLImpl", ex);
            shutdown();
        } catch (InstantiationException ex){
            WeatherLogger.log(Level.SEVERE, "Error getting MySQLImpl", ex);
            log.severe ("Error getting MySQLImpl", ex);
            shutdown();
        } catch (ClassNotFoundException ex){
            WeatherLogger.log(Level.SEVERE, "Error getting MySQLImpl", ex);
            log.severe ("Error getting MySQLImpl", ex);
            shutdown();
        }

        log.finer ("Got MySQLImpl.");

       //Note We are using the storage control system server so that the storage system and database 
      // system do not need to be the same machine. 
        StorageControlSystem storageControlSystem;
        storageControlSystem =  StorageControlSystemImpl.getStorageSystem();
        log.finer ("Using the (remote) storage control system so the storage system does not need to be on the same machine.");
        
        DatabaseServer databaseServer = new DatabaseServer(
                Integer.parseInt(PropertyManager.getGeneralProperty("weatherDbPort")),
                databaseImplementationSystem,
                storageControlSystem);
        
        databaseServer.start();
        
        log.finer("Database server started.");
        
    }

    private static void shutdown() {
         System.exit(-1);
    }

}
