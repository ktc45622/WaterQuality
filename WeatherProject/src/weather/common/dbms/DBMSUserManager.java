package weather.common.dbms;

import java.sql.Date;
import java.util.Vector;
import weather.common.data.Course;
import weather.common.data.EnrolledStudent;
import weather.common.data.User;

/**
 * The <code>DBMSUserManager</code> interface creates, retrieves, updates,
 * and deletes users in the database.
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @version Spring 2008
 */
public interface DBMSUserManager {

    /**
     * This attempts to locate a user in our database. 
     * 
     * The user login ID is used to query the database and the user object 
     * associated with this user login id is returned.  
     * If the user login identifier is not in the database then 
     * this routine returns null. 
     * The code for this routine should first <i> trim </i> the user name. 
     * All string values in the database should be trimmed. 
     * 
     * @param userLoginID A String representing the user login id to be
     * used to locate this user in the database.
     * @return A <code>User</code> object associated with userLoginID or
     * null if there is no corresponding user in the database.
     */
    public User obtainUser(String userLoginID);

    /**
     * This attempts to locate a user in our database. 
     * 
     * The user login ID is used to query the database and the user object 
     * associated with this user's name is returned. If a user with this name is
     * not in the database then this method returns null. 
     * The code for this routine should first <i> trim </i> the user name. 
     * All string values in the database should be trimmed. 
     *
     * @param userFirstName A String representing the user's first name.
     * @param userLastName A String representing the user's last name.
     * @return A <code>User</code> object containing the user information for a user
     * with this first and last name or null if there is no corresponding user.
     */
    public User obtainUser(String userFirstName, String userLastName);

    /**
     * This attempts to locate a user in our database. 
     * 
     * The userID is used to query the database and the user object 
     * associated with this userID is returned. If the user login identifier is
     * not in the database then this routine returns null. 
     * The code for this routine should first <i> trim </i> the user name. 
     * All string values in the database should be trimmed. 
     * 
     * @param userID An integer representing the user's id (or primary
     * key) to be used to locate this user in the database.
     * @return A <code>User</code> object associated with userLoginID or null
     * if there is no corresponding user.
     */
    public User obtainUser(int userID);

    /**
     * This attempts to locate a user in our database.
     * 
     * The user email is used to query the database and the user object 
     * associated with this user's name is returned. If a user with this name is
     * not in the database then this method returns null.
     * The code for this routine should first <i> trim </i> the user name.
     * All string values in the database should be trimmed.
     *
     * @param email A String representing the user's email.
     * @return A <code>User</code> object containing the user information for a user
     * with this first and last name or null if there is no corresponding user.
     */
    public User obtainUserEmail(String email);

    /**
     * Performs a query that returns all User objects held by the database,
     * then returns the objects in a Vector.
     *
     * @return A <code>Vector</code> containing all <code>User</code> objects
     * stored in the database
     */
    public Vector<User> obtainAllUsers();

    /**
     * Adds a <code>User</code> to the database. 
     * The parameter newUser is changed to reflect the new user information 
     * as stored in our database.
     * 
     * @param newUser The new <code>User</code>.
     * @return True if the <code>User</code> was successfully added,
     * false otherwise
     */
    public boolean addUser(User newUser);

    /**
     * Updates the <code>User</code> in the database with the given
     * userID number to myUser.
     * The <code>User</code> object myUser is changed to reflect the new user
     * information as stored in the database.
     * 
     * @param myUser The new information.
     * @param userID The user ID to identify which user to change.
     * @return True if the user with the given userID was updated, false
     * otherwise.
     */
    public boolean updateUser(User myUser, int userID);

    /**
     * Removes the <code>User</code> from the database that has a login id that
     * matches the userLoginID that is passed.
     *
     * @param userLoginID The login id of the user to be deleted.
     * @return True if the <code>User</code> with the given userLoginID was
     * removed, false otherwise.
     */
    public boolean removeUser(String userLoginID);

    /**
     * Performs a query that returns all User objects held by the database
     * whose UserType is Instructor, then returns the objects in a Vector.
     *
     * @return A <code>Vector</code> containing all <code>User</code> objects
     * stored in the database of UserType Instructor.
     */
    public Vector<User> obtainAllInstructors();

    /**
     * Updates the password for the <code>User</code> in the database
     * corresponding to the given userLoginID.
     *
     * @param userLoginID The login id of the user.
     * @param newPassword The new password for the user.
     * @return True if the password was updated, false otherwise.
     */
    public boolean updatePassword(String userLoginID, String newPassword);

    /**
     * Removes the <code>User</code> from the database that has an id that
     * matches the userID that is passed.
     *
     * @param userID The id of the user to be deleted.
     * @return True if the <code>User</code> with the given userID was removed,
     * false otherwise.
     */
    public boolean removeUser(int userID);

    /**
     * @param studentID A string representing the students ID number.
     * @return A student object or null if none is found.
     */
    public EnrolledStudent getEnrolledStudentByID(String studentID);

    /**
     * Gets all students as a vector of students.
     * @return A vector containing all students, or null if there are none.
     */
    public Vector<EnrolledStudent> obtainAllEnrolledStudents();

    /**
     * Updates the login date and number of logins for a given user.
     * @param user The user to update.
     * @return True if updated, false otherwise.
     */
    public boolean updateLoginDateAndNumberOfLogins(User user);

    /**
     * Obtains all the students in a specific course.
     * @param c The course to match students against.
     * @return A vector of students or null if none are found.
     */
    public Vector<User> obtainAllStudentsInCourse(Course c);

    /**
     * Returns a list of inactive users.
     * @param date The date to compare a against, returns all students that
     * have not logged in on or after this date.
     * @return A list of users.
     */
    public Vector<User> obtainInactiveStudents(Date date);

    /**
     * Removes Removes students who haven't logged in since before this date.
     * @param date Removes students who haven't logged in since before this date.
     * @return True if students were removed, false otherwise.
     */
    public boolean removeStudentsBeforeDate(Date date);

    /**
     * Gets all the instructors and administrators from the database.
     * @return A list of user objects containing all instructors and administrators.
     */
    public Vector<User> obtainAllInstructorsAndAdministrators();

    /**
     * Gets all the administrators from the database.
     * @return A list of user objects containing all administrators.
     */
    public Vector<User> obtainAllAdministrators();

    /**
     * Gets a list of all users with a first name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    public Vector<User> obtainUsersWithFirstNameSubstring(String substring);

    /**
     * Gets a list of all users with a last name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    public Vector<User> obtainUsersWithLastNameSubstring(String substring);

    /**
     * Gets a list of all users with a user name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    public Vector<User> obtainUsersWithLoginIDSubstring(String substring);
    
    /**
     * Gets a list of all users with a given email (max size is 1)
     * @param email The user's email to search for
     * @return A vector of users with the given email, empty if none
     */
    public Vector<User> obtainUsersByEmail(String email);
    
    /**
     * Gets a list of all users with a given username (max size is 1)
     * @param username The username to search for
     * @return A vector of users with the given email, empty if none
     */
    public Vector<User> obtainUsersByUsername(String username);
}
