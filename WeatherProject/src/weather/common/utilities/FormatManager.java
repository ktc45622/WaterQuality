package weather.common.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import weather.clientside.manager.DiaryManager;
import weather.common.data.diary.DailyEntry;
import weather.common.data.note.InstructorNote;

/**
 * This class contains general utility methods used to check the format of any
 * value passed to it using the Pattern and Matcher classes.
 *
 * @see DataFilter. TODO: some of these methods belong with
 * @see DiaryEntry. TODO: once other todo's are complete, this class is no
 * longer needed.
 *
 * @author Chris Mertens
 */
public class FormatManager {

    private static Pattern pattern;
    private static Matcher matcher;
    private static SimpleDateFormat dateformatMMDDYYYY = 
            new SimpleDateFormat(PropertyManager
            .getGeneralProperty("dateFormatString"));
    // The row numbers for the daily diary output.
    private static final int dateRow = 0;
    private static final int tempPatternRow = 1;
    private static final int tempHighRow = 2;
    private static final int tempLowRow = 3;
    private static final int tempRangeRow = 4;
    private static final int pressureTrendRow = 5;
    private static final int pressureStartingValueRow = 6;
    private static final int pressureEndingValueRow = 7;
    private static final int dPTrendRow = 8;
    private static final int dPStartingValueRow = 9;
    private static final int dPEndingValueRow = 10;
    private static final int rHPatternRow = 11;
    private static final int rHHighRow = 12;
    private static final int rHLowRow = 13;
    private static final int cloudsMorningRow = 14;
    private static final int cloudsAfternoonRow = 15;
    private static final int cloudsEveningRow = 16;
    private static final int windShiftRow = 17;
    private static final int windSpeedRow = 18;
    private static final int precipitationRow = 19;
    private static final int totalRows = 20;

    /**
     * Takes a string and determines if it represents an integer value between
     * -999 and 999.
     *
     * @param value the string to check.
     * @return true if the string matches the format, false otherwise.
     */
    public static boolean isThreeDigitInteger(String value) {
        try {
            Integer.parseInt(value);
            pattern = Pattern.compile("(-?)([0-9]{1,3})");
            matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Takes a string and determines if it represents an integer value between 0
     * and 999.
     *
     * @param value the string to check.
     * @return true if the string matches the format, false otherwise.
     */
    public static boolean isNonNegativeThreeDigitInteger(String value) {
        try {
            Integer.parseInt(value);
            pattern = Pattern.compile("[0-9]{1,3}");
            matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Validates a string as a double, if it is a double it returns true, if it
     * is not a double, then it returns false.
     *
     * @param value the double to validate
     * @return true if it is a double, false if not.
     */
    public static boolean isDecimalValid(String value) {
        try {
            Float.parseFloat(value);
            pattern = Pattern.compile("(-?)([0-9]{0,3})(((.)([0-9]{0,3}))?)");
            matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Tests a string to see if it is a non-negative at most four digit number
     * with decimals that are no more than to the thousandths place.
     *
     * @param value the string to test
     * @return true if it is valid under the conditions, false if not.
     */
    public static boolean isNonNegativeFourDigitDouble(String value) {
        try {
            Double.parseDouble(value);
            pattern = Pattern.compile("([0-9]{0,4})(((.)([0-9]{0,3}))?)");
            matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Takes a string and tests it to see if it represents a four digit integer.
     *
     * @param value the string to test.
     * @return true if the string matches the format, false otherwise.
     */
    public static boolean isNonNegativeFourDigitInteger(String value) {
        try {
            Integer.parseInt(value);
            pattern = Pattern.compile("[0-9]{1,4}");
            matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Takes a string and determines if it represents a two decimal non-negative
     * double.
     *
     * @param value the string being tested
     * @return true if the string matches the format; false otherwise
     */
    public static boolean isTwoDecimalNonNegativeDouble(String value) {
        try {
            Double.parseDouble(value);
            pattern = Pattern.compile("([0-9]{1,3})(((.)([0-9]{1,2}))?)");
            matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * TODO: JavaDocs
     *
     * @param file The file to extract from.
     * @return A string of contents from the file.
     * @throws Exception
     */
    public static String readFileToString(File file) throws Exception {
        FileInputStream stream = null;
        FileChannel fc = null;
        String contents = null;

        // Code obtained from http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
        // Minor modifications.
        try {
            stream = new FileInputStream(file);
            fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            contents = Charset.defaultCharset().decode(bb).toString();
        } finally {
            fc.close();
            stream.close();
        }

        return contents;
    }

    /**
     * Formats an array of diary entries to be stored in an .html file.  The
     * entries that form the output are always for the current diary resource, 
     * so the given dates must have entries for that resource.
     * @param dates the list of dates to format the entries of.
     * @return the formatted entries.
     */
    public static String diaryToHTML(Date[] dates) {
        dateformatMMDDYYYY.setTimeZone(DiaryManager.getResource().getTimeZone()
                .getTimeZone());
        String contents = "";
        int numCols = dates.length;
        ArrayList<ArrayList> diaryData = new ArrayList<>(dates.length);
        for (Date date : dates) {
            diaryData.add(getEntryValues(DiaryManager.getEntryForDate(date)));
        }
        try {
            contents = readFileToString(
                    new File(CommonLocalFileManager.getDiaryCssFilename()));
        } catch (Exception e) {
            // Don't do anything; we just won't have any css.
        }

        StringBuilder sb = new StringBuilder();
        /*This is the part of the html that is constant and creates the header
         information.*/
        sb.append("<html><style type='text/css'>");
        sb.append(contents);
        sb.append(
                "</style><table rules=\"all\" cellspacing=\"0\" "
                + "cellpadding=\"5\" style=\"white-space: nowrap;\">"
                + "<tr><td colspan=\"" + (numCols + 2) + "\">Weather Diary Output - "
                + DiaryManager.getUserLastName() + " "
                + DiaryManager.getUserFirstName()
                + "</td></tr>");
        sb.append("<tr><td colspan=\"" + (numCols + 2) + "\">Location - "
                + DiaryManager.getResource().getResourceName()
                + "</td></tr>");
        //Print the dates as headers for the table.
        sb.append("<tr><th colspan=\"2\">DATE</th>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<th>" + diaryData.get(i).get(dateRow) + "</th>");
        }
        sb.append("</tr>");

        //Print the last modified dates of the entries.
        sb.append("<tr><th colspan=\"2\">LAST MODIFIED</th>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<th>").append(dateformatMMDDYYYY.format(DiaryManager
                    .getEntryForDate(dates[i]).getLastModifiedDate()))
                    .append("</th>");
        }
        sb.append("</tr>");

        //Print temperature data.
        sb.append("<tr><td colspan=\"2\">TEMP. PATTERN</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + abbreviate((String) diaryData.get(i).get(tempPatternRow)) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td rowspan=\"2\">TEMP.</td>");
        sb.append("<td>H</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(tempHighRow) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td>L</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(tempLowRow) + "</td>");
        }
        sb.append("</tr>");
        sb.append(("<tr><td colspan=\"2\">TEMP. RANGE</td>"));
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(tempRangeRow) + "</td>");
        }
        sb.append("</tr>");

        //Print pressure data.
        sb.append("<tr><td colspan=\"2\">PRE. TREND</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + abbreviate((String) diaryData.get(i).get(pressureTrendRow)) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td rowspan=\"2\">PRE.</td>");
        sb.append("<td>B</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(pressureStartingValueRow) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td>E</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(pressureEndingValueRow) + "</td>");
        }
        sb.append("</tr>");

        //Print dew point data.
        sb.append("<tr><td colspan=\"2\">DP TREND</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + abbreviate((String) diaryData.get(i).get(dPTrendRow)) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td rowspan=\"2\">DP</td>");
        sb.append("<td>B</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(dPStartingValueRow) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td>E</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(dPEndingValueRow) + "</td>");
        }
        sb.append("</tr>");

        //Print relative humidity data.
        sb.append("<tr><td colspan=\"2\">RH Pattern</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + abbreviate((String) diaryData.get(i).get(rHPatternRow)) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td rowspan=\"2\">RH</td>");
        sb.append("<td>H</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(rHHighRow) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td>L</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(rHLowRow) + "</td>");
        }
        sb.append("</tr>");

        //Print cloud data.
        sb.append("<tr><td rowspan=\"3\">CLOUDS</td>");
        sb.append("<td>M</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(cloudsMorningRow) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td>A</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(cloudsAfternoonRow) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td>E</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(cloudsEveningRow) + "</td>");
        }
        sb.append("</tr>");

        //Print wind data.
        sb.append("<tr><td colspan=\"2\">WIND SHIFT</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(windShiftRow) + "</td>");
        }
        sb.append("</tr>");
        sb.append("<tr><td colspan=\"2\">WIND SPEED</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(windSpeedRow) + "</td>");
        }
        sb.append("</tr>");

        //Print precipitation daata.
        sb.append("<tr><td colspan=\"2\">PRECIP</td>");
        for (int i = 0; i < numCols; i++) {
            sb.append("<td>" + diaryData.get(i).get(precipitationRow) + "</td>");
        }
        sb.append("</tr>");

        /* Closes the table and html. */
        sb.append(
                "</table>"
                + "</html>");
        return sb.toString();
    }

    /**
     * Forms a single note into HTML
     *
     * @param note The note to format.
     * @return A formatted string to export.
     */
    public static String noteToHTML(InstructorNote note) {
        dateformatMMDDYYYY.setTimeZone(DiaryManager.getResource().getTimeZone()
                .getTimeZone());
        StringBuilder sb = new StringBuilder();
        String contents = "";
        try {
            contents = readFileToString(
                    new File(CommonLocalFileManager.getDiaryCssFilename()));
        } catch (Exception e) {
            // Don't do anything; we just won't have any css.
        }
        sb.append("<html><style type='text/css'>");
        sb.append(contents);
        sb.append("</style><b>Start Date: </b>").append(dateformatMMDDYYYY.format(note.getStartTime())).append("</br>");
        sb.append("<b>End Date: </b>").append(dateformatMMDDYYYY.format(note.getEndTime())).append("</br>");
        sb.append("<b>Location: </b>").append(DiaryManager.getResource().getName()).append("</br>");
        sb.append("<b>Title: </b>").append(note.getNoteTitle()).append("</br></br>");
        String s = note.getText();
        s = s.replaceAll("\\n", "</br>");
        sb.append("<p>").append(s).append("</p>");
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Forms a single note into a text file.
     *
     * @param note The note to format.
     * @return A formatted string to export.
     */
    public static String noteToText(InstructorNote note) {
        dateformatMMDDYYYY.setTimeZone(DiaryManager.getResource().getTimeZone()
                .getTimeZone());
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");
        sb.append("Start Date:\t").append(dateformatMMDDYYYY.format(note.getStartTime())).append(nl);
        sb.append("End Date:\t").append(dateformatMMDDYYYY.format(note.getEndTime())).append(nl);
        sb.append("Location:\t").append(DiaryManager.getResource().getName()).append(nl);
        sb.append("Title:\t").append(note.getNoteTitle()).append(nl).append(nl);
        sb.append(note.getText());
        return sb.toString();
    }

    /**
     * Used to populate a collection with string values that have been formatted
     * to check for null/uninitialized values.
     *
     * @param entry the entry to format.
     * @return A string representing the formatted entry.
     */
    private static ArrayList<String> getEntryValues(DailyEntry entry) {
        ArrayList<String> diaryData = new ArrayList<String>(totalRows);
        diaryData.add(dateRow, dateformatMMDDYYYY.format(entry.getDate())); //0
        diaryData.add(tempPatternRow, entry.getTempTrendType().displayString()); //1
        diaryData.add(tempHighRow, nullCheck(entry.getMaxTemp(), " F")); //2
        diaryData.add(tempLowRow, nullCheck(entry.getMinTemp(), " F")); //3
        diaryData.add(tempRangeRow, nullCheck(entry.getTempRange(), "")); //4
        diaryData.add(pressureTrendRow, entry.getBPTrendType().displayString());  //5
        diaryData.add(pressureStartingValueRow, nullCheck(entry.getStartBP(), " mB")); //6
        diaryData.add(pressureEndingValueRow, nullCheck(entry.getEndBP(), " mB")); //7
        diaryData.add(dPTrendRow, entry.getDPTrendType().displayString()); //8
        diaryData.add(dPStartingValueRow, nullCheck(entry.getStartDP(), " F")); //9
        diaryData.add(dPEndingValueRow, nullCheck(entry.getEndDP(), " F")); //10
        diaryData.add(rHPatternRow, entry.getRHTrendType().displayString()); //11
        diaryData.add(rHHighRow, nullCheck(entry.getMaxRH(), "%")); //12
        diaryData.add(rHLowRow, nullCheck(entry.getMinRH(), "%")); //13
        diaryData.add(cloudsMorningRow, entry.getPrimaryMorningCloudType().displayString()); //14
        diaryData.add(cloudsAfternoonRow, entry.getPrimaryAfternoonCloudType().displayString()); //15
        diaryData.add(cloudsEveningRow, entry.getPrimaryNightCloudType().displayString()); //16
        diaryData.add(windShiftRow, nullCheck(entry.getMinWindChill(), "")); //17
        diaryData.add(windSpeedRow, nullCheck(entry.getWindSpeed().displayMPH(), "")); //18 displayMPH gives units.
        diaryData.add(precipitationRow, nullCheck(entry.getDailyPrecipitation(), " in.")); //19
        return diaryData;
    }

    /**
     * Determines if a value is empty, if it is places a - in its place. Appends
     * the proper units on the value if it is not empty.
     *
     * @param value the string to check.
     * @param units the units to append.
     * @return either a - or the string with its units.
     */
    private static String nullCheck(String value, String units) {
        if (value == null || value.equals("") || value.equals("N/A")) {
            return "-";
        } else {
            return value + units;
        }
    }

    /**
     * Used to abbreviate the trend enumerations so multiple diary entries can
     * fit on one page when exported.
     *
     * @param enumeration The string containing an enumeration to be abbreviated
     * @return The abbreviated enumerated type.
     */
    private static String abbreviate(String enumeration) {
        if (enumeration.equals("Typical Diurinal Pattern")) {
            return "TDP";
        } else if (enumeration.equals("Non-Typical Diurinal Pattern")) {
            return "NTDP";
        } else if (enumeration.equals("Rising then falling")) {
            return "R then F";
        } else if (enumeration.equals("Falling then rising")) {
            return "F then R";
        } else // Passed enumeration not found
        {
            return enumeration;
        }
    }

    /**
     * Formats an array of diary entries to be stored in a .csv file.  The
     * entries that form the output are always for the current diary resource, 
     * so the given dates must have entries for that resource.
     * @param dates the array of dates corresponding to what entries to format.
     * @return the formatted entries.
     */
    public static String diaryToCSV(Date[] dates) {
        dateformatMMDDYYYY.setTimeZone(DiaryManager.getResource().getTimeZone()
                .getTimeZone());
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");
        int numCols = dates.length;
        ArrayList<ArrayList> diaryData = new ArrayList<>(dates.length);
        for (Date date : dates) {
            diaryData.add(getEntryValues(DiaryManager.getEntryForDate(date)));
        }

        sb.append("\"Weather Diary Output - " + DiaryManager.getUserFirstName() + " "
                + DiaryManager.getUserLastName() + "\""
                + nl + nl
                + "\"Location - " + DiaryManager.getResource().getName() + "\""
                + nl + nl
                + "\"Range (" + dates[0] + " to " + dates[dates.length - 1] + ")\""
                + nl + nl
                + "\"DATE\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(dateRow) + "\",");
        }
        sb.append(nl);

        //Print the last modified dates of the entries.
        sb.append("\"LAST MODIFIED\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"").append(dateformatMMDDYYYY.format(DiaryManager
                    .getEntryForDate(dates[i]).getLastModifiedDate())).append("\",");
        }
        sb.append(nl);

        //Temperature data
        sb.append("\"TEMP. PATTERN\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + abbreviate((String) diaryData.get(i).get(tempPatternRow)) + "\",");
        }
        sb.append(nl);
        sb.append("\"MAX TEMP.\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(tempHighRow) + "\",");
        }
        sb.append(nl);
        sb.append("\"MIN TEMP.\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(tempLowRow) + "\",");
        }
        sb.append(nl);
        sb.append("\"TEMP. RANGE\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(tempRangeRow) + "\",");
        }
        sb.append(nl);

        //Pressure data
        sb.append("\"PRES. TREND\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + abbreviate((String) diaryData.get(i).get(pressureTrendRow)) + "\",");
        }
        sb.append(nl);
        sb.append("\"INITIAL PRES.\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(pressureStartingValueRow) + "\",");
        }
        sb.append(nl);
        sb.append("\"FINAL PRES.\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(pressureEndingValueRow) + "\",");
        }
        sb.append(nl);

        //Dew Point data
        sb.append("\"DP TREND\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + abbreviate((String) diaryData.get(i).get(dPTrendRow)) + "\",");
        }
        sb.append(nl);
        sb.append("\"INITIAL DP\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(dPStartingValueRow) + "\",");
        }
        sb.append(nl);
        sb.append("\"FINAL DP\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(dPEndingValueRow) + "\",");
        }
        sb.append(nl);

        //Relative Humidity data
        sb.append("\"RH PATTERN\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + abbreviate((String) diaryData.get(i).get(rHPatternRow)) + "\",");
        }
        sb.append(nl);
        sb.append("\"RH HIGH\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(rHHighRow) + "\",");
        }
        sb.append(nl);
        sb.append("\"RH LOW\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(rHLowRow) + "\",");
        }
        sb.append(nl);

        //Cloud data
        sb.append("\"MORNING CLOUDS\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(cloudsMorningRow) + "\",");
        }
        sb.append(nl);
        sb.append("\"AFTERNOON CLOUDS\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(cloudsAfternoonRow) + "\",");
        }
        sb.append(nl);
        sb.append("\"EVENING CLOUDS\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(cloudsEveningRow) + "\",");
        }
        sb.append(nl);

        //Wind data
        sb.append("\"WIND SHIFT\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(windShiftRow) + "\",");
        }
        sb.append(nl);
        sb.append("\"WIND SPEED\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(windSpeedRow) + "\",");
        }
        sb.append(nl);

        //Precipitation data
        sb.append("\"PRECIPITATION\",");
        for (int i = 0; i < numCols; i++) {
            sb.append("\"" + diaryData.get(i).get(precipitationRow) + "\",");
        }
        sb.append(nl);

        return sb.toString();
    }

    /**
     * Formats an array of diary entries to be stored in a text file.  The
     * entries that form the output are always for the current diary resource, 
     * so the given dates must have entries for that resource.
     * @param dates the array of date objects
     * @return a string containing the diary format
     */
    public static String diaryToText(Date[] dates) {
        dateformatMMDDYYYY.setTimeZone(DiaryManager.getResource().getTimeZone()
                .getTimeZone());
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");
        int numCols = dates.length;
        ArrayList<ArrayList> diaryData = new ArrayList<>(dates.length);
        for (Date date : dates) {
            diaryData.add(getEntryValues(DiaryManager.getEntryForDate(date)));
        }

        sb.append("Weather Diary Output - " + DiaryManager.getUserFirstName() + " "
                + DiaryManager.getUserLastName() + nl + "Location - "
                + DiaryManager.getResource().getResourceName() + nl + "DATE:\t\t");

        //Date data
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(dateRow) + "\t");
        }
        sb.append(nl);

        //Print the last modified dates of the entries.
        sb.append("LAST MODIFIED:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(dateformatMMDDYYYY.format(DiaryManager
                    .getEntryForDate(dates[i]).getLastModifiedDate())).append("\t");
        }
        sb.append(nl);

        //Temperature data
        sb.append(nl + "--TEMPERATURE--" + nl);
        sb.append("    PATTERN:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(abbreviate((String) diaryData.get(i).get(tempPatternRow)) + "\t\t");
        }
        sb.append(nl);
        sb.append("        MAX:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(tempHighRow) + "\t\t");
        }
        sb.append(nl);
        sb.append("        MIN:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(tempLowRow) + "\t\t");
        }
        sb.append(nl);
        sb.append("      RANGE:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(tempRangeRow) + "\t\t");
        }

        //Pressure data
        sb.append(nl + "--PRESSURE--" + nl);
        sb.append("      TREND:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(abbreviate((String) diaryData.get(i).get(pressureTrendRow)) + "   \t");
        }
        sb.append(nl);
        sb.append("    INITIAL:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(pressureStartingValueRow) + "\t\t");
        }
        sb.append(nl);
        sb.append("      FINAL:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(pressureEndingValueRow) + "\t\t");
        }
        sb.append(nl);

        //Dew Point data
        sb.append("--DEW POINT--" + nl);
        sb.append("      TREND:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(abbreviate((String) diaryData.get(i).get(dPTrendRow)) + "   \t");
        }
        sb.append(nl);
        sb.append("    INITIAL:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(dPStartingValueRow) + "\t\t");
        }
        sb.append(nl);
        sb.append("      FINAL:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(dPEndingValueRow) + "\t\t");
        }
        sb.append(nl);

        //Relative Humidity data
        sb.append("--RELATIVE HUMIDITY--" + nl);
        sb.append("    PATTERN:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(abbreviate((String) diaryData.get(i).get(rHPatternRow)) + "\t\t");
        }
        sb.append(nl);
        sb.append("        MAX:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(rHHighRow) + "\t\t");
        }
        sb.append(nl);
        sb.append("        MIN:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(rHLowRow) + "\t\t");
        }
        sb.append(nl);

        //Cloud data
        sb.append("--CLOUDS--" + nl);
        sb.append("    MORNING:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(cloudsMorningRow) + "\t\t");
        }
        sb.append(nl);
        sb.append("  AFTERNOON:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(cloudsAfternoonRow) + "\t\t");
        }
        sb.append(nl);
        sb.append("    EVENING:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(cloudsEveningRow) + "\t\t");
        }
        sb.append(nl);

        //Wind data
        sb.append("--WIND--" + nl);
        sb.append("      SHIFT:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(windShiftRow) + "\t\t");
        }
        sb.append(nl);
        sb.append("      SPEED:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(windSpeedRow) + "\t\t");
        }
        sb.append(nl + nl + nl + nl);

        //Precipitation data
        sb.append("--PRECIPITATION--" + nl);
        sb.append("      TOTAL:\t");
        for (int i = 0; i < numCols; i++) {
            sb.append(diaryData.get(i).get(precipitationRow) + "\t\t");
        }
        sb.append(nl);


        return sb.toString();
    }
}
