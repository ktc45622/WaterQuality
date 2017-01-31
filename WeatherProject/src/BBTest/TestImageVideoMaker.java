package BBTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;
import weather.serverside.FFMPEG.ImageVideoMaker;

public class TestImageVideoMaker {

    public static void main(String[] args) {
        Debug.setEnabled(true);
        
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
        
        StorageControlSystemLocalImpl localStore = new StorageControlSystemLocalImpl(dbms, fileSystemRoot);
        
        //Get saved properties.
        int videoLength = Integer.parseInt(PropertyManager.getGeneralProperty("MOVIE_LENGTH"));
        String codec = PropertyManager.getServerProperty("FFMPEG_VCODEC");

        //Set video start time.  Start with calendar object.
        GregorianCalendar startCalendar = new GregorianCalendar();
        //Set calendars to date.  (Set in local time.)
        startCalendar.set(2016, GregorianCalendar.JULY, 5, 13, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        //Make Date object.
        Date date = new Date(startCalendar.getTimeInMillis());
        
        //Setup resource and image instances.
        int resourceNumber = 307;
        Resource resource = dbms.getResourceManager()
                .getWeatherResourceByNumber(resourceNumber);
        ArrayList<ResourceInstance> instances = new ArrayList<>();
        File dataFolder = new File("C:\\2016 Test Image");
        for (File file : dataFolder.listFiles()) {
            try {
                ImageInstance thisInstance = new ImageInstance(file);
                instances.add(thisInstance);
            } catch (WeatherException ex) {
                Debug.println("Non-Image file found: " + file.getAbsolutePath());
            }
        }
        
        //Create ImageVideoMaker.
        ImageVideoMaker ivm = new ImageVideoMaker(localStore, instances, resource,
            videoLength, date, codec);
        
        //Create videos.
        ivm.createVideos();
        
        //Retrive videos.
        File aviFile, mp4File;
        try {
             aviFile = ivm.getAVI();
             mp4File = ivm.getMP4();
        } catch (WeatherException ex) {
            Debug.println("Error getting files.");
            
            //Clear temporary files.
            ivm.cleanup();
            return;
        }
        
        //Store Files.
        File targetFolder = new File("C:\\IVM Result 16");
        targetFolder.mkdirs();
        File aviTarget = new File(targetFolder.getAbsolutePath() + File.separator
            + "Result.avi");
        File mp4Target = new File(targetFolder.getAbsolutePath() + File.separator
            + "Result.mp4");
        try {
            Files.copy(aviFile.toPath(), aviTarget.toPath());
            Files.copy(mp4File.toPath(), mp4Target.toPath());
        } catch (IOException ex) {
            Debug.println("Error storing files.");
        }
        
        //Clear temporary files.
        ivm.cleanup();
    }
}
