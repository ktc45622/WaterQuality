package weather.common.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formats log messages.
 *
 * Format is:
 * LEVEL | DATE TIME | CLASS | MESSAGE
 *
 * If there is throwable information, the message will span multiple lines and
 * show a stack trace.
 *
 * @author Eric Subach (2010)
 */
public class WeatherLogFormatter extends Formatter {
    // Info before the appended message.
    String preMessage;
    // Formatted message to be logged.
    String formattedMessage;
    // Message component of log record.
    String message;
    // Log level.
    String level;
    // Class that called log method.
    String msgClass;
    
    int threadID;
    Throwable thrown;
    String detailedMsg;
    StringBuffer sb;
    StringTokenizer tokenizer;

    Date date;
    DateFormat format;

    int i, length;


    public WeatherLogFormatter () {
        format = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss a");
    }


    /**
     * Pad a string on the right hand side with white space.
     *
     * @param str string to pad
     * @param length length of space taken (including word and padding)
     * @return padded string
     */
    private static String padRight (String str, int length) {
        return (String.format ("%1$-" + length + "s", str));
    }


    @Override
    public String format (LogRecord record) {
        message = record.getMessage ();
        level = record.getLevel ().toString ();
        date = new Date (record.getMillis ());

        msgClass = record.getSourceClassName ();
        // Don't need fully qualified name.
        //msgClass = msgClass.substring (msgClass.lastIndexOf (".") + 1);

        threadID = record.getThreadID ();

        // Log level, date, time
        preMessage = padRight (level, 6) + " | " + format.format (date) + /*" | " + msgClass +*/ " | ";
        // Short description of error.
        formattedMessage = preMessage + msgClass + ": " + message + "\n";

        thrown = record.getThrown ();


        // If there is a throwable object, print the info and stack trace.
        if (thrown != null) {
            addStackTrace ();
        }


        return (formattedMessage);
    }

    
    private void addStackTrace () {
        // Add a line to visually seperate stack traces.
        formattedMessage = preMessage + "\n" + formattedMessage;

        detailedMsg = thrown.getLocalizedMessage ();

        // Show detailed message if supplied.
        if (detailedMsg != null) {
            formattedMessage += preMessage + "Detailed Message: " + detailedMsg + "\n";
        }

        // Add stack trace to string buffer.
        try {
            sb = new StringBuffer ();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            thrown.printStackTrace(pw);
            pw.close();
            sb.append(sw.toString());

            tokenizer = new StringTokenizer (sb.toString (), "\n");

            // Add each line of the trace to the message.
            while (tokenizer.hasMoreTokens ()) {
                formattedMessage += preMessage + tokenizer.nextToken () + "\n";
            }

            formattedMessage += "\n";
        }
        catch (Exception e) {
            Debug.println ("EXCEPTION in WeatherLogFormatter");
            e.printStackTrace ();
        }
    }
}
