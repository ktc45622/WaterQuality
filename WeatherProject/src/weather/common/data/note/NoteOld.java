package weather.common.data.note;

import java.sql.Date;
import java.io.Serializable;

/**
 * This class is NOT used. The only "note" class used is Note.java. It is being
 * kept here until it is sure this class is not needed. No code was touched
 * by the 2009 class.
 *
 * A class representing a local note. Each individual note
 * contains a body of text (description) and a time range (startTime and 
 * endTime). Student notes are comparable to each other and serializable.
 * These are stored locally, location specified by the properties file.
 *
 * @author Bloomsburg University Software Engineering
 * @author Tyler Lusby (2008)
 * @version Spring 2008
 */
public class NoteOld implements Comparable, Serializable {
    /**
     * Description is the actual body of the note.
     */
    private String description;
    /**
     * day is the beginning time for the time range this note covers.
     */
    private Date day;
    /**
     * timestamp is the ending time for the time range this note covers.
     */
    private Date timestamp;
    /**
     * serialVersionUID is for the serializability of this object.
     */
    static final long serialVersionUID = 2L;

    /**
     * Creates a new LocalNote object with a given date and time and a blank
     * description.
     *
     * @param day - The date the note is created on.
     * @param timestamp - The time the note is created.
     */
    public NoteOld(Date day, Date timestamp) {
        this.day = day;
        this.timestamp = timestamp;
        this.description = "";
    }

    /**
     * Sets this LocalNote's description.
     *
     * @param description  The new description for this note.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets this LocalNote's description.
     *
     * @return description This LocalNote's description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets this LocalNote's day.
     *
     * @return day - This LocalNote's day.
     */
    public Date getDay() {
        return this.day;
    }

    /**
     * Sets this LocalNote's start time.
     *
     * @param day The start time of the note
     */
    public void setStartTime(Date day) {
        this.day = day;
    }

    /**
     * Gets this LocalNote's timestamp.
     *
     * @return timestamp - This LocalNote's timestamp.
     */
    public Date getTimeStamp() {
        return this.timestamp;
    }

    /**
     * Sets this LocalNote's timestamp.
     *
     * @param timestamp - The new timestamp of the note.
     */
    public void setTimeStamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Compares two student notes. If this LocalNote's start time is before the
     * start time of <i>comparable</i>'s start time, a -1 is returned. If the
     * two start times are equal, a 0 is returned. If this LocalNote's start
     * time is after the start time of <i>comparable</i>'s start time, a 1 is
     * returned.
     *
     * @param comparable - The LocalNote object to compare this NoteOld to.
     * @return An integer value describing this LocalNote's start time relative
     *      to <i>comparable</i>'s start time.
     *
     *
     */
    //@Override
    public int compareTo(Object comparable) {
        if (this.day.before(((NoteOld)comparable).getTimeStamp())) {
            return -1;
        }
        if (this.day.equals(((NoteOld)comparable).getTimeStamp())) {
            return 0;
        }
        return 1;
    }

    /**
     * Returns a string representation of this note. Currently, this is only the
     * note's description.
     *
     * @return description - A string representation of this note.
     * */
    @Override
    public String toString() {
        return this.description;
    }
}
