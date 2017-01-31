package weather.common.utilities;

/**
 * This class contains methods that are used to perform web-related actions
 * needed to grade forecaster lessons.
 * @author Brian Bankes
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import weather.common.dbms.DBMSSystemManager;

public class WebGradingTools {
    private final static String UCRA_DATA_URL = PropertyManager.getGeneralProperty("UCRA_DATA_URL");
    
    /**
     * This method obtains response data for every station available on the
     * provided date. Information is parseWebAnswersd to be entered into the
     * database as Station object. Data obtained from the website
     * http://mtarchive.geol.iastate.edu/fcst/ which is stored as a database 
     * property.
     *
     * @param dbms The application's <code>DBMSSystemManager</code>.
     * @param cal Date to obtain response data for.
     * @param stationCode The code of the station to obtain response data for.
     */
    public static void parseWebAnswers(DBMSSystemManager dbms, Calendar cal,
            String stationCode) {
        //Map to store parsed dota.
        HashMap<String, String> qapair = new HashMap<>();

        String fileName = UCRA_DATA_URL + WebGradingTools.getFormattedDate(cal)
                + ".out";
        Debug.println("Parsing File: " + fileName);

        String fileContents = WebGradingTools.readURL(fileName);

        //Variables to hold boundrary indexes of web page line to be stored.
        int lineBeginIndex, lineEndIndex;

        //Find lineEndIndex needed to start loop.
        //(testCal of "STAATION=stationCode" line)
        int stationLineIndex = fileContents.indexOf("STATION=" + stationCode);
        lineEndIndex = fileContents.indexOf('\n', stationLineIndex);

        //Use Debug to see if station was found.
        if (lineEndIndex == -1) {
            Debug.println("Error: " + stationCode + " not found.");
            return;
        } else {
            Debug.println("Success: " + stationCode + " was found.");
            Debug.println("Start: " + stationLineIndex
                    + " End: " + lineEndIndex);
        }

        //Variables to hold the curret line and its parts.
        String currentLine, question, answer;

        //Parse the 36 lines of station data.
        for (int i = 0; i < 36; i++) {
            //Get boundrary indexes
            lineBeginIndex = lineEndIndex + 1;  //start of next line
            lineEndIndex = fileContents.indexOf('\n', lineBeginIndex);
            currentLine = fileContents.substring(lineBeginIndex, lineEndIndex);
            Debug.println("Line " + i + ": " + currentLine);
            int equalsSignLoc = currentLine.indexOf('=');   //split of Q and A
            question = currentLine.substring(0, equalsSignLoc);
            answer = currentLine.substring(equalsSignLoc + 1);
            // enter values into hash map 
            qapair.put(question, answer);
        }

        // add new Station object to database
        dbms.getForecasterStationDataManager().addStationData(stationCode,
                new java.sql.Date(cal.getTimeInMillis()), qapair);

    }
    
    /**
     * This method returns the day in proper format to download the answer file.
     * The format is ex: 20120901 not 201291.
     * @param cal The calendar holding the date.
     * @return String holding the formatted dote.
     */
    public static String getFormattedDate(Calendar cal) {
        StringBuilder s = new StringBuilder();
        s.append(cal.get(Calendar.YEAR));
        
        int month = cal.get(Calendar.MONTH) 
                + 1; //Add one since months zero based.
        if (month < 10) {
            s.append(0);
        }
        s.append(month);// add one since months zero based

        if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
            s.append(0);
        }
        s.append(cal.get(Calendar.DAY_OF_MONTH));

        return s.toString();
    }
    
    /**
     * Reads the text of a URL into a String.
     * @param pageName The name of the page to be read.
     * @return A String holding the text of the URL.
     */
    public static String readURL(String pageName) {
        StringBuilder stringAsBuilder = new StringBuilder();
        BufferedReader br;
        URL url;
        try {
            url = new URL(pageName);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String buffer;
            while ((buffer = br.readLine()) != null) {
                stringAsBuilder.append(buffer).append("\n");
            }
            br.close();
            return new String(stringAsBuilder);
        } catch (IOException e) {
            Debug.println("Can't scan URL: " + pageName);
            return null;
        }
    }
    
    /**
     * Private method used to debug the result of looking for and showing the
     * contents of a web page.
     * @param pageName The web page.
     */
    private static void debugResult(String pageName){
        if(PageChecker.doesPageExist(pageName)) {
            Debug.println(pageName + " exists.");
            Debug.println("Contents:");
            Debug.println(readURL(pageName));
        } else {
            Debug.println(pageName + " does not exist.");
        }
    }
    
    /**
     * For testing.
     * @param args (Should not be used.)
     */
    public static void main(String args[]) {
        //Test data results page for last 10 days.
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 10; i++) {
            String fileName = getFormattedDate(cal);
            debugResult(UCRA_DATA_URL + fileName + ".out");
            cal.add(Calendar.DATE, -1);
        }
    }
}
