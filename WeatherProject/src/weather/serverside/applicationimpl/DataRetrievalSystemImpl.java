package weather.serverside.applicationimpl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import weather.DataRetrievalSystem;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.utilities.WeatherLogger;
import weather.serverside.retrieval.RetrievalClient;
import weather.serverside.retrieval.RetrievalCommand;
import weather.serverside.retrieval.RetrievalCommandType;

/**
 * A remote implementation of the DataRetrievalSystem. This version should be used
 * when the retrieval system and the storage system will not be contained on the 
 * same Java Virtual Machine. 
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert(2008)
 * @version Spring 2008
 */
public class DataRetrievalSystemImpl implements DataRetrievalSystem {

    DBMSSystemManager dbms = null;
    
    /**
     * Default constructor for DataRetrievalSystemImpl.
     * TODO: actually code constructor
     */
    public DataRetrievalSystemImpl(DBMSSystemManager dbms) {
       this.dbms = dbms;
     
    }

    /**
     * Adds the specified <code>Resource</code> to the ResourceRetriever and begins retrieving.
     * @param resource The desired <code>Resource</code>.
     * @return True when the <code>Resource</code> is successfully added.
     */
    @Override
    public boolean addResource(Resource resource) {
        try
        {
            RetrievalCommand command = new RetrievalCommand(RetrievalCommandType.START, resource);
            RetrievalClient.sendCommand(command);
            return true;
        } catch (UnknownHostException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Updates the specification of a <code>Resource</code> currently being retrieved by the
     * ResourceRetriever. Note that this uses the exact same functionality as
     * addResource.
     * @param resource The desired Resource.
     * @return True when the Resource is successfully updated.
     */
    @Override
    public boolean updateResource(Resource resource) {
          return addResource(resource);
    }

    /**
     * Causes the specified <code>Resource</code> to cease being retrieved by
     * the <code>ResourceRetriever</code>.
     * This has no result if the resource doesn't exist.
     * @param resource The desired <code>Resource</code>.
     * @return True when the <code>Resource</code> is successfully removed.
     */
    @Override
    public boolean removeResource(Resource resource) {
        try
        {
            RetrievalCommand command = new RetrievalCommand(RetrievalCommandType.STOP, resource);
            RetrievalClient.sendCommand(command);
            return true;
        } catch (UnknownHostException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Returns the error log maintained by this system. This Data Retrieval
     * system will log all warnings and error messages to an error log file. 
     * This file will need to be managed (cleared, saved, returned). Any 
     * time an error occurs (the first time we can't access a site) an 
     * entry is made in this log. 
     * TODO: getErrorLog needs to be implemented
     * @return A textual log of errors.
     */
    @Override
    public String getErrorLog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Clears the error log.
     * TODO clearErrorLog needs to be implemented
     */
    @Override
    public void clearErrorLog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
