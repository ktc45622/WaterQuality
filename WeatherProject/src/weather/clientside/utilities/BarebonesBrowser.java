package weather.clientside.utilities;

import java.awt.Component;
import java.lang.reflect.Method;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.ScreenInteractionUtility;

/**
 * Bare Bones Browser Launch
 * Version 1.5 (December 10, 2005)
 * Minimal modifications made.
 * Found at http://www.centerkey.com/java/browser/
 * Supports: Mac OS X, GNU/Linux, Unix, Windows XP
 * Public Domain Software
 * 
 * Example Usage:
 *   String url = "http://www.centerkey.com/";
 *    BarebonesBrowser.openURL(url, this);    
 * 
 * Imported to Bloomsburg University Weather Project by Tyler Lusby   
 * @author Dem Pilafiam
 */
public class BarebonesBrowser {

    private static final String ERROR_MSG 
            = "Error attempting to launch web browser";

    /**
     * This method opens a URL in a browser. This class first finds which
     * operating system is in use, so that the appropriate system calls can be
     * made to open a browser window. 
     *
     * @param url The URL to be opened.
     * @param parent The window requesting that the URL be opened. It is used to
     * place <code>JOptionPane</code> error dialogs on the screen. If null, any
     * dialogs will be centered on the screen.
     * @return False if an occurs while attempting to open the URL; True 
     * otherwise.
     */
    public static boolean openURL(String url, Component parent) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else { //assume Unix or Linux
                String[] browsers = {
                    "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(
                            new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
            return true;
        } catch (Exception e) {
            JOptionPane pane = new JOptionPane(ERROR_MSG,
                    JOptionPane.ERROR_MESSAGE);
            JDialog dialog = pane.createDialog("Error");
            dialog.setIconImage(IconProperties.getTitleBarIconImage());
            dialog.setAlwaysOnTop(true);
            if (parent != null) {
                dialog.setLocationRelativeTo(parent);
            } else {
                ScreenInteractionUtility.positionWindow(dialog, true);
            }
            dialog.setVisible(true);
            return false;
        }
    }
}
