package weather.clientside.applicationimpl;

import weather.InstructorControlSystem;
import weather.GeneralService;

/**
 * This class implements the InstructorControlSystem interface
 * and provides one implementation of all instructor use cases.
 * Please consult the use case manual for detailed notes on each method.
 * 
 * @author jsgentil (2009)
 * @author Bloomsburg University Software Engineering
 * @version Spring 2009
 */
public class InstructorControlImpl implements InstructorControlSystem {

    private GeneralService generalService = null;

    public InstructorControlImpl(GeneralService generalService){
        this.generalService = generalService;
    }


    /**
     * Adds a student to a class.
     */
    @Override
    public void addStudent() {
        //TODO: Implement addStudent();
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Searches the database for students.  
     */
    @Override
    public void searchStudent() {
        //TODO: implement searchStudent()
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Searches the database for instructors. 
     */
    @Override
    public void searchInstructor() {
        //TODO: implement searchInstructor()
        throw new RuntimeException("Not yet implemented");
    }
}
