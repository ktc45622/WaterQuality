/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.common.dbms.remote;

import java.util.ArrayList;
import java.util.Collection;
import weather.common.utilities.WeatherException;

/**
 * Tests the construction and other methods of the RemoteDatabaseResult class.
 * @author Brian Zaiser
 * 2013-June-17: began code.
 * 2013-June-: tested code.
 */
public class RemoteDatabaseResultTester {

    /**
     * Test the methods of the <code>RemoteDatabaseResult</code> class which has
     *  members: <code>RemoteDatabaseResultStatus</code> resultStatus, 
     * <code>Object</code> result, and <code>WeatherException</code> error.
     * 
     * @param args the command line arguments - none
     */
    public static void main(String[] args) {
        
        //Create a RemoteDatabaseResult.
        RemoteDatabaseResult rdr;
        //Create an Object for the result.
        Object result;
        //Create a WeatherException for the error.
        WeatherException myError = null, myError2;
        
        //Create the result status with the simple case of returning a successful modification.
        RemoteDatabaseResultStatus dbStatus;
        dbStatus = RemoteDatabaseResultStatus.DatabaseModificationSuccessful;
        
        //Test the constructor with a null result.
        rdr = new RemoteDatabaseResult(dbStatus, null);
        result = rdr.getResult();
        myError = rdr.getError();
        //Print result.
        System.out.println("Status: " + rdr.getResultStatus() 
                + "\tResult: " + result
                + "\tError: " + myError);
        
        //Create another with a boolean result.
        dbStatus = RemoteDatabaseResultStatus.SingleResultObjectReturned;
        rdr.setResultStatus(dbStatus);
        rdr.setResult(true);
        result = rdr.getResult();
        myError = rdr.getError();
        //Print result.
        System.out.println("Status: " + rdr.getResultStatus() 
                + "\tResult: " + result
                + "\tError: " + myError);
        
        //Create a collection of Double for the result.
        Collection<Double> doubleCollection = new ArrayList<>();
        doubleCollection.add(23.41);
        doubleCollection.add(1578.4);
        doubleCollection.add(0.0000021);
        
        //Create another using the Collection
        dbStatus = RemoteDatabaseResultStatus.VectorOfResultsReturned;
        rdr.setResultStatus(RemoteDatabaseResultStatus.VectorOfResultsReturned);
        rdr.setResult(doubleCollection);
        result = rdr.getResult();
        myError = rdr.getError();
        //Print result.
        System.out.println("Status: " + rdr.getResultStatus() 
                + "\tResult: " + result
                + "\tError: " + myError);
        
        //Create non-null errors.
        myError = new WeatherException("help");
        WeatherException anotherError = new WeatherException(56);
        
        //Create another RDR with an exception.
        dbStatus = RemoteDatabaseResultStatus.ErrorObjectReturned;
        rdr = new RemoteDatabaseResult(dbStatus,null,myError);
        myError2 = rdr.getError();
        result = rdr.getResult();
        //Print result.
        System.out.println("Status: " + rdr.getResultStatus() 
                + "\tResult: " + result
                + "\tError: " + myError2);
        
        rdr.setError(anotherError);
        System.out.println("rdr: " + rdr); //Uses toString().
        System.out.println("New error: " + rdr.getError());
    }
}
