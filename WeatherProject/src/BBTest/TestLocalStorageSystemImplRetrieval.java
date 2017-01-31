package BBTest;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.MP4Instance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

public class TestLocalStorageSystemImplRetrieval {

    public static void main(String[] args) {
        //Setup local storage
        DBMSSystemManager dbms = null;
        String fileSystemRoot = "C:";
        
        Debug.setEnabled(true);
        
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        StorageControlSystemLocalImpl localStore = new StorageControlSystemLocalImpl(dbms, fileSystemRoot);
        
         //Setup resource numbers and resources.
        int videoResourceNumber = 102;
        int stationResourceNumber = 299;
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
        Resource stationResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(stationResourceNumber);

        //Set range for below loops (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        //Set calendars to dates.  (Set in local time.)
        startCalendar.set(2016, GregorianCalendar.FEBRUARY, 1, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2016, GregorianCalendar.FEBRUARY, 2, 0, 0, 0);
        endCalendar.set(GregorianCalendar.MILLISECOND, 0);
        //Set resource range.
        ResourceRange range = new ResourceRange(new Date(startCalendar
                .getTimeInMillis()), new Date(endCalendar.getTimeInMillis()));
        
        //Used to make requests.
        ResourceInstancesRequested resourceRequest;
        
        Debug.println();

        //Test mp4 movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.mp4, movieResource);
        Debug.println("\nMP4 hour-long list:");
        ResourceInstancesReturned rir = localStore
                .getResourceInstances(resourceRequest);
        debugRIR(rir);
        
        if (rir.getNumberOfValuesReturned() > 0) {
            ArrayList<File> returnedFiles = new ArrayList<>();
            for (ResourceInstance rInstance : rir.getResourceInstances()) {
                MP4Instance mp4Instance = (MP4Instance) rInstance;
                returnedFiles.add(localStore
                        .getFileForMP4Movie(mp4Instance.getResourceNumber(),
                                new Date(mp4Instance.getStartTime()), false,
                                false));
            }
            Debug.println("\nMP4 Hour-Long File Location List:");
            for (File file : returnedFiles) {
                Debug.println(file.getAbsolutePath());
            }
        }
        
        Debug.println();
        
        //Test avi movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        Debug.println("\nAVI hour-long list:");
        debugRIR(localStore.getResourceInstances(resourceRequest));
        
        Debug.println();
        
        //Test weather station retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1 ,false, 
                 stationResource.getFormat(), stationResource);
        Debug.println("\nWeather Station Instance list:");
        debugRIR(localStore.getResourceInstances(resourceRequest));
        
        Debug.println();
        
        //Test image retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 4, false, 
                movieResource.getFormat(), movieResource);
        Debug.println("\nImage Instance list:");
        debugRIR(localStore.getResourceInstances(resourceRequest));
        
        Debug.println();
        
        //Test day-log mp4 movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        ArrayList<ResourceInstancesReturned> list = localStore
                .getDayLongMovies(resourceRequest);
        
        if (list.isEmpty()) {
            Debug.println("List of day-long rir's is EMPTY!");
            return;
        }
        
        Debug.println("\nMP4 day-long standard-quality list:");
        debugRIR(list.get(0));
        ArrayList<File> returnedFiles = new ArrayList<>();
        if (list.get(0).getNumberOfValuesReturned() > 0) {
            for (ResourceInstance rInstance : list.get(0)
                    .getResourceInstances()) {
                MP4Instance mp4Instance = (MP4Instance) rInstance;
                returnedFiles.add(localStore
                        .getFileForMP4Movie(mp4Instance.getResourceNumber(),
                                new Date(mp4Instance.getStartTime()), true,
                                false));
            }
            Debug.println("\nMP4 Standard-Quality Day-Long File Location List:");
            for (File file : returnedFiles) {
                Debug.println(file.getAbsolutePath());
            }
        }
        
        Debug.println("\nMP4 day-long low-quality list:");
        debugRIR(list.get(1));
        returnedFiles.clear();
        if (list.get(0).getNumberOfValuesReturned() > 0) {
            for (ResourceInstance rInstance : list.get(1)
                    .getResourceInstances()) {
                MP4Instance mp4Instance = (MP4Instance) rInstance;
                returnedFiles.add(localStore
                        .getFileForMP4Movie(mp4Instance.getResourceNumber(),
                                new Date(mp4Instance.getStartTime()), true,
                                true));
            }
            Debug.println("\nMP4 Low-Quality Day-Long File Location List:");
            for (File file : returnedFiles) {
                Debug.println(file.getAbsolutePath());
            }
        }
    }
    
    //Helper function to debug output.
    private static void debugRIR(ResourceInstancesReturned rir) {
        //Check for data.
        if (rir.getNumberOfValuesReturned() == 0) {
            Debug.println("No Elements.");
            return;
        }
        
        for (int j = 0; j < rir.getNumberOfValuesReturned(); j++) {
            ResourceInstance instance = rir.getResourceInstances().get(j);
            if (instance != null) {
                Debug.println("Element " + j + ": Type: " + instance.getClass()
                    .toString() + instance.toString());
            } else {
                Debug.println("Element " + j + " is NULL");
            }
        }
    }
}
