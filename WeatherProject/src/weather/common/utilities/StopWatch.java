package weather.common.utilities;

import java.text.*;

/**
 * Calculates execution time of sections of code.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Mike Graboske (2008)
 * @version Spring 2008
 */
public class StopWatch {
    /**
     *The time in milliseconds since start() is called when stop() is called.
     */  
    private long elapsedTime;
    /**
     *The time in milliseconds on the system clock when start() is called. 
     */
    private long startTime;
    /**
     * True if  start() was called, false otherwise.
     */ 
    private boolean isRunning;
    
    /**
     * Default constructor. 
     */ 
    public StopWatch() {
        reset();
    }
    
    /**
     * Starts and allows stop() to be called.
     */
    public void start(){
        if(isRunning) return;
        isRunning = true;
        startTime = System.currentTimeMillis();
    }
    /**
     * Stops and saves the time since start() was called.
     */
    public void stop(){
        if(!isRunning) return;
        isRunning = false;
        long endTime=System.currentTimeMillis();
        elapsedTime = endTime-startTime;
    }
    /**
     * Sets the stored time to zero.
     */
    public void reset(){
        this.elapsedTime=0L;
        isRunning = false;
    }
    /**
     * Returns the time stored if stop has been called or the time since start 
     * was called.
     * @return  the elapsed time in milliseconds
     */

    public long getElapsedTime(){
        if(!isRunning)return elapsedTime;
        return System.currentTimeMillis()-startTime;
    }
    /**
     * Returns the elapsed time in a minutes:seconds:milliseconds
     * format.
     * @return String representation of the elapsed time
     */
    @Override
    public String toString(){
        java.util.Date date = new java.util.Date(getElapsedTime());
        java.text.DateFormat df=new SimpleDateFormat("mm:ss:SSSS");
        return df.format(date);
    }
}
