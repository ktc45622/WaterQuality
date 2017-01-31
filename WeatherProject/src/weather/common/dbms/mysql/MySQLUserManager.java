package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.Course;
import weather.common.data.EnrolledStudent;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkType;
import weather.common.dbms.DBMSBookmarkCategoriesManager;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>MySQLUserManager</code> class manipulates users stored in the Users
 * table in the database. An instance of this class can be created either by
 * calling <code>getUserManager()</code> from the <code>MySQLImpl</code> class
 * or by calling its constructor:
 * <pre>
 *  Vector<User> userList;
 *  DBMSSystem dbms;
 *  userList = dbms.getUserManager().obtainAllUsers();
 * </pre>
 *  
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public class MySQLUserManager implements DBMSUserManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;

    public MySQLUserManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    /**
     * Obtains all users stored in the database.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * This method will only return users with a userNumber higher than 100.
     * @return A <code>Vector</code> of all users, empty if there are none.
     */
    @Override
    public Vector<User> obtainAllUsers() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                User currentUser = MySQLHelper.makeUserFromResultSet(rs);
                list.add(currentUser);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Obtains a <code>User</user> with the given first name and the given last
     * name from the database.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param userFirstName The first name of the user.
     * @param userLastName The last name of the user.
     * @return A <code>User</code> object that contains information for the user
     * with the given first name and the last name, or null if a <code>User</code>
     * does not exist.
     */
    @Override
    public User obtainUser(String userFirstName, String userLastName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User currentUser = null;
        try {
            String sql = "SELECT * FROM users WHERE firstName = ? AND lastName = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userFirstName);
            ps.setString(2, userLastName);
            rs = ps.executeQuery();
            if (rs.first()) {
                currentUser = MySQLHelper.makeUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "obtaining a user based on first and last name from the database.", e);
            new WeatherException(4016, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return currentUser;
    }

    /**
     * Obtains a <code>User</code> with the given user ID from the database.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param userID The user ID of the <code>User</code> to be returned.
     * @return A <code>User</code> object that contains information for the user
     * with the given user ID, or null if a <code>User</code> does not exist.
     */
    @Override
    public User obtainUser(int userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User currentUser = null;
        try {
            String sql = "SELECT * FROM users WHERE userNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            if (rs.first()) {
                currentUser = MySQLHelper.makeUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "obtaining a user based on userID from the database.", e);
            new WeatherException(4017, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return currentUser;
    }

    /**
     * Obtains a user in the database. The username is used to
     * query the database and the user object associated with this user login 
     * identifier is returned. The code for this routine should <i>trim</i>
     * the user login identifier.
     * All string values in the database should be 
     * trimmed.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param userLoginID The login ID for the <code>User</code> to be returned.
     * @return A <code>User</code> corresponding to the given userLoginID, null
     * if the <code>User</code> does not exist.
     */
    @Override
    public User obtainUser(String userLoginID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User currentUser = null;
        try {
            String sql = "SELECT * FROM users where loginID = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userLoginID);
            rs = ps.executeQuery();
            if (rs.first()) {
                currentUser = MySQLHelper.makeUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "obtaining a user based on loginId from the database.", e);
            new WeatherException(4018, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return currentUser;
    }

    /**
     * Obtains a <code>User</code> with the given email from the database.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param email The email of the <code>User</code> to be returned.
     * @return A <code>User</code> object that contains information for the user
     * with the given email, null if the given email does not exist.
     */
    @Override
    public User obtainUserEmail(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User currentUser = null;
        try {
            String sql = "SELECT * FROM users where email = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.first()) {
                currentUser = MySQLHelper.makeUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return currentUser;
    }

    /**
     * Adds a <code>User</code> to the database.  A record of a new user is added
     * to the Users table.
     * The <code>executeStatementAndReturnGeneratedKey</code> method executes
     * the <code>PreparedStatement</code> and obtains the Number of the given
     * user in the Users table. The Number field is the Primary Key and is
     * generated automatically by MySQL. The obtained Number is then used to
     * set the userNumber field of the given <code>User</code>.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param newUser The <code>User</code> to be added to the database.
     * @return True if a <code>User</code> was added to the database, false
     * otherwise.
     */
    @Override
    public boolean addUser(User newUser) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "INSERT INTO users (loginID, loginPassword,"
                    + " email, firstName, lastName, userType, notes,"
                    + " lastLoginTime, loginCount) "
                    + "VALUES (?, ?, ?, ?, ?, ? ,?, ?, ?)";

            conn = dbms.getLocalConnection();
            Calendar calendar = Calendar.getInstance();
            Timestamp currentDateTime = new Timestamp(calendar.getTime().getTime());
            ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);
            ps.setString(1, newUser.getLoginId());
            ps.setString(2, newUser.getPassword());
            ps.setString(3, newUser.getEmailAddress());
            ps.setString(4, newUser.getFirstName());
            ps.setString(5, newUser.getLastName());
            ps.setString(6, newUser.getUserType().toString());
            ps.setString(7, newUser.getNotes());
            ps.setTimestamp(8, currentDateTime);
            ps.setInt(9, 0);                             // set login count to 0
            //@TODO an issue with the key was created
            int key = MySQLHelper.executeStatementAndReturnGeneratedKey(ps);

            if (key > 0) {
                newUser.setUserNumber(key);
                bSuccess = true;
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }

    /**
     * Updates a <code>User</code> object's information with the given user ID
     * to the information of the given <code>User</code>.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * 
     * @param myUser The <code>User</code> that a user with the given ID is
     * updated to.
     * @param userID The user ID of the user whose information need to be updated.
     * @return True if the <code>User</code> was updated, false otherwise.
     */
    @Override
    public boolean updateUser(User myUser, int userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "UPDATE users SET loginID = ?, loginPassword = ?, email = ?,"
                    + "firstName = ?, lastName = ?, userType = ? WHERE userNumber = ?";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, myUser.getLoginId());
            ps.setString(2, myUser.getPassword());
            ps.setString(3, myUser.getEmailAddress());
            ps.setString(4, myUser.getFirstName());
            ps.setString(5, myUser.getLastName());
            ps.setString(6, myUser.getUserType().toString());
            ps.setInt(7, userID);

            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }

    /**
     * Removes a <code>User</code> with the given LoginID (a.k.a. username).
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param userLoginID The login ID of the user to be removed.
     * @return True if a <code>User</code> with the given login ID was
     * removed, false if attempting to delete the currently logged in user or if
     * the user cannot be removed.
     */
    @Override
    public boolean removeUser(String userLoginID) {
        return removeUser(obtainUser(userLoginID).getUserNumber());
    }

    /**
     * Removes a <code>User</code> with the given ID.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param userID The ID of the <code>User</code> to be removed.
     * @return True if a <code>User</code> with the given ID was removed,
     * false otherwise.
     */
    @Override
    public boolean removeUser(int userID) {
        PreparedStatement ps = null;
        Connection conn = null;
        
        try {
            String sql = "{call sp_deleteUser(?)}";
            conn = dbms.getLocalConnection();
            ps = conn.prepareCall(sql);
            ps.setInt(1, userID);
            return !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return false;
    }

    /**
     * Obtains all users of type instructor stored in the database.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @return A <code>Vector</code> of all users, it will by empty if there are none.
     */
    @Override
    public Vector<User> obtainAllInstructors() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users WHERE userType = 'Instructor'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                User currentUser = MySQLHelper.makeUserFromResultSet(rs);
                list.add(currentUser);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Updates the password for a <code>User</code> with the given login ID to
     * the given password.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param userLoginID The login id of the user.
     * @param newPassword The new password for the user.
     * @return True if the password of a <code>User</code> with the given
     * login ID was updated, false otherwise.
     */
    @Override
    public boolean updatePassword(String userLoginID, String newPassword) {
        newPassword = PropertyManager.encrypt(newPassword);
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "UPDATE users SET loginPassword = ? WHERE loginID = ?;";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setString(2, userLoginID);
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }

    /**
     * Returns a student object by the students ID.
     * @param studentID The students ID.
     * @return A student object or null if none is found.
     */
    @Override
    public EnrolledStudent getEnrolledStudentByID(String studentID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        EnrolledStudent currentStudent = null;
        try {
            String sql = "SELECT * FROM users where studentID = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            rs = ps.executeQuery();
            if (rs.first()) {
                currentStudent = MySQLHelper.makeEnrolledStudentSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "obtaining a student based on studentId from the database.", e);
            new WeatherException(4018, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return currentStudent;
    }

    /**
     * Returns all currently enrolled students.
     * @return A vector of enrolled students or an empty vector if none are found.
     */
    @Override
    public Vector<EnrolledStudent> obtainAllEnrolledStudents() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<EnrolledStudent> list = new Vector<EnrolledStudent>();
        try {
            String sql = "SELECT * FROM users WHERE userType = 'Student'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(MySQLHelper.makeEnrolledStudentSet(rs));
            }

        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Obtains all the students in a specific course.
     * @param c The course to match students against.
     * @return A vector of students in the course (empty if there are none), 
     *          or null if the course is invalid.
     */
    @Override
    public Vector<User> obtainAllStudentsInCourse(Course c) {
        if (c == null) {
            return null;
        }

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT users.* FROM users, enrollment"
                    + " WHERE users.userType = 'Student' "
                    + " AND users.userNumber = enrollment.userNumber "
                    + " AND enrollment.courseNumber = (?)";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getCourseNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Updates the last login time for a user.
     * @param user The user whose login time to update.
     * @return True if the update is successful, false otherwise.
     */
    @Override
    public boolean updateLoginDateAndNumberOfLogins(User user) {
        boolean isSuccess = false;
        // If the data for this object is not set, don't use it.
        if (user.getUserNumber() == -1) {
            return isSuccess;   // then why return success?
        }

        Calendar calendar = Calendar.getInstance();
        Timestamp currentDateTime = new Timestamp(calendar.getTime().getTime());
        Connection conn = null;
        PreparedStatement ps = null;

        try {

            String sql = "UPDATE users SET lastLoginTime = ?, loginCount = ? WHERE loginID = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, currentDateTime);
            ps.setInt(2, (user.getNumberOfLogins() + 1));
            ps.setString(3, user.getLoginId());
            isSuccess = !ps.execute();

        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccess;
    }

    /**
     * Returns a list of inactive users.
     * @param date The date to compare a against, returns all students that
     *              have not logged in on or after this date.
     * @return A vector of inactive students, empty if there are none.
     */
    @Override
    public Vector<User> obtainInactiveStudents(Date date) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<User> list = new Vector<User>();

        try {
            String sql = "SELECT * FROM users WHERE userType = 'Student' "
                    + " AND lastLoginTime < (?) ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, date);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Removes Removes students who haven't logged in since before this date.
     * @param date Removes students who haven't logged in since before this date.
     * @return True if students were removed, false otherwise.
     */
    @Override
    public boolean removeStudentsBeforeDate(Date date) {
        //Assume a success but change later if not.
        boolean result = true;
        
        for (User user : obtainAllUsers()) {
            if (user.getUserType() == UserType.student && user
                .getLastLogInDate().getTime() < date.getTime()) {
                if (!removeUser(user.getUserNumber())) {
                    //This is a failed delete.
                    result = false;
                }
            }
        }
        
        //Return success state.
        return result;
    }

    /**
     * Gets all the instructors and administrators from the database.
     * @return A vector of user objects containing all instructors and administrators.
     */
    @Override
    public Vector<User> obtainAllInstructorsAndAdministrators() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users WHERE userType = 'Instructor' "
                    + " OR userType = 'Administrator'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                User currentUser = MySQLHelper.makeUserFromResultSet(rs);
                list.add(currentUser);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Gets all of the administrators from the database.
     * @return A vector of user objects containing all administrators.
     */
    @Override
    public Vector<User> obtainAllAdministrators() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users WHERE userType = 'Administrator'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                User currentUser = MySQLHelper.makeUserFromResultSet(rs);
                list.add(currentUser);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }
    
    /**
     * Gets all of the students from the database.
     * @return A vector of User objects containing all students.
     */
    public Vector<User> obtainAllStudents() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users WHERE userType = 'Student'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                User currentUser = MySQLHelper.makeUserFromResultSet(rs);
                list.add(currentUser);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }
    
    /**
     * Gets all of the guests from the database.
     * @return A vector of User objects containing all guests.
     */
    public Vector<User> obtainAllGuests() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users WHERE userType = 'Guest'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                User currentUser = MySQLHelper.makeUserFromResultSet(rs);
                list.add(currentUser);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

   /**
     * Gets a list of all users with a first name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    @Override
    public Vector<User> obtainUsersWithFirstNameSubstring(String substring) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users"
                    + " WHERE firstName LIKE (?) ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + substring + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Gets a list of all users with a last name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    @Override
    public Vector<User> obtainUsersWithLastNameSubstring(String substring){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users"
                    + " WHERE lastName LIKE (?) ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + substring + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
    * Gets a list of all users with a user name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    @Override
    public Vector<User> obtainUsersWithLoginIDSubstring(String substring){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT * FROM users"
                    + " WHERE loginID LIKE (?) ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + substring + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }
    
    /**
     * Gets a list of all users with a given email. (max size is 1)
     * @param email The user's email to search for.
     * @return A vector of users with the given email, empty if none.
     */
    @Override
    public Vector<User> obtainUsersByEmail(String email) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<User> list = new Vector<User>();
        
        try {
            String sql = "SELECT * FROM users"
                    + " WHERE email = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Gets a list of all users with a given username. (max size is 1)
     * @param username The username to search for.
     * @return A vector of users with the given username, empty if none.
     */
    @Override
    public Vector<User> obtainUsersByUsername(String username) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<User> list = new Vector<User>();

        try {
            String sql = "SELECT * FROM users"
                    + " WHERE loginID = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }
}
