package weather.common.data.note;

import java.io.Serializable;
import java.util.Date;

/**
 * A class representing a student's note. Each individual note contains a body
 * of text (description) and a date. A note is written for a particular day.
 * Student notes
 * are comparable to each other and serializable.
 *
 * @author Bloomsburg University Software Engineering
 * @author Tyler Lusby (2008)
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 * @version Spring 2010
 */
public class Note implements Comparable, Serializable {

    /**
     * noteText is the actual body of the note.
     */
    private String noteText;
    /**
     * noteDate is the date for which the note is meant to cover.
     */
    private Date noteDate;
    
    /**
    * Determines if a de-serialized file is compatible with this class.
    *
    * Not necessary to include in first version of the class, but included here
    * as a reminder of its importance. Maintainers must change this value if
    * and only if the new version of this class is not compatible with old
    * versions.
    *
    * @see <a href="http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html">Java specification for
    * serialization</a>
    * @serial
    */
    private static final long serialVersionUID = 2L;

    /**
     * Creates a new Note object with a given date and a blank text field.
     *
     * @param noteDate The date which the note is meant for.
     */
    public Note(Date noteDate) {
        this.noteDate = noteDate;
        this.noteText = "";
    }

    /**
     * Creates a new Note object with given date and text.
     *
     * @param noteDate The date of the note.
     * @param noteText The text of the note.
     */
    public Note(Date noteDate, String noteText) {
        this.noteDate = noteDate;
        this.noteText = noteText;
    }

    /**
     * Sets this note's description.
     *
     * @param noteText The new text for this note.
     */
    public void setText(String noteText) {
        this.noteText = noteText;
    }

    /**
     * Gets this note's description.
     *
     * @return This note's description.
     */
    public String getText() {
        return this.noteText;
    }

    /**
     * Gets this note's associated date.
     *
     * @return This note's associated date.
     */
    public Date getDate() {
        return this.noteDate;
    }

    /**
     * Sets this note's associated date.
     *
     * @param noteDate The new associated date.
     */
    public void setDate(Date noteDate) {
        this.noteDate = noteDate;
    }

    /**
     * Compares the date of this Note object to the date of <i>otherNote</i>. Returns
     * true if the dates are the same; false otherwise.
     *
     * @param obj The note to be compared.
     * @return True if the dates are the same, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Note &&
                    getDate().equals(((Note)obj).getDate());
    }
    /**
     * This method calculates the hash code value for this Note object.
     * @return The notes hash code value.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.noteDate != null ? this.noteDate.hashCode() : 0);
        return hash;
    }

    /**
     * Compares two student notes. If this student note's start time is before
     * the start time of <i>comparable</i>'s start time, a -1 is returned. If
     * the two start times are equal, a 0 is returned. If this student note's
     * start time is after the start time of <i>comparable</i>'s start time,
     * a 1 is returned.
     * @param comparable The Note object to compare this Note to.
     * @return The student note's start time relative to <i>comparable</i>'s start time.
     */
    @Override
    public int compareTo(Object comparable) {
        Date cmpDate = ((Note)comparable).getDate();
        if (noteDate.before(cmpDate))
            return -1;
        if (noteDate.after(cmpDate))
            return 1;
        return 0;
    }

    /**
     * Returns a string representation of this note. Currently, this is only
     * the note's description.
     * 
     * @return A string representation of this note.
     * 
     */
    @Override
    public String toString() {
        return this.noteText;
    }
    
    
}
