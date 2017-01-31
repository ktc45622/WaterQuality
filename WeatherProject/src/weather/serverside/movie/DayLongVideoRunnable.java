package weather.serverside.movie;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;
import weather.common.utilities.Emailer;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.serverside.FFMPEG.LongVideoMaker;

/**
 * The Runnable that creates movies for the <code>DayLongVideoScheduler</code>.
 * It will not do so past a certain number a minutes into each hour, which is 
 * set by database properties.  If a run is tried after that point, an email
 * will be sent to all administrators.
 *
 * @author Brian Bankes
 */
public class DayLongVideoRunnable implements Runnable {
    /**
     * The Resource from which a movie will be made.
     */
    private final Resource resource;
    /**
     * The storage system to which the movie will be saved.  This currently must
     * be a local system (1/27/16).
     */
    private final StorageControlSystem storageSystem;
    /**
     * The number of minutes past the hour beyond which the runnable cannot run.
     * If a run is requested later than this, an error email will be sent to all
     * administrators.
     */
    private static final int LAST_RUNNABLE_MINUTE = 
            Integer.parseInt(PropertyManager
            .getGeneralProperty("rangeRetrieveGracePeriod"))
            + Integer.parseInt(PropertyManager
            .getGeneralProperty("LVM_WAIT_MAX"));

    /**
     * Constructor.
     *
     * @param resource The specified Resource.
     * @param storageSystem The storage system the movie will save to.
     */
    public DayLongVideoRunnable(Resource resource, 
            StorageControlSystem storageSystem) {
        this.resource = resource;
        this.storageSystem = storageSystem;
    }
    
     /**
     * Sends an email error notification to all administrators if the day-long
     * videos cannot be made because too much time has elapsed since the start
     * of the hour
     * 
     * @param endOfAttemptedVideo A Gregorian Calendar holding the time that the
     * video to be made was supposed to end.
     * @param attemptedRunTime A Gregorian Calendar holding the time that the
     * Thread was supposed to run.
     * @param lastPossibleRunTime A Gregorian Calendar holding the latest time 
     * that the Thread could have run.
     */
    private void sendOutOfTimerMessage(GregorianCalendar endOfAttemptedVideo,
            GregorianCalendar attemptedRunTime, 
            GregorianCalendar lastPossibleRunTime) {
        //Make other calendar for email.
        GregorianCalendar startOfAttemptedVideo = (GregorianCalendar) 
                endOfAttemptedVideo.clone();
        startOfAttemptedVideo.set(GregorianCalendar.HOUR_OF_DAY, 0);
        startOfAttemptedVideo.set(GregorianCalendar.MINUTE, 0);
        startOfAttemptedVideo.set(GregorianCalendar.SECOND, 0);
        startOfAttemptedVideo.set(GregorianCalendar.MILLISECOND, 0);
        
        //Construct email subject and text.
        StringBuilder subject = new StringBuilder();
        subject.append("BU Weather Viewer Unable to Create Day-Long Video ");
        subject.append("for ").append(resource.getResourceName());
        StringBuilder message = new StringBuilder();
        message.append("The server was unable to make a day-long video.  ");
        message.append("The window time during which the server can make the ");
        message.append("video has expired.  ");
        message.append("Please see the details below:\n");
        message.append("Resource: ").append(resource.getResourceName());
        message.append(" (#").append(resource.getResourceNumber()).append(")");
        message.append("\nStart of attempted video: ");
        message.append(CalendarFormatter.format(startOfAttemptedVideo));
        message.append("\nEnd of attempted video: ");
        message.append(CalendarFormatter.format(endOfAttemptedVideo));
        message.append("\nAttempted run time: ");
        message.append(CalendarFormatter.format(attemptedRunTime));
        message.append("\nLast possible run time: ");
        message.append(CalendarFormatter.format(lastPossibleRunTime));
        
        //Send email.
        try {
            Emailer.emailAdmin(message.toString(), subject.toString());
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE,
                    "Day-long video maker unable to send error email message.");
            Debug.println(
                    "Day-long video maker unable to send error email message.");
        }
    }

    /**
     * Builds and saves the movie when thread runs.
     */
    @Override
    public void run() {
        //Calculate the end of the most recent hour to have passed from the 
        //current time.
        GregorianCalendar endOfNextVideo = new GregorianCalendar();
        endOfNextVideo.add(GregorianCalendar.HOUR, -1);
        endOfNextVideo.set(GregorianCalendar.MINUTE, 59);
        endOfNextVideo.set(GregorianCalendar.SECOND, 59);
        endOfNextVideo.set(GregorianCalendar.MILLISECOND, 999);
        
        //Test if it is too late in the hour to make the video and send error
        //emails if so.
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar lastPossibleRunTime = (GregorianCalendar) 
                now.clone();
        lastPossibleRunTime.set(GregorianCalendar.MINUTE, 
                LAST_RUNNABLE_MINUTE);
        lastPossibleRunTime.set(GregorianCalendar.SECOND, 0);
        lastPossibleRunTime.set(GregorianCalendar.MILLISECOND, 0); 
        if (now.getTimeInMillis() > lastPossibleRunTime.getTimeInMillis()) {
            sendOutOfTimerMessage(endOfNextVideo, now, lastPossibleRunTime);
            return;
        }
        
        //Setup video maker.
        Date endTime = new Date(endOfNextVideo.getTimeInMillis());
        LongVideoMaker lvm = new LongVideoMaker(storageSystem, resource,
            endTime, true, "the day-long runnable thread", "Runnable");
        
        //Try to make video and log any error.
        if (!lvm.createDayLongVideo()) {
            WeatherLogger.log(Level.SEVERE, "A day-long video was not made for "
                        + resource.getName());
            Debug.println("A day-long video was NOT made for " 
                    + resource.getName());
        }
    }
}