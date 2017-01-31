/**
 * This class adapted from 
 * https://examples.javacodegeeks.com/desktop-java/imageio/list-read-write-supported-image-formats/
 * as a means to see which file type are supported by javax.imageio.ImageIO.
 */
package BBTest;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import weather.common.utilities.Debug;

public class LookForJavaImageIOTypes {

    public static void main(String[] args) throws IOException {
        
        Debug.setEnabled(true);

        Set<String> set = new HashSet<String>();

        // Get list of all informal format names understood by the current set of registered readers
        String[] formatNames = ImageIO.getReaderFormatNames();

        for (int i = 0; i < formatNames.length; i++) {
            set.add(formatNames[i]);
        }
        Debug.println("Supported read formats: " + set);

        set.clear();

        // Get list of all informal format names understood by the current set of registered writers
        formatNames = ImageIO.getWriterFormatNames();

        for (int i = 0; i < formatNames.length; i++) {
            set.add(formatNames[i]);
        }
        Debug.println("Supported write formats: " + set);

        set.clear();

        // Get list of all MIME types understood by the current set of registered readers
        formatNames = ImageIO.getReaderMIMETypes();

        for (int i = 0; i < formatNames.length; i++) {
            set.add(formatNames[i]);
        }
        Debug.println("Supported read MIME types: " + set);

        set.clear();

        // Get list of all MIME types understood by the current set of registered writers
        formatNames = ImageIO.getWriterMIMETypes();

        for (int i = 0; i < formatNames.length; i++) {
            set.add(formatNames[i]);
        }
        Debug.println("Supported write MIME types: " + set);

    }

}
