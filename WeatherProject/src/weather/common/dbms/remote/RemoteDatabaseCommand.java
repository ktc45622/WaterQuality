package weather.common.dbms.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.utilities.PropertyManager;

/**
 * Used to send which "Manager" and method is to be called along with the 
 * arguments for the method. The <code>DatabaseCommandType</code> contains Strings 
 * which name the Manager and the method. The <code>ArrayList</code> contains 
 * <code>Object</code>s that are the parameters for the SQL statements as <code>
 * PreparedStatements</code> in the methods.
 * @author Brian Zaiser
 */
public class RemoteDatabaseCommand implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    private DatabaseCommandType databaseCommand;
    private ArrayList<Object> arguments;
   
    /**
     * Explicit Constructor: accepts arguments of the same types as the members 
     * of the <code>RemoteDatabaseCommand</code> and populates the members.
     * @param databaseCommand The manager and method names in a DatabaseCommandType.
     * @param arguments The values to be used in the PreparedStatement in the 
     *          named method of the named manager.
     */
    public RemoteDatabaseCommand(DatabaseCommandType databaseCommand, ArrayList<Object> arguments) {
        this.databaseCommand = databaseCommand;
        this.arguments = arguments;
    }
    
    /**
     * Returns the <code>DatabaseCommandType</code> so the server calls the correct 
     * method.
     * @return the member DatabaseCommandType
     */
    public DatabaseCommandType getDatabaseCommand() {
        return databaseCommand;
    }
    
    /**
     * Sets the DatabaseCommandType for this RemoteDatabaseCommand.
     * @param databaseCommand the new DatabaseCommandType
     */
    public void setDatabaseCommand(DatabaseCommandType databaseCommand) {
        this.databaseCommand = databaseCommand;
        return;
    }
  
    /**
     * Returns the <code>ArrayList</code> with the values to be used in the 
     * <code>PreparedStatement</code> in the named method
     * @return the member ArrayList
     */
    public ArrayList<Object> getArguments() {
        return arguments;
    }
    
    /**
     * Sets the arguments of this RemoteDatabaseCommand
     * @param arrayList the new set of arguments
     */
    public void setArguments(ArrayList<Object> arrayList) {
        this.arguments = arrayList;
    }
    
    /**
     * Provide a means of "printing" the command and contents of the 
     * <code>ArrayList</code>. Relies on the default toString methods of both a 
     * <code>DatabaseCommandType</code> and an <code>ArrayList</code>.
     * @return a String containing the String for the member DatabaseCommandType, 
     *      and a String containing the elements of the member <code>ArrayList</code>
     */
    @Override
    public String toString() {
        return "Command: " + databaseCommand + "; Arguments: " + arguments;
    }
    
    /**
     * Sends the current database command to the remote database server. 
     * 
     * @return The result object returned from the database server. 
     */
    public RemoteDatabaseResult execute() {
       try{
        Socket socket = new Socket(PropertyManager.getGeneralProperty("weatherDbHost"),
        Integer.parseInt(PropertyManager.getGeneralProperty("weatherDbPort")));

        OutputStream out = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);

        oos.writeObject(this);
        
        InputStream in = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);      

            RemoteDatabaseResult result =
                (RemoteDatabaseResult) ois.readObject();

        socket.close();
        return result;
       }catch (UnknownHostException ex1 ) {
           // Create and log a fatal exception
           System.exit(-1);
       }
       catch (IOException ex2){
           // Create and log an exception -  return null
           return null;
       }
       catch (ClassNotFoundException ex3){
           // Create and log a fatal exception
           System.exit(-1);
       }
       return null;
    }
}
