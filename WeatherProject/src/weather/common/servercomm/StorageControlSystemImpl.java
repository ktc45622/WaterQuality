package weather.common.servercomm;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import weather.StorageControlSystem;
import weather.common.data.*;
import weather.common.data.resource.*;
import weather.common.dbms.DBMSSystemManager;
import weather.common.servercomm.storage.StorageCommand;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;

/**
 /**
 * This class implements the StorageControlSystem interface in such a way as to
 * retrieve files stored on a remote machine. All requests to store and provide
 * data are eventually handled by <code>StorageRequestHandlerImpl</code> on the
 * remote computer.
 *
 * @author Bloomsburg University Software Engineering
 * @author Bill Katsak (2008)
 * @author Zach Rothweiler
 * @version Spring 2012
 */
//@SuppressWarnings("unchecked")
public class StorageControlSystemImpl implements StorageControlSystem, Serializable {
    private String hostname;
    private int port;
    private static StorageControlSystemImpl  storageImplementation = null;

    public static StorageControlSystemImpl getStorageSystem(){
        if(storageImplementation== null){
            storageImplementation = new StorageControlSystemImpl();
        }
        return storageImplementation;
    }
    /**
     * Constructs a <code>StorageControlSystem</code> and uses values from the
     * property file to initialize the fields
     */
    private StorageControlSystemImpl() {
        
        //Debug.println("set hostname");
        this.hostname = PropertyManager.getGeneralProperty("storageHost").trim();
        //Debug.println("hostname : "+hostname);
        //Debug.println("set port");
        this.port = Integer.parseInt(PropertyManager.getGeneralProperty("storagePort").trim());
        //Debug.println("port : "+port);       
    }

    /**
     * Gets the DBMS from this object.
     * 
     * @return A <code>DBMSSystemManager</code> that is the DBMS from this
     * object.
     */
    @Override
    public DBMSSystemManager getDBMS() {
        throw new RuntimeException("Not yet implemented");
    }
    
    /**
     * Creates whatever does not exist of the file structure for this instance.
     *
     * @return True if all creation actions were successful; false otherwise.
     */
    @Override
    public boolean createFileStructure() {
        throw new RuntimeException("Not yet implemented");
    }


    /**
     * Used to place a specified weather resource instance into a file system so
     * long as the instance is NOT a day-long video.
     * 
     * @param resourceInstance A weather resource instance to be put into the
     * file system.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean placeResourceInstance(ResourceInstance resourceInstance) {
        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.STORE);
        command.setFirstResourceInstance(resourceInstance);

        Object returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            return false;
        } else {
            return (Boolean)returnedObject;
        }
    }

  
    /**
     * Used to place a specified weather resource instance into a file system
     * when the instance IS a day-long video, which must be in mp4 format.  A 
     * standard-quality version must be provided.  The low-quality version can 
     * be null if one is not available.
     * 
     * @param standardInstance The standard-quality version of a day-long movie
     * instance to be put into the file system.
     * @param lowQualityInstance The low-quality version of a day-long movie
     * instance to be put into the file system.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean placeDayLongMovie(ResourceInstance standardInstance,
            ResourceInstance lowQualityInstance) {
        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.STORE_DAY_LONG_MP4);
        command.setFirstResourceInstance(standardInstance);
        command.setSecondResourceInstance(lowQualityInstance);

        Object returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            return false;
        } else {
            return (Boolean)returnedObject;
        }
    }
    
    
    /**
     * Gets the error log text file. The current error log file is returned
     * as a string.
     * TODO: implement method
     * @return the error log as a String.
     */
    @Override
    public String getErrorLog() {
        throw new RuntimeException("Not yet implemented");
    }

   
    /**
     * Sets the root directory to which all new resource instances will be
     * saved. The server will need to update
     * its property file so that this root directory is used on all
     * system restarts.
     * TODO: implement method
     * @param rootDirectory the folder to set as the root for the file
     *    storage system.
     */
    @Override
    public void setRootDirectory(String rootDirectory) {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Returns the current root folder of the file storage system.
     * TODO: implement method
     * @return the name of the root folder of the file storage
     *    system as a string.
     */
    @Override
    public String getRootDirectory() {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Gets a single series resource instances within a certain time range when
     * day-long movies are NOT being requested.
     * @param request The object providing the resource instances being 
     * requested.
     * @return An instance ResourceInstancesReturned holding the requested data.
     * This will be an empty ResourceInstancesReturned if no data is returned.
     */
    @Override
    public ResourceInstancesReturned 
        getResourceInstances(ResourceInstancesRequested request) {
        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.PROVIDE);
        command.setResourceRequest(request);

        Object returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            return new ResourceInstancesReturned();
        } else {
            return (ResourceInstancesReturned)returnedObject;
        }
    }
    
    /**
     * Used to retrieve two series of resource instances within a certain time
     * range when day-long movies ARE being requested.
     * @param request The object specifying the resource and range for the data
     * being requested.  (All its other fields are ignored.)
     * @return An ArrayList of ResourceInstancesReturned objects holding the
     * requested movies.  Array index 0 is to hold the standard-quality movies
     * and array index 1 is to hold the low-quality movies.  This will be an 
     * empty ArrayList if no data is returned.
     */
    @Override
    public ArrayList<ResourceInstancesReturned>
         getDayLongMovies(ResourceInstancesRequested request) {
        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.PROVIDE_DAY_LONG_MP4);
        command.setResourceRequest(request);

        Object returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            return new ArrayList<>();
        } else {
            return (ArrayList<ResourceInstancesReturned>)returnedObject;
        }
    }

    /**
     * This method attempts to connect to the server stored in the class' data.  If
     * there is no response from the server for 10 seconds, a fatal exception is
     * thrown.
     * 
     * @return true if the server is available
     */
    @Override
    public boolean pingServer() {
        Debug.println("in ping server hostname "+hostname+":"+port);
        
        boolean value = false;
        Socket socket = new Socket();
        try {
            int timeout = Integer.parseInt(PropertyManager.getGeneralProperty("storageDefaultTimeout"));
            InetSocketAddress addr = new InetSocketAddress (hostname, port);
            socket.connect(addr, timeout);
            socket.close();
            value = true;
        } catch (SocketTimeoutException ex){
            value = false;
             Debug.println("In Ping sever -- timeout exceptiom ");
        } catch (IOException ex) {
            value = false;
            Debug.println("In Ping sever -- IO exceptiom ");
            ex.printStackTrace();
        } catch (Exception ex) {
            value = false;
        } 

        Debug.println("Ping successful?: "+value);
        return (value);
    }
    
    /**
     * Creates a new command sent to the storage system to create a new default
     * nighttime movie.
     * 
     * @param resource The <code>Resource</code> for which the movie is created.
     * @param picture The <code>ImageInstance</code> used to create the movie.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setNewDefaultNightimeMovie(Resource resource,
            ImageInstance picture) {
        Debug.println("Setting default night time movie.");
        picture.setResourceNumber(resource.getResourceNumber());
        StorageCommand command = new StorageCommand();
        command.setFirstResourceInstance(picture);
        command.setCommandType(StorageCommandType.STORE_DEFAULT_NIGHT);

        Object returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            return false;
        } else {
            return (Boolean)returnedObject;
        }
    }
    
    /**
     * Creates a command for the storage system to create a new no data movie.
     * 
     * @param picture The <code>ImageInstance</code> used to create the movie.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setNewDefaultGenericNoDataMovie(ImageInstance picture) { 
        Debug.println("Setting default nodata movie.");
        StorageCommand command = new StorageCommand();
        command.setFirstResourceInstance(picture);
        command.setCommandType(StorageCommandType.STORE_NO_DATA_MP4);

        boolean result1;
        Object returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            result1 = false;
        } else {
            result1 = (Boolean)returnedObject;
        }
        
        command = new StorageCommand();
        command.setFirstResourceInstance(picture);
        command.setCommandType(StorageCommandType.STORE_NO_DATA_AVI);

        boolean result2;
        returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            result2 = false;
        } else {
            result2 = (Boolean)returnedObject;
        }
        
        return result1 && result2;
    }
    
    /**
     * Takes a resource, and a picture, and creates a new command sent to the
     * storage system to create a new default daytime (no data)
     * movie from this picture for the specific resource.
     * 
     * @param resource The resource to create a new daytime movie for.
     * @param picture The picture to use to create the movie.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setNewDefaultDaytimeMovie(Resource resource,
            ImageInstance picture) {
        Debug.println("Setting default day time movie.");
        picture.setResourceNumber(resource.getResourceNumber());
        StorageCommand command = new StorageCommand();
        command.setFirstResourceInstance(picture);
        command.setCommandType(StorageCommandType.STORE_DEFAULT_DAY);
        
        Object returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            return false;
        } else {
            return (Boolean)returnedObject;
        }
    }
    
    /**
     * Retrieves a list of currently existing folders from the storage system.
     * @return An <code>ArrayList</code> containing a list of the folder names.
     * This will be an empty ArrayList if no data is returned.
     */
    @Override
    public ArrayList<String> retrieveFolderList(){
        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.PROVIDE_FOLDER_LIST);

        Object returnedObject = sendCommandAndGetResult(command);
        if (returnedObject == null) {
            return new ArrayList<>();
        } else {
            return (ArrayList<String>)returnedObject;
        }
    }
    
    /**
     * Sends a command to the storage server and, if it is a provide command,
     * returns the sought data in an <code>Object</code>. If the command is a
     * store command, the <code>Object</code> is a <code>Boolean</code> that
     * indicates the succuss of the store. As of 1/13/16, execution makes is way
     * to the <code>provide</code> method of
     * <code>StorageRequestHandlerImpl</code>.
     *
     * @param command The command to execute on the server.
     * @return The <code>Object</code> or result received back from the server.
     */
    private Object sendCommandAndGetResult(StorageCommand command){
        if (command == null) {
            Debug.println("command was null");
            return null;
        }
        
      Object returned = null;
      Socket socket = null;
      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;
      int timeout = Integer.parseInt(PropertyManager.getGeneralProperty("storageDefaultTimeout"));
      boolean finished = false;
      int count = 0;
      Debug.println(hostname + ":" + port);
      while (!finished && count < 4){
        try{
            
            socket = new Socket(hostname, port);
            Debug.println("New Socket successful");
  //          System.err.println(hostname + ":" + port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            Debug.println("Got output stream");
            
            oos.writeObject(command);
            Debug.println("Writing command to output stream finished");
            oos.flush();
            Debug.println("output stream flushed");
            socket.setSoTimeout(timeout);
            Debug.println("The socket Time out value is " + socket.getSoTimeout());
            Debug.println("The remote socket value is " + socket.getRemoteSocketAddress());
            ois = new ObjectInputStream(socket.getInputStream());
            Debug.println("Got input stream");
            returned = ois.readObject();
            Debug.println("read from input stream");
            finished = true;
            oos.close();
            ois.close();
        }
        catch (SocketException se){
            // This is caused by a time out, we will try again
            se.printStackTrace(); // just for now
        } 
        catch (ClassNotFoundException ex) {
            //We cannot procced after this coding error
            WeatherException we = new WeatherException(4020, true, ex);
            System.err.println("class not found");
            we.show();
        }
        catch (UnknownHostException ex) {
            //We cannot procced after this error
            WeatherException we = new WeatherException(4023, true, ex);
            System.err.println("unknown host");
            we.show();
            
        }
        catch (IOException ex) {
            WeatherException we = new WeatherException(4023, true, ex);
            System.err.println("io exception trying to send command to storage system");
            ex.printStackTrace();
            we.show();
            
        }
        finally{
            closeSocket(socket);
        }
        count++;
      } //end of whil loop
      if(!finished){
          //Cound not connect in three attempts -- show an error message to the user and terminate
          WeatherException we = new WeatherException(4023, true);
          we.show();
      }
      return returned;
    }
    
    /**
     * Returns the path to an MP4 movie as a <code>File</code> object given a
     * resource number and a time. For hour-long movies, a path to a default
     * movie is returned if the movie does not exist. For full-day movies, the
     * original path is always returned and the caller must specify the quality
     * (standard or low) of the requested movie.
     * @param resourceID The resource number to match the <code>Resource</code>
     * of the requested movie.
     * @param time The start time of the movie as a <code>Date</code> object.
     * @param isFullDay True if a day-long movie is being requested; False
     * otherwise.
     * @param isLowQuality True if a low-quality movie is being requested; False
     * otherwise. This parameter is ignored if an hour-long movie is being
     * requested.
     * @return The path in a <code>File</code> object. For hour-long movies, a
     * path to a default movie is returned if the movie does not exist. For
     * full-day movies, the original path is always returned.
     */
    @Override
    public File getFileForMP4Movie(int resourceID, Date time, boolean isFullDay,
            boolean isLowQuality) {
        throw new RuntimeException("Not yet implemented");
    }
    
    /**
     * Closes the given socket.
     * @param s The socket to close.
     */
    private void closeSocket(Socket s){
        try{
            if (s != null) {
                s.close();
                Debug.println("socket closed");
            }
        }
        catch(IOException ex){
            
        }
    }

    /**
     * Gets the default MP4 hour-long movie out of the storage system for a 
     * given resource at a given time.  The method will bypass the actual 
     * recorded data if it exists.
     *
     * If it's nighttime, we set the path to a default nighttime movie for the
     * resource Otherwise, we set the path to a default daytime movie for the
     * resource.
     *
     * Next, create a new file from that path. If that file exists (which means
     * we have a default picture to return), return it. Otherwise, return the
     * storage system's default NoData movie instead.
     *
     * @param resource The resource whose movie we are searching for.
     * @param calendar A calender holding the time of the movie we're looking
     * for.
     *
     * @return If it's nighttime, a default nighttime movie. If it's daytime, 
     * a default daytime movie. The next option is the storage system's default
     * NoData movie. The final option is null.
     */
    @Override
    public File getDefaultMP4MovieForTime(Resource resource, 
            GregorianCalendar calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Method to determine if there are hour-long MP4 videos are present for the
     * given <code>Resource</code> on the given day to make a day-long video.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if there are enough hour-long MP4 videos present for the
     * given <code>Resource</code> on the given day to make a day-long video;
     * False otherwise.
     */
    @Override
    public boolean hasHourLongMP4Videos(int resourceID, Date time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Method to determine if enough hour-long MP4 videos can be made form AVI
     * videos that are present for the given <code>Resource</code> on the given 
     * day to make a day-long video.  This function will make an MP4 copy of 
     * each AVI video that is present for the given day.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if enough hour-long MP4 videos can be made form AVI videos
     * that are present for the given <code>Resource</code> on the given day to
     * make a day-long video; False otherwise.
     */
    @Override
    public boolean canMakeEnoughMP4VideosFromAVIVideos(int resourceID, 
            Date time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

     /**
     * Method to convert all the hour-long MOV videos that are present for a
     * given <code>Resource</code> on the given day to MP4 format.
     * 
     * NOTE: This method will delete, rather than convert, any hour-long MOV
     * videos of the wrong length.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if all copying and file clean-up was successful; False 
     * otherwise.
     */
    @Override
    public boolean convertMOVVideosToMP4Videos(int resourceID, Date time) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
