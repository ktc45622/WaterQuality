package weather.common.utilities;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to provide the rest of the application with a Logger
 * object to log exceptions.  Log files are named WeatherError[date][time].log.
 * Only one Logger object will exist for the entire program, and all exceptions
 * will be logged to the same file for any one instance of the program running.
 *
 * TODO: Decide how to deal with WeatherException thrown by initializeLogging
 * 
 * @author Bloomsburg University Software Engineering
 * @author Joe Sharp (2009)
 * @author Eric Subach (2010)
 * @version Spring 2010
 */
public class WeatherLogger {

    private static Logger m_logger;

    /**
     * Creates a new log file name, creates a new logger object, and
     * initializes a new FileHandler to handle the logger.  The Logger
     * object is saved in the static Logger object m_logger.
     *
     * @throws weather.common.utilities.WeatherException
     */
    private static void initializeLogging() throws WeatherException
    {
        try {
            String logFile = getLogfileName();
            m_logger = Logger.getLogger(logFile);
            Handler handler = new FileHandler(logFile);
            handler.setFormatter(new java.util.logging.SimpleFormatter());
            m_logger.setUseParentHandlers(false);
            m_logger.addHandler(handler);
        } catch (IOException ex) {
            throw new WeatherException(3020, ex);
        }
    }

    /**
     * Takes the given date and formats it into an easily readable String
     * representing this date. 
     * 
     * Date format: MM-dd-yyyy
     *
     * @param date The date we want to format.
     * @return An easily read, String representation of the given date.
     */
    private static String getFormattedDate(Date date)
    {
        DateFormat format;
        format = new SimpleDateFormat ("MM-dd-yyyy");

        return (format.format (date));
    }

    /**
     * Takes the given date, extracts the time out of it and creates a String
     * representation of this time that is easily read. 
     * 
     * Time format: HH.MM.AM/PM
     *
     * @param date The date from which we want to extract the time from.
     * @return A string representation of the time extracted from the given date
     * object.
     */
    private static String getFormattedTime(Date date)
    {
        DateFormat format;
        format = new SimpleDateFormat ("hh.mm.a");

        return (format.format (date));
    }

    /**
     * Uses the other utility methods in this class to create a date/time
     * string that is formatted in a way that is convenient for naming the
     * Weather Error file.
     * 
     * @param date The date we want to create string for.
     * @return A string that represents the data and time contained in the given
     * date.
     */
    private static String getDateTime(Date date)
    {
        String sDateTime = getFormattedDate(date) + "_" +
                           getFormattedTime(date);
        return sDateTime;
    }

    /**
     * Retrieves log file base name and extension from the GeneralProperties
     * file and uses the current system time to create a log file name for
     * use creating a logger object for logging exceptions.
     *
     * @return A string log file name that will be unique to the minute that
     * this method was called.
     */
    private static String getLogfileName()
    {
        String logFile = PropertyManager.getGeneralProperty("LogFileBase");
        Date date = new Date(System.currentTimeMillis());
        String sFormattedDateTime = getDateTime(date);
        logFile += "." + sFormattedDateTime;
        logFile += PropertyManager.getGeneralProperty("LogFileExt");
        return logFile;
    }

    /**
     * Uses the given log file base name and extension and the current system
     * time to create a log file name used for creating a logger object for
     * logging exceptions. 
     * 
     * NOTE Use only in instances where the properties files are not available
     *       and a manually created logger needs to be used.
     * 
     * @param logFileBase Base name for the log file.
     * @param logFileExt Extension for the log file.
     * @return A string log file name that will be unique to the minute that
     * this method was called.
     */
    public static String getNewLogFileName(String logFileBase, String logFileExt)
    {
        String logFile = logFileBase;
        Date date = new Date(System.currentTimeMillis());
        String sFormattedDateTime = getDateTime(date);
        logFile += "." + sFormattedDateTime;
        logFile += logFileExt;
        return logFile;
    }

    /**
     * Log message and exception with timestamp at the given standard log level.
     *
     * @param level The Level of the message.
     * @param message The message to log with the exception.
     * @param ex The exception that is being logged.
     */
    public static void log(Level level, String message, Throwable ex)
    {
        if(m_logger == null)
            initLogger();
        m_logger.log(level, message, ex);
    }

    /**
     * Log message with timestamp at the given standard log level.
     *
     * @param level The Level of the log message.
     * @param message The message to log.
     */
    public static void log(Level level, String message){
        if(m_logger == null){
            initLogger();
        }
        m_logger.log(level, message);
    }

    /**
     * Initializes logging. If initialization fails, a default logger is
     * returned that will log messages to console output.
     */
    private static void initLogger()
    {
        try{
            initializeLogging();
        }catch(WeatherException ex)
        {
            m_logger = Logger.getLogger(getLogfileName());
        }
    }

}
