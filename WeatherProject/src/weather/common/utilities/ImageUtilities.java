package weather.common.utilities;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Provides utilities for saving and viewing images from the server.
 *
 * @author Bloomsburg University Software Engineering
 * @author Ryan Hipple (2007)
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class ImageUtilities {
    
    /**
     * Saves a specified URL to a specified local file.
     * <p>
     * Sample Usage:<br /><blockquote>
     *
     * File destFile = new File("C:\\temp.jpg");<br />
     * URL srcURL = new URL("http://148.137.179.233/netcam.jpg");<br />
     * <strong>ImageUtilities.downloadImage(srcURL, destFile);</strong><br />
     * JFrame frame = new JFrame("images");<br />
     * JLabel imglbl = ImageUtilities.imageFileToJLabel(destFile);<br />
     * frame.getContentPane().add(imglbl, BorderLayout.CENTER);<br />
     * frame.pack();<br />
     * frame.setVisible(true);</blockquote>
     *
     * @param src the source URL that is to be downloaded.
     * @param dest the destination file in which to save the URL data.
     * @throws FileNotFoundException If the destination file is not found.
     *   If just the file is missing, it will be created.  The exception
     *   is thrown when the parent directory is also not found.
     * @throws IOException If an input or output error occurs regarding the file.
     */
    public static void downloadImage(URL src, File dest)
    throws FileNotFoundException, IOException {
        
        FileOutputStream fos = null;
        fos = new  FileOutputStream(dest);
        
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        BufferedInputStream buff = new BufferedInputStream( src.openStream());
        
        boolean eof = false;
        while (!eof) {
            int byteValue = buff.read();
            bos.write(byteValue);
            if (byteValue  == -1)
                eof = true;
        }
        bos.close();
    }
    
    /**
     * Saves a specified URL to a local file, specified by a string.
     * <p>
     * Sample Usage:<br /><blockquote>
     *
     * String destFile = "C:\\temp.jpg";<br />
     * String srcURL = "http://148.137.179.233/netcam.jpg";<br />
     * <strong>ImageUtilities.downloadImage(srcURL, destFile);</strong><br />
     * JFrame frame = new JFrame("images");<br />
     * JLabel imglbl = ImageUtilities.imageFileToJLabel(destFile);<br />
     * frame.getContentPane().add(imglbl, BorderLayout.CENTER);<br />
     * frame.pack();<br />
     * frame.setVisible(true);</blockquote>
     * @param src the source URL that is to be downloaded.
     * @param dest a string representing a file path in which to save the file.
     * @throws MalformedURLException If the src String is not a valid URL.
     * @throws FileNotFoundException If the destination file is not found.
     *   If just the file is missing, it will be created.  The exception
     *   is thrown when the parent directory is also not found.
     * @throws IOException If an input or output error occurs regarding the file.
     */
    public static void downloadImage(String src, String dest)
    throws MalformedURLException, FileNotFoundException, IOException {
        File destFile = new File(dest);
        URL srcURL = new URL(src);
        downloadImage(srcURL,destFile);
    }
    /**
     * Saves the data at a specified URL to a local file, specified by a string.
     * <p>
     * Sample Usage:<br /><blockquote>
     *
     * String destFile = "C:\\temp.jpg";<br />
     * URL srcURL = new URL("http://148.137.179.233/netcam.jpg");<br />
     * <strong>ImageUtilities.downloadImage(srcURL, destFile);</strong><br />
     * JFrame frame = new JFrame("images");<br />
     * JLabel imglbl = ImageUtilities.imageFileToJLabel(destFile);<br />
     * frame.getContentPane().add(imglbl, BorderLayout.CENTER);<br />
     * frame.pack();<br />
     * frame.setVisible(true);</blockquote>
     * @param src the source URL that is to be downloaded.
     * @param dest a string representing a file path in which to save the file.
     * @throws FileNotFoundException If the destination file is not found.
     *   If just the file is missing, it will be created.  The exception
     *   is thrown when the parent directory is also not found.
     * @throws IOException If an input or output error occurs regarding the file.
     */
    public static void downloadImage(URL src, String dest)
    throws FileNotFoundException, IOException {
        File destFile = new File(dest);
        downloadImage(src,destFile);
    }
    /**
     * Saves a URL, specified by a string, to a specified local file.
     * <p>
     * Sample Usage:<br /><blockquote>
     *
     * File destFile = new File("C:\\temp.jpg");<br />
     * String srcURL = "http://148.137.179.233/netcam.jpg";<br />
     * <strong>ImageUtilities.downloadImage(srcURL, destFile);</strong><br />
     * JFrame frame = new JFrame("images");<br />
     * JLabel imglbl = ImageUtilities.imageFileToJLabel(destFile);<br />
     * frame.getContentPane().add(imglbl, BorderLayout.CENTER);<br />
     * frame.pack();<br />
     * frame.setVisible(true);</blockquote>
     * @param src a string representing a URL of the source file.
     * @param dest the destination file in which to save the URL data.
     * @throws MalformedURLException
     * @throws FileNotFoundException If the destination file is not found.
     *   If just the file is missing, it will be created.  The exception
     *   is thrown when the parent directory is also not found.
     * @throws IOException If an input or output error occurs regarding the file.
     */
    public static void downloadImage(String src, File dest)
    throws MalformedURLException, FileNotFoundException, IOException {
        URL srcURL = new URL(src);
        downloadImage(srcURL,dest);
    }
    
    /**
     * Gives the user a JLabel, containing a specified image file, that is easily inserted into a GUI.
     * <p>
     * Sample Usage:<br /><blockquote>
     *
     * File destFile = new File("C:\\temp.jpg");<br />
     * URL srcURL = new URL("http://148.137.179.233/netcam.jpg");<br />
     * ImageUtilities.downloadImage(srcURL, destFile);<br />
     * JFrame frame = new JFrame("images");<br />
     * <strong>JLabel imglbl = ImageUtilities.imageFileToJLabel(destFile);</strong><br />
     * frame.getContentPane().add(imglbl, BorderLayout.CENTER);<br />
     * frame.pack();<br />
     * frame.setVisible(true);</blockquote>
     * @param imageFile the file where the image is located that will be encapsulated in a JLabel.
     * @return A JLabel containing the specified image.  This JLabel is then easily inserted into a GUI.
     * @throws IOException If there are errors reading from the file.
     */
    public static JLabel imageFileToJLabel(File imageFile)
    throws IOException {
        BufferedImage input = null;
        input = ImageIO.read(imageFile);
        Icon icon = new ImageIcon(input);
        JLabel label = new JLabel(icon);
        return label;
    }
    /**
     * Gives the user a JLabel, containing a specified image file, that is easily inserted into a GUI.
     * <p>
     * Sample Usage:<br /><blockquote>
     *
     * String destFile = "C:\\temp.jpg";<br />
     * URL srcURL = new URL("http://148.137.179.233/netcam.jpg");<br />
     * ImageUtilities.downloadImage(srcURL, destFile);<br />
     * JFrame frame = new JFrame("images");<br />
     * <strong>JLabel imglbl = ImageUtilities.imageFileToJLabel(destFile);</strong><br />
     * frame.getContentPane().add(imglbl, BorderLayout.CENTER);<br />
     * frame.pack();<br />
     * frame.setVisible(true);</blockquote>
     * @param imageString the file name where the image is located that will be encapsulated in a JLabel.
     * @return a JLabel containing the specified image.  This Jlabel is then easily inserted into a GUI.
     * @throws IOException If there are errors reading from the file.
     */
    public static JLabel imageFileToJLabel(String imageString)
    throws IOException {
        return imageFileToJLabel(new File(imageString));
    }

     /**
     * Downloads an image from a given url and saves it to a file destination
     *
     *@param url the url to download from
     *@param dest the destination to save the image to
     * @throws WeatherException
     */

    public void downloadImage2(String url, String dest) throws WeatherException {
        try {
            FileOutputStream fos = null;
            fos = new FileOutputStream(dest);

            BufferedOutputStream bos = new BufferedOutputStream(fos);
            BufferedInputStream buff = new BufferedInputStream(new URL(url).openStream());

            boolean eof = false;
            while (!eof) {
                int byteValue = buff.read();
                bos.write(byteValue);
                if (byteValue == -1) {
                    eof = true;
                }
            }
            bos.close();
        } catch (MalformedURLException ex) {
            throw new WeatherException(5001, ex);
        } catch (FileNotFoundException ex) {
            throw new WeatherException(5001, ex);
        } catch (IOException ex) {
            throw new WeatherException(5002, ex);
        }

    }
}
