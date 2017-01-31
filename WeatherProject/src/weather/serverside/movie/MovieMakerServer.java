
package weather.serverside.movie;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.PropertyManager;

/**
 * This server is responsible for fetching all valid and active resources as defined
 * by the DBMS and then making movies and storing them on the storage server.
 * This server keeps a list of resources and schedules movies to be created
 * for each of these resources once every hour. Clients of the server can
 * request that resources be added or removed from this list.
 * This implementation assumes the storage system is local to the machine running
 * the movie maker server.
 * 
 * Normally this server thread will be executed as a service in the host operating system.
 *
 // @ERROR? It might cause an error for the first hour, and
 //         might not make a movie for the last hour since it
 //         only runs once per hour.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class MovieMakerServer {
    
    public static void main(String[] args) 
            throws ClassNotFoundException, InstantiationException, 
            IllegalAccessException, WeatherException {
        //weather.common.utilities.Debug.setEnabled(true);
        Debug.setEnabled(true);
        DBMSSystemManager DBMS =  MySQLImpl.getMySQLDMBSSystem();
        StorageControlSystem storage = new StorageControlSystemLocalImpl(DBMS);
        StorageControlSystem storage2 = new StorageControlSystemLocalImpl(DBMS);
        MovieMakerScheduler scheduler = new MovieMakerScheduler(storage);
        DayLongVideoScheduler dayLongVideoScheduler = new DayLongVideoScheduler(storage2);
        WeatherResourceType type;

        // For each resource, criteria required to schedule movie creation:
        // active, valid, of type weather camera or weather map loop, 
        Vector<Resource> resources = DBMS.getResourceManager().getResourceList();
        for(Resource r : resources) {
            type = r.getResourceType ();

            if (r.isActive ()) {
                if (type == WeatherResourceType.WeatherCamera ||
                    type == WeatherResourceType.WeatherMapLoop) {

                    // Schedule to make movies.
                    scheduler.startMaker(r);
                    dayLongVideoScheduler.startMaker(r);
                }
            }
        }

        try {
            ServerSocket serverSocket = null;
            serverSocket = new ServerSocket(Integer.parseInt(
                    PropertyManager.getGeneralProperty("moviePort")));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                MovieServerCommandHandler runner = 
                        new MovieServerCommandHandler(clientSocket, scheduler,
                        dayLongVideoScheduler);
                Thread th = new Thread(runner);
                th.start();
            }
        }
        catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, "Was unable to initialize a server socket with the " +
                    "moviePort from GeneralPropertiesSingleton or the connection " +
                    "from the client socket could not be established.", ex);
        }
    }
}
