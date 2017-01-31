package BBTest;

import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

public class TestLocalStorageFileStructureCreation {

    public static void main(String[] args) {
        Debug.setEnabled(true);

        //Setup local storage
        DBMSSystemManager dbms = null;
        String fileSystemRoot = "E:";

        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        StorageControlSystemLocalImpl localServer = new StorageControlSystemLocalImpl(dbms, fileSystemRoot);
        
        if (localServer.createFileStructure()) {
            Debug.println("File Structure Made.");
        } else {
            Debug.println("File Structure NOT Made.");
        }
    }
}
