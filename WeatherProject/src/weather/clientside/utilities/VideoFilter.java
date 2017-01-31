package weather.clientside.utilities;

import java.io.File;
import javax.swing.filechooser.*;

/**
 * Filter the set of video files shown to the user by type.
 *
 * @author arb35598
 */
public class VideoFilter extends FileFilter {

    //Accept all directories and all avi or mp4 files.
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        return f.getAbsolutePath().endsWith(".avi") || f.getAbsolutePath()
                .endsWith(".mp4");
    }

    //The description of this filter
    @Override
    public String getDescription() {
        return "All Supported Video Types";
    }
}
