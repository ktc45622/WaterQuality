package weather.common.dbms;

import java.util.Vector;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;

/**
 * This manager allows you to create, retrieve, update, and remove Resources
 * from the database. It also allows default nighttime, daytime, and generic
 * no data pictures to be inserted in the database and retrieved from
 * the database.
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public interface DBMSResourceManager {

    /**
     * Retrieves all weather resources in the database.
     * 
     * @return A <code>Vector</code> of <code>Resource</code> objects
     * representing the resources in the database.
     */
    public Vector<Resource> getResourceList();

    /**
     * Function to update of insert a <code>Resource</code> in the database.
     *
     * If the record received has a resource number below 1 then, it is new and
     * needs to be added to the database. If it has a resource number above 0
     * then this resource is already in the database and needs to be updated.
     * This operation returns the weather resource object as it would be
     * obtained from the database. This operation modifies the database and then
     * obtains this record from the database and returns it. The same object
     * passed to the operation is modified. This operation catches an
     * <code>SQLException</code> if there is an error in the SQL statement, logs
     * <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code>, displays it to a user, and returns an
     * empty <code>Resource</code>.
     *
     * @param resource The resource to be updated or inserted.
     * @return Either the <code>Resource</code> in its new state after a
     * successful operation or a empty <code>Resource</code> if the operation
     * fails.
     */
    public Resource updateWeatherResource(Resource resource);

    /**
     * Retrieves a <code>Resource</code> from the database with the given
     * resourceNumber.
     * 
     * @param resourceNumber The resourceID of the <code>Resource</code> to be
     * returned.
     * @return A <code>Resource</code> with the given resourceNumber, or null if
     * the given resourceNumber does not exist.
     */
    public Resource getWeatherResourceByNumber(int resourceNumber);

    /**
     * Removes the given <code>Resource</code> from the database.
     *
     * @param resource The <code>Resource</code> to remove from the database.
     * @return True if the given <code>Resource</code> was removed, false
     * otherwise.
     */
    public boolean removeResource(Resource resource);

    /**
     * Removes a <code>Resource</code> with the given resourceNumber from
     * the database.
     *
     * @param resourceNumber The resource number of the <code>Resource</code>
     * to be deleted.
     * @return True if a <code>Resource</code> with the given resourceNumber was
     * removed, false otherwise.
     */
    public boolean removeResource(int resourceNumber);

     /**
     * Retrieves the default nighttime picture stored in the default_pictures
     * table for a <code>Resource</code> with the given resourceNumber.
     *
     * @param resourceNumber The resource number  of a resource to retrieve
     * the default nighttime picture for.
     * @return The <code>ImageInstance</code> object that represents
     * the nighttime picture.
     */
    public ImageInstance getDefaultNighttimePicture(int resourceNumber);

    /**
     * Retrieves the default daytime picture stored in the default_pictures
     * table for a <code>Resource</code> with the given resourceNumber.
     *
     * @param resourceNumber The resourceNumber of a resource to retrieve
     * the default daytime picture for.
     * @return The <code>ImageInstance</code> object that represents
     * the default daytime picture.
     */
    public ImageInstance getDefaultDaytimePicture(int resourceNumber);

    /**
     * Sets the given default nighttime picture that is represented by
     * an <code>ImageInstance</code>  object for a <code>Resource</code> with
     * the given resourceNumber. The given default nighttime picture is stored
     * in the default_pictures table.
     *
     * @param resourceNumber The resource number of a resource to set
     * the default nighttime picture for.
     * @param imageInstance The <code>ImageInstance</code> object that
     * represents the default nighttime picture.
     * @return True if the save was successful; False otherwise.
     */
    public boolean setDefaultNighttimePicture(int resourceNumber, ImageInstance imageInstance);

    /**
     * Sets the given default daytime picture that is represented by
     * an <code>ImageInstance</code> object for a <code>Resource</code> with
     * the given resourceNumber.  The given default daytime picture is stored in
     * the default_pictures table.
     *
     * @param resourceNumber The resource number of a resource to set
     * the default daytime picture for.
     * @param imageInstance The <code>ImageInstance</code> object that
     * represents the default daytime picture.
     * @return True if the save was successful; False otherwise.
     */
    public boolean setDefaultDaytimePicture(int resourceNumber, ImageInstance imageInstance);

    /**
     * Retrieves the default generic no data picture stored in
     * the default_generic_no_data_picture table.
     *
     * @return An <code>ImageInstance</code> object representing the default
     * generic no data picture.
     */
    public ImageInstance getDefaultGenericNoDataImage();

    /**
     * Inserts the given default generic no data picture in
     * the default_generic_no_data_picture table.
     *
     * @param image The default generic no data picture to be inserted in
     * the default_generic_no_data_picture table.
     * @return True if the save was successful; False otherwise.
     */
    public boolean setDefaultGenericNoDataImage(ImageInstance image);


     /**
     * Adds a ResourceChangeListener to our set of listeners.
     * @param resourceChangeListener Object that wants to be notified
     *                                  of changes to resources.
     */
    public void addResourceChangeListener(ResourceChangeListener resourceChangeListener);

    /**
     * Removes a ResourceChangeListener from our set of listeners.
     * @param resourceChangeListener Object that no longer wants to be
     *                                 notified of changes to resources.
     */
    public void removeResourceChangeListener(ResourceChangeListener resourceChangeListener);
}
