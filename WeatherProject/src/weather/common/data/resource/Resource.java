package weather.common.data.resource;

import java.io.Serializable;
import java.sql.Date;
import weather.common.data.RetrievalMethod;

/**
 * Represents a data source.
 *
 * A weather resource is representation of any data source that we obtain data
 * from for use in the Weather Program. Examples would be a web camera, a
 * Internet site, or a weather station. This class specifies what kind of
 * resource it represents, as well as keeps track of all of the specific
 * information for that resource including the name of the resource, retrieval
 * frequency, resource type, so on and so forth. To study this class more in
 * depth, examine the Edit Resources feature while running the main program. All
 * fields that are specified while editing resources are also specified in this
 * class.
 *
 * An instance of this class will normally represent one row of data in our
 * Weather Resource Table.
 *
 * NOTE: Information for a resource is stored in both the resources and
 * time_zone_information tables. SQL statements must get data from both of these
 * tables.
 *
 * NOTE: The orderRank field is used to determine to order in which resources
 * will be listed. An orderRank of 0 indicates a resource not saved in the
 * database, as does a resourceNumber of -1.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Joe Sharp (2009)
 * @author Bingchen Yan (2012)
 * @version Spring 2012
 *
 */
public class Resource implements Serializable {

    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version of this
     * class is not compatible with old versions. See Sun documents for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but included here
     * as a reminder of its importance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Resource Number, this is the number that will uniquely identify instances
     * of this class throughout the program and in the database. A value of -1
     * means that the number is not yet specified.
     */
    private int resourceNumber = -1;
    /**
     * Weather resource type. See WeatherResourceType.java for the enumeration
     * of available Weather resource types.
     */
    private WeatherResourceType resourceType;
    /**
     * Retrieval method. See RetrievalMethod.java for more info. Keeps track of
     * how this resource is retrieved.
     */
    private RetrievalMethod accessMethod;
    /**
     * Name of the folder where this resource is stored in the storage. system.
     */
    private String storageFolderName = "";
    /**
     * The format type of the file.
     */
    private ResourceFileFormatType format;
    /**
     * URL to obtain this data.
     */
    private java.net.URL URL;
    /**
     * How many seconds between resource instance acquisition.
     */
    private int frequency;
    /**
     * True when we are currently collecting data.
     */
    private boolean active = true;
    /**
     * Is resource visible to students and guests?
     */
    private boolean visible = false;
    /**
     * Date we started obtaining data.
     */
    private Date dateInitiated = null;
    /**
     * The name of this weather resource.
     */
    private String resourceName = null;
    /**
     * The range of times to collect this resource.
     */
    private ResourceCollectionSpan collectionSpan;
    /**
     * The time this resource begins being acquired during the day. This is used
     * only if the "Specified Times" ResourceCollectionSpan is selected, but it
     * should be set to 0 if another option in used. This integer specifies the
     * hour of the day from 0 to 23 and is in the resource time zone.
     */
    private int startTime;
    /**
     * The time this resource stops being acquired during the day. This is used
     * only if the "Specified Times" ResourceCollectionSpan is selected, but it
     * should be set to 0 if another option in used. This integer specifies the
     * hour of the day from 0 to 23 and is in the resource time zone. For this
     * field, 0 means the end of the day, although it is actually the start of
     * the next day.
     */
    private int endTime;
    /**
     * The time that the program should check for updates to the dimensions of
     * the images produced by this resource if it is a video-making resource.
     * This integer specifies the hour of the day from 0 to 23 and is in the
     * resource time zone. The default value is -1, which means "Never." This
     * field should be -1 if the resource does not make videos.
     */
    private int updateHour;
    /**
     * The longitude for the location of this resource (may be negative).
     */
    private float longitude;
    /**
     * The latitude for the location of this resource (may be negative).
     */
    private float latitude;
    /**
     * Time zone.
     */
    private ResourceTimeZone timeZone;
    /**
     * Image width for video-making resources.
     */
    private int imageWidth;
    /**
     * Image height for video-making resources.
     */
    private int imageHeight;
    /**
     * Used to order resources.
     */
    private int orderRank;

    /**
     * Constructs a resource object.
     *
     * @param resourceNumber The number of the resource.
     * @param resourceType The type of the resource.
     * @param resourceName The name of the resource.
     * @param accessMethod The way the resource is accessed.
     * @param storageFolderName The name of the storage folder.
     * @param format The format of the resource.
     * @param URL The URL of the resource.
     * @param frequency The frequency of the resource.
     * @param active Boolean value, True if the resource is active, false
     * otherwise.
     * @param visible Boolean value, True if the resource is visible, false
     * otherwise.
     * @param dateInitiated The date the resource was initiated.
     * @param collectionSpan The collection span of resources to use.
     * @param startTime The time to start keeping this resource. This should be
     * 0 if the resource is not using the "Specified Times" collection option.
     * This value is in the resource time zone.
     * @param endTime The time to stop keeping this resource. This should be 0
     * if the resource is not using the "Specified Times" collection option.
     * This value is in the resource time zone.
     * @param latitude The latitude of the resource.
     * @param longitude The longitude of the resource.
     * @param timeZone The time zone.
     * @param imageWidth The image width for video-making resources. It should
     * be ignored for resources that do not make videos.
     * @param imageHeight The image height for video-making resources. It should
     * be ignored for resources that do not make videos.
     * @param orderRank The order rank.
     * @param updateHour The hour of the day, in resource time, when the program
     * should check for changes to the dimensions of images produced by video-
     * making resources. This should be -1 if no checking is to be done.
     *
     */
    public Resource(int resourceNumber, WeatherResourceType resourceType,
            String resourceName,
            RetrievalMethod accessMethod, String storageFolderName,
            ResourceFileFormatType format, java.net.URL URL, int frequency,
            boolean active, boolean visible, Date dateInitiated,
            ResourceCollectionSpan collectionSpan, int startTime, int endTime,
            float longitude, float latitude, ResourceTimeZone timeZone,
            int imageWidth, int imageHeight, int orderRank, int updateHour) {
        this.resourceNumber = resourceNumber;
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.accessMethod = accessMethod;
        this.storageFolderName = storageFolderName;
        this.format = format;
        this.URL = URL;
        this.frequency = frequency;
        this.active = active;
        this.visible = visible;
        this.dateInitiated = dateInitiated;
        this.collectionSpan = collectionSpan;
        this.startTime = startTime;
        this.endTime = endTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timeZone = timeZone;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.orderRank = orderRank;
        this.updateHour = updateHour;
    }

    /**
     * Copy Constructor.
     *
     * @param re The Resource needed to be copied.
     */
    public Resource(Resource re) {
        this.resourceNumber = re.getResourceNumber();
        this.resourceType = re.getResourceType();
        this.resourceName = re.getResourceName();
        this.accessMethod = re.getAccessMethod();
        this.storageFolderName = re.getStorageFolderName();
        this.format = re.getFormat();
        this.URL = re.getURL();
        this.frequency = re.getFrequency();
        this.active = re.isActive();
        this.visible = re.isVisible();
        this.dateInitiated = re.getDateInitiated();
        this.collectionSpan = re.getCollectionSpan();
        this.startTime = re.getStartTime();
        this.endTime = re.getEndTime();
        this.longitude = re.getLongitude();
        this.latitude = re.getLatitude();
        this.timeZone = re.getTimeZone();
        this.imageWidth = re.getImageWidth();
        this.imageHeight = re.getImageHeight();
        this.orderRank = re.getOrderRank();
        this.updateHour = re.getUpdateHour();
    }

    /**
     * Default constructor.
     */
    public Resource() {
        this.resourceNumber = -1;
        this.resourceType = WeatherResourceType.undefined;
        this.resourceName = "Not Specified";
        this.accessMethod = RetrievalMethod.undefined;
        this.storageFolderName = "Not Specified";
        this.format = ResourceFileFormatType.unknown;
        this.URL = null;
        this.frequency = -1;
        this.active = false;
        this.visible = false;
        this.dateInitiated = new java.sql.Date(System.currentTimeMillis());
        this.collectionSpan = null;
        this.startTime = 0;
        this.endTime = 0;
        this.longitude = 0;
        this.latitude = 0;
        this.timeZone = ResourceTimeZone.UTC;
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.orderRank = 0;
        this.updateHour = -1;
    }

    /**
     * Overrides Object.equals(Object) method, checks to see if two resources
     * are equal.
     *
     * @param o The resource object.
     * @return True if the resources are equal, false if they are not.
     *
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } // Self Reference?
        if (o instanceof Resource) {
            Resource other = (Resource) o;
            return other.hashCode() == this.hashCode();
        } else {
            return false;
        }
    }

    /**
     * This method determines if two resources have identical values in all
     * fields.
     *
     * @param otherResource The other resource object.
     * @return True if the resources are identical in all fields, false if one
     * or more fields are different.
     */
    public boolean identical(Resource otherResource) {
        if (this.resourceName.equals(otherResource.getResourceName())
                && this.resourceNumber == otherResource.getResourceNumber()
                && this.resourceType.equals(otherResource.getResourceType())
                && this.storageFolderName.equals(otherResource.getStorageFolderName())
                && this.accessMethod.equals(otherResource.getAccessMethod())
                && this.URL.equals(otherResource.getURL())
                && this.frequency == otherResource.getFrequency()
                && this.active == otherResource.isActive()
                && this.visible == otherResource.isVisible()
                && this.format.equals(otherResource.getFormat())
                && this.timeZone.equals(otherResource.getTimeZone())
                && this.latitude == otherResource.getLatitude()
                && this.longitude == otherResource.getLongitude()
                && this.collectionSpan.equals(otherResource.getCollectionSpan())
                && this.startTime == otherResource.getStartTime()
                && this.endTime == otherResource.getEndTime()
                && this.imageWidth == otherResource.getImageWidth()
                && this.imageHeight == otherResource.getImageHeight()
                && this.orderRank == otherResource.getOrderRank()
                && this.updateHour == otherResource.getUpdateHour()) {
            return true;
        }
        return false;
    }

    /**
     * Overrides Object.hashCode() method.
     *
     * Returns a numerical representation of this object.
     *
     * @return A numerical representation of this object.
     */
    @Override
    public int hashCode() {
        return this.getResourceNumber();
    }

    /**
     * Sets the name of the resource.
     *
     * @param name The name of the resource.
     */
    public void setName(String name) {
        this.resourceName = name;
    }

    /**
     * Returns the name of the resource.
     *
     * @return The name of the resource.
     */
    public String getName() {
        return this.resourceName;
    }

    /**
     * Sets the number of the resource.
     *
     * @param resourceNumber The resource number for this resource.
     */
    public void setResourceNumber(int resourceNumber) {
        this.resourceNumber = resourceNumber;
    }

    /**
     * Returns the number of the resource.
     *
     * @return The number of the resource.
     */
    public int getResourceNumber() {
        return this.resourceNumber;
    }

    /**
     * Sets the type of the resource.
     *
     * @param type The type of the resource.
     */
    public void setResourceType(WeatherResourceType type) {
        this.resourceType = type;
    }

    /**
     * Returns the type of the resource.
     *
     * @return The type of the resource.
     */
    public WeatherResourceType getResourceType() {
        return this.resourceType;
    }

    /**
     * Sets the access method of the resource.
     *
     * @param accessMethod The method used to access the resource.
     */
    public void setAccessMethod(RetrievalMethod accessMethod) {
        this.accessMethod = accessMethod;
    }

    /**
     * Returns the access method of the resource.
     *
     * @return The access method of the resource.
     */
    public RetrievalMethod getAccessMethod() {
        return this.accessMethod;
    }

    /**
     * Sets the folder name the resource is stored in.
     *
     * @param storageFolderName The storage name of the folder.
     */
    public void setStorageFolderName(String storageFolderName) {
        this.storageFolderName = storageFolderName;
    }

    /**
     * Returns the storage folder name of the resource.
     *
     * @return The folder the resource is stored in.
     */
    public String getStorageFolderName() {
        return this.storageFolderName;
    }

    /**
     * Sets the format of the resource.
     *
     * @param format The format of the resource.
     */
    public void setFormat(ResourceFileFormatType format) {
        this.format = format;
    }

    /**
     * Returns the format of the resource.
     *
     * @return The format of the resource.
     */
    public ResourceFileFormatType getFormat() {
        return this.format;
    }

    /**
     * Sets the URL of the resource.
     *
     * @param URL The URL of the resource.
     */
    public void setURL(java.net.URL URL) {
        this.URL = URL;
    }

    /**
     * Returns the URL of the resource.
     *
     * @return The URL of the resource.
     */
    public java.net.URL getURL() {
        return this.URL;
    }

    /**
     * Set number of seconds between resource instance acquisition.
     *
     * @param frequency The frequency of the resource.
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Returns number of seconds between resource instance acquisition.
     *
     * @return The frequency of the resource.
     */
    public int getFrequency() {
        return this.frequency;
    }

    /**
     * Sets whether the resource is active or inactive.
     *
     * @param active Sets the value true if the resource is active, false
     * otherwise.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns true if the resource is active, false otherwise.
     *
     * @return True if the resource is active, false if inactive.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets the resource as visible or invisible.
     *
     * @param visible True if the resource is visible, false otherwise.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns true if the resource is visible, false otherwise.
     *
     * @return True if the resource is visible, false otherwise.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets the date the resource was initiated.
     *
     * @param dateInitiated The date the resource was initiated.
     */
    public void setDateInitiated(java.sql.Date dateInitiated) {
        this.dateInitiated = dateInitiated;
    }

    /**
     * Returns the date the resource was initiated.
     *
     * @return The date the resource was initiated.
     */
    public java.sql.Date getDateInitiated() {
        return this.dateInitiated;
    }

    /**
     * Returns the time the instance ends at. This should be 0 if the resource
     * is not using the "Specified Times" collection option.
     *
     * @return The time the instance ends at as an integer from 0 to 23. This
     * should be 0 if the resource is not using the "Specified Times" collection
     * option. This value is in the resource time zone.  NOTE: A value of 0 
     * means the end of the day.
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Sets the time the instance is to end at. This should be 0 if the resource
     * is not using the "Specified Times" collection option.
     *
     * @param endTime The time the instance ends at as an integer from 0 to 23.
     * This should be 0 if the resource is not using the "Specified Times"
     * collection option. This value is in the resource time zone.  NOTE: A 
     * value of 0 means the end of the day.
     */
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns the name of the resource.
     *
     * @return The resource name.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets the name of the resource.
     *
     * @param resourceName The name of the resource.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Gets the range of times to collect this resource.
     *
     * @return The collection span of the resource.
     */
    public ResourceCollectionSpan getCollectionSpan() {
        return collectionSpan;
    }

    /**
     * Sets the range of times to collect this resource.
     *
     * @param collectionSpan The collection span of the resource.
     */
    public void setCollectionSpan(ResourceCollectionSpan collectionSpan) {
        this.collectionSpan = collectionSpan;
    }

    /**
     * Returns the time the instance starts at. This should be 0 if the resource
     * is not using the "Specified Times" collection option.
     *
     * @return The time the instance starts at as an integer from 0 to 23. This
     * should be 0 if the resource is not using the "Specified Times" collection
     * option. This value is in the resource time zone.
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Sets the time the instance is to starts at. This should be 0 if the
     * resource is not using the "Specified Times" collection option.
     *
     * @param startTime The time the instance starts at as an integer from 0 to
     * 23. This should be 0 if the resource is not using the "Specified Times"
     * collection option. This value is in the resource time zone.
     */
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the latitude of the resource.
     *
     * @return The latitude of the resource.
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the resource.
     *
     * @param latitude The latitude of the resource.
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of the resource.
     *
     * @return The longitude of the resource.
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the resource.
     *
     * @param longitude The longitude of the resource.
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Get time zone.
     *
     * @return The time zone.
     */
    public ResourceTimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Set the time zone.
     *
     * @param timeZone The time zone.
     */
    public void setTimeZone(ResourceTimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Get the order rank of this resource.
     *
     * @return The order rank.
     */
    public int getOrderRank() {
        return orderRank;
    }

    /**
     * Sets the order rank of this resource.
     *
     * @param orderRank The new order rank.
     */
    public void setOrderRank(int orderRank) {
        this.orderRank = orderRank;
    }

    /**
     * Get the image width of this resource.
     *
     * @return The image width.
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * Sets the image width of this resource.
     *
     * @param imageWidth The new image width.
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * Get the image height of this resource.
     *
     * @return The image height.
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Sets the image height of this resource.
     *
     * @param imageHeight The new image height.
     */
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
    
    /**
     * Gets the hour at which checking for image dimensions takes places.
     * 
     * @return The time that the program should check for updates to the
     * dimensions of the images produced by this resource if it is a
     * video-making resource. This value specifies the hour of the day from 0 to
     * 23 and is in the resource time zone. The default value is -1, which means
     * "Never." This field should be -1 if the resource does not make videos.
     */
    public int getUpdateHour() {
        return this.updateHour;
    }
    
    /**
     * Sets the hour at which checking for image dimensions takes places.
     * 
     * @param updateHour The time that the program should check for updates to
     * the dimensions of the images produced by this resource if it is a
     * video-making resource. This value specifies the hour of the day from 0 to
     * 23 and is in the resource time zone. The default value is -1, which means
     * "Never." This field should be -1 if the resource does not make videos.
     */
    public void setUpdateHour(int updateHour) {
        this.updateHour = updateHour;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Resource:\n");
        sb.append("resourceName: " + this.resourceName + "\n");
        sb.append("resourceNumber: " + this.resourceNumber + "\n");
        sb.append("storageFolderName: " + this.storageFolderName + "\n");
        sb.append("accessMethod: " + this.accessMethod + "\n");
        sb.append("URL: " + this.URL + "\n");
        sb.append("frequency: " + this.frequency + "\n");
        sb.append("active: " + this.active + "\n");
        sb.append("visible: " + this.visible + "\n");
        sb.append("format: " + this.format + "\n");
        sb.append("timeZone: " + this.timeZone + "\n");
        sb.append("latitude: " + this.latitude + "\n");
        sb.append("longitude: " + this.longitude + "\n");
        sb.append("collectionSpan: " + this.collectionSpan + "\n");
        sb.append("startTime: " + this.startTime + "\n");
        sb.append("endTime: " + this.endTime + "\n");
        sb.append("imageWidth: " + this.imageWidth + "\n");
        sb.append("imageHeight: " + this.imageHeight + "\n");
        sb.append("orderRank: " + this.orderRank + "\n");
        sb.append("updateHour: " + this.updateHour + "\n");
        return sb.toString();
    }
}
