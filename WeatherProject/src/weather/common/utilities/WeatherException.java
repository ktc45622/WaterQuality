package weather.common.utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import weather.common.gui.component.IconProperties;

/**
 * This class is our exception class.
 * Each exception has a number and an error
 * message.
 *
 *@author Bloomsburg University Software Engineering
 *@author Dave Moser (2008)
 *@author Joe Sharp (2009)
 *
 *@version Spring 2009
 */
public class WeatherException extends java.lang.Exception {

    /**
     * This error number is for use when we have an Exception from the
     * GeneralPropertiesSingleton and cannot access any properties files for
     * error numbers or messages.  This error number is stored locally
     * to deal with this situation.
     */
    public static final int GEN_PROP_ERROR = Integer.MIN_VALUE;

    /**
     * This error message corresponds to the GEN_PROP_ERROR error number and is
     * also for use in the case the we cannot access any properties files.
     */
    private static final String GEN_PROP_ERROR_MESSAGE = "Unable to load " +
            "GeneralProperties.properties file.  This file is " +
            "either missing or corrupt.";

    private static String sAdditionalMessage = "";

    private boolean isFatal = false;
    
    /**
     * Error number of this exception. This number identifies
     * a particular error message. These messages are loaded from a property file.
     * listing.
     */
    private int errorNumber;

    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown. 
     * 
     * This is the same as running WeatherException(0,false).
     * 
     * @see ExceptionSingleton for more information
     */
    public WeatherException() {
        this(0,false);
    }

    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown.
     * 
     * This is a utility and is the same as running WeatherException(msgId,false).
     * 
     * @see ExceptionSingleton for more information
     * 
     * @param msgId - the integer Identification number for the error message to be
     * associated with this exception.
     */
    public WeatherException(int msgId) {
        this(msgId, false);
    }

    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown and if this exception is fatal.
     * 
     * @param msgId
     * @param isFatal
     */
    public WeatherException(int msgId, boolean isFatal) {
        super(ExceptionSingleton.getInstance().getExceptionString(msgId));
        errorNumber = msgId;
        this.isFatal = isFatal;
    }
    
    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown and if this exception is fatal. Passing an instance of
     * throwable is used for chaining exceptions.  The <code>msgId</code> is
     * checked to see if it is the <code>GEN_PROP_ERROR</code>.  If so, the
     * <code>GEN_PROP_ERROR_MESSAGE</code> is passed to the super constructor.
     * Otherwise the error message is retrieved from the WeatherException
     * Properties file.  This is the only constructor that checks for the
     * General Properties error, as it is the only one that will be used in
     * GeneralPropertiesSingleton.
     * 
     * @param msgId the ID number of the message to be included in this exception
     * @param isFatal if this exception should be considered fatal.
     * @param oldE the original exception, used for chaining.
     */
    public WeatherException(int msgId, boolean isFatal, Throwable oldE){
        super((msgId == GEN_PROP_ERROR?GEN_PROP_ERROR_MESSAGE:ExceptionSingleton.getInstance().getExceptionString(msgId)), oldE);
        errorNumber = msgId;
        this.isFatal = isFatal;
    }
    
    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown. Passing an instance of
     * throwable is used for chaining exceptions.
     * 
     * @param msgId the ID number of the message to be included in this exception
     * @param oldE the original exception, used for chaining.
     */
    public WeatherException(int msgId, Throwable oldE){
        this(msgId, false, oldE);
    }

    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown. Since there are no integer parameters, it defaults
     * to error id of 0. So this is the same as running
     *   WeatherException(0,false).
     * It also takes the string parameter given and adds it to the the
     * additional message attribute of the class.  This will be included in the
     * error message when shown.
     *
     * @param message The additional message to add to the exception.
     *
     * @see ExceptionSingleton for more information
     */
    public WeatherException(String message)
    {
        this();
        sAdditionalMessage = message;
    }

    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown.
     *
     * This is a utility and is the same as running WeatherException(msgId,false).
     *
     * It also takes the string parameter given and adds it to the the
     * additional message attribute of the class.  This will be included in the
     * error message when shown.
     *
     * @see ExceptionSingleton for more information
     * 
     * @param msgId - the integer Identification number for the error message to be
     * associated with this exception.
     * @param message the additional message that will be added to the exception.
     */
    public WeatherException(int msgId, String message)
    {
        this(msgId);
        sAdditionalMessage = message;
    }

    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown and if this exception is fatal.
     *
     * It also takes the string parameter given and adds it to the
     * additional message attribute of the class.  This will be included in the
     * error message when shown.
     *
     * @param msgId the id of the message
     * @param oldE the old exception that was thrown
     * @param message the additional message to be added to the exception.
     */
    public WeatherException(int msgId, Throwable oldE, String message)
    {
        this(msgId, false, oldE);
        sAdditionalMessage = message;
    }

    /**
     * Creates a new instance of WeatherException with an integer representing
     * the error to be shown and if this exception is fatal. Passing an instance of
     * throwable is used for chaining exceptions.  The <code>msgId</code> is
     * checked to see if it is the <code>GEN_PROP_ERROR</code>.  If so, the
     * <code>GEN_PROP_ERROR_MESSAGE</code> is passed to the super constructor.
     * Otherwise the error message is retrieved from the WeatherException
     * Properties file.  This is the only constructor that checks for the
     * General Properties error, as it is the only one that will be used in
     * GeneralPropertiesSingleton.
     *
     * It also takes the string parameter given and adds it to the the
     * additional message attribute of the class.  This will be included in the
     * error message when shown.
     *
     * @param msgId the ID number of the message to be included in this exception
     * @param isFatal if this exception should be considered fatal.
     * @param oldE the original exception, used for chaining.
     * @param message the additional message to be added to the exception.
     */
    public WeatherException(int msgId, boolean isFatal, Throwable oldE,
            String message)
    {
        this(msgId, isFatal, oldE);
        sAdditionalMessage = message;
    }
    
    /**
     * Displays a message dialog box with error message and error number for
     * the exception.
     * 
     * If the error is fatal, this will terminate the program.
     */
    public void show() {
        show(sAdditionalMessage);
    }
    /**
     * Displays a message dialog box with error message and error number for
     * the exception in addition to the additional message passed.
     * Any additional message is placed right after the error statement.
     * Any required spaces or lines between the two are the requirement
     * of the invoking method.
     * If the error is fatal, this method will terminate the program.
     *
     * @param additionalMessage any extra message to be displayed.
     *
     */
    public void show(String additionalMessage) {
        String message = "";
        if(additionalMessage == null)
            additionalMessage = "";
        if(!additionalMessage.equals(sAdditionalMessage))
            additionalMessage += sAdditionalMessage;
        if(isFatal){
            message += "  THIS IS A FATAL EXCEPTION -- THE PROGRAM WILL CLOSE.\n";
        }
        if(additionalMessage != null)
             message =" (ERROR NUMBER: " +
                errorNumber + ") " + this.getMessage()
                + additionalMessage +
                "\n Stack Trace\n "+getStackTraceMessage(this);
        else
             message =" (ERROR NUMBER: " +
                errorNumber + ") " +getMessage() +
                "\n Stack Trace\n "+getStackTraceMessage(this);

        if(isFatal){
            message += "\n\nTHIS IS A FATAL EXCEPTION -- THE PROGRAM WILL NOW CLOSE.";
        }

        JOptionPane pane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = pane.createDialog("Weather Exception");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        ScreenInteractionUtility.positionWindow(dialog, true);
        dialog.setVisible(true);
        if(isFatal){
            System.exit(-1);
        }
    }

    /**
     * This method prints a stack trace for an exception.
     * If the trace is too long, the user is told that the
     * entire error can be found in the log file.
     * @param e the exception
     * @return the stack trace.
     */
    private String getStackTraceMessage(WeatherException e)
    {
        String sTraceMessage = "";
        StackTraceElement[] ste = e.getStackTrace();
        int traceLinesToShow = ste.length;
        String moreMessage = "";
        
        if(traceLinesToShow > 15){
            moreMessage = ". . .  "+(traceLinesToShow - 15) + " more -- Refer to " +
                    "WeatherError log file for complete stack trace.";
            traceLinesToShow = 15;
        }
        for(int i = 0; i < traceLinesToShow; i++)
            sTraceMessage += ste[i] + "\n";
        sTraceMessage += moreMessage;
        return sTraceMessage;
    }
    
    
    /**
     * Returns true if this exception was classified as a fatal exception during
     * creation, false otherwise.
     * 
     * @return true if this exception is fatal
     */
    public boolean isFatal(){
        return isFatal;
    }
    
    /**
     * Emails the current system administrator information about this exception.  
     * This should
     * only be used in critical system failure situations and only on server side 
     * implementations.  
     * 
     * @param additionalMessage any additional message to be emailed to the system administrator.
     * @throws weather.common.utilities.WeatherException
     */
    public void emailAdmin(String additionalMessage) throws WeatherException{
        
        Emailer.email(PropertyManager.getGeneralProperty("ADMIN_EMAIL"), 
                "Weather Project system failure: "+ additionalMessage +" "+ this.getMessage(), 
                "Weather Station Error Notice");
        
    }
    
    public static void main(String [] args){
       // try {
            //new WeatherException(-1).emailAdmin("I am a bad error.");
            new WeatherException(0012,"Database Error").show();
           try{
               new URL("...bad");
           }catch( MalformedURLException e2){
            String extra = "\nThe URL for resource with name "+
            "Bloom U Camera"+" is not formed correctly ";
              new WeatherException(3015,e2, extra).show();
               WeatherLogger.log(Level.SEVERE, "SQL Exception thrown while tying "
                  +"to close a ResultSet object. The connection object is not null ",e2);
           }
    //    } catch (WeatherException ex) {
    //        WeatherLogger.log(Level.SEVERE, null, ex);
    //    }
    }
}
