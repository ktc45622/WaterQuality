package weather.common.servercomm.storage;

import java.io.Serializable;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.resource.ResourceInstance;

/**
 * A class that represents a command that can be executed on the StorageServer.
 * It contains a <code>StorageCommandType</code> that specifies what is to be
 * done as well as other fields to supply or provide information to receive 
 * data.  it contains two instances of <code>ResourceInstance</code> to supply 
 * data and one instance of <code>ResourceInstancesRequested</code> to provide 
 * information to receive data.
 * 
 * 
 * @author Bloomsburg University Software Engineering (2008)
 * @author Joe Sharp (2008)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public class StorageCommand implements Serializable {
    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but
     * included here as a reminder of its importance.
     * @serial
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The resource instances with which to supply data.
     */
    private ResourceInstance resourceInstance1;
    private ResourceInstance resourceInstance2;
    
    /**
     * The resource request which provides the guidelines under which data is 
     * to be retrieved.
     */
    private ResourceInstancesRequested resourceRequest;
    
    /**
     * The type of storage command.
     */
    private StorageCommandType commandType;
    
    /**
     * Creates a new instance of StorageCommand with all fields set to null.
     */
    public StorageCommand() {
        this.resourceInstance1 = null;
        this.resourceInstance2 = null;
        this.resourceRequest = null;
        this.commandType = null;
    }

    /**
     * Retrieves the resource request.
     * @return The resource request.
     */
    public ResourceInstancesRequested getResourceRequest() {
        return resourceRequest;
    }

    /**
     * Assigns the resource request.
     * @param resourceRequest The resource request to be assigned.
     */
    public void setResourceRequest(ResourceInstancesRequested resourceRequest) {
        this.resourceRequest = resourceRequest;
    }
    
    /**
     * Retrieves the storage command type.
     * @return The storage command type.
     */
    public StorageCommandType getCommandType() {
        return commandType;
    }

    /**
     * Assigns the storage command type to be used.
     * @param commandType The storage command type to be used.
     */
    public void setCommandType(StorageCommandType commandType) {
        this.commandType = commandType;
    }

    /**
     * Returns the value of the serialVersionID variable held within the object.
     * @return The serialVersionID.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Retrieves the first resource instance.
     * @return The resource instance.
     */
    public ResourceInstance getFirstResourceInstance() {
        return resourceInstance1;
    }

    /**
     * Assigns the first resource instance.
     * @param resourceInstance The resource instance to be assigned.
     */
    public void setFirstResourceInstance(ResourceInstance resourceInstance) {
        this.resourceInstance1 = resourceInstance;
    }
    
    /**
     * Retrieves the second resource instance.
     * @return The resource instance.
     */
    public ResourceInstance getSecondResourceInstance() {
        return resourceInstance2;
    }

    /**
     * Assigns the second resource instance.
     * @param resourceInstance The resource instance to be assigned.
     */
    public void setSecondResourceInstance(ResourceInstance resourceInstance) {
        this.resourceInstance2 = resourceInstance;
    }
}
