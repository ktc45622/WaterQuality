package weather.clientside.utilities;

import javax.swing.JSeparator;
import javax.swing.tree.DefaultMutableTreeNode;
import weather.clientside.gradebook.GBForecastingLessonEntry;
import weather.common.data.Course;
import weather.common.data.forecasterlesson.ForecasterLesson;

/**
 * A class representing a node in the GradebookStudentEntry 's tree displaying
 * assignment information about a student. It extends the DefaultMutableTreeNode
 * class and uses almost all of its default implemented functionality. The 
 * modification is in the constructor: it is set up to take any implementation
 * of ForecasterLesson interface and generate the name of the tree node accordingly.
 * 
 * @author Nikita Maizet
 */
public class ForecasterJTreeNode extends DefaultMutableTreeNode {
    
    /**
     * Used for the head of the tree, which will not contain an object.
     */
    public ForecasterJTreeNode() {
        super();
        parent = null;
    }
    
    /**
     * Generates a node using FGBStudentViewLNode object.
     * 
     * @param node 
     */
    public ForecasterJTreeNode(FGBStudentViewLNode node) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = node;
    }
    
    /**
     * Generates a node using FGBStudentViewANode object.
     * 
     * @param node 
     */
    public ForecasterJTreeNode(FGBStudentViewANode node) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = node;
    }
    
    /**
     * Generates a node using FGBLessonViewLNode object.
     * 
     * @param node 
     */
    public ForecasterJTreeNode(FGBLessonViewLNode node) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = node;
    }
    
    /**
     * Generates a node using FGBLessonViewANode object.
     * 
     * @param node 
     */
    public ForecasterJTreeNode(FGBLessonViewANode node) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = node;
    }
    
    /**
     * Generates a node using GBLessonEntry object.
     * 
     * @param gblesson 
     */
    public ForecasterJTreeNode(GBForecastingLessonEntry gblesson) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = gblesson;
    }
    
    /**
     * Taking the ForecasterLesson implementation generates the node name using
     * its information.
     * 
     * @param lesson 
     */
    public ForecasterJTreeNode(ForecasterLesson lesson) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = lesson;
    }
    
    /**
     * Temporary for testing purposes.
     * Actual constructor may be found above.
     * 
     * @param s
     */
    public ForecasterJTreeNode(String[] s) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = s;
    }
    
    /**
     * Constructor that allows the caller to specify a list of values that will
     * be written to each Node. This can be displayed in any number of ways
     * depending on the purpose.
     * 
     * @param valuesToBeWritten 
     */
    public ForecasterJTreeNode(
            ForecasterNodeObject valuesToBeWritten){
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = valuesToBeWritten;
    }
    
    public ForecasterJTreeNode(
            Course course){
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = course;
    }
    
    public ForecasterJTreeNode(
            JSeparator sep){
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = sep;
    }
}
