package weather.serverside.servergrading;

import java.util.Calendar;
import java.util.Timer;
import weather.common.data.forecasterlesson.ForecasterLessonGrader;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;



/**
 * A program to handle server-side grading of all ungraded student attempts.
 * Grading is to happen once on start-up and once every day at 9:00 local time.
 * This code based on an example from 
 * http://archive.oreilly.com/pub/a/java/archive/quartz.html
 * 
 * @author Brian Bankes
 */
public class ServerGradingProgram {
    
    public static void main(String[] args) {
        DBMSSystemManager dbms = null;
        //Setup access to dbms.  
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        
        //Do initial grading.
        Debug.println("Initial Grading on Program Start...");
        ForecasterLessonGrader.tryToGradeAllAttempts(dbms);
        
        //Schedule daily grading.
        GradingThread thread = new GradingThread(dbms);
        Timer timer = new Timer();
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 9);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        if (date.getTimeInMillis() < System.currentTimeMillis()) {
            //Already after 9:00 am; start tomorrow.
            date.add(Calendar.DATE, 1);
        }
        Debug.println("First Thread Time: " + CalendarFormatter.format(date));
        timer.scheduleAtFixedRate(thread, date.getTime(), ResourceTimeManager
                .MILLISECONDS_PER_DAY);
    }
    
}
