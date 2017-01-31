package BBTest;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.MovieMakerSystem;
import weather.common.data.resource.AVIInstance;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.serverside.applicationimpl.MovieMakerSystemImpl;

/**
 * This file is to test the movie maker system.
 */
public class TestMovieMakerSystem {

    public static void main(String[] args) {
        Debug.setEnabled(true);
        
        //Setup local storage
        DBMSSystemManager dbms = null;
        
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        MovieMakerSystem movieMakerSystem = new MovieMakerSystemImpl();

        int hourVideoLength = Integer.parseInt(PropertyManager
            .getGeneralProperty("MOVIE_LENGTH"));
        String videoCodec = PropertyManager.getServerProperty("FFMPEG_VCODEC");
        
        //Set date for below.  Start with calendar object.
        GregorianCalendar startCalendar = new GregorianCalendar();
        startCalendar.set(2014, GregorianCalendar.JANUARY, 16, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        Date startDate = new Date(startCalendar.getTimeInMillis());
        
        //Get Resource.
        int videoResourceNumber = 104;
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
        
        //Perform day-long video save and retrieval.
        ArrayList<AVIInstance> movieList = movieMakerSystem.
                makeAndSaveDayLongMP4(movieResource, startDate, true);
        
        if (movieList.isEmpty()) {
            Debug.println("No Videos Returned.");
            return;
        }
        
        //Save result.
        File standardQualityFile 
                = new File("C:\\Testing\\StandardMovieMakerResult2.mp4");
        try {
            movieList.get(0).writeFile(standardQualityFile);
        } catch (WeatherException ex) {
            Logger.getLogger(TestMovieMakerSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        File lowQualityFile 
                = new File("C:\\Testing\\LowQualityMovieMakerResult2.mp4");
        try {
            movieList.get(1).writeFile(lowQualityFile);
        } catch (WeatherException ex) {
            Logger.getLogger(TestMovieMakerSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Test hour-long movie-making.
        ImageInstance image = new ImageInstance();
        try {
            image.readFile(new File("C:\\Default Images\\Default Night.jpeg"));
        } catch (WeatherException ex) {
            Logger.getLogger(TestMovieMakerSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<ResourceInstance> images = new ArrayList<>();
        images.add(image);
        movieList = movieMakerSystem.makeMovies(movieResource, images, 
                startDate, hourVideoLength, videoCodec);
        
        if (movieList.isEmpty()) {
            Debug.println("No Videos Returned.");
            return;
        }
        
        //Save result.
        File aviFile = new File("C:\\Testing\\AVIResult2.avi");
        try {
            movieList.get(0).writeFile(aviFile);
        } catch (WeatherException ex) {
            Logger.getLogger(TestMovieMakerSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        File mp4File  = new File("C:\\Testing\\MP4Result2.mp4");
        try {
            movieList.get(1).writeFile(mp4File);
        } catch (WeatherException ex) {
            Logger.getLogger(TestMovieMakerSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
