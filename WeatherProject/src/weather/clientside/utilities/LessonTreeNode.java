package weather.clientside.utilities;

import javax.swing.tree.DefaultMutableTreeNode;
import weather.common.data.forecasterlesson.ForecasterLesson;

/**
 * TODO: Not used, Remove.
 * 
 * A class representing a node in the GradebookStudentEntry 's tree displaying
 * assignment information about a student. It extends the DefaultMutableTreeNode
 * class and uses almost all of its default implemented functionality. The 
 * modification is in the constructor: it is set up to take any implementation
 * of ForecasterLesson interface and generate the name of the tree node accordingly.
 * 
 * @author Nikita Maizet
 */
public class LessonTreeNode extends DefaultMutableTreeNode {
    
    /**
     * Taking the ForecasterLesson implementation generates the node name using
     * its information.
     * 
     * @param lesson 
     */
    public LessonTreeNode(ForecasterLesson lesson) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = lesson;
    }
    
    /**
     * Temporary for testing purposes.
     * 
     * @param s 
     */
    public LessonTreeNode(String s) {
        super();
        parent = null;
        this.allowsChildren = true;
        this.userObject = s;
    }
}
