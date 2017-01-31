package weather.common.data.weatherstation;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.util.*;
import java.util.logging.Level;
import weather.clientside.utilities.StorageSpaceTester;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.data.resource.WeatherResourceType;
import weather.common.utilities.Debug;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This is the base class class for <code>WeatherUndergroundInstance</code>, the
 * class that parses the data the weather station collects.  Methods should only
 * be called from instances of <code>WeatherUndergroundInstance</code>.
 *
 * @author Bloomsburg University Software Engineering
 * @author Matthew Rhoades (2007)
 * @author Jacob Kelly (2007)
 * @author Steve Rhein (2008)
 * @author Rich Stepanski (2008)
 * @author Xianrui Meng (2010)
 * @version Spring 2010
 *
 */
public class WeatherStationInstance extends ResourceInstance {

    private static final long serialVersionUID = 1L;
    /**
     * Vector of WeatherStationVariable to hold variables from a parsed weather
     * files. May be empty if the data has not yet been parsed.
     */
    protected Vector<WeatherStationVariable> variables;
    /**
     * weatherStationValues will hold the data in compressed form, all the
     * times.
     */
    protected StringBuffer weatherStationValues;
    //whether or not the stringbuffer is compressed.
    protected boolean compressed;
    //This is used append to the year String, e.g. 01/03/10  10 means the year 
    //2010, we append the 20 to the front of 10.
    protected static final String TWENTY = "20";
    protected static final String SLASH = "/";
    /**
     * There could be many terms to delimit the tokens in the text, such '\r\n',
     * ';', ',', '|'. Future changes may be add this to the inputFormatFile
     */
    protected static final String CRLF = "\r\n";
    /**
     * WeatherStationVariable represents a weather variable and its variables per minutes,
     *  and it could represent a column of variables for the time.
     */
    protected WeatherStationVariable timeValues;

    
    /**
     * Creates a WeatherStationInstance object with an empty vector of variables.
     */
    public WeatherStationInstance() {
        super();
        super.setResourceType(WeatherResourceType.WeatherStationValues);
        variables = new Vector<>();
        weatherStationValues = new StringBuffer();
        timeValues = null;
        compressed = true;
    }

    /**
     * Construct instance with given Resource.
     * @param resource Used to construct ResourceInstance.
     */
    public WeatherStationInstance(Resource resource) {
        super(resource);
        super.setTime(new Date(System.currentTimeMillis()));
        super.setResourceType(WeatherResourceType.WeatherStationValues);
        variables = new Vector<>();
        weatherStationValues = new StringBuffer();
        timeValues = null;
        compressed = true;
    }
    
    /**
     * Returns Vector of <code>WeatherStationData</code> held internally.
     *
     * @return The vector of all WeatherStationData variables.
     */
    public Vector<WeatherStationVariable> getValues() {
        return new Vector<>(variables);
    }

    /**
     * Builds our Vector of WeatherStationData for this instance.  The code is 
     * in <code>WeatherUndergroundInstance</code>.
     *
     * @throws WeatherException <ul>
     *  <li>if the properties file does not exist or for some other reason
     * cannot be opened for reading. </li>
     *  <li>if an error occurred when reading from the properties
     * file input stream.</li>
     *  <li>if the passed String does not match expected
     * log format.</li>
     */
    public void createValuesVector() throws WeatherException {
    }

    /**
     * Helper method used by readFile and readURL to read a
     * <b>text</b> file. Clears the string buffer, and then writes variables to
     * it from the buffered reader.
     *
     * @param br The buffered reader connected to data file.
     * @throws java.io.IOException, weather.common.utilities.WeatherException.
     */
    private void readStream(BufferedReader br) throws IOException, WeatherException {
        this.weatherStationValues.delete(0, this.weatherStationValues.length());
        variables.clear();
        timeValues = null;
        String buffer;
        while ((buffer = br.readLine()) != null) {
            weatherStationValues.append(buffer).append(CRLF);
        }
        compressed = true;
    }

    /**
     * Gets the variable for the given WeatherStationDataType from the WeatherStationVariable
     * vector stored in this class.
     * @param key The WeatherStationDataType for this class.
     * @return The WeatherStationVariable for the given key.
     */
    protected WeatherStationVariable getVariableForKey(String key) {
        for (WeatherStationVariable variable : variables) {
            if (variable.getVariableKey().equalsIgnoreCase(key)) {
                return variable;
            }
        }
        return new WeatherStationVariable(key);
    }

    /**
     * Reads a <b>text</b> file or a <b>zip</b> file
     * containing weather station data. The file extension determines how
     * we try to read a file.
     * @param file The weather station file to read.
     * @throws weather.common.utilities.WeatherException - An error occurs when
     * reading from the file.
     */
    @Override
    public void readFile(File file) throws WeatherException {
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        try {
            fstream = new FileInputStream(file);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            readStream(br);
            in.close();
        } catch (FileNotFoundException fnfe) {
            WeatherLogger.log(Level.SEVERE, "Can't find weather station file "
                    + file + "\n", fnfe);
            throw new WeatherException(3004, fnfe,
                    "Can't find weather station file " + file + "\n");
        } catch (IOException ioe) {
            WeatherLogger.log(Level.SEVERE, "Can't read weather station file "
                    + file + "\n", ioe);
            throw new WeatherException(3005, ioe,
                    "Can't read weather station file " + file + "\n");
        }
    }

    /**
     * Read a weather data <b>text</b> file specified by a URL.
     * @param url The location of the text file.
     * @throws weather.common.utilities.WeatherException
     */
    @Override
    public void readURL(URL url) throws WeatherException {
        BufferedReader br = null;
        try {
            //Will always be a text file
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            readStream(br);
            br.close();
        } catch (IOException e) {
            throw new WeatherException(3006, e);
        }
    }

    /**
     * Writes the stored string to the specified file.
     * @param file The file in which to write the stored log.
     * @throws weather.common.utilities.WeatherException - An error occurred
     * when writing to the file.
     */
    @Override
    public void writeFile(File file) throws WeatherException {
        try {       
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(weatherStationValues.toString());
            out.close();
        } catch (IOException e) {
            throw new WeatherException(3008, e);
        }
    }
    
    /**
     * Writes the stored string to the specified file with the html "break"
     * tags removed.
     * @param file The file in which to write the stored log.
     * @throws weather.common.utilities.WeatherException - An error occurred
     * when writing to the file.
     */
    private void writeCleanFile(File file) throws WeatherException {
        try {
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(weatherStationValues.toString()
                    .replaceAll("<br>", ""));
            out.close();
        } catch (IOException e) {
            throw new WeatherException(3008, e);
        }
    }

    /**
     * Writes the stored stringBuffer into a zip file.  This method is not yet
     * implemented.
     * @param zipFile The zip file to write to.
     * @throws WeatherException if there's an error writing the compressed
     * string to a file.
     */
    public void writeZipFile(File zipFile) throws WeatherException {
    }

    /**
     * Writes the stored  string buffer into a text file. This method is not yet
     * implemented.
     * @param textFile The text file to write to.
     * @throws WeatherException If there's an error uncompressing a string or
     * writing the compressed string to a file.
     */
    public void writeTextFile(File textFile) throws WeatherException {
    }

    /**
     * Reads a compressed (zip) file into a  string buffer. This method is not
     * yet implemented.
     * Since the zip file is already compressed, no decompression is needed.
     * @param zipFile The zip file to read.
     * @throws WeatherException If there's an error reading from the file into
     * the string buffer.
     */
    public void readZipFile(File zipFile) throws WeatherException {
    }

    /**
     * Retrieves a vector of variables from a specified range of resources. The
     * code is in <code>WeatherUndergroundInstance</code>.  This stub returns
     * null.
     * 
     * @param range The range of resources to retrieve variables from.
     * @return A vector of variables from a resource range.
     */
    public Vector<WeatherStationVariable> 
            getValuesForResourceRange(ResourceRange range) {
        return null;
    }

    /**
     * Returns true or false depending if the StringBuffer is compressed.
     * @return True if compressed, false otherwise.
     */
    public boolean isCompressed() {
        return compressed;
    }

     /**
     * Write the weather station text file to the local hard drive. NOTE: This
     * method may only be called from the client side after being downloaded
     * from the server. Also deletes outdated copies of the station.
     *
     * @param zone The <code>TimeZone</code> of the <code>Resource</code>
     * providing the data for this object.
     * @param extension The extension to be given to the file name including the
     * dot.
     * @return The <code>File</code> just written to disk.
     */
    public File writeToLocalDisk(TimeZone zone, String extension) {
        String localFileName = getLocalSerializedFileName(zone) + extension;
        Debug.println("Preparing to write: " + localFileName);

        //Get data to compare.
        String newFileResource = this.getResourceNumber(localFileName);
        String newFileTime = this.getStartTime(localFileName);

        File weatherStationDataDirectory = new File(CommonLocalFileManager
                .getWeatherStationsDirectory());

        //Record if directory if pressent.
        boolean doesStationDirectoryExist = weatherStationDataDirectory.exists();

        File newFile = new File(CommonLocalFileManager
                .getWeatherStationsDirectory() + File.separator
                + localFileName);

        //If the target directory exist, we must look for an older version of the
        //day's data and delete it if we find it.
        if (doesStationDirectoryExist) {
            String testList[] = weatherStationDataDirectory.list();//Get the list of files in the weather station directory 
            for (int i = 0; i < testList.length; i++) {
                Debug.println("Testing file: " + testList[i]);
                //If there is no comma in the file name, it's not a match.
                if (testList[i].indexOf(',') == -1) {
                    continue;
                }
                File testFile = new File(CommonLocalFileManager
                        .getWeatherStationsDirectory()+ File.separator 
                        + testList[i]);

                //Get data to compare.
                String testFileResource = this.getResourceNumber(testList[i]);
                String testFileTime = this.getStartTime(testList[i]);

                //Test for matching older file.
                if (testFileResource.equals(newFileResource)
                        && testFileTime.equals(newFileTime)) {
                    Debug.println("File found to delete.");
                    if (testFile.delete()) {
                        Debug.println("File deleted.");
                    } else {
                        Debug.println("File not deleted.");
                    }
                } else {
                    Debug.println("File not a match to delete.");
                }
            }
        }
        
        //Test for space to write file.
        StorageSpaceTester.testApplicationHome();

        //Write the file to the disk.    
        try {
            writeFile(newFile);
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Could not write the  file to the local disk. The file name was "
                    + newFile.getAbsolutePath(), ex);
            Debug.println("Could not write the  file to the local disk. The file name was "
                    + newFile.getAbsolutePath());
        }

        //Repeat for "clean" file.
        String subDirectory = "CleanCSVFiles";
        Debug.println("Writing clean file...");

        //Use the content to create the subdirectory.
        File fullPathWithSub = new File(CommonLocalFileManager
                .getWeatherStationsDirectory()
                + File.separator + subDirectory);
        fullPathWithSub.mkdirs();

        File newCleanFile = new File(fullPathWithSub + File.separator + localFileName);

        //Look for match in clean subdirectory
        String testList[] = fullPathWithSub.list();
        for (int i = 0; i < testList.length; i++) {
            Debug.println("Testing clean file: " + testList[i]);
            //If there is no comma in the file name, it's not a match.
            if (testList[i].indexOf(',') == -1) {
                continue;
            }
            File testFile = new File(fullPathWithSub
                    + File.separator + testList[i]);

            //Get data to compare.
            String testFileResource = this.getResourceNumber(testList[i]);
            String testFileTime = this.getStartTime(testList[i]);

            //Test for matching older file.
            if (testFileResource.equals(newFileResource)
                    && testFileTime.equals(newFileTime)) {
                Debug.println("File found to delete.");
                if (testFile.delete()) {
                    Debug.println("File deleted.");
                } else {
                    Debug.println("File not deleted.");
                }
            } else {
                Debug.println("File not a match to delete.");
            }
        }

        //Write the file to the disk.    
        try {
            writeCleanFile(newCleanFile);
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Could not write the  file to the local disk. The file name was "
                    + newFile.getAbsolutePath(), ex);
            Debug.println("Could not write the  file to the local disk. The file name was "
                    + newFile.getAbsolutePath());
        }

        //Return mew file.
        return newFile;
    }

    /**
     * Returns the serialized file name for writing this weather station data
     * file to the local hard drive. This contains only the file name and not
     * the entire path.
     *
     * @param zone The <code>TimeZone</code> of the <code>Resource</code>
     * providing the data for this object.
     * @return The serialized file name for writing this weather station data
     * file to the local hard drive.
     */
    public String getLocalSerializedFileName(TimeZone zone) {
        StringBuilder filename = new StringBuilder();
        String delimiter = ",";
        long currentTime = getTime().getTime();
        
        //Get the storage from the server and add this name to the first position of the file name
        filename.append(getStorageFileName());
        filename.append(delimiter);
        
        //Get the resource number and add it to the 2nd position of the file name
        filename.append(getResourceNumber());
        filename.append(delimiter);
        
        //get the current and add the time in millisecond to the flie name
        filename.append(currentTime);
        filename.append(delimiter);
        
        //Set the start time of different files download on the same day to the same
        //Create an instance of the calendar in the resource time zone
        Calendar midNight = Calendar.getInstance();
        midNight.setTimeZone(zone);
        //Set the date to today
        midNight.setTimeInMillis(getResourceRange().getStartTime().getTime());
        //set the hour of the day to 0--00 a.m.
        midNight.set(Calendar.HOUR_OF_DAY, 0);
        //set minute to 0
        midNight.set(Calendar.MINUTE, 0);
        //set second to 0 
        midNight.set(Calendar.SECOND, 0);
        //set millisecond to 0
        midNight.set(Calendar.MILLISECOND, 0);
        //set the millisecond to the 4th part of the file name
        filename.append(midNight.getTimeInMillis());
        filename.append(delimiter);
        
        //Compute endTime
        Calendar endTime = (Calendar) midNight.clone();
        //find end od day
        endTime.add(Calendar.DATE, 1);
        endTime.add(Calendar.MILLISECOND, -1);
        //change end time to current time if the day hasn't ended yet.
        if(currentTime < endTime.getTimeInMillis()) {
            endTime.setTimeInMillis(currentTime);
        }
        filename.append(endTime.getTimeInMillis()); 
        
        //Return filename
        return filename.toString();
    }
   
    /**
     * This method is used to get the start time in a file name,
     * when two files are download on the same date, so delete the old file
     * and save the new file to the local disk. This method is used to get the
     * starttime in the filename, the starttime in one file name is the 4th part 
     * of the name.
     * @param fileName The file name being checked.
     * @return a string that shows the millisecond of the start time
     */
    private String getStartTime(String fileName) 
    {
        //get the index of the 3rd comma in the given file name
        int indexOfHeadComma = nthIndexOfComma(fileName, ',', 3);
        //get the index of the 4th comma in the given file name
        int indexOfEndComma = nthIndexOfComma(fileName, ',', 4);
        
        //use substring to get the string between the 3rd comma and 4th comma
        return fileName.substring(indexOfHeadComma + 1, indexOfEndComma).trim();
    }
    
    /**
     * This method is used to get the resource number in a file name, when two 
     * files are download on the same date, so delete the old file and save the 
     * new file to the local disk. This method is used to get the resource 
     * number in the filename, the resource number in one file name is the 2nd
     * part of the name.
     *
     * @param fileName The file name being checked.
     * @return a string that shows the resource number
     */
    private String getResourceNumber(String fileName) {
        //get the index of the 1st comma in the given file name
        int indexOfHeadComma = nthIndexOfComma(fileName, ',', 1);
        //get the index of the 2nd comma in the given file name
        int indexOfEndComma = nthIndexOfComma(fileName, ',', 2);

        //use substring to get the string between the 1st comma and 2nd comma
        return fileName.substring(indexOfHeadComma + 1, indexOfEndComma).trim();
    }

    /**
     * This method is used to get the nth index of the given character in 
     * one string
     * @param text is the string we need to look for
     * @param needle is the character we need to find
     * @param n nth given character
     * @return the index of the nth character we need to find
     */
    private int nthIndexOfComma(String text, char needle, int n)
    {    
        //read the given string from the head to the end
        for (int i = 0; i < text.length(); i++)
        {
            //find the index of the nth character
            if (text.charAt(i) == needle)
            {
                n--;
                if (n == 0)
                {
                    return i;
                }
            }
        }
        //return -1 if the given character can not be find
        return -1;
    }
    
    /**
     * Set the fields from the current filename of this instance.
     */
    public void setFieldsFromFilename(File file) {
        StringTokenizer tokenizer = new StringTokenizer(file.getName());

        setStorageFileName(tokenizer.nextToken(","));
        setResourceNumber(Integer.parseInt(tokenizer.nextToken(",")));
        setTime(new Date((Long.parseLong(tokenizer.nextToken(",")))));
        setStartTime(Long.parseLong(tokenizer.nextToken(",")));
        setEndTime(Long.parseLong(tokenizer.nextToken(",.")));
    }
}

