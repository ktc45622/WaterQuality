package weather.common.data.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import weather.clientside.utilities.StorageSpaceTester;
import weather.common.utilities.Debug;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The AVIInstance class specifies a weather movie. The weather movie is created
 * using FFmpeg and is of the type avi.
 *
 * @author Rod Cano
 * @author Eric Subach
 * @version 2011
 */
public class AVIInstance extends ResourceInstance {

    /**
     * Byte Array version of a Movie.
     */
    byte[] movie;
    /**
     * The file to which the movie byte array that has been written to.
     */
    File file = null;
    
    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version of this
     * class is not compatible with old versions. See Sun docs for <a
     * href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but included here
     * as a reminder of its importance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of AVIInstance. ResourceType is set to
     * WeatherResourceType.AVIVideo and and the storage filename is "None".
     */
    public AVIInstance() {
        super();
        super.setResourceType(WeatherResourceType.AVIVideo);
        this.setStorageFileName("None");
    }

    /**
     * Creates a new instance of AVIInstance with is FIELDS set according to the
     * name and path of the given file  This constructor does NOT load the date
     * from the file into the instance.  For that, use <code>readFile</code>.
     *
     * @param file The file used to set the fields of the instance.
     */
    public AVIInstance(File file) {
        super();
        super.setResourceType(WeatherResourceType.AVIVideo);
        this.file = file;
        this.setStorageFileName(file.getName());
    }

    /**
     * Creates a new instance of AVIInstance from the provided Resource.
     *
     * @param resource The resource used to build the AVIInstance.
     */
    public AVIInstance(Resource resource) {
        super(resource);
        super.setResourceType(WeatherResourceType.AVIVideo);
    }

    /**
     * Creates a new instance of AVIInstance.
     *
     * @param time The date and time the resource was created.
     * @param resourceType The type of Resource.
     * @param resourceNumber The number of this Resource.
     * @param fileName The filename of this AVIInstance.
     */
    public AVIInstance(Date time, WeatherResourceType resourceType,
            int resourceNumber, String fileName) {
        super(time, resourceType, resourceNumber, fileName);
    }

    /**
     * Writes the movie as a byte array to a provided File.
     *
     * @param file The file to write the movie byte array to.
     * @throws WeatherException
     */
    @Override
    public void writeFile(File file) throws WeatherException {
        try {
            BufferedOutputStream out =
                    new BufferedOutputStream(new FileOutputStream(file));
            try {
                out.write(movie);
            } catch (IOException ex) {
                throw new WeatherException(4000, ex);
            } finally {
                out.close();
            }
        } catch (FileNotFoundException ex1) {
            throw new WeatherException(4000, ex1);
        } catch (IOException ex2) {
            throw new WeatherException(4000, ex2);
        }

        // Remember where we kept the file.
        this.file = file;


    }

    /**
     * Returns the File that the movie byte array was written to.
     *
     * @return The File that the movie byte array was written to.
     */
    public File getMovieFile() {
        return file;
    }

    /**
     * Returns a String representation of the absolute pathname of the file
     * containing the movie's byte array
     *
     * @return A String representation of the absolute pathname of the file
     * containing the movie's byte array.
     */
    public String getMovie() {
        return file.getAbsolutePath();
    }

    /**
     * Write the file to the disk.
     *
     * NOTE: This method may only be called from the client side after being
     * downloaded from the server.
     */
    public void writeToDisk() {
        try {
            // Debug.println("Writing a " + this.getResourceType()+" in AVIInstance.java writeToDisk method.");
            if (this.getResourceType() == WeatherResourceType.WeatherCamera) {
                file = new File(CommonLocalFileManager
                        .getAVICameraDir() + File.separator 
                        + getSerializedFileName() + ".avi");
            }
            else if(this.getResourceType() == WeatherResourceType.WeatherMapLoop){
                file = new File(CommonLocalFileManager
                        .getAVIMaploopDir() + File.separator 
                        + getSerializedFileName() + ".avi");
            }
            else{
               // Debug.println("A non-video resource type was trying to be written. " + this.getStorageFileName());
                file = new File(CommonLocalFileManager.getAVIDir()
                        +File.separator + getSerializedFileName() + ".avi");
            }
            if(file.exists()) {
                return;
            }
            
            //Test for space to write file.
            StorageSpaceTester.testApplicationHome();
            
            writeFile(file);
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Error while writing .avi file to disk.", ex);
        }
//        catch (IOException ex) {
//            WeatherLogger.log(Level.SEVERE, "Error while writing .avi file to disk.", ex);
//        }
    }

   
    /**
     * Returns the serialized file name for writing this movie to the hard disk.
     * This contains only the file name and not the entire path.
     * @return The file name.
     */
    public String getSerializedFileName() {
        StringBuilder filename = new StringBuilder();
        String delimiter = ",";

        filename.append(getStorageFileName());
        filename.append(delimiter);
        filename.append(getResourceNumber());
        filename.append(delimiter);
        filename.append(getTime().getTime());
        filename.append(delimiter);
        filename.append(getResourceRange().getStartTime().getTime());
        filename.append(delimiter);
        filename.append(getResourceRange().getStopTime().getTime());
        //Debug.println("You need to see this : "+filename.toString());
        return filename.toString();
    }

    /**
     * Set the fields from the current filename of this instance.
     */
    public void setFieldsFromFilename() {
        StringTokenizer tokenizer = new StringTokenizer(file.getName());

        setStorageFileName(tokenizer.nextToken(","));
        setResourceNumber(Integer.parseInt(tokenizer.nextToken(",")));
        setTime(new Date((Long.parseLong(tokenizer.nextToken(",")))));
        setStartTime(Long.parseLong(tokenizer.nextToken(",")));
        setEndTime(Long.parseLong(tokenizer.nextToken(",.")));
        if(file.getParent().equals(CommonLocalFileManager.getAVICameraDir())){
            setResourceType(WeatherResourceType.WeatherCamera);
        }
        else if(file.getParent().equals(CommonLocalFileManager
                .getAVIMaploopDir())){
            setResourceType(WeatherResourceType.WeatherMapLoop);
        }
        else {
            setResourceType(WeatherResourceType.AVIVideo);
        }
    }
    
    /**
     * Reads a file into the Byte Array.
     * @param file The file to be read in.
     * @throws WeatherException 
     */
    @Override
    public void readFile(File file) throws WeatherException {
        try {
            Debug.println("Movie Instance Readfile -- reading the file "+ file.getAbsoluteFile());
            FileInputStream in = new FileInputStream(file);
            FileChannel fc = in.getChannel();
            movie = new byte[(int) fc.size()];
            ByteBuffer buffer = ByteBuffer.wrap(movie);
            fc.read(buffer);
            in.close();
        }
        catch (IOException ex) {
            throw new WeatherException(4003, ex);
        }
        this.file = file;
    }

    /**
     * Reads data from a URL.
     * @param url The URL.
     * TODO Maybe implement read from URL?
     * @throws ConnectException
     * @throws IOException
     * @throws SocketTimeoutException
     * @throws WeatherException 
     */
    @Override
    public void readURL(URL url) throws ConnectException, IOException, SocketTimeoutException, WeatherException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * This is a small testing program added on 7/15/16 after a change to the 
     * superclass of this class.
     * @param args
     */
    public static void main(String[] args) {
        try {
            AVIInstance instance = new AVIInstance();
            instance.readFile(new File("NoData.avi"));
            Debug.println("Read File: " + instance.getMovie());
            if (instance.movie == null) {
                Debug.println("MOVIE IS NULL.");
            } else {
                Debug.println("Movie is NOT null.");
            }
            instance.writeFile(new File("Testing Copy.avi"));
            Debug.println("Write File: " + instance.getMovie());
        } catch (WeatherException ex) {
            Debug.println("Exception Thrown: " + ex.getMessage());
        }
    }
}
