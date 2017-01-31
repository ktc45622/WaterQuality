/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.common.data.resource;

import java.io.*;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import weather.common.utilities.WeatherException;

/**
 * This class stores an instance of a Web page in a machine 
 * independent format. The image can be transfered and
 * stored on different machines.
 * 
 * @author Nate Hartzler 2012
 * @version Spring 2012
 */
public class WebPageInstance extends ResourceInstance implements Serializable {

    public WebPageInstance(int userNumber, String name, byte[] fileData) {
        this.name=name;
        this.userNumber=userNumber;
        this.zipFile=fileData;
    }

    private static final long serialVersionUID = 1;
    byte[] zipFile;
    int userNumber;
    String name;
    

    /**
     * Writes the web page contained in this object to the specified file.
     * @param file Specifies the file to be written.
     * @throws WeatherException
     */
    @Override
    public void writeFile(File file) throws WeatherException {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(zipFile);
            out.close();
        } catch (FileNotFoundException ex1) {
            throw new WeatherException(4001, ex1);
        } catch (IOException ex2) {
            throw new WeatherException(4001, ex2);
        }
    }


    /**
     * Returns the web page attribute of this <code>WebPageInstance</code>.
     *
     * @return The byte array of this <code>WebPageInstance</code>.
     */
    public byte[] getWebPageBytes() {
        return zipFile;
    }

    /**
     * Returns the length of this web page attribute.
     *
     * @return The length of the byte array.
     */
    public int length() {
        return zipFile.length;
    }

    /**
     * This method will read the web page contained in a local zip file. 
     * @param file Specifies the location of the zip file containing the web page.
     * @throws WeatherException
     */
    @Override
    public void readFile(File file) throws WeatherException {
        try {
            FileInputStream in = new FileInputStream(file);
            FileChannel fc = in.getChannel();

            zipFile = new byte[(int) fc.size()];

            ByteBuffer bb = ByteBuffer.wrap(zipFile);
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
     * Downloads the zip file located at the specified URL.
     * @param url The URL containing the zip file.
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

            zipFile = new byte[0];
            byte buffer[] = new byte[5000];

            int currentSize = 0;
            try {
                while ((currentSize = in.read(buffer)) != -1) {
                    byte newWebPage[] = new byte[zipFile.length + currentSize];
                    System.arraycopy(zipFile, 0, newWebPage, 0, zipFile.length);
                    System.arraycopy(buffer, 0, newWebPage, zipFile.length,
                            currentSize);
                    zipFile = newWebPage;
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

