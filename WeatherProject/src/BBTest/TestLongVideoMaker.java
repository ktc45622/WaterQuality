package BBTest;

/**
 * This file is to test the LongVideoMaker
 */

import java.sql.Date;
import java.util.GregorianCalendar;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;
import weather.serverside.FFMPEG.LongVideoMaker;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

public class TestLongVideoMaker {

    public static void main(String[] args) {
        
        Debug.setEnabled(true);
        
        //Place hour-long videos.
        int videoResourceNumber = 102;

        //Setup local storage
        DBMSSystemManager dbms = null;
        String fileSystemRoot = "C:";

        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        StorageControlSystemLocalImpl localServer = new StorageControlSystemLocalImpl(dbms, fileSystemRoot);
        
        //Get resource for LongVideoMaker.
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
        
        //Loop through test day.
        for (int i = 0; i < 24; i++) {
            if (i == 0 || i == 6 || i == 7 || i == 23) {
                GregorianCalendar endVideoCalendar = new GregorianCalendar();
                endVideoCalendar.set(2016, GregorianCalendar.MAY, 28, i, 59, 59);
                endVideoCalendar.set(GregorianCalendar.MILLISECOND, 999);

                Date endVideoDate = new Date(endVideoCalendar.getTimeInMillis());

                LongVideoMaker lvm = new LongVideoMaker(localServer, movieResource,
                        endVideoDate, true, "a testing program", "Test-Code");
                if (lvm.createDayLongVideo()) {
                    Debug.println("Video made with end time: " + CalendarFormatter
                            .format(endVideoCalendar));
                } else {
                    Debug.println("Video NOT made at: " + CalendarFormatter
                            .format(endVideoCalendar));
                    break;
                }
                Debug.println("Video made");
            }
        }
    }
}
