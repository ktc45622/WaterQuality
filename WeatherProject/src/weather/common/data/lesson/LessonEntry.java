package weather.common.data.lesson;

import java.io.Serializable;
import weather.ApplicationControlSystem;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.data.resource.WeatherResourceType;
import weather.common.utilities.Debug;

/**
 * LessonEntry contains a bookmark and the methods for accessing it for use in a
 * Lesson. ResourceID refers to the type of resource the LessonEntry is as a
 * WeatherResourceType.
 *
 * @author Justin Gamble
 * @version Spring 2012
 */
public class LessonEntry implements Serializable, Comparable<LessonEntry> {

    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Resource of the bookmark that this LessonEntry refers to.
     */
    private WeatherResourceType bookmarkResourceID;

    /**
     * The preferred location of this entry in the Lesson window.
     */
    private int preferredWindowLocation;
    /**
     * This LessonEntry's DBMS Number.
     */
    private int lessonEntryNumber;
    /**
     * The Lesson's DBMS Number that uses this LessonEntry.
     */
    private int lessonNumber;
    /**
     * The DBMS Number of the bookmark contained in this LessonEntry.
     */
    private int bookmarkNumber;
    /**
     * The actual bookmark this LessonEntry is supposed to hold, has to be
     * called from the lessonDisplay due to the way the DBMS system works.
     */
    private Bookmark bookmark;
    /**
     * The actual resource this LessonEntry is supposed to work with.
     */
    private Resource resource;
    /**
     * The name for this LessonEntry.
     */
    private String lessonEntryName;

    /**
     * Creates a LessonEntry with the given values.
     *
     * @param lessonEntryNumber The DBMS number for this LessonEntry.
     * @param name The Name for this LessonEntry.
     * @param lessonNumber The DBMS number for the Lesson this LessonEntry
     * belongs to.
     * @param bookmarkNumber The DBMS number for the Bookmark represented by
     * this LessonEntry.
     * @param resourceID The Resource of the Bookmark the LessonEntry cares
     * about.
     * @param windowLocation The preferred window location of this LessonEntry.
     */
    public LessonEntry(int lessonEntryNumber, int lessonNumber, String name, int bookmarkNumber, WeatherResourceType resourceID, int windowLocation) {
        this.lessonEntryName = name;
        this.lessonEntryNumber = lessonEntryNumber;
        this.lessonNumber = lessonNumber;
        this.bookmarkNumber = bookmarkNumber;
        bookmarkResourceID = resourceID;
        preferredWindowLocation = windowLocation;
    }

    /**
     * Creates a LessonEntry with the given values.
     *
     * @param name The Name for this LessonEntry.
     * @param bookmarkNumber The DBMS number for the Bookmark represented by
     * this LessonEntry.
     * @param resourceID The Resource of the Bookmark the LessonEntry cares
     * about.
     * @param windowLocation The preferred window location of this LessonEntry.
     */
    public LessonEntry(String name, int bookmarkNumber, WeatherResourceType resourceID, int windowLocation) {
        this.lessonEntryName = name;
        this.lessonEntryNumber = -1;
        this.lessonNumber = -1;
        this.bookmarkNumber = bookmarkNumber;
        bookmarkResourceID = resourceID;
        preferredWindowLocation = windowLocation;
    }

    /**
     * Changes the primary resource for this LessonEntry.
     *
     * @param bookmarkResourceID The new Bookmark resource that the LessonEntry
     * cares about.
     */
    public void setBookmarkResourceID(WeatherResourceType bookmarkResourceID) {
        this.bookmarkResourceID = bookmarkResourceID;
    }

    /**
     * Changes the preferredWindowLocation of this LessonEntry.
     *
     * @param preferredWindowLocation The new desired window position.
     */
    public void setPreferredWindowLocation(int preferredWindowLocation) {
        this.preferredWindowLocation = preferredWindowLocation;
    }

    /**
     * Retrieves the primary Bookmark Resource for this LessonEntry.
     *
     * @return The Resource Identifier, 1 is Camera, 2 is MapLoop, 3 is
     * StationData.
     */
    public WeatherResourceType getBookmarkResourceID() {
        return bookmarkResourceID;
    }

    /**
     * Retrieves the preferredWindowLocation for this LessonEntry in the Lesson
     * Display.
     *
     * @return The current preferred window location.
     */
    public int getPreferredWindowLocation() {
        return preferredWindowLocation;
    }

    /**
     * Retrieves the Resource that this LessonEntry should be working with.
     *
     * @return The current Resource of this LessonEntry.
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * The DBMS BookmarkNumber of the Bookmark contained in this LessonEntry.
     *
     * @return The BookmarkNumber for the Bookmark held by this LessonEntry.
     */
    public int getBookmarkNumber() {
        return bookmarkNumber;
    }

    /**
     * The DBMS LessonEntryNumber for this LessonEntry.
     *
     * @return The DBMS LessonEntryNumber for this LessonEntry.
     */
    public int getLessonEntryNumber() {
        return lessonEntryNumber;
    }

    /**
     * The DBMS LessonNumber of the Lesson that uses this LessonEntry.
     *
     * @return The DBMS LessonNumber of the Lesson this LessonEntry is a part
     * of.
     */
    public int getLessonNumber() {
        return lessonNumber;

    }

    /**
     * Gets the DBMS BookmarkNumber of the bookmark contained in this entry.
     *
     * @param bookmarkNumber The DBMS BookmarkNumber of the bookmark contained
     * in this entry.
     */
    public void setBookmarkNumber(int bookmarkNumber) {
        this.bookmarkNumber = bookmarkNumber;
    }

    /**
     * The DBMS LessonNumber of the Lesson that uses this LessonEntry.
     *
     * @param lessonNumber The new DBMS LessonNumber of the Lesson this
     * LessonEntry is a part of.
     */
    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    /**
     * Compares LessonEntry types based on the LessonEntryNumber.
     *
     * @param obj The object to compare to this one.
     * @return True if the given object is equal to this one, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final LessonEntry other = (LessonEntry) obj;

        return (lessonEntryNumber == other.getLessonEntryNumber());
    }

    /**
     * Calculates the hash code value, which is just the LessonEntryNumber.
     *
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return (lessonEntryNumber);
    }

    @Override
    public String toString() {
        return "LessonEntry : " + lessonEntryName + " is a LessonEntry of Lesson : " + lessonNumber;
    }

    /**
     * Gets this LessonEntry's name.
     *
     * @return String representing the name of this LessonEntry.
     */
    public String getLessonEntryName() {
        return lessonEntryName;
    }

    /**
     * Sets this LessonEntry's name.
     *
     * @param lessonEntryName The new name of this LessonEntry.
     */
    public void setLessonEntryName(String lessonEntryName) {
        this.lessonEntryName = lessonEntryName;
    }

    /**
     * Sets the LessonEntryNumber of this LessonEntry.
     *
     * @param lessonEntryNumber The new LessonEntryNumber for this LessonEntry.
     */
    public void setLessonEntryNumber(int lessonEntryNumber) {
        this.lessonEntryNumber = lessonEntryNumber;
    }

    public ImageInstance getResourceImage() {
        ImageInstance ret = null;
        switch (bookmarkResourceID) {
            case WeatherCamera:
                ret = bookmark.getWeatherCameraPicture();
                break;
            case WeatherMapLoop:
                ret = bookmark.getWeatherMapPicture();
                break;
            case WeatherStation:
                ret = bookmark.getWeatherStationPicture();
                break;
            default:
                Debug.println("Resource isn't a correct type.");
                break;
        }
        return ret;
    }

    public boolean isImageEntry() {
        boolean ret = true;
        if (!bookmark.getStartTime().equals(bookmark.getEndTime()) && (bookmarkResourceID == WeatherResourceType.WeatherCamera || bookmarkResourceID == WeatherResourceType.WeatherMapLoop)) {
            ret = false;
        }

        return ret;
    }

    public ResourceRange getResourceRange() {
        return new ResourceRange(bookmark.getStartTime(), bookmark.getEndTime());
    }

    /**
     * Sets the contained bookmark by the bookmark number. Used for ease of
     * access to the bookmark
     *
     * @param apc The application control system for the accessing window.
     */
    public void setBookmark(ApplicationControlSystem apc) {
        Debug.println("LessonEntry bookmarkResourceID = " + bookmarkResourceID);
        this.bookmark = apc.getDBMSSystem().getBookmarkManager().searchByBookmarkNumber(bookmarkNumber);
        int resourceNumber = 0;
        switch (bookmarkResourceID) {
            case WeatherCamera:
                resourceNumber = bookmark.getWeatherCameraResourceNumber();
                break;
            case WeatherMapLoop:
                resourceNumber = bookmark.getWeatherMapLoopResourceNumber();
                break;
            case WeatherStation:
                resourceNumber = bookmark.getWeatherStationResourceNumber();
                break;
            default:
                Debug.println("Resource isn't a correct type.");
                break;
        }

        this.resource = apc.getDBMSSystem().getResourceManager().getWeatherResourceByNumber(resourceNumber);
    }

    /**
     * Checks to see if the bookmark being requested is null or not.
     *
     * @param apc The application control system for the accessing window.
     * @return true if the bookmark is null, false otherwise.
     */
    public boolean checkBookmarkIsNull(ApplicationControlSystem apc) {
        Debug.println("Checking if bookmark is null.");
        bookmark = apc.getDBMSSystem().getBookmarkManager().searchByBookmarkNumber(bookmarkNumber);
        return bookmark == null;
    }

    /**
     * Compare two LessonEntries so they can be sorted by display order.
     *
     * @param other The Object this is being compared to.
     * @return The comparison of this LessonEntry with the Other: Negative means
     * this is lower. Zero means they are equal, or the objects could not be
     * compared. Positive means this is higher.
     */
    @Override
    public int compareTo(LessonEntry other) {

        if (this.preferredWindowLocation == other.preferredWindowLocation) {
            return this.bookmarkNumber - other.bookmarkNumber;
        } else {
            return this.preferredWindowLocation - other.preferredWindowLocation;
        }
    }
}
