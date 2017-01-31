package weather.common.data;

import java.io.Serializable;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceRange;

/**
 * This class is used to facilitate the requesting of 
 * resource instances from our file storage system. The class allows the
 * user to set the range of values desired and lists the source
 * of the instances.
 *
 * The constructor parameters are a follows:
 * 1. a ResourceRange - The duration of the request.
 * 2. an integer primitive = The total maximum number of file to be returned by
 * the request.  This is only meaningful for images.
 * 3. a boolean - Whether or not a movie is requested.
 * 4. a ResourceFileFormatType - The type of the resource being requested.
 * 5. a Resource - The resource being requested.
 * 
 * @see weather.common.data.resource.ResourceRange
 * @author Bloomsburg University Software Engineering
 * @author Dave Moser (2008)
 * @version Spring 2008
 */
public class ResourceInstancesRequested implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private ResourceRange resourceRange; 
    private int resourceID; // Instances from this weather resource
    private Resource resource;
    private int numberOfResourceInstancesRequested;
    private boolean wantMovie;
    private ResourceFileFormatType fileType;

    /**
     * Constructor
     * @param range The duration of the request.
     * @param valuesRequested  The total maximum number of file to be returned
     * by the request.  This is only meaningful for images.
     * @param wantMovie Whether or not a movie is requested.  This parameter
     * MUST be set to false if the data being requested is NOT in video form,
     * even if the resource doss not make videos.
     * @param fileType The type of the resource being requested.
     * @param resource The resource being requested.
     */
   public ResourceInstancesRequested(ResourceRange range, int valuesRequested,
           boolean wantMovie, ResourceFileFormatType fileType,
           Resource resource){
        this.resourceRange=range;
        this.numberOfResourceInstancesRequested=valuesRequested;
        this.wantMovie = wantMovie;
        this.fileType = fileType;
       
        if(resource == null){
            this.resourceID=-1;
            this.resource=null;
        }
        else {
            this.resource=resource;
            this.resourceID=resource.getResourceNumber();
        }   
   }

   /**
    * Returns true if a movie is wanted and false otherwise.
    * @return True if a movie is wanted and false otherwise.
    */
    public boolean requestingMovie()    {
        return wantMovie;
    }

    /**
     * Gets the requested resource type.
     * @return The format type.
     */
    public ResourceFileFormatType getFileType()    {
        return fileType;
    }

    /**
     * Sets the requested resource to be the given format type.
     * @param fileType The format type.
     */
    public void setFileType(ResourceFileFormatType fileType)    {
        this.fileType = fileType;
    }

    /**
     * Assigns a value to whether a movie is wanted or not.
     * @param wantMovie True if a movie is wanted, false otherwise.
     */
    public void setWantMovie(boolean wantMovie)    {
        this.wantMovie = wantMovie;
    }

    /**
     * Returns the ResourceRange object contained in this request.
     * @return The ResourceRange object for this request.
     * @see weather.common.data.resource.ResourceRange
     */
    public ResourceRange getResourceRange() {
        return resourceRange;
    }

    /**
     * Sets the ResourceRange object contained in this request.
     * @param resourceRange The resource range to use.
     * @see weather.common.data.resource.ResourceRange
     */
    public void setResourceRange(ResourceRange resourceRange) {
        this.resourceRange = resourceRange;
    }
    
    /**
     * Returns the value of <code>resourceID</code>.
     * @return The resourceID.
     */
    public int getResourceID() {
        return resourceID;
    }

    /**
     * Returns the Resource.
     */
    public Resource getResource(){
        return resource;
    }
    
    /**
     * Returns the SerialVersionUID needed by all serializable objects.
     * @return The ID of this serializable object.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Returns the number of resource instances that are being requested.
     * @return The number of resource instances requested.
     */
    public int getNumberOfResourceInstancesRequested() {
        return numberOfResourceInstancesRequested;
    }

    /**
     * Assigns a value to the number of values that are requested.
     * @param valuesRequested The number of values being requested.
     */
    public void setNumberOfResourceInstanceRequested(int valuesRequested) {
        this.numberOfResourceInstancesRequested = valuesRequested;
    }

    /**
     * Returns a String object that contains the string values of all the
     * data in this class.
     * @return The String object containing all the values in this class.
     */
    @Override
    public String toString() {
        String sReturn = "";
        sReturn += "ID: " + resourceID;
        sReturn += " Num Requested: " + numberOfResourceInstancesRequested;
        sReturn += " Movie: " + wantMovie;
        sReturn += " Start Date: " + resourceRange.getStartTime();
        sReturn += " End Date: " + resourceRange.getStopTime();
        return sReturn;
    }
}
