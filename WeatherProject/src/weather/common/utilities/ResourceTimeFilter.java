package weather.common.utilities;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Determines whether a file's timestamp is within a given hour.
 * It assumes the correct day.
 *
 * @author Eric Subach (2010)
 */
public class ResourceTimeFilter implements FilenameFilter {
    int hour;


    /**
     * Filter based on timestamp.
     *
     * @param hour The hour to be match on a 24-hour clock.
     */
    public ResourceTimeFilter (int hour) {
        this.hour = hour;
    }


    /**
     * Accept function of filter.
     * @param directory not used; use <code>name</code> instead
     * @param name full path of file
     * @return true if timestamp is within range
     */
    @Override
    public boolean accept (File directory, String name) {
        //Note - -update toString as this method is changed. 
        //Find hour in file name.
        int startIndex = name.lastIndexOf("-") + 1;
        String hourString = name.substring(startIndex, startIndex + 2); // hour is two digits.
        
        //Parse hour.
        try {
            return Integer.parseInt(hourString) == hour;
        } catch (NumberFormatException ex) {
            return false;
        }  
    }
    @Override
    public String toString(){
       
        return "" +hour;
    }
}
