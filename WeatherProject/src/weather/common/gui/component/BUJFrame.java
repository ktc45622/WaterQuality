package weather.common.gui.component;

import java.awt.Component;
import java.awt.Image;
import javax.swing.JFrame;
import weather.common.utilities.ScreenInteractionUtility;

/**
 * Frame that performs common operations on initialization, such as setting the
 * icon and centering the window.
 *
 * Usage: Call postInitialize after adding all content. The default constructor
 *        is called in the subclass constructor. To have more control, use the
 *        super keyword and choose one of the constructors of this class.
 *
 *        The correct way to use this class is to create a new class with the
 *        NetBeans form builder then change the extending class to this one.
 *
 * @author Eric Subach (2011)
 */
public class BUJFrame extends JFrame {
    private static Image icon; 

    static {
        icon = IconProperties.getTitleBarIconImage();
    }


    public BUJFrame () {
        setIconImage(icon);
        
        /**
         * Listeners needed to maintain the current screen location for the
         * <code>ScreenInteractionUtility</code>.
         *
         */
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            /**
             * After gaining focus, this code must update current screen
             * location for the <code>ScreenInteractionUtility</code>.
             *
             * @param evt Not used.
             */
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                ScreenInteractionUtility.setFocusLocation(getBUJFrame()
                        .getLocationOnScreen());
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            /**
             * After a window move, this code must update current screen
             * location for the <code>ScreenInteractionUtility</code>.
             *
             * @param evt Not used.
             */
            @Override
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                ScreenInteractionUtility.setFocusLocation(getBUJFrame()
                        .getLocationOnScreen());
            }
        });
    }

    public BUJFrame (String title) {
        this();
        setTitle(title);
    }

    /**
     * A function to make this object available to the inner classes.
     * @return This object as a <code>Component</code>.
     */
    private Component getBUJFrame() {
        return this;
    }

    /**
     * Operations to be performed after all content has been added, such as
     * making the window visible and causing it to appear on the screen.
     * 
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    public void postInitialize(boolean shouldCenter) {
        ScreenInteractionUtility.positionWindow(this, shouldCenter);
        setVisible(true);
    }
}
