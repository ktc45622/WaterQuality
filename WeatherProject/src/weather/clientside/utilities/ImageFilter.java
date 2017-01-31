package weather.clientside.utilities;

import java.io.File;
import javax.swing.filechooser.*;
import weather.common.utilities.ImageFileTester;

/**
 * Filter the set of image files shown to the user by type.
 *
 * @author arb35598
 */
public class ImageFilter extends FileFilter {

    //Accept all directories and all gif, jpg, bmp, wbmp or png files.
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        return ImageFileTester.isImageFile(f);
    }

    //The description of this filter
    @Override
    public String getDescription() {
        return "All Supported Image Types";
    }
}
