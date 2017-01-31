package BBTest;

/**
 * @author Dr. Curt Jones 
 */
import java.io.File;
import weather.common.utilities.PropertyManager;

public class DiskSpaceDetail {
    public static void main(String[] args) { 
        File file = new File("c:");
        long totalSpace = file.getTotalSpace(); //total disk space in bytes.
        long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
        long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.
        
        //local warning and termination free space levels
        long localWarningLevel = Long.parseLong(PropertyManager
                .getGeneralProperty("DIRECTORY_CHANGE_RECOMMENDED"));

        long localRequredChangeLevel = Long.parseLong(PropertyManager
                .getGeneralProperty("DIRECTORY_CHANGE_REQUIRED"));
        
        System.out.println(" === Partition Detail ===");

        System.out.println(" === bytes ===");
        System.out.println("Total size : " + totalSpace + " bytes");
        System.out.println("Space free : " + usableSpace + " bytes");
        System.out.println("Space free : " + freeSpace + " bytes");
        System.out.println("Warning level : " + localWarningLevel + " bytes");
        System.out.println("Requred Change Level : " + localRequredChangeLevel 
                + " bytes");

        System.out.println(" === mega bytes ===");
        System.out.println("Total size : " + totalSpace / 1024 / 1024 + " mb");
        System.out.println("Space free : " + usableSpace / 1024 / 1024 + " mb");
        System.out.println("Space free : " + freeSpace / 1024 / 1024 + " mb");
        System.out.println("Warning level : " + localWarningLevel / 1024 / 1024 
                + " mb");
        System.out.println("Requred Change Level : " 
                + localRequredChangeLevel / 1024 / 1024 + " mb");
    }
}
