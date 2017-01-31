package weather.common.data.resource;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import weather.common.utilities.Debug;

/**
 * Specifies a date and time range.
 *
 * Usually it will be used to specify
 * what resource instances are being requested from the file storage system. 
 * Specify the range of times. For example a user would specify
 * that resource instances are being requested between 
 * 1/2/2008 10:00 p.m. and 1/2/2008 10:20 p.m.
 *
 * @author Bloomsburg University Software Engineering
 * @author Dave Moser (2008)
 * @version Spring 2008
 */
public class ResourceRange implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * The sql.Date object representing the beginning time
     * for the range of resource instances we are requesting.
     */
    private Date startTime;
    /**
     * The sql.Date object representing the ending time
     * for the range of resource instances we are requesting.
     */
    private Date stopTime;

    /**
     * Constructs a ResourceRange object.
     * Note: The Date objects are sql.Date objects, not util.Date objects.
     *
     * @param startTime The date the resource is started.
     * @param stopTime The date the resource is stopped.
     */
    public ResourceRange(Date startTime, Date stopTime) {
        this.setStartTime(startTime);
        this.setStopTime(stopTime);
    }

    /**
     * Returns the beginning time for the range of resource instances
     * requested.
     *
     * @return The start time of the resource range.
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the starting time for the resource range. 
     *
     * @param startTime The beginning time of this resource range.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the ending time of this resource range.
     *
     * @return The ending time of this resource range.
     */
    public Date getStopTime() {
        return stopTime;
    }

    /**
     * Sets the ending time of this resource range.
     *
     * @param stopTime The ending time of this resource range.
     */
    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    /**
     * This method compares the given ResourceRange with this resource range
     * and returns true if the start and end times for both ResourceRanges
     * are the same.
     * @param compareTo The ResourceRange to compare to.
     * @return True if the given ResourceRange is equal this ResourceRange.
     */
    public boolean equals(ResourceRange compareTo) {
        if (this.getStartTime().getTime() == compareTo.getStartTime().getTime()
                && this.getStopTime().getTime() == compareTo.getStopTime().
                getTime()) {
            return true;
        }
        return false;
    }
    
     /**
     * This method compares the given ResourceRange with this resource range
     * and returns true if the start and end times for both ResourceRanges
     * specify the same HOURS.
     * @param compareTo The ResourceRange to compare to.
     * @return True if the given ResourceRange is equal this ResourceRange.
     */
    public boolean isSameHours(ResourceRange compareTo) {
       Debug.print("Range compare (this): ");
       String thisString = this.getString();
       Debug.println(thisString);
       Debug.print("Range compare (compareTo): ");
       String compareToString = compareTo.getString();
       Debug.println(thisString);
       return thisString.equals(compareToString);
    }
    
    /**
     * This method produces a string representation of the ResourceRange in the
     * form "M/dd/yyyy hh:00a to M/dd/yyyy hh:00a"
     */
    
    public String getString() {
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("M/dd/yyyy hh:00a");
        builder.append(format.format(getStartTime()).toString());
        builder.append(" to ");
        builder.append(format.format(getStopTime()).toString());
        return builder.toString();
    }
}
