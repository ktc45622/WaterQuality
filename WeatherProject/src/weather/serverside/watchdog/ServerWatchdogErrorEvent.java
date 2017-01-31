package weather.serverside.watchdog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import weather.common.data.resource.Resource;
import weather.common.utilities.Debug;
import weather.common.utilities.Emailer;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherTracer;

/**
 * A structure for holding information about error found by a ServerWatchdog
 * Note: it may be a good idea to change some of the members from 
 * Strings to ENUMs.
 * 
 * @author Patrick Brinich (2013)
 */
public class ServerWatchdogErrorEvent implements Comparable<ServerWatchdogErrorEvent>,
                                                 java.io.Serializable {
    
    private static final long serialVersionUID = 1;
    /**
     * The name system that caused the event
     */
    private String system;
    /**
     * The description of the error
     */
    private String error;
    /**
     * The description of the action taken
     */
    private String action;
    /**
     * Additional information
     */
    private String info;
    /**
     * The resource that caused the error--<code>null</code> if no resource
     */
    private Resource resource;
    /**
     * The time the error happened
     */
    private long time;
    /**
     * The logger
     */
    private static final WeatherTracer log = WeatherTracer.getWatchdogLog();

    /**
     * Constructs a new ServerWatchdogErrorEvent at the current time.
     * @param system The name system that caused the event
     * @param error The description of the action taken
     * @param action Additional information
     * @param info Additional information
     * @param resource The resource that caused the error--<code>null</code> if no resource
     */
    public ServerWatchdogErrorEvent(String system, String error, String action, String info, Resource resource) {
        this.system = system;
        this.error = error;
        this.action = action;
        this.info = info;
        this.resource = resource;

        this.time = System.currentTimeMillis();
    }

    /**
     * Constructs a new ServerWatchdogErrorEvent
     * @param system The name system that caused the event
     * @param error The description of the action taken
     * @param action Additional information
     * @param info Additional information
     * @param resource The resource that caused the error--<code>null</code> if no resource
     * @param time The time the error was found
     */
    public ServerWatchdogErrorEvent(String system, String error, String action, String info, Resource resource, long time) {
        this.system = system;
        this.error = error;
        this.action = action;
        this.info = info;
        this.resource = resource;
        this.time = time;
    }
    
    /**
     * Notifies the admin that the error occurred.
     */
    public void notifyAdmin() {
        String subject = "Server Watchdog: " + system + " System Error";
        if (resource != null) {
            subject = "Server Watchdog: Error on " + system 
                    + " for resource " + resource.getResourceName();
        }
        
        String message = "This is an automated message from the Server Watchdog.\n";
        message += buildMessage();
        
        try {
            Emailer.emailAdmin(message, subject);
            log.fine ("Administrators have been notified by email.");
            Debug.println("Administrators have been notified by email.") ;       
        }
        catch(WeatherException | NullPointerException ex) {
            log.severe ("Error emailing admins.", ex);
            Debug.println ("Error while trying to email Administrators.");
        }
    }
    
    /**
     * Logs the error information
     */
    public void logError() {
        log.severe(buildMessage());
    }
    
    /**
     * Prints the error information to the debug console
     */
    public void printErrorToDebug() {
        Debug.println(buildMessage());
    }

    /**
     * Calculates the hash code for this ServerWatchdogErrorEvent
     * @return a unique hash code
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.system);
        hash = 59 * hash + Objects.hashCode(this.error);
        hash = 59 * hash + Objects.hashCode(this.action);
        hash = 59 * hash + Objects.hashCode(this.info);
        hash = 59 * hash + Objects.hashCode(this.resource); //Zero if null
        hash = 59 * hash + (int) (this.time ^ (this.time >>> 32));
        return hash;
    }

    /**
     * Tests for equality between this and another object
     * @param obj the object to compare
     * @return true if <code>obj</code> is a ServerWatchdogErrorEvent and both are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServerWatchdogErrorEvent other = (ServerWatchdogErrorEvent) obj;
        return this.identical(other);
    }

    /**
     * Compares this ServerWatchdogErrorEvent to another. This method is consistent
     * with equals. ServerWatchdogEvents with a more recent time are greater
     * than ones with a less recent time. If the times are equal, but the the
     * two objects are not identical, this ServerWatchdogErrorEvent is less than the
     * other.
     *
     * @param other the ServerWatchdogErrorEvent to compare to
     * @return A negative integer if this is less than <code>o</code>; zero, if
     * equal; and a positive integer, if greater than <code>o</code>.
     */
    @Override
    public int compareTo(ServerWatchdogErrorEvent other) {
        final int LESSTHAN = -1;
        final int EQUAL = 0;
        final int GREATERTHAN = 1;

        if (this.time < other.time) {
            return LESSTHAN;
        } else if (this.time > other.time) {
            return GREATERTHAN;
        } else if (this.equals(other)) {
            return EQUAL;
        } else {
            return LESSTHAN;
        }
    }

    /**
     * Gets the name of the system that caused the error.
     * @return the name of the system. 
     */
    public String getSystem() {
        return system;
    }

    /**
     * Gets the error message. 
     * @return the error message.
     */
    public String getError() {
        return error;
    }

    /**
     * Gets the description of the action taken.
     * @return a string containing the action description.
     */
    public String getAction() {
        return action;
    }

    /**
     * Get the additional info associated with this error event.
     * @return a string containing the additional info.
     */
    public String getInfo() {
        return info;
    }

    /**
     * Gets the Resource that caused this error event.
     * @return the Resource that caused the error. Will be <code>null</code> if
     * there was no resource associated with the error
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Gets the time of this error event.
     * @return the time in milliseconds.
     */
    public long getTime() {
        return time;
    }

    
    
    /**
     * Tests to see if this and another ServerWatchdogErrorEvent are identical
     * @param other the other ServerWatchdogErrorEvent to test
     * @return true if they are identical; false otherwise.
     */
    private boolean identical(ServerWatchdogErrorEvent other) {
        boolean retVal = this.system.equals(other.system)
                && this.error.equals(other.error)
                && this.action.equals(other.action)
                && this.info.equals(other.info)
                && this.time == other.time;
        if (resource != null) {
            retVal = retVal
                    && this.resource.identical(other.resource);
        }

        return retVal;
    }

    /**
     * Builds the error message for various methods of the class
     * @return the error message for this ServerWatchdogErrorEvent
     */
    private String buildMessage() {
        String message;
        message = "The server watchdog has found an error.\n"
                + "System: " + system + "\n";
        if (resource != null) {
            message += "Resource: " + resource.getResourceName() + ", " + resource.getResourceNumber() + "\n";
        }
        message += "Error: "  + error  + "\n"
                 + "Action: " + action + "\n";
        

        DateFormat formatDate, formatTime;
        Date date;
        formatDate = new SimpleDateFormat("MM-dd-yyyy");
        formatTime = new SimpleDateFormat("hh:mm:ss a");
        date = new Date(time);

        message +=
                  "Date: " + formatDate.format(date) + "\n"
                + "Time: " + formatTime.format(date) + "\n"
                + "\n"
                + info +"\n";
        return message;
    }
}
