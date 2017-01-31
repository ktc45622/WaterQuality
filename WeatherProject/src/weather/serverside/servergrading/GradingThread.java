package weather.serverside.servergrading;

import java.util.TimerTask;
import weather.common.data.forecasterlesson.ForecasterLessonGrader;
import weather.common.dbms.DBMSSystemManager;
import weather.common.utilities.Debug;

/**
 * This class is the <code>TimerTask</code> for the server-side grading program.
 * 
 * @author Brian Bankes
 */
public class GradingThread extends TimerTask {
    
    private DBMSSystemManager dbms;
    
    /**
     * Constructor.
     * 
     * @param dbms The program's <code>DBMSSystemManager</code>.
     */
    public GradingThread (DBMSSystemManager dbms) {
        this.dbms = dbms;
    }

    @Override
    public void run() {
        Debug.println("Running Grader Task...");
        ForecasterLessonGrader.tryToGradeAllAttempts(dbms);
    }
}