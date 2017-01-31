package weather.common.data;

import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import java.util.Vector;

/**
 * Objects of this class are used to facilitate downloading resources from the
 * server to be used in the main program.  The resourceInstances Vector is used
 * to store <code>ResourceInstance</code> objects that have been downloaded
 * from the server.  The number of resource instances that have been returned
 * is stored in numberOfValuesReturned and the resourceRange field maintains
 * the range of dates of resources that this object contains.
 *
 * @author Bloomsburg University Software Engineering
 * @author Curt Jones (2008)
 * @author Joe Sharp (2009)
 * @version Spring 2009
 */
public class ResourceInstancesReturned implements java.io.Serializable {
    private Vector<ResourceInstance> resourceInstances = null;
    private int numberOfValuesReturned = 0;
    private ResourceRange resourceRange = null;
    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor.
     */
    public ResourceInstancesReturned(){
     resourceInstances = null;
     numberOfValuesReturned = 0;
     resourceRange = null;
    }


    /**
     * Constructor that initializes this object's fields with the given
     * parameters.
     *
     * @param resourceInstances Vector of resources that have been returned
     *      from the server.
     * @param numberOfValuesReturned The number of resource instances that have
     *      been returned from the server.
     * @param resourceRange The range of dates that the returned resources
     *      represent.
     */
    public ResourceInstancesReturned(
            Vector<ResourceInstance> resourceInstances,
            int numberOfValuesReturned,
            ResourceRange resourceRange){
        this.numberOfValuesReturned = numberOfValuesReturned;
        this.resourceInstances = resourceInstances;
    }

    /**
     * Gets the resource instances stored in this object.
     *
     * @return The resource instances of this object.
     */
    public Vector<ResourceInstance> getResourceInstances() {
        return resourceInstances;
    }

    /**
     * Sets the resource instance of this object.
     *
     * @param resourceInstances The resource instance of this object.
     */
    public void setResourceInstances(Vector<ResourceInstance> resourceInstances) {
        this.resourceInstances = resourceInstances;
    }

    /**
     * Gets the number of values that were returned from this object.
     *
     * @return The number of value that were returned from this object.
     */
    public int getNumberOfValuesReturned() {
        return numberOfValuesReturned;
    }

    /**
     * Sets the number of values that are to be returned from this object.
     *
     * @param numberOfValuesReturned The number of value that are to be returned from this object.
     */
    public void setNumberOfValuesReturned(int numberOfValuesReturned) {
        this.numberOfValuesReturned = numberOfValuesReturned;
    }

    /**
     * Gets the resource range of this object.
     *
     * @return The resource range of this object.
     */
    public ResourceRange getResourceRange() {
        return resourceRange;
    }

    /**
     * Sets the resource range of this object.
     *
     * @param resourceRange The resource range of this object.
     */
    public void setResourceRange(ResourceRange resourceRange) {
        this.resourceRange = resourceRange;
    }
}
