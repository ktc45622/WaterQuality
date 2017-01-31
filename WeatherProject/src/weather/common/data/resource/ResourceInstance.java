package weather.common.data.resource;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import weather.common.utilities.WeatherException;

/**
 * A class that represents an instance of a weather resource. 
 * For example, it could be a picture from a weather
 * camera or site. It could also represent data from a 
 * weather station. This class is Abstract and Serializable.
 *
 *@author Bloomsburg University Software Engineering 
 *@author Anthony Tersine (2007)
 *@author Daniel Wieand (2008)
 *@author David Reichert (2008)
 *@author Joseph Horro (2011)
 *@version Spring 2011
 */
public abstract class ResourceInstance implements java.io.Serializable {

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
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The starting and ending time for this resource instance. The values in 
     * this variable will be a single instance in time for a  picture and a 
     * range for a movie or for a weather station instance. 
     * This field is used when the server sends information to the client.
     * @serial
     */
    private ResourceRange resourceRange;
    
    /**
     * The date and time this resource instance was created. This information
     * is used to help determine the path information in the storage system.
     * @serial
     */
    private Date time;
    
    /**
     * The type of this weather resource.
     * @serial
     */
    private WeatherResourceType resourceType;
    
    /**
     * The resourceNumber is an identifier that specifies the weather resource 
     * that provided this resource instance. It is the primary key of the 
     * weather resource table maintained in our database. It allows us to 
     * obtain information about the weather source
     * that generated this data instance.
     * @serial
     */
    private int resourceNumber;
    
    /**
     * The name of the file in our storage system that will contain 
     * this resource instance. 
     * This identifier must contain a legal file name.
     * The file name is normally based on the name of the weather
     * resource that is being used to generate the data instances along with a
     * time stamp appended to the end of the file.
     * An example would be: NetCam20071208-090832.jpg.
     * @serial
     */
    private String storageFileName;    //storage file name only (no path info)

    /**
     * The default constructor for the ResourceInstance class.
     */
    public ResourceInstance() {
        this.time = new Date(System.currentTimeMillis());
        this.resourceType = WeatherResourceType.undefined;
        this.resourceNumber = -1;
        this.storageFileName = null;
        //Assume the resource range is the current time
        this.resourceRange = new ResourceRange(this.time, this.time);
    }

    /**
     * A constructor for the ResourceInstance class.
     * @param time The time.
     * @param resourceType The WeatherResourceType.
     * @param resourceNumber The weather source resource number.
     * @param storageFileName The storage file name of the resource instance.
     */
    public ResourceInstance(Date time,
            WeatherResourceType resourceType,
            int resourceNumber, String storageFileName) {
        this.time = time;
        this.resourceType = resourceType;
        this.resourceNumber = resourceNumber;
        this.storageFileName = storageFileName;
        //Assume the resource range is the current time
        this.resourceRange = new ResourceRange(this.time, this.time);
    }

    /**
     * A constructor for the ResourceInstance class.
     * @param resource The desired resource.
     */
    public ResourceInstance(Resource resource) {
        this(); // call default constructor
        this.setResourceNumber(resource.getResourceNumber());
        this.setStorageFileName(resource.getName());
        this.setResourceType(resource.getResourceType());
    }

    /**
     * Abstract method to read from a file with the specified path.
     * @param file The input file with complete path information.
     * @throws WeatherException
     */
    public abstract void readFile(File file) throws WeatherException;

    /**
     * Abstract method to read from a URL with the specified path.
     * @param url The input URL with complete path.
     * @throws IOException
     * @throws WeatherException
     */
    public abstract void readURL(URL url) throws ConnectException, IOException, 
            SocketTimeoutException, WeatherException;

    /**
     * Abstract method that writes the file.
     * @param file The output file with complete path information.
     * @throws WeatherException
     */
    public abstract void writeFile(File file) throws WeatherException;

    /** 
     * A method to read data from a URL given as a string.
     * @param url The location of this resource.
     * @throws IOException 
     * @throws WeatherException
     * @throws MalformedURLException
     */
    public void readURL(String url) 
            throws IOException, MalformedURLException, WeatherException {
        this.readURL(new URL(url));
    }

    /**
     * Returns the number of milliseconds between when the data was collected 
     * for this ResourceInstance and January 1, 1970 0:00:00 GMT.
     * @return The number of milliseconds between when the data was collected 
     * and January 1, 1970 0:00:00 GMT.
     */
    public long getMillis() {
        return time.getTime();
    }

    /**
     * Sets the time the data in this ResourceInstance was collected.
     * @param millis The time the data was collected, in milliseconds since 
     * January 1, 1970 0:00:00 GMT.
     */
    public void setMillis(long millis) {
        this.time = new Date(millis);
    }
    
    /**
     * Returns the date and time the data in this ResourceInstance was collected.
     * @return The date and time the data in this ResourceInstance was collected.
     */
    public Date getTime() {
        return time;
    }
    
    /**
     * Sets the date and time the data in this ResourceInstance was collected.
     * @param time The date and time the data in this ResourceInstance was 
     * collected.
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * Returns the WeatherResourceType of this resource instance.
     * @return The WeatherResourceType of this ResourceInstance.
     */
    public WeatherResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Sets the type of this resource instance.
     * @param resourceType The new WeatherResourceType.
     */
    public void setResourceType(WeatherResourceType resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Returns the resource number of this resource instance.
     * @return The resource number.
     */
    public int getResourceNumber() {
        return resourceNumber;
    }

    /**
     * Sets the resource number of this resource instance.
     * @param resourceNumber The new resource number.
     */
    public void setResourceNumber(int resourceNumber) {
        this.resourceNumber = resourceNumber;
    }

    /**
     * Returns the file name of this resource instance.
     * @return The name of the file.
     */
    public String getStorageFileName() {
        return storageFileName;
    }

    /**
     * Sets the storage file name for this resource instance.
     * @param storageFileName The new file name.
     */
    public void setStorageFileName(String storageFileName) {
        this.storageFileName = storageFileName;
    }

    /**
     * Returns the range of time this resource instance was recorded.
     * @return The range of time this resource instance was recorded.
     */
    public ResourceRange getResourceRange() {
        return resourceRange;
    }

    /**
     * Sets the range of time this resource instance was recorded.
     * @param resourceRange The range of time this resource instance was recorded.
     */
    public void setResourceRange(ResourceRange resourceRange) {
        this.resourceRange = resourceRange;
    }

    /**
     * Returns the time this resource instance stopped being recorded in milliseconds.
     * @return The time this resource instance stopped being recorded in milliseconds.
     */
    public long getEndTime() {
        return getResourceRange().getStopTime().getTime();
    }

    /**
     * Sets the time this resource instance stopped being recorded in milliseconds.
     * @param endTime The time this resource instance stopped being recorded in milliseconds.
     */
    public void setEndTime(long endTime) {
        this.resourceRange.setStopTime(new Date(endTime));
    }

    /**
     * Returns the time this resource instance started being recorded in milliseconds.
     * @return The time this resource instance started being recorded in milliseconds.
     */
    public long getStartTime() {
        return getResourceRange().getStartTime().getTime();
    }

    /**
     * Sets the time this resource instance started being recorded.
     * @param startTime The time this resource instance started being recorded
     * in milliseconds.
     */
    public void setStartTime(long startTime) {
        this.resourceRange.setStartTime(new Date(startTime)); 
    }
    
    @Override
    public String toString(){
        return "\nInstance for resouse #" + this.resourceNumber
                + "\nStorage file name: " + this.storageFileName
                + "\nType: " + this.resourceType.toString()
                + "\n" + this.showDate(time, "Time")
                + this.showResourceRange(resourceRange);
    }
    
    /**
     * Returns a formatted date as a String with a label.  The display will use 
     * the local time zone.
     * @param date The date to be shown.
     * @param name The label
     * @return a formatted date as a String with a label.
     */
    private String showDate(Date date, String name) {
        String dateFormat = "MM/dd/yy hh:mm:ss.SSS a z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        
        if (date == null) {
            return name + ": null\n";
        } else {
            return name + ": " + simpleDateFormat.format(date) + "\n";
        }
    }

    /**
     * Returns a String representation of a ResourceRange.
     * @param range The ResourceRange.
     * @return a String representation of a ResourceRange.
     */
    private String showResourceRange(ResourceRange range) {
        return showDate(range.getStartTime(), "Range Start")
            + showDate(range.getStopTime(), "Range End");
    }
}
