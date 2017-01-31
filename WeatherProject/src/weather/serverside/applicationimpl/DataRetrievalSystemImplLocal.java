package weather.serverside.applicationimpl;

import weather.DataRetrievalSystem;
import weather.common.data.resource.Resource;
import weather.serverside.retrieval.ResourceRetriever;

/**
 * A local implementation of the DataRetrievalSystem. This version should be used
 * when the retrieval system and the dbms system will be contained on the same
 * Java Virtual Machine. 
 * 
 * The local implementation is preferable since the only exception that can occur
 * happens when the ResourceRetriever is unable to write a Resource to the
 * StorageSystem.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert
 * @version Spring 2008
 */
public class DataRetrievalSystemImplLocal implements DataRetrievalSystem{
    
    private ResourceRetriever retriever;
    
    /**
     * Constructor for DataRetrievalSystemImplLocal.
     * TODO: finish implementing constructor
     * @param retriever a local <code>ResourceRetriever</code>
     */
    public DataRetrievalSystemImplLocal(ResourceRetriever retriever)
    {
        this.retriever = retriever;
     //needs reference to database
    // needs to register with resource manager as an observer
    }

    /**
     * Adds the specified <code>Resource</code> to the <code>ResourceRetriever</code> and begins retrieving.
     * TODO: Never returns false, what if resource is not active and valid?
     * @param resource The desired <code>Resource</code>.
     * @return True when the <code>Resource</code> is successfully added
     */
    @Override
    public boolean addResource(Resource resource)
    {
        retriever.startRetrieval(resource);
        return true;
    }

    /**
     * Updates the specification of a <code>Resource</code> currently being 
     * retrieved by the <code>ResourceRetriever</code>. Note that this uses 
     * the exact same functionality as addResource.
     * @param resource The desired <code>Resource</code>.
     * @return True when the <code>Resource</code> is successfully updated.
     */
    @Override
    public boolean updateResource(Resource resource)
    {
        return addResource(resource);
    }

    /**
     * Causes the specified <code>Resource</code> to cease being retrieved by
     * the <code>ResourceRetriever</code>.  This has no result if the resource
     * doesn't exist.
     * TODO: should this always be true?
     * @param resource the desired <code>Resource</code>.
     * @return True when the <code>Resource</code> is successfully removed.
     */
    @Override
    public boolean removeResource(Resource resource)
    {
        retriever.stopRetrieval(resource);
        return true;
    }
     
    /**
     * Returns the error log maintained by this system. This Data Retrieval 
     * system will log all warnings and error messages to an error log file. 
     * This file will need to be managed (cleared, saved, returned). Any 
     * time an error occurs (the first time we can't access a site) an 
     * entry is made in this log. 
     * 
     * TODO: getErrorLog needs to be implemented
     * @return A textual log of errors. 
     */
    @Override
    public String getErrorLog()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Clears the error log.
     * TODO: clearErrorLog needs to be implemented
     */
    @Override
    public void clearErrorLog()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
