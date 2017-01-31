package weather.serverside.storage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.WeatherTracer;


/**
 * This server accepts request to store and provide weather resource instance 
 * files in our storage subsystem. 
 * The server listens for connections on the port number assigned. It uses
 * a Thread Pool to execute instances of the storage handlers that handle
 * each client.
 * Normally this server thread
 * will be executed as a service in the host operating system.
 * 
 * @author Bloomsburg University Software Engineering
 * @version Spring 2007
 */
public class StorageServer extends Thread {
    private int maxPoolSize = Integer.parseInt(
                            PropertyManager.getServerProperty("maxPoolSize"));
    private int corePoolSize = Integer.parseInt(
                            PropertyManager.getServerProperty("corePoolSize"));
    private  long keepAliveTime = 90000L; //90 seconds in milliseconds
    
    private boolean terminate = false;
    private int portNumber = -1;
    private ServerSocket serverSocket = null;
    Socket socket = null;
    private ThreadPoolExecutor threadPoolExecutor = null;
    StorageHandlerPool handlerPool = null;
    StorageManagement storageManagement = null;
    
    private static WeatherTracer log = WeatherTracer.getStorageLog ();
    
    
    
    /** Creates a new instance of StorageServer
     * @param portNumber the port number to use as the server
     * @param storageManagement the storage management to use
     */
    public StorageServer(int portNumber, StorageManagement storageManagement) {
        log.info ("Creating new storage server.");
        this.portNumber = portNumber;
        this.storageManagement = storageManagement;
    }
    
    public void init(){
        log.info ("Storage server started.");

        try {
            serverSocket = new ServerSocket(portNumber);
            log.fine ("Got server socket; bound to port " + portNumber + ".");
        }
        catch (IOException ex) {
            log.severe ("Error getting server socket on port " + portNumber + ".", ex);
            
            serverSocket = null;
            terminate();
            WeatherLogger.log(Level.SEVERE,"Storage Server cannot listen on port "
                    +getPortNumber(),ex);
            WeatherLogger.log(Level.SEVERE,"Storage Server shutdown "
                    +getPortNumber(),ex);
            new WeatherException("Storage Server not able to listen on port "+
                    getPortNumber()+" Server will terminate ").show();

            System.exit(-1); // no use doing anything else
       }
        
       threadPoolExecutor = new ThreadPoolExecutor( getCorePoolSize(), 
                                                     getMaxPoolSize(), 
                                                     getKeepAliveTime(), 
                                                     TimeUnit.MILLISECONDS,
                                                     new LinkedBlockingQueue<Runnable>());
       
       handlerPool = new StorageHandlerPool(getMaxPoolSize(),
                                            storageManagement);
      
    }
    
    @Override
    public void run(){
        // Bind the server to a port.
        init();
        StorageRequestHandler handler = null;

        log.info ("Entering main server loop.");

        while (!Thread.interrupted()){
            try {
                log.finer ("Waiting for connection.");
                socket = serverSocket.accept(); // Blocking Call
                log.finer ("Client connected at remote port " + socket.getPort () + ".");
            } catch (IOException ex) {
               WeatherLogger.log(Level.SEVERE,"Error accepting socket",ex);
               log.info ("Error accepting connection, reentering main loop.");
               continue; // try another
               //@TODO count errors
            }
            while ( (handler = handlerPool.getHandler()) == null  ){
                try {
                    sleep(100); // wait for one to become available
                } catch (InterruptedException ex) {
                    // interrupted, just close this socket and quit
                    try {
                        log.severe ("Interrupted, closing socket.", ex);
                        socket.close();
                    } catch (IOException ex2) {
                        // Ignore, we want to quit
                    }
                    log.fine ("Exiting handler loop.");
                    break; // We are interrupted -- quit
                }
            }// end of loop to get handler

            if( handler != null){
                handler.setAvailable(false);
                handler.setSocket(socket);
                log.info ("Executing handler");
                threadPoolExecutor.execute(handler);
            }
        }// end of while !interrrupted loop
        
        getThreadPoolExecutor().shutdown();
        log.fine ("Shut down thread pool executor.");
        
        try {
            //Need to close server socket 
            if (serverSocket !=null) {
                serverSocket.close();
                log.info ("Closed server socket.");
            }
        } catch (IOException ex) {
                //We are trying to quit, do nothing. 
        }
    } // End of run method

    public  int getMaxPoolSize() {
        return maxPoolSize;
    }

    public  void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public  int getCorePoolSize() {
        return corePoolSize;
    }

    public  void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public  long getKeepAliveTime() {
        return keepAliveTime;
    }

    public  void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public void terminate() {
        this.terminate = true;
        this.interrupt();
    }
}
