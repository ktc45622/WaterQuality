package weather.common.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A class to manage zipping and unzipping Strings.
 * Uses best possible zip compression to compress Strings as much as possible.
 *<p>
 * Usage:<br/>
 *<code>
 * String s = "text here";<br/>
 * String compressed = ZipManager.compress(s);<br/>
 * String decompressed = ZipManager.decompress(compressed);<br/>
 *</code>
 * <code>'decompressed'</code> should be the same String as <code>'s'</code>.
 *</p>
 * @author Spring 2010
 * @author Ryan Kelly
 */
public class ZipManager {
    /**
     * Decompresses a compressed String and returns its uncompressed value as a
     * String object.
     * @param s the String to decompress
     * @return the decompressed String - the same as it was before compression.
     * @throws WeatherException if there is an error decompressing (bits out of order, etc.);
     */
    public static String decompress(String s) throws WeatherException{
        if(s.equals(""))
            return "";
        ByteArrayInputStream fis = null;
        ByteArrayOutputStream fos = null;
        String uncompressed = "";
        try {
            //gets bytes from compressed string in ISO-8859 format
            fis = new ByteArrayInputStream(s.getBytes("ISO-8859-1"));
            fos = new ByteArrayOutputStream();
            ZipInputStream zis = new ZipInputStream(fis);
            //Reposition the ZipInputStream to the beginning of the file
            zis.getNextEntry();
            final int BUFSIZ = 4096;
            byte inbuf[] = new byte[BUFSIZ];//create a byte buffer
            int n;
            //read from zipInputStream to buffer
            while ((n = zis.read(inbuf, 0, BUFSIZ)) != -1) {
                fos.write(inbuf, 0, n); //write from buffer to fileoutput stream
            }
            //Close streams
            zis.close();
            fos.close();
        } catch (IOException e) {
            throw new WeatherException(0, "Error compressing file");
        } finally {
            try {
                //try closing all streams
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                //Pull uncompressed bytes into new string.
                uncompressed = new String(fos.toByteArray());
            } catch (IOException e) {
                throw new WeatherException(0, "Error closing file compression stream");
            }
        }
        return uncompressed;
    }

    /**
     * Compresses a String using zip compression and returns it as a String
     * object.
     * @param s the String to compress
     * @return the String in compressed (zip) format.
     * @throws WeatherException if there is an error compressing (null string, etc).
     */
    public static String compress(String s) throws WeatherException {
        if(s.equals(""))
            return "";
        ByteArrayInputStream fis = null;
        ByteArrayOutputStream fos = null;
        String erg = "";
        try {
            fis = new ByteArrayInputStream(s.getBytes("ISO-8859-1"));
            fos = new ByteArrayOutputStream();

            ZipOutputStream zos =
                    new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry("name1");
            zos.putNextEntry(ze);
            final int BUFSIZ = 4096;
            byte inbuf[] = new byte[BUFSIZ];
            int n;
            while ((n = fis.read(inbuf)) != -1) {
                zos.write(inbuf, 0, n);
            }
            fis.close();
            fis = null;
            zos.close();
        } catch (IOException e) {
            throw new WeatherException(3020, "Error compressing file");
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                    erg = new String(fos.toByteArray(), "ISO-8859-1");
                }
            } catch (IOException e) {
                throw new WeatherException(0, "Error closing file compression stream");
            }
        }
        return erg;
    }

}
