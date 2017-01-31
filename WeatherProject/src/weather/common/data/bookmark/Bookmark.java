package weather.common.data.bookmark;

import java.io.Serializable;
import java.sql.Date;
import weather.common.data.AccessRights;
import weather.common.data.resource.ImageInstance;

/**
 * Represents a bookmark instance(a single point in time) or event (a time
 * range).
 *
 * @author Joseph Horro
 * @author Justin Enslin
 * @version Spring 2012
 */
public class Bookmark implements Serializable {
    // fields match database columns

    // Constants for use in lessons
    public static final int USE_WEATHER_CAMERA = 0;
    public static final int USE_WEATHER_MAP = 1;
    public static final int USE_DATAPLOT = 2;
    /**
     * Auto generated number and primary key for the bookmarks table.
     */
    private int bookmarkNumber;
    /**
     * Determines whether this is an instance or event.
     */
    private BookmarkDuration type;
    /**
     * Foreign Key to the bookmark_categories table.
     */
    private int categoryNumber;
    /**
     * Foreign Key to the bookmark_types table.
     */
    private int typeNumber;
    /**
     * The name of the bookmark - limited to 50 characters (Varchar(50)).
     */
    private String name;
    /**
     * Foreign Key to the users table. The user who created this bookmark.
     */
    private int createdBy;
    /**
     * The access type of this bookmark. This is the same access type used for
     * notes and thus shares a class.
     */
    private AccessRights accessRights;
    /**
     * The start time of this bookmark - accurate to the hour.
     */
    private Date startTime;
    /**
     * The start time of this bookmark - accurate to the hour.
     */
    private Date endTime;
    /**
     * Defaults to not ranked.
     */
    private BookmarkRank ranking;
    /**
     * Resource number of the camera currently in the window. -1 for none.
     */
    private int weatherCameraResourceNumber;
    /**
     * Resource number of the weather map loop currently in the window. -1 for
     * none.
     */
    private int weatherMapLoopResourceNumber;
    /**
     * Resource number of the weather station currently in the window. -1 for
     * none.
     */
    private int weatherStationResourceNumber;
    /**
     * A picture from the weather camera resource. This may be null. Stored in
     * the database as a medium blob.
     */
    private ImageInstance weatherCameraPicture;
    /**
     * A picture from the weather map resource. This may be null. Stored in the
     * database as a medium blob.
     */
    private ImageInstance weatherMapPicture;
    /**
     * A picture from the weather station resource. This may be null. Stored in
     * the database as a medium blob.
     */
    private ImageInstance weatherStationPicture;
    /**
     * The notes for this bookmark. Limited to 250 characters (Varchar(250)).
     * May be null.
     */
    private String notes;
    /**
     * The start time of the data plot resource range. This is the supplied
     * range, not the plotted range.
     */
    private Date plotRangeStartTime;
    /**
     * The end time of the data plot resource range. This is the supplied range,
     * not the plotted range.
     */
    private Date plotRangeEndTime;
    /**
     * The fitted/default option of the data plot resource range.
     */
    private boolean isGraphFitted;
    /**
     * The text of the radio button that specifies the graph on the data plot.
     */
    private String selectedRadioName;
    /**
     * An integer that indicates the selection of the data plot day span combo
     * box.
     */
    private int plotDaySpanComboBoxIndex;
    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Not necessary to include in first version of the class, but included here
     * as a reminder of its importance. Maintainers must change this value if
     * and only if the new version of this class is not compatible with old
     * versions.
     *
     * @see <a href="http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html">Sun documentation for serialization
     * for details.</a>
     *
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for making a new Bookmark instance. This constructor is meant
     * to be used with database information as the bookmarkNumber is an auto-
     * generated field.
     *
     * @param bookmarkNumber Auto generated number and primary key for the
     * bookmarks table.
     * @param categoryNumber Foreign Key to the bookmark_categories table.
     * @param typeNumber Foreign Key to the bookmark_types table.
     * @param name The name of the bookmark - limited to 50 characters
     * (Varchar(50)).
     * @param createdBy Foreign Key to the users table. The user who created
     * this bookmark.
     * @param accessRights The access type of this bookmark. This is the same
     * access type used for notes and thus shares a class.
     * @param startTime The start time of this bookmark.
     * @param ranking The rank of this resource. i.e. average or good
     * @param weatherCameraID Resource number of the camera currently in the
     * window. -1 for none.
     * @param weatherMapLoopID Resource number of the weather map loop currently
     * in the window. -1 for none.
     * @param weatherStationID Resource number of the weather station currently
     * in the window. -1 for none.
     * @param weatherCameraPicture A picture from the weather camera resource.
     * This may be null.
     * @param weatherMapPicture A picture from the weather map resource. This
     * may be null.
     * @param weatherStationPicture A picture from the weather station resource.
     * This may be null.
     * @param notes The notes for this bookmark. Limited to 250 characters
     * (Varchar(250)). May be null.
     * @param plotRangeStartTime The start time of the data plot resource range.
     * This is the supplied range, not the plotted range.
     * @param plotRangeEndTime The end time of the data plot resource range.
     * This is the supplied range, not the plotted range.
     * @param isGraphFitted A boolean indicating whether or not the fitted
     * option is selected for the bookmark's data plot.
     * @param selectedRadioName The text of the radio button that specifies the
     * graph on the bookmark's data plot.
     * @param plotDaySpanComboBoxIndex An integer that indicates the selection
     * of the data plot's day span combo box.
     */
    public Bookmark(int bookmarkNumber, int categoryNumber, int typeNumber,
            String name, int createdBy, AccessRights accessRights,
            Date startTime, BookmarkRank ranking,
            int weatherCameraID, int weatherMapLoopID, int weatherStationID,
            ImageInstance weatherCameraPicture, ImageInstance weatherMapPicture,
            ImageInstance weatherStationPicture, String notes,
            Date plotRangeStartTime, Date plotRangeEndTime,
            boolean isGraphFitted, String selectedRadioName,
            int plotDaySpanComboBoxIndex) {
        this.type = BookmarkDuration.instance;
        this.bookmarkNumber = bookmarkNumber;
        this.categoryNumber = categoryNumber;
        this.typeNumber = typeNumber;
        this.name = name;
        this.createdBy = createdBy;
        this.accessRights = accessRights;
        this.startTime = startTime;
        this.endTime = startTime;
        this.ranking = ranking;
        this.weatherCameraResourceNumber = weatherCameraID;
        this.weatherMapLoopResourceNumber = weatherMapLoopID;
        this.weatherStationResourceNumber = weatherStationID;
        this.weatherCameraPicture = weatherCameraPicture;
        this.weatherMapPicture = weatherMapPicture;
        this.weatherStationPicture = weatherStationPicture;
        this.notes = notes;
        this.plotRangeStartTime = plotRangeStartTime;
        this.plotRangeEndTime = plotRangeEndTime;
        this.isGraphFitted = isGraphFitted;
        this.selectedRadioName = selectedRadioName;
        this.plotDaySpanComboBoxIndex = plotDaySpanComboBoxIndex;
    }

    /**
     * Constructor for making a new Bookmark instance or event. It is to be used
     * to create a "blank" instance to be filled with data by the program. It
     * has a default type of bookmark instance.
     */
    public Bookmark() {
        this.type = BookmarkDuration.instance;
        this.bookmarkNumber = -1;
        this.categoryNumber = -1;
        this.typeNumber = -1;
        this.name = null;
        this.createdBy = -1;
        this.accessRights = null;
        this.startTime = null;
        this.endTime = null;
        this.ranking = null;
        this.weatherCameraResourceNumber = -1;
        this.weatherMapLoopResourceNumber = -1;
        this.weatherStationResourceNumber = -1;
        this.weatherCameraPicture = null;
        this.weatherMapPicture = null;
        this.weatherStationPicture = null;
        this.notes = null;
        this.plotRangeStartTime = null;
        this.plotRangeEndTime = null;
        this.isGraphFitted = false;
        this.selectedRadioName = null;
        this.plotDaySpanComboBoxIndex = -1;
    }

    /**
     * Constructor for making a new Bookmark event. This constructor is meant to
     * be used with database information as the bookmarkNumber is an auto-
     * generated field.
     *
     * @param bookmarkNumber Auto generated number and primary key for the
     * bookmarks table.
     * @param categoryNumber Foreign Key to the bookmark_categories table.
     * @param typeNumber Foreign Key to the bookmark_types table.
     * @param name The name of the event - limited to 50 characters
     * (Varchar(50)).
     * @param createdBy Foreign Key to the users table. The user who created
     * this event.
     * @param accessRights The access type of this event. This is the same
     * access type used for notes and thus shares a class.
     * @param startTime The start time of this event.
     * @param endTime The end time of this event.
     * @param ranking The rank of this resource. i.e. average or good
     * @param weatherCameraID Resource number of the camera currently in the
     * window. -1 for none.
     * @param weatherMapLoopID Resource number of the weather map loop currently
     * in the window. -1 for none.
     * @param weatherStationID Resource number of the weather station currently
     * in the window. -1 for none.
     * @param weatherCameraPicture A picture from the weather camera resource.
     * This may be null.
     * @param weatherMapPicture A picture from the weather map resource. This
     * may be null.
     * @param weatherStationPicture A picture from the weather station resource.
     * This may be null.
     * @param notes The notes for this event. Limited to 250 characters
     * (Varchar(250)). May be null.
     * @param plotRangeStartTime The start time of the data plot resource range.
     * This is the supplied range, not the plotted range.
     * @param plotRangeEndTime The end time of the data plot resource range.
     * This is the supplied range, not the plotted range.
     * @param isGraphFitted A boolean indicating whether or not the fitted
     * option is selected for the event's data plot.
     * @param selectedRadioName The text of the radio button that specifies the
     * graph on the event's data plot.
     * @param plotDaySpanComboBoxIndex An integer that indicates the selection
     * of the data plot's day span combo box.
     */
    public Bookmark(int bookmarkNumber, int categoryNumber, int typeNumber,
            String name, int createdBy, AccessRights accessRights,
            Date startTime, Date endTime, BookmarkRank ranking,
            int weatherCameraID, int weatherMapLoopID, int weatherStationID,
            ImageInstance weatherCameraPicture, ImageInstance weatherMapPicture,
            ImageInstance weatherStationPicture, String notes,
            Date plotRangeStartTime, Date plotRangeEndTime,
            boolean isGraphFitted, String selectedRadioName,
            int plotDaySpanComboBoxIndex) {
        this.type = BookmarkDuration.event;
        this.bookmarkNumber = bookmarkNumber;
        this.categoryNumber = categoryNumber;
        this.typeNumber = typeNumber;
        this.name = name;
        this.createdBy = createdBy;
        this.accessRights = accessRights;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ranking = ranking;
        this.weatherCameraResourceNumber = weatherCameraID;
        this.weatherMapLoopResourceNumber = weatherMapLoopID;
        this.weatherStationResourceNumber = weatherStationID;
        this.weatherCameraPicture = weatherCameraPicture;
        this.weatherMapPicture = weatherMapPicture;
        this.weatherStationPicture = weatherStationPicture;
        this.notes = notes;
        this.plotRangeStartTime = plotRangeStartTime;
        this.plotRangeEndTime = plotRangeEndTime;
        this.isGraphFitted = isGraphFitted;
        this.selectedRadioName = selectedRadioName;
        this.plotDaySpanComboBoxIndex = plotDaySpanComboBoxIndex;
    }

    /**
     * A constructor that creates a copy of an existing bookmark.
     *
     * @param original The bookmark to copy.
     */
    public Bookmark(Bookmark original) {
        this.bookmarkNumber = original.bookmarkNumber;
        this.type = original.type;
        this.categoryNumber = original.categoryNumber;
        this.typeNumber = original.typeNumber;
        this.name = original.name;
        this.createdBy = original.createdBy;
        this.accessRights = original.accessRights;
        this.startTime = original.startTime;
        this.endTime = original.endTime;
        this.ranking = original.ranking;
        this.weatherCameraResourceNumber = original.weatherCameraResourceNumber;
        this.weatherMapLoopResourceNumber = original.weatherMapLoopResourceNumber;
        this.weatherStationResourceNumber = original.weatherStationResourceNumber;
        this.weatherCameraPicture = original.weatherCameraPicture;
        this.weatherMapPicture = original.weatherMapPicture;
        this.weatherStationPicture = original.weatherStationPicture;
        this.notes = original.notes;
        this.plotRangeStartTime = original.plotRangeStartTime;
        this.plotRangeEndTime = original.plotRangeEndTime;
        this.isGraphFitted = original.isGraphFitted;
        this.selectedRadioName = original.selectedRadioName;
        this.plotDaySpanComboBoxIndex = original.plotDaySpanComboBoxIndex;
    }

    /**
     * Compares bookmark types based on the bookmarkNumber.
     *
     * @param obj The object to compare to this one.
     * @return True if the given object is equal to this one, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Bookmark other = (Bookmark) obj;

        return (bookmarkNumber == other.bookmarkNumber);
    }

    @Override
    public String toString() {
        return "Bookmark Number " + bookmarkNumber + ": " + name;
    }

    /**
     * Calculates the hash code value, which is just the bookmarkNumber.
     *
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return (bookmarkNumber);
    }

    /**
     * Get the access rights of this bookmark.
     *
     * @return The access rights of this bookmark.
     */
    public AccessRights getAccessRights() {
        return accessRights;
    }

    /**
     * Sets the access type of this bookmark. This is the same access type used
     * for notes and thus shares a class.
     *
     * @param accessRights The access rights to be set for this bookmark.
     */
    public void setAccessRights(AccessRights accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * Gets bookmarks number.
     *
     * @return The bookmarks number.
     */
    public int getBookmarkNumber() {
        return bookmarkNumber;
    }

    /**
     * Sets the bookmarks number of this bookmark.
     *
     * @param bookmarkNumber The number for the bookmark to be set to.
     */
    public void setBookmarkNumber(int bookmarkNumber) {
        this.bookmarkNumber = bookmarkNumber;
    }

    /**
     * Gets the category number of the bookmark.
     *
     * @return The category number of the bookmark.
     */
    public int getCategoryNumber() {
        return categoryNumber;
    }

    /**
     * Sets the category number of the bookmark.
     *
     * @param categoryNumber The number to set as the category number.
     */
    public void setCategoryNumber(int categoryNumber) {
        this.categoryNumber = categoryNumber;
    }

    /**
     * Get the user that created this bookmark.
     *
     * @return The number of the user who created this bookmark.
     */
    public int getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets who created this bookmark.
     *
     * @param createdBy The number of the user who created this bookmark.
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the name of the bookmark.
     *
     * @return The name of the bookmark.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the bookmark. -Limited to 50 characters (Varchar(50))
     *
     * @param name The String to set as the bookmarks name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the notes of the bookmark.
     *
     * @return The notes of the bookmark.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes of the bookmark. -Limited to 250 characters
     * (Varchar(250)).
     *
     * @param notes The String to set as the notes for this Bookmark.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets the rank of this resource. i.e. average or good
     *
     * @return The rank of this resource.
     */
    public BookmarkRank getRanking() {
        return ranking;
    }

    /**
     * Sets rank of this resource. i.e. average or good
     *
     * @param ranking The ranking to set for this resource.
     */
    public void setRanking(BookmarkRank ranking) {
        this.ranking = ranking;
    }

    /**
     * Gets the start time of this bookmark - accurate to the hour.
     *
     * @return The start time of this bookmark.
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of this bookmark - accurate to the hour.
     *
     * @param startTime The start time to set for this bookmark.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the start time of this bookmark - accurate to the hour.
     *
     * @return The start time of this bookmark.
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Sets the start time of this bookmark - accurate to the hour.
     *
     * @param endTime The start time of this bookmark.
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the type number of the bookmark.
     *
     * @return The type number of the bookmark.
     */
    public int getTypeNumber() {
        return typeNumber;
    }

    /**
     * Sets the type number of the bookmark.
     *
     * @param typeNumber The number to set as the type number of the bookmark.
     */
    public void setTypeNumber(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    /**
     * Gets the resource number of the camera currently in the window. -1 for
     * none.
     *
     * @return The resource number of the camera currently in the window.
     */
    public int getWeatherCameraResourceNumber() {
        return weatherCameraResourceNumber;
    }

    /**
     * Sets the resource number of the camera currently in the window.
     *
     * @param weatherCameraResourceNumber The number to set as the weather
     * camera ID.
     */
    public void setWeatherCameraResourceNumber(int weatherCameraResourceNumber) {
        this.weatherCameraResourceNumber = weatherCameraResourceNumber;
    }

    /**
     * Gets the picture from the weather camera resource.
     *
     * @return The picture from the weather camera resource.
     */
    public ImageInstance getWeatherCameraPicture() {
        return weatherCameraPicture;
    }

    /**
     * Sets the picture from the weather camera resource.
     *
     * @param weatherCameraPicture A picture from the weather camera resource.
     */
    public void setWeatherCameraPicture(ImageInstance weatherCameraPicture) {
        this.weatherCameraPicture = weatherCameraPicture;
    }

    /**
     * Gets the resource number of the weather map loop currently in the window.
     * -1 means none.
     *
     * @return The resource number of the weather map loop currently in the
     * window.
     */
    public int getWeatherMapLoopResourceNumber() {
        return weatherMapLoopResourceNumber;
    }

    /**
     * Sets the resource number of the weather map loop currently in the window.
     *
     * @param weatherMapLoopResourceNumber The number to set as the resource
     * number of the weather map loop.
     */
    public void setWeatherMapLoopResourceNumber(int weatherMapLoopResourceNumber) {
        this.weatherMapLoopResourceNumber = weatherMapLoopResourceNumber;
    }

    /**
     * Gets the weather map picture from the bookmark.
     *
     * @return The weather map picture from the bookmark.
     */
    public ImageInstance getWeatherMapPicture() {
        return weatherMapPicture;
    }

    /**
     * Sets the picture from the weather map resource.
     *
     * @param weatherMapPicture The weather map picture to be set.
     */
    public void setWeatherMapPicture(ImageInstance weatherMapPicture) {
        this.weatherMapPicture = weatherMapPicture;
    }

    /**
     * Gets the resource number of the weather station currently in the window.
     *
     * @return The resource number of the weather station currently in the
     * window.
     */
    public int getWeatherStationResourceNumber() {
        return weatherStationResourceNumber;
    }

    /**
     * Sets the resource number of the weather station currently in the window.
     *
     * @param weatherStationResourceNumber The number to set as the weather
     * stations ID.
     */
    public void setWeatherStationResourceNumber(int weatherStationResourceNumber) {
        this.weatherStationResourceNumber = weatherStationResourceNumber;
    }

    /**
     * Gets the picture from the weather station resource.
     *
     * @return The picture from the weather station resource.
     */
    public ImageInstance getWeatherStationPicture() {
        return weatherStationPicture;
    }

    /**
     * Sets the picture from the weather station resource.
     *
     * @param weatherStationPicture The picture to set as the weather station
     * picture.
     */
    public void setWeatherStationPicture(ImageInstance weatherStationPicture) {
        this.weatherStationPicture = weatherStationPicture;
    }

    /**
     * Gets the type of this bookmark(instance/event).
     *
     * @return The type of this bookmark.
     */
    public BookmarkDuration getType() {
        return type;
    }

    /**
     * Sets the type of this bookmark(instance/event).
     *
     * @param type The <code>BookmarkDuration</code> to set this bookmark to.
     */
    public void setType(BookmarkDuration type) {
        this.type = type;
    }

    /**
     * Gets the start time of the data plot resource range. This is the supplied
     * range, not the plotted range.
     *
     * @return The start time of the data plot range for bookmarks.
     */
    public Date getPlotRangeStartTime() {
        return plotRangeStartTime;
    }

    /**
     * Sets the start time of the data plot resource range for bookmarks. This
     * is the supplied range, not the plotted range.
     *
     * @param startTime The time at which the start of an bookmark's data plot
     * range is to be set.
     */
    public void setPlotRangeStartTime(Date startTime) {
        plotRangeStartTime = startTime;
    }

    /**
     * Gets the end time of the data plot resource range for bookmarks. This is
     * the supplied range, not the plotted range.
     *
     * @return The end time of the data plot range for bookmarks.
     */
    public Date getPlotRangeEndTime() {
        return plotRangeEndTime;
    }

    /**
     * Sets the end time of the data plot resource range for bookmarks. This is
     * the supplied range, not the plotted range.
     *
     * @param endTime The time at which the end of an bookmark's data plot range
     * is to be set.
     */
    public void setPlotRangeEndTime(Date endTime) {
        plotRangeEndTime = endTime;
    }

    /**
     * Gets the graph fitted option of the data plot for bookmarks.
     *
     * @return The data plot range fitted option for bookmarks.
     */
    public boolean getGraphFittedOption() {
        return isGraphFitted;
    }

    /**
     * Sets the graph fitted option of the data plot for bookmarks.
     *
     * @param isGraphFitted The state to which the fitted option of an
     * bookmark's data plot is to be set.
     */
    public void setGraphFittedOption(boolean isGraphFitted) {
        this.isGraphFitted = isGraphFitted;
    }

    /**
     * Gets the selected graph of the data plot for bookmarks. The returned
     * value is the text of the radio button that was selected to pick the
     * graph.
     *
     * @return The text of the radio button that was selected to pick the graph
     * for bookmark.
     */
    public String getSelectedRadioName() {
        return selectedRadioName;
    }

    /**
     * Sets the selected graph of the data plot for bookmarks. The supplied
     * value must be the text of the radio button that is to be selected to pick
     * the graph.
     *
     * @param selectedRadioName The text of the radio button that specifies an
     * bookmark's data plot variable(s).
     */
    public void setSelectedRadioName(String selectedRadioName) {
        this.selectedRadioName = selectedRadioName;
    }

    /**
     * Gets a private array index indicative of the day span combo box selection
     * of the data plot for bookmarks.
     *
     * @return A private array index indicative of the day span combo box
     * selection of the data plot for bookmarks.
     */
    public int getPlotDaySpanComboBoxIndex() {
        return plotDaySpanComboBoxIndex;
    }

    /**
     * Sets the day span combo box selection of the data plot for bookmarks. The
     * combo box is set by specifying the index of a private array that
     * corresponds to the desired selection.
     *
     * @param plotDaySpanComboBoxIndex The index of a private array that
     * corresponds to the desired selection of the day span combo box of an
     * bookmark's data plot.
     */
    public void setPlotDaySpanComboBoxIndex(int plotDaySpanComboBoxIndex) {
        this.plotDaySpanComboBoxIndex = plotDaySpanComboBoxIndex;
    }
}
