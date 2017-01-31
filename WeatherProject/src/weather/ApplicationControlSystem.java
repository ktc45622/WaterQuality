package weather;

import weather.common.dbms.DBMSSystemManager;
import weather.common.utilities.WeatherException;

/**
 * This interface specifies the operations needed to encapsulate the
 * current set of
 * application control systems required for our
 * application to execute in one location.
 * An instance of this interface is passed amongst the
 * other control system objects so that each object has access to the
 * other control objects it needs. For example,
 * it allows administrator method to access
 * methods used by instructors, students and guests. Without this specification,
 * each method would need to be passed 6 objects instead of one object.
 *
 * @author Brandon McKenzie (2009)
 * @author Bloomsburg University Software Engineering
 * @version Spring 2009
 */
public interface ApplicationControlSystem {

    /**
     * Specifies the signature of a method that returns the current ClientControlSystem.
     * @return the current ClientControlSystem.
     */
    public ClientControlSystem getClientControlSystem();

    /**
     * Specifies the signature of a method that returns the current InstructorControlSystem.
     * @return the current InstructorControlSystem.
     */
    public InstructorControlSystem getInstructorControlSystem();

    /**
     * Specifies the signature of a method that returns the current AdministratorControlSystem.
     * @return the current AdministratorControlSystem.
     */
    public AdministratorControlSystem getAdministratorControlSystem();

    /**
     * Specifies the signature of a method that returns the Service class.
     * @return the service class.
     */
    public GeneralService getGeneralService();

    /**
     * Specifies the signature of a method that sets the ClientControlSystem.
     * @param clientControlSystem the ClientControlSystem to use.
     */
    public void setClientControlSystem(ClientControlSystem clientControlSystem);

    /**
     * Specifies the signature of a method that sets the InstructorControlSystem.
     * @param instructorControlSystem the InstructorControlSystem to use.
     */
    public void setInstructorControlSystem(InstructorControlSystem instructorControlSystem);

    /**
     * Specifies the signature of a method that sets the current AdministratorControlSystem.
     * @param applicationControlSystem the AdministratorControlSystem to use.
     */
    public void setAdministratorControlSystem(AdministratorControlSystem applicationControlSystem);

    /**
     * Specifies the signature of a method that sets the Service class.
     * @param generalService the Service class.
     */
    public void setGeneralService(GeneralService generalService);

    /**
     * Specifies the signature of a method that returns the current DBMS.
     * @return the current DBMS.
     */
    public DBMSSystemManager getDBMSSystem();

    /**
     * Specifies the signature of a method that sets the current DBMS.
     * @param DBMS the new DBMS to use.
     */
    public void setDBMSSystem(DBMSSystemManager DBMS);

   
    /**
     * Specifies the signature of a method that initializes all other GUI
     * control and backend service classes, then displays
     * the main window.
     * @throws weather.common.utilities.WeatherException
     */
    public void mainApplicationControlService() throws WeatherException;
}
