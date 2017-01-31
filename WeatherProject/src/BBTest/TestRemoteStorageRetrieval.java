package BBTest;

import java.io.File;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.resource.AVIInstance;
import weather.common.data.resource.MP4Instance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.storage.StorageCommand;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.Debug;
//import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.serverside.storage.StorageManagement;
import weather.serverside.storage.StorageRequestHandlerImpl;

public class TestRemoteStorageRetrieval {

    public static void main(String[] args) {
        //Setup remote storage
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
        StorageManagement storageManagement = new StorageManagement(dbms, fileSystemRoot);
        StorageRequestHandlerImpl handler = new StorageRequestHandlerImpl(storageManagement);

        //Set range for below loops (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        startCalendar.set(2015, GregorianCalendar.NOVEMBER, 1, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2015, GregorianCalendar.NOVEMBER, 4, 0, 0, 0);
        endCalendar.set(GregorianCalendar.MILLISECOND, 0);
        //Set resource range.
        ResourceRange range = new ResourceRange(new Date(startCalendar
                .getTimeInMillis()), new Date(endCalendar.getTimeInMillis()));
        
        //Setup resource numbers and resources.
        int videoResourceNumber = 102;
        int stationResourceNumber = 299;
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
        Resource stationResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(stationResourceNumber);
        
        //Storage command for provide testing.
        StorageCommand provideCommand = new StorageCommand();
        
        //More needed variables
        ResourceInstancesRequested resourceRequest = null;
        Vector<File> files;
        
        Debug.println();

        //Test mp4 movie retrieval.
        Debug.println("Testing retrieveFileVector with mp4 movies:");
        
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.mp4, movieResource);
        files = storageManagement.retrieveFileVector(resourceRequest, false);

        //Test cast of type of returned movies
        Iterator<File> it = files.iterator();
        // Get the resource of this request.
        Resource resource = storageManagement.getResource(resourceRequest.getResourceID());

        if (resource == null) {
            Debug.println("No Resource!");
        }

        //Get the resource instances from the files.
        int counter = 0;
        while (it.hasNext()) {
            Debug.println(it.next().getAbsolutePath());
            //Make sure mp4 movies are returned.
            AVIInstance movieInstance;
            // Instantiate correct movie type.
            if (resourceRequest.getFileType() == ResourceFileFormatType.avi) {
                movieInstance = new AVIInstance(resource);
            } else { // if .mp4
                movieInstance = new MP4Instance(resource);
            }
            counter++;
            Debug.println("Returned Class(item #" + counter + "): " + movieInstance.getClass().getName());
        }

        //Need to see if the list of files is correct
        Debug.println("\nFile List:");
        Debug.println("Array Size: " + files.size());
        for (int i = 0; i < files.size(); i++) {
            Debug.println("File " + (i + 1) + ": " + files.get(i).getAbsolutePath());
        }
        
        Debug.println();
        
        //Test provide method of StorageRequestHandlerImpl for movies.
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            handler.provide(provideCommand);
        } catch (WeatherException ex) {
            Logger.getLogger(TestRemoteStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Debug.println();
        
        //Test avi movie retrieval.
        Debug.println("Testing retrieveFileVector with avi movies:");
        
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        files = storageManagement.retrieveFileVector(resourceRequest, false);

        //Test cast of type of returned movies
        it = files.iterator();
        // Get the resource of this request.
        resource = storageManagement.getResource(resourceRequest.getResourceID());

        if (resource == null) {
            Debug.println("No Resource!");
        }

        //Get the resource instances from the files.
        counter = 0;
        while (it.hasNext()) {
            Debug.println(it.next().getAbsolutePath());
            //Make sure avi movies are returned.
            AVIInstance movieInstance;
            // Instantiate correct movie type.
            if (resourceRequest.getFileType() == ResourceFileFormatType.avi) {
                movieInstance = new AVIInstance(resource);
            } else { // if .mp4
                movieInstance = new MP4Instance(resource);
            }
            counter++;
            Debug.println("Returned Class(item #" + counter + "): " + movieInstance.getClass().getName());
        }

        //Need to see if the list of files is correct
        Debug.println("\nFile List:");
        Debug.println("Array Size: " + files.size());
        for (int i = 0; i < files.size(); i++) {
            Debug.println("File " + (i + 1) + ": " + files.get(i).getAbsolutePath());
        }
        
        Debug.println();
        
        //Test provide method of StorageRequestHandlerImpl for movies.
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            handler.provide(provideCommand);
        } catch (WeatherException ex) {
            Logger.getLogger(TestRemoteStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Debug.println();
        
        //Test retrieval of weather station data.
         resourceRequest =  new ResourceInstancesRequested(range, 1 ,false, 
                 stationResource.getFormat(), stationResource);
        files = storageManagement.retrieveFileVector(resourceRequest, false);
        
        //Need to see if the list of files is correct
        Debug.println("Weather Station Testing:");
        Debug.println("Array Size: " + files.size());
        for(int i = 0; i < files.size(); i++){
            Debug.println("File " + (i + 1) + ": " + files.get(i).getAbsolutePath());
        }
        
        Debug.println();
        
        //Test provide method of StorageRequestHandlerImpl for weather stations
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            handler.provide(provideCommand);
        } catch (WeatherException ex) {
            Logger.getLogger(TestRemoteStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Debug.println();
        
        //Test image retrieval.
        Debug.println("Testing retrieveFileVector with images:");
       
        resourceRequest = new ResourceInstancesRequested(range, 4, false, 
                movieResource.getFormat(), movieResource);
        files = storageManagement.retrieveFileVector(resourceRequest, false); 
        
        //Need to see if the list of files is correct
        Debug.println("Array Size: " + files.size());
        for(int i = 0; i < files.size(); i++){
            Debug.println("File " + (i + 1) + ": " + files.get(i).getAbsolutePath());
        }
        
        Debug.println();
        
        //Test provide method of StorageRequestHandlerImpl for images.
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            handler.provide(provideCommand);
        } catch (WeatherException ex) {
            Logger.getLogger(TestRemoteStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Debug.println();
        
        //Test day-log mp4 movie retrieval.
        
//        //Change range to test if nulls are handled correctly.
//        range.setStopTime(new Date(range.getStopTime().getTime()
//            + ResourceTimeManager.MILLISECONDS_PER_DAY));
        
        Debug.println("Testing retrieveFileVector with day-long mp4 movies:");
        
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.mp4, movieResource);
        files = storageManagement.retrieveFileVector(resourceRequest, true);

        //Test cast of type of returned movies
        it = files.iterator();
        // Get the resource of this request.
        resource = storageManagement.getResource(resourceRequest.getResourceID());

        if (resource == null) {
            Debug.println("No Resource!");
        }

        //Get the resource instances from the files.
        counter = 0;
        while (it.hasNext()) {
            File file = it.next();
            if (file == null) {
                Debug.println("Null Value Returned");
            } else {
                Debug.println(file.getAbsolutePath());
                //Make sure mp4 movies are returned.
                AVIInstance movieInstance;
                // Instantiate correct movie type.
                if (resourceRequest.getFileType() == ResourceFileFormatType.avi) {
                    movieInstance = new AVIInstance(resource);
                } else { // if .mp4
                    movieInstance = new MP4Instance(resource);
                }
                counter++;
                Debug.println("Returned Class(item #" + counter + "): " + movieInstance.getClass().getName());
            }
        }
        
        //Need to see if the list of files is correct
        Debug.println("\nFile List:");
        Debug.println("Array Size: " + files.size());
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i) != null) {
                Debug.println("File " + (i + 1) + ": " + files.get(i)
                            .getAbsolutePath());
            } else {
                Debug.println("File " + (i + 1) + ": NULL");
            }
        }
        
        Debug.println();
        
        //Test provide method of StorageRequestHandlerImpl for day-long movies.
        provideCommand.setCommandType(StorageCommandType.PROVIDE_DAY_LONG_MP4);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            handler.provide(provideCommand);
        } catch (WeatherException ex) {
            Logger.getLogger(TestRemoteStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
