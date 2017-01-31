package weather.common.dbms;
import java.util.ArrayList;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.Response;

/**
 * manages the <code>Response</code> objects in the database.
 * @author jbenscoter
 */
public interface DBMSForecasterResponseManager {

    /**
     * inserts a <code>Response</code> object in the database
     * @param a the <code>Attempt</code> object to add the <code>Response</code> 
     * to.
     * @param r the <code>Response</code> object to insert.
     * @return the <code>Response</code> object with the id filled out
     */
    public Response insertResponse(Attempt a, Response r);

    /**
     * updates a <code>Response</code> object in the database
     * @param r the <code>Respones</code> object to update
     * @return true if successful, false otherwise
     */
    public boolean updateResponse(Response r);

    /**
     * deletes a <code>Response</code> object in the database
     * @param r the <code>Response</code> object to delete
     * @return the <code>Response</code> object with the id set to -1
     */
    public Response deleteResponse(Response r);

    /**
     * get an <code>ArrayList</code> of <code>Responses</code> 
     * @param a the <code>Attempt</code> to get the <code>Responses</code>
     * @return an <code>ArrayList</code> of <code>Response</code> objects
     */
    public ArrayList<Response> getResponsesByAttempt(Attempt a);
}
