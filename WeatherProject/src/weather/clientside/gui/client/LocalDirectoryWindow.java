package weather.clientside.gui.client;

import java.awt.Dimension;
import java.io.*;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import weather.clientside.utilities.ResourceTreeManager;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.Debug;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;

/**
 * A window used for setting local working directory.
 *
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 */
public class LocalDirectoryWindow extends BUDialog {
    private String savechecker;
    private boolean mustSpecifyNew;
    private String storedDataFolderName = "Stored Data";
    private File oldUserRootDirectory = new File(PropertyManager
            .getLocalProperty("CORE_DIR") + File.separator + PropertyManager
            .getLocalProperty("ROOT_DIR") + File.separator + PropertyManager
            .getUserId());
    private File oldStoredDataDirectory = new File(PropertyManager
            .getLocalProperty("CORE_DIR")+ File.separator 
            + PropertyManager.getLocalProperty("ROOT_DIR")+ File.separator 
            + storedDataFolderName);

    /**
     * Initializes and creates the local properties window.
     * @param mustSpecifyNew Whether or not the user must change the default
     * save directory.
     */
    public LocalDirectoryWindow(boolean mustSpecifyNew) {
        super();
        initComponents();
        updateInfoLabel();
        savechecker = fldMainSaveDirectory.getText();
        this.btnClose.setVisible(!mustSpecifyNew);
        this.mustSpecifyNew = mustSpecifyNew;
        
        int width = 617 + this.getInsets().left + this.getInsets().right;
        int height = 190 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        super.postInitialize(true);
    }

    /**
     * Copies a directory to a new location with the help of its recursive version,
     * then deletes the old location. Note that the current user's local.properties file
     * can not be deleted, as it is still in use by the program when this function
     * would be called, and will not be copied.
     * 
     * @param oldDir The directory to be copied and then deleted.
     * @param newDir The directory to be moved to.
     * @param isCritical Whether or not the program must end if the move fails.
     */
    private void dirMove(File oldDir, File newDir, boolean isCritical) {
        //This flag is true once the copy is successful.
        boolean copyMade = false;
        
        while (!copyMade) {
            try {
                Debug.println("\nMoving these files and folders:");
                File localPropertiesFile = new File(
                        PropertyManager.getLocalPropertiesDirectory(), "local.properties");

                recursiveDirMove(oldDir, newDir, localPropertiesFile, 0);
                savechecker = fldMainSaveDirectory.getText();
                copyMade = true;
            } catch (Exception ex) {
                String message = "The data could not be copied.  Would you like"
                        + "\nto try again?";
                if (isCritical) {
                    message += "\nIMPORTANT: Choosing \"NO\' will end the program!";
                }
                String title = "Data Transfer Error";
                boolean result = StorageSpaceTester
                        .askUserStorageQuestion(message, title);
                if (result) {
                    //Go to next loop if choice is "YES."
                } else if (isCritical) {
                    //Set GeneralWeather.properties to indicate the program is not running.
                    PropertyManager.setGeneralProperty("ClientRunning", "false");
                    System.exit(-1);
                } else {
                    return;
                }
            }
        }
    }

    /**
     * Recursively copies a directory to a new location, then deletes the old location.
     * Note: this method should NOT be called directly from anywhere but the dirMove() method.
     * 
     * @param oldDir The directory to be copied and then deleted.
     * @param newDir the directory to be moved to.
     * @param hold A file to be held and not copied.
     */
    private void recursiveDirMove(File oldDir, File newDir, File hold, int ctr) throws FileNotFoundException, IOException {
        if (ctr == 15) {    // This is an extra safeguard to prevent directories from being endlessly created.
            return;
        }

        //Create a list of items in this directory.
        File[] filestomove = oldDir.listFiles();
        
        InputStream fin;
        OutputStream fout;

        //Traverse the list of directory items.
        for (int i = 0; i < filestomove.length; i++)
        {
            //If we come across a directory...
            if (filestomove[i].isDirectory())
            {
                //Then we need to rerun this function for that directory.
                File oldchilddir = filestomove[i];
                File newchilddir = new File(newDir.getPath()+ File.separator + filestomove[i].getName());
                newchilddir.mkdirs();
                recursiveDirMove(oldchilddir, newchilddir, hold, ctr+1);
            }
            else
            {
                //If we come across a file, just copy it (unless it is the hold file).
                if (filestomove[i].equals(hold)) {
                    continue;
                }

                File dest = new File(newDir.getPath()+ File.separator + filestomove[i].getName());
                fin = new FileInputStream(filestomove[i]);
                fout = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int len;
                while((len = fin.read(buffer)) > 0)
                {
                    fout.write(buffer, 0, len);
                }
                fin.close();
                fout.close();
                //Delete file when done.
                filestomove[i].delete();
                Debug.println("\t"+filestomove[i].getAbsoluteFile());
            }
        }
        
        Debug.println(oldDir.getAbsoluteFile());
        oldDir.delete();
        savechecker = fldMainSaveDirectory.getText();
    }

    /**
     * Deletes an entire directory.
     * @param dir The directory to be deleted.
     */
    void purgeDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                purgeDirectory(file);
            }
            file.delete();
        }
    }
    
    /**
     * Determines the size of a directory.
     * @param directory The directory.
     * @return The size in bytes.
     */
    private static long folderSize(File directory) {
        long length = 0;
        if (directory.canRead()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += folderSize(file);
                }
            }
        }
        return length;
    }
    
    /**
     * Updates the information status label with the necessary details about the
     * selected path.
     */
    private void updateInfoLabel() {
        //Get memory cutoff levels.
        long bytesForRecommendedChange = Long.parseLong(PropertyManager
                .getGeneralProperty("DIRECTORY_CHANGE_RECOMMENDED"));
        long bytesForRequiredChange = Long.parseLong(PropertyManager
                .getGeneralProperty("DIRECTORY_CHANGE_REQUIRED"));
        
        //Get location to test.
        String selectedDirPath = fldMainSaveDirectory.getText();
        File selectedDir = new File(selectedDirPath);
        
        //Find remaining space after potential move of user data.
        long potentialFreeSpace = selectedDir.getFreeSpace()
                - folderSize(oldUserRootDirectory);
        
        //Make Text.
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>Selected Path Information:</b><br/>");
        sb.append("Read/Write Status: ");
        if (selectedDir.canRead()) {
            sb.append("<font color = \"green\">Readable</font>");
        } else {
            sb.append("<font color = \"red\">Not Readable</font>");
        }
        sb.append(" - ");
        if (selectedDir.canWrite()) {
            sb.append("<font color = \"green\">Writable</font>");
        } else {
            sb.append("<font color = \"red\">Not Writable</font>");
        }
        sb.append("<br/>");
        sb.append("Disk Space Remaining (if your personal data is moved): ");
        sb.append(potentialFreeSpace).append(" bytes -<br>");
        for(int i = 0; i < 5; i++) {
            sb.append("&nbsp");
        }
        if (potentialFreeSpace < bytesForRequiredChange) {
            sb.append("<font color = \"red\">Below Program Requirement</font>");
        } else if (potentialFreeSpace < bytesForRecommendedChange) {
            sb.append("<font color = \"orange\">Below Program Recommendation</font>");
        } else {
            sb.append("<font color = \"green\">Meets Program Recommendation</font>");
        }
        sb.append("</html>");
        
        //Set lebel test.
        lblDriveInfo.setText(sb.toString());
    }

    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblSetDirectory = new javax.swing.JLabel();
        fldMainSaveDirectory = new javax.swing.JTextField();
        btnBrowseMainDirectory = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnDefault = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();
        lblDriveInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Weather Viewer - Set Default Working Directory");
        setAlwaysOnTop(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSetDirectory.setText("Set Default Working Directory:");
        getContentPane().add(lblSetDirectory, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, -1, -1));

        fldMainSaveDirectory.setEditable(false);
        fldMainSaveDirectory.setText(PropertyManager.getLocalProperty("CORE_DIR"));
        fldMainSaveDirectory.setToolTipText("Save local files into this directory");
        getContentPane().add(fldMainSaveDirectory, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, 511, 25));

        btnBrowseMainDirectory.setText("Browse");
        btnBrowseMainDirectory.setToolTipText("Pick a directory to save to");
        btnBrowseMainDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseMainDirectoryActionPerformed(evt);
            }
        });
        getContentPane().add(btnBrowseMainDirectory, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 40, 75, 25));

        btnSave.setText("Save");
        btnSave.setToolTipText("Save changes");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 153, 61, 25));

        btnClose.setText("Close");
        btnClose.setToolTipText("Close this window");
        btnClose.setPreferredSize(new java.awt.Dimension(67, 23));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 153, 67, 25));

        btnDefault.setText("Use Default Directory");
        btnDefault.setToolTipText("Reset default working directory to the home directory of the computer");
        btnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultActionPerformed(evt);
            }
        });
        getContentPane().add(btnDefault, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 153, 153, 25));

        btnBrowse.setMnemonic('d');
        btnBrowse.setText("Browse Data Directory");
        btnBrowse.setToolTipText("Browse the folder where data is stored");
        btnBrowse.setMaximumSize(new java.awt.Dimension(135, 23));
        btnBrowse.setMinimumSize(new java.awt.Dimension(135, 23));
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        getContentPane().add(btnBrowse, new org.netbeans.lib.awtextra.AbsoluteConstraints(177, 153, 159, 25));
        getContentPane().add(lblDriveInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 77, 593, 64));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * The actions taken when the "Browse" button for local save directory is
     * clicked. This opens a WeatherFileChooser that allows the user to select a
     * directory. The default directory is the user's current save directory.
     *
     * @param evt The event of the button being pressed.
     */
    private void btnBrowseMainDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseMainDirectoryActionPerformed
        //Construct file chooser
        String chosenDirectory = WeatherFileChooser
                .openDirectoryChooser(new File(PropertyManager
                .getLocalProperty("CORE_DIR")), "Select Destination...", null, 
                this);
        if (chosenDirectory != null) {
            //Get selection
            fldMainSaveDirectory.setText(chosenDirectory);
            updateInfoLabel();
        }
    }//GEN-LAST:event_btnBrowseMainDirectoryActionPerformed
    
    /**
    * The actions taken when the Save button is pressed. It verifies the
    * selected directory is suitable.  If not, it opens a new "Browse."
    * @param evt The event of the save button being pressed.
    */
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        //Make sure location has been changed.
        if (PropertyManager.getLocalProperty("CORE_DIR")
                .equals(fldMainSaveDirectory.getText())) {
            if (mustSpecifyNew) {
                StorageSpaceTester
                        .showStorageMessage("You must chose a new location.",
                        "Please Change Location");
            } else {
                dispose();
            }
            return;
        }
        
        /*Check if selection is suitable*/
        
        //Get memory cutoff levels.
        long bytesForRecommendedChange = Long.parseLong(PropertyManager
                .getGeneralProperty("DIRECTORY_CHANGE_RECOMMENDED"));
        long bytesForRequiredChange = Long.parseLong(PropertyManager
                .getGeneralProperty("DIRECTORY_CHANGE_REQUIRED"));
        
        //Variable for user warning.
        boolean wasUserWarned = false;

        String selectedRootDirPath = fldMainSaveDirectory.getText()
                + File.separator + PropertyManager.getLocalProperty("ROOT_DIR");

        //Get location to test.
        File selectedRootDir = new File(selectedRootDirPath);
        
        //If ROOT_DIR is not pessent, mut directly test field path.
        if (!selectedRootDir.exists()) {
            selectedRootDirPath = fldMainSaveDirectory.getText();

            //Get location to test.
            selectedRootDir = new File(selectedRootDirPath);
        }

        //Check for disk access.
        if (!selectedRootDir.canWrite() || !selectedRootDir.canRead()) {
            String message =
                    "The currrent storage location is not readable and/or\n"
                    + "writable.  Please press YES to select a different"
                    + " location\nor NO to retry the current one.";
            String tilte = "Select New Working Directory?";
            boolean result = StorageSpaceTester
                    .askUserStorageQuestion(message, tilte);
            if (result) {
                btnBrowseMainDirectoryActionPerformed(evt);
            } else {
               btnSaveActionPerformed(evt); 
            }
            //Stop this call.
            return;
        }

        //Find remaining space after potential move of user data.
        long potentialFreeSpace = selectedRootDir.getFreeSpace()
                - folderSize(oldUserRootDirectory);
        
        //Check for available disk space.
        if (potentialFreeSpace < bytesForRequiredChange) {
            String message =
                    "The selected storage location is very low on memory.\n"
                    + "Please select a different location.";
            String tilte = "Very Low Memory";
            StorageSpaceTester.showStorageMessage(message, tilte);
            btnBrowseMainDirectoryActionPerformed(evt);
            //Stop this call.
            return;
        }


        if (potentialFreeSpace < bytesForRecommendedChange) {
            String message =
                    "The selected storage location is low on memory.\n"
                    + "Would you like to select a different location.";
            String tilte = "Low Memory";
            boolean result = StorageSpaceTester.askUserStorageQuestion(message, tilte);
            if (result) {
                btnBrowseMainDirectoryActionPerformed(evt);
                //Stop this call.
                return;
            } else {
                //Record that user was warned so we don't reset storage tester.
                wasUserWarned = true;
            }
        }
        
        /*Change storage folder*/
        File newUserRootDirectory = new File(fldMainSaveDirectory.getText()
               + File.separator + PropertyManager.getLocalProperty("ROOT_DIR")
               + File.separator + PropertyManager.getUserId());
        File newStoredDataDirectory = new File(fldMainSaveDirectory.getText()
                + File.separator + PropertyManager.getLocalProperty("ROOT_DIR")
                + File.separator + storedDataFolderName);
        
        newUserRootDirectory.mkdirs();
        newStoredDataDirectory.mkdirs();

        //Move personal data. 
        dirMove(oldUserRootDirectory, newUserRootDirectory, true);
        
        //Change stored property.
        PropertyManager.setLocalProperty("CORE_DIR", 
                fldMainSaveDirectory.getText());
        
        //Calcutate space after stored data would be move.
        potentialFreeSpace = selectedRootDir.getFreeSpace()
                - folderSize(oldStoredDataDirectory);
        
        //Check if stored data should be moved.
        if (potentialFreeSpace < bytesForRequiredChange) {
            StorageSpaceTester.showStorageMessage(
                    "There is not enough space to copy the stored data.", 
                    "Insufficient Space");
        } else if (potentialFreeSpace < bytesForRecommendedChange
                && !wasUserWarned) {
            boolean result1 = StorageSpaceTester.askUserStorageQuestion(
                    "Moving the stored dats from your old working\n"
                    + "directory will put the available memory below the\n"
                    + "recommended level.  Would you like to move it\n"
                    + "anyway?",
                    "Move Data?");
            if (result1) {
                boolean result2 = StorageSpaceTester.askUserStorageQuestion(
                        "This could take a while.  Are you sure you want to "
                        + "copy\nthe stored data?",
                        "Are You Sure?");
                if (result2) {
                    //Record that user was warned about space so we don't 
                    //reset storage tester.
                    wasUserWarned = true;
                    //Remove old data to prevent multiple copies.
                    purgeDirectory(newStoredDataDirectory);
                    dirMove(oldStoredDataDirectory, newStoredDataDirectory,
                            false);
                }
            }
        } else {
            boolean result1 = StorageSpaceTester.askUserStorageQuestion(
                    "Would you like the stored dats from your old working\n"
                    + "directory moved to the new directory?",
                    "Move Data?");
            if (result1) {
                boolean result2 = StorageSpaceTester.askUserStorageQuestion(
                        "This could take a while.  Are you sure you want to "
                        + "copy\nthe stored data?",
                        "Are You Sure?");
                if (result2) {
                    //Remove old data to prevent multiple copies.
                    purgeDirectory(newStoredDataDirectory);
                    dirMove(oldStoredDataDirectory, newStoredDataDirectory,
                            false);
                }
            }
        }
       
        //Finish Up
        ResourceTreeManager.initializeData();
        if (!wasUserWarned) {
            StorageSpaceTester.resetTestFlag();
        }
        this.dispose();
}//GEN-LAST:event_btnSaveActionPerformed

    /**
    * The actions taken if the cancel button is pressed. This closes the local
    * preferences window without any saving.
    * @param evt The event of the cancel button being pressed.
    */
    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        if(!savechecker.equals(fldMainSaveDirectory.getText())) {
            if(leaveWithoutSaving()) {
                dispose();
            }
        } else {
            dispose();
        }
}//GEN-LAST:event_btnCloseActionPerformed

    /**
    * The actions taken if the defaults button is pressed. This sets all the
    * components corresponding to local properties to their default values
    * based on the property file containing all the defaults.
    * @param evt The event of the default button being pressed.
    */
    private void btnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultActionPerformed
        if(PropertyManager.getDefaultProperty("CORE_DIR").equals("")) {
            fldMainSaveDirectory.setText(System.getProperty("user.home"));
        } else {
            fldMainSaveDirectory.setText(PropertyManager
                    .getDefaultProperty("CORE_DIR"));
        }
        updateInfoLabel();
        savechecker = fldMainSaveDirectory.getText();
}//GEN-LAST:event_btnDefaultActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        try {
            // Open an explorer window where the data is saved
            // (Windows specific).
            //Drive letter:\\ will never be valid for a local drive.
            String dataDirectory = CommonLocalFileManager.getDataDirectory().
                    replace(":"+File.separator+File.separator, ":"+File.separator);
            Runtime.getRuntime().exec("explorer " + dataDirectory);
            Debug.println("Current data directory: "+dataDirectory);
            savechecker = fldMainSaveDirectory.getText();
        }
        catch (IOException ex) {
            WeatherLogger.log(Level.FINE, "Could not open Browse Data Directory Window.", ex);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (mustSpecifyNew) {
            JOptionPane.showMessageDialog(this, "You must choose a new working directory.",
                    "Choose New Directory", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!savechecker.equals(fldMainSaveDirectory.getText())) {
            if(leaveWithoutSaving()) {
                dispose();
            }
        } else {
            dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * Will display a "Leave without saving changes?" window, and returns an
     * answer.
     * @return True if YES, false if NO
     * @author Eric Lowrie (2012)
     */
    private boolean leaveWithoutSaving() {
        if (JOptionPane.showConfirmDialog(this, "Leave without saving changes?",
                "Weather Viewer", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnBrowseMainDirectory;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDefault;
    private javax.swing.JButton btnSave;
    private javax.swing.JTextField fldMainSaveDirectory;
    private javax.swing.JLabel lblDriveInfo;
    private javax.swing.JLabel lblSetDirectory;
    // End of variables declaration//GEN-END:variables
}
