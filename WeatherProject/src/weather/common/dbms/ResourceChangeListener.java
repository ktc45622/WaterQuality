package weather.common.dbms;

import weather.common.data.resource.Resource;

/**
 * @author Curt Jones
 */
public interface ResourceChangeListener {

    /**
     * Notification that the specified resource was added to the database.
     * @param resource The resource to add to the database.
     */
    public boolean addResource(Resource resource);

    /**
     * Notification that the specified resource's information was updated in the database.
     * @param resource The resource to be updated in the database.
     */
    public boolean updateResource(Resource resource);

     /**
     * Notification that the specified Resource was removed from the database.
     * @param resource The resource to be removed from the database. 
     */
    public boolean removeResource(Resource resource);

}
