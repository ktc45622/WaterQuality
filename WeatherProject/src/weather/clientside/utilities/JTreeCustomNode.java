package weather.clientside.utilities;

/**
 * Layout for creating a custom JTree node class holding object for parsing
 * the node.
 * Classes implementing this interface are necessary primarily when the 
 * functionality to behave a specific way upon clicking a node in a custom
 * JTree is desired.
 * 
 * @author Nikita Maizet
 */
public interface JTreeCustomNode {
    
    /**
     * Sets the object from which the implementing class will draw necessary
     * data from to render node.
     * 
     * @param obj 
     */
    public void setSourceObject(Object obj);
    
    /**
     * Returns the object from which the implementing class will draw necessary
     * data from to render node.
     * 
     * @return Object - specific kind specified by programmer
     */
    public Object getSourceObject();
}
