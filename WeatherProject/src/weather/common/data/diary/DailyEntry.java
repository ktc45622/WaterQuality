package weather.common.data.diary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import weather.GeneralService;
import weather.common.data.AccessRights;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.Note;
import weather.common.dbms.DBMSSystemManager;

/**
 * This class stores all the values associated with a single entry in the daily
 * diary. It contains set and get methods for each value. The equals and
 * compareTo methods are also overloaded.
 *
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 * @author Alinson Antony(Spring 2012)
 *
 * @version 2012
 */
public class DailyEntry implements Comparable, Serializable {
    private Date entryDate;
    private String maxTemp, minTemp, tempRange;
  //  private HourlyTimeType maxTempTime, minTempTime;
    private TemperatureTrendType tempTrendType;
    private String startBP, endBP, BPRange;
  //  private HourlyTimeType maxBPTime, minBPTime;
    private BarometricPressureTrendType BPTrendType;
    private String startDP, endDP, DPRange;
 //   private HourlyTimeType maxDPTime, minDPTime;
    private DewPointTrendType DPTrendType;
    private String maxRH, minRH, RHRange;
//    private HourlyTimeType maxRHTime, minRHTime;
    private RelativeHumidityTrendType RHTrendType;
    private CloudType primaryMorningCloudType, secondaryMorningCloudType;
    private CloudType primaryAfternoonCloudType, secondaryAfternoonCloudType;
    private CloudType primaryNightCloudType, secondaryNightCloudType;
    private ArrayList<WindDirectionType> surfaceAirWindDirection;
    private WindDirectionSummaryType windDirectionSummary;
    private WindSpeedType windSpeed;
    private String maxGustSpeed, dailyPrecipitation, maxHeatIndex, minWindChill;
    private WindDirectionType upperAirWindDirection;
   // private YesNoType temperatureProfileQ1;
  //  private PressureChangeRelationType dailyPressureQ1;
  //  private PrecipitationChanceType dailyPressureQ2;
  //  private YesNoType dailyHumidityQ1;
  //  private TemperaturePredictionType predictionsQ1;
  //  private YesNoType predictionsQ2;
    
    /**
     * The note for the entry, which will only be visible for students and
     * guests. For other uses getNote() will substitute a different
     * <code>Note</code> made from all private instances of 
     * <code>InstructorNote</code> made by the current program user on the given 
     * day.
     */
    private Note entryNote;
    
    /**
     * The date and time this entry was last modified.
     */
    private Date lastModifiedDate = null;
    
    //Resouce Infomation
    private String cameraName;
    private String stationName;
    private int cameraNumber;
    private int stationNumber;

   /**
    * Determines if a de-serialized file is compatible with this class.
    *
    * Not necessary to include in first version of the class, but included here
    * as a reminder of its importance. Maintainers must change this value if
    * and only if the new version of this class is not compatible with old
    * versions.
    *
    * @see <a href="http://java.sun.com/products/jdk/1.1/docs/guide
    * /serialization/spec/version.doc.html">Java specification for
    * serialization</a>
    * @serial
    */
   private static final long serialVersionUID = 1L;

    /**
     * Creates a new DailyEntry with only a date and resource information, 
     * leaving the other fields empty.
     * @param entryDate The date of the entry.
     * @param cameraNumber The resource number of the camera resource associated
     * with this entry.
     * @param cameraName The resource name of the camera resource associated
     * with this entry.
     * @param stationNumber The resource number of the station resource 
     * associated with this entry.
     * @param stationName The resource name of the station resource associated
     * with this entry.
     */
    public DailyEntry(Date entryDate, int cameraNumber, String cameraName, 
            int stationNumber, String stationName) {
        this.entryDate = entryDate;
        entryNote = new Note(entryDate);
        this.cameraNumber = cameraNumber;
        this.cameraName = cameraName;
        this.stationNumber = stationNumber;
        this.stationName = stationName;
        maxTemp = "";
        minTemp = "";
        //maxTempTime = HourlyTimeType.NA;
        //minTempTime = HourlyTimeType.NA;
        tempTrendType = TemperatureTrendType.DIURINAL;
        startBP = "";
        endBP = "";
        //maxBPTime = HourlyTimeType.NA;
        //minBPTime = HourlyTimeType.NA;
        BPTrendType = BarometricPressureTrendType.STEADY;
        startDP = "";
        endDP = "";
        //maxDPTime = HourlyTimeType.NA;
        //minDPTime = HourlyTimeType.NA;
        DPTrendType = DewPointTrendType.STEADY;
        maxRH = "";
        minRH = "";
        //maxRHTime = HourlyTimeType.NA;
        //minRHTime = HourlyTimeType.NA;
        RHTrendType = RelativeHumidityTrendType.DIURINAL;
        primaryMorningCloudType = CloudType.NA;
        secondaryMorningCloudType = CloudType.NA;
        primaryAfternoonCloudType = CloudType.NA;
        secondaryAfternoonCloudType = CloudType.NA;
        primaryNightCloudType = CloudType.NA;
        secondaryNightCloudType = CloudType.NA;
        //windDirectionSummary need not be initialized
        windDirectionSummary = WindDirectionSummaryType.NA;
        windSpeed = WindSpeedType.NA;
        maxGustSpeed = "";
        dailyPrecipitation = "";
        maxHeatIndex = "";
        minWindChill = "";
        upperAirWindDirection = WindDirectionType.NA;
        //temperatureProfileQ1 = YesNoType.NA;
        //dailyPressureQ1 = PressureChangeRelationType.NA;
        //dailyPressureQ2 = PrecipitationChanceType.NA;
        //dailyHumidityQ1 = YesNoType.NA;
        //predictionsQ1 = TemperaturePredictionType.NA;
        //predictionsQ2 = YesNoType.NA;
        entryNote = new Note(entryDate);
        lastModifiedDate = new Date();
    }

    /**
     * Creates a new DailyEntry with data in all fields.
     * @param entryDate The date of the entry.
     * @param cameraNumber The resource number of the camera resource associated
     * with this entry.
     * @param cameraName The resource name of the camera resource associated
     * with this entry.
     * @param stationNumber The resource number of the station resource
     * associated with this entry.
     * @param stationName The resource name of the station resource associated
     * with this entry.
     * @param entryNote The note for the entry, which will only be visible for
     * students and guests.  For other uses getNote() will substitute a 
     * different <code>Note</code> made from all private instances of 
     * <code>InstructorNote</code> made by the current program user on the given
     * day.
     * @param maxTemp The maximum temperature.
     * @param minTemp The minimum temperature.
     * @param tempRange The range between the maximum and minimum temperatures.
     * @param tempTrendType The temperature trend,
     * @param startBP The start-of-day barometric pressure.
     * @param endBP The end-of-day barometric pressure.
     * @param BPRange The barometric pressure range.
     * @param BPTrendType The barometric pressure trend type.
     * @param startDP The dew point at the start of the day.
     * @param endDP The dew point at the end of the day.
     * @param DPRange The range between the maximum and minimum dew point
     * @param DPTrendType The dew point trend type.
     * @param maxRH The maximum relative humidity.
     * @param minRH The minimum relative humidity.
     * @param RHRange The range between the maximum and minimum relative humidity
     * @param RHTrendType The relative humidity trend.
     * @param primaryMorningCloudType The primary cloud type for the morning.
     * @param secondaryMorningCloudType The secondary cloud type for the morning.
     * @param primaryAfternoonCloudType The primary cloud type for the afternoon.
     * @param secondaryAfternoonCloudType The secondary cloud type for the afternoon.
     * @param primaryNightCloudType The primary cloud type for the night.
     * @param secondaryNightCloudType The secondary cloud type for the night.
     * @param surfaceAirWindDirection The surface air wind direction.
     * @param windDirectionSummary Summary of the wind direction.
     * @param windSpeed The wind speed.
     * @param maxGustSpeed The maximum gust speed.
     * @param dailyPrecipitation The daily precipitation.
     * @param maxHeatIndex The maximum heat index.
     * @param minWindChill The minimum wind chill.
     * @param upperAirWindDirection The upper air wind direction.
     * @param lastModifiedDate The date and time when this entry was last modified.
     */
    public DailyEntry(Date entryDate, int cameraNumber, String cameraName, 
            int stationNumber, String stationName, Note entryNote,
            String maxTemp, //HourlyTimeType maxTempTime,
            String minTemp,// HourlyTimeType minTempTime,
            String tempRange, 
            TemperatureTrendType tempTrendType,
            String startBP,
           // HourlyTimeType maxBPTime,
            String endBP,
           // HourlyTimeType minBPTime,
            String BPRange,
            BarometricPressureTrendType BPTrendType,
            String startDP,
            //HourlyTimeType maxDPTime, 
            String endDP,
           // HourlyTimeType minDPTime, 
            String DPRange,
            DewPointTrendType DPTrendType,
            String maxRH,// HourlyTimeType maxRHTime,
            String minRH,
          //  HourlyTimeType minRHTime, 
            String RHRange,
            RelativeHumidityTrendType RHTrendType,
            CloudType primaryMorningCloudType,
            CloudType secondaryMorningCloudType,
            CloudType primaryAfternoonCloudType,
            CloudType secondaryAfternoonCloudType,
            CloudType primaryNightCloudType,
            CloudType secondaryNightCloudType,
            ArrayList<WindDirectionType> surfaceAirWindDirection,
            WindDirectionSummaryType windDirectionSummary,
            WindSpeedType windSpeed, 
            String maxGustSpeed,
            String dailyPrecipitation, 
            String maxHeatIndex,
            String minWindChill,
            WindDirectionType upperAirWindDirection,
            Date lastModifiedDate
          //  YesNoType temperatureProfileQ1,
          //  PressureChangeRelationType dailyPressureQ1,
          //  PrecipitationChanceType dailyPressureQ2,
           // YesNoType dailyHumidityQ1,
         //   TemperaturePredictionType predictionsQ1,
         //   YesNoType predictionsQ2
            ) {
        this.entryDate = entryDate;
        this.cameraNumber = cameraNumber;
        this.cameraName = cameraName;
        this.stationNumber = stationNumber;
        this.stationName = stationName;
        this.entryNote = entryNote;
        this.maxTemp = maxTemp;
       // this.maxTempTime = maxTempTime;
        this.minTemp = minTemp;
       // this.minTempTime = minTempTime;
        this.tempRange = tempRange;
        this.tempTrendType = tempTrendType;
        this.startBP = startBP;
      //  this.maxBPTime = maxBPTime;
        this.endBP = endBP;
     //   this.minBPTime = minBPTime;
        this.BPRange = BPRange;
        this.BPTrendType = BPTrendType;
        this.startDP = startDP;
    //    this.maxDPTime = maxDPTime;
        this.endDP = endDP;
    //    this.minDPTime = minDPTime;
        this.DPRange = DPRange;
        this.DPTrendType = DPTrendType;
        this.maxRH = maxRH;
    //    this.maxRHTime = maxRHTime;
        this.minRH = minRH;
    //    this.minRHTime = minRHTime;
        this.RHRange = RHRange;
        this.RHTrendType = RHTrendType;
        this.primaryMorningCloudType = primaryMorningCloudType;
        this.secondaryMorningCloudType = secondaryMorningCloudType;
        this.primaryAfternoonCloudType = primaryAfternoonCloudType;
        this.secondaryAfternoonCloudType = secondaryAfternoonCloudType;
        this.primaryNightCloudType = primaryNightCloudType;
        this.secondaryNightCloudType = secondaryNightCloudType;
        this.surfaceAirWindDirection = surfaceAirWindDirection;
        this.windDirectionSummary = windDirectionSummary;
        this.windSpeed = windSpeed;
        this.maxGustSpeed = maxGustSpeed;
        this.dailyPrecipitation = dailyPrecipitation;
        this.maxHeatIndex = maxHeatIndex;
        this.minWindChill = minWindChill;
        this.upperAirWindDirection = upperAirWindDirection;
       // this.temperatureProfileQ1 = temperatureProfileQ1;
       // this.dailyPressureQ1 = dailyPressureQ1;
       // this.dailyPressureQ2 = dailyPressureQ2;
       // this.dailyHumidityQ1 = dailyHumidityQ1;
       // this.predictionsQ1 = predictionsQ1;
       // this.predictionsQ2 = predictionsQ2;
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Return whether or not the entry contains data excluding notes.
     * @return True if the entry contains data excluding notes; false otherwise.
     */
    public boolean hasData() {
        if (tempTrendType != TemperatureTrendType.DIURINAL) {
            return true;
        }
        if (BPTrendType != BarometricPressureTrendType.STEADY) {
            return true;
        }
        if (DPTrendType != DewPointTrendType.STEADY) {
            return true;
        }
        if (RHTrendType != RelativeHumidityTrendType.DIURINAL) {
            return true;
        }
        if (primaryMorningCloudType != CloudType.NA) {
            return true;
        }
        if (secondaryMorningCloudType != CloudType.NA) {
            return true;
        }
        if (primaryAfternoonCloudType != CloudType.NA) {
            return true;
        }
        if (secondaryAfternoonCloudType != CloudType.NA) {
            return true;
        }
        if (primaryNightCloudType != CloudType.NA) {
            return true;
        }
        if (secondaryNightCloudType != CloudType.NA) {
            return true;
        }
        if (windDirectionSummary != WindDirectionSummaryType.NA) {
            return true;
        }
        if (windSpeed != WindSpeedType.NA) {
            return true;
        }
        if (upperAirWindDirection != WindDirectionType.NA) {
            return true;
        }
        if (surfaceAirWindDirection != null 
                && surfaceAirWindDirection.size() > 0) {
            return true;
        }
        for (String dataString : new String[] {maxTemp, minTemp, 
            tempRange, startBP, endBP, BPRange, startDP, endDP, DPRange, maxRH, 
            minRH, RHRange, maxGustSpeed, dailyPrecipitation, maxHeatIndex, 
            minWindChill} ) {
            if (dataString != null && !dataString.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets the date of the entry.
     * @return The date the entry belongs to.
     */
    public Date getDate() {
           return entryDate;
    }

    /**
     * Sets the date of the entry.
     * @param newDate The date to set the entry to.
     */
    public void setDate(Date newDate) {
        entryDate = newDate;
    }

    /**
     * Gets the note for the entry. Note: The note in this object will only be
     * returned for students and guests. For other uses this method will
     * substitute a different <code>Note</code> made from all private instances
     * of <code>InstructorNote</code> made by the current program user on the
     * given day.
     * @param gs The <code>GeneralService</code> object currently in use by the
     * program.
     * @return The entry's note.
     */
    public Note getNote(GeneralService gs) {
        return getNote(gs.getDBMSSystem(), gs.getUser());
    }
    
    /**
     * Gets the note for the entry. Note: The note in this object will only be
     * returned for students and guests. For other uses this method will
     * substitute a different <code>Note</code> made from all private instances
     * of <code>InstructorNote</code> made by the current program user on the
     * given day.
     * @param dbms The <code>DBMSSystemManager</code> object currently in use by 
     * the program.
     * @param author The author of the note.
     * @return The entry's note.
     */
    public Note getNote(DBMSSystemManager dbms, User author) {
        //Determine if the object's note should be returned.
        UserType userType = author.getUserType();
        if (userType == UserType.student || userType == UserType.guest) {
            return entryNote;
        }
        
        //Get private instuctor notes for other users
        ArrayList<InstructorNote> privateNotes = new ArrayList<>();
        for (InstructorNote note : dbms.getNoteManager()
                .getNotesForTimespan(new java.sql.Date(entryDate.getTime()))) {
            if (note.getAccessRights() == AccessRights.Private 
                    && note.getInstructorNumber() == author.getUserNumber()) {
                privateNotes.add(note);
            }
        }
        
        //Make text for return object.
        StringBuilder noteText = new StringBuilder();
        if (privateNotes.size() > 1) {
            int count = 1;
            noteText.append("Your private notes for this location today: ");
            for (InstructorNote privateNote : privateNotes) {
                noteText.append("\n\n").append(count).append(".  ")
                        .append(privateNote.getText());
                count++;
            }
            noteText.append("\n\n");
            noteText.append("To change them, please use the class notes tab.");
        } else if (privateNotes.size() == 1) {
            noteText.append("Your private note for this location today: ");
            InstructorNote privateNote = privateNotes.get(0);
            noteText.append("\n\n").append(privateNote.getText());
            noteText.append("\n\n");
            noteText.append("To change it, please use the class notes tab.");
        } else {
            noteText.append("You have no private notes for this location ")
                    .append("today.  To add one, please use the class notes ")
                    .append("tab.");
        }
        
        //Return new object.
        return new Note(entryDate, noteText.toString());
    }
    
    /**
     * Sets the note for the entry.
     * @param newNote The new note to set.
     */
    public void setNote(Note newNote) {
        entryNote = newNote;
    }

    /**
     * Gets the maximum temperature for the entry.
     * @return The maximum temperature.
     */
    public String getMaxTemp() {
        return maxTemp;
    }
    
    /**
     * Sets the date and time when this <code>DailyEntry</code> was last 
     * modified.
     * @param lastModifiedDate The date and time this <code>DailyEntry</code>
     * was last modified.
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate =  lastModifiedDate;
    }
    
    /**
     * Gets the date and time when this <code>DailyEntry</code> was last 
     * modified.
     * @return The date and time when this <code>DailyEntry</code> was last 
     * modified.
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Sets the maximum temperature for the note.
     * @param newMaxTemp The new maximum temperature.
     */
    public void setMaxTemp(String newMaxTemp) {
        maxTemp = newMaxTemp;
    }

    /**
     * Gets the time the maximum temperature occurred.
     * @return The time of the maximum temperature.
     */
  /*  public HourlyTimeType getMaxTempTime() {
        return maxTempTime;
    }*/

    /**
     * Sets the time the maximum temperature occurred.
     * @param newMaxTempTime the new time for the maximum temperature.
     */
   /* public void setMaxTempTime(HourlyTimeType newMaxTempTime) {
        maxTempTime = newMaxTempTime;
    }*/

    /**
     * Gets the minimum temperature for the entry.
     * @return The entry's minimum temperature.
     */
    public String getMinTemp() {
        return minTemp;
    }

    /**
     * Sets the minimum temperature of the entry.
     * @param newMinTemp The new minimum temperature.
     */
    public void setMinTemp(String newMinTemp) {
        minTemp = newMinTemp;
    }

    /**
     * Gets the time the minimum temperature occurred.
     * @return The time of the minimum temperature.
     */
  /*  public HourlyTimeType getMinTempTime() {
        return minTempTime;
    }*/

    /**
     * Sets the time the minimum temperature occurred.
     * @param newMinTempTime The new time for the minimum temperature.
     */
  /*  public void setMinTempTime(HourlyTimeType newMinTempTime) {
        minTempTime = newMinTempTime;
    }*/

    /**
     * Gets the range between the minimum and maximum temperature.
     * @return The range of temperatures.
     */
    public String getTempRange() {
        return tempRange;
    }

    /**
     * Sets the temperature range.
     * @param newTempRange The new temperature range.
     */
    public void setTempRange(String newTempRange) {
        tempRange = newTempRange;
    }
/**
     * Gets the temperature trend type for the entry.
     * @return The temperature trend type.
     */
    public TemperatureTrendType getTempTrendType() {
        return tempTrendType;
    }

    /**
     * Sets the Temperature trend type.
     * @param newTempTrendType The new temperature trend type.
     */
    public void setTempTrendType(TemperatureTrendType newTempTrendType) {
        tempTrendType = newTempTrendType;
    }

    /**
     * Gets the barometric pressure at the start of the day for the entry.
     * @return The day's starting barometric pressure.
     */
    public String getStartBP() {
        return startBP;
    }

    /**
     * Sets the value of the barometric pressure at the start of day for the entry.
     * @param newValue The day's starting barometric pressure.
     */
    public void setMaxBP(String newValue) {
        startBP = newValue;
    }

    /**
     * Gets the time the maximum barometric pressure occurred.
     * @return The time of the maximum barometric pressure.
     */
 /*   public HourlyTimeType getMaxBPTime() {
        return maxBPTime;
    }*/

    /**
     * Sets the time the maximum barometric pressure occurred.
     * @param newMaxBPTime The new time of the maximum barometric pressure.
     */
   /* public void setMaxBPTime(HourlyTimeType newMaxBPTime) {
        maxBPTime = newMaxBPTime;
    }*/

    /**
     * Gets the barometric pressure at the end of the day for the entry.
     * @return The barometric pressure at the end of the day.
     */
    public String getEndBP() {
        return endBP;
    }

    /**
     * Sets the end value of the barometric pressure for the entry.
     * @param newValue The new value for the end of day barometric pressure.
     */
    public void setEndBP(String newValue) {
        endBP = newValue;
    }

    /**
     * Gets the time the minimum barometric pressure occurred.
     * @return The time of the minimum barometric pressure.
     */
   /* public HourlyTimeType getMinBPTime() {
        return minBPTime;
    }
*/
    /**
     * Sets the time of the minimum barometric pressure.
     * @param newMinBPTime The new time of the minimum barometric pressure.
     */
   /* public void setMinBPTime(HourlyTimeType newMinBPTime) {
        minBPTime = newMinBPTime;
    }*/

    /**
     * Gets the range between the minimum and maximum barometric pressure.
     * @return The range of barometric pressures.
     */
    public String getBPRange() {
        return BPRange;
    }

    /**
     * Sets the barometric pressure range.
     *
     * @param newBPRange The new barometric pressure range.
     */
    public void setBPRange(String newBPRange) {
        BPRange = newBPRange;
    }

    /**
     * Gets the barometric pressure trend type for the entry.
     * @return The barometric pressure trend type.
     */
    public BarometricPressureTrendType getBPTrendType() {
        return BPTrendType;
    }

    /**
     * Sets the barometric pressure trend type.
     * @param newBPTrendType The new barometric pressure trend type.
     */
    public void setBPTrendType(BarometricPressureTrendType newBPTrendType) {
        BPTrendType = newBPTrendType;
    }

    /**
     * Gets the dew point at the start of the day.
     * @return The entry's value for the start of day dew point.
     */
    public String getStartDP() {
        return startDP;
    }

    /**
     * Sets the dew point for the start of the day.
     * @param newValue The dew point value at the start of the day.
     */
    public void setStartDP(String newValue) {
        startDP = newValue;
    }

    /**
     * Gets the time the maximum dew point occurred.
     * @return The time of the maximum dew point.
     */
   /* public HourlyTimeType getMaxDPTime() {
        return maxDPTime;
    }*/

    /**
     * Sets the time the maximum dew point occurred.
     * @param newMaxDPTime The new time of the maximum dew point.
     */
  /*  public void setMaxDPTime(HourlyTimeType newMaxDPTime) {
        maxDPTime = newMaxDPTime;
    }*/

    /**
     * Gets the dew point at the end of the day.
     * @return The entry's value for the end of day dew point.
     */
    public String getEndDP() {
        return endDP;
    }

    /**
     * Sets the dew point for the end of day.
     * @param newValue The dew point value at the start of the day
     */
    public void setEndDP(String newValue) {
        endDP = newValue;
    }

    /**
     * Gets the time the min dew point occurred.
     * @return The time of the min dew point.
     */
   /* public HourlyTimeType getMinDPTime() {
        return minDPTime;
    }*/

    /**
     * Sets the time the min dew point occurred.
     * @param newMinDPTime The new time for the minimum dew point.
     */
   /* public void setMinDPTime(HourlyTimeType newMinDPTime) {
        minDPTime = newMinDPTime;
    }*/

    /**
     * Gets the range between the minimum and maximum dew points.
     * @return The dew point range.
     */
    public String getDPRange() {
        return DPRange;
    }

    /**
     * Sets the dew point range.
     * @param newDPRange The new dew point range.
     */
    public void setDPRange(String newDPRange) {
        DPRange = newDPRange;
    }
/**
     * Gets the Dew point trend type for the entry.
     * @return The Dew point trend type.
     */
    public DewPointTrendType getDPTrendType() {
        return DPTrendType;
    }

    /**
     * Sets the Dew point trend type.
     * @param newDPTrendType The new Dew point trend type.
     */
    public void setDPTrendType(DewPointTrendType newDPTrendType) {
        DPTrendType = newDPTrendType;
    }

    /**
     * Gets the maximum relative humidity of the entry.
     * @return The maximum relative humidity.
     */
    public String getMaxRH() {
        return maxRH;
    }

    /**
     * Sets the maximum relative humidity for the entry.
     * @param newMaxRH The new maximum relative humidity.
     */
    public void setMaxRH(String newMaxRH) {
        maxRH = newMaxRH;
    }

    /**
     * Gets the time the maximum relative humidity occurred.
     * @return The time of the maximum relative humidity.
     */
   /* public HourlyTimeType getMaxRHTime() {
        return maxRHTime;
    }*/

    /**
     * Sets the time the maximum relative humidity occurred.
     * @param newMaxRHTime The new time of the maximum relative humidity.
     */
   /* public void setMaxRHTime(HourlyTimeType newMaxRHTime) {
        maxRHTime = newMaxRHTime;
    }*/

    /**
     * Gets the minimum relative humidity of the entry.
     * @return The minimum relative humidity.
     */
    public String getMinRH() {
        return minRH;
    }

    /**
     * Sets the min relative humidity for the entry.
     * @param newMinRH The new minimum relative humidity.
     */
    public void setMinRH(String newMinRH) {
        minRH = newMinRH;
    }

    /**
     * Gets the time the min relative humidity occurred.
     * @return The time of the minimum relative humidity.
     */
   /* public HourlyTimeType getMinRHTime() {
        return minRHTime;
    }*/

    /**
     * Sets the time the minimum relative humidity occurred.
     * @param newMinRHTime The new time for the minimum relative humidity.
     */
   /* public void setMinRHTime(HourlyTimeType newMinRHTime) {
        minRHTime = newMinRHTime;
    }*/

    /**
     * Gets the range between the minimum and maximum relative humidities.
     * @return The relative humidity range.
     */
    public String getRHRange() {
        return RHRange;
    }

    /**
     * Sets the relative humidity range.
     * @param newRHRange The new relative humidity range.
     */
    public void setRHRange(String newRHRange) {
        RHRange = newRHRange;
    }
/**
     * Gets the Relative humidity trend type for the entry.
     * @return The Relative humidity  trend type.
     */
    public RelativeHumidityTrendType getRHTrendType() {
        return RHTrendType;
    }

    /**
     * Sets the Relative Humidity trend type.
     * @param newRHTrendType The new Relative humidity trend type.
     */
    public void setRHTrendType(RelativeHumidityTrendType newRHTrendType) {
        RHTrendType = newRHTrendType;
    }

    /**
     * Gets the array list for surface air wind direction.
     * @return The array list for surface air wind direction.
     */
    public ArrayList<WindDirectionType> getSurfaceAirWindDirections() {
        if (surfaceAirWindDirection != null)
            return surfaceAirWindDirection;
        return new ArrayList<WindDirectionType>();
    }

    /**
     * Sets the surface air wind direction array list.
     * @param newSurfaceAirWindDirection The new surface air wind direction
     * array list.
     */
    public void setSurfaceAirWindDirections(ArrayList<WindDirectionType> newSurfaceAirWindDirection) {
        surfaceAirWindDirection = newSurfaceAirWindDirection;
    }

    /**
     * Gets the wind direction summary.
     * @return The wind direction summary.
     */
    public WindDirectionSummaryType getWindDirectionSummary() {
        return windDirectionSummary;
    }

    /**
     * Sets the wind direction summary.
     * @param newWindDirectionSummary The new wind direction summary.
     */
    public void setWindDirectionSummary(WindDirectionSummaryType
            newWindDirectionSummary) {
        windDirectionSummary = newWindDirectionSummary;
    }

    /**
     * Gets the wind speed for the entry.
     * @return The wind speed of the entry.
     */
    public WindSpeedType getWindSpeed() {
        return windSpeed;
    }

    /**
     * Set the wind speed of the entry.
     * @param newWindSpeed The new wind speed.
     */
    public void setWindSpeed(WindSpeedType newWindSpeed) {
        windSpeed = newWindSpeed;
    }

    /**
     * Gets the max gust speed for the entry.
     * @return The max gust speed.
     */
    public String getMaxGustSpeed() {
        return maxGustSpeed;
    }

    /**
     * Sets the max gust speed for the entry.
     * @param newMaxGustSpeed The new max gust speed.
     */
    public void setMaxGustSpeed(String newMaxGustSpeed) {
        maxGustSpeed = newMaxGustSpeed;
    }

    /**
     * Gets the daily precipitation for the entry.
     * @return The daily precipitation of the entry.
     */
    public String getDailyPrecipitation() {
        return dailyPrecipitation;
    }

    /**
     * Sets the daily precipitation of the entry.
     * @param newDailyPrecipitation The new daily precipitation.
     */
    public void setDailyPrecipitation(String newDailyPrecipitation) {
        dailyPrecipitation = newDailyPrecipitation;
    }

    /**
     * Returns the maximum heat index of the entry.
     * @return The maximum heat index of the entry.
     */
    public String getMaxHeatIndex() {
        return maxHeatIndex;
    }

    /**
     * Sets the maximum heat index of the entry.
     * @param newHeatIndex The maximum heat index.
     */
    public void setMaxHeatIndex(String newHeatIndex) {
        maxHeatIndex = newHeatIndex;
    }

    /**
     * Returns the minimum wind chill for the entry.
     * @return The minimum wind chill.
     */
    public String getMinWindChill() {
        return minWindChill;
    }

    /**
     * Sets the minimum wind chill for the entry.
     * @param newMinWindChill The minimum wind chill for the entry.
     */
    public void setMinWindChill(String newMinWindChill) {
        minWindChill = newMinWindChill;
    }

    /**
     * Returns the upper air wind direction for the entry.
     * @return A <code>WindDirectionType</code> that represents the upper air wind direction.
     */
    public WindDirectionType getUpperAirWindDirection() {
        return upperAirWindDirection;
    }

    /**
     * Sets the upper air wind direction for the entry.
     * @param newUpperAirWindDirection A <code>WindDirectionType</code> that is the upper air
     *  wind direction.
     */
    public void setUpperAirWindDirection(WindDirectionType newUpperAirWindDirection) {
        upperAirWindDirection = newUpperAirWindDirection;
    }

    /**
     * Returns the primary morning cloud type.
     * @return The primary morning cloud type.
     */
    public CloudType getPrimaryMorningCloudType() {
        return primaryMorningCloudType;
    }

    /**
     * Returns the secondary morning cloud type.
     * @return The secondary morning cloud type.
     */
    public CloudType getSecondaryMorningCloudType() {
        return secondaryMorningCloudType;
    }

    /**
     * Returns the primary afternoon cloud type.
     * @return The primary afternoon cloud type.
     */
    public CloudType getPrimaryAfternoonCloudType() {
        return primaryAfternoonCloudType;
    }

    /**
     * Returns the secondary afternoon cloud type.
     * @return The secondary afternoon cloud type.
     */
    public CloudType getSecondaryAfternoonCloudType() {
        return secondaryAfternoonCloudType;
    }

    /**
     * Returns the primary nighttime cloud type.
     * @return The primary nighttime cloud type.
     */
    public CloudType getPrimaryNightCloudType() {
        return primaryNightCloudType;
    }

    /**
     * Returns the secondary nighttime cloud type.
     * @return The secondary nighttime cloud type.
     */
    public CloudType getSecondaryNightCloudType() {
        return secondaryNightCloudType;
    }
/*
    public YesNoType getTemperatureProfileQ1() {
        return temperatureProfileQ1;
    }

    public PressureChangeRelationType getDailyPressureQ1() {
        return dailyPressureQ1;
    }

    public PrecipitationChanceType getDailyPressureQ2() {
        return dailyPressureQ2;
    }

    public YesNoType getDailyHumidityQ1() {
        return dailyHumidityQ1;
    }

    public TemperaturePredictionType getPredictionsQ1() {
        return predictionsQ1;
    }

    public YesNoType getPredictionsQ2() {
        return predictionsQ2;
    }

    public void setTemperatureProfileQ1(YesNoType value) {
        temperatureProfileQ1 = value;
    }

    public void setDailyPressureQ1(PressureChangeRelationType value) {
        dailyPressureQ1 = value;
    }

    public void setDailyPressureQ2(PrecipitationChanceType value) {
        dailyPressureQ2 = value;
    }

    public void setDailyHumidityQ1(YesNoType value) {
        dailyHumidityQ1 = value;
    }

    public void setPredictionsQ1(TemperaturePredictionType value) {
        predictionsQ1 = value;
    }

    public void setPredictionsQ2(YesNoType value) {
        predictionsQ2 = value;
    }
*/
    
    /*Methods to return return resource infomatiob*/
    
    public String getCameraName() {
        return this.cameraName;
    }
    
    public String getStationName() {
        return this.stationName;
    }
    
    public int getCameraNumber() {
        return this.cameraNumber;
    }

    public int getStationNumber() {
        return this.stationNumber;
    }
    
    /**
     * The overridden equals method that compares two entries based on their
     * dates.
     * @param obj The entry to compare to.
     * @return True this entry's date is equal to the other entry's
     * date and false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DailyEntry
                && getDate().equals(((DailyEntry) obj).getDate())
                && getCameraNumber() == ((DailyEntry) obj).getCameraNumber();
    }

    /**
     * Returns the hash code.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.entryDate);
        hash = 97 * hash + this.cameraNumber;
        return hash;
    }

    /**
     * 
     * @param comparable The entry to compare to.
     * @return -1, 1, or 0 based on how the two dates compare.
     */
    @Override
    public int compareTo(Object comparable) {
        DailyEntry comparableEntry = (DailyEntry) comparable;
        if (this.equals(comparableEntry)) {
            return 0;
        }
        if (this.entryDate.getTime() < comparableEntry.entryDate.getTime()) {
            return -1;
        }
        if (this.entryDate.getTime() > comparableEntry.entryDate.getTime()) {
            return 1;
        }
        return this.cameraNumber - comparableEntry.cameraNumber;
    }

    /**
     * Converts the entry to a string. Currently just returns the date of the
     * entry.
     * @return The string representation of the entry.
     */
    @Override
    public String toString() {
        return "Entry Dete: " + this.getDate().toString() + " Camera: " 
                + this.getCameraNumber() + " - " + this.getCameraName() 
                + " Station: " + this.getStationNumber() + " - " 
                + this.getStationName();
    }
}
