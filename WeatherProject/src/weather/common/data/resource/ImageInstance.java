package weather.common.data.resource;

import java.awt.Image;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import weather.common.utilities.ImageFileTester;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class stores an instance of an image in a machine 
 * independent format. The image can be transfered and
 * stored on different machines.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Jacob Kelly (2007)
 * @author Dave Moser (2008)
 * @author Bill Katsak (2008)
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public class ImageInstance extends ResourceInstance implements Serializable {

    private static final long serialVersionUID = 1;
    byte[] image;

    /**
     * Default constructor that creates a new instance of an image by creating a
     * generic instance of ResourceInstance.
     */
    public ImageInstance() {
        super();
        super.setResourceType(WeatherResourceType.WeatherImage);
        super.setTime(new Date(System.currentTimeMillis()));
    }

    /**
     * A constructor for ImageInstance.
     * @param resource The resource used to build the ImageInstance.
     */
    public ImageInstance(Resource resource) {
        super(resource);
        super.setResourceType(WeatherResourceType.WeatherImage);
        super.setTime(new Date(System.currentTimeMillis()));
    }

    /**
     * Creates an instance of the ImageInstance class with the given Resource
     * and the given file.
     *
     * @param resource The resource which captured the image.
     * @param file The image file to create an ImageInstance from.
     * @throws weather.common.utilities.WeatherException if the file is not of a
     * valid image type.
     */
    public ImageInstance(Resource resource, File file) throws WeatherException {
        super(resource);
        readFile(file);
        super.setResourceType(WeatherResourceType.WeatherImage);
        super.setTime(new Date(System.currentTimeMillis()));
    }

    /**
     * Creates an instance of the ImageInstance class no associated Resource
     * and the given file.
     *
     * @param file The image file to create an ImageInstance from.
     * @throws weather.common.utilities.WeatherException if the file is not of a
     * valid image type.
     */
    public ImageInstance(File file) throws WeatherException {
        super();
        readFile(file);
    }

    /**
     * Creates an ImageInstance using the given image.
     *
     * @param image The binary representation of an image.
     */
    public ImageInstance(byte[] image) {
        this.image = image;
    }

    /**
     * Writes the image contained in this object to the specified file.
     * @param file Specifies the file to be written.
     * @throws WeatherException
     */
    @Override
    public void writeFile(File file) throws WeatherException {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(image);
            out.close();
        } catch (FileNotFoundException ex1) {
            throw new WeatherException(4001, ex1);
        } catch (IOException ex2) {
            throw new WeatherException(4001, ex2);
        }
    }

    /**
     * Returns the image stored by this object.
     * @return The image stored in this object.
     * @throws WeatherException
     */
    public Image getImage() throws WeatherException {
        Image imageToReturn = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(image);
            imageToReturn = ImageIO.read(bis);
        } catch (IOException ex) {
            throw new WeatherException(4002, ex);
        }

        return imageToReturn;
    }

    /**
     * Returns the image attribute of this <code>ImageInstance</code>.
     *
     * @return The byte array of this <code>ImageInstance</code>.
     */
    public byte[] getImageBytes() {
        return image;
    }

    /**
     * Returns the length of this image attribute.
     *
     * @return The length of the byte array.
     */
    public int length() {
        return image.length;
    }

    /**
     * This method will read the image contained in a local file. 
     * @param file Specifies the location of the file containing the image.
     * @throws WeatherException
     */
    @Override
    public void readFile(File file) throws WeatherException {
        try {
            if (!ImageFileTester.isImageFile(file)) {
                throw new IOException();
            }
            FileInputStream in = new FileInputStream(file);
            FileChannel fc = in.getChannel();

            image = new byte[(int) fc.size()];

            ByteBuffer bb = ByteBuffer.wrap(image);
            fc.read(bb);
            in.close();
        } catch (IOException ex) {
            throw new WeatherException(4003, ex);
        }

    }

    /**
     * Returns the SerialVersionUID needed by all serializable objects.
     * @return The ID of this serializable object.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Downloads the image located at the specified URL.
     * @param url The URL containing the image.
     * @throws WeatherException
     */
    @Override
    public void readURL(URL url) throws ConnectException, SocketTimeoutException, WeatherException {
        InputStream in = null;
        try {
            URLConnection conn = url.openConnection();
            //Set timeout to 4 seconds
            conn.setConnectTimeout(4000);
            conn.connect();
            in = conn.getInputStream();

            image = new byte[0];
            byte buffer[] = new byte[5000];

            int currentSize = 0;
            try {
                while ((currentSize = in.read(buffer)) != -1) {
                    byte newImage[] = new byte[image.length + currentSize];
                    System.arraycopy(image, 0, newImage, 0, image.length);
                    System.arraycopy(buffer, 0, newImage, image.length,
                            currentSize);
                    image = newImage;
                }
            } catch (IOException ioe2) {
                throw new WeatherException(5004, ioe2, "Error occurred while "
                        + "attempting to read from URL: " + url);
            }
        } catch (IOException ioe) {
            throw new WeatherException(5003, ioe,
                    "Error attempting to connect to: " + url);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    //Do nothing we are just trying to close the connection.
                }
            }
        }
    }
}
