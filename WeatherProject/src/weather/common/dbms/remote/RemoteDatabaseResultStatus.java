
package weather.common.dbms.remote;

/**
 * Provides a means to test success or failure of a database command. Used by 
 * the remote managers to test the result. Set by the server.
 * @author Curt Jones
 */
public enum RemoteDatabaseResultStatus implements java.io.Serializable{
    DatabaseModificationSuccessful("DatabaseModificationSuccessful"), //used for commands that place data into the database
    SingleResultObjectReturned("SingleResultObjectReturned"), //a single record returned
    VectorOfResultsReturned("VectorOfResultsReturned"), //multiple records returned
    ErrorObjectReturned("ErrorObjectReturned");  //the database command was not successful
    
    private static final long serialVersionUID = 1L;
    private String name; 
    
    private RemoteDatabaseResultStatus(String name){
        this.name = name;
    }

      /**
     * Returns a String representation of this RemoteDatabaseResultStatus.
     * @return String representation of this RemoteDatabaseResultStatus.
     */
    @Override
    public String toString() {
        return this.name;
    }
    
     /**
     * Returns an enumerated constant based on a String command name and a 
     * String command type.
     * If the command name does not represent a defined enumerated value, 
     * it returns null.
     * 
     * @param name Name of the command being used.
     * @return An enumerated constant matching the command name or null if none was found.
     */
    public static RemoteDatabaseResultStatus fromString(String name) {
        
        for (RemoteDatabaseResultStatus t : RemoteDatabaseResultStatus.values()) {
            if (t.name.equals(name) )
                return t;
        }
        
        return null;
    }
}
