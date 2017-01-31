package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import weather.GeneralService;
import weather.clientside.gui.client.ExportNotesWindow;
import weather.clientside.manager.DiaryManager;
import weather.clientside.manager.NotesAndDiaryPanelManager;
import weather.clientside.utilities.RightClickMenu;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.*;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.NoteFileInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSFileManager;
import weather.common.dbms.DBMSNoteManager;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.DateRangeSelectionWindow;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;

/**
 * This is the form for editing instructor notes.  The user is able to edit all
 * notes passed to the constructors.
 *
 * @author Andrew Bennett (2010)
 * @author Joe Van Lente (2010)
 * @author Chris Vitello (2012)
 * @version Spring 2012
 */
public class EditInstructorNoteWindow extends BUDialog {

    private GeneralService service;
    private User user;
    private Vector<InstructorNote> allNotes;
    private long startOfFirstDayInMillis;
    private long startOfLastDayInMillis;
    
    //Attached files section
    //List of names of currently attached files
    private DefaultListModel<String> fileListModel;
    //List to hold files attached when note was loaded.
    private LinkedList<NoteFileInstance> originalFiles;
    //List to hold currently attached files.
    private LinkedList<NoteFileInstance> attachedFiles;
    
    private boolean modified;
    private File activeDir;
    private InstructorNote currentNote;
    private ResourceRange currentRange;
    private boolean holdChangesMade = false;
    private NotesAndDiaryPanelManager noteManager;
    private String dateFormatString = PropertyManager.
            getGeneralProperty("dateFormatString");
    private SimpleDateFormat df =
            new SimpleDateFormat(dateFormatString);
    private TimeZone timeZone;
    /**
     * Constructor that first displays the most recent available note created by
     * the current user.
     *
     * @param service The GeneralService for the weather application.
     * @param noteManager The main GUI's note/diary panel manager.
     * @param allNotes All notes the user can edit.
     */
    public EditInstructorNoteWindow(GeneralService service, NotesAndDiaryPanelManager noteManager,
            Vector<InstructorNote> allNotes) {
        super();
        this.noteManager = noteManager;
        init(service, null, allNotes);
        super.postInitialize(!noteManager.isExternal());
    }

    /**
     * Constructor that first displays a given note.
     * 
     * @param service The GeneralService for the weather application.
     * @param noteManager The main GUI's note/diary panel manager.
     * @param initNote The initial note to load into the window.
     * @param allNotes All notes the user can edit.
     */
    public EditInstructorNoteWindow(GeneralService service, NotesAndDiaryPanelManager noteManager,
            InstructorNote initNote, Vector<InstructorNote> allNotes) {
        super();
        this.noteManager = noteManager;
        init(service, initNote, allNotes);
        super.postInitialize(!noteManager.isExternal());
    }

    /**
     * Initializes fields.
     *
     * @param service The general service.
     * @param initNote The first note to be displayed; most recent note if null.
     * @param allNotes All notes the user can edit.
     */
    private void init(GeneralService service, InstructorNote initNote,
            Vector<InstructorNote> allNotes) {
        user = service.getUser();
        this.allNotes = allNotes;

        currentNote =
                initNote == null ? allNotes.firstElement() : initNote;
        currentRange = new ResourceRange(currentNote.getStartTime(), currentNote.getEndTime());

        this.service = service;
        fileListModel = new DefaultListModel<>();

        initComponents();

        this.timeZone = DiaryManager.getResource().getTimeZone().getTimeZone();
        df.setTimeZone(timeZone);
        
        attachLabel.setIcon(IconProperties.getAttachmentIconImage());
        nextBtn.setEnabled(false);
        originalFiles = new LinkedList<>();
        attachedFiles = new LinkedList<>();
        activeDir = new File(CommonLocalFileManager.getDataDirectory());

        noteLabel.setVisible(false);
        noteNumberLabel.setVisible(false);
        displayNote(currentNote);
        addCommonListeners();
        
        int width = 600 + this.getInsets().left + this.getInsets().right;
        int height = 578 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
    }

    /**
     * This modifies the current note window to adapt it to an already created
     * note. Mainly changes the create/update button enables.
     */
    public void displayNote(InstructorNote note) {
        holdChangesMade = true;
        noteLabel.setText(Integer.toString(note.getNoteNumber()));
        noteLabel.setVisible(true);
        noteNumberLabel.setVisible(true);
        this.locationLabel.setText("Location: " + service.getDBMSSystem()
                .getResourceManager().getWeatherResourceByNumber(note
                .getCameraNumber()).getResourceName());
        User noteAuthor = service.getDBMSSystem().getUserManager().
                obtainUser(currentNote.getInstructorNumber());
        instructorLabel.setText((noteAuthor == null) ? "Author was deleted"
                : noteAuthor.getLastName() + ", " + noteAuthor.getFirstName());
        titleField.setText(note.getNoteTitle());
        noteArea.setText(note.getText());
        setResourceRange(new ResourceRange(note.getStartTime(), note.getEndTime()));

        if (note.getAccessRights().equals(AccessRights.Instructors)) {
            radInstructorsOnly.setSelected(true);
        } else if (note.getAccessRights().equals(AccessRights.AllStudents)) {
            radAllStudents.setSelected(true);
        } else if (note.getAccessRights().equals(AccessRights.CourseStudents)) {
            radCourseStudents.setSelected(true);
        } else if (note.getAccessRights().equals(AccessRights.Everyone)) {
            radAllUsers.setSelected(true);
        } else {
            radPersonal.setSelected(true);
        }
        
        attachedFiles.clear();
        originalFiles.clear();
        fileListModel.clear();
        for (NoteFileInstance nfi : service.getDBMSSystem().getFileManager().
                getAllFilesForNote(note)) {
            originalFiles.add(nfi);
            attachedFiles.add(nfi);
            fileListModel.addElement(nfi.getFileName());
        }
        attachLabel.setVisible(fileListModel.size() > 0);

        currentNote = note;
        nextBtn.setEnabled(allNotes.indexOf(currentNote) < allNotes.size() - 1);
        previousBtn.setEnabled(allNotes.indexOf(currentNote) > 0);
        modified = false;
        holdChangesMade = false;
    }
    
    /**
     * Checks if a linked list of <code>NoteFileInstance</code> contains an instance with
     * a given file number.
     * @param list The list to be checked.
     * @param noteFileNumber The file number to be found.
     * @return True if the indicated file is found, false otherwise.
     */
    private boolean collectionHasFileNumber(LinkedList<NoteFileInstance> list, int noteFileNumber){
        for(NoteFileInstance nfi : list){
            if(nfi.getFileNumber() == noteFileNumber){
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the current note to the information currently in the input
     * fields.
     */
    private void updateCurrentNote() {
        currentNote.setNoteTitle(titleField.getText().trim());
        currentNote.setStartTime(new Date(startOfFirstDayInMillis));
        currentNote.setEndTime(new Date(startOfLastDayInMillis));

        if (radAllUsers.isSelected()) {
            currentNote.setAccessRights(AccessRights.Everyone);
        } else if (radAllStudents.isSelected()) {
            currentNote.setAccessRights(AccessRights.AllStudents);
        } else if (radCourseStudents.isSelected()) {
            currentNote.setAccessRights(AccessRights.CourseStudents);
        } else if (radInstructorsOnly.isSelected()) {
            currentNote.setAccessRights(AccessRights.Instructors);
        } else {
            //Store personal notes as "Private."
            currentNote.setAccessRights(AccessRights.Private);
        }

        currentNote.setText(noteArea.getText());

        DBMSNoteManager noteMgr = service.getDBMSSystem().getNoteManager();
        noteMgr.updateNote(currentNote);

        //Variables for below file maintenance.
        DBMSFileManager fileMgr = service.getDBMSSystem().getFileManager();
        
        //Find and remove notes in original list not now pressent.
        for (NoteFileInstance nfi : originalFiles) {
            if (!collectionHasFileNumber(attachedFiles, nfi.getFileNumber())) {
                fileMgr.deleteNotesFile(nfi);
            }
        }
        
        //Find and add any pressent notes not in database
        for (NoteFileInstance nfi : attachedFiles) {
            if (!collectionHasFileNumber(originalFiles, nfi.getFileNumber())) {
                fileMgr.insertNotesFile(nfi);
            }
        }
        
        //File lists mudt be updated to reflect state of file in database.
        attachedFiles.clear();
        originalFiles.clear();
        fileListModel.clear();
        for (NoteFileInstance nfi : service.getDBMSSystem().getFileManager().
                getAllFilesForNote(currentNote)) {
            originalFiles.add(nfi);
            attachedFiles.add(nfi);
            fileListModel.addElement(nfi.getFileName());
        }
        attachLabel.setVisible(fileListModel.size() > 0);

        modified = false;
        updateButton.setEnabled(false);
        noteManager.setClassNotes();

        JOptionPane.showMessageDialog(this, "This note has been updated.");
    }

    /**
     * Sets the resource range and updates time fields to display the new range
     * for the current instructor note.
     *
     * @param newRange The new resource range.
     */
    private void setResourceRange(ResourceRange newRange) {
        startTimeField.setText(df.format(newRange.getStartTime()));
        startOfFirstDayInMillis = newRange.getStartTime().getTime();
        endTimeField.setText(df.format(newRange.getStopTime()));
        startOfLastDayInMillis = newRange.getStopTime().getTime();
        currentRange = newRange;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        accessRightsGroup = new javax.swing.ButtonGroup();
        popupMenu = new javax.swing.JPopupMenu();
        removeItem = new javax.swing.JMenuItem();
        noteForTimeRangeJLabel = new javax.swing.JLabel();
        rangeToJLabel = new javax.swing.JLabel();
        endTimeField = new javax.swing.JTextField();
        changeRangeButton = new javax.swing.JButton();
        accessRightsPanel = new javax.swing.JPanel();
        radAllUsers = new javax.swing.JRadioButton();
        radAllStudents = new javax.swing.JRadioButton();
        radCourseStudents = new javax.swing.JRadioButton();
        radInstructorsOnly = new javax.swing.JRadioButton();
        radPersonal = new javax.swing.JRadioButton();
        startTimeField = new javax.swing.JTextField();
        noteScrollPane = new javax.swing.JScrollPane();
        noteArea = new javax.swing.JTextArea();
        attachButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        noteNumberLabel = new javax.swing.JLabel();
        noteTitleJLabel = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        noteLabel = new javax.swing.JLabel();
        fileListScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        attachLabel = new javax.swing.JLabel();
        previousBtn = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        locationLabel = new javax.swing.JLabel();
        instructorLabel = new javax.swing.JLabel();
        instructorJLabel = new javax.swing.JLabel();

        removeItem.setText("Remove");
        removeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeItemActionPerformed(evt);
            }
        });
        popupMenu.add(removeItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Weather Viewer - Instructor Class Notes");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        noteForTimeRangeJLabel.setText("Time Range For Notes:");
        getContentPane().add(noteForTimeRangeJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 72, 133, 16));

        rangeToJLabel.setText("to");
        getContentPane().add(rangeToJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 72, -1, 16));

        endTimeField.setEditable(false);
        getContentPane().add(endTimeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 69, 135, 22));

        changeRangeButton.setText("Edit Time Range");
        changeRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeRangeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(changeRangeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 68, 127, 25));

        accessRightsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Note User Access Rights"));
        accessRightsPanel.setToolTipText("Who can access this specific class note");

        accessRightsGroup.add(radAllUsers);
        radAllUsers.setText("All Users");
        accessRightsPanel.add(radAllUsers);

        accessRightsGroup.add(radAllStudents);
        radAllStudents.setText("All Students");
        accessRightsPanel.add(radAllStudents);

        accessRightsGroup.add(radCourseStudents);
        radCourseStudents.setText("Course Enrolled Students");
        accessRightsPanel.add(radCourseStudents);

        accessRightsGroup.add(radInstructorsOnly);
        radInstructorsOnly.setText("Instructors Only");
        accessRightsPanel.add(radInstructorsOnly);

        accessRightsGroup.add(radPersonal);
        radPersonal.setText("Personal");
        accessRightsPanel.add(radPersonal);

        getContentPane().add(accessRightsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 105, 576, 64));

        startTimeField.setEditable(false);
        getContentPane().add(startTimeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 69, 135, 22));

        noteScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("NOTES"));

        noteArea.setColumns(20);
        noteArea.setLineWrap(true);
        noteArea.setRows(5);
        noteArea.setWrapStyleWord(true);
        noteScrollPane.setViewportView(noteArea);

        getContentPane().add(noteScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 218, 575, 239));

        attachButton.setText("Attach File");
        attachButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachButtonActionPerformed(evt);
            }
        });
        getContentPane().add(attachButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 461, -1, -1));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(524, 541, -1, -1));

        updateButton.setText("Update Note");
        updateButton.setEnabled(false);
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(232, 541, -1, -1));

        noteNumberLabel.setText("Note #: ");
        getContentPane().add(noteNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(507, 12, 48, 16));

        noteTitleJLabel.setText("Title:");
        noteTitleJLabel.setToolTipText("");
        getContentPane().add(noteTitleJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(156, 185, 63, -1));

        titleField.setColumns(100);
        getContentPane().add(titleField, new org.netbeans.lib.awtextra.AbsoluteConstraints(224, 182, 205, 22));

        noteLabel.setPreferredSize(new java.awt.Dimension(34, 14));
        getContentPane().add(noteLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(562, 12, 25, 16));

        fileListScrollPane.setBorder(null);

        fileList.setBackground(new java.awt.Color(236, 233, 216));
        fileList.setBorder(javax.swing.BorderFactory.createTitledBorder("FILES"));
        fileList.setModel(fileListModel);
        fileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fileList.setFixedCellWidth(150);
        fileList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        fileList.setVisibleRowCount(-1);
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileListMouseReleased(evt);
            }
        });
        fileListScrollPane.setViewportView(fileList);

        getContentPane().add(fileListScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 499, 490, 35));
        getContentPane().add(attachLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 499, 14, 17));

        previousBtn.setText("Previous Note");
        previousBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousBtnActionPerformed(evt);
            }
        });
        getContentPane().add(previousBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 181, -1, 25));

        nextBtn.setText("Next Note");
        nextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtnActionPerformed(evt);
            }
        });
        getContentPane().add(nextBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(453, 181, -1, 25));

        deleteButton.setText("Delete Note");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        getContentPane().add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 541, -1, -1));

        printButton.setText("Print");
        printButton.setEnabled(false);
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        getContentPane().add(printButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(496, 461, 91, -1));

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        getContentPane().add(exportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(238, 461, 91, -1));

        locationLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        getContentPane().add(locationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 483, 16));

        instructorLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        instructorLabel.setPreferredSize(new java.awt.Dimension(34, 14));
        getContentPane().add(instructorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(96, 40, 490, 16));

        instructorJLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        instructorJLabel.setText("Instructor: ");
        getContentPane().add(instructorJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, 76, 16));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The event the change range button was clicked. Opens a new
     * DateSelectionWindow to get the new desired range for the note and then
     * updates the date range information fields.
     *
     * @param evt The event the changeButton was clicked.
     */
    private void changeRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeRangeButtonActionPerformed
        ResourceRange newRange = DateRangeSelectionWindow
                .getNewResourceRange(currentRange, timeZone, false, true,
                !noteManager.isExternal());

        if (newRange != null) {
            setResourceRange(newRange);
        }
    }//GEN-LAST:event_changeRangeButtonActionPerformed

    /**
     * The event the cancel button was clicked. Simply disposes the window.
     *
     * @param evt The event the cancelButton a was clicked.
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        exit();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * The event the window is closing. The user clicked the 'X'. Asks the user
     * if they really wanted to close the form.
     *
     * @param evt The WindowEvent to see if windowClose 'X' was clicked.
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exit();
    }//GEN-LAST:event_formWindowClosing

    /**
     * Checks for modifications and closes screen if user decides to close or if
     * no modifications were made.
     */
    private void exit() {
        if (modified) {
            int ans = JOptionPane.showConfirmDialog(this, "The update will not be "
                    + "saved. Continue?", "Please Confirm", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.NO_OPTION) {
                return;
            }
        }
        noteManager.updateClassNoteNoteDates();
        this.dispose();
    }
    
    /**
     * Handles action event for the attach button.
     *
     * @param evt The ActionEvent generated by the attach button.
     */
    private void attachButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachButtonActionPerformed
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                new File(CommonLocalFileManager.getRootDirectory()),
                "Attach File", this);
        if (file == null) {
            return;
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            int size = 0;
            while (fis.read() != -1) {
                size++;
            }
            fis.close();

            byte[] fileData = new byte[size];
            fis = new FileInputStream(file);
            fis.read(fileData);
            fis.close();

            NoteFileInstance nfi = new NoteFileInstance(
                    -1, -1, service.getUser().getUserNumber(), file.getName(), fileData);
            attachedFiles.add(nfi);
            fileListModel.addElement(nfi.getFileName());
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to load file.");
            WeatherLogger.log(Level.SEVERE, "Unable to load file.");
            return;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An I/O error has occurred.");
            WeatherLogger.log(Level.SEVERE, "I/O error :" + e.getMessage());
            return;
        }
        
        //The attachment was successful.
        attachLabel.setVisible(true);
        changesMade();
    }//GEN-LAST:event_attachButtonActionPerformed

    /**
     * Handles event for the previous button.
     *
     * @param evt The action event generated.
     */
    private void previousBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousBtnActionPerformed
        check();
        displayNote(allNotes.get(allNotes.indexOf(currentNote) - 1));
    }//GEN-LAST:event_previousBtnActionPerformed

    /**
     * Handles event for the next button.
     *
     * @param evt The action event generated.
     */
    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtnActionPerformed
        check();
        displayNote(allNotes.get(allNotes.indexOf(currentNote) + 1));
    }//GEN-LAST:event_nextBtnActionPerformed

    /**
     * Handles event for the updateFieldContent button.
     *
     * @param evt The action event generated.
     */
    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        updateCurrentNote();
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int resp = JOptionPane.showConfirmDialog(this, "Permanently delete this"
                + " note?", "Please Confirm", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.NO_OPTION) {
            return;
        }

        service.getDBMSSystem().getNoteManager().removeNote(new Instructor(user), currentNote);
        int index = allNotes.indexOf(currentNote);
        allNotes.remove(currentNote);
        if (allNotes.isEmpty()) {
            noteManager.setClassNotes();
            dispose();
            return;
        }
        displayNote(allNotes.get(index == allNotes.size() ? index - 1 : index));
        noteManager.setClassNotes();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void fileListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileListMouseReleased
        //Handle removals.
        if (evt.isPopupTrigger()) {
            popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());            
            fileList.setSelectedIndex(fileList.locationToIndex(evt.getPoint()));
            return;
        }
        
        //If code get here, user is saving.
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
    }//GEN-LAST:event_fileListMouseReleased

    private void removeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeItemActionPerformed
        if (popupMenu.getInvoker() == fileList) {
            removeSelectedFile();
        }
    }//GEN-LAST:event_removeItemActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        new ExportNotesWindow(currentNote);
    }//GEN-LAST:event_exportButtonActionPerformed

    /**
     * Removes the selected file from the file list which shows attached files.
     */
    private void removeSelectedFile() {
        int index = fileList.getSelectedIndex();
        try {
            attachedFiles.remove(index);
            fileListModel.remove(index);
        } catch (Exception e) {
            return;  // This just happens when the file list itself is selected, but not any particular file. No action needed.
        }
        attachLabel.setVisible(fileListModel.size() > 0);
        changesMade();
    }

    /**
     * Checks if any changes have been made; if so, prompts user to updateFieldContent the
     * current note.
     */
    private void check() {
        if (modified) {
            int ans = JOptionPane.showConfirmDialog(this, "Save changes?",
                    "Please Confirm", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) {
                updateCurrentNote();
            }
        }
    }

    /**
     * If hold is not on, sets modified property to true and enables updateFieldContent
     * button.
     */
    private void changesMade() {
        if (holdChangesMade) {
            return;
        }
        modified = true;
        updateButton.setEnabled(true);
    }

    /**
     * Adds listeners that are common to several components.
     */
    private void addCommonListeners() {
        JTextComponent[] fields = new JTextComponent[]{
            startTimeField, endTimeField, titleField, noteArea
        };

        RightClickMenu.addMenuTo(fields);

        PopupMenuListener popupMenuListener = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                fileList.clearSelection();
            }
        };

        popupMenu.addPopupMenuListener(popupMenuListener);

        DocumentListener docLsr = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changesMade();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changesMade();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };

        for (JTextComponent f : fields) {
            f.getDocument().addDocumentListener(docLsr);
        }

        ActionListener radLsr = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changesMade();
            }
        };

        for (JRadioButton r : new JRadioButton[]{radAllStudents, radAllUsers, 
            radCourseStudents, radInstructorsOnly, radPersonal}) {
            r.addActionListener(radLsr);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup accessRightsGroup;
    private javax.swing.JPanel accessRightsPanel;
    private javax.swing.JButton attachButton;
    private javax.swing.JLabel attachLabel;
    private javax.swing.JButton changeRangeButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField endTimeField;
    private javax.swing.JButton exportButton;
    private javax.swing.JList fileList;
    private javax.swing.JScrollPane fileListScrollPane;
    private javax.swing.JLabel instructorJLabel;
    private javax.swing.JLabel instructorLabel;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JButton nextBtn;
    private javax.swing.JTextArea noteArea;
    private javax.swing.JLabel noteForTimeRangeJLabel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JLabel noteNumberLabel;
    private javax.swing.JScrollPane noteScrollPane;
    private javax.swing.JLabel noteTitleJLabel;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JButton previousBtn;
    private javax.swing.JButton printButton;
    private javax.swing.JRadioButton radAllStudents;
    private javax.swing.JRadioButton radAllUsers;
    private javax.swing.JRadioButton radCourseStudents;
    private javax.swing.JRadioButton radInstructorsOnly;
    private javax.swing.JRadioButton radPersonal;
    private javax.swing.JLabel rangeToJLabel;
    private javax.swing.JMenuItem removeItem;
    private javax.swing.JTextField startTimeField;
    private javax.swing.JTextField titleField;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
