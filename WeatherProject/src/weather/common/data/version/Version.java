package weather.common.data.version;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import weather.common.utilities.Debug;

/**
 * A class to hold a compare version numbers defined as three integers separated
 * by dots.
 * 
 * @author Brian Bankes
 */
public class Version implements Comparable {
    
    private final int majorVersion;
    private final int minorVersion;
    private final int minorRelease;
    private String versionNotes;
    private Date releaseDate;
    
    /**
     * Constructor.
     * @param majorVersion The major version number.
     * @param minorVersion The minor version number.
     * @param minorRelease The minor release number.
     */
    public Version (int majorVersion, int minorVersion, int minorRelease) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.minorRelease = minorRelease;
        this.versionNotes = null;
        this.releaseDate = null;
    }
    
    /**
     * Sets the version notes.
     * @param versionNotes The new version notes.
     */
    public void setVersionNotes(String versionNotes) {
        this.versionNotes = versionNotes;
    }
    
    /**
     * Sets the release date.
     * @param releaseDate 
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    /**
     * Gets the version notes.
     * @return The version notes if they are not null or "No information 
     * available." if they are null;
     */
    public String getVersionNotes() {
        if (this.versionNotes == null) {
            return "No information available.";
        } else {
            return this.versionNotes;
        }
    }
    
    /**
     * Gets the release date.
     * @return The release date which can be null.
     */
    public Date getReleaseDate() {
        return this.releaseDate;
    }
    
    /**
     * Gets the major version number.
     * @return The major version number.
     */
    public int getMajorVersion() {
        return this.majorVersion;
    }
    
    /**
     * Gets the minor version number.
     * @return The minor version number.
     */
    public int getMinorVersion() {
        return this.minorVersion;
    }
    
    /**
     * Gets the minor release number.
     * @return The minor release number.
     */
    public int getMinorRelease() {
        return this.minorRelease;
    }
    
    /**
     * Tests if this object is enough newer than the function's argument to 
     * force an update of any program instance that is the argument's version.
     * @param other The <code>Version</code> to be compared.
     * @return True if this version is at least one minor version number newer
     * than the argument; False otherwise.
     */
    public boolean isNewerVersionThan(Version other) {
        if (this.majorVersion > other.majorVersion) {
            return true;
        }
        if (this.majorVersion == other.majorVersion
                && this.minorVersion > other.minorVersion) {
            return true;
        }
        return false;
    }

    /**
     * Perform a full comparison of this object and the function's argument 
     * (assumed to be of type <code>Version</code>) based on the three parts of
     * the version number.
     * @param o The <code>Version</code> to be compared.
     * @return A negative value if the version number of this object is less
     * than that of the argument; A positive value if the version number of this
     * object is greater than that of the argument; zero otherwise.
     */
    @Override
    public int compareTo(Object o) {
        Version compareVersion = (Version) o;
        return (10000 * this.majorVersion + 100 * this.minorVersion 
                + this.minorRelease) - (10000 * compareVersion.majorVersion 
                + 100 * compareVersion.minorVersion 
                + compareVersion.minorRelease); 
    }
    
    /**
     * A test for equality based only on object type and the three parts of the 
     * version number.
     * @param obj The <code>Object</code> being compared.
     * @return True if the argument is of type <code>Version</code> and the 
     * three parts of the version number are all the same; False otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return compareTo(obj) == 0;
    }
    
    /**
     * Returns the hash code.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.majorVersion;
        hash = 53 * hash + this.minorVersion;
        hash = 53 * hash + this.minorRelease;
        return hash;
    }
    
    /**
     * Returns the full version number as a <code>String</code>.
     * @return The full version number as a <code>String</code>.
     */
    @Override
    public String toString() {
        return "" + this.majorVersion + "." + this.minorVersion + "." 
                + this.minorRelease;
    }
    
    /**
     * Parses an input <code>String</code> into a <code>Version</code>; Returns
     * null if the parse fails.
     * @param input The <code>String</code> to parse, which must be of the form
     * number.number.number where each number is one or two digits.
     * @return The referenced <code>Version</code> or null if the parse fails.
     * NOTE: The <code>Version</code> will have "No information available." for
     * its notes.
     */
    public static Version parseVersion(String input) {
        StringTokenizer st = new StringTokenizer(input, ".");
        ArrayList<Integer> tokensAsInts = new ArrayList<>();
        
        //Break into sections berween dots and ensure each one is an integer in
        //the range [0, 99].
        while (st.hasMoreTokens()) {
            //A thrown exception means the token is invalid, so return null.
            try {
                int tokenValue = Integer.parseInt(st.nextToken());
                if (tokenValue < 0 || tokenValue > 99) {
                    throw new Exception();
                }
                //With no exception, store token in list.
                tokensAsInts.add(tokenValue);
            } catch (Exception ex) {
                return null;
            }
        }
        
        //In order to have a valid input, there must be three token.
        if (tokensAsInts.size() != 3) {
            return null;
        }
        
        //The input is valid, so make and return the version.
        return new Version(tokensAsInts.get(0).intValue(),
                tokensAsInts.get(1).intValue(), tokensAsInts.get(2).intValue());
    }
    
    //For testing.
    public static void main(String[] args) {
        //construct test data.
        Version[] versions = new Version[5];
        versions[0] = new Version(1, 0, 0);
        versions[0].setVersionNotes("Version 0");
        versions[1] = new Version(1, 1, 0);
        versions[1].setVersionNotes("Version 1");
        versions[2] = new Version(1, 1, 1);
        versions[2].setVersionNotes("Version 2");
        versions[3] = new Version(2, 0, 0);
        versions[3].setVersionNotes("Version 3");
        versions[4] = new Version(2, 0, 0); //No notes for testing purposes.
        
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Debug.println("Comparing " + versions[i].toString() + " ("
                        + versions[i].getVersionNotes() + ") to " + versions[j]
                        .toString() + " (" + versions[j].getVersionNotes() 
                        + ")");
                
                if (versions[i].compareTo(versions[j]) < 0) {
                    Debug.println("compareTo: LESS THAN");
                }
                if (versions[i].compareTo(versions[j]) == 0) {
                    Debug.println("compareTo: EQUAL");
                }
                if (versions[i].compareTo(versions[j]) > 0) {
                    Debug.println("compareTo: GREATER THAN");
                }
                
                if (versions[i].equals(versions[j])) {
                    Debug.println("equals: TRUE");
                } else {
                    Debug.println("equals: FALSE");
                }
                
                if (versions[i].isNewerVersionThan(versions[j])) {
                    Debug.println("isNewerVersionThan: TRUE");
                } else {
                    Debug.println("isNewerVersionThan: FALSE");
                }
            }
        }
        
        //Rnndomly fill, sort, and prirnt an ArrayList.
        ArrayList<Version> versionList = new ArrayList<>();
        versionList.add(versions[3]);
        versionList.add(versions[1]);
        versionList.add(versions[0]);
        versionList.add(versions[4]);
        versionList.add(versions[2]);
        
        Collections.sort(versionList);
        
        Debug.println("Sorted List:");
        for (Version version : versionList) {
            Debug.println(version);
        }
        
        //Test parsing method.
        Debug.println("Check Parsing:");
        String[] testStrings = {"99.99.99", "0.0.0", "-1.0.0", "0.0.100",
                                "a.0.0", "0.,.0", "1.2", "1.2.3.4"};
        
        for (String string : testStrings) {
            Version parsedVersion = Version.parseVersion(string);
            if (parsedVersion == null) {
                Debug.println("Parse: " + string + " Result: NULL");
            } else {
                Debug.println("Parse: " + string + " Result: " + parsedVersion
                        .toString());
            }
        }
    }
}
