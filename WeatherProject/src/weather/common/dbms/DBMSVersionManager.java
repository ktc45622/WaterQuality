package weather.common.dbms;

import java.util.ArrayList;
import weather.common.data.version.Version;

/**
 * This interface allows new version data to be uploaded to the Internet.  This 
 * is done by inserting an instance of the <code>Version</code> class into a
 * database that is accessible to all copies of the program.  A method is also
 * specified to retrieve the most recent version data as a <code>Version</code>
 * object.
 * 
 * @see Version
 * @author Brian Bankes
 */
public interface DBMSVersionManager {

    /**
     * Inserts an <code>Version</code> object in the database.
     * @param version The <code>Version</code> object to insert.
     * @return True if the version data was successfully inserted; 
     * False otherwise.
     */
    public boolean insertVersion(Version version);

    /**
     * Retrieves the most recent version data stored in the database.
     * @return A <code>Version</code> object holding the most recent version 
     * data stored in the database. 
     */
    public Version getMostResentVersion();
    
    /**
     * Gets a sorted list of all versions currently in the database.
     * @return An <code>ArrayList</code> holding all instances of 
     * <code>Version</code> currently in the database sorted from oldest to 
     * newest. 
     */
    public ArrayList<Version> getAllVersions();
    
    /**
     * Updates the given <code>Version</code> in the database.  The notes field
     * is all that can be updated.
     * @param version The given <code>Version</code>.
     * @return True if the version data was successfully updated; 
     * False otherwise.
     */
    public boolean updateVersionNotes(Version version);
    
    /**
     * Gets the <code>Version</code> in the database that matches the given
     * <code>String</code> or null if none exists.
     * @param versionString The given <code>String</code>.
     * @return The <code>Version</code> in the database that matches the given
     * <code>String</code> or null if none exists.
     */
    public Version getVersionFromString(String versionString);
}