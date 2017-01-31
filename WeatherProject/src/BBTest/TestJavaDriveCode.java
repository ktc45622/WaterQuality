package BBTest;

/**
 * This is program to test Java's recognition of drives. 
 */

import java.io.File;
import weather.common.utilities.Debug;

public class TestJavaDriveCode {
    public static void main(String[] args) {
        File withSlash = new File("E:\\");
        File noSlash = new File("E:");
        Debug.println("Testing with slash:");
        for (File file : withSlash.listFiles()) {
            Debug.println(file.getName());
        }
        Debug.println();
        Debug.println("Testing without slash:");
        for (File file : noSlash.listFiles()) {
            Debug.println(file.getName());
        }
    }  
}
