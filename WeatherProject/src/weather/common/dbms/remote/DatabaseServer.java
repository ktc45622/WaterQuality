package weather.common.dbms.remote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.dbms.DBMSSystemManager;

import weather.common.utilities.Debug;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.WeatherTracer;

/**
 *
 * @author Brian Zaiser
 */
public class DatabaseServer extends Thread {
    //The maximum number of Threads allowed in the thread pool.

    private int maxPoolSize = 32;
    //Integer.parseInt(PropertyManager.getGeneralProperty("maxDabaseServerPoolSize"));
    //The "routine" number of Threads available in the thread pool.
    private int corePoolSize = 16;
    // Integer.parseInt(PropertyManager.getGeneralProperty("coreDatabaseServer PoolSize"));
    //The maximum number of milliseconds to keep a Thread connected to .
    private long keepAliveTime = 90000L; //90 seconds in milliseconds
    //Whether to disconnect or not.
    private boolean terminate = false;
    //Which port number does the server listen on?
    private int portNumber = -1;
    private ServerSocket databaseServerSocket = null;
    Socket socket = null;
    //??
    private ThreadPoolExecutor threadPoolExecutor = null;
    //??
    DatabaseServerRequestHandlerPool handlerPool = null;
    //The means of performing database commands.
    DBMSSystemManager dbms = null;
    //Need access to a storage system 
    private StorageControlSystem storageSystem;
    //A log for transactions that don't work.
    private static WeatherTracer log = WeatherTracer.getDatabaseServerLog();

    /**
     * Creates a new instance of the Database Server
     *
     * @param portNumber The port number this server is going to use for
     * requests.
     * @param dbms The database system manager to be used.
     * @param storageSystem The storage management system the database
     * server us to use.
     */
    public DatabaseServer(int portNumber, DBMSSystemManager dbms, StorageControlSystem storageSystem) {

        log.info("Creating new database server. Port number " + portNumber + ".");
        this.portNumber = portNumber;
        this.dbms = dbms;
        this.storageSystem = storageSystem;
    }

    /**
     * Initializes the Thread to listen on the stated port number.
     */
    public void init() {
        log.info("Database server started.");

        try {
            databaseServerSocket = new ServerSocket(portNumber);
            log.fine("Got Database server socket; bound to port " + portNumber + ".");
        } catch (IOException ex) {
            log.severe("Error getting Database server socket on port " + portNumber + ".", ex);

            databaseServerSocket = null;
            terminate();
            WeatherLogger.log(Level.SEVERE, "Database Server cannot listen on port "
                    + getPortNumber(), ex);
            WeatherLogger.log(Level.SEVERE, "Database Server shutdown on port "
                    + getPortNumber(), ex);

            System.exit(-1); // no use doing anything else
        }

        threadPoolExecutor = new ThreadPoolExecutor(getCorePoolSize(),
                getMaxPoolSize(),
                getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        handlerPool = new DatabaseServerRequestHandlerPool(getMaxPoolSize(),
                dbms, storageSystem);

    }

    /**
     * The code that executes when the Thread runs.
     */
    @Override
    public void run() {
        // Bind the server to a port.
        init();
        DatabaseServerRequestHandler handler = null;

        log.info("Entering main database server loop.");
        Debug.println("Entering main database server loop");

        while (!Thread.interrupted()) {
            try {
                //        log.finer ("Database sever waiting for a connection.");
                socket = databaseServerSocket.accept(); // Blocking Call
                //          log.finer ("Database client connected at remote port " + socket.getPort () + ".");
                Debug.println("Socket Accepted");
            } catch (IOException ex) {
                WeatherLogger.log(Level.SEVERE, "Error accepting socket", ex);
                log.severe("Error accepting connection, reentering main loop.", ex);
                continue; // try another

            }
            Debug.println("Attempting to get handler.");
            while ((handler = handlerPool.getHandler()) == null) { //keep looping unti we have a handler
                try {
                    Debug.println("Waiting for handler.");
                    sleep(100); // wait for one to become available
                } catch (InterruptedException ex) {
                    // interrupted, just close this socket and quit
                    try {
                        log.severe("Database server was Interrupted, closing socket.", ex);
                        socket.close();
                    } catch (IOException ex2) {
                        log.severe("Database server error while closig socket.", ex2);
                        break;
                    }
                    //log.fine ("Exiting handler loop.");
                    break; // We are interrupted -- quit
                }
            }// end of loop to get handler

            Debug.println("Got Handler");
            if (handler != null) {
                handler.setAvailable(false);
                handler.setSocket(socket);
                // log.info ("Executing handler");
                Debug.println("Setting handler to be executed");
                threadPoolExecutor.execute(handler);
            }
        }// end of while !interrrupted loop

        getThreadPoolExecutor().shutdown();
        // log.fine ("Shut down thread pool executor.");

        try {
            //Need to close server socket 
            if (databaseServerSocket != null) {
                databaseServerSocket.close();
                //       log.info ("Closed server socket.");
            }
        } catch (IOException ex) {
            //We are trying to quit, do nothing. 
        }
    } // End of run method

    /**
     * Returns the maximum number of Threads allowed in the thread pool.
     *
     * @return The maximum thread pool size.
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Sets the maximum number of Threads allowed in the thread pool
     *
     * @param maxPoolSize The new maximum thread pool size.
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * Returns the "routine" number of Threads in the thread pool.
     *
     * @return The "routine" thread pool size.
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Sets the "routine" number of Threads in the thread pool.
     *
     * @param corePoolSize The new "routine" thread pool size.
     */
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * Returns the current maximum allowable lifetime for a Thread to be
     * assigned to a Socket.
     *
     * @return The current maximum time per Thread per Socket.
     */
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Sets the maximum allowable lifetime for a Thread to be assigned to a
     * Socket.
     *
     * @param keepAliveTime The new maximum time per Thread per Socket.
     */
    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    /**
     * Returns the port number on which the Server listens.
     *
     * @return The port number of this process on the server.
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * Returns the .
     *
     * @return The .
     */
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    /**
     * Returns a boolean indicating if the connection is to be terminated for
     * this Thread.
     *
     * @return The termination status for this Thread.
     */
    public boolean isTerminate() {
        return terminate;
    }

    /**
     * Signals that the Thread is to be terminated.
     */
    public void terminate() {
        this.terminate = true;
        this.interrupt();
    }

    /**
     * Returns the member StorageSystem.
     *
     * @return The storageSystem.
     */
    public StorageControlSystem getStorageSystem() {
        return storageSystem;
    }

    /**
     * Sets the member StprageSystem.
     *
     * @param storageSystem The storageSystem to use.
     */
    public void setStorageSystem(StorageControlSystem storageSystem) {
        this.storageSystem = storageSystem;
    }
}
