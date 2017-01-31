/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.common.data.lesson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import weather.ApplicationControlSystem;
import weather.common.data.AccessRights;

/**
 *
 * @author Justin Gamble 
 * @version Spring 2012
 */
public class Lesson implements Serializable{

    
    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Collection of LessonEntries associated with this Lesson.
     */
    Collection<LessonEntry> lessonCollection;
    
    /**
     * DBMS LessonNumber.
     */
    private int lessonNumber;
    
    /**
     * DBMS InstructorNumber.
     */
    private int instructorNumber;
    
    /**
     * DBMS InstructorNumber.
     */
    private int lessonCategoryNumber;
    
    /**
     * Access Rights.
     */
    private AccessRights accessRights;
    
    /**
     * Lesson Name.
     */
    private String name;
    
    /**
     * Creates an empty <code>Lesson</code>. Lesson number is set to -1, nothing
     * else is set.
     */
    public Lesson(){
       lessonNumber = -1; 
    }
    
    /**
     * Creates a <code>Lesson</code> from database information.
     * @param lessonCollection All the LessonEntries for this Lesson.
     * @param lessonNumber DBMS LessonNumber.
     * @param instructorNumber Instructor who made this Lesson.
     * @param lessonCategoryNumber Category for this Lesson.
     * @param accessRights The AccessRights for this Lesson.
     * @param name The Name for this Lesson.
     */
    public Lesson(int lessonNumber, String name, int instructorNumber,AccessRights accessRights,int lessonCategoryNumber, ArrayList<LessonEntry> lessonCollection) {
        this.lessonCollection = lessonCollection;
        this.lessonNumber = lessonNumber;
        this.instructorNumber = instructorNumber;
        this.lessonCategoryNumber = lessonCategoryNumber;
        this.accessRights = accessRights;
        this.name = name;
    }
    
    /**
     * Creates a <code>Lesson</code> from user generated information.
     * @param lessonCollection All the LessonEntries for this Lesson.
     * @param instructorNumber Instructor who made this Lesson.
     * @param lessonCategoryNumber Category for this Lesson.
     * @param accessRights The AccessRights for this Lesson.
     * @param name The Name for this Lesson.
     */
    public Lesson(String name, int instructorNumber,AccessRights accessRights,int lessonCategoryNumber, ArrayList<LessonEntry> lessonCollection) {
        this.lessonCollection = lessonCollection;
        this.lessonNumber = -1;
        this.instructorNumber = instructorNumber;
        this.lessonCategoryNumber = lessonCategoryNumber;
        this.accessRights = accessRights;
        this.name = name;
    }

    public Lesson(int lessonNumber, int instructorNumber, int lessonCategoryNumber, AccessRights accessRights, String lessonName) {
        this.lessonNumber = lessonNumber;
        this.instructorNumber = instructorNumber;
        this.lessonCategoryNumber = lessonCategoryNumber;
        this.accessRights = accessRights;
        this.name = lessonName;
    }
    
    /**
     * Retrieves the AccessRights for this Lesson.
     * @return Instance of AccesRights for this Lesson.
     */
    public AccessRights getAccessRights() {
        return accessRights;
    }
    /**
     * Sets the AccessRights for this Lesson.
     * @param accessRights The new AccessRights for this Lesson.
     */
    public void setAccessRights(AccessRights accessRights) {
        this.accessRights = accessRights;
    }
    /**
     * Returns the DBMS InstructorNumber for the Instructor who created this Lesson.
     * @return The DBMS InstructorNumber of the Lesson creator.
     */
    public int getInstructorNumber() {
        return instructorNumber;
    }
    /**
     * Sets the DBMS InstructorNumber of the Instructor who created this Lesson.
     * @param instructorNumber The new DBMS Identifier for the Instructor who created this Lesson.
     */
    public void setInstructorNumber(int instructorNumber) {
        this.instructorNumber = instructorNumber;
    }
    /**
     * Retrieves the LessonCategoryNumber that this Lesson is a part of.
     * @return The DBMS LessonCategoryNumber that this Lesson belongs to.
     */
    public int getLessonCategoryNumber() {
        return lessonCategoryNumber;
    }
    /**
     * Sets the LessonCategoryNumber that this Lesson should belong to.
     * @param lessonCategoryNumber The new LessonCategoryNumber that this Lesson should belong to.
     */
    public void setLessonCategoryNumber(int lessonCategoryNumber) {
        this.lessonCategoryNumber = lessonCategoryNumber;
    }
    /**
     * Returns the collection of LessonEntries that are held by this Lesson.
     * @return A LinkedList<LessonEntry> that contains all the LessonEntries held by this Lesson.
     */
    public Collection<LessonEntry> getLessonCollection() {
        return lessonCollection;
    }
    /**
     * Adds a LessonEntry to this Lesson.
     * @param added The LessonEntry to be added.
     * @return True if the operation is successful, otherwise false.
     */
    public boolean addLessonEntry(LessonEntry added){
        return lessonCollection.add(added);
    }
    /**
     * Gets the DBMS LessonNumber for this Lesson.
     * @return The DBMS Lesson Identifier.
     */
    public int getLessonNumber() {
        return lessonNumber;
    }
    /**
     * Sets the DBMS LessonNumber for this Lesson.
     * @param lessonNumber The new DBMS LessonNumber for this Lesson.
     */
    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }
    /**
     * Returns the name of this Lesson.
     * @return The current name of this Lesson.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of this Lesson.
     * @param name The new name of this Lesson.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public void populateLessonEntryCollection(ApplicationControlSystem apc){
        
//        lessonCollection = new ArrayList(Arrays.asList(apc.getDBMSSystem().getLessonEntryManager().obtainByLessonNumber(lessonNumber).toArray()));
        lessonCollection = new ArrayList<>(apc.getDBMSSystem().getLessonEntryManager().obtainByLessonNumber(lessonNumber));
    }
    
    /**
     * Compares Lesson types based on the LessonNumber.
     *
     * @param obj The object to compare to this one.
     * @return True if the given object is equal to this one, false
     *      otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Lesson other = (Lesson) obj;

        return (lessonNumber == other.getLessonNumber());
    }
    /**
     * Calculates the hash code value, which is just the LessonNumber.
     *
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return (lessonNumber);
    }
    
    @Override
    public String toString(){
        return name;
    }
    /**
     * Gets the number of LessonEntries handled by this Lesson.
     * @return The number of LessonEntries handled by this Lesson.
     */
    public int getLessonSize(){
        return lessonCollection.size();
    }

    public void setLessonCollection(Collection<LessonEntry> lessonCollection) {
        this.lessonCollection = lessonCollection;
    }
    
    
    
}
