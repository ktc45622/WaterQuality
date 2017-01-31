package BBTest;

import java.io.File;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.resource.MP4Instance;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;

/**
 * NOTE: This test program requires the remote part of the storage system (file
 * system root and storage port) to be configured elsewhere.
 */


public class TestStorageSystemImpl2 {

    public static void main(String[] args) {
        Debug.setEnabled(true);
        
        //Setup resource number.
        int videoResourceNumber = 102;

        //Setup storage system
        StorageControlSystemImpl storage = StorageControlSystemImpl.getStorageSystem();
        
        //Setup test data.
        File testDataLongMP4 = new File("C:\\Generic Movies\\GenericDayVideo.mp4");
        if (testDataLongMP4.exists()) {
            Debug.println("Standard test MP4 data exists.");
        } else {
            Debug.println("Standard test MP4 data does NOT exist.");
            return;
        }
        File testDataLongLQMP4 = new File("C:\\Generic Movies\\GenericDayVideo_low.mp4");
        if (testDataLongLQMP4.exists()) {
            Debug.println("Low Quality test MP4 data exists.");
        } else {
            Debug.println("Low Quality test MP4 data does NOT exist.");
            return;
        }

        //Set range for below loop (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        startCalendar.set(2015, GregorianCalendar.JANUARY, 2, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2015, GregorianCalendar.JANUARY, 3, 0, 0, 0);
        endCalendar.set(GregorianCalendar.MILLISECOND, 0);

        //Store day-long videos.
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.DATE, 1)) {

            MP4Instance mp4Instance1 = new MP4Instance();
            mp4Instance1.setStartTime(loopCalendar.getTimeInMillis());
            mp4Instance1.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_DAY);
            mp4Instance1.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance1.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance1.readFile(testDataLongMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            MP4Instance mp4Instance2 = new MP4Instance();
            mp4Instance2.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance2.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance2.readFile(testDataLongLQMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (storage.placeDayLongMovie(mp4Instance1, mp4Instance2)) {
                Debug.println("Store Succcessful.");
            } else {
                Debug.println("Store NOT Succcessful.");
            }
        }
    }
}
