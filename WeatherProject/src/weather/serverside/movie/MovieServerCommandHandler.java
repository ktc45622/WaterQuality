
package weather.serverside.movie;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.*;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.serverside.FFMPEG.ImageVideoMaker;
import weather.serverside.FFMPEG.LongVideoMaker;

/**
 * A class that represents command request threads in the MovieMakerServer.
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public class MovieServerCommandHandler implements Runnable {
    
    /**
     * Represents the client.
     */
    private final Socket clientSocket;

    /**
     * The output stream used for writing to the client represented by clientSocket.
     */
    private ObjectOutputStream oos;

    /**
     * The input stream used for reading from the client represented by clientSocket.
     */
    private ObjectInputStream ois;
    
    /**
     * The MovieMakerScheduler which the command will be executed upon.
     */
    private final MovieMakerScheduler scheduler;
    
    /**
     * The DayLongVideoScheduler which the command will be executed upon.
     */
    private final DayLongVideoScheduler dayLongScheduler;
    
    /**
     * The StorageControlSystem to be gotten from the scheduler.
     */
    private final StorageControlSystem storageSystem;
    
    /**
     * Constructor for the MovieServerCommandHandler object.
     * @param clientSocket A socket which represents the client.
     * @param scheduler The MovieMakerScheduler which the command will be 
     * executed upon.
     * @param dayLongScheduler The DayLongVideoScheduler which the command will 
     * be executed upon.
     */
    public MovieServerCommandHandler(Socket clientSocket, 
            MovieMakerScheduler scheduler, 
            DayLongVideoScheduler dayLongScheduler) {
        this.clientSocket = clientSocket;
        this.scheduler = scheduler;
        this.dayLongScheduler = dayLongScheduler;
        this.storageSystem  = scheduler.getStorageControlSystem();
        this.oos = null;
        this.ois = null;
    }

    /**
     * The command request thread.
     */
    @Override
    public void run() {
        if (this.clientSocket == null) {
            WeatherLogger.log(Level.SEVERE, "MovieSeverCommandHandler ran for "
                    + "null clientSocket. Returned from method.");
            return;
        }
        
        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());
            Debug.println("created input stream");
            MovieCommand command = (MovieCommand) ois.readObject();
            Debug.println("Command received: "+ command);
            execute(command);
            ois.close();
            oos.close();
        }
        catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, "Unable to connect to inputstream " +
                    "from MovieMakerServer", ex);
        }
        catch (ClassNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, 
                    "Unable to read command from inputstream. " +
                    "The inputstream is from MovieMakerServer, either the " +
                    "inputstream failed to initialize or MovieMakerServer is" +
                    "not accepting connections.", ex);
        }
        finally {
           try {
               this.clientSocket.close();//Closes I/O stream
           }
           catch (IOException ex) {
               WeatherLogger.log(Level.WARNING,
                       "Unable to close client socket ", ex);
           }//end of try-catch block
       }//end of finally
    }

    /**
     * Executes the desired command on the MovieMakerScheduler specified by 
     * the constructor.
     * @param command The desired command to be executed on the MovieMakerScheduler.
     */
    private void execute(MovieCommand command) {
        MovieCommandType commandType = command.getCommandType();
        
        if (commandType == MovieCommandType.START) {
            scheduler.startMaker(command.getResource());
            Debug.println("command to start making a day long video for "+ command.getResource().getName());
            dayLongScheduler.startMaker(command.getResource());
        } // end START
        else if (commandType == MovieCommandType.STOP) {
            scheduler.stopMaker(command.getResource());
            Debug.println("command to stop making a day long video for "+ command.getResource().getName());
            dayLongScheduler.stopMaker(command.getResource());
        } // end STOP
        else if (commandType == MovieCommandType.MAKE_TWO_MOVIES) {
            ArrayList<AVIInstance> movieInstances = 
                    makeVideoFromImages(command);
            try {
                oos.writeObject(movieInstances);
                oos.flush();
            }
            catch (IOException ex) {
                WeatherLogger.log(Level.SEVERE, 
                        "Error: could not write value to client", ex);
            }
        } // end MAKE_TWO_MOVIES
        else if (commandType == MovieCommandType.MAKE_AND_SAVE_DAY_LONG) {
            ArrayList<AVIInstance> movieInstances = 
                    makeAndSaveDayLongVideo(command);
            try {
                oos.writeObject(movieInstances);
                oos.flush();
            }
            catch (IOException ex) {
                WeatherLogger.log(Level.SEVERE, 
                        "Error: could not write value to client", ex);
            }
        } // end MAKE_DAY_LONG
    }
    
    /**
     * Method to create videos from a provide collection of images. The videos 
     * will be returned as an <code>ArrayList</code> with the zeroth element 
     * being the AVI<code>AVIInstance</code> and the next element being the
     * MP4 <code>AVIInstance</code>.  The <code>ArrayList</code> will have no
     * elements if the videos cannot be made.
     *
     * @param command The command requesting the videos be made.
     * @return An <code>ArrayList</code> with the zeroth element being the AVI
     * <code>AVIInstance</code> and the next element being the MP4
     * <code>AVIInstance</code>.  The <code>ArrayList</code> will have no
     * elements if the videos cannot be made.
     */
    private ArrayList<AVIInstance> makeVideoFromImages(MovieCommand command) {
        //Get data from command.
        Resource resource = command.getResource();
        ArrayList<ResourceInstance> instances = command.getImages();
        Date startTime = command.getStartTime();
        int videoLength = command.getVideoLength();
        String aviCodec = command.getAVICodec();
        
        //Create videos.
        ImageVideoMaker ivm = new ImageVideoMaker(storageSystem, instances, 
                resource, videoLength, startTime, aviCodec);
        ivm.createVideos();
        
        //Retrive videos as files.
        File aviFile;
        try {
             aviFile = ivm.getAVI();
        } catch (WeatherException ex) {
            Debug.println("Error getting AVI file.");
            aviFile = null;
        }
        
        File mp4File;
        try {
             mp4File = ivm.getMP4();
        } catch (WeatherException ex) {
            Debug.println("Error getting MP4 file.");
            mp4File = null;
        }
        
        //Read files into instances.
        AVIInstance aviInstance = null;
        if (aviFile != null) {
            aviInstance = new AVIInstance();
            try {
                aviInstance.readFile(aviFile);
            } catch (WeatherException ex) {
                aviInstance = null;
            }
        }
        
        MP4Instance mp4Instance = null;
        if (mp4File != null) {
            mp4Instance = new MP4Instance();
            try {
                mp4Instance.readFile(mp4File);
            } catch (WeatherException ex) {
                mp4Instance = null;
            }
        }
        
        //Setup result.
        ArrayList<AVIInstance> resultList = new ArrayList<>();
        if (aviInstance != null && mp4Instance != null) {
            resultList.add(aviInstance);
            resultList.add(mp4Instance);
        }
        
        //Cleanup video maker.
        ivm.cleanup();
        
        //Return result.
        return resultList;
    }
    
    /**
     * Method to create day-long video and its low-quality copy from hour-long
     * video that are already in the storage system. Both copies will fill
     * missing hours with "No Data" videos, be saved to the storage system, be
     * in MP4 format, and be returned as a <code>AVIInstance</code>. The two
     * instances of <code>AVIInstance</code> will be in an
     * <code>ArrayList</code> with the zeroth element being the standard-quality
     * movie and the next element being the low-quality copy.  The
     * <code>ArrayList</code> will have no elements if the videos cannot be
     * made. Any existing videos will be overridden.
     *
     * @param command The command requesting the videos be made. 
     * @return An <code>ArrayList</code> of type <code>AVIInstance</code> 
     * holding the day-long MP4 movie at index 0 and its low-quality copy at 
     * index 1.  The<code>ArrayList</code> will have no elements if the videos 
     * cannot be made.
     */
    private ArrayList<AVIInstance> makeAndSaveDayLongVideo(
            MovieCommand command) {
        //Make return object.
        ArrayList<AVIInstance> resultList = new ArrayList<>();
        
        //Get data from command.
        Resource resource = command.getResource();
        Date startTime = command.getStartTime();

        //Find the end of the available video for this day.
        long endMillis = ResourceTimeManager
                .getLastMilliOfExpectedVideo(startTime
                .getTime(), resource.getTimeZone().getTimeZone());
        
        //Return empty list if ending time is 0.
        if (endMillis == 0) {
            return resultList;
        }
        
        //Make ending time into date.
        Date endTime = new Date(endMillis);

        //Create and save day-long videos.
        LongVideoMaker lvm = new LongVideoMaker(storageSystem, resource,
                endTime, command.getMakingLowQuality(), 
                "the movie server command handler", "Handler");

        boolean success = lvm.createDayLongVideo();

        //Return empty list if videos were not made.
        if (!success) {
            return resultList;
        }

        /**
         * The videos have been saved, but must be retrieved from the storage
         * system.
         */
        //Make requst for existing day-long video.
        ResourceRange dayRange = new ResourceRange(startTime, endTime);
        ResourceInstancesRequested dayRequest
                = new ResourceInstancesRequested(dayRange, 1, true,
                        ResourceFileFormatType.mp4, resource);

        //Try to send request and, if successful, retrieve instances.
        ArrayList<ResourceInstancesReturned> dayReturned;
        dayReturned = storageSystem.getDayLongMovies(dayRequest);
        
        //See if a day was returned.
        if (dayReturned.isEmpty()) {
            Debug.println("Unable to retrieve day-long instances from"
                    + " storage.");
            //Couldn't get data; return empty list.
            return new ArrayList<>();
        }
        
        MP4Instance standardInstance = (MP4Instance) dayReturned.get(0)
                .getResourceInstances().get(0);
        resultList.add(standardInstance);
        MP4Instance lowQualityInstance = (MP4Instance) dayReturned.get(1)
                .getResourceInstances().get(0);
        resultList.add(lowQualityInstance);

        return resultList;
    }
}
