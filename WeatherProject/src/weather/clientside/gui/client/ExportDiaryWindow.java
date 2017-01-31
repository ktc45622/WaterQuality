package weather.clientside.gui.client;

import java.awt.Dimension;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import weather.clientside.manager.DiaryManager;
import weather.clientside.manager.NotesAndDiaryPanelManager;
import weather.clientside.utilities.BarebonesBrowser;
import weather.clientside.utilities.ClientSideLocalFileManager;
import weather.clientside.utilities.ListCellDateRenderer;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.diary.DailyEntry;
import weather.common.data.resource.HTMLFormattedString;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.FormatManager;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.PropertyManager;
/**
 * This window exports diary entries to text, HTML or CSV format. 
 *
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 * 
 * @version Spring 2010
 */
public class ExportDiaryWindow extends BUDialog {
    private DefaultListModel<Date> diaryModel;
    private DefaultListModel<Date> changeableModel;  
    private SimpleDateFormat dateformatMMDDYYYY;
    private Date[] dates;
    private String exportFormat = "html";
    private String dateFormatString 
            = PropertyManager.getGeneralProperty("dateFormatString");
    private ListCellDateRenderer renderer
            = new ListCellDateRenderer(dateFormatString);
    private ArrayList<DailyEntry> resourceEntries;
    private NotesAndDiaryPanelManager manager;
    
    /** 
     * Creates new form ExportNoteAndDiaryWindow.
     * @param manager The parent <code>NotesAndDiaryPanelManager</code>.
     */
    public ExportDiaryWindow(NotesAndDiaryPanelManager manager) {
        super();
        initComponents();
        this.manager = manager;
        diaryModel = new DefaultListModel<>();
        changeableModel = new DefaultListModel<>();
        resourceEntries = DiaryManager
                .getAllEntriesRorCurrentDiaryResourceWithData();
        renderer.setTimeZone(DiaryManager.getResource().getTimeZone()
                .getTimeZone());
        lstViewEntries.setModel(diaryModel);
        lstEditEntries.setModel(changeableModel);
        lstViewEntries.setCellRenderer(renderer);
        lstEditEntries.setCellRenderer(renderer);
        cmbStartDate.setRenderer(renderer);
        cmbEndDate.setRenderer(renderer);        
        dateformatMMDDYYYY = new SimpleDateFormat(dateFormatString);
        dateformatMMDDYYYY.setTimeZone(DiaryManager.getResource().getTimeZone()
                .getTimeZone());
        fillDateComboBoxes();
        defaultComboBoxSelections();
        fillDiaryModel();                   
        setDestinationPath();
        radgrpFormat.setSelected(radTXT.getModel(), true);
        lblCurrentResource.setText("<html><b>Camera Location: "
                + DiaryManager.getResource().getResourceName() + "</b></hlml>");
        setExportFormat();
        int width = 655 + this.getInsets().left + this.getInsets().right;
        int height = 491 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }
    
    /**
     * Fills the combo boxes with all dates of existing entries which are used
     * for selecting a date range.
     */
    private void fillDateComboBoxes() {
        cmbStartDate.removeAllItems();
        cmbEndDate.removeAllItems();
        int comboEntryCount = Integer.parseInt(PropertyManager.getGUIProperty("DailyDiaryDropdownListMaxLength"));
        for (int count = (resourceEntries.size() < comboEntryCount 
                ? 0 : (resourceEntries.size() - comboEntryCount)); 
                count < resourceEntries.size(); count++) {
            cmbStartDate.addItem(resourceEntries.get(count).getDate());
            cmbEndDate.addItem(resourceEntries.get(count).getDate());
        }
    }

    /**
     * Ensures that the start date is equal to or earlier than the end date.
     * 
     * @return True if the dates are properly chosen.
     */
    private boolean checkDateRange() {        
        Date start = (Date) cmbStartDate.getSelectedItem();
        Date end = (Date) cmbEndDate.getSelectedItem();
        if (!start.equals(end) && !start.before(end)) {
            JOptionPane.showMessageDialog(this, "Start date must be the same as or "
                + "earlier than the end date", "Invalid Date Selection", JOptionPane.ERROR_MESSAGE);                     
            defaultComboBoxSelections();   
            fillDiaryModel();
            return false;
        }
        return true;
    }

    /**
     * Decides which format the entries should be exported to.
     * The options are TXT, HTML and CSV.
     */
    private void setExportFormat() {
        if (exportFormat.equals("html")) {
            radHTML.setSelected(true);
        }
        else if (exportFormat.equals("csv")) {
            radCSV.setSelected(true);
        }
        else {
            radTXT.setSelected(true);
        }
    }

    /**
     * Selects the default values for the combo boxes, which is the first
     * date for the start date and the last one for the end date.
     */
    private void defaultComboBoxSelections() {
        cmbStartDate.setSelectedIndex(0);
        cmbEndDate.setSelectedIndex(cmbEndDate.getItemCount() - 1);
    }

    /**
     * Fills the list with all entries between the selected range.
     */
    private void fillDiaryModel() {
        diaryModel.removeAllElements();
        changeableModel.removeAllElements();
        
        Date startDate = (Date) cmbStartDate.getSelectedItem();
        Date endDate = (Date) cmbEndDate.getSelectedItem();        
        if (startDate.after(endDate)) {
            return;
        }
        for (int i = 0; i < resourceEntries.size(); i++) {
            Date entryDate = resourceEntries.get(i).getDate();
            if ((entryDate.after(startDate) && entryDate.before(endDate))
                    || entryDate.equals(startDate) 
                    || entryDate.equals(endDate)) {
                diaryModel.addElement(entryDate);
            }
                
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
        return changeableModel;
    }
    
    /**
     * Sets the destination path.
     */
    private void setDestinationPath()
    {
        destinationTextField.setText(CommonLocalFileManager.getNoteDirectory());
    }

    /**
     * Moves the selected entries from one list to the other.
     * 
     * @param entriesToMove The selected entries.
     * @param toModel The model to move the entries to.
     * @param fromModel The model the entries are being moved from.
     */
    private void swapLists(int[] entriesToMove, DefaultListModel<Date> toModel, DefaultListModel<Date> fromModel) {
        for (int i = 0; i < entriesToMove.length; i++) {
            Date fromElement = (Date)fromModel.getElementAt(entriesToMove[i]);
            if (toModel.isEmpty()) {
                toModel.addElement(fromElement);
            }
            else {
                for (int j = 0; j < toModel.size(); j++) {
                    Date toElement = (Date)toModel.getElementAt(j);
                    if (fromElement.before(toElement)) {
                        toModel.add(j, fromElement);
                        break;
                    }
                    else if (j == toModel.size()-1) {
                        toModel.addElement(fromElement);
                        break;
                    }
                }
            }
        }
        for (int i = entriesToMove.length-1; i >= 0; i--) {
            fromModel.remove(entriesToMove[i]);
        }
    }

    /**
     * Exports entries based on the date range 
     * and of the file type specified by the user.
     */
    private void exportDiary() {
        StringBuilder filename = new StringBuilder();
        String formattedDiary;

        filename.append(DiaryManager.getUserLastName().replace(' ', '_'))
                .append('_').append(DiaryManager.getUserFirstName())
                .append("_Diary_");

        //Gets Dates for File Name.
        setSelectedDates();

        // Use the start date and end date in the filename.
        filename.append(dateformatMMDDYYYY.format(dates[0]));
        filename.append("_");
        filename.append(dateformatMMDDYYYY.format(dates[dates.length - 1]));

        // State the diary resource name and the number of dates.
        filename.append("_").append(DiaryManager.getResource().getName()
                      .replaceAll(" ", "_")).append("_");
        filename.append(dates.length);
        filename.append("_dates");

        formattedDiary =
                exportFormat.equals("html") ? FormatManager.diaryToHTML(dates)
                : exportFormat.equals("csv") ? FormatManager.diaryToCSV(dates)
                : FormatManager.diaryToText(dates);


        filename.append(".").append(exportFormat);
        HTMLFormattedString exportedDiary = new HTMLFormattedString(formattedDiary);
        File savedFile = ClientSideLocalFileManager
                .saveSpecifiedFileReturnFile(destinationTextField.getText(),
                filename.toString(), exportedDiary, "." + exportFormat, this);
        if (savedFile != null) {
            if (exportFormat.equals("html")) {
                int resp = JOptionPane.showConfirmDialog(this,
                        "The file has been saved. Open in browser?", "File Saved", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    BarebonesBrowser.openURL(savedFile.toString(), this);
                }
            } else {
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
     * Takes the selected items in the list, converts them to a Date object and
     * places them in a Date array.
     */
    private void setSelectedDates() {
        Object[] tempDates;
        tempDates = changeableModel.toArray();
        dates = new Date[tempDates.length];
        for (int count = 0; count < tempDates.length; count++) {
            dates[count] = (Date)tempDates[count];
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
        btnCancel = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        lblStap1 = new javax.swing.JLabel();
        lblExportedDates = new javax.swing.JLabel();
        lblStep2 = new javax.swing.JLabel();
        lblAvailableDates = new javax.swing.JLabel();
        lblStep3 = new javax.swing.JLabel();
        lblRange = new javax.swing.JLabel();
        cmbStartDate = new javax.swing.JComboBox<>();
        lblTo = new javax.swing.JLabel();
        cmbEndDate = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstViewEntries = new javax.swing.JList<>();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstEditEntries = new javax.swing.JList<>();
        format_JLabel = new javax.swing.JLabel();
        radTXT = new javax.swing.JRadioButton();
        radCSV = new javax.swing.JRadioButton();
        radHTML = new javax.swing.JRadioButton();
        destinationLabel = new javax.swing.JLabel();
        destinationTextField = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        lblCurrentResource = new javax.swing.JLabel();

        setTitle("Weather Viewer - Export Diary");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCancel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(413, 454, 71, 25));

        btnExport.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        getContentPane().add(btnExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(171, 454, 71, 25));

        lblStap1.setText("<html><b>Step 1: Select the beginning and ending dates for the daily diary entries that you wish to export.</b></html>");
        getContentPane().add(lblStap1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 48, 631, 16));

        lblExportedDates.setText("<html><b>Exported Dates</b></html>");
        getContentPane().add(lblExportedDates, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 164, 102, 16));

        lblStep2.setText("<html><b>Step 2: Select the specific dates that you would like to export by highlighting the dates and using the >> button to move them to the Exported Dates.</b></html>");
        getContentPane().add(lblStep2, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 120, 631, 32));

        lblAvailableDates.setText("<html><b>Available Dates</b></html>");
        getContentPane().add(lblAvailableDates, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 164, 102, 16));

        lblStep3.setText("<html><b>Step 3: Choose the output format and destination.</b></html> ");
        getContentPane().add(lblStep3, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 342, 631, 16));

        lblRange.setText("Time Range:");
        getContentPane().add(lblRange, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 76, 109, 16));

        cmbStartDate.setMaximumRowCount(10);
        cmbStartDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbStartDateActionPerformed(evt);
            }
        });
        getContentPane().add(cmbStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 76, 109, 22));

        lblTo.setText("to");
        getContentPane().add(lblTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(211, 76, 109, 16));

        cmbEndDate.setMaximumRowCount(10);
        cmbEndDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEndDateActionPerformed(evt);
            }
        });
        getContentPane().add(cmbEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 76, 109, 22));

        jScrollPane1.setViewportView(lstViewEntries);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 192, 102, 130));

        btnAdd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAdd.setText(">>");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        getContentPane().add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 202, 51, 25));

        btnRemove.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnRemove.setText("<<");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        getContentPane().add(btnRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 287, 51, 25));

        jScrollPane2.setViewportView(lstEditEntries);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 192, 102, 130));

        format_JLabel.setText("Diaries Format:");
        getContentPane().add(format_JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 374, 89, 16));

        radgrpFormat.add(radTXT);
        radTXT.setText("Text");
        radTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radTXTActionPerformed(evt);
            }
        });
        getContentPane().add(radTXT, new org.netbeans.lib.awtextra.AbsoluteConstraints(109, 370, 53, 25));

        radgrpFormat.add(radCSV);
        radCSV.setText("Spreadsheet (CSV)");
        radCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radCSVActionPerformed(evt);
            }
        });
        getContentPane().add(radCSV, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 370, 139, 25));

        radgrpFormat.add(radHTML);
        radHTML.setText("Web Page (HTML)");
        radHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radHTMLActionPerformed(evt);
            }
        });
        getContentPane().add(radHTML, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 370, 133, 25));

        destinationLabel.setText("Destination:");
        getContentPane().add(destinationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 407, 68, 16));
        getContentPane().add(destinationTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(88, 407, 249, 22));

        btnBrowse.setText("Browse..");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        getContentPane().add(btnBrowse, new org.netbeans.lib.awtextra.AbsoluteConstraints(345, 407, 83, 25));
        getContentPane().add(lblCurrentResource, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 631, 16));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This method is called when the start date is selected from the
     * start date combo box. Diary entries from this date are then displayed.
     * 
     * @param evt The event that a date is selected from the start date combo box.
     */
    private void cmbStartDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStartDateActionPerformed
        if (cmbStartDate.getItemCount() > 0 && cmbEndDate.getItemCount() > 0)   {
            fillDiaryModel();
            checkDateRange();
        }
    }//GEN-LAST:event_cmbStartDateActionPerformed

    /**
     * This method is called when the end date is selected from the
     * end date combo box. Entries between the start and end date are then displayed, unless
     * there are no entries to display.
     * 
     * @param evt The event that a date is selected from the end date combo box.
     */
    private void cmbEndDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEndDateActionPerformed
        if (cmbStartDate.getItemCount() > 0 && cmbEndDate.getItemCount() > 0)   {
            fillDiaryModel();
            checkDateRange();
        }
    }//GEN-LAST:event_cmbEndDateActionPerformed

    /**
     * This method is called if the Cancel button is clicked. The window is 
     * closed.
     * 
     * @param evt The event that the Cancel button is clicked.
     */
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
}//GEN-LAST:event_btnCancelActionPerformed

    /**
     * This method is called when the >> button is clicked in the advanced mode for
     * exporting entries. The entry is removed from the left list and added to the right list.
     * 
     * @param evt The event that the >> button is clicked.
     */
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        swapLists(lstViewEntries.getSelectedIndices(), changeableModel, diaryModel);
}//GEN-LAST:event_btnAddActionPerformed

    /**
     * This method is called when the back button is clicked in the advanced mode for
     * exporting entries. The entry is added to the left list and removed from the right
     * list.
     * @param evt The event that that button is clicked.
     */
    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        swapLists(lstEditEntries.getSelectedIndices(), diaryModel, changeableModel);
    }//GEN-LAST:event_btnRemoveActionPerformed

    /**
     * This method is called if the Export button is clicked.
     * 
     * @param evt The event that the Export button is clicked.
     */
    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        if (!changeableModel.isEmpty()) {
            exportDiary();
        }
        else {
            JOptionPane.showMessageDialog(this, "No entries have been chosen "
                    + "for export.", "Nothing Selected For Export", JOptionPane
                    .INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnExportActionPerformed

    /**
     * This method is called if the HTML format radio button is clicked.
     * The exported file will be of this type.
     * 
     * @param evt The event that the HTML radio button is clicked.
     */
    private void radHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radHTMLActionPerformed
        exportFormat = "html";
    }//GEN-LAST:event_radHTMLActionPerformed

    /**
     * This method is called if the CSV format radio button is clicked.
     * The exported file will be of CSV type.
     * 
     * @param evt The event that the CSV radio button is clicked.
     */
    private void radCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radCSVActionPerformed
        exportFormat = "csv";
    }//GEN-LAST:event_radCSVActionPerformed

    private void radTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radTXTActionPerformed
        exportFormat = "txt";
    }//GEN-LAST:event_radTXTActionPerformed
     /**
      * This method is called when browse button is pressed.
      * This allow user to select different directory for exporting Daily Diary.
      * 
      * @param evt The event that the Browse Button is pressed.
      */
    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        //Let user choose directory.
        String chosenDirectory = WeatherFileChooser
                .openDirectoryChooser(new File(CommonLocalFileManager
                        .getNoteDirectory()), "Save Diary Entries...", null, 
                        this);
        if (chosenDirectory != null) {
            File selectedFile = new File(chosenDirectory);
            destinationTextField.setText(selectedFile.getPath());
        }
      
    }//GEN-LAST:event_btnBrowseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnRemove;
    private javax.swing.JComboBox<Date> cmbEndDate;
    private javax.swing.JComboBox<Date> cmbStartDate;
    private javax.swing.JLabel destinationLabel;
    private javax.swing.JTextField destinationTextField;
    private javax.swing.JLabel format_JLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAvailableDates;
    private javax.swing.JLabel lblCurrentResource;
    private javax.swing.JLabel lblExportedDates;
    private javax.swing.JLabel lblRange;
    private javax.swing.JLabel lblStap1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblTo;
    private javax.swing.JList<Date> lstEditEntries;
    private javax.swing.JList<Date> lstViewEntries;
    private javax.swing.JRadioButton radCSV;
    private javax.swing.JRadioButton radHTML;
    private javax.swing.JRadioButton radTXT;
    private javax.swing.ButtonGroup radgrpFormat;
    // End of variables declaration//GEN-END:variables
}
