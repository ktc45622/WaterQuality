
package weather.clientside.manager;

import java.util.Vector;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.ResourceInstance;

/**
 * This interface specifies the operations needed in any class that wants to be
 * informed of ResourceSearchController events.
 * 
 * @author Bingchen Yan
 */
public interface ResourceSearchListener {
    /**
     * set data. The vector of images
     * @param images new value of images.
     */
    public void setData(Vector<ResourceInstance> images);
    /**
     * set the name of this panel.
     * @param name new value of images.
     */
    public void setName(String name);
    /**
     * display the first data value (will be left most data value).
     */
    public void start();
    /**
     * display the next data value (increment by 1).
     */
    public void next();
    /**
     * display the last data value (will be right most data value).
     */
    public void last();
    /**
     * Display the next set of three.
     */
    public void nextSet();
    /**
     * Display the previous set of three
     */
    public void previousSet();
    /**
     * display this index (data on left of screen).
     * @param index specify position.
     */
    public void setPosition(int index);
}
