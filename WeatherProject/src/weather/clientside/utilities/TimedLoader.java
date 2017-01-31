/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.clientside.utilities;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import weather.common.utilities.ScreenInteractionUtility;

/**
 * This class is designed to perform a given loading task while displaying a
 * model dialog that shows the elapsed time of the load. Instances should be
 * created form within the method perform the load. This allows the 
 * <code>doLoading()</code> method to use class and local variables from the
 * calling code as in the below example. This example shows the to methods that
 * must be overridden. The second is <code>getLabelText()</code>, which provides
 * a description of what is being loaded to show as part of the dialog text. The
 * example is taken form <code>ClientControlImpl</code>:
 * 
 * <code>
 *      //Start TimedLoader
 *      TimedLoader loader = new TimedLoader() {
 *          @Override
 *          protected String getLabelText() {
 *              return "Bloomsburg Weather Viewer";
 *          }
 *
 *          @Override
 *          protected void doLoading() {
 *              //Set the default range for downloading resources if not in selective
 *              //login mode
 *              if (!isSelectiveModePicked) {
 *                  Debug.println("Setting default resource range.");
 *                  ResourceTimeManager.setResourceRange(ResourceTimeManager
 *                          .getDefaultRange());
 *              }
 * 
 *              //Test for space to write files.
 *              StorageSpaceTester.testApplicationHome();
 *
 *              //Load resource tree
 *              ResourceTreeManager.initializeData();
 *
 *              Debug.println("Constructing new MainApplicationWindow.");
 *              mainApplicationWindow = 
 *                      new MainApplicationWindow(appcontrolsystem,
 *                      false, null);
 *          }    
 *      };
 *      loader.execute();
 * </code>
 * 
 * Note that the overridden methods should not, and mostly cannot be called.
 * After an instance is created, execute should, as above, be called to perform
 * the load.
 * 
 * NOTE: Be careful in testing any code that directly or indirectly set a form
 * visible or repaints a form. Although this sometimes works, it can throw 
 * obscure exceptions when used in doLoading() and should be thoroughly tested.
 * 
 * @author Brian Bankes
 */
public abstract class TimedLoader {

    /**
     * Method which must be overridden to provide a description of what is being
     * loaded. It should not be called once overridden, as detailed in the class
     * definition file.  If it is overridden to return null, an empty string 
     * will be used instead.
     * 
     * @return A description of what is being loaded.
     */
    protected abstract String getLabelText();

    /**
     * Method which must be overridden with the code that performs the desired
     * load. It should not be called once overridden, as detailed in the class
     * definition file. 
     * 
     * NOTE: Be careful in testing any code that directly or indirectly set a
     * form visible or repaints a form. Although this sometimes works, it can
     * throw obscure exceptions when used in doLoading() and should be
     * thoroughly tested.
     */
    protected abstract void doLoading();
    
    /**
     * Instance of inner <code>ProgressDialog</code> class.
     */
    private final ProgressDialog dialog = new ProgressDialog();
    
    /**
     * Instance of inner <code>LoadingWorker</code> class.
     */
    private final LoadingWorker worker = new LoadingWorker();
    
    /**
     * Performs the load while showing the elapsed time in a modal dialog. This
     * should be the only method after the instance is created, as detailed in
     * the class definition file.
     */
    public final void execute() {
        dialog.startTimer();
        worker.execute();
        dialog.showDialog();
    }
    
    /**
     * Inner class needed to do all loading on a different thread.
     */
    private class LoadingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            doLoading();
            return null;
        }
        
        @Override
        protected void done() {
            dialog.stopTimer();
            dialog.hideDialog();
        }
    }

    /**
     * Inner class to show a progress dialog for the purpose of loading
     * something.
     */
    private class ProgressDialog implements ActionListener {

        private final JDialog progressDialog;
        private final JPanel progressPanel;
        private JLabel progressLabel;
        private final Timer timer;
        private String labelText;
        private int elapsedTime = 0;

        /**
         * Constructor to show a progress dialog for the purpose of loading
         * something.
         */
        public ProgressDialog() {
            progressDialog = new JDialog(); 
            progressDialog.setModalityType(ModalityType.APPLICATION_MODAL);
            progressDialog.setLayout(new BorderLayout());
            labelText = getLabelText();
            if (labelText == null) {
                labelText = "";
            }
            progressDialog.setUndecorated(true);
            progressDialog.setSize(450, 50);
            progressPanel = new JPanel();
            progressPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
            progressPanel.setLayout(new BorderLayout());
            timer = new Timer(1000, this);
            progressLabel = new JLabel();
            progressLabel.setHorizontalAlignment(JLabel.CENTER);
            timer.setRepeats(true);
            progressPanel.add(progressLabel, BorderLayout.CENTER);
            progressDialog.add(progressPanel, BorderLayout.CENTER);
            timer.setInitialDelay(0);
            ScreenInteractionUtility.positionWindow(progressDialog, true);
            progressDialog.setAlwaysOnTop(true);
        }

        /**
         * Shows this modal dialog on the screen.
         */
        public void showDialog() {
            progressDialog.setVisible(true);
        }
        
        /**
         * Starts the timer in the background.
         */
        public void startTimer() {
            timer.start();
        }

        /**
         * Hides this dialog.
         */
        public void hideDialog() {
            progressDialog.dispose();
        }
        
        /**
         * Stops the timer.
         */
        public void stopTimer() {
            timer.stop();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == timer) {
                elapsedTime++;
                progressPanel.removeAll();
                progressLabel = new JLabel("   Loading " + labelText 
                        + " - Time: " + elapsedTime + " seconds");
                progressPanel.add(progressLabel, BorderLayout.CENTER);
                progressPanel.revalidate();
            }
        }
    }
}
