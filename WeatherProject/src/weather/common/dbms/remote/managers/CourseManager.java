
package weather.common.dbms.remote.managers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.mysql.TableMetaData;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSCourseManager.
 * @author Brian Zaiser
 * @author Xiang Li
 */
public class CourseManager implements DBMSCourseManager{

    /**
     * Retrieves all of the associated information of all the Courses from
     * the database.
     * @return A collection of Course objects with all fields filled.
     */
    @Override
    public Vector<Course> obtainAllCourses() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_ObtainAllCourses;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Course>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all of the associated information of a Course from the database
     * based on the course identifier, section, and name.
     * @param classIdentifier The course identification.
     * @param section The section of the course.
     * @param className The name of the course.
     * @return A Course object with all fields filled.
     */
    @Override
    public Course obtainCourse(String classIdentifier, int section, String className) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_ObtainCourse;
       arguments = new ArrayList<Object>();
       arguments.add(classIdentifier);
       arguments.add(section);
       arguments.add(className);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (Course) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Deletes the record from the database for the specified Course .
     * @param course The Course to be deleted from the database.
     * @return True, if the deletion from the database was successful; false, otherwise.
     */
    @Override
    public boolean removeCourse(Course course) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_RemoveCourse;
       arguments = new ArrayList<Object>();
       arguments.add(course);
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
     * Updates the database record for the specified Course using the new
     * information in the fields.
     * @param course The course to be updated in the database.
     * @return True, if the update to the database was successful; false, otherwise.
     */
    @Override
    public boolean updateCourse(Course course) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_Update;
       arguments = new ArrayList<Object>();
       arguments.add(course);
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
     * Returns a Course object from the database based on the specified course 
     * being added to the database.
     * @param course The course to be added to the database.
     * @return A Course object with all fields filled.
     */
    @Override
    public Course insertCourse(Course course) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_Insert;
       arguments = new ArrayList<Object>();
       arguments.add(course);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (Course) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all of the associated information of all the Courses from
     * the database based on the specified date, if they are inactive.
     * @param date
     * @return A collection of Course objects with all fields filled.
     */
    @Override
    public Vector<Course> obtainInactiveCourses(Date date) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_ObtainInactiveCourses;
       arguments = new ArrayList<Object>();
       arguments.add(date);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Course>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Deletes the records from the database for the Courses that were added 
     * before the specified date.
     * @param date
     * @return True, if the deletions from the database were successful; false, otherwise.
     */
    @Override
    public boolean removeCoursesBeforeDate(Date date) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_RemoveCourseByDate;
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
     * Retrieves the metadata for the Course table.
     * @return The metadata for the Course table.
     */
    @Override
    public TableMetaData getMetaData() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_GetMetadata;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return (TableMetaData) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all of the associated information of all the Courses from
     * the database based on the current user.
     * @param currentLoggedInUser The current user.
     * @return A collection of Course objects with all fields filled.
     */
    @Override
    public Vector<Course> obtainAllCoursesTaughyByUser(User currentLoggedInUser) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Course_ObtainAllCoursesByUser;
       arguments = new ArrayList<Object>();
       arguments.add(currentLoggedInUser);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Course>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    @Override
    public Course obtainCourse(int courseNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Course> obtainCoursesByStudent(User u) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
