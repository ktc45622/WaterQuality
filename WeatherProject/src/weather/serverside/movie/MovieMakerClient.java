
package weather.serverside.movie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;

/**
 * A client class for sending commands to the Movie Server.  The executeCommand
 * method does not receive a response, while the sendCommand listens for the
 * server's response.
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Zach Rothweiler
 * @version Spring 2012
 */
public class MovieMakerClient {
    
    /**
     * Executes a command on the movie server.  This method does not receive
     * a response from the server.
     * @param command The command to execute on the movie server.
     * @throws weather.common.utilities.WeatherException if connection cannot be
     * made.
     */
    public static void executeCommand(MovieCommand command) throws WeatherException{
        Socket socket=null;
        ObjectInputStream ois=null;  
        ObjectOutputStream oos=null;
        
        try{
            socket = new Socket(PropertyManager.getGeneralProperty("movieHost"),
                    Integer.parseInt(PropertyManager.getGeneralProperty("moviePort")));

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
           
            oos.writeObject(command);
            oos.flush();
            
            oos.close(); 
            ois.close();
        }
        catch(UnknownHostException ex){
            throw new WeatherException();
        }
        catch(IOException ex){
            throw new WeatherException();
        }
        finally{
            closeSocket(socket);
        }
    }
    
    /**
     * Sends a command to the movie server to be executed and receives the 
     * response.
     * @param command The command to execute on the movie server.
     * @return The object returned from the movie server.
     * @throws weather.common.utilities.WeatherException if connection cannot be
     * made.
     */
    public static Object sendCommand(MovieCommand command) throws WeatherException{
        Socket socket=null;
        ObjectOutputStream oos=null;
        ObjectInputStream ois=null;
        Object returned=null;
        
        try{
            socket=new Socket(PropertyManager.getGeneralProperty("movieHost"),
                    Integer.parseInt(PropertyManager.getGeneralProperty("moviePort")));
            
            oos=new ObjectOutputStream(socket.getOutputStream());
            ois=new ObjectInputStream(socket.getInputStream());
            
            oos.writeObject(command);
            oos.flush();
            
            returned=ois.readObject();
            
            ois.close();
            oos.close();
        }
        catch(IOException ex){
            throw new WeatherException();
        }
        catch(ClassNotFoundException ex){
            throw new WeatherException();
        }
        finally{
            closeSocket(socket);
        }
        
        return returned;      
    }
    
    /**
     * Closes the given socket.
     * @param s The socket to close.
     */
    private static void closeSocket(Socket s){
        try{
            if(s!=null){
                s.close();
            }
        }
        catch(IOException ex){
            
        }
    }
}
