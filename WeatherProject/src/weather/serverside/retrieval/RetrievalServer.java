package weather.serverside.retrieval;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.WeatherTracer;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;


/**
 * This server is responsible for fetching all valid and active
 * resources as defined
 * by the DBMS system and stores the files on a storage server.
 * This class is currently implemented assuming that the storage system
 * is on the same physical machine as our storage system. 
 * Normally this server thread
 * will be executed as a service in the host operating system.
 *
 * TODO Does retrieval of resources need to check if within sunrise/sunset time?
 *
 * @author Bloomsburg University Software Engineering
 * @author David Moser (2008)
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class RetrievalServer {

    private static ServerSocket serverSocket;
   // private static Socket clientSocket; //2/22/2010 We want one socket per client
    // Logger for retrieval system.
    private static WeatherTracer log;


    public static void main(String[] args){
        // Get logger.
        log = WeatherTracer.getRetrievalLog ();

        log.info ("Retrieval server started.");
        Debug.setEnabled(true);
        DBMSSystemManager system = null;

        try {
            system =  MySQLImpl.getMySQLDMBSSystem();
        } catch (IllegalAccessException ex){
            WeatherLogger.log(Level.SEVERE, null, ex);
            log.severe ("Error getting MySQLImpl", ex);
            shutdown();
        } catch (InstantiationException ex){
            WeatherLogger.log(Level.SEVERE, null, ex);
            log.severe ("Error getting MySQLImpl", ex);
            shutdown();
        } catch (ClassNotFoundException ex){
            WeatherLogger.log(Level.SEVERE, null, ex);
            log.severe ("Error getting MySQLImpl", ex);
            shutdown();
        }

        log.finer ("Got MySQLImpl.");

        StorageControlSystem storage = new StorageControlSystemLocalImpl(system);
        log.finer ("Got storage control system (local).");
        
        ResourceRetriever retriever = new ResourceRetriever(
                Integer.parseInt(PropertyManager.getServerProperty("corePoolSize")),
                storage);
        log.finer ("Got resource retriever.");
Debug.println("Starting retrieval with corePoolSize "+PropertyManager.getServerProperty("corePoolSize"));

        Vector<Resource> resources = null;
        resources = system.getResourceManager().getResourceList();
        log.finer ("Got all resources.");

        for(Resource r : resources) {
            if(r.isActive()) {
                retriever.startRetrieval(r);
                Debug.println("Starting retrieval of resoruce "+r.getName()+" in "+r.getStorageFolderName());
            }
        }

        log.fine ("Started the retrieval of all resources.");

        try {
            serverSocket = null;
            serverSocket = new ServerSocket(Integer.parseInt(
                    PropertyManager.getGeneralProperty("retrievalPort")));
            log.fine ("Got server socket; bound to port " + serverSocket.getLocalPort () + ".");

            log.info ("Entering main server loop.");

            while (true) {
                log.finer ("Waiting for connection.");
               // clientSocket = null;
                Socket clientSocket = serverSocket.accept();
                log.finer ("Client connected at remote port " + clientSocket.getPort () + ".");

                RetrievalCommandHandler runner = new RetrievalCommandHandler(clientSocket, retriever);
                Thread th = new Thread(runner);

                log.finer ("Starting new retrieval command handler.");
                th.start(); //runner will close client socket
                //Does create garbage, but allows multiple requests at once
            }
        } catch (IOException ex) {
            log.info ("Exiting main server loop.");

            WeatherLogger.log(Level.SEVERE, null, ex);
            log.severe ("Error in main loop.", ex);
            shutdown();
        }
    }

    private static void shutdown()
    {
        try{
            serverSocket.close();
        }catch(IOException e){
            //do nothing we are trying to close
        }

        log.info ("Retrieval server shut down.");

        System.exit(-1);
    }
}
