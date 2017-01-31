package weather.serverside.old_weather_underground_retrieval;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.gui.component.IconProperties;
import weather.common.gui.component.SplashScreenWindow;
import weather.common.utilities.Debug;
import weather.common.utilities.ScreenInteractionUtility;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

/**
 * This is the main program that will launch the window that retrieves old data
 * from Weather Underground weather stations.
 */
public class OldWeatherUndergroundFileRetrieverMainProgram {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Creates a splash screen that will be displayed until the user presses
        // a key or clicks a mouse button.
        new SplashScreenWindow();
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException 
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                //Get all active video-making resources.
                DBMSSystemManager dbms = null;
                try {
                    dbms = MySQLImpl.getMySQLDMBSSystem();
                } catch (ClassNotFoundException 
                        | InstantiationException | IllegalAccessException e) {
                    showErrorPaneAndExit("Could not get a database instance.\n"
                        + e.toString(), "No Database Connection");
                }
                Vector<Resource> resources
                        = dbms.getResourceManager().getResourceList();

                //Screen for correct resoures.
                ArrayList<Resource> activeStationResources = new ArrayList<>();
                for (Resource resource : resources) {
                    WeatherResourceType type = resource.getResourceType();

                    if (resource.isActive()) {
                        if (type == WeatherResourceType.WeatherStationValues) {
                            activeStationResources.add(resource);
                        }
                    }
                }
                
                //Check selected resources.
                if (activeStationResources.isEmpty()) {
                    showErrorPaneAndExit("No active weather stations were "
                     + "found.", "No Resources Found");
                } else {
                    Debug.println("\nActive Video Resources:\n");
                    for (Resource r : activeStationResources) {
                        Debug.println(r.getName());
                    }
                    Debug.println("RESOURCE COUNT: " + activeStationResources
                            .size() + "\n");
                }
                
                //Get storage system.
                StorageControlSystem storage 
                        = new StorageControlSystemLocalImpl(dbms);
                Debug.println("\nGot storage system. (Root Drive: "
                        + storage.getRootDirectory() + ")\n");
                
                //Show main window here.
                new OldWeatherUndergroundFileRetrieverMainForm(storage,
                        activeStationResources);
                
                //Close database connections
                dbms.closeDatabaseConnections();
                
                System.exit(0);
            }
        });
    }
    
    /**
     * Helper function to show an error message and close the program.
     * @param message The message in the box.
     * @param title The title of the box.
     */
    private static void showErrorPaneAndExit(String message, String title) {
        JOptionPane pane = new JOptionPane(message + "\n\nTHE PROGRAM WILL NOW "
                + "CLOSE.", JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        ScreenInteractionUtility.positionWindow(dialog, true);
        dialog.setVisible(true);
        System.exit(1);
    }
}
