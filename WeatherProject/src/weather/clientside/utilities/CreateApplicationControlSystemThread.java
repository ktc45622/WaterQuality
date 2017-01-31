package weather.clientside.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
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
import weather.common.dbms.DBMSSystemManager;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * Thread that creates an ApplicationControlSystem.
 *
 * This is used at program startup.
 *
 * @author Eric Subach (2011)
 */
public class CreateApplicationControlSystemThread extends Thread {
    private ApplicationControlSystem applicationControl;

    @Override
    public void run() {
        try {
            
            /*
             * Create the main control systems
             * Our application needs 6 control systems to work. All our
             * control systems need access to the General Service object.
             *
             */
            DBMSSystemManager dbms = getDBMSSystemManager(); // Database System
            //Debug.println ("got DBMS");
            
            
            
            
            GeneralService generalService = new GeneralServiceImpl(dbms);
            //Debug.println ("get general service");

            ClientControlSystem clientControl =
                    new ClientControlImpl(generalService);

            InstructorControlSystem instructorControl =
                   new InstructorControlImpl(generalService);

            AdministratorControlSystem adminControl =
                   new AdminControlImpl(generalService);

            applicationControl =
                   new ApplicationControlImpl(generalService,
                    clientControl, instructorControl,  adminControl, dbms);
            Debug.println ("got controls");

            
            //Tell our resource tree manager what storage system to use.
            ResourceTreeManager.setStorageControlSystem(
                    StorageControlSystemImpl.getStorageSystem());
            Debug.println ("set storage control system");
        }
        catch (ClassNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, "Missing expected class in Main. Please obtain the current version of program.", ex);
            WeatherException exception = new WeatherException(015, true,ex);
            exception.show("Missing expected class. Please obtain the current version of program.");
        } catch (InstantiationException ex) {
            WeatherLogger.log(Level.SEVERE, "Failed instantiation in Main.", ex);
            WeatherException exception = new WeatherException(016, true,ex);
            exception.show("Failed instantiation.");
        } catch (IllegalAccessException ex) {
            WeatherLogger.log(Level.SEVERE, "Illegal access in Main.", ex);
            WeatherException exception = new WeatherException(017, true,ex);
            exception.show("Program has just attempted to access something it shouldn't have.");
        }
    }


    public ApplicationControlSystem getApplicationControlSystem() {
        return applicationControl;
    }
    
    private DBMSSystemManager getDBMSSystemManager() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
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
