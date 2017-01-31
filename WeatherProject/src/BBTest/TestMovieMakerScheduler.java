
package BBTest;

import java.io.File;
import java.util.GregorianCalendar;
import weather.StorageControlSystem;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;
import weather.serverside.movie.MovieMakerScheduler;

/**
 * This is a test program for the MovieMakerScheduler.
 */
public class TestMovieMakerScheduler {
    
    public static void main(String[] args) 
            throws ClassNotFoundException, InstantiationException, 
            IllegalAccessException, WeatherException {
        Debug.setEnabled(true);
        
        //Setup variables.
        String fileSystemRoot = "C:";
        DBMSSystemManager dbms =  MySQLImpl.getMySQLDMBSSystem();
        StorageControlSystem storage = new StorageControlSystemLocalImpl(dbms,
            fileSystemRoot);
        MovieMakerScheduler movieMakerScheduler = new MovieMakerScheduler(storage);
        
        /* Setup first resource. */
        
        int resourceNumber1 = 303;
        int minutesBetweenImages1 = 15;
        Resource resource1 = dbms.getResourceManager().getWeatherResourceByNumber(resourceNumber1);
       
        File imageFolder1 = new File("C:\\TestDay-303");
        
        /* Must place data in storage system. */
        
        //Make calendar for below loop.
        GregorianCalendar loopCalendar1 = new GregorianCalendar();
        loopCalendar1.setTimeInMillis(ResourceTimeManager
                .getStartOfDayFromMilliseconds(System.currentTimeMillis(), 
                resource1.getTimeZone().getTimeZone()));
        
        //Place files from image test folder.
        for (File file : imageFolder1.listFiles()) {
            ImageInstance instance = new ImageInstance(file);
            instance.setStartTime(loopCalendar1.getTimeInMillis());
            instance.setResourceNumber(resourceNumber1);
            
            storage.placeResourceInstance(instance);
            
            //Prepare for next loop.
            loopCalendar1.add(GregorianCalendar.MINUTE, minutesBetweenImages1);
        }
        
        /* Setup first resource. */
        
        int resourceNumber2 = 319;
        int minutesBetweenImages2 = 30;
        Resource resource2 = dbms.getResourceManager().getWeatherResourceByNumber(resourceNumber2);
       
        File imageFolder2 = new File("C:\\TestDay-319");
        
        /* Must place data in storage system. */
        
        //Make calendar for below loop.
        GregorianCalendar loopCalendar2 = new GregorianCalendar();
        loopCalendar2.setTimeInMillis(ResourceTimeManager
                .getStartOfDayFromMilliseconds(System.currentTimeMillis(), 
                resource2.getTimeZone().getTimeZone()));
        
        //Place files from image test folder.
        for (File file : imageFolder2.listFiles()) {
            ImageInstance instance = new ImageInstance(file);
            instance.setStartTime(loopCalendar2.getTimeInMillis());
            instance.setResourceNumber(resourceNumber2);
            
            storage.placeResourceInstance(instance);
            
            //Prepare for next loop.
            loopCalendar2.add(GregorianCalendar.MINUTE, minutesBetweenImages2);
        }
        
        //Schedule resources.
        movieMakerScheduler.startMaker(resource1);
        movieMakerScheduler.startMaker(resource2);
    }
}
