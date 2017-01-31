package BBTest;

import weather.common.data.version.Version;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.DBMSVersionManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

public class TestVersionManager {

    public static void main(String[] args) {
        //Setup dbms.
        DBMSSystemManager dbms = null;

        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        
        //Check dbms.
        if (dbms == null) {
            Debug.println("NO DBMS!");
        } else {
            Debug.println("Got DBMS.");
        }
        
        //construct test data.
        Version[] versions = new Version[5];
        versions[0] = new Version(1, 0, 0);
        versions[0].setVersionNotes("Version 1");
        versions[1] = new Version(1, 1, 0);
        versions[1].setVersionNotes("Version 1.1");
        versions[2] = new Version(1, 1, 1);
        versions[2].setVersionNotes("Version 1.1.1");
        versions[3] = new Version(3, 0, 0);
        versions[3].setVersionNotes("Version 3");
        versions[4] = new Version(2, 0, 0); //No notes for testing purposes.
        
        //Get manager from database.
        DBMSVersionManager vm = dbms.getVersionManager();
          
        //Insert test data.
        for (int i = 0; i < 5; i++) {
            vm.insertVersion(versions[i]);
        }
        
        //Show sorted data.
        Debug.println("Sorted Database Versions:");
        for (Version version : vm.getAllVersions()) {
            Debug.println("Version: " + version.toString() + " ("
                    + version.getVersionNotes() + ")");
        }
        
        //Find newent version.
        String newestVersionString = vm.getMostResentVersion().toString() + " ("
                + vm.getMostResentVersion().getVersionNotes() + ")";
        
        Debug.println("Newest saved version: " + newestVersionString);
        
        //Test note update.
        versions[4].setVersionNotes("Version 2");
        vm.updateVersionNotes(versions[4]);
        
        //Show sorted data after update.
        Debug.println("Sorted Database Versions After Update:");
        for (Version version : vm.getAllVersions()) {
            Debug.println("Version: " + version.toString() + " ("
                    + version.getVersionNotes() + ")");
        }
    }    
}
