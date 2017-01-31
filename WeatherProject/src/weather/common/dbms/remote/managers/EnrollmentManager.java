
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.dbms.DBMSEnrollmentManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSEnrollmentManager.
 * @author Brian Zaiser
 */
public class EnrollmentManager implements DBMSEnrollmentManager{

    /**
     * Retrieves a collection of Course objects for the specified student.
     * @param student The student whose courses are requested.
     * @return A collection of Course objects with all fields filled.
     */
    @Override
    public Vector<Course> getCoursesForStudent(User student) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Enrollment_GetCoursesForStudent;
       arguments = new ArrayList();
       arguments.add(student);
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
     * Retrieves a collection of Course objects for the specified instructor.
     * @param instructor The instructor whose courses are requested.
     * @return A collection of Course objects with all fields filled.
     */
    @Override
    public Vector<Course> getCoursesForInstructor(User instructor) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Enrollment_GetCoursesForInstructor;
       arguments = new ArrayList();
       arguments.add(instructor);
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
     * Retrieves a collection of User objects representing the students in the specified course.
     * @param course The course the students are taking.
     * @return A collection of User objects with all fields filled.
     */
    @Override
    public Vector<User> getStudentsInCourse(Course course) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Enrollment_GetStudentsInCourse;
       arguments = new ArrayList();
       arguments.add(course);
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
     * Adds a record for the specified student in the specified course by studentID.
     * @param student A user object for the student be added to the Course.
     * @param course The course the student is adding.
     * @return True, if records added successfully; false, otherwise.
     */
    @Override
    public boolean insertStudentIntoCourse(User student, Course course) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Enrollment_InsertStudentInCourse;
       arguments = new ArrayList();
       arguments.add(student);
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
     * Deletes the record for the specified student from the specified course.
     * @param student The student being removed from the Course.
     * @param course The Course the student is dropping.
     * @return True, if record deleted successfully; false, otherwise.
     */
    @Override
    public boolean removeStudentFromCourse(User student, Course course) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Enrollment_RemoveStudentFromCourse;
       arguments = new ArrayList();
       arguments.add(student);
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
     * Deletes all student records from the database that are enrolled in the specified course.
     * @param course The course from which to delete students.
     */
    @Override
    public void removeAllStudentsFromCourse(Course course) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Enrollment_RemoveAllStudentsFromCourse;
       arguments = new ArrayList();
       arguments.add(course);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return ; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return ;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return ;
        } //end of if statement
    }

    /**
     * Deletes student records from the database, for those students not 
     * currently enrolled in any courses.
     * @return True, if records deleted successfully; false, otherwise.
     */
    @Override
    public boolean deleteStudentsEnrolledInNoCourses() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Enrollment_DeleteStudentsNotEnrolled;
       arguments = null;
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
    
}
