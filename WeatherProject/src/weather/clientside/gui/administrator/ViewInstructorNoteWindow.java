package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import weather.GeneralService;
import weather.clientside.gui.client.ExportNotesWindow;
import weather.clientside.manager.DiaryManager;
import weather.clientside.manager.NotesAndDiaryPanelManager;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.User;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.NoteFileInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.PropertyManager;

/**
* This is the form for viewing instructor notes.  Each instance allows the user
* to read all the notes for the current diary resource on the diary manager's
* current day.
 * 
 * @author Chris Vitello (2012)
 * @author Xiang Li -- 2014
 * @version Spring 2012
 */
public class ViewInstructorNoteWindow extends BUDialog {
    private GeneralService service;
    private User user;
    private Vector<InstructorNote> allNotesForCurrentResource;
    private DefaultListModel<String> fileListModel;
    private LinkedList<NoteFileInstance> attachedFiles;
    private InstructorNote currentNote;
    private String dateFormatString = PropertyManager.getGeneralProperty("dateFormatString");
    private SimpleDateFormat df = new SimpleDateFormat(dateFormatString);

    /**
     * Constructor that takes the main application window's general service and
     * an instructor note which will be the first note displayed.
     * Creates a new instance of EditInstuctorNoteWindow.
     * 
     * @param service The GeneralService for the weather application.
     * @param noteManager The main GUI's note/diary panel manager.
     * @param initNote The initial note to display in the window.
     */
    public ViewInstructorNoteWindow(GeneralService service,
            NotesAndDiaryPanelManager noteManager, InstructorNote initNote) {
        super();
        init(service, initNote); 
        super.postInitialize(!noteManager.isExternal());
    }

    /**
     * Initializes fields.
     * @param service The general service.
     * @param initNote The first note to be displayed; most recent note if null.
     */
    private void init(GeneralService service, InstructorNote initNote) {
        user = service.getUser();
        
        //Get all notes for the current resource and day.
        Vector<InstructorNote> allNotes = service.getDBMSSystem().
                getNoteManager().getNotesVisibleToUser(user,
                new Date(DiaryManager.getDate().getTime()));
        allNotesForCurrentResource = DiaryManager
                .restrictToCurrentResource(allNotes);
        
        //Set time zome.
        df.setTimeZone(DiaryManager.getResource().getTimeZone().getTimeZone());

        currentNote = initNote == null ? 
                allNotesForCurrentResource.lastElement() : initNote;

        this.service = service;
        fileListModel = new DefaultListModel<String>();

        initComponents();
        
        //Show location and date.
        String location = DiaryManager.getResource().getName();
        String date = df.format(DiaryManager.getDate());
        locationDateLabel.setText("<html><center>Notes For " + location 
                + "<br/>On " + date + "</center></html>");

        attachedFiles = new LinkedList<>();
        attachLabel.setVisible(false);
        attachLabel.setIcon(IconProperties.getAttachmentIconImage());
        nextBtn.setEnabled(false);
        noteLabel.setVisible(false);
        noteNumberLabel.setVisible(false);    
        
        int width = 600 + this.getInsets().left + this.getInsets().right;
        int height = 629 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        displayNote(currentNote);
    }

    /**
     * This modifies the current note window to adapt
     * it to an already created note. Mainly changes the create/update button
     * enables.
     */
    public void displayNote(InstructorNote note) {        
        noteLabel.setText(Integer.toString(note.getNoteNumber()));
        noteLabel.setVisible(true);
        noteNumberLabel.setVisible(true);
        titleField.setText(note.getNoteTitle());
        noteArea.setText(note.getText());
        setResourceRange(new ResourceRange(note.getStartTime(), note.getEndTime()));
        
        User noteAuthor = service.getDBMSSystem().getUserManager().
                    obtainUser(note.getInstructorNumber());
        instructorLabel.setText(noteAuthor.getLastName() + ", " +
                noteAuthor.getFirstName());
        
        attachedFiles.clear();
        fileListModel.clear();
        for (NoteFileInstance nfi : service.getDBMSSystem().getFileManager().
                getAllFilesForNote(note)) {
            attachedFiles.add(nfi);
            fileListModel.addElement(nfi.getFileName());
        }
        attachLabel.setVisible(fileListModel.size() > 0);

        currentNote = note;
        nextBtn.setEnabled(allNotesForCurrentResource.indexOf(currentNote) < allNotesForCurrentResource.size()-1);
        previousBtn.setEnabled(allNotesForCurrentResource.indexOf(currentNote) > 0);
    }

    

    /**
     * Sets the resource range and updates time fields to display the new range
     * for the current instructor note.
     * 
     * @param newRange The new resource range.
     */
    private void setResourceRange(ResourceRange newRange) {
        startTimeField.setText(df.format(newRange.getStartTime()));
        endTimeField.setText(df.format(newRange.getStopTime()));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        noteForTimeRangeJLabel = new javax.swing.JLabel();
        rangeToJLabel = new javax.swing.JLabel();
        endTimeField = new javax.swing.JTextField();
        startTimeField = new javax.swing.JTextField();
        noteScrollPane = new javax.swing.JScrollPane();
        noteArea = new javax.swing.JTextArea();
        closeButton = new javax.swing.JButton();
        noteTitleJLabel = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        fileListScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        attachLabel = new javax.swing.JLabel();
        previousBtn = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        locationDateLabel = new javax.swing.JLabel();
        noteNumberLabel = new javax.swing.JLabel();
        noteLabel = new javax.swing.JLabel();
        instructorJLabel = new javax.swing.JLabel();
        instructorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Weather Viewer - Instructor Class Notes");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        noteForTimeRangeJLabel.setText("Time Range For Note:");
        getContentPane().add(noteForTimeRangeJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 87, -1, -1));

        rangeToJLabel.setText("to");
        getContentPane().add(rangeToJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 87, -1, -1));

        endTimeField.setEditable(false);
        getContentPane().add(endTimeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 84, 135, -1));

        startTimeField.setEditable(false);
        getContentPane().add(startTimeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 84, 135, -1));

        noteScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("NOTES"));

        noteArea.setEditable(false);
        noteArea.setColumns(20);
        noteArea.setLineWrap(true);
        noteArea.setRows(5);
        noteArea.setWrapStyleWord(true);
        noteScrollPane.setViewportView(noteArea);

        getContentPane().add(noteScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 155, 576, 339));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(268, 592, 63, 25));

        noteTitleJLabel.setText("             Title:");
        getContentPane().add(noteTitleJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(141, 122, -1, -1));

        titleField.setEditable(false);
        titleField.setColumns(100);
        getContentPane().add(titleField, new org.netbeans.lib.awtextra.AbsoluteConstraints(228, 119, 205, -1));

        fileListScrollPane.setBorder(null);

        fileList.setBackground(new java.awt.Color(236, 233, 216));
        fileList.setBorder(javax.swing.BorderFactory.createTitledBorder("FILES"));
        fileList.setModel(fileListModel);
        fileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fileList.setFixedCellWidth(150);
        fileList.setFocusCycleRoot(true);
        fileList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        fileList.setName(""); // NOI18N
        fileList.setVisibleRowCount(-1);
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fileListMousePressed(evt);
            }
        });
        fileListScrollPane.setViewportView(fileList);

        getContentPane().add(fileListScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 543, 490, 37));
        getContentPane().add(attachLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 543, 14, 17));

        previousBtn.setText("Previous Note");
        previousBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousBtnActionPerformed(evt);
            }
        });
        getContentPane().add(previousBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 118, -1, 25));

        nextBtn.setText("Next Note");
        nextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtnActionPerformed(evt);
            }
        });
        getContentPane().add(nextBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(451, 118, -1, 25));

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        getContentPane().add(exportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 506, 100, 25));

        printButton.setText("Print");
        printButton.setEnabled(false);
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        getContentPane().add(printButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(488, 506, 100, 25));

        locationDateLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        locationDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(locationDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 483, 32));

        noteNumberLabel.setText("Note #: ");
        getContentPane().add(noteNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(507, 12, 48, 16));

        noteLabel.setPreferredSize(new java.awt.Dimension(34, 14));
        getContentPane().add(noteLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(562, 12, 25, 16));

        instructorJLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        instructorJLabel.setText("Instructor: ");
        getContentPane().add(instructorJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 56, 76, 16));

        instructorLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        instructorLabel.setPreferredSize(new java.awt.Dimension(34, 14));
        getContentPane().add(instructorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(96, 56, 490, 16));

        getAccessibleContext().setAccessibleName("Weather Viewer - View Instructor Class Notes");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The event the cancel button was clicked. Simply disposes the window.
     * 
     * @param evt The event the cancelButton a was clicked.
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * The event the window is closing. The user clicked the 'X'.
     * Asks the user if they really wanted to close the form.
     * 
     * @param evt The WindowEvent to see if windowClose 'X' was clicked.
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        dispose();
    }//GEN-LAST:event_formWindowClosing

    /**
     * Handles event for the previous button.
     * @param evt The action event generated.
     */
    private void previousBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousBtnActionPerformed
        displayNote(allNotesForCurrentResource.get(allNotesForCurrentResource.indexOf(currentNote)-1));
    }//GEN-LAST:event_previousBtnActionPerformed

    /**
     * Handles event for the next button.
     * @param evt The action event generated.
     */
    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtnActionPerformed
        displayNote(allNotesForCurrentResource.get(allNotesForCurrentResource.indexOf(currentNote)+1));
    }//GEN-LAST:event_nextBtnActionPerformed

    private void fileListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileListMousePressed
        if (attachedFiles.size() == 0) {
            return;
        }

        int index = fileList.locationToIndex(evt.getPoint());
        NoteFileInstance nfi = attachedFiles.get(index);
        
        //Get location from user.
        String chosenDirectory = WeatherFileChooser
                .openDirectoryChooser(new File(CommonLocalFileManager
                        .getDataDirectory()), "Save File...", null, this);
        if (chosenDirectory == null) {
            fileList.clearSelection();
            return;
        }
        File dest = new File(chosenDirectory + File.separator
                + nfi.getFileName());
        if (dest.exists()) {
            JOptionPane.showMessageDialog(this,
                    "The file is already saved at this location.");
            fileList.clearSelection();
            return;
        }

        try {
            FileOutputStream fout = new FileOutputStream(dest);
            fout.write(nfi.getFileData());
            fout.close();
        } catch (Exception e) {
            fileList.clearSelection();
            JOptionPane.showMessageDialog(this, "An error occurred while trying to save the file.");
            //Test for remaining space in application home, which has no effect
            //if the save was not there.
            StorageSpaceTester.testApplicationHome();
            return;
        }
        fileList.clearSelection();
        JOptionPane.showMessageDialog(this, "File saved.");

        //Test for remaining space in application home, which has no effect
        //if the save was not there.
        StorageSpaceTester.testApplicationHome();
    }//GEN-LAST:event_fileListMousePressed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        new ExportNotesWindow(currentNote);
    }//GEN-LAST:event_exportButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attachLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField endTimeField;
    private javax.swing.JButton exportButton;
    private javax.swing.JList fileList;
    private javax.swing.JScrollPane fileListScrollPane;
    private javax.swing.JLabel instructorJLabel;
    private javax.swing.JLabel instructorLabel;
    private javax.swing.JLabel locationDateLabel;
    private javax.swing.JButton nextBtn;
    private javax.swing.JTextArea noteArea;
    private javax.swing.JLabel noteForTimeRangeJLabel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JLabel noteNumberLabel;
    private javax.swing.JScrollPane noteScrollPane;
    private javax.swing.JLabel noteTitleJLabel;
    private javax.swing.JButton previousBtn;
    private javax.swing.JButton printButton;
    private javax.swing.JLabel rangeToJLabel;
    private javax.swing.JTextField startTimeField;
    private javax.swing.JTextField titleField;
    // End of variables declaration//GEN-END:variables
}
