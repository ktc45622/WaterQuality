package BBTest;

import java.io.File;

/**
 * This is a program used to make some testing possible.  It is intended to 
 * rename image stored in a particular location (C:\Batch Testing\Images).
 */
public class RenameTestImages {
    
    public static void main(String[] args) {
        File folder = new File("C:\\Batch Testing\\Images");
        int imageNum = 1;
        for (File file : folder.listFiles()) {
            file.renameTo(new File("C:\\Batch Testing\\Images\\image"
                + imageNum + ".jpg"));
            imageNum++;
        }
    }
}
