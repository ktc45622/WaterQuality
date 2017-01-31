package weather.common.utilities;

import java.io.File;

/**
 * Utility class for images. Provides a way to check for a valid image file.
 *
 * @author arb35598
 */
public class ImageFileTester {
    private final static String JPEG = "jpeg";
    private final static String JPG = "jpg";
    private final static String GIF = "gif";
    private final static String BMP = "bmp";
    private final static String WBMP = "wbmp";
    private final static String PNG = "png";

    /**
     * Checks for a valid image file.
     * @param f The file to check.
     * @return True if the file is of a valid image type; False otherwise,
     */
    public static boolean isImageFile(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        
        if (ext == null) {
            return false;
        }
        
        return ext.equals(JPEG) || ext.equals(JPG) || ext.equals(GIF) 
                || ext.equals(BMP) || ext.equals(WBMP) || ext.equals(PNG);
    }
}

