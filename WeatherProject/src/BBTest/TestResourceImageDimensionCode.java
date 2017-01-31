/**
 * This file is to test the code for handling resource image dimensions stored 
 * in the database.
 */

package BBTest;

import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSResourceManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

public class TestResourceImageDimensionCode {

    public static void main(String[] args) {
        //Setup DBMS.
        DBMSSystemManager dbms = null;

        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        
        //Change image dimensions or a stored resource.
        DBMSResourceManager rm = dbms.getResourceManager();
        Resource dbResource = rm.getWeatherResourceByNumber(129);
        
        Debug.print("Before Change - ");
        Debug.println(dbResource.toString());
        
        dbResource.setImageWidth(500);
        dbResource.setImageHeight(450);
        dbResource.setUpdateHour(5);
        
        Debug.print("\nAfter Change - ");
        Debug.println(dbResource.toString());
        
        //Copy and check identical(...).
        Resource copiedResource = new Resource(dbResource);
        
        Debug.print("\nCopied ");
        Debug.println(copiedResource.toString());
        if (dbResource.identical(copiedResource)) {
            Debug.println("\nResources identical.");
        } else {
            Debug.println("\nResources NOT identical.");
        }
        
        //Change fields and recheck identical(...).
        copiedResource.setResourceNumber(-1);   //To mark as unsaved.
        copiedResource.setOrderRank(0);         //To mark as unsaved.
        copiedResource.setImageWidth(400);
        copiedResource.setImageHeight(300);
        copiedResource.setUpdateHour(0);
        copiedResource.setName("Test");
        copiedResource.setStorageFolderName("Test");
        
        Debug.print("\nAfter Changes - Copied ");
        Debug.println(copiedResource.toString());
        if (dbResource.identical(copiedResource)) {
            Debug.println("\nResources identical.");
        } else {
            Debug.println("\nResources NOT identical.");
        }
        
        Debug.println();
        
        //Update database.
        Debug.println("Updating database.");
        if (rm.updateWeatherResource(dbResource).getResourceNumber() == -1) {
            Debug.println("Resoure NOT Updated.");
        }
        Debug.println("Inserting copy.");
        if (rm.updateWeatherResource(copiedResource).getResourceNumber() == -1) {
            Debug.println("Resoure NOT Added.");
        }
        Debug.println("Done. - Check database.");
    }
}
