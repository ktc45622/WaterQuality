package weather.clientside.gui.client;

import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import weather.common.gui.component.IconProperties;

/**
 * This is a helper class for a series of 4 windows coded to act as one. The 
 * sequence is as follows:
 * 1.  ForecasterChooseLesson 
 * 2.  ForecasterInstructions 
 * 3.  ForecasterLessonWindow
 * 4.  ForecasterOverview
 * 
 * This is a fifth class is used to provide the above four with easier access to
 * <code>JOptionPane</code>. 
 * 
 * @author Brian Bankes
 */

public class ForecasterJOptionPaneFactory {
    /**
     * Function to show an information message.
     *
     * @param message The message in the box.
     * @param title The title of the box.
     * @param parent The parent component of the JOptionPane dialog to be 
     * displayed.
     */
    public static void showInfoPane(String message, String title, 
            Component parent) {
        JOptionPane pane = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); 
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Function to show an error message.
     *
     * @param message The message in the box.
     * @param title The title of the box
     * @param parent The parent component of the JOptionPane dialog to be 
     * displayed.
     */
    public static void showErrorPane(String message, String title, 
            Component parent) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Function to ask the user a question.
     *
     * @param message The message in the box.
     * @param title The title of the box.
     * @param parent The parent component of the JOptionPane dialog to be 
     * displayed.
     * @return True if the user answers yes, false otherwise.
     */
    public static boolean askUserQuestion(String message, String title, 
            Component parent) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue);
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }
}
