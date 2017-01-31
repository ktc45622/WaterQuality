package weather.common.dbms.mysql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import weather.DataRetrievalSystem;
import weather.MovieMakerSystem;
import weather.StorageControlSystem;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceTimeZone;
import weather.common.dbms.DBMSResourceManager;
import weather.common.dbms.ResourceChangeListener;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.serverside.applicationimpl.DataRetrievalSystemImpl;
import weather.serverside.applicationimpl.MovieMakerSystemImpl;

/**
 * The <code>MySQLResourceManager</code> class manipulates resources in the
 * database. This class is used by the Storage, Retrieval and Movie Maker
 * Systems.
 * It allows to retrieve and set a default nighttime picture and
 * a default daytime picture for a WeatherCamera or a WeatherSite.
 * The default nighttime and daytime pictures are stored in the default_pictures
 * table.
 *
 * An instance of this class can be created by calling
 * <code>getResourceManager()</code> from the <code>MySQLImpl</code>:
 * <pre>
 *  Vector<Resource> resources;
 *  DBMSSystem system;
 *  resources = system.getResourceManager().getResourceList();
 * </pre>
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @author Mike Graboske (2008)
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public class MySQLResourceManager implements DBMSResourceManager {
//  TODO -- get down to one instance of database objects.
//  TODO -- Have services register themselves. Can't be done yet.
//  TODO -- Add a method to add a new resource instead of update doing two jobs. -- is this important?

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;
    private Vector<ResourceChangeListener> resourceChangeListeners;
    private Vector<StorageControlSystem> storageSystems; //Need to keep for default pictures right now

    public MySQLResourceManager(MySQLImpl dbms) {
        this.dbms = dbms;
        resourceChangeListeners = new Vector<ResourceChangeListener>();
        // These two lines of code are needed because we have multiple instances
        // of MySQLImpl.
        // Each client creates their own instance of MySQLImpl.
        // Each one of our servers create their own instance of MySQLImpl.
        // We need to create proxies that access the servers to give them commands.

        MovieMakerSystem mms = new MovieMakerSystemImpl(); 
        DataRetrievalSystem drs = new DataRetrievalSystemImpl(dbms);
        addResourceChangeListener(mms);
        addResourceChangeListener(drs);

        // Still need storage systems saved separately right now for default pictures concept
        // Storage systems are also not resource change listeners
        storageSystems = new Vector<StorageControlSystem>();
        addStorageSystem(StorageControlSystemImpl.getStorageSystem());

    }

    /**
     * Returns all the weather resources collected in the database.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * Catches <code>MalformedURLException</code> if a malformed URL
     * is found.  Log the exception in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @return A <code>Vector</code> object of all resources in the database.
     */
    @Override
    public Vector<Resource> getResourceList() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<Resource> list = new Vector<Resource>();
        Resource resource = null;
        try {
            String sql = "SELECT * from resources r, time_zone_information tz "
                    + "WHERE r.resourceNumber = tz.resourceNumber ORDER BY active DESC, orderRank ASC";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                resource = MySQLHelper.makeResourceFromResultSet(rs);
                list.add(resource);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } catch (MalformedURLException e2) {
            WeatherLogger.log(Level.SEVERE, "MalformedURLException is thrown "
                    + "while trying to retrieve a URL from the database.", e2);
            String str = "\nThe URL for resource with name "
                    + resource.getName() + " is not formed correctly.";
            new WeatherException(3015, e2, str).show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Returns the weather resource from the database with the given resource
     * number.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * Catches <code>MalformedURLException</code> if a malformed URL
     * is found.  Log the exception in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param resourceNumber The number of a resource to be returned.
     * @return A <code>Resource</code> object with the given number
     * if this number exists, null otherwise.
     */
    @Override
    public Resource getWeatherResourceByNumber(int resourceNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Resource resource = null;
        try {
            String sql = "SELECT * FROM resources r, time_zone_information tz "
                    + "WHERE r.resourceNumber = tz.resourceNumber AND "
                    + "r.resourceNumber = ?";
            conn = dbms.getLocalConnection();
            
            //Added after errors occurred during testing on 7/25/16.
            if (conn == null || conn.isClosed() || !conn.isValid(4)) {
                Debug.println("Connection in getWeatherResourceByNumber(...) not valid.");
                conn = dbms.getLocalConnection();
            }
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, resourceNumber);
            rs = ps.executeQuery();
            if (!rs.first()) {
                return null;
            }
            resource = MySQLHelper.makeResourceFromResultSet(rs);
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } catch (MalformedURLException e2) {
            String str = "\nThe URL for resource with name "
                    + resource.getName() + " is not formed correctly.";
            new WeatherException(3015, e2, str).show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return resource;
    }

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
    @Override
    public Resource updateWeatherResource(Resource resource) {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        Statement stmt = null;

        ResultSet rs = null;

        String sql = null;
        int orderRank = 1;

        int resourceNumber;
        ResourceTimeZone timeZone;

        resourceNumber = resource.getResourceNumber();
        timeZone = resource.getTimeZone();
        boolean newResource = (resourceNumber < 1);
        try {
            // Connect to database.
            conn = dbms.getLocalConnection();
            // If adding a resource.
            if (newResource) {
                stmt = conn.createStatement();
                // first find the max value of orderRank, and give this resource
                // a value of one greater - if there are none, the first
                // resource will have a rank of 1 (order rank of 0 means 
                // resource has not been saved)
                sql = "SELECT MAX(orderRank) FROM resources";
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    orderRank = rs.getInt("MAX(orderRank)") + 1;
                }
                sql =
                        "INSERT INTO resources(type, name, retrievalMethod, storageFolderName, "
                        + "format, urlString, timeInterval, active, visible, dateInitiated, "
                        + "collectionSpan, startTime, endTime, latitude, longitude, width, "
                        + "height, updateHour, orderRank) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);
                ps.setInt(19, orderRank);

                sql = "INSERT INTO time_zone_information (resourceNumber, "
                        + "timeZone) "
                        + "VALUES (?,?)";

                ps2 = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);
                ps2.setInt(1, resourceNumber);
                ps2.setString(2, timeZone.toString());
            } // If updating a resource.
            else {
                sql = "UPDATE resources SET type = ?, name = ?, retrievalMethod = ?, "
                        + "storageFolderName = ?, format = ?, urlString = ?, timeInterval = ?, active = ?, "
                        + "visible = ?, dateInitiated = ?, collectionSpan = ?, startTime = ?, endTime = ?,"
                        + "latitude = ?, longitude = ?, width = ?, height = ?, updateHour = ?, orderRank = ? "
                        + "WHERE resourceNumber = ?";

                ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);
                ps.setInt(20, resourceNumber);
                ps.setInt(19, resource.getOrderRank());

                sql = "UPDATE time_zone_information SET timeZone = ? "
                        + "WHERE resourceNumber = ?";

                ps2 = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);
                ps2.setString(1, timeZone.toString());
                ps2.setInt(2, resourceNumber);
            }

            ps.setString(1, resource.getResourceType().toString());
            ps.setString(2, resource.getName());
            ps.setString(3, resource.getAccessMethod().toString());
            ps.setString(4, resource.getStorageFolderName());
            ps.setString(5, resource.getFormat().toString());
            ps.setString(6, resource.getURL().toExternalForm());
            ps.setInt(7, resource.getFrequency());
            ps.setBoolean(8, resource.isActive());
            ps.setBoolean(9, resource.isVisible());
            ps.setDate(10, resource.getDateInitiated());
            ps.setString(11, resource.getCollectionSpan().toString());
            ps.setInt(12, resource.getStartTime());
            ps.setInt(13, resource.getEndTime());
            ps.setFloat(14, resource.getLatitude());
            ps.setFloat(15, resource.getLongitude());
            ps.setInt(16, resource.getImageWidth());
            ps.setInt(17, resource.getImageHeight());
            ps.setInt(18, resource.getUpdateHour());

            // Execute statements.
            ps.execute();
            ps2.execute();

            // NOTE: Keys may be auto-generated if a new resource is inserted.

            // Get key (if generated).
            rs = ps.getGeneratedKeys();

            // If there is a key, get the assigned resource number and store it.
            if (rs.first()) {
                int resourceNum = rs.getInt(1);
                resource.setResourceNumber(resourceNum);

                rs = ps2.getGeneratedKeys();
                if (rs.first()) {
                    int timeZoneNum = rs.getInt(1);
                    sql = "UPDATE time_zone_information SET resourceNumber = ? "
                            + "WHERE timeZoneInformationNumber = ?";
                    PreparedStatement ps3 = conn.prepareStatement(sql);
                    ps3.setInt(1, resourceNum);
                    ps3.setInt(2, timeZoneNum);
                    ps3.execute();
                }
            }
            
            //Set order rank of resource, if new.
            if (newResource) {
                resource.setOrderRank(orderRank);
            }

            //Update listeners.
            if (newResource) {
                addResourceNotification(resource);
            }
            else {
                updateResourceNotification(resource);
            }
         

        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
            
            //Set return variable to empty (new) resource.
            resource = new Resource();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return resource;
    }

    /**
     * Calls <code>removeResource(int resourceID)</code> with a resourceID equal to
     * the parameters <code>getResourceNumber()</code> method.
     *
     * @param resource The object that contains the resource number to remove.
     * @return True if the given resource was removed, false otherwise.
     */
    @Override
    public boolean removeResource(Resource resource) {
        return this.removeResource(resource.getResourceNumber());
    }

    /**
     * Removes a <code>Resource</code> with the given resourceNumber from
     * the database. Also deletes appropriate records from the 
     * time_zone_information, default_pictures and resource_relation tables.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param resourceNumber The number of the resource to be removed.
     * @return True if the resource was removed, false otherwise.
     */
    @Override
    public boolean removeResource(int resourceNumber) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        PreparedStatement ps4 = null;
        boolean bSuccess = false;
        Resource r = new Resource();
        try {
            // Make connection to database.
            conn = dbms.getLocalConnection();

            // Delete resource.
            String sql = "delete from resources where resourceNumber = ?";
            ps1 = conn.prepareStatement(sql);
            ps1.setInt(1, resourceNumber);

            // Delete time zone information.
            sql = "DELETE FROM time_zone_information WHERE resourceNumber = ?";
            ps2 = conn.prepareStatement(sql);
            ps2.setInt(1, resourceNumber);

            // Delete default picture.
            sql = "DELETE FROM default_pictures WHERE resourceNumber = ?";
            ps3 = conn.prepareStatement(sql);
            ps3.setInt(1, resourceNumber);
            ps3.execute();
            
            // Delete resource relation.
            sql = "DELETE FROM resource_relation WHERE cameraNumber = ?";
            ps4 = conn.prepareStatement(sql);
            ps4.setInt(1, resourceNumber);
            ps4.execute();

            //Making a temp resource to send to Retrieval... all it needs is the right ID
            r.setResourceNumber(resourceNumber);
            r.setActive(false); //Setting this to false will have our next method remove it from Retrieval
            //Covering all bases
            r.setVisible(false);
            r.setName("Resource deleted from database");
            r.setStorageFolderName("Deleted Resources");
            r.setFrequency(Integer.MAX_VALUE);
            removeResourceNotification(r); // stop movie and retrevial systems
            bSuccess = (!ps1.execute() && !ps2.execute()
                    && !ps3.execute() && !ps4.execute());
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps1);
            MySQLHelper.closePreparedStatement(ps2);
            MySQLHelper.closePreparedStatement(ps3);
            MySQLHelper.closePreparedStatement(ps4);
        }
        
        return bSuccess;
    }

    /**
     * Adds a storage system to the object.
     * @param scsi The storage system to add to the object.
     */
    public void addStorageSystem(StorageControlSystem scsi) {
        this.storageSystems.add(scsi);
    }

    /**
     * Retrieves the default nighttime picture stored in the default_pictures
     * table for a <code>Resource</code> with the given resource number.
     *
     * @param resourceNumber The number  of a resource to retrieve
     * the default nighttime picture for.
     * @return The <code>ImageInstance</code> object that represents
     * the nighttime picture, or null if the given resourceNumber does not exist.
     */
    @Override
    public ImageInstance getDefaultNighttimePicture(int resourceNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ImageInstance image = null;
        try {
            String sql = "SELECT * FROM default_pictures WHERE resourceNumber = ?"
                    + " AND defaultNighttimeImage IS NOT NULL ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, resourceNumber);
            rs = ps.executeQuery();
            if (rs.first()) {
                byte[] bytes = rs.getBytes("defaultNighttimeImage");
                image = new ImageInstance(bytes);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();

        }
        return image;

    }

    /**
     * Retrieves the default daytime picture stored in the default_pictures
     * table for a <code>Resource</code> with the given resource number.
     *
     * @param resourceNumber The number of a resource to retrieve
     * the default daytime picture for.
     * @return The <code>ImageInstance</code> object that represents
     * the default daytime picture, or null if the given resourceNumber does not
     * exist.
     */
    @Override
    public ImageInstance getDefaultDaytimePicture(int resourceNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ImageInstance image = null;
        try {
            String sql = "SELECT * FROM default_pictures WHERE resourceNumber = ?"
                    + " AND defaultDaytimeImage IS NOT NULL ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, resourceNumber);
            rs = ps.executeQuery();
            if (rs.first()) {
                byte[] bytes = rs.getBytes("defaultDaytimeImage");
                image = new ImageInstance(bytes);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();

        }
        return image;
    }

    /**
     * Sets the given default nighttime picture that is represented by
     * an <code>ImageInstance</code>  object for a <code>Resource</code> with
     * the given resourceNumber. Updates the default nighttime picture stored in
     * the default_pictures table if a record with the given resourceNumber
     * exists, otherwise it inserts a record for the given resourceNumber.
     * Lets the storage system know that the default nighttime picture for
     * a <code>Resource</code> with the given resource number has arrived.
     *
     * @param resourceNumber The number of a resource to set the default
     * nighttime picture for.
     * @param imageInstance The <code>ImageInstance</code> object that
     * represents the default nighttime picture.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setDefaultNighttimePicture(int resourceNumber, ImageInstance imageInstance) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM default_pictures WHERE resourceNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, resourceNumber);
            rs = ps.executeQuery();
            InputStream in = new ByteArrayInputStream(imageInstance.getImageBytes());
            if (rs.first()) {
                sql = "UPDATE default_pictures SET defaultNighttimeImage = ?"
                        + "WHERE resourceNumber = ?";
                ps = conn.prepareStatement(sql);
                ps.setBinaryStream(1, in, imageInstance.length());
                ps.setInt(2, resourceNumber);

            } else {
                sql = "INSERT INTO default_pictures (resourceNumber, defaultNighttimeImage)"
                        + "VALUES(?,?)";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, resourceNumber);
                ps.setBinaryStream(2, in, imageInstance.length());
            }
            ps.execute();
            in.close();
            Resource resource = this.getWeatherResourceByNumber(resourceNumber);
            for (StorageControlSystem sc : storageSystems) {
                if (!sc.setNewDefaultNightimeMovie(resource, imageInstance)) {
                    return false;
                }
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
            return false;

        } catch (IOException e2) {
            WeatherLogger.log(Level.SEVERE, "IOException is thrown while "
                    + "trying to close an input stream.", e2);
            new WeatherException(4106, e2, "Unable to close an input stream.")
                    .show();
            return false;
        }
        
        //If code gets here, save was successful.
        return true;
    }

    /**
     * Sets the given default daytime picture that is represented by
     * an <code>ImageInstance</code> object for a <code>Resource</code> with
     * the given resourceNumber. Updates the default daytime picture stored in
     * the default_pictures table if a record with the given resourceNumber
     * exists, otherwise it inserts a record for the given resourceNumber.
     * Lets the storage system know that the default daytime picture for
     * a <code>Resource</code> with the given resource number has arrived.
     *
     * @param resourceNumber The number of a resource to set the default
     * daytime picture for.
     * @param imageInstance The <code>ImageInstance</code> object that
     * represents the default daytime picture.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setDefaultDaytimePicture(int resourceNumber, ImageInstance imageInstance) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM default_pictures WHERE resourceNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, resourceNumber);
            rs = ps.executeQuery();
            InputStream in = new ByteArrayInputStream(imageInstance.getImageBytes());
            if (rs.first()) {
                sql = "UPDATE default_pictures SET defaultDaytimeImage = ?"
                        + "WHERE resourceNumber = ?";
                ps = conn.prepareStatement(sql);
                ps.setBinaryStream(1, in, imageInstance.length());
                ps.setInt(2, resourceNumber);

            } else {
                sql = "INSERT INTO default_pictures (resourceNumber, defaultDaytimeImage)"
                        + "VALUES(?,?)";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, resourceNumber);
                ps.setBinaryStream(2, in, imageInstance.length());
            }
            ps.execute();
            in.close();
            Resource resource = this.getWeatherResourceByNumber(resourceNumber);
            for (StorageControlSystem sc : storageSystems) {
                if (!sc.setNewDefaultDaytimeMovie(resource, imageInstance)) {
                    return false;
                }
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
            return false;
        } catch (IOException e2) {
            WeatherLogger.log(Level.SEVERE, "IOException is thrown while "
                    + "trying to close an input stream.", e2);
            new WeatherException(4106, e2, "Unable to close an input stream.")
                    .show();
            return false;
        }
        
        //If code gets here, save was successful.
        return true;
    }

    /**
     * Retrieves the default generic no data picture stored in
     * the default_generic_no_data_picture table.
     *
     * @return An <code>ImageInstance</code> object representing the default
     * generic no data picture.
     */
    @Override
    public ImageInstance getDefaultGenericNoDataImage() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ImageInstance image = null;
        try {
            String sql = "SELECT noDataImage FROM default_generic_no_data_picture";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.first()) {
                byte[] bytes = rs.getBytes("noDataImage");
                image = new ImageInstance(bytes);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return image;

    }

    /**
     * Inserts the given default generic no data picture in
     * the default_generic_no_data_picture table.
     * Lets the storage system know that the default generic no data picture
     * has arrived.
     *
     * @param image The default generic no data picture to be inserted in
     * the default_generic_no_data_picture table.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setDefaultGenericNoDataImage(ImageInstance image) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT noDataImage FROM default_generic_no_data_picture";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            InputStream in = new ByteArrayInputStream(image.getImageBytes());
            if (rs.first()) {
                sql = "UPDATE default_generic_no_data_picture SET noDataImage = ?";
                ps = conn.prepareStatement(sql);
                ps.setBinaryStream(1, in, image.length());
            } else {
                sql = "INSERT INTO default_generic_no_data_picture (noDataImage)"
                        + "VALUES(?)";
                ps = conn.prepareStatement(sql);
                ps.setBinaryStream(1, in, image.length());
            }
            ps.execute();
            in.close();

            // Update the storage control system
            for (StorageControlSystem sc : storageSystems) {
                if (!sc.setNewDefaultGenericNoDataMovie(image)) {
                    return false;
                }
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
            return false;
        } catch (IOException e2) {
            WeatherLogger.log(Level.SEVERE, "IOException is thrown while "
                    + "trying to close an input stream.", e2);
            new WeatherException(4106, e2, "Unable to close an input stream.")
                    .show();
            return false;
        }
        
        //If code gets here, save was successful.
        return true;
    }

    /**
     * Adds a ResourceChangeListener to our set of listeners.
     * @param resourceChangeListener Object that wants to be notified
     *                                  of changes to resources.
     */
    @Override
    public void addResourceChangeListener(ResourceChangeListener resourceChangeListener) {
        resourceChangeListeners.add(resourceChangeListener);
    }

    /**
     * Removes a ResourceChangeListener from our set of listeners.
     * @param resourceChangeListener Object that no longer wants to be
     *                                 notified of changes to resources.
     */
    @Override
    public void removeResourceChangeListener(ResourceChangeListener resourceChangeListener) {
        resourceChangeListeners.remove(resourceChangeListener); //Must be the same exact object
    }
    
    private void updateResourceNotification(Resource resource) {
        for (ResourceChangeListener rcl :  resourceChangeListeners) {
                rcl.updateResource(resource);
        }
    }
    
   private void addResourceNotification(Resource resource) {
        for (ResourceChangeListener rcl :  resourceChangeListeners) {
                rcl.addResource(resource);
        }
    }
   
   private void removeResourceNotification(Resource resource) {
        for (ResourceChangeListener rcl :  resourceChangeListeners) {
                rcl.removeResource(resource);
        }
    }
}
