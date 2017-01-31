package weather.common.data.lesson;

import java.io.Serializable;
import weather.common.data.InstructorDataType;
import weather.common.data.InstructorFileInstance;

/**
 * This class stores the information about a file that an instructor might want
 * to keep with one of the instructor data types: Notes, Lessons, Bookmarks,
 * Events, and Private. An instructor would have to attach the file to a Lesson
 * he/she created. The information about the file is kept in the database.
 * 
 * The code for this class was taken and modified from NoteFileInstance.
 * 
 * @author John Lenhart
 * @version Spring 2012
 */
public class LessonFileInstance extends InstructorFileInstance implements Serializable {
    
    private static final long serialVersionUID = 1;

    /**
     * Creates an instance of the LessonFileInstance class with the given lesson
     * file number, lesson number, instructor number, file name, and file
     * contents.
     *
     * @param lessonFileNumber The number of this file.
     * @param lessonNumber The number of the lesson this file is attached
     * to.
     * @param instructorNumber The number of the instructor using this file.
     * @param fileName The name of this file.
     * @param file The contents of this file.
     */
    public LessonFileInstance(int lessonFileNumber, int lessonNumber,
            int instructorNumber, String fileName, byte[] file) {
        super(lessonFileNumber, instructorNumber, InstructorDataType.InstructionalLessons,
                lessonNumber, fileName, file);
    }

    /**
     * Retrieves the number of the lesson this file is attached to.
     *
     * @return The lesson number.
     */
    public int getLessonNumber() {
        return super.getDataNumber();
    }

    /**
     * Sets the number of the lesson this file is attached to.
     *
     * @param lessonNumber The lesson number to be set.
     */
    public void setLessonNumber(int lessonNumber) {
        super.setDataNumber(lessonNumber);
    }
    
    /**
     * Overridden equals method.
     *
     * @param o The other object.
     * @return True if and only if other object is a lesson file instance and
     * its filename is the same as this filename.
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof LessonFileInstance
                && ((LessonFileInstance) o).getFileName().equals(getFileName()));
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
