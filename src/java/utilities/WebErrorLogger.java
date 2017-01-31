package utilities;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import javax.servlet.ServletContext;

/**
 *  
 * 
 * @author cjones
 */
public class WebErrorLogger {
    
    static ServletContext servletContext = null;
    public static void initialize(ServletContext sc) {
        servletContext = sc;
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
    private static String getFormattedDate(Date date) {
        
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
    private static String getFormattedTime(Date date) {
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
    private static String getDateTime(Date date) {
        String sDateTime = getFormattedDate(date) + "-" +
                           getFormattedTime(date);
        return sDateTime;
    }

   
    
   
    /**
     * Log message and exception with timestamp at the given standard log level.
     *
     * @param level The Level of the message.
     * @param message The message to log with the exception.
     * @param ex The exception that is being logged.
     */
    public static void log(Level level, String message, Throwable ex) {
        if(servletContext == null){
            return;
        }    
        servletContext.log(message, ex);
    }

    /**
     * Log message with timestamp at the given standard log level.
     *
     * @param level The Level of the log message.
     * @param message The message to log.
     */
    public static void log(Level level, String message){
        if(servletContext == null){
            return;
        }
        servletContext.log(message);
    }

    
}
