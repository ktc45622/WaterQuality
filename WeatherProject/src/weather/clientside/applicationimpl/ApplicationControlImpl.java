package weather.clientside.applicationimpl;

import weather.AdministratorControlSystem;
import weather.ApplicationControlSystem;
import weather.ClientControlSystem;
import weather.GeneralService;
import weather.InstructorControlSystem;
import weather.common.dbms.DBMSSystemManager;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;

/**
 * The Application Control service is designed to be a means to provide a central
 * control and communications medium that all GUI front-end classes may use to
 * communicate with the Authorization-Based control systems (the client, instructor,
 * and admin control systems), as well as
 * our General Service object.
 *
 * In addition, all opening control flow logic is handled here.
 *
 * @author jsgentil (2009)
 * @author Bloomsburg University Software Engineering
 * @version Spring 2009
 */
public class ApplicationControlImpl implements ApplicationControlSystem {

    private ClientControlSystem clientControlSystem;
    private InstructorControlSystem instructorControlSystem;
    private AdministratorControlSystem administratorControlSystem;
    private GeneralService generalService;
    private DBMSSystemManager DBMS;


    /**
     * Creates a new Application Control Impl with each Authorization-based GUI control
     * system as a parameter, along with the DBMS.
     * @param generalService The general service for this implementation.
     * @param clientcontrolsystem The client-level GUI control.
     * @param instructorcontrolsystem The instructor-level GUI control.
     * @param administratorcontrolsystem The admin-level GUI control.
     * @param DBMS The database manager system.
     */
    public ApplicationControlImpl(GeneralService generalService,
            ClientControlSystem clientcontrolsystem,
            InstructorControlSystem instructorcontrolsystem,
            AdministratorControlSystem administratorcontrolsystem, 
            DBMSSystemManager DBMS) {

            this.setClientControlSystem(clientcontrolsystem);
            this.setInstructorControlSystem(instructorcontrolsystem);
            this.setAdministratorControlSystem(administratorcontrolsystem);
            this.setDBMSSystem(DBMS);
            this.generalService = generalService;
    }

    /**
     * Returns the client-level GUI control system.
     * @return The client-level GUI control system.
     */
    @Override
    public ClientControlSystem getClientControlSystem()  {
        return clientControlSystem;
    }

    /**
     * Returns the instructor-level GUI control system.
     * @return The instructor-level GUI control system.
     */
    @Override
    public InstructorControlSystem getInstructorControlSystem() {
        return instructorControlSystem;
    }

    /**
     * Returns the admin-level GUI control system.
     * @return The admin-level GUI control system.
     */
    @Override
    public AdministratorControlSystem getAdministratorControlSystem(){
        return administratorControlSystem;
    }

    /**
     * Returns the back-end service provider.
     * @return The back-end service provider.
     */
    @Override
    public GeneralService getGeneralService(){
        return generalService;
    }

    /**
     * Sets the client-level GUI control system.
     * @param clientControlSystem The client-level GUI control system.
     */
    @Override
    public void setClientControlSystem(ClientControlSystem clientControlSystem){
        this.clientControlSystem = clientControlSystem;
    }

    /**
     * Sets the instructor-level GUI control system.
     * @param instructorControlSystem The instructor-level GUI control system.
     */
    @Override
    public void setInstructorControlSystem(InstructorControlSystem
                                            instructorControlSystem) {
        this.instructorControlSystem = instructorControlSystem;
    }

    /**
     * Sets the admin-level GUI control system.
     * @param administratorControlSystem The admin-level GUI control system.
     */
    @Override
    public void setAdministratorControlSystem(AdministratorControlSystem administratorControlSystem)
    {
        this.administratorControlSystem = administratorControlSystem;
    }

    /**
     * Sets the backend service.
     * @param generalService The backend service.
     */
    @Override
    public void setGeneralService(GeneralService  generalService){
        this.generalService =  generalService;
    }

    /**
     * Returns the database manager.
     * @return The DBMS manager.
     */
    @Override
    public DBMSSystemManager getDBMSSystem() {
        return this.DBMS;
    }

    /**
     * Sets the DBMS manager.
     * @param DBMS The new DBMS manager.
     */
    @Override
    public void setDBMSSystem(DBMSSystemManager DBMS) {
        this.DBMS = DBMS;
        if (generalService != null) {
            generalService.setDBMSSystem(DBMS);
        }
    }

  
    /**
     * Begins the login process, builds storage and resources, then calls on
     * the client-level control system to finish the job and open the main window.
     * @throws weather.common.utilities.WeatherException Not thrown in this implementation.
     */
    @Override
    public void mainApplicationControlService() throws WeatherException {
        this.clientControlSystem.login();
        Debug.println("login complete");
        clientControlSystem.mainApplicationControlService(this);
    }
}
