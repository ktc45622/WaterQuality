package weather.serverside.old_weather_underground_retrieval;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.GregorianCalendar;
import weather.StorageControlSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.data.weatherstation.WeatherUndergroundInstance;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.PageChecker;
import weather.common.utilities.WeatherException;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

/**
 * A class with a static helper method to store the data for one weather 
 * underground weather station on a past day to the local file system.
 * @author Brian Bankes
 */
public class OldWeatherUndergroundFileRetriever {
    
    /**
     * A static helper method to store the data for one weather underground 
     * weather station on a past day to the local file system.
     *
     * @param scs A <code>StorageControlSystem</code> with access to the file
     * system where all file storage occurs.  As of 7/12/16, this must be an 
     * instance of <code>StorageControlSystemLocalImpl</code>.
     * @param resource The given <code>Resource</code>.
     * @param date The day for which data is to be obtained stored in s
     * <code>GregorianCalendar</code> object. It should be set to midnight in 
     * the time zone of the given <code>Resource</code>.
     * @param overrideExistingData True if existing data should be 
     * overridden; False otherwise.
     * NOTE 1: overriding will always occur if there is a saved file with no 
     * data.
     * NOTE 2: overriding will never occur if less data is retrieved than is
     * already stored.
     * @return Zero if a file is properly made or retained, one if there was an
     * amount of data on the Internet less than which is stored (only an option
     * when overriding is used), two if no data was found on the Internet, or 
     * three if an error has prevented a save.
     */
    public static int getOldData(StorageControlSystem scs, 
            Resource resource, GregorianCalendar date, 
            boolean overrideExistingData) {
        /**
         * See how may line of existing data there are. A value of -1 for the
         * following variable means there is no saved file, which is assumed
         * until one is found.
         */ 
        int rowsOfSavedData = -1;
        
        //Setup range to requst file.
        GregorianCalendar rangeEnd = (GregorianCalendar)date.clone();
        rangeEnd.add(GregorianCalendar.DATE, 1);
        ResourceRange requestRange = new ResourceRange(new Date(date
                .getTimeInMillis()), new Date(rangeEnd.getTimeInMillis()));
        
        //Make request object.
        ResourceInstancesRequested resourceRequest = 
                new ResourceInstancesRequested(requestRange, 1 , false, 
                resource.getFormat(), resource);
        
        //Get result.
        ResourceInstancesReturned rir = scs.
                getResourceInstances(resourceRequest);
        if (rir.getNumberOfValuesReturned() > 0) {
            WeatherUndergroundInstance storedWUI = (WeatherUndergroundInstance)
                rir.getResourceInstances().get(0);
            rowsOfSavedData = storedWUI.getRowsOfData();
        }
        
        
        //Stop if old date should be kept because the user selected that option
        //and there is at least one line of existing data.
        if (!overrideExistingData && rowsOfSavedData > 0) {
            Debug.print("OldWeatherUndergroundFileRetriever is ");
            Debug.println("keeping old data by user request.");
            return 0;
        }
        
        /**
         * Get data from Internet and test it for validity.
         */
        
        //Get base url of resource as a string.
        String baseURL = resource.getURL().toString();
        
        //Get fields of calendar.
        int year = date.get(GregorianCalendar.YEAR);
        int month = date.get(GregorianCalendar.MONTH) + 1;
        int day = date.get(GregorianCalendar.DATE);
        
        //Make URL for this day and check that it is valid.
        URL dateURL; 
        try {
            dateURL = new URL(baseURL + "&year=" + year + "&month=" + month
                    + "&day=" + day);
        } catch (MalformedURLException ex) {
            Debug.print("OldWeatherUndergroundFileRetriever error: ");
            Debug.println("Malformed URL found.");
            return 3;
        }
        if (!PageChecker.doesPageExist(dateURL.toString())) {
            Debug.print("OldWeatherUndergroundFileRetriever error: ");
            Debug.println("Nonexistant URL found.");
            return 2;
        }
        
        //Make weather underground instance.
        WeatherUndergroundInstance webWUI = new WeatherUndergroundInstance();
        webWUI.setResourceNumber(resource.getResourceNumber());
        webWUI.setStartTime(date.getTimeInMillis());
        try {
            webWUI.readURL(dateURL);
        } catch (WeatherException ex) {
            Debug.print("OldWeatherUndergroundFileRetriever error: ");
            Debug.println("Data read error.");
            return 3;
        }
        
        //Check web instance for no data.
        int rowsOfWebData = webWUI.getRowsOfData();
        if (rowsOfWebData == 0) {
            Debug.print("OldWeatherUndergroundFileRetriever cannot proceed ");
            Debug.println("because there is no data on the web.");
            return 2;
        }
        
        //Check web instance for insufficient data.
        if (rowsOfSavedData > rowsOfWebData) {
            Debug.print("OldWeatherUndergroundFileRetriever is ");
            Debug.print("keeping old data because there is more data on the ");
            Debug.println("system than the web.");
            return 1;
        }
        
        //Attempt save.
        if (!scs.placeResourceInstance(webWUI)) {
            Debug.print("OldWeatherUndergroundFileRetriever error: ");
            Debug.println("Unable to save data.");
            return 3;
        }
        
        //All steps were successful.
        return 0;
    }
    
    //For testing.
    public static void main(String[] args) {
        Debug.setEnabled(true);
        
        //Setup resource numbers.
        int stationResourceNumber = 299;

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
        
        //Get resource.
        Resource resource = dbms.getResourceManager().getWeatherResourceByNumber(stationResourceNumber);
    
        //Get calendar object.
        GregorianCalendar dateCalendar = new GregorianCalendar();
        dateCalendar.set(2016, GregorianCalendar.FEBRUARY, 2, 0, 0, 0);
        dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
        
        //Test method.
        switch (OldWeatherUndergroundFileRetriever.getOldData(localServer, 
                resource, dateCalendar, true)) {
            case 0:
                Debug.println("Data saved or retained.");
                break;
            case 1:
                Debug.println("Data NOT saved. - LESS WEB DATA THAN STORED");
                break;
            case 2:
                Debug.println("Data NOT saved. - NO WEB DATA FOUND");
                break;
            case 3:
                Debug.println("Data NOT saved. - PROGRAM ERROR");
        }
    }
}
