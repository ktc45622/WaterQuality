package weather.common.gui.component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import weather.common.utilities.ScreenInteractionUtility;
import weather.common.utilities.WeatherException;

/**
 * Class creates a splash screen to be displayed when the program starts.  Note
 * that every module that displays on screen windows should display this splash
 * screen because it initializes the <code>ScreenInteractionUtility</code>.
 *
 * This class was modeled after a class found online at:
 * http://www.java-tips.org/java-se-tips/javax.swing/how-to-implement-a-splash-screen-for-an-applic-2.html
 *
 * @author Bloomsburg University
 * @author Trevor Erdley - Spring 2011
 */
public class SplashScreenWindow extends JFrame {

    private boolean clickedContinue = false;
    private boolean alreadyUpdated = false;
    private float alpha = 0f;
    
    /**
     * A function to make this object available to the inner classes.
     * @return This object as a <code>Component</code>.
     */
    private Component getThisWindow() {
        return this;
    }

    public SplashScreenWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        JLabel l = new JLabel(IconProperties.getSplashScreenIconImage());
        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize
                = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        this.setTitle("Bloomsburg Weather Viewer");
        this.setIconImage(IconProperties.getTitleBarIconImage());
        setLocation(screenSize.width / 2 - (labelSize.width / 2),
                screenSize.height / 2 - (labelSize.height / 2));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clickedContinue = true;
                
                //Set the program's initial sceen location.
                ScreenInteractionUtility.setFocusLocation(getThisWindow()
                        .getLocationOnScreen());
                
                setVisible(false);
                dispose();
            }
        });

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                clickedContinue = true;
                
                //Set the program's initial sceen location.
                ScreenInteractionUtility.setFocusLocation(getThisWindow()
                        .getLocationOnScreen());
                
                setVisible(false);
                dispose();
            }
        });

        setVisible(true);
        while (clickedContinue == false) {
            try {
                updateOverlay();
            } catch (Exception ex) {
                WeatherException e = new WeatherException("The splash screeen has"
                        + "encountered an error and will close.  Now loading the login screen.");
                e.show();
                clickedContinue = true; //Make sure we exit the loop we are in
                //Set the program's initial sceen location.
                ScreenInteractionUtility.setFocusLocation(getThisWindow()
                        .getLocationOnScreen());
                dispose();
            }
        }
    }

    /**
     * paint Overridden paint method to enable an overlay effect.
     *
     * @param g The graphics object.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    /**
     * Checks the state of the window and if it is not active will enable the
     * repaint method to draw an overlay to show its status.
     */
    private void updateOverlay() throws InterruptedException {
        if (!this.isActive()) {
            alpha = 0.65f;
            if (!alreadyUpdated) {
                this.repaint();
                alreadyUpdated = true;
            }
        } else if (this.isActive()) {
            alreadyUpdated = false;
            alpha = 0.0f;
            this.repaint();
        }
        Thread.sleep(100);
    }
}
