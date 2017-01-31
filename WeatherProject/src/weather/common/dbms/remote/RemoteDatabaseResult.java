
package weather.common.dbms.remote;

import weather.common.utilities.WeatherException;

/**
 * Contains the status of the SQL command performed on the server, and either
 * an Object that is the result, or a WeatherException. The result may be 
 * boolean, integer, void, <code>Vector</code>, <code>Collection</code>, 
 * <code>TableMetaData</code>, or any class used by the
 * managers for the Weather Project.
 * @author Brian Zaiser
 */
public class RemoteDatabaseResult implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    private RemoteDatabaseResultStatus resultStatus; // always present
    private Object result; //null if no results
    private WeatherException error; //null if no errors


    /**
     * Explicit Constructor: populates the member status and result with 
     * the argument.
     * @param result The result(s) of the SQL command.
     */
    public RemoteDatabaseResult(RemoteDatabaseResultStatus resultStatus, Object result) {
        this.resultStatus = resultStatus; 
        this.result = result;
        this.error = null;
    }
    
    /**
     * Explicit Constructor: populates the member status, result and error 
     * with the argument.
     * @param result The result(s) of the SQL command.
     * @param error The error object associated with this request.
     */
    public RemoteDatabaseResult(RemoteDatabaseResultStatus resultStatus,Object result,WeatherException error ) {
        this.resultStatus = resultStatus; 
        this.result = result;
        this.error = error;
    }
    
    
    /**
     * Returns the Object that represents the result(s) of the command.
     * @return The member Object.
     */
    public Object getResult() {
        return this.result;
    }
    
    /**
     * Sets the member Object to the argument.
     * @param result The new Object representing the command result.
     */
    public void setResult(Object result) {
        this.result =result;
    }
    
    /**
     * Returns the result in the default format of the member Object's type.
     * @return A String.
     */
    public String toString() {
        if (result != null)
            return result.toString();
        return null;
    }
    
    /**
     * Returns the WeatherException, if any, caused by a database error.
     * @return The member WeatherException caused by a database error.
     */
    public WeatherException getError() {
        return error;
    }

    /**
     * Sets the member WeatherException if there was a database error.
     * @param error The WeatherException representing a database error.
     */
    public void setError(WeatherException error) {
        this.error = error;
    }
/**
 * Returns the status of the database command performed. Status may be: 
 * DatabaseModificationSuccessful - the update or insert completed,
 * SingleResultObjectReturned - only one record in the ResultSet,
 * VectorOfResultsReturned - a Vector containing multiple records, or
 * ErrorObjectReturned - an error occurred.
 * @return The member resultStatus.
 */
    public RemoteDatabaseResultStatus getResultStatus() {
        return resultStatus;
    }

    /**
     * Sets the member resultStatus based on the completion of the command.
     * @param resultStatus The status of the completion of the command.
     */
    public void setResultStatus(RemoteDatabaseResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }
    
}
