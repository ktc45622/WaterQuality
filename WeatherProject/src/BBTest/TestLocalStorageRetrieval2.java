package BBTest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.storage.StorageCommand;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.serverside.storage.StorageServerLocal;

public class TestLocalStorageRetrieval2 {

    public static void main(String[] args) {
        //Setup local storage and program.
        Debug.setEnabled(true);
        DBMSSystemManager dbms = null;
        String fileSystemRoot = "C:";
        
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        StorageServerLocal localStore = new StorageServerLocal(dbms, fileSystemRoot);

        //Set range for below loop (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        //Set calendars to dates.  (Set in local time.)
        startCalendar.set(2016, GregorianCalendar.APRIL, 7, 7, 15, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2016, GregorianCalendar.APRIL, 8, 21, 59, 59);
        endCalendar.set(GregorianCalendar.MILLISECOND, 999);
        //Set resource range.
        ResourceRange range = new ResourceRange(new Date(startCalendar
                .getTimeInMillis()), new Date(endCalendar.getTimeInMillis()));
        
        //Setup resource numbers and resources.
        int videoResourceNumber = 102;
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
        
        //Storage command for provide testing.
        StorageCommand provideCommand = new StorageCommand();
        
        //Request object.
        ResourceInstancesRequested resourceRequest;
        
        Debug.println();

        //Test day-log mp4 movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        provideCommand.setCommandType(StorageCommandType.PROVIDE_DAY_LONG_MP4);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            showResult(localStore.executeLocalProvide(provideCommand));
        } catch (WeatherException ex) {
            ex.show();
        }
        
        Debug.println();

        //Test mp4 movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.mp4, movieResource);
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            showResult(localStore.executeLocalProvide(provideCommand));
        } catch (WeatherException ex) {
            ex.show();
        }

        Debug.println();

        //Test avi movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            showResult(localStore.executeLocalProvide(provideCommand));
        } catch (WeatherException ex) {
            ex.show();
        }
    }
    
    //Helper function to debug output.
    private static void showResult(ArrayList<ResourceInstancesReturned> list) {
        for (int i = 0; i < list.size(); i++) {
            Debug.println("\nResult index " + i + ":");
            ResourceInstancesReturned rir = list.get(i);
            for (int j = 0; j < rir.getNumberOfValuesReturned(); j++) {
                ResourceInstance instance = rir.getResourceInstances().get(j);
                if (instance == null) {
                    Debug.println("Element " + j + " is null.");
                } else {
                    Debug.println("Element " + j + ": Type: " + instance.getClass()
                        .toString() + instance.toString());
                }
            }
        }
    }
}
