package weather.background_task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import weather.AdministratorControlSystem;
import weather.ApplicationControlSystem;
import weather.ClientControlSystem;
import weather.GeneralService;
import weather.InstructorControlSystem;
import weather.clientside.applicationimpl.AdminControlImpl;
import weather.clientside.applicationimpl.ApplicationControlImpl;
import weather.clientside.applicationimpl.ClientControlImpl;
import weather.clientside.applicationimpl.GeneralServiceImpl;
import weather.clientside.applicationimpl.InstructorControlImpl;
import weather.clientside.utilities.ResourceTreeManager;
import weather.clientside.utilities.StorageSpaceTester;
import weather.common.dbms.DBMSSystemManager;
import weather.common.gui.component.SplashScreenWindow;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;

/**
 * This will connect to all the servers and databases for the background
 * downloader and run the background downloader window.
 *
 * @author Colton Daily (2014)
 * @version Spring 2014
 */
public class BackgroundTaskMainProgram {

    private static ApplicationControlSystem applicationControl;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Creates a splash screen that will be displayed until the user presses
        // a key or clicks a mouse button.
        new SplashScreenWindow();
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                /*
                * Set GeneralWeather.properties to indicate that a client-side
                * jar is running.  This is necessary to keep the installer or
                * uninstaller from running while a client-side jar is also 
                * running.
                */
                PropertyManager.configure();
                PropertyManager.setGeneralProperty("ClientRunning", "true");

                /*
                 * Create the main control systems
                 * Our application needs 6 control systems to work. All our
                 * control systems need access to the General Service object.
                 *
                 */
                DBMSSystemManager dbms = null;
                try {
                    dbms = getDBMSSystemManager(); // Database System
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                }

                GeneralService generalService = new GeneralServiceImpl(dbms);

                ClientControlSystem clientControl
                        = new ClientControlImpl(generalService);

                InstructorControlSystem instructorControl
                        = new InstructorControlImpl(generalService);

                AdministratorControlSystem adminControl
                        = new AdminControlImpl(generalService);

                applicationControl
                        = new ApplicationControlImpl(generalService,
                                clientControl, instructorControl, adminControl, dbms);
                Debug.println("got controls");

                //Tell our resource tree manager what storage system to use.
                ResourceTreeManager.setStorageControlSystem(
                        StorageControlSystemImpl.getStorageSystem());
                
                //Show login window, which exits progran if iogin isn't completed.
                new BackgroundTaskLoginWindow(generalService);
                
                //Test for space to write files.
                StorageSpaceTester.testApplicationHome();
                
                //Show main window.
                new BackgroundTaskWindow(applicationControl);
            }
        });
    }

    private static DBMSSystemManager getDBMSSystemManager() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String dbmsClassName = PropertyManager.getGeneralProperty("dbmsClassName");
        Method dbmsInstanceGetter = null;
        try {
            dbmsInstanceGetter = Class.forName(dbmsClassName).getDeclaredMethod("getInstance");
            return (DBMSSystemManager) dbmsInstanceGetter.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException ex) {
            return null;
        }
    }
}
