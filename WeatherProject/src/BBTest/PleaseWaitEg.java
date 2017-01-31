/**
 * This is a test program designed to test the code in the 
 * <code>ProgressDialog</code> class below,  That class was basically copied
 * into <code>TimedLoader</code> as an inner class. The structure of this file,
 * along with some of the code, is copied from here:
 * http://stackoverflow.com/questions/20269083/make-a-swing-thread-that-show-a-please-wait-jdialog
 * @author Brian Bankes
 */
package BBTest;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import weather.common.utilities.Debug;

public class PleaseWaitEg {

    public static void main(String[] args) {
        JButton showWaitBtn = new JButton(new ShowWaitAction("Show Wait Dialog"));
        JPanel panel = new JPanel();
        panel.add(showWaitBtn);
        JFrame frame = new JFrame("Frame");
        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}

class ShowWaitAction extends AbstractAction {

    protected static final int SLEEP_TIME = 3 * 1000;
    protected static final int MAX_VAL = 30;

    public ShowWaitAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {

                int sum = 0;
                for (int i = 1; i <= MAX_VAL; i++) {
                    sum += i;
                    Debug.println("Sum " + i + ": " + sum);
                    Thread.sleep(SLEEP_TIME);
                }
                return null;
            }
        };
        
        final ProgressDialog pd = new ProgressDialog();
        pd.startTimer();

        mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("state")) {
                    if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                        pd.stopTimer();
                        pd.hideDialog();
                    }
                }
            }
        });
        mySwingWorker.execute();
        pd.showDialog();
    }
}

class ProgressDialog implements ActionListener {

    private final JDialog progressDialog;
    private final JPanel progressPanel;
    private JLabel progressLabel;
    private final Timer timer;
    private final String dialogMessage;
    private int elapsedTime = 0;

    /**
     * Constructor to show a progress dialog for the purpose of loading
     * something.
     */
    public ProgressDialog() {
        progressDialog = new JDialog();
        progressDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        progressDialog.setLayout(new BorderLayout());
        dialogMessage = "Test";
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
        progressDialog.setLocationRelativeTo(null);
    }

    /**
     * Shows this dialog.
     */
    public void showDialog() {
        progressDialog.setVisible(true);
    }

    /**
     * Starts the timer.
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
            progressLabel = new JLabel("   Loading " + dialogMessage
                    + " - Time: " + elapsedTime + " seconds");
            progressPanel.add(progressLabel, BorderLayout.CENTER);
            progressPanel.revalidate();
        }
    }
}
