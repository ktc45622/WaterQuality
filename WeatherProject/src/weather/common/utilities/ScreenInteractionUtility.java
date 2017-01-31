package weather.common.utilities;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * This is a utility class that provides customized application-wide methods for
 * interacting with real-world computer screens.
 * @author Brian Bankes
 */
public class ScreenInteractionUtility {
    
    /**
     * This static variable will hold the location of the window that currently
     * has focus. This applies to the main program and all other programs. This
     * variable must be kept up-to-date through use of 
     * <code>setFocusLocation</code>. Note that this point must always be a 
     * point on a monitor, so if <code>setFocusLocation</code> is passed a 
     * nonexistent point, it will correct for this.
     */
    private static Point currentFocusLocation;
    
    /**
     * A helper function to determine the screen that contains the 
     * <code>currentFocusLocation</code>.
     * 
     * @return The screen that contains the <code>currentFocusLocation</code>.
     */
    private static GraphicsDevice getCurrentScreen() {
        for (GraphicsDevice gd : GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices()) {
            if (currentFocusLocation.getX() 
                    >= gd.getDefaultConfiguration().getBounds().getMinX()
                    && currentFocusLocation.getX()
                    < gd.getDefaultConfiguration().getBounds().getMaxX()
                    && currentFocusLocation.getY() 
                    >= gd.getDefaultConfiguration().getBounds().getMinY()
                    && currentFocusLocation.getY() 
                    < gd.getDefaultConfiguration().getBounds().getMaxY()) {
                return gd;
            }
        }
        
        /**
         * Code should not get here. If it does, configure() was not called.
         */ 
        return null;
    }
    
    /**
     * To ensure proper execution of any program in this project, this method 
     * MUST be called whenever the window with focus changes or is moved. This
     * method updates this utility so it can track the location of the window
     * with focus.
     * 
     * @param newFocusLocation The location of the window with focus. This point
     * can be off the screen as this method will correct for this.
     */
    public static void setFocusLocation(Point newFocusLocation) {
        //Assume the provided point is on a moniter. The currentFocusLocation
        //will be changed later if it is not.
        currentFocusLocation = newFocusLocation;
        
        //This for loop will check if the provided point is on a moniter. if so,
        //the function can return with no futher work as currentFocusLocation
        //will stay as is.
        for (GraphicsDevice gd : GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices()) {
            if (newFocusLocation.getX() 
                    >= gd.getDefaultConfiguration().getBounds().getMinX()
                    && newFocusLocation.getX()
                    < gd.getDefaultConfiguration().getBounds().getMaxX()
                    && newFocusLocation.getY() 
                    >= gd.getDefaultConfiguration().getBounds().getMinY()
                    && newFocusLocation.getY() 
                    < gd.getDefaultConfiguration().getBounds().getMaxY()) {
                //The provided pointis on a monitor, so return.
                return;
            }
        }
        
        
        /**
         * If the code get here, the provided point is NOT on a monitor. Checks
         * for the closest monitor will involve finding the closest distance
         * between the provided point and any corner of any available monitor.
         */
        
        //Variable to hold the monitor with the closest corner once found.
        GraphicsDevice nearestDevice = null;
        
        //The current shortest distance, with can initially be 0 because the 
        //fact that the nearestDevice is null will be used to set this variable
        //to the first calculated distance.
        double shortestDistance = 0;
        
        for (GraphicsDevice gd : GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices()) {
            //Get the bounds for this monitor.
            Rectangle testBounds = gd.getDefaultConfiguration().getBounds();
            
            //Find the upper left corner and test against it.
            Point upperLeft = new Point((int) testBounds.getMinX(),
                (int) testBounds.getMinY());
            //The test for null ensures the first calculated distance and its
            //monitor are always recorded.
            if (nearestDevice == null || newFocusLocation.distance(upperLeft)
                    < shortestDistance) {
                //Tentatively record this monitor and update the shortest 
                //distance.
                shortestDistance = newFocusLocation.distance(upperLeft);
                nearestDevice = gd;
            }
            
            //Find the upper right corner and test against it.
            Point upperRight = new Point((int) testBounds.getMaxX(),
                (int) testBounds.getMinY());
            if (newFocusLocation.distance(upperRight) < shortestDistance) {
                //Tentatively record this monitor and update the shortest 
                //distance.
                shortestDistance = newFocusLocation.distance(upperRight);
                nearestDevice = gd;
            }
            
            //Find the lower left corner and test against it.
            Point lowerLeft = new Point((int) testBounds.getMinX(),
                (int) testBounds.getMaxY());
            if (newFocusLocation.distance(lowerLeft) < shortestDistance) {
                //Tentatively record this monitor and update the shortest 
                //distance.
                shortestDistance = newFocusLocation.distance(lowerLeft);
                nearestDevice = gd;
            }
            
            //Find the lower right corner and test against it.
            Point lowerRight = new Point((int) testBounds.getMaxX(),
                (int) testBounds.getMaxY());
            if (newFocusLocation.distance(lowerRight) < shortestDistance) {
                //Tentatively record this monitor and update the shortest 
                //distance.
                shortestDistance = newFocusLocation.distance(lowerRight);
                nearestDevice = gd;
            }
        }
        
        //This if statement is here for the compiler. It should have no effect
        //as the nearestDevice cannot be null.
        if (nearestDevice == null) {
            return;
        }
        
        /**
         * The nearest monitor has been found, so get its bound and use then
         * to adjust currentFocusLocation so it is on that monitor.
         */
        Rectangle chosenBounds = nearestDevice.getDefaultConfiguration()
                .getBounds();
        
        if (currentFocusLocation.x < chosenBounds.getMinX()) {
            currentFocusLocation.x = (int) chosenBounds.getMinX();
        }
        if (currentFocusLocation.x >= chosenBounds.getMaxX()) {
            currentFocusLocation.x = (int) chosenBounds.getMaxX() - 1;
        }
        if (currentFocusLocation.y < chosenBounds.getMinY()) {
            currentFocusLocation.y = (int) chosenBounds.getMinY();
        }
        if (currentFocusLocation.y >= chosenBounds.getMaxY()) {
            currentFocusLocation.y = (int) chosenBounds.getMaxY() - 1;
        }
    }
    
    /**
     * Gets the dimensions of the monitor that currently shows the window with
     * focus.
     * 
     * @return An object holding the dimensions of the monitor that currently
     * shows the window with focus.
     */
    public static Dimension getCurrentScreenResolution() {
        GraphicsDevice graphicsDevice = getCurrentScreen();
        DisplayMode mode = graphicsDevice.getDisplayMode();
        return new Dimension(mode.getWidth(), mode.getHeight());
    }
    
    /**
     * This function will position the given <code>Window</code> on the monitor
     * that currently shows the window with focus. It will either center the
     * given <code>Window</code> on that monitor or position the given
     * <code>Window</code> with a slight offset to the window with focus.
     * 
     * @param window The given <code>Window</code>.
     * @param shouldCenter True if the given <code>Window</code> should be
     * centered on the monitor that currently shows the window with focus; False
     * if an offset from the window with focus should be used.
     */
    public static void positionWindow(Window window, boolean shouldCenter) {
        //Must get current monitor.
        GraphicsDevice graphicsDevice = getCurrentScreen();
        
        //Get the height of the task bar.
        Insets screenInsets = Toolkit.getDefaultToolkit().
                getScreenInsets(graphicsDevice.getDefaultConfiguration());
        int taskBarHeight = screenInsets.bottom;
        
        if (shouldCenter) {
            //Center the window.
            Dimension screenSize = getCurrentScreenResolution();
            
            /**
             * To find the place for the upper-left corner of the window, this
             * code must find the left and top bounds of the current monitor in
             * the overall monitor coordinate system and add to those bounds
             * the appropriate distance to center the window on that monitor.
             */
            int leftWindowBound = (int) (graphicsDevice
                    .getDefaultConfiguration().getBounds().getMinX());
            int distanceFromLeftEdge = ((int) screenSize.getWidth()
                    - (int) window.getSize().getWidth()) / 2;
            int topWindowBound = (int) (graphicsDevice
                    .getDefaultConfiguration().getBounds().getMinY());
            int distanceFromTopEdge = ((int) (screenSize.getHeight() 
                    - taskBarHeight) - (int) window.getSize().getHeight()) / 2;
            window.setLocation(new Point(leftWindowBound + distanceFromLeftEdge,
                topWindowBound + distanceFromTopEdge));
        } else { //Not centered.
            /**
             * To find the place for the upper-left corner of the window, this
             * code must calculate a point with the desired offset. However.
             * this could put the lower-right corner of the window off the 
             * screen, so the code corrects for this.
             */
            int offset = 20;
            int offsetX = currentFocusLocation.x + offset;
            int offsetY = currentFocusLocation.y + offset;
            
            /**
             * Determine how far to the bottom or right the window can sit.
             */
            int largestXThatWillFit = (int) (graphicsDevice
                    .getDefaultConfiguration().getBounds().getMaxX())
                    - window.getWidth();
            int largestYThatWillFit = (int) (graphicsDevice
                    .getDefaultConfiguration().getBounds().getMaxY())
                    - taskBarHeight - window.getHeight();
            
            //Do checks to compare values and position window.
            int finalX = Math.min(offsetX, largestXThatWillFit);
            int finalY = Math.min(offsetY, largestYThatWillFit);
            window.setLocation(new Point(finalX, finalY));
        }
    }
}
