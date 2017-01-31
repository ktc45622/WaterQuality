
package weather.common.dbms.remote.managers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.Course;
import weather.common.data.EnrolledStudent;
import weather.common.data.User;
import weather.common.dbms.DBMSUserManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSUserManager.
 * @author Brian Zaiser
 */
public class UserManager implements DBMSUserManager{

    /**
     * Retrieves the user record based on login ID.
     * @param userLoginID The specific user's login ID.
     * @return A User object with all fields filled.
     */
    @Override
    public User obtainUser(String userLoginID) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUserByLoginID;
       arguments = new ArrayList<Object>();
       arguments.add(userLoginID);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (User) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves the user record based on the first and last names.
     * @param userFirstName The user's first name.
     * @param userLastName The user's last name.
     * @return A User object with all fields filled.
     */
    @Override
    public User obtainUser(String userFirstName, String userLastName) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUserByName;
       arguments = new ArrayList<Object>();
       arguments.add(userFirstName);
       arguments.add(userLastName);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (User) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves the user record based on userID.
     * @param userID The specific userID.
     * @return A User object with all fields filled.
     */
    @Override
    public User obtainUser(int userID) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUserByUserID;
       arguments = new ArrayList<Object>();
       arguments.add(userID);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (User) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves the user record by the specified email.
     * @param email The specific email.
     * @return A User object with all fields filled.
     */
    @Override
    public User obtainUserEmail(String email) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUserEmail;
       arguments = new ArrayList<Object>();
       arguments.add(email);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (User) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all user records.
     * @return A collection of User objects with all fields filled.
     */
    @Override
    public Vector<User> obtainAllUsers() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainAllUsers;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Adds a user record from the information in the User object's fields.
     * @param newUser The user to be added using field values.
     * @return True, if record successfully added; false, otherwise.
     */
    @Override
    public boolean addUser(User newUser) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_AddUser;
       arguments = new ArrayList<Object>();
       arguments.add(newUser);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Updates a user record by the User object for the specified user ID.
     * @param myUser The specific user object.
     * @param userID The specific user ID.
     * @return True, if record successfully updated; false, otherwise.
     */
    @Override
    public boolean updateUser(User myUser, int userID) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_UpdateUser;
       arguments = new ArrayList<Object>();
       arguments.add(myUser);
       arguments.add(userID);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Deletes the user record for the specified login ID, by the current user.
     * @param userLoginID The specified login ID.
     * @return True, if record successfully deleted; false, otherwise.
     */
    @Override
    public boolean removeUser(String userLoginID) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_RemoveUserByLoginID;
       arguments = new ArrayList<Object>();
       arguments.add(userLoginID);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Retrieves all user records of instructors.
     * @return A collection of User objects with all fields filled.
     */
    @Override
    public Vector<User> obtainAllInstructors() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainAllInstructors;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Updates the password field of the user record to the specified new 
     * password for the specified login ID.
     * @param userLoginID The login ID of the user.
     * @param newPassword The new password.
     * @return True, if record successfully updated; false, otherwise.
     */
    @Override
    public boolean updatePassword(String userLoginID, String newPassword) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_UpdatePassword;
       arguments = new ArrayList<Object>();
       arguments.add(userLoginID);
       arguments.add(newPassword);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Deletes a user record based on the userID, and the current user.
     * @param userID The userID for the record to be deleted.
     * @return True, if record successfully deleted; false, otherwise.
     */
    @Override
    public boolean removeUser(int userID) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_RemoveUserByUserID;
       arguments = new ArrayList<Object>();
       arguments.add(userID);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Retrieves the record of an enrolled student by ID.
     * @param studentID The specific student ID.
     * @return A EnrolledStudent object with all fields filled.
     */
    @Override
    public EnrolledStudent getEnrolledStudentByID(String studentID) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_GetEnrolledStudentByID;
       arguments = new ArrayList<Object>();
       arguments.add(studentID);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (EnrolledStudent) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all enrolled students records.
     * @return A collection of EnrolledStudent objects with all fields filled.
     */
    @Override
    public Vector<EnrolledStudent> obtainAllEnrolledStudents() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainAllEnrolledStudents;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<EnrolledStudent>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Updates the last login date and number of logins for the specified user.
     * @param user The specific user.
     * @return True, if record successfully updated; false, otherwise.
     */
    @Override
    public boolean updateLoginDateAndNumberOfLogins(User user) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_UpdateLoginDateAndNumberOfLogins;
       arguments = new ArrayList<Object>();
       arguments.add(user);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Retrieves all user records of students enrolled in the specified course.
     * @param c The specific course.
     * @return A collection of User objects with all fields filled.
     */
    @Override
    public Vector<User> obtainAllStudentsInCourse(Course c) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainAllStudentsInCourse;
       arguments = new ArrayList<Object>();
       arguments.add(c);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Returns a list of inactive users.
     * @param date The date to compare a against, returns all students that have 
     * not logged in on or after this date.
     * @return A vector of inactive students, empty if there are none.
     */
    @Override
    public Vector<User> obtainInactiveStudents(Date date) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainInactiveStudents;
       arguments = new ArrayList<Object>();
       arguments.add(date);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Deletes student records for students added before the specified date.
     * @param date The cut-off date.
     * @return True, if record successfully deleted; false, otherwise.
     */
    @Override
    public boolean removeStudentsBeforeDate(Date date) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_RemoveStudentsBeforeDate;
       arguments = new ArrayList<Object>();
       arguments.add(date);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Retrieves all user records of instructors and administrators.
     * @return A collection of User objects with all fields filled.
     */
    @Override
    public Vector<User> obtainAllInstructorsAndAdministrators() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainAllInstructorsAndAdministrators;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all user records of administrators.
     * @return A collection of User objects with all fields filled.
     */
    @Override
    public Vector<User> obtainAllAdministrators() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainAllAdministrators;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Gets a list of all users with a first name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    @Override
    public Vector<User> obtainUsersWithFirstNameSubstring(String substring) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUsersByFirstName;
       arguments = new ArrayList<Object>();
       arguments.add(substring);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Gets a list of all users with a last name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    @Override
    public Vector<User> obtainUsersWithLastNameSubstring(String substring) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUsersByLastName;
       arguments = new ArrayList<Object>();
       arguments.add(substring);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Gets a list of all users with a user name containing the given substring.
     * @param substring The given substring.
     * @return A (possibly empty) vector of matching users.
     */
    @Override
    public Vector<User> obtainUsersWithLoginIDSubstring(String substring) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUsersByLoginID;
       arguments = new ArrayList<Object>();
       arguments.add(substring);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records for users with the specified e-mail.
     * @param email The specified e-mail.
     * @return A collection of User objects with all fields filled.
     */
    @Override
    public Vector<User> obtainUsersByEmail(String email) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUsersByEmail;
       arguments = new ArrayList<Object>();
       arguments.add(email);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }
    
    /**
     * Retrieves all records for users with the specified username.
     * @param username The specified username.
     * @return A collection of User objects with all fields filled.
     */
    @Override
    public Vector<User> obtainUsersByUsername(String username) {
         RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.User_ObtainUsersByExactLoginID;
       arguments = new ArrayList<Object>();
       arguments.add(username);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<User>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }
    
}
