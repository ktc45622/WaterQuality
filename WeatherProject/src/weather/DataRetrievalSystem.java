package weather;

import weather.common.data.resource.Resource;
import weather.common.dbms.ResourceChangeListener;

/**
 * This interface specifies the requests that the data retrieval system will 
 * accept. Any class that implements this object should include a constructor
 * that allows the user to send a vector of resources to start the data 
 * retrieval process. This system needs to know the storage file location.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public interface DataRetrievalSystem extends ResourceChangeListener {

    /**
     * Specifies the signature of the method to implement the Add Resource 
     * use case. This method will add another resource to the list of 
     * resources from which this system will obtain data. The resource can 
     * be anyone of our allowable resource types. If a resource with this 
     * ID is already in the resource list, then this record replaces the
     * current specification. 
     *
     * @param resource a resource to be added.
     * @return returns true if the resource can be added; false if there
     *     is an error.
     */
    
    @Override
    public boolean addResource(Resource resource);

    /**
     * Specifies the signature of the method that allows an outside entity 
     * to change the settings on a particular resource. Most likely the 
     * time frequency will change. Resource ID is used to determine if two
     * resource specifications are the same. If the resource ID is not 
     * currently in the list, then it is added to our list. 
     *
     * @param resource a new resource to replace a current resource.
     * @return returns true if the resource is replaced successfully, false
     *     if there is an error. 
     */
    @Override
    public boolean updateResource(Resource resource);

    /**
     * Specifies the signature of the method that allows an outside entity 
     * to remove a resource location from our list of resources that we are 
     * currently accessing for data. If the resourceID is not in the 
     * current list, then this request is ignored, however an entry 
     * is made in the error log.
     *
     * @param resource a resource to be removed.
     * @return returns true if the resource is successfully removed, false
     *     if there is an error. 
     */
    @Override
    public boolean removeResource(Resource resource);

    /**
     * Returns the error log maintained by this system. This Data Retrieval
     * system will log all warnings and error messages to an error log file. 
     * This file will need to be managed (cleared, saved, returned). Any 
     * time an error occurs (the first time we can't access a site) an
     * entry is made in this log. 
     *
     * @return a textual log of errors.
     */
    public String getErrorLog();

    /**
     * Specifies the signature of the method to clear our current error log.
     * The error log will be cleared at set intervals.
     */
    public void clearErrorLog();

}
