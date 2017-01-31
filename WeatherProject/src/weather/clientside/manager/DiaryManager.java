package weather.clientside.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import weather.ApplicationControlSystem;
import weather.common.data.AccessRights;
import weather.common.data.User;
import weather.common.data.diary.DailyEntry;
import weather.common.data.note.InstructorNote;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSDiaryManager;
import weather.common.utilities.*;

/**
 * A static manager class used for controlling a user's daily diary.
 * 
 * NOTE: All stored times represent the start of days in the time zone of the
 * current resource.
 *
 * @author Bloomsburg University Software Engineering
 * @author Tyler Lusby (2007)
 * @author Tom Hand (2008)
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 *
 * @version Spring 2010
 */
public class DiaryManager {
    //The current diary user.
    private static User diaryUser = null;
    
    //Needed for database access.
    private static ApplicationControlSystem classAppControl;
    private static DBMSDiaryManager diaryManager;
    
    //Holds current date of diary and class notes.
    private static Calendar calendar;
    
    //The resource currently selected.
    private static Resource resource;

    /**
     * Configures the variables needed for the DailyDiary to function properly.
     * @param appControl The program's application control system.
     * @param user The user currently logged in.
     * @param initDate The initial date of the manager.
     * @param initResource  The resource to which the manager is set at first.  
     * This cannot be null.
     */
    public static void configure(ApplicationControlSystem appControl, User user, 
            Date initDate, Resource initResource) {
        classAppControl = appControl;
        diaryUser = user;
        resource = initResource;
        calendar = new GregorianCalendar();
        calendar.setTimeZone(resource.getTimeZone().getTimeZone());
        calendar.setTime(ResourceTimeManager.getStartOfDayDateFromMilliseconds(initDate
                .getTime(), resource.getTimeZone().getTimeZone()));
        diaryManager = classAppControl.getDBMSSystem().getDiaryManager();
    }

    /**
     * Gets the first name of the user who owns this diary.
     * @return The first name of the user.
     */
    public static String getUserFirstName() {
        return diaryUser.getFirstName();
    }

    /**
     * Gets the last name of the user who owns this diary.
     * @return The last name of the user.
     */
    public static String getUserLastName() {
        return diaryUser.getLastName();
    }

    /**
     * Saves a diary entry to the database.
     * @param entry The new entry to add or update.
     * @return Whether or not the save was successful.
     */
    public static boolean saveEntry(DailyEntry entry) {
        return diaryManager.saveEntry(entry, diaryUser);
    }

    /**
     * Gets the current entry for the day, user, and resource being displayed, 
     * which may be blank.
     * @return The entry for the current day, user, and resource being 
     * displayed.
     */
    public static DailyEntry getCurrentEntry() {
        return getEntryForDate(new Date(calendar.getTimeInMillis()));
    }
    
    /**
     * Returns whether or not a diary entry by the current user is saved in the
     * institutional database for the current diary resource on the given date.
     * @param date The given date, which must be the start of the day in the
     * time zone of the current diary resource.
     * @return True if there is a diary entry by the current user is saved in
     * the institutional database for the current diary resource on the given
     * date; False otherwise.
     */
    public static boolean isDiaryEntrySaved(Date date) {
        return diaryManager.getEntry(diaryUser, 
                new java.sql.Date(date.getTime()), resource) != null;
    }
    
    /**
     * Returns whether or not any private class notes by the current user are
     * saved in the institutional database for the current diary resource on the
     * given date.
     * @param date The given date, which must be the start of the day in the
     * time zone of the current diary resource.
     * @return True if there are any private class notes by the current user is
     * saved in the institutional database for the current diary resource on the
     * given date; False otherwise.
     */
    public static boolean arePrivateClassNotesSaved(Date date) {
        for (InstructorNote note : classAppControl.getDBMSSystem()
                .getNoteManager().getNotesForTimespan(new java.sql.Date(date
                .getTime()))) {
            if (note.getAccessRights() == AccessRights.Private
                    && note.getInstructorNumber() == diaryUser.getUserNumber() 
                    && note.getCameraNumber() == resource.getResourceNumber()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the possibly blank entry for the user and resource being displayed 
     * on the given date.
     * @param date The given date, which must be the start of the day in the
     * time zone of the current diary resource.
     * @return The entry for the user and resource being displayed on the given
     * date.
     */
    public static DailyEntry getEntryForDate(Date date) {
        //Get entry form database if present.
        DailyEntry dbEntry = diaryManager.getEntry(diaryUser, 
                new java.sql.Date(date.getTime()), resource);
        
        if (dbEntry == null) {
            //With no saved data, rerurn a blank entry.
            Resource station = getRelatedResource();
            return new DailyEntry(date, resource.getResourceNumber(), resource
                    .getResourceName(), station.getResourceNumber(), 
                    station.getResourceName());
        } else {
            //Return database entry.
            return dbEntry;
        }
    }

    /**
     * The values to be entered into any daily entry have specific formats to
     * follow. This method ensures all the user-typed values meet the format
     * requirements specified. Starts with the first value and as soon as it
     * finds something formatted wrong, it displays an error stating which
     * value needs fixing.
     * @param maxTemp The maximum temperature entered by the user.
     * @param minTemp The minimum temperature entered by the user.
     * @param maxBP The maximum barometric pressure entered by the user.
     * @param minBP The minimum barometric pressure entered by the user.
     * @param maxDP The maximum dew point entered by the user.
     * @param minDP The minimum dew point entered by the user.
     * @param maxRH The maximum relative humidity entered by the user.
     * @param minRH The minimum relative humidity entered by the user.
     * @param maxGustSpeed The maximum gust speed entered by the user.
     * @param dailyPrecipitation The daily precipitation entered by the user.
     * @param maxHeatIndex The maximum heat index entered by the user.
     * @param minWindChill The minimum departure from normal temperature 
     *                     due to wind chill entered by user.
     * @return An error message stating which value is malformed or a
     * success message allowing the entry to be saved with all values formed
     * correctly.
     */
    public static String testEntryFormat(String maxTemp, String minTemp,
            String maxBP, String minBP, String maxDP, String minDP,
            String maxRH, String minRH, String maxGustSpeed,
            String dailyPrecipitation, String maxHeatIndex, String minWindChill) {
        String returnMessage = "";
        if (!maxTemp.isEmpty() && !FormatManager.isDecimalValid(maxTemp)) {
            returnMessage += "Maximum temperature MUST be three digits and no decimal point past thousandths.\n";
        }
        if (!minTemp.isEmpty() && !FormatManager.isDecimalValid(minTemp)) {
            returnMessage += "Minimum temperature MUST be three digits and no decimal point past thousandths.\n";
        }
        if (FormatManager.isDecimalValid(maxTemp) && FormatManager.isDecimalValid(minTemp) &&
                !maxTemp.isEmpty() && !minTemp.isEmpty() && !(Double.parseDouble(maxTemp)
                >= Double.parseDouble(minTemp))) {
            returnMessage += "Maximum temperature MUST be greater than or equal to "
                    + "minimum temperature.\n";
        }
        if (!maxBP.isEmpty() && !FormatManager.isNonNegativeFourDigitDouble(maxBP)) {
            returnMessage += "Maximum barometric pressure MUST be a four digit real number "
                    + "and round to the nearest thousandths.\n";
        }
        if (!minBP.isEmpty() && !FormatManager.isNonNegativeFourDigitDouble(minBP)) {
            returnMessage += "Minimum barometric pressure MUST be a four digit real number "
                    + "and round to the nearest thousandths.\n";
        }
        if (!maxDP.isEmpty() && !FormatManager.isDecimalValid(maxDP)) {
            returnMessage += "Maximum dew point MUST be three digits and round to the"
                    + "nearest thousandths.\n";
        }
        if (!minDP.isEmpty() && !FormatManager.isDecimalValid(minDP)) {
            returnMessage += "Minimum dew point MUST be three digits and round to "
                    + "the nearest thousandths.\n";
        }
        if (!maxRH.isEmpty() && !FormatManager.isTwoDecimalNonNegativeDouble(maxRH)) {
            returnMessage += "Maximum relative humidity MUST be a decimal value less than 100 "
                    + "and round to nearest hundredths.\n";
        }
        if (!minRH.isEmpty() && !FormatManager.isTwoDecimalNonNegativeDouble(minRH)) {
            returnMessage += "Minimum relative humidity MUST be a decimal value less than 100 "
                    + "and round to nearest hundredths.\n";
        }
       if (FormatManager.isDecimalValid(maxRH) && FormatManager.isDecimalValid(minRH) &&
                !maxRH.isEmpty() && !minRH.isEmpty() && !(Double.parseDouble(maxRH)
                >= Double.parseDouble(minRH))) {
            returnMessage += "Maximum relative humidity MUST be greater than or equal to "
                    + "minimum relative humidity.\n";
        }
        if (!maxGustSpeed.isEmpty() && !FormatManager.isNonNegativeThreeDigitInteger(maxGustSpeed)) {
            returnMessage += "Maximum gust speed MUST be a three digit non-negative "
                    + "integer.\n";
        }
        if (!dailyPrecipitation.isEmpty() && !FormatManager.isTwoDecimalNonNegativeDouble(dailyPrecipitation)) {
            returnMessage += "Daily precipitation MUST be a decimal value less than 100 "
                    + "and round to nearest hundredths.\n";
        }
        if (!maxHeatIndex.isEmpty() && 
                !FormatManager.isNonNegativeFourDigitDouble(maxHeatIndex)) {
            returnMessage += "Maximum heat index MUST be no higher than four digits and "
                    + "round to the nearest thousandths.\n";
        }
        if (!minWindChill.isEmpty() && !FormatManager.isThreeDigitInteger(minWindChill)) {
            returnMessage += "Minimum wind chill MUST be a three digit integer.\n";
        }
        if (returnMessage.equals("")) {
            return "valid";
        } else {
            return returnMessage;
        }
    }

    /**
     * Sets the current diary date.
     * @param newDate The new date to set.
     */
    public static void setDate(Date newDate) {
        Date normalizedDate = ResourceTimeManager
                .getStartOfDayDateFromMilliseconds(newDate.getTime(),
                resource.getTimeZone().getTimeZone());
        calendar.setTime(normalizedDate);
    }

    /**
     * Gets the current diary date.
     * @return The Date object.
     */
    public static Date getDate() {
        return calendar.getTime();
    }
    
    /**
     * Sets the current resource of the manager.  It also maintains the current
     * local time (12 a. m.) while changing time zones, if necessary.
     * @param newResource The new resource.  (If it is null, the function has no
     * effect.)
     */
    public static void setResource(Resource newResource) {
        if (newResource == null) {
            return;
        }
        
        //Must save calendar date for reset after time zone is set.
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        
        //Set new resource and time zome.
        resource = newResource;
        calendar.setTimeZone(resource.getTimeZone().getTimeZone());
        
        //Restore time
        calendar.set(year, month, day, 0, 0);
    }
    
    /**
     * Gets the current diary resource.
     * @return The Resource object.
     */
    public static Resource getResource() {
        return resource;
    }
    
    
   /**
     * Gets the resource related to the current diary resource.
     * @return The related Resource object or a "dummy" resource if there is no
     * related resource.
     */ 
    public static Resource getRelatedResource() {
        Resource station = classAppControl.getDBMSSystem()
                .getResourceRelationManager()
                .getRelatedStationResource(resource);
        if (station == null) {
            station = new Resource();
            station.setResourceName("None");
        }
        return station;
    }

    /**
     * Gets the size of the diary (number of entries).
     * @return The number of entries in the diary.
     */
    public static int getDiarySize() {
        return diaryManager.getAllEntriesByUser(diaryUser).size();
    }

    /**
     * Gets the entry corresponding to the index given.
     * @param index The index of the entry requested.
     * @return The entry at the specified index.
     */
    public static DailyEntry getEntry(int index) {
        return diaryManager.getAllEntriesByUser(diaryUser).get(index);
    }
    
    /**
     * Gets a (possibly empty) list of all diary entries that contain data for
     * the current diary resource sorted by date. The object returned is an
     * <code>ArrayList</code> of type <code>DailyEntry</code>.
     * @return A (possibly empty) list of all diary entries that contain data
     * for the current diary resource sorted by date. The object returned is an
     * <code>ArrayList</code> of type <code>DailyEntry</code>.
     */
    public static ArrayList<DailyEntry> 
        getAllEntriesRorCurrentDiaryResourceWithData() {
        ArrayList<DailyEntry> returnList = new ArrayList<>();
        for (int i = 0; i < DiaryManager.getDiarySize(); i++) {
            if (DiaryManager.getEntry(i).getCameraNumber() == resource
                .getResourceNumber() && DiaryManager.getEntry(i).hasData()) {
                returnList.add(DiaryManager.getEntry(i));
            }
        }
        Collections.sort(returnList);
        return returnList;
    }

    /**
     * Removes the current <code>DairyEntry</code> from the database.
     * @return Whether or not the save was successful.
     */
    public static boolean removeCurrentEntry() {
       return diaryManager.deleteEntry(diaryUser, new java.sql.Date(calendar
                .getTimeInMillis()), resource);
    }
    
    /**
     * Given an input <code>Vector</code> of <code>InstructorNote</code>, 
     * returns a restricted version with only those for the diary's current
     * location.
     * @param input The input <code>Vector</code>.
     * @return The restricted <code>Vector</code>.
     */
    public static Vector<InstructorNote> 
            restrictToCurrentResource(Vector<InstructorNote> input) {
       Vector<InstructorNote> result = new Vector<>();
       
       for (InstructorNote note : input) {
           if (note.getCameraNumber() == resource.getResourceNumber()) {
               result.add(note);
           }
       }
       
       return result;
    }
}