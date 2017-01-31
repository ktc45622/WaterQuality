
package weather.serverside.storage;

import java.util.logging.Level;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.WeatherTracer;

/**
 * This main method initializes and starts up the storage system server to listen
 * for remote connections. If the storage system server does not start, then
 * error logs are created, the user is notified, and the program terminates.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Bill Katsak (2008)
 * @version Spring 2008
 */
public class StorageServerMainProgram 
{
    private static String fileSystemRoot =
            PropertyManager.getServerProperty("storageRootFolder");
    private static WeatherTracer log = WeatherTracer.getStorageLog ();

    
    public static void main(String[] args)  {
        try {
            DBMSSystemManager mySQLImpl =  MySQLImpl.getMySQLDMBSSystem();
            StorageManagement storageManagement = new StorageManagement(mySQLImpl, fileSystemRoot);
            int port = Integer.parseInt(PropertyManager.getGeneralProperty("storagePort"));
            
            StorageServer storageServer = new StorageServer(port, storageManagement);
            storageServer.start();
        }
        catch (ClassNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, 
             "Starting storage server --Internal programming error --class not found", ex);
            log.severe ("Unable to start storage server, class not found.", ex);
        } catch (InstantiationException ex) {
            WeatherLogger.log(Level.SEVERE, 
             "Starting storage server error -- ", ex);
            log.severe ("Unable to start storage server, can't create new instance of class.", ex);
        } catch (IllegalAccessException ex) {
            WeatherLogger.log(Level.SEVERE,
                    "Starting storage server -- Illegal access error -- ", ex);
            log.severe ("Unable to start storage server, illegel access exception.", ex);
        } 
    }
}
