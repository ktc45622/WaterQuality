package weather.common.data.weatherstation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.util.*;
import java.util.logging.Level;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import static weather.common.data.weatherstation.WeatherStationInstance.CRLF;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class extends WeatherStationInstance to implement usage of output
 * from the Weather Underground websites. This will be compatible for use
 * with any weather station having a URL of the form
 * <br><code>
 * http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=[ WEATHER UNDERGROUND ID ]&format=1
 * </br></code>
 * where <code>[ WEATHER UNDERGROUND ID ]</code> is any valid Weather Underground ID such as
 * KAZCASAG1. That station was used in testing. The only discrepancy between
 * these weather stations is whether they have the solar property or not. This
 * is why there are two properties files. One file is WUnderground.properties
 * and the other is WUnderground-nosolar.properties. The data contained in these
 * files may be moved to a database but this may also be unnecessary because
 * they will be constant.
 * 
 * @author Wayne Nilsen
 * @version Spring 2011
 */
public class WeatherUndergroundInstance extends WeatherStationInstance {
    private static String COMMA_ONLY = ",";
    private static String NO_SOLAR_PROP_LOC = "wunder_nosolar";
    private static String SOLAR_PROP_LOC = "wunder";

    /**
     * This constructor creates a WeatherUndergroundInstance object with an
     * empty vector of timesInVar.
     */
    public WeatherUndergroundInstance() {
        super();
    }

    /**
     * Construct instance with given Resource.
     * @param resource Used to construct ResourceInstance.
     */
    public WeatherUndergroundInstance(Resource resource) {
        super(resource);
    }

    /**
     * This method is used to determine if a weather station is compatible with
     * the Weather Underground parser. This is only guaranteed when the url of
     * the resource is of this form:<br>
     * http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=[ WEATHER UNDERGROUND ID ]&format=1
     * <br>
     * as mentioned in the constructor.
     * @param resource The resource to check.
     * @return True if the weather station is compatible, false otherwise.
     */
    public static boolean isWeatherUndergroundInstance(Resource resource) {
        //Debug.println("URL is " +resource.getURL().toString() + " call will return " 
        //        + resource.getURL().toString().toLowerCase().startsWith("http://www.wunderground.com"));
      //  return resource.getURL().toString().toLowerCase().startsWith("https://www.wunderground.com");
        return resource.getURL().toString().contains("www.wunderground.com");
    }

    /**
     * Returns the appropriate properties file for this WeatherUndergroundInstance.
     * If the instance has a column for solar radiation, it is given a properties
     * file to parse the extra column. By default the properties file with no
     * solar radiation is returned.
     * @return The file path for the appropriate properties file.
     */
    public String getPropertiesLocation() {
        StringTokenizer st = new Myst(this.weatherStationValues.toString());
        if (!st.hasMoreTokens()) {
            return NO_SOLAR_PROP_LOC;
        }
        st.nextToken("\n"); //skip one line at beginning of file
        if (!st.hasMoreTokens()) {
            return NO_SOLAR_PROP_LOC;
        }
        String firstLine = st.nextToken("\n");
        if (firstLine.contains("SolarRadiation")) {
            return SOLAR_PROP_LOC;
        }
        return NO_SOLAR_PROP_LOC;
    }
    
    /** 
     * Gets the number of rows of data stored in this instance, which is 
     * decompressed if it is not already.
     * @return The number of rows of data stored in this instance.
     */
    public int getRowsOfData() {
        if (compressed) {
            createValuesVector();
        }
        return variables.get(0).size();
    }
    
    /**
     * Builds our Vector of WeatherStationData for this instance.
     */
    @Override
    public void createValuesVector() {
        // declaration in WeatherStationInstance --> protected Vector<WeatherStationVariable> variables
        if (variables == null) {
            variables = new Vector<>();
        } else {
            variables.clear();
        }
        try {
            String propFile = this.getPropertiesLocation();
            Debug.println("PROPERTIES LOCATION = " + propFile);
            WeatherStationVariableProperties wvProperties =
                    new WeatherStationVariableProperties(propFile);
            for (String variableKey : wvProperties.getOrderedVariableKeys()) {
                variables.add(new WeatherStationVariable(variableKey));
            }

            // propertiesString should be a string buffer to save time and garbage creation - 6-28-2014
            String propertiesString = this.weatherStationValues.toString().
                    replaceAll("<!--[^>]*-->", "").replace("\n<br>", "");
            //replace("\n<br>", "") : now each row of data separated by a '\n'
            //Also, each line of data ends with a comma and the time value is the first token of the next line
                       
            //Empty properties need to be expanded into spaces for the tokenizer
            while(propertiesString.contains(",,")){
                propertiesString = propertiesString.replaceAll(",,", ", ,");
            }
            StringTokenizer st = new Myst(propertiesString);
            if(!st.hasMoreTokens()) {
                return;
            }
            
            //Must read lines until headers are bypassed.
            boolean headerRead = false;
            while (!headerRead && st.hasMoreTokens()) {
                String thisLine = st.nextToken("\n");
                if (thisLine.startsWith("Time")) {
                    headerRead = true;
                }
            }
            
             while (st.hasMoreTokens()) {//outer loop processes each row of data
                 //In order to determine if a row of data is valid, a helper
                 //function will now attempt to retreive valid data.  Because 
                 //the return value is a boolean to indicate success, an 
                 //ArrayList will be passed so that it can be filled with the 
                 //parsed result.
                 String thisLine = st.nextToken("\n");
                 ArrayList<Number> outputList = new ArrayList<>();
                 if (getNumbers(wvProperties, thisLine, outputList)) {
                     for (int i = 0; i < variables.size(); i++) {
                         WeatherStationVariable wv = variables.get(i);
                         wv.add(outputList.get(i));
                     }
                 }
             }//no more data in file
             
             //Add solar column if missing.
             if (propFile.equals(NO_SOLAR_PROP_LOC)) {
                 WeatherStationVariable solarVar = new WeatherStationVariable("solar");
                 for (int i = 0; i < variables.get(0).size(); i++) {
                     solarVar.add(null);
                 }
                 variables.add(13, solarVar); //solar is the 13th column zero-indexed 
             }
             
             timeValues = getVariableForKey("time"); 
             compressed = false;
            //ignore the last token, the comma at the end of the line
            //st.nextToken(COMMA_ONLY); Not needed --> see while loop 6-28-2014
        } catch (Exception e) {
            Debug.println("Unexpected exception: " + e.getMessage());
            //This is just meant to clean up after an exception that was not
            //expected and should be no problem. No WeatherException should
            //be thrown in this case.
            int minSize = 0;
            for (WeatherStationVariable wv : variables) {
                if (wv.size() < minSize) {
                    minSize = wv.size();
                }
            }
            for (WeatherStationVariable wv : variables) {
                while (wv.size() > minSize) {
                    wv.removeAtTail();
                }
            }
            
            //Log error.
            WeatherLogger.log(Level.SEVERE, "Unable to parse data!", e);
            Debug.println("Unable to parse data!\nMessage:\n"
                + e.getMessage());
        } 
        
        //If parsing fails, try again with default file.
        if (compressed) {
            try {
                this.readFile(new File("NoData.csv"));
            } catch (WeatherException ex) {
                //Should never be thrown.
            }
            this.createValuesVector();
        }
    }
    
    /**
     * A helper function to determine if a line from the input file can be 
     * successfully parsed into data.  In making the determination, the function
     * actually does the parsing.  As a result, the parsed data is returned in 
     * an <code>ArrayList</code> of type <code>Number</code>.  If the function
     * returns false, the passed output variable should not be used afterward,
     * as it will hold garbage.  Note that valid output may include values of 
     * null if the <code>WeatherStationVariable</code> with which the value is 
     * associated is not being used.
     * 
     * @param wvProperties The <code>WeatherStationVariableProperties</code>
     * object holding the information from the properties file in use.
     * @param input A full line of text to be parsed from the input file.
     * @param output An <code>ArrayList</code> of type <code>Number</code>. If
     * the return value is true, it will be filled with the parsed data.  If
     * not it will be filled with garbage.
     * @return True if the parsing was successful; False otherwise.
     */
    private boolean getNumbers(WeatherStationVariableProperties wvProperties,
            String input, ArrayList<Number> output) {
        StringTokenizer st = new StringTokenizer(input, COMMA_ONLY);
        Number value;
        String nextToken;
        for (int i = 0; i < variables.size(); i++) {
            if (!st.hasMoreTokens()) {
                //There are not enough tokens in the input line, so return false.
                return false;
            }
            nextToken = st.nextToken();
            //Debug.println("In getNumbers.  Token: " + nextToken);
            
            /**
             * The default value to be added is null.  Some weather station
             * variable that are not being used are of a type not given below. 
             * For those, is it alright to discard the token and add null to the 
             * output <code>ArrayList</code>.
             */
            value = null;   
            try {
                switch (wvProperties.getType(variables.get(i).getVariableKey())) {
                    case FLOAT:
                        value = Float.parseFloat(nextToken);
                        break;
                    case INT:
                        value = Integer.parseInt(nextToken);
                        break;
                    case TIME:
                        value = parseTime(nextToken);
                        break;
                    case LONG:
                        value = Long.parseLong(nextToken);
                        break;

                }
                output.add(value);
            } catch (WeatherException | NumberFormatException ex) {
                //A parsing error has occurred, so return false.
                return false;
            }
        }
        
        //If code get here, parsing was successful.
        //However, values could still be invalid.
        /**
         * PATCH: Check for this by seeing if the outside temperature is 
         * impossibly low.  From what we have seen, this means the whole row is
         * invalid.  Values should eventually be checked individually. (8/27/15)
         */
        if (output.get(1).floatValue() < -200) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method parses the time given a string representation of that time.
     * It strips newlines and carriage returns from the beginning of the string.
     * The format of the string input is: <code>yyyy-mm-dd hh:mm:ss</code>
     * This is overridden from the super class because the date format is different
     * in Weather Underground files.
     * @param time The string with the given format.
     * @return The time string as a long representation. 
     */
    private long parseTime(String time) throws WeatherException {
        //format: yyyy-mm-dd hh:mm:ss
        //        0123456789012345678
        Calendar cal = Calendar.getInstance();
        StringTokenizer st = new Myst(time);
        try {
            cal.set(Integer.parseInt(st.nextToken("-").replace("\n" , "").replace("\r", "")) , //year
                Integer.parseInt(st.nextToken("-"))-1 , //month
                Integer.parseInt(st.nextToken("- ")) , //day
                Integer.parseInt(st.nextToken(": ")) , //hour
                Integer.parseInt(st.nextToken(":")) , //minute
                Integer.parseInt(st.nextToken()) //second and end of string
            );
        } catch (NumberFormatException e) {
            throw new WeatherException();
        }
        
        return cal.getTimeInMillis();
    }
    
     /**
     * Given a start date, this method will sort through the timesInVar stored in
     * the Number Vector timesInVar, which stores the time timesInVar for the
     * current data in this WeatherStationInstance and find the index
     * of the first value that is greater than or equal to the startTime.
     * @param startTime Date representing the start time for which to find an index.
     * @return The index in the stored timesInVar for the given start time.
     */
    private int getStartIndexForTime(Date startTime) {
        long startMilliseconds = startTime.getTime();
        int startIndex = 0;
        while (startIndex < timeValues.size() - 1 && startMilliseconds > timeValues.get(startIndex).longValue()) {
            startIndex++;
        }
        return startIndex;
    }

    /**
     * Given an end date, this method will sort through the timesInVar stored in
     * the Number Vector timesInVar, which stores the time timesInVar for the
     * current data in this WeatherStationInstance and find the index
     * of the first value that is less than or equal to the endTime.
     * @param endTime Date representing the end time for which to find an index.
     * @return The index in the stored timesInVar for the given end time.
     */
    private int getEndIndexForTime(Date endTime) {
        long endMilliseconds = endTime.getTime();
        int endIndex = timeValues.size() - 1;
        while (endIndex > 0 && endMilliseconds < timeValues.get(endIndex).longValue()) {
            endIndex--;
        }
        return endIndex;
    }

    /**
     * Retrieves a vector of timesInVar from a specified range of resources.
     * NOTE: if the station id compressed, this function will return null.
     * @param range The range of resources to retrieve timesInVar from.
     * @return A vector of timesInVar from a resource range.
     */
    @Override
    public Vector<WeatherStationVariable> getValuesForResourceRange(ResourceRange range) {
        if (compressed) {
            createValuesVector();
        }
        Vector<WeatherStationVariable> returnValues = new Vector<>();
        int startIndex;
        int endIndex;
        startIndex = getStartIndexForTime(range.getStartTime());
        endIndex = getEndIndexForTime(range.getStopTime());
        if (startIndex < timeValues.size() && endIndex >= startIndex) {
            for (WeatherStationVariable variable : variables) {
                Vector<Number> fullVectorList = variable.getValues();
                List<Number> variableValueList = fullVectorList
                        .subList(startIndex, endIndex);
                Vector<Number> variableValues = new Vector<>(variableValueList);
                WeatherStationVariable newVariable = new WeatherStationVariable(
                        variable.getVariableKey());
                newVariable.resetValues(variableValues);
                returnValues.add(newVariable);
            }
        }
        return returnValues;
    }

    /**
     * This inner class is for debugging purposes. it will be removed in later
     * versions. 
     */
    public class Myst extends StringTokenizer {
        
        public Myst(String in) {
            super(in);
        }

        /**
         * Here I override the nextToken so that I can uncomment the debug line
         * and see what tokens are getting selected for each token.
         * @param del The delimeter.
         * @return The next token according to the delimeter. 
         */
        @Override
        public String nextToken(String del) {
            String out = super.nextToken(del);
            //Debug.println("INCOMING TOKEN: \""+out+"\"");
            return out;
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
            this.weatherStationValues.delete(0, this.weatherStationValues.length());
            variables.clear();
            timeValues = null;
            String inputLine;
            while ((inputLine = br.readLine()) != null){
                weatherStationValues.append(inputLine).append(CRLF);
            }
            br.close();
            compressed = true;
        } catch (IOException e) {
            throw new WeatherException(3006, e);
        }
    }
}
