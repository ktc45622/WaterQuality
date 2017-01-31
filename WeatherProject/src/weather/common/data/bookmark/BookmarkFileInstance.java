package weather.common.data.bookmark;

import java.io.Serializable;
import weather.common.data.InstructorDataType;
import weather.common.data.InstructorFileInstance;

/**
 * This class stores the information about a file that an instructor might want
 * to keep with one of the instructor data types: Notes, Lessons, Bookmarks,
 * Events, and Private. An instructor would have to attach the file to a bookmark
 * he/she created. The information about the file is kept in the database.
 * 
 * The code for this class was taken and modified from NoteFileInstance.
 * 
 * @author John Lenhart
 * @version Spring 2012
 */
public class BookmarkFileInstance extends InstructorFileInstance implements Serializable {
    
    private static final long serialVersionUID = 1;

    /**
     * Creates an instance of the BookmarkFileInstance class with the given bookmark
     * file number, bookmark number, instructor number, file name, and file
     * contents.
     *
     * @param bookmarkFileNumber The number of this file.
     * @param bookmarkNumber The number of the bookmark this file is attached to.
     * @param instructorNumber The number of the instructor using this file.
     * @param fileName The name of this file.
     * @param file The contents of this file.
     */
    public BookmarkFileInstance(int bookmarkFileNumber, int bookmarkNumber,
            int instructorNumber, String fileName, byte[] file) {
        super(bookmarkFileNumber, instructorNumber, InstructorDataType.Bookmarks,
                bookmarkNumber, fileName, file);
    }

    /**
     * Retrieves the number of the bookmark this file is attached to.
     *
     * @return The bookmark number.
     */
    public int getBookmarkNumber() {
        return super.getDataNumber();
    }

    /**
     * Sets the number of the bookmark this file is attached to.
     *
     * @param bookmarkNumber The bookmark number to be set.
     */
    public void setBookmarkNumber(int bookmarkNumber) {
        super.setDataNumber(bookmarkNumber);
    }

    /**
     * Overridden equals method.
     *
     * @param o The other object.
     * @return True if and only if other object is a bookmarks file instance and its filename
     *      is the same as this filename.
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof BookmarkFileInstance &&
                ((BookmarkFileInstance)o).getFileName().equals(getFileName()));
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
