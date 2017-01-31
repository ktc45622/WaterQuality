package weather;

/**
 * This interface specifies the operations that require instructor access
 * rights.
 *
 * @author Brandon McKenzie (2009)
 * @author Joshua Gentile (2009)
 * @author Chris Mertens (2009)
 * @author Ora Merkel (2009)
 * @author Bloomsburg University Software Engineering
 * @version Spring 2009
 */

public interface InstructorControlSystem 
{
    /**
     * Specifies the signature of the method to add a student to the database.
     */
    public void addStudent();

    /**
     * Specifies the signature of the method to search the student database for
     * a particular student.
     */
    public void searchStudent();

    /**
     * Specifies the signature of the method to search the instructor database
     * for a particular instructor.
     */
    public void searchInstructor();
}
