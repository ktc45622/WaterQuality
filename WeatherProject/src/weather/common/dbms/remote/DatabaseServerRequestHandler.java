
package weather.common.dbms.remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.dbms.DBMSSystemManager;
import weather.common.utilities.WeatherLogger;

/**
 *
 * @author 
 */
class DatabaseServerRequestHandler implements Runnable{
    
   private DBMSSystemManager dbms = null;
   public boolean available = false; 
   Socket socket = null;
   private StorageControlSystem storageSystem = null;
    
    public DatabaseServerRequestHandler(DBMSSystemManager dbms, StorageControlSystem storageSystem){
        this.dbms = dbms;
        this.storageSystem = storageSystem;
    }

    public boolean isAvailable(){
        return available; 
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        this.setAvailable(false);// Not availble now - handling a request
        try {
            // 1. Read comamnd and arguments from socket
            ObjectInputStream  ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            RemoteDatabaseCommand command = null;
            
            try {
                command = (RemoteDatabaseCommand) ois.readObject();       
            } catch (ClassNotFoundException ex) {
                 WeatherLogger.log(Level.SEVERE, "Could not read the command object ", ex);
            }
            
            DatabaseServerRequestExecutor exec = new DatabaseServerRequestExecutor(command, dbms);
            RemoteDatabaseResult result = exec.execute();
            
            oos.writeObject(result);
            oos.flush();
            
            socket.close();
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, "Error executing database comamnd", ex);
        }
        this.setAvailable(true); // finished current request, now available again 
    }

    private void execute(RemoteDatabaseCommand command) {
        
        
    }

    
}
