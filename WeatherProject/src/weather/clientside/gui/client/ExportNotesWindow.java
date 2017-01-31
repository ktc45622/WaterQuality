package weather.clientside.gui.client;

import java.awt.Dimension;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import weather.clientside.manager.DiaryManager;
import weather.clientside.utilities.BarebonesBrowser;
import weather.clientside.utilities.ClientSideLocalFileManager;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.note.InstructorNote;
import weather.common.data.resource.HTMLFormattedString;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.FormatManager;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.PropertyManager;
/**
 * This window exports class notes to either HTML or Text format.
 *
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 * 
 * @version Spring 2010
 */
public class ExportNotesWindow extends BUDialog {
    DefaultListModel diaryModel;
    DefaultListModel changableModel;
    String exportedFileName;    
    SimpleDateFormat dateformatMMDDYYYY;
    Date[] dates;
    String exportFormat = "html";
    String dateFormatString = PropertyManager.getGeneralProperty("dateFormatString");
    InstructorNote note;
    
    /**
     * Creates a new ExportNoteAndDiaryWindow that exports the provided note.
     * @param note The note to export.
     */
    public ExportNotesWindow(InstructorNote note)   {
        super();
        initComponents();
        setDestinationPath();        
        this.note = note;
        dateformatMMDDYYYY = new SimpleDateFormat(dateFormatString);
        dateformatMMDDYYYY.setTimeZone(DiaryManager.getResource().getTimeZone()
                .getTimeZone());
        radgrpFormat.setSelected(radTXT.getModel(), true);
        setExportFormat();
        int width = 440 + this.getInsets().left + this.getInsets().right;
        int height = 151 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }

    /**
     * Decides which format the entries should be exported to.
     * The options are TXT and CSV.
     */
    private void setExportFormat() {
        if (exportFormat.equals("html")) {
            radHTML.setSelected(true);
        }
        else {
            radTXT.setSelected(true);
        }
    }

    /**
     * This method return diaryModel attribute of this class.
     * @return The DefaultListModel attribute.
     */
    public DefaultListModel getDairyModel()
    {
        return diaryModel;
    }
     /**
     * This method return diaryModel attribute of this class.
     * @return The DefaultListModel attribute.
     */
    public DefaultListModel getChangableModel()
    {
        return changableModel;
    }
    
    /**
     * Sets the destination path.
     */
    private void setDestinationPath()
    {
        destinationTextField.setText(CommonLocalFileManager.getNoteDirectory());
    }
    
    /**
     * Exports the passed note into the specified file type.
     */
    private void exportNote() {
        StringBuilder filename = new StringBuilder();
        String formattedNote;

        filename.append(note.getNoteTitle());
        filename.append("_");
        filename.append(dateformatMMDDYYYY.format(note.getStartTime()));
        filename.append("_");
        filename.append(dateformatMMDDYYYY.format(note.getEndTime()));
            
        // Change the filename depending on the save mode.
        formattedNote = exportFormat.equals("html") ? FormatManager.noteToHTML(note) :
                                                      FormatManager.noteToText(note);
        

        filename.append(".").append(exportFormat);
        HTMLFormattedString exportedNote = new HTMLFormattedString(formattedNote);
        File savedFile = ClientSideLocalFileManager
                .saveSpecifiedFileReturnFile(destinationTextField.getText(),
                filename.toString(), exportedNote, "." + exportFormat, this);
        if (savedFile != null) {
            if (exportFormat.equals("html")) {
                int resp = JOptionPane.showConfirmDialog(this,
                        "The file has been saved. Open in browser?", "File Saved", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    BarebonesBrowser.openURL(savedFile.toString(), this);
                }
            }
            else {
                int resp = JOptionPane.showConfirmDialog(this,
                        "The file has been saved. Open in default application?", "File Saved", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    try {
                        java.awt.Desktop.getDesktop().open(savedFile);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                }
            }
            //Test for remaining space in application home, which has
            //no effect if the save was not there.
            StorageSpaceTester.testApplicationHome();
            dispose();
        } else {
            //Test for remaining space in application home, which has
            //no effect if the save was not there.
            StorageSpaceTester.testApplicationHome();
        }
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

        radgrpFormat = new javax.swing.ButtonGroup();
        lblFormat = new javax.swing.JLabel();
        radHTML = new javax.swing.JRadioButton();
        radTXT = new javax.swing.JRadioButton();
        destinationLabel = new javax.swing.JLabel();
        destinationTextField = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblTop = new javax.swing.JLabel();

        setTitle("Weather Viewer - Export Class Notes");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblFormat.setText("Export Format:");
        getContentPane().add(lblFormat, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 44, 86, 16));

        radgrpFormat.add(radHTML);
        radHTML.setText("Web Page (HTML)");
        radHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radHTMLActionPerformed(evt);
            }
        });
        getContentPane().add(radHTML, new org.netbeans.lib.awtextra.AbsoluteConstraints(167, 40, 133, 25));

        radgrpFormat.add(radTXT);
        radTXT.setText("Text");
        radTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radTXTActionPerformed(evt);
            }
        });
        getContentPane().add(radTXT, new org.netbeans.lib.awtextra.AbsoluteConstraints(106, 40, 53, 25));

        destinationLabel.setText("Destination:");
        getContentPane().add(destinationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 77, 68, 16));

        destinationTextField.setToolTipText("");
        getContentPane().add(destinationTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(88, 77, 249, 22));

        btnBrowse.setText("Browse..");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        getContentPane().add(btnBrowse, new org.netbeans.lib.awtextra.AbsoluteConstraints(345, 77, 83, 25));

        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        getContentPane().add(btnExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(99, 114, 71, 25));

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 114, 71, 25));

        lblTop.setText("<html><b>Please select the file type and destination of the export.</b></html>");
        getContentPane().add(lblTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 367, 16));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This method is called when browse button is pressed. This allow user to
     * select different directory for exporting Daily Diary.
     *
     * @param evt The event that the Browse Button is pressed.
     */
    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {
        String chosenDirectory = WeatherFileChooser
                .openDirectoryChooser(new File(CommonLocalFileManager
                .getNoteDirectory()), "Select Destination...", null, this);
        if (chosenDirectory != null) {
            File selectedFile = new File(chosenDirectory);
            destinationTextField.setText(selectedFile.getPath());
        }
    }
    
    /**
     * This method is called when the Export button is clicked. If the date
     * range, as provided by the user, is valid, the selected entries are
     * exported. This is for the simple mode for exporting.
     *
     * @param evt The event that the Export button is clicked.
     */
    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        exportNote();
}//GEN-LAST:event_btnExportActionPerformed

    /**
     * This method is called if the Cancel button is clicked.
     * The window is closed.
     * 
     * @param evt The event that the Cancel button is clicked.
     */
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
}//GEN-LAST:event_btnCancelActionPerformed

//GEN-FIRST:event_btnBrowseActionPerformed
 
//GEN-LAST:event_btnBrowseActionPerformed

    private void radTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radTXTActionPerformed
        exportFormat = "txt";
    }//GEN-LAST:event_radTXTActionPerformed

    /**
     * This method is called if the HTML format radio button is clicked.
     * The exported file will be of this type.
     * 
     * @param evt The event that the HTML radio button is clicked.
     */
    private void radHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radHTMLActionPerformed
        exportFormat = "html";
    }//GEN-LAST:event_radHTMLActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnExport;
    private javax.swing.JLabel destinationLabel;
    private javax.swing.JTextField destinationTextField;
    private javax.swing.JLabel lblFormat;
    private javax.swing.JLabel lblTop;
    private javax.swing.JRadioButton radHTML;
    private javax.swing.JRadioButton radTXT;
    private javax.swing.ButtonGroup radgrpFormat;
    // End of variables declaration//GEN-END:variables
}
