package weather.common.data.note;

import java.io.Serializable;
import weather.common.data.InstructorDataType;
import weather.common.data.InstructorFileInstance;

/**
 * This class stores the information about a file that an instructor might want
 * to keep with one of the instructor data types: Notes, Lessons, Bookmarks,
 * Events, and Private. An instructor would have to attach the file to a note
 * he/she created. The information about the file is kept in the database.
 *
 * @author cjones
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public class NoteFileInstance extends InstructorFileInstance implements Serializable {
    
    private static final long serialVersionUID = 1;

    /**
     * Creates an instance of the NoteFileInstance class with the given note
     * file number, note number, instructor number, file name, and file
     * contents.
     *
     * @param noteFileNumber The number of this file.
     * @param noteNumber The number of the note this file is attached to.
     * @param instructorNumber The number of the instructor using this file.
     * @param fileName The name of this file.
     * @param file The contents of this file.
     */
    public NoteFileInstance(int noteFileNumber, int noteNumber,
            int instructorNumber, String fileName, byte[] file) {
        super(noteFileNumber, instructorNumber, InstructorDataType.Notes,
                noteNumber, fileName, file);
    }

    /**
     * Retrieves the number of the note this file is attached to.
     *
     * @return The note number.
     */
    public int getNoteNumber() {
        return super.getDataNumber();
    }

    /**
     * Sets the number of the note this file is attached to.
     *
     * @param noteNumber The note number to be set.
     */
    public void setNoteNumber(int noteNumber) {
        super.setDataNumber(noteNumber);
    }

    /**
     * Overridden equals method.
     *
     * @param o The other object.
     * @return True if and only if other object is a notes file instance and its filename
     *      is the same as this filename
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof NoteFileInstance &&
                ((NoteFileInstance)o).getFileName().equals(getFileName()));
    }

    /**
     * Calculate hash code.
     * 
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return getFileName().hashCode();
    }
}
