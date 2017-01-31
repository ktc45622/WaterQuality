package weather.common.gui.component;

import java.awt.Component;
import java.awt.Image;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import weather.AdministratorControlSystem;
import weather.ApplicationControlSystem;
import weather.common.dbms.DBMSEventManager;
import weather.common.utilities.ScreenInteractionUtility;

/**
 * Dialog that performs common operations on initialization, such as setting the
 * icon and centering the window.
 *
 * Usage: Call postInitialize after adding all content.
 *
 *        The correct way to use this class is to create a new class with the
 *        NetBeans form builder then change the extending class to this one.
 *
 * @author Eric Subach (2010)
 * @author Ty Vanderstappen (2012)
 * @version Spring 2012
 */
public class BUDialog extends JDialog {
    
    // Version information.
    public static final long serialVersionUID = 1;
    
    protected ApplicationControlSystem appControl;
    protected AdministratorControlSystem adminService;
    protected DBMSEventManager eventManager;

    // Success and fail messages for option dialog.
    protected String strSuccess, strFailure;

    private static Image icon;

    static {
        icon = IconProperties.getTitleBarIconImage();
    }
    
    /**
     * A function to make this object available to the inner classes.
     * @return This object as a <code>Component</code>.
     */
    private Component getBUDialog() {
        return this;
    }
    
    /**
     * The default constructor.
     */
    public BUDialog()
    {
        setup();
    }

    /**
     * Constructor that takes one ApplicationControlSystem.
     * @param appControl The application control system.
     */
    public BUDialog (ApplicationControlSystem appControl) {
        this.appControl = appControl;
        // Get event manager to communicate with DBMS.
        eventManager = appControl.getGeneralService().getDBMSSystem().getEventManager ();
        setup();
    }
    
    /**
     * Constructor that takes one AdministratorControlSystem.
     * @param adminService The administrator control system.
     */
    public BUDialog (AdministratorControlSystem adminService) {
        this.adminService = adminService;
        setup();
    }
    
    /**
     * Constructor that takes one ApplicationControlSystem and
     * one AdministratorControlSystem.
     * @param adminService The administrator control system.
     * @param appControl The application control system.
     */
    public BUDialog (AdministratorControlSystem adminService,
                        ApplicationControlSystem appControl) {
        this.adminService = adminService;
        this.appControl = appControl;
        // Get event manager to communicate with DBMS.
        eventManager = appControl.getGeneralService().getDBMSSystem().getEventManager ();
        setup();
    }
    
    /**
     * Completes basic JDialog functions such as setting the icon and
     * default close operation of the window.
     */
    private void setup()
    {
        setModalityType(ModalityType.APPLICATION_MODAL);
       
        //disposes window on close
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setIconImage (icon);

        // Default dialog messages.
        strSuccess = "Success.";
        strFailure = "Failure.";
        
        /**
         * Listeners needed to maintain the current screen location for the 
         * <code>ScreenInteractionUtility</code>.
         * 
         */
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            /**
             * After gaining focus, this code must update current screen
             * location for the <code>ScreenInteractionUtility</code>.
             * @param evt Not used.
             */
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                ScreenInteractionUtility.setFocusLocation(getBUDialog()
                        .getLocationOnScreen());
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            /**
             * After a window move, this code must update current screen
             * location for the <code>ScreenInteractionUtility</code>.
             * @param evt Not used.
             */
            @Override
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                ScreenInteractionUtility.setFocusLocation(getBUDialog()
                        .getLocationOnScreen());
            }
        });
    }

    /**
     * Operations to be performed after all content has been added, such as
     * making the window visible and causing it to appear on the screen.
     * 
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    protected void postInitialize(boolean shouldCenter) {
        ScreenInteractionUtility.positionWindow(this, shouldCenter);
        setVisible(true);
    }

    /**
     * Show a message dialog containing different responses depending on success
     * or failure.
     * @param flagSuccess Flag for success.
     */
    protected void showMessageDialog (boolean flagSuccess) {
        if (flagSuccess) {
            JOptionPane.showMessageDialog (this, strSuccess);
            dispose ();
        }
        else {
            JOptionPane.showMessageDialog (this, strFailure);
        }
    }
}
