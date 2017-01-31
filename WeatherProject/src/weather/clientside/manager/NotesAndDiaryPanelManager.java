package weather.clientside.manager;

import java.awt.Component;
import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import weather.ApplicationControlSystem;
import weather.GeneralService;
import weather.clientside.gui.administrator.CreateInstructorNoteWindow;
import weather.clientside.gui.administrator.EditInstructorNoteWindow;
import weather.clientside.gui.administrator.ViewInstructorNoteWindow;
import weather.clientside.gui.client.*;
import weather.clientside.gui.component.DateListCellRenderer;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.gui.component.ResourceListCellItem;
import weather.clientside.utilities.*;
import weather.common.data.*;
import weather.common.data.diary.*;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.Note;
import weather.common.data.note.NoteFileInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSDailyDiaryWebLinkManager;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.*;

/**
 * A panel manager that contains a JTabbedPane where each panel represents a
 * different note or diary related GUI. There are get methods for each panel as
 * well as the entire tabbed pane to be used elsewhere in the program. This
 * class also contains common note and diary operations. All dates are taken
 * from the DiaryManager and are set to the start of the date in the time zone
 * of the current diary resource.
 *
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 * @author Chris Vitello (2012)
 * @version Spring 2012
 */
public class NotesAndDiaryPanelManager extends javax.swing.JPanel {

    private NoteAndDiaryWindowListener winListener;
    private NoteAndDiaryChangeListener changeListener;
    private NoteAndDiaryCloseListener closeListener;
    private DailyEntry currentEntry;
    private boolean personalNotesSaved = true, diarySaved = true;
    private boolean externalWindowOpened = false;
    private String entryFormat;
    private ExternalNotesForm externalFrame;
    private ArrayList<WindDirectionType> surfaceWindDirection;
    private MainApplicationWindow mainWin;
    private GeneralService service;
    private ApplicationControlSystem appControl;

    //Flage to stop code from firing due to programatic change of controls.
    private boolean holdCheckForUnsavedDiary = false;
    private boolean holdCmbDailyEntryListener = false;
    private boolean holdLoadOfNewResource = false;
    private boolean holdChangeMainWindowTimeSpan = false;

    private String dateFormatString = PropertyManager.getGeneralProperty("dateFormatString");
    private ListCellDateRenderer renderer = new ListCellDateRenderer(dateFormatString);
    private SimpleDateFormat dateFormatMMDDYYYY = new SimpleDateFormat(dateFormatString);
    private Vector<DailyDiaryWebLinks> webLinks;
    private User user;
    private String userType;
    private MovieController mc;

    /**
     * Variables to indicate class notes by turning tab green.
     */
    //the tabbed pane (set in constructor)
    private JTabbedPane currentPane = null; 
    //should tab be green?
    private boolean hasClassNotes;      

    /**
     * Variables to indicate what diary data is saved in the database for the
     * date for which this panel is showing data.
     */ 
    private boolean isDiaryEntrySaved;
    private boolean arePrivateClassNotesSaved;

    //Column numbers for class notes table.
    final int privilegedUsernoteTableNumberOfColumns = 5;
    final int noteNumberColumnNumber = 0;
    final int noteAuthorColumnNumber = 1;
    final int noteTitleColumnNumber = 2;
    final int noteTextColumnNumber = 3;
    final int noteAttachedFilesColumnNumber = 4;
    final int studentNoteTableTextColumnNumber = 2;
    final int studentNoteTableFileColumnNumber = 3;
    final int studentNoteTableNumberOfColumns = 4;

    /**
     * This is a <code>TableCellRenderer</code> for use with the student table
     * so the student (or guest) can see the entire note text. See the custom
     * creation code for the note table and the code for
     * <code>MultiLineCellRenderer</code>
     */
    private final MultiLineCellRenderer wordWrapRenderer
            = new MultiLineCellRenderer();

    /**
     * Initializes all the panels and components used for notes and diary
     * entries.
     *
     * @param mainWin The program's main window.
     * @param appControl The program's application control thread.
     * @param mc The movie controller from the program's main window.
     */
    public NotesAndDiaryPanelManager(MainApplicationWindow mainWin,
            ApplicationControlSystem appControl, MovieController mc) {
        this.user = appControl.getGeneralService().getUser();
        this.userType = user.getUserType().toString();
        initComponents();
        this.mc = mc;
        this.service = appControl.getGeneralService();
        this.appControl = appControl;
        this.mainWin = mainWin;
        winListener = new NoteAndDiaryWindowListener();
        changeListener = new NoteAndDiaryChangeListener();
        closeListener = new NoteAndDiaryCloseListener();
        surfaceWindDirection = new ArrayList<>();

        //Set note fields and note save buttons for privileged users.
        if (isPrivilegedUser()) {
            txtDiaryNotes.setEditable(false);
            txtDiaryNotes.setBackground(Color.GRAY);
            txtLocalNotes.setEditable(false);
            txtLocalNotes.setBackground(Color.GRAY);
            btnSaveLocalNotes.setVisible(false);
            saveLocalNotesSideButton.setVisible(false);
        }

        DBMSDailyDiaryWebLinkManager webLinkManager = appControl.getAdministratorControlSystem()
                .getGeneralService().getDBMSSystem().getDailyDiaryWebLinkManager();
        webLinks = webLinkManager.getLinks();
        cmbDateList.setRenderer(renderer);
        noteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteTable.setRowSelectionAllowed(true);
        noteTable.setAutoCreateRowSorter(true);
        noteTable.addMouseListener(doubleClick);

        //Set up camera list.
        GUIComponentFactory.initCameraComboBox(cameraComboBox,
                appControl.getGeneralService(), false);

        loadPanel(ExternalNotesForm.PERSONAL_NOTES); //personal notes show on load
        setPersonalNotes();
        setDiaryFieldsAndFlags();
        setClassNotes();
        addCommonListeners();
    }

    /**
     * Helper function to open a browser and show the given URL. NOTE: This
     * should only be used to open a link on the daily diary tab.
     *
     * @param URL The URL to be opened.
     */
    private void openDiaryLink(String URL){
        BarebonesBrowser.openURL(URL, externalFrame);
    }

    /**
     * Sets the main window's camera and station resources to those shown by the
     * external version of this manager. (The station resource is the one
     * related to the camera resource.)
     */
    private void updateMainWindowToDiaryResources() {
        //Get camera resources.
        Vector<Resource> newResources = new Vector<>();
        Resource resource;
        resource = DiaryManager.getResource();
        appControl.getGeneralService()
                .setCurrentWeatherCameraResource(resource);
        newResources.add(resource);

        mc.setFutureVideoResources(newResources);

        //Needed because setting the video resources doesn't change the vides.
        //That call sets up the video panel so a call like this will change 
        //them.
        mc.updateRange(mc.getResourceRange());

        //Get station resource.
        resource = DiaryManager.getRelatedResource();

        if (resource.getResourceNumber() != -1) {
            appControl.getGeneralService()
                    .setCurrentWeatherStationResource(resource);
            mc.setWeatherStationResource(resource);
        } else {
            appControl.getGeneralService()
                    .setCurrentWeatherStationResource(null);
            mc.setWeatherStationResource(null);
        }
    }

    /**
     * Sets the main window's date resources to those shown by the external
     * version of this manager. (All of the day is loaded.)
     */
    private void updateMainWindowToDiaryDate() {
        //Must compute midnight is the time zone local to the mian window camera
        //by first getting diary midmight.
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(DiaryManager.getResource().getTimeZone().getTimeZone());
        long diaryTimeInMill1s = DiaryManager.getDate().getTime();
        calendar.setTimeInMillis(diaryTimeInMill1s);

        //Must save calendar date for reset after time zone is set.
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        //Set time zone to that of main window camera.
        calendar.setTimeZone(mc.getPrimaryTimeZone());

        //Restore date.
        calendar.set(year, month, day, 0, 0);

        //Extract start time in millis
        long mainWindowStartTimeInMillis = calendar.getTimeInMillis();

        //Find end od disply range.
        long lastAvailableTime = ResourceTimeManager.getDefaultRange().getStopTime().getTime();
        long endTime = mainWindowStartTimeInMillis + ResourceTimeManager.MILLISECONDS_PER_DAY;
        if (endTime > lastAvailableTime) {
            endTime = lastAvailableTime;
        }

        //Update range.
        mc.updateRange(new ResourceRange(new java.sql.Date(mainWindowStartTimeInMillis),
                new java.sql.Date(endTime)));
    }

    /**
     * The function returns a list of all date with class notes visible to the
     * current user for the current diary resource.
     *
     * @return A sorted list of all date with class notes visible to the current
     * user for the current diary resource.
     */
    private ArrayList<Date> getAllDatesWithVisibleClassNotes() {
        Vector<InstructorNote> allNotes;

        //Get notes from data base.
        UserType uType = user.getUserType();
        if (uType == UserType.instructor) {
            allNotes = service.getDBMSSystem().getNoteManager()
                    .getAllNotesVisibleToInstructor(user);
        } else if (uType == UserType.administrator) {
            allNotes = service.getDBMSSystem().getNoteManager()
                    .getAllNotes();
        } else if (uType == UserType.guest) {
            allNotes = service.getDBMSSystem().getNoteManager()
                    .getAllNotesVisibleToGuest();
        } else {
            allNotes = service.getDBMSSystem().getNoteManager()
                    .getAllNotesVisibleToStudent(user);
        }

        //Restrict notes to those for the currnt diary resource.
        Vector<InstructorNote> resourceNotes = DiaryManager
                .restrictToCurrentResource(allNotes);

        //Make list to return.
        ArrayList<Date> returnList = new ArrayList<>();
        for (InstructorNote note : resourceNotes) {
            //Set date to be tested to beginning of range to test.
            Calendar currentDate = new GregorianCalendar();
            currentDate.setTime(note.getStartTime());
            //Store last date to test.
            Calendar lastDate = new GregorianCalendar();
            lastDate.setTime(note.getEndTime());

            //Add date not already in list.
            while (currentDate.getTimeInMillis() <= lastDate.getTimeInMillis()) {
                if (!returnList.contains(currentDate.getTime())) {
                    returnList.add(currentDate.getTime());
                }
                currentDate.add(Calendar.DATE, 1);
            }
        }

        //Sort and return list.
        Collections.sort(returnList);
        return returnList;
    }

    /**
     * The function returns a list of all date with class notes that are written
     * by and private to the current user for the current diary resource.
     *
     * @return A sorted list of all date with class notes that are written by
     * and private to the current user for the current diary resource.
     */
    private ArrayList<Date> getAllDatesWithPrivateClassNotes() {
        Vector<InstructorNote> allNotes;
        Vector<InstructorNote> allPrivateNotes;

        //Get notes from data base.
        allNotes = service.getDBMSSystem().getNoteManager()
                .getNotesByInstructor(user);

        //Restrict to private notes.
        allPrivateNotes = new Vector<>();
        for (InstructorNote note : allNotes) {
            if (note.getAccessRights() == AccessRights.Private) {
                allPrivateNotes.add(note);
            }
        }

        //Restrict notes to those for the currnt diary resource.
        Vector<InstructorNote> resourceNotes = DiaryManager
                .restrictToCurrentResource(allPrivateNotes);

        //Make list to return.
        ArrayList<Date> returnList = new ArrayList<>();
        for (InstructorNote note : resourceNotes) {
            //Set date to be tested to beginning of range to test.
            Calendar currentDate = new GregorianCalendar();
            currentDate.setTime(note.getStartTime());
            //Store last date to test.
            Calendar lastDate = new GregorianCalendar();
            lastDate.setTime(note.getEndTime());

            //Add date not already in list.
            while (currentDate.getTimeInMillis() <= lastDate.getTimeInMillis()) {
                if (!returnList.contains(currentDate.getTime())) {
                    returnList.add(currentDate.getTime());
                }
                currentDate.add(Calendar.DATE, 1);
            }
        }

        //Sort and return list.
        Collections.sort(returnList);
        return returnList;
    }

    /**
     * This function returns a list of all daily diary entries created by the
     * user for the current diary resource. Dates with no personal notes can be
     * excluded with a flag.
     *
     * @param includeEmptyNotes Whether or not entries with no personal notes
     * should be included.
     * @return A list of diary entry dates.
     */
    private ArrayList<Date> getAllDiaryEntryDates(boolean includeEmptyNotes) {
        int index = DiaryManager.getDiarySize() - 1;

        //Start with any class notes functioning as personal notes.
        ArrayList<Date> returnList = getAllDatesWithPrivateClassNotes();

        while (index >= 0) {
            //Exclude other resources.
            int entryResourceNumber = DiaryManager.getEntry(index)
                    .getCameraNumber();
            int diaryResourceNumber = DiaryManager.getResource()
                    .getResourceNumber();
            if (entryResourceNumber != diaryResourceNumber) {
                index--;
                continue;
            }

            //Exclude any date already in return list.
            Date entryDate = DiaryManager.getEntry(index).getDate();
            if (returnList.contains(entryDate)) {
                index--;
                continue;
            }

            //Date can now be added.
            if (includeEmptyNotes || !DiaryManager.getEntry(index)
                    .getNote(appControl.getGeneralService()).getText()
                    .isEmpty()) {
                returnList.add(0, entryDate);
            }
            index--;
        }

        //Sort and return list.
        Collections.sort(returnList);
        return returnList;
    }
    
    /**
     * Helper function to determine the parent window for this object.
     * 
     * @return The parent window for this object.
     */
    private Component getWholeWindow() {
        if (!externalWindowOpened) {
            return mainWin;
        } else {
            return externalFrame;
        }
    }
    
    /**
     * Helper function to view, but not edit, a given note.
     * 
     * @param note The given note.
     */
   private void viewInstructorNote(InstructorNote note) {
       new ViewInstructorNoteWindow(service, this, note);
   }

    /**
     * Helper function to show an information message.
     *
     * @param message The message in the box.
     * @param title The title of the box.
     */
    private void showInfoPane(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(getWholeWindow());
        dialog.setVisible(true);
    }

    /**
     * Helper function to show an error message.
     *
     * @param message The message in the box.
     * @param title The title of the box.
     */
    private void showErrorPane(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(getWholeWindow());
        dialog.setVisible(true);
    }

    /**
     * Helper function to ask the user a question.
     *
     * @param message The message in the box.
     * @param title The title of the box.
     * @return True if the user answers yes, false otherwise.
     */
    private boolean askUserQuestion(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(getWholeWindow());
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue).intValue();
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }

    /**
     * Close the external frame, if it is shown.
     */
    public void closeAnyPresentExternalFrame() {
        if (externalWindowOpened) {
            externalFrame.dispose();
        }
    }

    /**
     * Returns header panel.
     *
     * @return The manager's header panel.
     */
    public JPanel getHeaderPanel() {
        return pnlHeader;
    }

    /**
     * Returns options and notes panel.
     *
     * @return The manager's options and notes panel.
     */
    public JPanel getOptionsAndNotesPanel() {
        return pnlOptionsAndNotes;
    }

    /*
     * Whenever a cell is selected in the noteTable, the note in that row is used
     * in the construction of a view/edit InstructorNote window depending on the
     * user's privileges. The mouse adapter is not declared in the constructor
     * to avoid declaring the appControl final.
     */
    MouseAdapter doubleClick = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                JTable target = (JTable) e.getSource();
                //Get the note number column from the TableModel since the column
                //may have been removed from JTable display (if user is student).
                int noteNumber = (Integer) target.getModel()
                        .getValueAt(target.getSelectedRow(),
                                noteNumberColumnNumber);
                InstructorNote note = service.getDBMSSystem()
                        .getNoteManager().getNote(noteNumber);

                //Check to see if the user has the ability to modify notes and
                //check to see if they were the original author. If they were,
                //they can modify this note. If not, a privileged user can view
                //it. Admins can modify all notes. Students and guests can only
                //download files and can only do so by clicking on a "download"
                //arrow.
                if ((isPrivilegedUser() && note.getInstructorNumber()
                        == appControl.getGeneralService().getUser().getUserNumber())
                        || userType.equals(UserType.administrator.toString())) {
                    editInstructorNoteWindow(note);
                } else if (isPrivilegedUser()) {
                    viewInstructorNote(note);
                } else {
                    //Determine if cell is a "download" arrow.
                    int selectedCol = noteTable.columnAtPoint(e.getPoint());
                    if (selectedCol != studentNoteTableFileColumnNumber) {
                        return;
                    }
                    Vector<NoteFileInstance> allAttachedFiles
                            = appControl.getDBMSSystem()
                            .getFileManager().getAllFilesForNote(note);
                    if (allAttachedFiles.isEmpty()) {
                        return;
                    }

                    //Files found to download.
                    //Get location from user.
                    String downloadTitle;
                    if (allAttachedFiles.size() == 1) {
                        downloadTitle = "Save 1 File...";
                    } else {
                        downloadTitle = "Save " + allAttachedFiles.size()
                                + " Files...";
                    }
                    String chosenDirectory = WeatherFileChooser
                            .openDirectoryChooser(new File(CommonLocalFileManager
                                    .getDataDirectory()), downloadTitle, null,
                                    externalWindowOpened ? externalFrame
                                            : mainWin);
                    if (chosenDirectory == null) {
                        return;
                    }

                    //Start saving files...
                    int goodSaveCount = 0;
                    int badSaveCount = 0;
                    Component window = externalWindowOpened ? externalFrame
                            : mainWin;
                    for (NoteFileInstance nfi : allAttachedFiles) {
                        File dest = new File(chosenDirectory + File.separator
                                + nfi.getFileName());
                        if (dest.exists()) {
                            JOptionPane.showMessageDialog(window, "The file "
                                    + nfi.getFileName()
                                    + "\nis already saved at this location.",
                                    "Aborted Save",
                                    JOptionPane.INFORMATION_MESSAGE);
                            badSaveCount++;
                            continue;
                        } //End if for exists() check.

                        try {
                            FileOutputStream fout = new FileOutputStream(dest);
                            fout.write(nfi.getFileData());
                            fout.close();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(window, "A error "
                                    + "occurred while saving the file\n"
                                    + nfi.getFileName() + ".",
                                    "Save Error", JOptionPane.WARNING_MESSAGE);
                            badSaveCount++;
                            continue;
                        }

                        //If code gets here, the save is good.
                        goodSaveCount++;
                    }

                    //Show results.
                    int total = goodSaveCount + badSaveCount;
                    String message = "Download Results:\nTotal Save Attempts: "
                            + total + "\nSuccessful Saves: " + goodSaveCount
                            + "\nFailed Saves: " + badSaveCount;
                    JOptionPane.showMessageDialog(window, message, "Summary",
                            JOptionPane.INFORMATION_MESSAGE);

                    //Test for remaining space in application home, which has no
                    //effect if the save was not there.
                    StorageSpaceTester.testApplicationHome();
                }
            }
        }
    };

    /**
     * Sets the handle to the JTabbedPane containing this manager.
     *
     * @param pane the JTabbedPane
     */
    public final void setCurrentPane(JTabbedPane pane) {
        currentPane = pane;
        //Check for initial state of class notes.
        if (hasClassNotes) {
            currentPane.setForegroundAt(1, Color.green); //green if present
        } else {
            currentPane.setForegroundAt(1, Color.black); //black otherwise
        }
    }

    /**
     * Sees if a daily diary is saved.
     *
     * @return True if the daily diary has been saved; if not, returns false.
     */
    public boolean isDiarySaved() {
        return diarySaved;
    }

    /**
     * Determines if the current user can save class notes. Class notes can only
     * be saved by instructors or administrators.
     *
     * @return True is the user is an admin/instructor, false otherwise.
     */
    private boolean isPrivilegedUser() {
        return userType.equals("administrator")
                || userType.equals("instructor");
    }

    /**
     * Loads the class notes for the current user.
     */
    public final void setClassNotes() {
        Date date = DiaryManager.getDate();
        if (isPrivilegedUser()) {
            hasClassNotes = loadClassNotesForPrivilegedUser(service.getUser(), date.getTime());
        } else {
            hasClassNotes = loadClassNotesForStudent(service.getUser(), date.getTime());
        }

        //Check for new state of class notes.
        if (currentPane == null) {
            return; //in constructor, will check later
        }

        //Change color of tab.
        if (hasClassNotes) {
            currentPane.setForegroundAt(1, Color.green); //green if present
        } else {
            currentPane.setForegroundAt(1, Color.black); //black otherwise
        }

        //Must update note fields for privileged users without marking diary as
        //changed.
        if (isPrivilegedUser()) {
            holdCheckForUnsavedDiary = true;
            String noteText = DiaryManager.getCurrentEntry().getNote(service)
                    .getText();
            txtDiaryNotes.setText(noteText);
            txtLocalNotes.setText(noteText);
            holdCheckForUnsavedDiary = false;
        }
    }

    /**
     * Creates a new EditInstructorNoteWindow. This function is only called if
     * an entry in the noteTable is double clicked. This function exists so it
     * can pass this <code>NotesAndDiaryManager</code> to the new window.
     *
     * @param note The note to be edited.
     */
    private void editInstructorNoteWindow(InstructorNote note) {
        //Get all possilble notes for edit window.
        Vector<InstructorNote> allNotes;
        if (user.getUserType() == UserType.instructor) {
            allNotes = service.getDBMSSystem().getNoteManager()
                    .getNotesByInstructor(user);
        } else {
            allNotes = service.getDBMSSystem().getNoteManager().getAllNotes();
        }

        //Restrict notes to those for the currnt diary resource.
        Vector<InstructorNote> resourceNotes = DiaryManager
                .restrictToCurrentResource(allNotes);

        new EditInstructorNoteWindow(service, this, note, resourceNotes);
    }

    /**
     * Fills the combo box with the dates of the the most recent entries for the
     * current diary resource. The maximum size is the property
     * DailyDiaryDropdownListMaxLength. Dates with no personal notes can be
     * excluded with a flag.
     *
     * @param includeEmptyNotes Whether or not entries with no personal notes
     * should be included.
     */
    private void fillDailyEntryBox(boolean includeEmptyNotes) {
        ArrayList<Date> dates = getAllDiaryEntryDates(includeEmptyNotes);
        int index = dates.size() - 1;
        int count = 0;
        int comboEntryMax = Integer.parseInt(PropertyManager.getGUIProperty("DailyDiaryDropdownListMaxLength"));

        while (index >= 0 && count < comboEntryMax) {
            cmbDateList.insertItemAt(dates.get(index), 0);
            count++;
            index--;
        }
    }

    /**
     * Fills the combo box with the dates of the the most recent class notes.
     * The maximum size is the property DailyDiaryDropdownListMaxLength.
     */
    private void fillClassNotesBox() {
        ArrayList<Date> dates = getAllDatesWithVisibleClassNotes();
        int index = dates.size() - 1;
        int count = 0;
        int comboEntryMax = Integer.parseInt(PropertyManager.getGUIProperty("DailyDiaryDropdownListMaxLength"));

        while (index >= 0 && count < comboEntryMax) {
            cmbDateList.insertItemAt(dates.get(index), 0);
            count++;
            index--;
        }
    }

    /**
     * Sets the manager to show the given panel.
     *
     * @param panelIndex The index of the panel to be loaded.
     */
    private void loadPanel(int panelIndex) {
        //Hide unneeded top panels.
        pnlNewDate.setVisible(panelIndex != ExternalNotesForm.CLASS_NOTES);
        pnlCreatedDate.setVisible(panelIndex == ExternalNotesForm.DAILY_DIARY);

        //Change labed texts.
        if (panelIndex == ExternalNotesForm.DAILY_DIARY) {
            lblDateExplanation.setText("<html><center>Enter your observations below to record a daily diary entry for</center></html>");
            lblNewDate.setText("Make New Entry");
            lblExistingDate.setText("Select Existing Entry");
        } else if (panelIndex == ExternalNotesForm.CLASS_NOTES) {
            lblDateExplanation.setText("<html><center>The class notes below are recorded for</center></html>");
            lblExistingDate.setText("View Another Day");
        } else {    //peronal notea
            lblDateExplanation.setText("<html><center>Enter your presonal notes below to record them for</center></html>");
            lblNewDate.setText("Make A New Note");
            lblExistingDate.setText("View Existing Note");
        }

        //Hide or show personal note controls for daily diary. 
        diaryNotesLabel.setVisible(panelIndex == ExternalNotesForm.DAILY_DIARY);
        diaryNotesScrollPane.setVisible(panelIndex == ExternalNotesForm.DAILY_DIARY);

        //Hide or show side button to save personal notes if the user is not
        //privileged.
        if (!isPrivilegedUser()) {
            saveLocalNotesSideButton.setVisible(panelIndex == ExternalNotesForm.PERSONAL_NOTES);
        }

        //Hide or show external versions of class note buttons.  This depends on
        //both the panel index and the user type.
        if (panelIndex == ExternalNotesForm.CLASS_NOTES) {
            createNoteSideButton.setVisible(isPrivilegedUser());
            editNoteSideButton.setVisible(isPrivilegedUser());
            refreshNotesSideButton.setVisible(!isPrivilegedUser());
        } else {
            createNoteSideButton.setVisible(false);
            editNoteSideButton.setVisible(false);
            refreshNotesSideButton.setVisible(false);
        }

        //Change combo box dates.
        updateDateSelectionBox(panelIndex);
    }

    /**
     * Updates the date selection box by showing dates that are appropriate for
     * the given panel index. The method called by this method handle
     * restricting the list size and making sure the dates are for the current
     * resource of the <code>DiaryManager</code>.
     *
     * @param panelIndex The given panel index.
     */
    private void updateDateSelectionBox(int panelIndex) {
        //remove old
        holdCmbDailyEntryListener = true;
        cmbDateList.removeAllItems();

        //add new
        if (panelIndex == ExternalNotesForm.DAILY_DIARY) {
            //Fill box with all diary entries.
            fillDailyEntryBox(true);
        } else if (panelIndex == ExternalNotesForm.PERSONAL_NOTES) {
            //Fill box with diary entries that contain personal notes.
            fillDailyEntryBox(false);
        } else {
            //Fill box with dates that have class notes.
            fillClassNotesBox();
        }
        holdCmbDailyEntryListener = false;
    }

    /**
     * Updates the date selection box to the date supplied.
     *
     * @param newDate The new date to be added.
     */
    public void updateSelectedDate(Date newDate) {
        newDate = ResourceTimeManager.
                getStartOfDayDateFromMilliseconds(newDate.getTime(),
                        DiaryManager.getResource().getTimeZone().getTimeZone());
        boolean entered = false;
        int itemCount = cmbDateList.getItemCount();
        Date itemDate;
        for (int i = 0; i < itemCount; i++) {
            itemDate = (Date) cmbDateList.getItemAt(i);
            if (!newDate.after(itemDate)) {
                if (newDate.before(itemDate)) {
                    cmbDateList.insertItemAt(newDate, i);
                }
                entered = true;
                break;
            }
        }
        if (!entered) {
            cmbDateList.addItem(newDate);
        }
        cmbDateList.setSelectedItem(newDate);
        updateFieldContent(newDate);
    }

    /**
     * Gets the PersonalNotes panel.
     *
     * @return The personal notes panel.
     */
    public JPanel getPersonalNotesPanel() {
        return pnlPersonalNotes;
    }

    /**
     * Gets the class notes panel.
     *
     * @return The class notes panel.
     */
    public JPanel getClassNotesPanel() {
        //If user is not an admin or instructor, they cannot add/modify notes
        if (!isPrivilegedUser()) {
            this.btnEditNote.setVisible(false);
            this.btnCreateNote.setVisible(false);
        } else {
            this.btnRefreshNotes.setVisible(false);
        }
        return pnlClassNotes;
    }

    /**
     * Gets the daily diary panel.
     *
     * @return The diary panel.
     */
    public JPanel getDiaryPanel() {
        return pnlDiary;
    }

    /**
     * Opens up the main external window with the personal notes, class notes,
     * and diary panels.
     *
     * @param panelFocus The panel to focus on upon open.
     */
    public void openExternalWindow(int panelFocus) {
        if (!externalWindowOpened) {
            externalFrame = new ExternalNotesForm();
            externalWindowOpened = true;
            externalFrame.displayExternalNotesAndDiaryWindow(panelFocus, this,
                    winListener, changeListener, closeListener);
            loadPanel(panelFocus);
            mainWin.repaint();
        } else {
            loadPanel(panelFocus);
            externalFrame.requestFocus();
        }
        updateSelectedDate(DiaryManager.getDate());

        //Set check boxes after the resouse is selected to avoid umwanted fire.
        this.changeLocationCheckBox.setSelected(true);
        this.changeDateCheckBox.setSelected(true);
    }

    /**
     * Updates the static <code>Resource</code> of the
     * <code>DiaryManager</code>. As updates should not happen if the manager is
     * external, they will not. This function hides the internal panel if the
     * parameter is null.
     *
     * @param newResource The new <code>Resource</code>.
     */
    public void updateInternalDiaryResource(Resource newResource) {
        //Manager should not be changed if external.
        if (!externalWindowOpened) {
            //If parameter is null, just hide panel.
            if (newResource == null) {
                currentPane.setVisible(false);
            } else {
                currentPane.setVisible(true);
                DiaryManager.setResource(newResource);
                updateFieldContent(DiaryManager.getDate());
            }
        }
    }

    /**
     * Gets the boolean value to determine if personal notes have been modified
     * since the last save or not.
     *
     * @return True or false based on if notes have been modified since last
     * save.
     */
    public boolean getPersonalNotesSaved() {
        return personalNotesSaved;
    }

    /**
     * Checks if personal notes have been modified since the last save and asks
     * the user to save before continuing on.
     */
    public void checkPersonalNotesSaved() {
        if (!personalNotesSaved) {
            if (askUserQuestion("Modifications have been made to personal notes "
                    + "since the last save. Would you like to save them?",
                    "Save Changes?")) {
                if (savePersonalNotes()) {
                    showInfoPane("Your personal notes have been saved",
                            "Notes Saved");
                }
            } else {
                setPersonalNotes();
            }
        }
    }

    /**
     * Sets the personal notes text area for the personal notes panel.
     */
    private void setPersonalNotes() {
        txtLocalNotes.setText(DiaryManager.getCurrentEntry()
                .getNote(appControl.getGeneralService()).getText());
        personalNotesSaved = true;  //Back where we were, so nothing new to save.
    }

    /**
     * Saves just the personal notes section by getting the daily entry for the
     * given day and setting the notes portion of it to the notes that are to be
     * saved.
     *
     * @return Whether or not the save was successful.
     */
    private boolean savePersonalNotes() {
        Date currentDate = DiaryManager.getDate();
        Note currentPersonalNote = new Note(currentDate, txtLocalNotes.getText());
        DailyEntry currentDiaryEntry = DiaryManager.getCurrentEntry();
        currentDiaryEntry.setNote(currentPersonalNote);
        currentDiaryEntry.setLastModifiedDate(new Date());
        boolean saved = DiaryManager.saveEntry(currentDiaryEntry);
        setPersonalNotes();
        setDiaryFieldsAndFlags();
        personalNotesSaved = saved;
        return saved;
    }

    /**
     * Checks if the current diary entry has been changed since the last save.
     * If it has, a pop-up will be displayed asking the user to save before
     * moving on.
     */
    public void checkDiarySaved() {
        if (!diarySaved) {
            if (askUserQuestion("Modifications have been made to the daily diary "
                    + "since the last save. Would you like to save them?",
                    "Save Changes?")) {
                saveDiaryEntry();
            } else {
                setDiaryFieldsAndFlags();
                diarySaved = true;      //Don't care data isn't saved, so pretend it is.
            }
        }
    }

    /**
     * Sets all the diary entry fields and data flags based on the values held
     * by the current entry.
     */
    private void setDiaryFieldsAndFlags() {
        currentEntry = DiaryManager.getCurrentEntry();
        
        //Set Diary Data Flags
        isDiaryEntrySaved = DiaryManager.isDiaryEntrySaved(currentEntry
                .getDate());
        arePrivateClassNotesSaved = DiaryManager
                .arePrivateClassNotesSaved(currentEntry.getDate());

        //Temperature Values
        fldMaxTemp.setText(currentEntry.getMaxTemp());
        // cmbMaxTempTime.setSelectedItem(currentEntry.getMaxTempTime().displayString());
        fldMinTemp.setText(currentEntry.getMinTemp());
        //  cmbMinTempTime.setSelectedItem(currentEntry.getMinTempTime().displayString());
        fldTempRange.setText(currentEntry.getTempRange());
        cmbTempTrend.setSelectedItem(currentEntry.getTempTrendType().displayString());
        //Barometric Pressure Values
        fldStartBP.setText(currentEntry.getStartBP());
        // cmbMaxBPTime.setSelectedItem(currentEntry.getMaxBPTime().displayString());
        fldEndBP.setText(currentEntry.getEndBP());
        // cmbMinBPTime.setSelectedItem(currentEntry.getMinBPTime().displayString());
        cmbBPTrend.setSelectedItem(currentEntry.getBPTrendType().displayString());
        fldBPRange.setText(currentEntry.getBPRange());

        //Dew Point Values
        fldStartDP.setText(currentEntry.getStartDP());
        //  cmbMaxDPTime.setSelectedItem(currentEntry.getMaxDPTime().displayString());
        fldEndDP.setText(currentEntry.getEndDP());
        //  cmbMinDPTime.setSelectedItem(currentEntry.getMinDPTime().displayString());
        fldDPRange.setText(currentEntry.getDPRange());
        cmbDPTrend.setSelectedItem(currentEntry.getDPTrendType().displayString());
        //Relative Humidity Values
        fldMaxRH.setText(currentEntry.getMaxRH());
        //   cmbMaxRHTime.setSelectedItem(currentEntry.getMaxRHTime().displayString());
        fldMinRH.setText(currentEntry.getMinRH());
        //    cmbMinRHTime.setSelectedItem(currentEntry.getMinRHTime().displayString());
        fldRHRange.setText(currentEntry.getRHRange());
        cmbRHTrend.setSelectedItem(currentEntry.getRHTrendType().displayString());
        //Sky & Cloud Condition Values
        cmbPrimaryMorningClouds.setSelectedItem(currentEntry.getPrimaryMorningCloudType().displayString());
        cmbSecondaryMorningClouds.setSelectedItem(currentEntry.getSecondaryMorningCloudType().displayString());
        cmbPrimaryAfternoonClouds.setSelectedItem(currentEntry.getPrimaryAfternoonCloudType().displayString());
        cmbSecondaryAfternoonClouds.setSelectedItem(currentEntry.getSecondaryAfternoonCloudType().displayString());
        cmbPrimaryNightClouds.setSelectedItem(currentEntry.getPrimaryNightCloudType().displayString());
        cmbSecondaryNightClouds.setSelectedItem(currentEntry.getSecondaryNightCloudType().displayString());
        //Wind Values
        setWindDirectionCheckBoxes();
        cmbWindSpeed.setSelectedItem(currentEntry.getWindSpeed().displayString());
        fldMaxGustSpeed.setText(currentEntry.getMaxGustSpeed());
        fldDailyPrecipitation.setText(currentEntry.getDailyPrecipitation());
        fldMaxHeatIndex.setText(currentEntry.getMaxHeatIndex());
        fldMinWindChill.setText(currentEntry.getMinWindChill());
        cmbWindDirectionSummary.setSelectedItem(currentEntry.getWindDirectionSummary().displayString());
        cmbUpperAirWindDirection.setSelectedItem(currentEntry.getUpperAirWindDirection().displayString());

        //Note value
        txtDiaryNotes.setText(currentEntry.getNote(appControl
                .getGeneralService()).getText());

        //Update the field that shows when this entry was last modified.
        if (isDiaryEntrySaved) {
            Date lastModifiedDate = currentEntry.getLastModifiedDate();
            lastModifiedStatusLabel.setText(dateFormatMMDDYYYY
                    .format(lastModifiedDate));
        } else if (arePrivateClassNotesSaved) {
            lastModifiedStatusLabel
                    .setText("<html><center>There are only private<br/>"
                            + "class notes for this date.</center></html>");
        } else {
            lastModifiedStatusLabel
                    .setText("<html><center>This is a<br/>new entry."
                            +"</center></html>");
        }
        diarySaved = true;
    }

    /**
     * Takes the array of wind directions selected from the diary entry and
     * selects the check boxes corresponding to the elements of the array.
     */
    public void setWindDirectionCheckBoxes() {
        if (currentEntry.getSurfaceAirWindDirections() == null) {
            currentEntry.setSurfaceAirWindDirections(new ArrayList<WindDirectionType>());
        }
        surfaceWindDirection = currentEntry.getSurfaceAirWindDirections();
        JCheckBox[] checkBoxes = getWindDirectionButtonArray();
        WindDirectionType[] types = WindDirectionType.values();

        for (JCheckBox r : checkBoxes) {
            r.setSelected(false);
        }

        outer:
        for (WindDirectionType t : surfaceWindDirection) {
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals(t)) {
                    checkBoxes[i - 1].setSelected(true);
                    continue outer;
                }
            }
        }
    }

    /**
     * Generates an array of the wind direction check boxes.
     *
     * @return The array of the wind direction check boxes.
     */
    private JCheckBox[] getWindDirectionButtonArray() {
        // Note: The values in this array MUST match the enumeration values in  
        // the WindDirectionType class exactly except for the N/A option.
        return new JCheckBox[]{
            checkNorthWest, checkNorth, checkNorthEast, checkEast,
            checkSouthEast, checkSouth, checkSouthWest, checkWest, checkCalm
        };
    }

    /**
     * Called when a change is made to any diary field to alert the program
     * changes have been made since the last time the diary was saved. The diary
     * will be marked as unsaved if the change was not made programmatically as
     * indicated by a flag (holdCheckForUnsavedDiary).
     */
    public void checkToMarkDiaryUnsaved() {
        if (holdCheckForUnsavedDiary) {
            return;
        }
        diarySaved = false;
    }

    /**
     * Saves the current diary entry and makes sure the fields being saved are
     * formatted correctly.
     *
     * @return Whether or not the save was successful.
     */
    public boolean saveDiaryEntry() {
        // Check whether the format of the values in the textboxes are valid.
        entryFormat = DiaryManager.testEntryFormat(fldMaxTemp.getText(),
                fldMinTemp.getText(), fldStartBP.getText(), fldEndBP.getText(),
                fldStartDP.getText(), fldEndDP.getText(), fldMaxRH.getText(),
                fldMinRH.getText(), fldMaxGustSpeed.getText(),
                fldDailyPrecipitation.getText(), fldMaxHeatIndex.getText(),
                fldMinWindChill.getText());

        if (entryFormat.equals("valid")) {
            Date currentDate = DiaryManager.getDate();

            //Make the note to be saved locally, which is empty for a privileged
            //user.
            Note localNote;
            if (isPrivilegedUser()) {
                localNote = new Note(currentDate);
            } else {
                localNote = new Note(currentDate, txtDiaryNotes.getText());
            }

            DailyEntry currentDailyEntry = new DailyEntry(
                    currentDate,
                    DiaryManager.getResource().getResourceNumber(),
                    DiaryManager.getResource().getResourceName(),
                    DiaryManager.getRelatedResource().getResourceNumber(),
                    DiaryManager.getRelatedResource().getResourceName(),
                    localNote, //local entry note
                    fldMaxTemp.getText(),
                    //  HourlyTimeType.getEnum(cmbMaxTempTime.getSelectedItem().toString()),
                    fldMinTemp.getText(),
                    // HourlyTimeType.getEnum(cmbMinTempTime.getSelectedItem().toString()),
                    fldTempRange.getText(),
                    TemperatureTrendType.getEnum(cmbTempTrend.getSelectedItem().toString()),
                    fldStartBP.getText(),
                    //  HourlyTimeType.getEnum(cmbMaxBPTime.getSelectedItem().toString()),
                    fldEndBP.getText(),
                    //  HourlyTimeType.getEnum(cmbMinBPTime.getSelectedItem().toString()),
                    fldBPRange.getText(),
                    BarometricPressureTrendType.getEnum(cmbBPTrend.getSelectedItem().toString()),
                    fldStartDP.getText(),
                    // HourlyTimeType.getEnum(cmbMaxDPTime.getSelectedItem().toString()),
                    fldEndDP.getText(),
                    //  HourlyTimeType.getEnum(cmbMinDPTime.getSelectedItem().toString()),
                    fldDPRange.getText(),
                    DewPointTrendType.getEnum(cmbDPTrend.getSelectedItem().toString()),
                    fldMaxRH.getText(),
                    //  HourlyTimeType.getEnum(cmbMaxRHTime.getSelectedItem().toString()),
                    fldMinRH.getText(),
                    //   HourlyTimeType.getEnum(cmbMinRHTime.getSelectedItem().toString()),
                    fldRHRange.getText(),
                    RelativeHumidityTrendType.getEnum(cmbRHTrend.getSelectedItem().toString()),
                    CloudType.getEnum(cmbPrimaryMorningClouds.getSelectedItem().toString()),
                    CloudType.getEnum(cmbSecondaryMorningClouds.getSelectedItem().toString()),
                    CloudType.getEnum(cmbPrimaryAfternoonClouds.getSelectedItem().toString()),
                    CloudType.getEnum(cmbSecondaryAfternoonClouds.getSelectedItem().toString()),
                    CloudType.getEnum(cmbPrimaryNightClouds.getSelectedItem().toString()),
                    CloudType.getEnum(cmbSecondaryNightClouds.getSelectedItem().toString()),
                    surfaceWindDirection,
                    WindDirectionSummaryType.getEnum(cmbWindDirectionSummary.getSelectedItem().toString()),
                    WindSpeedType.getEnum(cmbWindSpeed.getSelectedItem().toString()),
                    fldMaxGustSpeed.getText(),
                    fldDailyPrecipitation.getText(),
                    fldMaxHeatIndex.getText(),
                    fldMinWindChill.getText(),
                    WindDirectionType.getEnum(cmbUpperAirWindDirection.getSelectedItem().toString()),
                    new Date()
            // Questions
            /*   YesNoType.getEnum(cmbTempProfileQ1.getSelectedItem().toString()),
                    PressureChangeRelationType.getEnum(cmbDailyPressureQ1.getSelectedItem().toString()),
                    PrecipitationChanceType.getEnum(cmbDailyPressureQ2.getSelectedItem().toString()),
                    YesNoType.getEnum(cmbDailyHumidityQ1.getSelectedItem().toString()),
                    TemperaturePredictionType.getEnum(cmbPredictionsQ1.getSelectedItem().toString()),
                    YesNoType.getEnum(cmbPredictionsQ2.getSelectedItem().toString())*/
            );
            Debug.println("Saving: " + currentDailyEntry.toString());
            boolean saved = DiaryManager.saveEntry(currentDailyEntry);
            setDiaryFieldsAndFlags();
            setPersonalNotes();
            diarySaved = saved;
            personalNotesSaved = true;
            return saved;
        } else {
            diarySaved = false;
            showErrorPane(entryFormat, "Entry Errors");
            return false;
        }
    }

    /**
     * Calculates a range for the two values passed into the method.
     *
     * @param maxVal The max value of the range to calculate.
     * @param minVal The min value of the range to calculate.
     * @return The range of the two values.
     */
    public String getRange(String maxVal, String minVal) {
        DecimalFormat df = new DecimalFormat("#0.###");
        if (FormatManager.isDecimalValid(maxVal)
                && FormatManager.isDecimalValid(minVal)) {
            return df.format(Double.parseDouble(maxVal)
                    - Double.parseDouble(minVal));
        }
        return "";
    }

    /**
     * Adds a wind direction to the wind direction array.
     *
     * @param direction The direction to add.
     */
    private void addSurfaceWindDirection(WindDirectionType direction) {
        surfaceWindDirection.add(direction);
    }

    /**
     * Removes a wind direction from the wind direction array.
     *
     * @param direction The direction to remove.
     */
    private void removeSurfaceWindDirection(WindDirectionType direction) {
        surfaceWindDirection.remove(direction);
    }

    /**
     * Hides buttons on the tabbed panes. They are replaced by buttons on the
     * side panel when the manager is in external mode. This method is also
     * responsible for setting the manager's camera selection toot to the
     * correct item.
     */
    public void externalize() {
        personalNotePanel.setVisible(false);
        classNotePanel.setVisible(false);
        for (int i = 0; i < cameraComboBox.getItemCount(); i++) {
            if (service.getCurrentWeatherCameraResource().getName().equals(
                    cameraComboBox.getItemAt(i).toString())) {
                cameraComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Reverses the changes made by externalize() and makes the buttons visible
     * again.
     */
    public void internalize() {
        personalNotePanel.setVisible(true);
        classNotePanel.setVisible(true);
    }

    /**
     * This method loads the class notes for all guests and students. The table
     * only displays the author and the text of the note.
     *
     * @param user The user to get class notes for.
     * @param currentTime The current time.
     * @return True if table contains notes, false otherwise
     */
    private boolean loadClassNotesForStudent(User user, long currentTime) {
        //Allow single-cell selection.
        noteTable.setRowSelectionAllowed(false);

        Vector<InstructorNote> noteVector = DiaryManager
                .restrictToCurrentResource(service.getDBMSSystem().
                        getNoteManager().getNotesVisibleToUser(user,
                                new java.sql.Date(currentTime)));
        Vector<NoteFileInstance> attachedFiles = new Vector<>();
        User noteAuthor;
        int noteTableNumberOfRows = noteVector.size();

        //Create columns for the note number, author and text.
        noteTable.setModel(new ImageTableModel(noteTableNumberOfRows,
                studentNoteTableNumberOfColumns,
                studentNoteTableFileColumnNumber));
        noteTable.getColumnModel().getColumn(noteAuthorColumnNumber)
                .setHeaderValue("Author");
        noteTable.getColumnModel().getColumn(studentNoteTableTextColumnNumber)
                .setHeaderValue("Text");
        noteTable.getColumnModel().getColumn(studentNoteTableFileColumnNumber)
                .setHeaderValue("Files");

        for (int i = 0; i < noteTableNumberOfRows; i++) {
            noteAuthor = service.getDBMSSystem().getUserManager().
                    obtainUser(noteVector.get(i).getInstructorNumber());
            attachedFiles = service.getDBMSSystem().getFileManager().
                    getAllFilesForNote(noteVector.get(i));
            String noteText = noteVector.get(i).getText();
            if (noteAuthor == null) {
                noteTable.setValueAt("DELETED", i, noteAuthorColumnNumber);
            } else {
                noteTable.setValueAt(noteVector.get(i).getNoteNumber(), i,
                        noteNumberColumnNumber);
                noteTable.setValueAt(noteAuthor.getLastName(), i,
                        noteAuthorColumnNumber);
                noteTable.setValueAt(noteText, i,
                        studentNoteTableTextColumnNumber);
                if (!attachedFiles.isEmpty()) {
                    noteTable.setValueAt(IconProperties
                            .getArrowDownSmallIcon(), i,
                            studentNoteTableFileColumnNumber);
                } else {
                    noteTable.setValueAt(IconProperties.getCross(), i,
                            studentNoteTableFileColumnNumber);
                }
            }
            noteTable.revalidate();
        }

        //Removes note number column from JTable display but not from the
        //underlying table data.
        noteTable.getColumnModel().getColumn(noteNumberColumnNumber)
                .setMinWidth(0);
        noteTable.getColumnModel().getColumn(noteNumberColumnNumber)
                .setMaxWidth(0);
        noteTable.getColumnModel().getColumn(noteNumberColumnNumber)
                .setWidth(0);
        //Sets other column widths.
        noteTable.getColumnModel().getColumn(noteAuthorColumnNumber)
                .setMaxWidth(100);
        noteTable.getColumnModel().getColumn(studentNoteTableFileColumnNumber)
                .setMaxWidth(50);

        //Indicate if notes were found.
        return noteTableNumberOfRows > 0;
    }

    /**
     * This method loads the class notes for a specified privileged user. Most
     * of the note's data is included in the table.
     *
     * @param user The user to get class notes for.
     * @param currentTime The current time.
     * @return True if table contains notes, false otherwise
     */
    private boolean loadClassNotesForPrivilegedUser(User user, long currentTime) {
        //Allow full-row selection.
        noteTable.setRowSelectionAllowed(true);

        Vector<InstructorNote> noteVector = DiaryManager
                .restrictToCurrentResource(service.getDBMSSystem()
                        .getNoteManager().getNotesVisibleToUser(user,
                                new java.sql.Date(currentTime)));
        Vector<NoteFileInstance> attachedFiles = new Vector<>();
        User noteAuthor;
        int noteTableNumberOfRows = noteVector.size();

        noteTable.setModel(new MyDefaultTableModel(noteTableNumberOfRows, privilegedUsernoteTableNumberOfColumns));
        noteTable.getColumnModel().getColumn(noteNumberColumnNumber).setHeaderValue("Number");
        noteTable.getColumnModel().getColumn(noteAuthorColumnNumber).setHeaderValue("Author");
        noteTable.getColumnModel().getColumn(noteTitleColumnNumber).setHeaderValue("Title");
        noteTable.getColumnModel().getColumn(noteAttachedFilesColumnNumber).setHeaderValue("Files");

        for (int i = 0; i < noteTableNumberOfRows; i++) {
            noteAuthor = service.getDBMSSystem().getUserManager().
                    obtainUser(noteVector.get(i).getInstructorNumber());
            attachedFiles = service.getDBMSSystem().getFileManager().
                    getAllFilesForNote(noteVector.get(i));
            noteTable.setValueAt(noteVector.get(i).getNoteNumber(), i, noteNumberColumnNumber);
            if (noteAuthor == null) {
                noteTable.setValueAt("DELETED", i, noteAuthorColumnNumber);
            } else {
                noteTable.setValueAt(noteAuthor.getLastName(), i, noteAuthorColumnNumber);
                noteTable.setValueAt(noteVector.get(i).getNoteTitle(), i, noteTitleColumnNumber);
                noteTable.setValueAt(noteVector.get(i).getText(), i, noteTextColumnNumber);
                noteTable.setValueAt(attachedFiles.size(), i, noteAttachedFilesColumnNumber);
            }
            noteTable.revalidate();
        }

        //Removes note text column from JTable display but not from the
        //underlying table data so the text can be found for tool tips.
        noteTable.getColumnModel().getColumn(noteTextColumnNumber)
                .setMinWidth(0);
        noteTable.getColumnModel().getColumn(noteTextColumnNumber)
                .setMaxWidth(0);
        noteTable.getColumnModel().getColumn(noteTextColumnNumber)
                .setWidth(0);
        //Sets other column widths.
        noteTable.getColumnModel().getColumn(noteNumberColumnNumber)
                .setMaxWidth(50);
        noteTable.getColumnModel().getColumn(noteTitleColumnNumber)
                .setMinWidth(150);
        noteTable.getColumnModel()
                .getColumn(noteAttachedFilesColumnNumber)
                .setMaxWidth(50);

        //Indicate if notes were found.
        return noteTableNumberOfRows > 0;
    }

    /**
     * Checks if any entries exist for the current resource or not.
     *
     * @return True if at least one entry exists.
     */
    private boolean hasExistingEntriesForCurrentResource() {
        if (!DiaryManager.getAllEntriesRorCurrentDiaryResourceWithData()
                .isEmpty()) {
            return true;
        } else {
            showInfoPane("You have no saved diary entries for " + DiaryManager
                    .getResource().getResourceName() + ".", "No Entries");
            return false;
        }
    }

    // @todo See what can be done with this method.
//    private Vector<Course> showCourseSelectionDialog(Vector<Course> courseList) {
//        Map<Course, JCheckBox> courseCheckMap = new HashMap<Course, JCheckBox>();
//        Vector<Course> chosenCourses = new Vector<Course>();
//        final JDialog dialog = new JDialog();
//        dialog.setModal(true);
//        dialog.setSize(400, 300);
//        MainApplicationWindow.centerWindow(dialog);
//        JScrollPane scrollPane = new JScrollPane();
//        JButton okButton = new JButton("Ok");
//        okButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wasClassSelectionCanceled = false;
//                dialog.setVisible(false);
//            }
//        });
//        JButton cancelButton = new JButton("Cancel");
//        cancelButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wasClassSelectionCanceled = true;
//                dialog.setVisible(false);
//            }
//        });
//        JViewport viewPort = new JViewport();
//        JPanel checkPanel = new JPanel(new GridLayout(courseList.size(), 1));
//        String instructions = "Select the courses to which this note pertains.";
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        mainPanel.add(new JLabel(instructions), BorderLayout.NORTH);
//        for (Course course: courseList) {
//            String courseText = course.getDepartment() + "-" + course.getCourse()
//                    + "-" + course.getSection() + " " + course.getName();
//            JCheckBox checkBox = new JCheckBox(courseText);
//            courseCheckMap.put(course, checkBox);
//            checkPanel.add(checkBox);
//        }
//        viewPort.setView(checkPanel);
//        scrollPane.setViewport(viewPort);
//        JPanel southPanel = new JPanel();
//        southPanel.add(okButton);
//        southPanel.add(cancelButton);
//        mainPanel.add(scrollPane, BorderLayout.CENTER);
//        mainPanel.add(southPanel, BorderLayout.SOUTH);
//        dialog.add(mainPanel);
//        dialog.setVisible(true);
//        if (!wasClassSelectionCanceled) {
//            for (Course course: courseList) {
//                if (courseCheckMap.get(course).isSelected())
//                    chosenCourses.add(course);
//            }
//        }
//        return chosenCourses;
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlPanels = new javax.swing.JTabbedPane();
        pnlPersonalNotes = new javax.swing.JPanel();
        notesScrollPane = new javax.swing.JScrollPane();
        txtLocalNotes = new javax.swing.JTextArea();
        personalNotePanel = new javax.swing.JPanel();
        btnSaveLocalNotes = new javax.swing.JButton();
        btnDiary = new javax.swing.JButton();
        btnExpandPersonalNotes = new javax.swing.JButton();
        pnlClassNotes = new javax.swing.JPanel();
        classNotePanel = new javax.swing.JPanel();
        btnCreateNote = new javax.swing.JButton();
        btnEditNote = new javax.swing.JButton();
        btnExpandNotes = new javax.swing.JButton();
        btnRefreshNotes = new javax.swing.JButton();
        noteScrollPane = new javax.swing.JScrollPane();
        noteTable = isPrivilegedUser() ? 
        new ShowColumnAsToolTipTable(noteTextColumnNumber) : new JTable() {    
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == studentNoteTableTextColumnNumber) {
                    return wordWrapRenderer;
                } else {
                    return super.getCellRenderer(row, column);
                }
            }
        };
        pnlDiary = new javax.swing.JPanel();
        diaryTabbedPane = new javax.swing.JTabbedPane();
        daily_diary_Part1_JPanel = new javax.swing.JPanel();
        pnlRelativeHumidity = new javax.swing.JPanel();
        lblMaxRH = new javax.swing.JLabel();
        lblRHRange = new javax.swing.JLabel();
        fldMaxRH = new javax.swing.JTextField();
        fldMinRH = new javax.swing.JTextField();
        fldRHRange = new javax.swing.JTextField();
        lblMinRHTime = new javax.swing.JLabel();
        cmbMaxRHTime = new javax.swing.JComboBox();
        cmbMinRHTime = new javax.swing.JComboBox();
        lblMinRH = new javax.swing.JLabel();
        lblRHTrend = new javax.swing.JLabel();
        cmbRHTrend = new javax.swing.JComboBox();
        lblMaxRHTime = new javax.swing.JLabel();
        pnlCloudTypes = new javax.swing.JPanel();
        lblMorningClouds = new javax.swing.JLabel();
        lblAfternoonClouds = new javax.swing.JLabel();
        lblNightClouds = new javax.swing.JLabel();
        cmbPrimaryNightClouds = new javax.swing.JComboBox();
        cmbPrimaryAfternoonClouds = new javax.swing.JComboBox();
        cmbPrimaryMorningClouds = new javax.swing.JComboBox();
        cmbSecondaryMorningClouds = new javax.swing.JComboBox();
        cmbSecondaryAfternoonClouds = new javax.swing.JComboBox();
        cmbSecondaryNightClouds = new javax.swing.JComboBox();
        lblPrimary = new javax.swing.JLabel();
        lblSecondary = new javax.swing.JLabel();
        temperatureTitle = new javax.swing.JLabel();
        temperatureLink = new javax.swing.JLabel();
        barometricPressureTitle = new javax.swing.JLabel();
        barometricPressureLink = new javax.swing.JLabel();
        dewPointTitle = new javax.swing.JLabel();
        dewPointLink = new javax.swing.JLabel();
        relativeHumidityTitle = new javax.swing.JLabel();
        relativeHumidityLink = new javax.swing.JLabel();
        cloudsTitle = new javax.swing.JLabel();
        cloudsLink = new javax.swing.JLabel();
        pnlTemperature = new javax.swing.JPanel();
        lblMaxTemp = new javax.swing.JLabel();
        lblMinTemp = new javax.swing.JLabel();
        lblTempRange = new javax.swing.JLabel();
        fldMaxTemp = new javax.swing.JTextField();
        fldMinTemp = new javax.swing.JTextField();
        fldTempRange = new javax.swing.JTextField();
        lblMaxTempTime = new javax.swing.JLabel();
        lblMinTempTime = new javax.swing.JLabel();
        cmbMaxTempTime = new javax.swing.JComboBox();
        cmbMinTempTime = new javax.swing.JComboBox();
        lblTempTrend = new javax.swing.JLabel();
        cmbTempTrend = new javax.swing.JComboBox();
        pnlBarometricPressure = new javax.swing.JPanel();
        lblStartBP = new javax.swing.JLabel();
        lblBPTrend = new javax.swing.JLabel();
        fldStartBP = new javax.swing.JTextField();
        fldEndBP = new javax.swing.JTextField();
        lblMaxBPTime = new javax.swing.JLabel();
        lblMinBPTime = new javax.swing.JLabel();
        cmbEndBPTime = new javax.swing.JComboBox();
        cmbStartBPTime = new javax.swing.JComboBox();
        cmbBPTrend = new javax.swing.JComboBox();
        lblEndBP = new javax.swing.JLabel();
        lblBPRange = new javax.swing.JLabel();
        fldBPRange = new javax.swing.JTextField();
        pnlDewPoint = new javax.swing.JPanel();
        lblStartDP = new javax.swing.JLabel();
        lblDPTrend = new javax.swing.JLabel();
        fldStartDP = new javax.swing.JTextField();
        fldEndDP = new javax.swing.JTextField();
        lblMaxDPTime = new javax.swing.JLabel();
        lblMinDPTime = new javax.swing.JLabel();
        cmbEndDPTime = new javax.swing.JComboBox();
        cmbStartDPTime = new javax.swing.JComboBox();
        cmbDPTrend = new javax.swing.JComboBox();
        lblEndDP = new javax.swing.JLabel();
        lblDPRange = new javax.swing.JLabel();
        fldDPRange = new javax.swing.JTextField();
        daily_Diary_Part2_JPanel = new javax.swing.JPanel();
        pnlWind = new javax.swing.JPanel();
        pnlCompass = new javax.swing.JPanel();
        lblSouth = new javax.swing.JLabel();
        lblEast = new javax.swing.JLabel();
        lblWest = new javax.swing.JLabel();
        lblNorth = new javax.swing.JLabel();
        checkNorth = new javax.swing.JCheckBox();
        checkNorthWest = new javax.swing.JCheckBox();
        checkNorthEast = new javax.swing.JCheckBox();
        checkWest = new javax.swing.JCheckBox();
        checkCalm = new javax.swing.JCheckBox();
        checkEast = new javax.swing.JCheckBox();
        checkSouthWest = new javax.swing.JCheckBox();
        checkSouthEast = new javax.swing.JCheckBox();
        checkSouth = new javax.swing.JCheckBox();
        lblWindSpeed = new javax.swing.JLabel();
        lblMaxGustSpeed = new javax.swing.JLabel();
        cmbWindSpeed = new javax.swing.JComboBox();
        fldMaxGustSpeed = new javax.swing.JTextField();
        lblWindPanelHeader = new javax.swing.JLabel();
        lblWindDirectionSummary = new javax.swing.JLabel();
        cmbWindDirectionSummary = new javax.swing.JComboBox();
        pnlAdditionalVariables = new javax.swing.JPanel();
        lblDailyPrecip = new javax.swing.JLabel();
        lblMaxHI = new javax.swing.JLabel();
        lblWindDirectionUp = new javax.swing.JLabel();
        fldDailyPrecipitation = new javax.swing.JTextField();
        fldMaxHeatIndex = new javax.swing.JTextField();
        fldMinWindChill = new javax.swing.JTextField();
        lblMinWindChill = new javax.swing.JLabel();
        precipitationLink = new javax.swing.JLabel();
        heatIndexLink = new javax.swing.JLabel();
        windChillLink = new javax.swing.JLabel();
        windDirectionLink = new javax.swing.JLabel();
        cmbUpperAirWindDirection = new javax.swing.JComboBox();
        windSpeedAndDirectionTitle = new javax.swing.JLabel();
        windSpeedAndDirectionLink = new javax.swing.JLabel();
        additionalVariablesTitle = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblTemperatureProfile = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblTempProfileQ1 = new javax.swing.JLabel();
        cmbTempProfileQ1 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        lblDailyPressureChange = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblDailyPressureQ1 = new javax.swing.JLabel();
        cmbDailyPressureQ1 = new javax.swing.JComboBox();
        lblDailyPressureQ2 = new javax.swing.JLabel();
        cmbDailyPressureQ2 = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        lblDailyHumidityProfile = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblDailyHumidityQ1 = new javax.swing.JLabel();
        cmbDailyHumidityQ1 = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        lblPredictions = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lblPredictionsQ1 = new javax.swing.JLabel();
        cmbPredictionsQ1 = new javax.swing.JComboBox();
        lblPredictionsQ2 = new javax.swing.JLabel();
        cmbPredictionsQ2 = new javax.swing.JComboBox();
        pnlButtons = new javax.swing.JPanel();
        btnSaveEntry = new javax.swing.JButton();
        btnExportEntry = new javax.swing.JButton();
        btnSaveAndClose = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        expandCheckBox = new javax.swing.JCheckBox();
        pnlHeader = new javax.swing.JPanel();
        pnlDate = new javax.swing.JPanel();
        lblDateExplanation = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        lblGapFill1 = new javax.swing.JLabel();
        pnlNewDate = new javax.swing.JPanel();
        lblNewDate = new javax.swing.JLabel();
        btnNewEntry = new javax.swing.JButton();
        lblGapFill2 = new javax.swing.JLabel();
        pnlExistintngDate = new javax.swing.JPanel();
        lblExistingDate = new javax.swing.JLabel();
        cmbDateList = new javax.swing.JComboBox<>();
        btnSearchEntries = new javax.swing.JButton();
        pnlCreatedDate = new javax.swing.JPanel();
        lastModifiedLabel = new javax.swing.JLabel();
        lastModifiedStatusLabel = new javax.swing.JLabel();
        pnlOptionsAndNotes = new javax.swing.JPanel();
        diaryNotesLabel = new javax.swing.JLabel();
        diaryNotesScrollPane = new javax.swing.JScrollPane();
        txtDiaryNotes = new javax.swing.JTextArea();
        changeDateCheckBox = new javax.swing.JCheckBox();
        changeLocationCheckBox = new javax.swing.JCheckBox();
        cameraComboBox = new javax.swing.JComboBox<>();
        selectCameraLabel = new javax.swing.JLabel();
        setVideoDateAndTimeButton = new javax.swing.JButton();
        refreshNotesSideButton = new javax.swing.JButton();
        createNoteSideButton = new javax.swing.JButton();
        editNoteSideButton = new javax.swing.JButton();
        saveLocalNotesSideButton = new javax.swing.JButton();
        doNotUseLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlPanels.setMinimumSize(new java.awt.Dimension(500, 730));
        pnlPanels.setName(""); // NOI18N
        pnlPanels.setPreferredSize(new java.awt.Dimension(500, 730));

        notesScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notesScrollPane.setPreferredSize(new java.awt.Dimension(500, 54));

        txtLocalNotes.setColumns(20);
        txtLocalNotes.setLineWrap(true);
        txtLocalNotes.setRows(5);
        txtLocalNotes.setWrapStyleWord(true);
        txtLocalNotes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtLocalNotesKeyTyped(evt);
            }
        });
        notesScrollPane.setViewportView(txtLocalNotes);

        btnSaveLocalNotes.setText("Save Notes");
        btnSaveLocalNotes.setToolTipText("Save your notes");
        btnSaveLocalNotes.setMaximumSize(new java.awt.Dimension(120, 23));
        btnSaveLocalNotes.setMinimumSize(new java.awt.Dimension(120, 23));
        btnSaveLocalNotes.setPreferredSize(new java.awt.Dimension(120, 23));
        btnSaveLocalNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveLocalNotesActionPerformed(evt);
            }
        });

        btnDiary.setText("Daily Diary");
        btnDiary.setToolTipText("View your daily diary");
        btnDiary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiaryActionPerformed(evt);
            }
        });

        btnExpandPersonalNotes.setText("Expand Notes...");
        btnExpandPersonalNotes.setToolTipText("Send notes to an external file");
        btnExpandPersonalNotes.setMaximumSize(new java.awt.Dimension(120, 23));
        btnExpandPersonalNotes.setMinimumSize(new java.awt.Dimension(120, 23));
        btnExpandPersonalNotes.setPreferredSize(new java.awt.Dimension(120, 23));
        btnExpandPersonalNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandPersonalNotesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout personalNotePanelLayout = new javax.swing.GroupLayout(personalNotePanel);
        personalNotePanel.setLayout(personalNotePanelLayout);
        personalNotePanelLayout.setHorizontalGroup(
            personalNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSaveLocalNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnDiary, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnExpandPersonalNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        personalNotePanelLayout.setVerticalGroup(
            personalNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(personalNotePanelLayout.createSequentialGroup()
                .addComponent(btnSaveLocalNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnDiary)
                .addGap(5, 5, 5)
                .addComponent(btnExpandPersonalNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnlPersonalNotesLayout = new javax.swing.GroupLayout(pnlPersonalNotes);
        pnlPersonalNotes.setLayout(pnlPersonalNotesLayout);
        pnlPersonalNotesLayout.setHorizontalGroup(
            pnlPersonalNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPersonalNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(notesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(personalNotePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlPersonalNotesLayout.setVerticalGroup(
            pnlPersonalNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(notesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .addGroup(pnlPersonalNotesLayout.createSequentialGroup()
                        .addComponent(personalNotePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnlPanels.addTab("Personal Notes", pnlPersonalNotes);

        btnCreateNote.setIcon(IconProperties.getNewNoteIconImage());
        btnCreateNote.setText("Create Note");
        btnCreateNote.setToolTipText("Create a New Note");
        btnCreateNote.setIconTextGap(1);
        btnCreateNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateNoteActionPerformed(evt);
            }
        });

        btnEditNote.setIcon(IconProperties.getEditNoteIconImage());
        btnEditNote.setText("Modify Existing Note(s)");
        btnEditNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditNoteActionPerformed(evt);
            }
        });

        btnExpandNotes.setText("Expand Notes...");
        btnExpandNotes.setToolTipText("Open a seperate notes window with more options");
        btnExpandNotes.setPreferredSize(new java.awt.Dimension(100, 23));
        btnExpandNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandNotesActionPerformed(evt);
            }
        });

        btnRefreshNotes.setText("Refresh");
        btnRefreshNotes.setToolTipText("Refresh class notes.");
        btnRefreshNotes.setPreferredSize(new java.awt.Dimension(120, 23));
        btnRefreshNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshNotesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout classNotePanelLayout = new javax.swing.GroupLayout(classNotePanel);
        classNotePanel.setLayout(classNotePanelLayout);
        classNotePanelLayout.setHorizontalGroup(
            classNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCreateNote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnEditNote, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
            .addComponent(btnExpandNotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnRefreshNotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        classNotePanelLayout.setVerticalGroup(
            classNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(classNotePanelLayout.createSequentialGroup()
                .addComponent(btnCreateNote)
                .addGap(5, 5, 5)
                .addComponent(btnEditNote)
                .addGap(5, 5, 5)
                .addComponent(btnExpandNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnRefreshNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        noteTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        noteTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        noteScrollPane.setViewportView(noteTable);

        javax.swing.GroupLayout pnlClassNotesLayout = new javax.swing.GroupLayout(pnlClassNotes);
        pnlClassNotes.setLayout(pnlClassNotesLayout);
        pnlClassNotesLayout.setHorizontalGroup(
            pnlClassNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClassNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noteScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(classNotePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlClassNotesLayout.setVerticalGroup(
            pnlClassNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClassNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClassNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlClassNotesLayout.createSequentialGroup()
                        .addComponent(classNotePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(noteScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlPanels.addTab("Class Notes", pnlClassNotes);

        pnlDiary.setLayout(new java.awt.BorderLayout());

        pnlRelativeHumidity.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11), java.awt.Color.BLACK)); // NOI18N
        pnlRelativeHumidity.setMaximumSize(new java.awt.Dimension(200, 114));
        pnlRelativeHumidity.setMinimumSize(new java.awt.Dimension(200, 114));
        pnlRelativeHumidity.setPreferredSize(new java.awt.Dimension(276, 110));

        lblMaxRH.setText("Maximum:");

        lblRHRange.setText("Range:");

        fldMaxRH.setToolTipText("Maximum relative humidity");
        fldMaxRH.setPreferredSize(new java.awt.Dimension(38, 20));
        fldMaxRH.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fldMaxRHKeyReleased(evt);
            }
        });

        fldMinRH.setToolTipText("Minimum relative humidity");
        fldMinRH.setPreferredSize(new java.awt.Dimension(38, 20));
        fldMinRH.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fldMinRHKeyReleased(evt);
            }
        });

        fldRHRange.setEditable(false);
        fldRHRange.setToolTipText("Difference in relative humidy from maximum to minimum");
        fldRHRange.setPreferredSize(new java.awt.Dimension(38, 20));

        lblMinRHTime.setText("Time:");

        cmbMaxRHTime.setModel(GUIComponentFactory.initHourlyTimeComboBoxModel());
        cmbMaxRHTime.setToolTipText("Time at which maximum relative humidity occured");
        cmbMaxRHTime.setEnabled(false);

        cmbMinRHTime.setModel(GUIComponentFactory.initHourlyTimeComboBoxModel());
        cmbMinRHTime.setToolTipText("Time at which minimum relative humidity occured");
        cmbMinRHTime.setEnabled(false);

        lblMinRH.setText("Minimum:");

        lblRHTrend.setText("Trend:");

        cmbRHTrend.setModel(GUIComponentFactory.initRHTrendComboBoxModel());

        lblMaxRHTime.setText("Time:");

        javax.swing.GroupLayout pnlRelativeHumidityLayout = new javax.swing.GroupLayout(pnlRelativeHumidity);
        pnlRelativeHumidity.setLayout(pnlRelativeHumidityLayout);
        pnlRelativeHumidityLayout.setHorizontalGroup(
            pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRelativeHumidityLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRHTrend, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblMaxRH, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblMinRH, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblRHRange, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRelativeHumidityLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(cmbRHTrend, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlRelativeHumidityLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(fldMaxRH, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                            .addComponent(fldMinRH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fldRHRange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRelativeHumidityLayout.createSequentialGroup()
                                .addComponent(lblMinRHTime)
                                .addGap(18, 18, 18)
                                .addComponent(cmbMinRHTime, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRelativeHumidityLayout.createSequentialGroup()
                                .addComponent(lblMaxRHTime)
                                .addGap(18, 18, 18)
                                .addComponent(cmbMaxRHTime, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        pnlRelativeHumidityLayout.setVerticalGroup(
            pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRelativeHumidityLayout.createSequentialGroup()
                .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbRHTrend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRHTrend, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbMaxRHTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fldMaxRH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblMaxRHTime)
                        .addComponent(lblMaxRH)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbMinRHTime)
                    .addGroup(pnlRelativeHumidityLayout.createSequentialGroup()
                        .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblMinRH)
                                .addComponent(lblMinRHTime))
                            .addComponent(fldMinRH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRelativeHumidityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fldRHRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRHRange))
                .addContainerGap())
        );

        pnlCloudTypes.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11), java.awt.Color.BLACK)); // NOI18N

        lblMorningClouds.setText("Morning (6am - noon):");

        lblAfternoonClouds.setText("Afternoon (noon - 6 pm):");

        lblNightClouds.setText("Evening / Night (6pm - 6am):");

        cmbPrimaryNightClouds.setModel(GUIComponentFactory.initCloudComboBoxModel());
        cmbPrimaryNightClouds.setToolTipText("Primary evening sky conditions");

        cmbPrimaryAfternoonClouds.setModel(GUIComponentFactory.initCloudComboBoxModel());
        cmbPrimaryAfternoonClouds.setToolTipText("Primary afternoon sky conditions");

        cmbPrimaryMorningClouds.setModel(GUIComponentFactory.initCloudComboBoxModel());
        cmbPrimaryMorningClouds.setToolTipText("Primary morning sky conditions");

        cmbSecondaryMorningClouds.setModel(GUIComponentFactory.initCloudComboBoxModel());
        cmbSecondaryMorningClouds.setToolTipText("Secondary morning sky conditions");

        cmbSecondaryAfternoonClouds.setModel(GUIComponentFactory.initCloudComboBoxModel());
        cmbSecondaryAfternoonClouds.setToolTipText("Secondary afternoon sky conditions");

        cmbSecondaryNightClouds.setModel(GUIComponentFactory.initCloudComboBoxModel());
        cmbSecondaryNightClouds.setToolTipText("Secondary evening sky conditions");

        lblPrimary.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPrimary.setText("Primary:");

        lblSecondary.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSecondary.setText("Secondary:");

        javax.swing.GroupLayout pnlCloudTypesLayout = new javax.swing.GroupLayout(pnlCloudTypes);
        pnlCloudTypes.setLayout(pnlCloudTypesLayout);
        pnlCloudTypesLayout.setHorizontalGroup(
            pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCloudTypesLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAfternoonClouds)
                    .addComponent(lblMorningClouds)
                    .addComponent(lblNightClouds))
                .addGap(18, 18, 18)
                .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCloudTypesLayout.createSequentialGroup()
                        .addComponent(lblPrimary)
                        .addGap(88, 88, 88)
                        .addComponent(lblSecondary))
                    .addGroup(pnlCloudTypesLayout.createSequentialGroup()
                        .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbPrimaryMorningClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPrimaryAfternoonClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPrimaryNightClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbSecondaryMorningClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbSecondaryAfternoonClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbSecondaryNightClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(166, Short.MAX_VALUE))
        );
        pnlCloudTypesLayout.setVerticalGroup(
            pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCloudTypesLayout.createSequentialGroup()
                .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrimary)
                    .addComponent(lblSecondary))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlCloudTypesLayout.createSequentialGroup()
                        .addComponent(cmbSecondaryMorningClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSecondaryAfternoonClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSecondaryNightClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlCloudTypesLayout.createSequentialGroup()
                        .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbPrimaryMorningClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMorningClouds))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbPrimaryAfternoonClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblAfternoonClouds))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCloudTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbPrimaryNightClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNightClouds)))))
        );

        temperatureTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        temperatureTitle.setText("1. Temperature (°F):");

        temperatureLink.setText("<html><a href=\"\">Link</html>");
        temperatureLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        temperatureLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                temperatureLinkMouseClicked(evt);
            }
        });

        barometricPressureTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        barometricPressureTitle.setText("2. Barometric Pressure (mb):");

        barometricPressureLink.setText("<html><a href=\"\">Link</a></html>");
        barometricPressureLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        barometricPressureLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                barometricPressureLinkMouseClicked(evt);
            }
        });

        dewPointTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        dewPointTitle.setText("3. Dew Point (°F):");

        dewPointLink.setText("<html><a href=\"\">Link</a></html>");
        dewPointLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dewPointLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dewPointLinkMouseClicked(evt);
            }
        });

        relativeHumidityTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        relativeHumidityTitle.setText("4. Relative Humidity (%):");

        relativeHumidityLink.setText("<html><a href=\"\">Link</a></html>");
        relativeHumidityLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        relativeHumidityLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                relativeHumidityLinkMouseClicked(evt);
            }
        });

        cloudsTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cloudsTitle.setText("5. Sky Conditions / Cloud Types:");

        cloudsLink.setText("<html><a href=\"\">Link</a></html>");
        cloudsLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cloudsLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cloudsLinkMouseClicked(evt);
            }
        });

        pnlTemperature.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), java.awt.Color.BLACK)); // NOI18N
        pnlTemperature.setMaximumSize(new java.awt.Dimension(200, 114));
        pnlTemperature.setMinimumSize(new java.awt.Dimension(200, 114));
        pnlTemperature.setPreferredSize(new java.awt.Dimension(276, 110));

        lblMaxTemp.setText("Maximum:");

        lblMinTemp.setText("Minimum:");

        lblTempRange.setText("Range:");

        fldMaxTemp.setToolTipText("Maximum temperature");
        fldMaxTemp.setPreferredSize(new java.awt.Dimension(38, 20));
        fldMaxTemp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fldMaxTempKeyReleased(evt);
            }
        });

        fldMinTemp.setToolTipText("Minimum temperature");
        fldMinTemp.setPreferredSize(new java.awt.Dimension(38, 20));
        fldMinTemp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fldMinTempKeyReleased(evt);
            }
        });

        fldTempRange.setEditable(false);
        fldTempRange.setToolTipText("Difference in temperature from maximum and minimum");
        fldTempRange.setPreferredSize(new java.awt.Dimension(38, 20));

        lblMaxTempTime.setText("Time:");

        lblMinTempTime.setText("Time:");

        cmbMaxTempTime.setModel(GUIComponentFactory.initHourlyTimeComboBoxModel());
        cmbMaxTempTime.setToolTipText("Time at which maximum temperature occured");
        cmbMaxTempTime.setEnabled(false);

        cmbMinTempTime.setModel(GUIComponentFactory.initHourlyTimeComboBoxModel());
        cmbMinTempTime.setToolTipText("Time at which minimum temperature occured");
        cmbMinTempTime.setEnabled(false);

        lblTempTrend.setText("Trend:");

        cmbTempTrend.setModel(GUIComponentFactory.initTempTrendComboBoxModel());

        javax.swing.GroupLayout pnlTemperatureLayout = new javax.swing.GroupLayout(pnlTemperature);
        pnlTemperature.setLayout(pnlTemperatureLayout);
        pnlTemperatureLayout.setHorizontalGroup(
            pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTemperatureLayout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblMaxTemp)
                        .addComponent(lblMinTemp, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblTempRange, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(lblTempTrend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTemperatureLayout.createSequentialGroup()
                        .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fldTempRange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fldMinTemp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fldMaxTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlTemperatureLayout.createSequentialGroup()
                                .addComponent(lblMaxTempTime)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbMaxTempTime, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlTemperatureLayout.createSequentialGroup()
                                .addComponent(lblMinTempTime)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbMinTempTime, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(cmbTempTrend, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlTemperatureLayout.setVerticalGroup(
            pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTemperatureLayout.createSequentialGroup()
                .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTempTrend)
                    .addComponent(cmbTempTrend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTemperatureLayout.createSequentialGroup()
                        .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaxTemp)
                            .addComponent(fldMaxTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMinTemp)
                            .addComponent(fldMinTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTempRange)
                            .addComponent(fldTempRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlTemperatureLayout.createSequentialGroup()
                        .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaxTempTime)
                            .addComponent(cmbMaxTempTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlTemperatureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbMinTempTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMinTempTime))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBarometricPressure.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11), java.awt.Color.BLACK)); // NOI18N
        pnlBarometricPressure.setMaximumSize(new java.awt.Dimension(200, 114));
        pnlBarometricPressure.setMinimumSize(new java.awt.Dimension(200, 114));
        pnlBarometricPressure.setPreferredSize(new java.awt.Dimension(276, 110));

        lblStartBP.setText("Start Of Day:");

        lblBPTrend.setText("Trend:");

        fldStartBP.setToolTipText("Barometric pressure at beginning of day");
        fldStartBP.setPreferredSize(new java.awt.Dimension(38, 20));
        fldStartBP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fldStartBPKeyReleased(evt);
            }
        });

        fldEndBP.setToolTipText("Barometric pressure at end of day");
        fldEndBP.setPreferredSize(new java.awt.Dimension(38, 20));
        fldEndBP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fldEndBPKeyReleased(evt);
            }
        });

        lblMaxBPTime.setText("Time:");

        lblMinBPTime.setText("Time:");

        cmbEndBPTime.setModel(GUIComponentFactory.initHourlyTimeComboBoxModel());
        cmbEndBPTime.setToolTipText("Time at which ending barometric pressure occured");
        cmbEndBPTime.setEnabled(false);

        cmbStartBPTime.setModel(GUIComponentFactory.initHourlyTimeComboBoxModel());
        cmbStartBPTime.setToolTipText("Time at which beginning barometric pressure occured");
        cmbStartBPTime.setEnabled(false);

        cmbBPTrend.setModel(GUIComponentFactory.initBarometricPressureTrendComboBoxModel());
        cmbBPTrend.setToolTipText("Barometric pressure trend");

        lblEndBP.setText("End Of Day:");

        lblBPRange.setText("Range:");

        fldBPRange.setEditable(false);
        fldBPRange.setToolTipText("Difference in barometric pressure from maximum to minimum");
        fldBPRange.setPreferredSize(new java.awt.Dimension(38, 20));

        javax.swing.GroupLayout pnlBarometricPressureLayout = new javax.swing.GroupLayout(pnlBarometricPressure);
        pnlBarometricPressure.setLayout(pnlBarometricPressureLayout);
        pnlBarometricPressureLayout.setHorizontalGroup(
            pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBarometricPressureLayout.createSequentialGroup()
                .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBarometricPressureLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblEndBP))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBarometricPressureLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblStartBP, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblBPTrend, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblBPRange, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlBarometricPressureLayout.createSequentialGroup()
                        .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fldBPRange, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fldEndBP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fldStartBP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlBarometricPressureLayout.createSequentialGroup()
                                .addComponent(lblMaxBPTime)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbEndBPTime, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlBarometricPressureLayout.createSequentialGroup()
                                .addComponent(lblMinBPTime)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbStartBPTime, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(cmbBPTrend, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
        );
        pnlBarometricPressureLayout.setVerticalGroup(
            pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBarometricPressureLayout.createSequentialGroup()
                .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbBPTrend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBPTrend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBarometricPressureLayout.createSequentialGroup()
                        .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMinBPTime)
                            .addComponent(cmbStartBPTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fldStartBP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStartBP))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblEndBP))
                    .addGroup(pnlBarometricPressureLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaxBPTime)
                            .addComponent(cmbEndBPTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fldEndBP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBarometricPressureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBPRange)
                    .addComponent(fldBPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlDewPoint.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11), java.awt.Color.BLACK)); // NOI18N
        pnlDewPoint.setMaximumSize(new java.awt.Dimension(200, 114));
        pnlDewPoint.setMinimumSize(new java.awt.Dimension(200, 114));
        pnlDewPoint.setPreferredSize(new java.awt.Dimension(276, 110));

        lblStartDP.setText("Start Of Day:");

        lblDPTrend.setText("Trend:");

        fldStartDP.setToolTipText("Dew point at beginning of day");
        fldStartDP.setPreferredSize(new java.awt.Dimension(38, 20));
        fldStartDP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fldStartDPKeyReleased(evt);
            }
        });

        fldEndDP.setToolTipText("Dew point at end of day");
        fldEndDP.setPreferredSize(new java.awt.Dimension(38, 20));
        fldEndDP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fldEndDPKeyReleased(evt);
            }
        });

        lblMaxDPTime.setText("Time:");

        lblMinDPTime.setText("Time:");

        cmbEndDPTime.setModel(GUIComponentFactory.initHourlyTimeComboBoxModel());
        cmbEndDPTime.setToolTipText("Time at which ending dew point occured");
        cmbEndDPTime.setEnabled(false);

        cmbStartDPTime.setModel(GUIComponentFactory.initHourlyTimeComboBoxModel());
        cmbStartDPTime.setToolTipText("Time at which beginning dew point occured");
        cmbStartDPTime.setEnabled(false);

        cmbDPTrend.setModel(GUIComponentFactory.initBarometricPressureTrendComboBoxModel());
        cmbDPTrend.setToolTipText("Barometric pressure trend");

        lblEndDP.setText("End Of Day:");

        lblDPRange.setText("Range:");

        fldDPRange.setEditable(false);
        fldDPRange.setToolTipText("Difference in barometric pressure from maximum to minimum");
        fldDPRange.setPreferredSize(new java.awt.Dimension(38, 20));

        javax.swing.GroupLayout pnlDewPointLayout = new javax.swing.GroupLayout(pnlDewPoint);
        pnlDewPoint.setLayout(pnlDewPointLayout);
        pnlDewPointLayout.setHorizontalGroup(
            pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDewPointLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblStartDP)
                    .addComponent(lblEndDP)
                    .addComponent(lblDPTrend)
                    .addComponent(lblDPRange))
                .addGap(18, 18, 18)
                .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDewPointLayout.createSequentialGroup()
                        .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fldDPRange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fldStartDP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fldEndDP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMinDPTime)
                            .addComponent(lblMaxDPTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbStartDPTime, 0, 85, Short.MAX_VALUE)
                            .addComponent(cmbEndDPTime, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(cmbDPTrend, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlDewPointLayout.setVerticalGroup(
            pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDewPointLayout.createSequentialGroup()
                .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbDPTrend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDPTrend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMinDPTime)
                    .addComponent(cmbStartDPTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fldStartDP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStartDP, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDewPointLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fldEndDP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEndDP, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fldDPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDPRange, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(pnlDewPointLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDewPointLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaxDPTime)
                            .addComponent(cmbEndDPTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout daily_diary_Part1_JPanelLayout = new javax.swing.GroupLayout(daily_diary_Part1_JPanel);
        daily_diary_Part1_JPanel.setLayout(daily_diary_Part1_JPanelLayout);
        daily_diary_Part1_JPanelLayout.setHorizontalGroup(
            daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, daily_diary_Part1_JPanelLayout.createSequentialGroup()
                        .addComponent(pnlCloudTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))
                    .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                        .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(cloudsTitle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cloudsLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                                .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                                        .addComponent(temperatureTitle)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(temperatureLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                                        .addComponent(dewPointTitle)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dewPointLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(pnlTemperature, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pnlDewPoint, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                                        .addComponent(relativeHumidityTitle)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(relativeHumidityLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                                        .addComponent(barometricPressureTitle)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(barometricPressureLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(pnlBarometricPressure, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                                        .addComponent(pnlRelativeHumidity, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(294, 294, 294))
        );
        daily_diary_Part1_JPanelLayout.setVerticalGroup(
            daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(daily_diary_Part1_JPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(temperatureTitle)
                        .addComponent(temperatureLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(barometricPressureTitle)
                    .addComponent(barometricPressureLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlBarometricPressure, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                    .addComponent(pnlTemperature, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dewPointLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(relativeHumidityTitle)
                        .addComponent(relativeHumidityLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dewPointTitle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRelativeHumidity, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(pnlDewPoint, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(daily_diary_Part1_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cloudsTitle)
                    .addComponent(cloudsLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCloudTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(271, 271, 271))
        );

        diaryTabbedPane.addTab("Variables 1", daily_diary_Part1_JPanel);

        pnlWind.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11), java.awt.Color.BLACK)); // NOI18N

        pnlCompass.setPreferredSize(new java.awt.Dimension(172, 172));
        pnlCompass.setLayout(new java.awt.GridBagLayout());

        lblSouth.setText("S");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        pnlCompass.add(lblSouth, gridBagConstraints);

        lblEast.setText("E");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(lblEast, gridBagConstraints);

        lblWest.setText("W");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(lblWest, gridBagConstraints);

        lblNorth.setText("N");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        pnlCompass.add(lblNorth, gridBagConstraints);

        checkNorth.setToolTipText("North");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(checkNorth, gridBagConstraints);

        checkNorthWest.setToolTipText("North West");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(checkNorthWest, gridBagConstraints);

        checkNorthEast.setToolTipText("North East");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(checkNorthEast, gridBagConstraints);

        checkWest.setToolTipText("West");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(checkWest, gridBagConstraints);

        checkCalm.setToolTipText("Calm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(checkCalm, gridBagConstraints);

        checkEast.setToolTipText("East");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(checkEast, gridBagConstraints);

        checkSouthWest.setToolTipText("South West");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        pnlCompass.add(checkSouthWest, gridBagConstraints);

        checkSouthEast.setToolTipText("South East");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        pnlCompass.add(checkSouthEast, gridBagConstraints);

        checkSouth.setToolTipText("South");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        pnlCompass.add(checkSouth, gridBagConstraints);

        lblWindSpeed.setText("General Wind Speed Characteristics:");

        lblMaxGustSpeed.setText("Max Gust Speed (mph):");

        cmbWindSpeed.setModel(GUIComponentFactory.initWindSpeedComboBoxModel());
        cmbWindSpeed.setToolTipText("Average wind speed of the date");

        fldMaxGustSpeed.setToolTipText("Maximum gust speed");

        lblWindPanelHeader.setText("Please select all prominent wind directions during the 24 hour period.");

        lblWindDirectionSummary.setText("From early morning through nightfall, the");

        cmbWindDirectionSummary.setModel(GUIComponentFactory.initWindDirectionSummaryComboBoxModel());
        cmbWindDirectionSummary.setToolTipText("Summary of wind direction for the day.");

        javax.swing.GroupLayout pnlWindLayout = new javax.swing.GroupLayout(pnlWind);
        pnlWind.setLayout(pnlWindLayout);
        pnlWindLayout.setHorizontalGroup(
            pnlWindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWindLayout.createSequentialGroup()
                .addContainerGap(130, Short.MAX_VALUE)
                .addGroup(pnlWindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlWindLayout.createSequentialGroup()
                        .addComponent(pnlCompass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addGroup(pnlWindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlWindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cmbWindSpeed, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblWindSpeed)
                                .addGroup(pnlWindLayout.createSequentialGroup()
                                    .addComponent(lblMaxGustSpeed)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fldMaxGustSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlWindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblWindDirectionSummary)
                                .addComponent(cmbWindDirectionSummary, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(82, 82, 82))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlWindLayout.createSequentialGroup()
                        .addComponent(lblWindPanelHeader)
                        .addGap(126, 126, 126))))
        );
        pnlWindLayout.setVerticalGroup(
            pnlWindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlWindLayout.createSequentialGroup()
                .addComponent(lblWindPanelHeader)
                .addGroup(pnlWindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWindLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 19, Short.MAX_VALUE)
                        .addComponent(lblWindDirectionSummary)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbWindDirectionSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblWindSpeed)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbWindSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlWindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaxGustSpeed)
                            .addComponent(fldMaxGustSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(pnlWindLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlCompass, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
        );

        pnlAdditionalVariables.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11), java.awt.Color.BLACK)); // NOI18N

        lblDailyPrecip.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDailyPrecip.setText("7. Daily Precipitation (in-liquid):");

        lblMaxHI.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMaxHI.setText("8. Maximum Heat Index (°F):");
        lblMaxHI.setToolTipText("Maximum heat index");

        lblWindDirectionUp.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblWindDirectionUp.setText("10. Wind Direction (upper air):");

        fldDailyPrecipitation.setToolTipText("Daily precipitation");

        fldMaxHeatIndex.setToolTipText("Maximum heat index");

        fldMinWindChill.setToolTipText("Minimum heat index");

        lblMinWindChill.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMinWindChill.setText("9. Minimum Departure from Normal Due to Wind Chill (°F):");

        precipitationLink.setText("<html><a href=\"\">Link</a></html>");
        precipitationLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        precipitationLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                precipitationLinkMouseClicked(evt);
            }
        });

        heatIndexLink.setText("<html><a href=\"\">Link</a></html>");
        heatIndexLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        heatIndexLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                heatIndexLinkMouseClicked(evt);
            }
        });

        windChillLink.setText("<html><a href=\"\">Link</a></html>");
        windChillLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        windChillLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                windChillLinkMouseClicked(evt);
            }
        });

        windDirectionLink.setText("<html><a href=\"\">Link</a></html>");
        windDirectionLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        windDirectionLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                windDirectionLinkMouseClicked(evt);
            }
        });

        cmbUpperAirWindDirection.setModel(GUIComponentFactory.initWindDirectionComboBoxModel());
        cmbUpperAirWindDirection.setToolTipText("Upper air wind direction");

        javax.swing.GroupLayout pnlAdditionalVariablesLayout = new javax.swing.GroupLayout(pnlAdditionalVariables);
        pnlAdditionalVariables.setLayout(pnlAdditionalVariablesLayout);
        pnlAdditionalVariablesLayout.setHorizontalGroup(
            pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                        .addComponent(lblDailyPrecip)
                        .addGap(10, 10, 10)
                        .addComponent(precipitationLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(155, 155, 155)
                        .addComponent(fldDailyPrecipitation, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                        .addComponent(lblMaxHI, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(heatIndexLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(159, 159, 159)
                        .addComponent(fldMaxHeatIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                        .addComponent(lblMinWindChill)
                        .addGap(6, 6, 6)
                        .addComponent(windChillLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(fldMinWindChill, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                        .addComponent(lblWindDirectionUp, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(windDirectionLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(159, 159, 159)
                        .addComponent(cmbUpperAirWindDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        pnlAdditionalVariablesLayout.setVerticalGroup(
            pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fldDailyPrecipitation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDailyPrecip)
                            .addComponent(precipitationLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fldMaxHeatIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMaxHI)
                            .addComponent(heatIndexLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fldMinWindChill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMinWindChill)
                            .addComponent(windChillLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbUpperAirWindDirection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAdditionalVariablesLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pnlAdditionalVariablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblWindDirectionUp)
                            .addComponent(windDirectionLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        cmbUpperAirWindDirection.getAccessibleContext().setAccessibleName("");

        windSpeedAndDirectionTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        windSpeedAndDirectionTitle.setText("6. Wind Directions (Surface):");

        windSpeedAndDirectionLink.setText("<html><a href=\"\">Link</a></html>");
        windSpeedAndDirectionLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        windSpeedAndDirectionLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                windSpeedAndDirectionLinkMouseClicked(evt);
            }
        });

        additionalVariablesTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        additionalVariablesTitle.setText("ADDITIONAL VARIABLES:");

        javax.swing.GroupLayout daily_Diary_Part2_JPanelLayout = new javax.swing.GroupLayout(daily_Diary_Part2_JPanel);
        daily_Diary_Part2_JPanel.setLayout(daily_Diary_Part2_JPanelLayout);
        daily_Diary_Part2_JPanelLayout.setHorizontalGroup(
            daily_Diary_Part2_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(daily_Diary_Part2_JPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(daily_Diary_Part2_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlAdditionalVariables, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlWind, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(daily_Diary_Part2_JPanelLayout.createSequentialGroup()
                        .addComponent(windSpeedAndDirectionTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(windSpeedAndDirectionLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(additionalVariablesTitle))
                .addContainerGap())
        );
        daily_Diary_Part2_JPanelLayout.setVerticalGroup(
            daily_Diary_Part2_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(daily_Diary_Part2_JPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(daily_Diary_Part2_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(windSpeedAndDirectionTitle)
                    .addComponent(windSpeedAndDirectionLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlWind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(additionalVariablesTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAdditionalVariables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(358, Short.MAX_VALUE))
        );

        diaryTabbedPane.addTab("Variables 2", daily_Diary_Part2_JPanel);

        lblTemperatureProfile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTemperatureProfile.setText("Temperature Profile");
        lblTemperatureProfile.setEnabled(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblTempProfileQ1.setText("Does the daily temperature profile conform to the typical diurnal pattern? ");
        lblTempProfileQ1.setEnabled(false);

        cmbTempProfileQ1.setModel(GUIComponentFactory.initYesNoComboBoxModel());
        cmbTempProfileQ1.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(lblTempProfileQ1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTempProfileQ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(129, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTempProfileQ1)
                    .addComponent(cmbTempProfileQ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTemperatureProfile)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lblTemperatureProfile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        lblDailyPressureChange.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDailyPressureChange.setText("Daily Pressure Change");
        lblDailyPressureChange.setEnabled(false);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblDailyPressureQ1.setText("How does the daily pressure relate to the standard atmospheric pressure (1013.25 mb)?");
        lblDailyPressureQ1.setEnabled(false);

        cmbDailyPressureQ1.setModel(GUIComponentFactory.initPressureChangeComboBoxModel());
        cmbDailyPressureQ1.setEnabled(false);

        lblDailyPressureQ2.setText("Based on the pressure during the day, predict tomorrow's weather conditions.");
        lblDailyPressureQ2.setEnabled(false);

        cmbDailyPressureQ2.setModel(GUIComponentFactory.initPrecipitationChanceComboBoxModel());
        cmbDailyPressureQ2.setEnabled(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(lblDailyPressureQ1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbDailyPressureQ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(lblDailyPressureQ2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbDailyPressureQ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDailyPressureQ1)
                    .addComponent(cmbDailyPressureQ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDailyPressureQ2)
                    .addComponent(cmbDailyPressureQ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDailyPressureChange)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(lblDailyPressureChange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblDailyHumidityProfile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDailyHumidityProfile.setText("Daily Humidity Profile");
        lblDailyHumidityProfile.setEnabled(false);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblDailyHumidityQ1.setText("Does the daily humidity profile conform to daily temperature variations?");
        lblDailyHumidityQ1.setEnabled(false);

        cmbDailyHumidityQ1.setModel(GUIComponentFactory.initYesNoComboBoxModel());
        cmbDailyHumidityQ1.setEnabled(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(lblDailyHumidityQ1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbDailyHumidityQ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(147, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblDailyHumidityQ1)
                .addComponent(cmbDailyHumidityQ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDailyHumidityProfile)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(lblDailyHumidityProfile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        lblPredictions.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPredictions.setText("Predictions");
        lblPredictions.setEnabled(false);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblPredictionsQ1.setText("Predict tomorrow's temperature in relation to today.");
        lblPredictionsQ1.setEnabled(false);

        cmbPredictionsQ1.setModel(GUIComponentFactory.initTemperaturePredictionComboBoxModel());
        cmbPredictionsQ1.setEnabled(false);

        lblPredictionsQ2.setText("Can we expect precipitation tomorrow?");
        lblPredictionsQ2.setEnabled(false);

        cmbPredictionsQ2.setModel(GUIComponentFactory.initYesNoComboBoxModel());
        cmbPredictionsQ2.setEnabled(false);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lblPredictionsQ1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbPredictionsQ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lblPredictionsQ2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbPredictionsQ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(238, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPredictionsQ1)
                    .addComponent(cmbPredictionsQ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPredictionsQ2)
                    .addComponent(cmbPredictionsQ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPredictions)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(lblPredictions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(358, Short.MAX_VALUE))
        );

        diaryTabbedPane.addTab("Questions", jPanel2);

        pnlDiary.add(diaryTabbedPane, java.awt.BorderLayout.PAGE_START);

        pnlPanels.addTab("Daily Diary", pnlDiary);

        add(pnlPanels, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 531));

        pnlButtons.setLayout(new java.awt.GridLayout(1, 0));

        btnSaveEntry.setText("Save This Entry");
        btnSaveEntry.setToolTipText("Saves the Daily Diary entry");
        btnSaveEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveEntryActionPerformed(evt);
            }
        });
        pnlButtons.add(btnSaveEntry);

        btnExportEntry.setText("Export Daily Diary");
        btnExportEntry.setToolTipText("Export Daily Diary to different formats");
        btnExportEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportEntryActionPerformed(evt);
            }
        });
        pnlButtons.add(btnExportEntry);

        btnSaveAndClose.setText("Save And Close");
        btnSaveAndClose.setToolTipText("Saves diary entry and closes the window");
        btnSaveAndClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAndCloseActionPerformed(evt);
            }
        });
        pnlButtons.add(btnSaveAndClose);

        btnRemove.setText("Delete This Entry");
        btnRemove.setToolTipText("Delete entire entry for this date");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        pnlButtons.add(btnRemove);

        expandCheckBox.setText("Expand");
        expandCheckBox.setEnabled(false);
        expandCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandCheckBoxActionPerformed(evt);
            }
        });
        pnlButtons.add(expandCheckBox);

        add(pnlButtons, new org.netbeans.lib.awtextra.AbsoluteConstraints(945, 194, 680, -1));

        pnlHeader.setMaximumSize(new java.awt.Dimension(342, 42));
        pnlHeader.setMinimumSize(new java.awt.Dimension(342, 42));
        pnlHeader.setPreferredSize(new java.awt.Dimension(933, 90));
        pnlHeader.setLayout(new java.awt.GridLayout(1, 0, 15, 0));

        pnlDate.setLayout(new java.awt.GridLayout(0, 1));

        lblDateExplanation.setText("<html><center>Enter your observations below to record a daily diary entry for</center></html>");
        lblDateExplanation.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        pnlDate.add(lblDateExplanation);

        lblDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlDate.add(lblDate);

        lblGapFill1.setToolTipText("");
        pnlDate.add(lblGapFill1);

        pnlHeader.add(pnlDate);

        pnlNewDate.setLayout(new java.awt.GridLayout(0, 1));

        lblNewDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNewDate.setText("Make New Entry");
        pnlNewDate.add(lblNewDate);

        btnNewEntry.setText("Pick New Date");
        btnNewEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewEntryActionPerformed(evt);
            }
        });
        pnlNewDate.add(btnNewEntry);
        pnlNewDate.add(lblGapFill2);

        pnlHeader.add(pnlNewDate);

        pnlExistintngDate.setLayout(new java.awt.GridLayout(0, 1));

        lblExistingDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblExistingDate.setText("Select Existing Entry");
        pnlExistintngDate.add(lblExistingDate);

        cmbDateList.setToolTipText("Select from among the most recent data");
        cmbDateList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDateListActionPerformed(evt);
            }
        });
        cmbDateList.setRenderer(new DateListCellRenderer());
        pnlExistintngDate.add(cmbDateList);

        btnSearchEntries.setText("Search For Another Day");
        btnSearchEntries.setToolTipText("Select from among all data");
        btnSearchEntries.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSearchEntries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchEntriesActionPerformed(evt);
            }
        });
        pnlExistintngDate.add(btnSearchEntries);

        pnlHeader.add(pnlExistintngDate);

        pnlCreatedDate.setLayout(new java.awt.GridLayout(0, 1));

        lastModifiedLabel.setText("Date Last Modified:");
        lastModifiedLabel.setToolTipText("");
        pnlCreatedDate.add(lastModifiedLabel);

        lastModifiedStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lastModifiedStatusLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlCreatedDate.add(lastModifiedStatusLabel);

        pnlHeader.add(pnlCreatedDate);

        add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(945, 341, -1, -1));

        pnlOptionsAndNotes.setPreferredSize(new java.awt.Dimension(250, 138));
        pnlOptionsAndNotes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        diaryNotesLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        diaryNotesLabel.setText("Notes:");
        pnlOptionsAndNotes.add(diaryNotesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 143, 320, 16));

        txtDiaryNotes.setColumns(20);
        txtDiaryNotes.setLineWrap(true);
        txtDiaryNotes.setRows(5);
        txtDiaryNotes.setToolTipText("Additional notes");
        txtDiaryNotes.setWrapStyleWord(true);
        txtDiaryNotes.setPreferredSize(new java.awt.Dimension(200, 94));
        diaryNotesScrollPane.setViewportView(txtDiaryNotes);

        pnlOptionsAndNotes.add(diaryNotesScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 165, 320, 366));

        changeDateCheckBox.setText("Change Main Display When Date Is Changed");
        pnlOptionsAndNotes.add(changeDateCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 81, 320, 25));

        changeLocationCheckBox.setText("Change Main Display When Location Is Changed");
        pnlOptionsAndNotes.add(changeLocationCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 320, 25));

        cameraComboBox.setToolTipText("Select location via weather camera.");
        cameraComboBox.setLightWeightPopupEnabled(false);
        cameraComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraComboBoxActionPerformed(evt);
            }
        });
        pnlOptionsAndNotes.add(cameraComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 320, 22));

        selectCameraLabel.setText("Select location via weather camera.:");
        pnlOptionsAndNotes.add(selectCameraLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 320, 16));

        setVideoDateAndTimeButton.setText("Set Program to Diary Date and Location");
        setVideoDateAndTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setVideoDateAndTimeButtonActionPerformed(evt);
            }
        });
        pnlOptionsAndNotes.add(setVideoDateAndTimeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 112, 320, 25));

        refreshNotesSideButton.setText("Refresh");
        refreshNotesSideButton.setToolTipText("Refresh class notes.");
        refreshNotesSideButton.setPreferredSize(new java.awt.Dimension(120, 23));
        refreshNotesSideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshNotesSideButtonActionPerformed(evt);
            }
        });
        pnlOptionsAndNotes.add(refreshNotesSideButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 143, 320, 25));

        createNoteSideButton.setIcon(IconProperties.getNewNoteIconImage());
        createNoteSideButton.setText("Create Note");
        createNoteSideButton.setToolTipText("Create a New Note");
        createNoteSideButton.setIconTextGap(1);
        createNoteSideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNoteSideButtonActionPerformed(evt);
            }
        });
        pnlOptionsAndNotes.add(createNoteSideButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 143, 320, 25));

        editNoteSideButton.setIcon(IconProperties.getEditNoteIconImage());
        editNoteSideButton.setText("Modify Existing Note(s)");
        editNoteSideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editNoteSideButtonActionPerformed(evt);
            }
        });
        pnlOptionsAndNotes.add(editNoteSideButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 174, 320, 25));

        saveLocalNotesSideButton.setText("Save Notes");
        saveLocalNotesSideButton.setToolTipText("Save your notes");
        saveLocalNotesSideButton.setMaximumSize(new java.awt.Dimension(120, 23));
        saveLocalNotesSideButton.setMinimumSize(new java.awt.Dimension(120, 23));
        saveLocalNotesSideButton.setPreferredSize(new java.awt.Dimension(120, 23));
        saveLocalNotesSideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLocalNotesSideButtonActionPerformed(evt);
            }
        });
        pnlOptionsAndNotes.add(saveLocalNotesSideButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 143, 320, 25));

        add(pnlOptionsAndNotes, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 550, 320, 531));

        doNotUseLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        doNotUseLabel.setText("Main panel for designing only - do not use..");
        add(doNotUseLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 60, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void expandCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandCheckBoxActionPerformed
        // TODO: add your handling code here:
    }//GEN-LAST:event_expandCheckBoxActionPerformed

    /**
     * Function to delete the current diary entry.
     */
    private void deleteCurrentEntry() {
        boolean success = DiaryManager.removeCurrentEntry();
        if (!success) {
            showErrorPane("Unable to delete this entry.", "Deletion Error");
        }
    }

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        if (arePrivateClassNotesSaved) {
            //No deletion is possible if there are only personal class notes
            //for this day.
            if (!isDiaryEntrySaved) {
                showInfoPane("You cannot delete an entry with only personl\n"
                        + "class notes.", "Deletion Not Allowed");
                return;
            }
            
            //Hande the case where private class notes are saved for this day 
            //and will not be deleted leaving diary on this day.
            String message = "This will not delete your personal class notes.\n"
                    + "Do you wish to detete this entry anyway.";
            if (!askUserQuestion(message, "Remove Entry")) {
                return;
            }

            diarySaved = true;  //Any unsaved changes won't be saved.
            
            //Clear diary.
            deleteCurrentEntry();
            
            //Update diary fields.
            setDiaryFieldsAndFlags();
        } else {
            //Make sure entry has been saved.
            if (!isDiaryEntrySaved) {
                showInfoPane("You cannot delete an entry that is not saved.",
                        "Deletion Not Allowed");
                return;
            }
            
            if (!askUserQuestion("Are you sure "
                    + "you wish to remove this entry?", "Remove Entry")) {
                return;
            }

            Date removeDate = (Date) cmbDateList.getSelectedItem();
            diarySaved = true;  //Any unsaved changes won't be saved.

            //If the list size is 1, leave the manager on a black entry for 
            //today.
            if (cmbDateList.getItemCount() == 1) {
                Date startOfToday = ResourceTimeManager.
                        getStartOfDayDateFromMilliseconds(System
                                .currentTimeMillis(), DiaryManager.getResource()
                                .getTimeZone().getTimeZone());
                
                //Place a hold on changing the main window's date if the entry
                //is for today.
                if (removeDate.getTime() == startOfToday.getTime()) {
                    this.holdChangeMainWindowTimeSpan = true;
                }

                //Clear diary.
                deleteCurrentEntry();

                //Update to the blank record of today.
                updateSelectedDate(startOfToday);

                //Reset diary date.
                DiaryManager.setDate(startOfToday);

                //Remove old date from list if it is not today.
                if (removeDate.getTime() != startOfToday.getTime()) {
                    cmbDateList.removeItem(removeDate);
                }
                
                //Undo the hold on changing the main window's date if the entry
                //is for today.
                if (removeDate.getTime() == startOfToday.getTime()) {
                    this.holdChangeMainWindowTimeSpan = false;
                }
                
                return;
            }

            //Find post-delete index
            int index = cmbDateList.getSelectedIndex();
            if (index == cmbDateList.getItemCount() - 1) {
                index--;
            }

            deleteCurrentEntry();
            cmbDateList.removeItem(removeDate);

            //Clean up
            cmbDateList.setSelectedIndex(index);
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnSaveAndCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveAndCloseActionPerformed
        if (saveDiaryEntry()) {
            showInfoPane("Your diary entry has been "
                    + "saved successfully.", "BU Daily Diary Entry");

            /*Prepare for close.*/
            //uncheck check bowes so event handlers doesn't fire when date and time 
            //are changed
            this.changeLocationCheckBox.setSelected(false);
            this.changeDateCheckBox.setSelected(false);

            //set notes to current resource.
            externalWindowOpened = false;
            mainWin.initializeNotesPanel();
            updateInternalDiaryResource(appControl.getGeneralService()
                    .getCurrentWeatherCameraResource());

            //set notes to current date
            updateSelectedDate(mc.getCurrentAbsoluteTime());

            /*Close frame.*/
            externalFrame.dispose();
            internalize();
        }
    }//GEN-LAST:event_btnSaveAndCloseActionPerformed

    /**
     * Shows an <code>ExportDiaryWindow</code> for the current diary
     * <code>Resourcce</code>.
     */
    public void exportDiary() {
        checkDiarySaved();
        if (hasExistingEntriesForCurrentResource()) {
            new ExportDiaryWindow(this);
        }
    }

    private void btnExportEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportEntryActionPerformed
        exportDiary();
    }//GEN-LAST:event_btnExportEntryActionPerformed

    private void btnSaveEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveEntryActionPerformed
        if (saveDiaryEntry()) {
            showInfoPane("Your diary entry has been "
                    + "saved successfully.", "BU Daily Diary Entry");
        }
        setDiaryFieldsAndFlags();
    }//GEN-LAST:event_btnSaveEntryActionPerformed

    private void windSpeedAndDirectionLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_windSpeedAndDirectionLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Wind Directions (Surface)")) {
                openDiaryLink(wl.getURLString());
            }
        }

    }//GEN-LAST:event_windSpeedAndDirectionLinkMouseClicked

    private void windDirectionLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_windDirectionLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Wind Direction (Upper Air)")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_windDirectionLinkMouseClicked

    private void windChillLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_windChillLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Maximum Departure from Normal Due to Wind")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_windChillLinkMouseClicked

    private void heatIndexLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_heatIndexLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Maximum Heat Index")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_heatIndexLinkMouseClicked

    private void precipitationLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_precipitationLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Daily Precipitation")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_precipitationLinkMouseClicked

    private void cloudsLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cloudsLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Sky Conditions / Cloud Types")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_cloudsLinkMouseClicked

    private void relativeHumidityLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_relativeHumidityLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Relative Humidity")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_relativeHumidityLinkMouseClicked

    private void dewPointLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dewPointLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Dew Point")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_dewPointLinkMouseClicked

    private void barometricPressureLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barometricPressureLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Barometric Pressure")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_barometricPressureLinkMouseClicked

    private void temperatureLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_temperatureLinkMouseClicked
        for (DailyDiaryWebLinks wl : webLinks) {
            if (wl.getName().equalsIgnoreCase("Temperature")) {
                openDiaryLink(wl.getURLString());
            }
        }
    }//GEN-LAST:event_temperatureLinkMouseClicked

    private void fldMinRHKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fldMinRHKeyReleased
        fldRHRange.setText(getRange(fldMaxRH.getText(), fldMinRH.getText()));
    }//GEN-LAST:event_fldMinRHKeyReleased

    private void fldMaxRHKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fldMaxRHKeyReleased
        fldRHRange.setText(getRange(fldMaxRH.getText(), fldMinRH.getText()));
    }//GEN-LAST:event_fldMaxRHKeyReleased

    private void cmbDateListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDateListActionPerformed
        //Stop method if its actions should not be executed.
        if (!externalWindowOpened || holdCmbDailyEntryListener) {
            return;
        }

        //Test for saves
        if (externalFrame.isDailyDairy()) {
            checkDiarySaved();
            if (!diarySaved) {
                holdCheckForUnsavedDiary = true;
                cmbDateList.setSelectedItem(DiaryManager.getDate());
                holdCheckForUnsavedDiary = false;
                return;
            }
        }
        if (externalFrame.isPersonalNotes()) {
            checkPersonalNotesSaved();
        }

        holdCheckForUnsavedDiary = true;
        updateFieldContent((Date) cmbDateList.getSelectedItem());
        holdCheckForUnsavedDiary = false;

        //Change main window if option is checked and the progron is not in the
        //middle of changing resources.
        if (changeDateCheckBox.isSelected() && !holdChangeMainWindowTimeSpan) {
            this.updateMainWindowToDiaryDate();
        }
    }//GEN-LAST:event_cmbDateListActionPerformed

    private void btnSearchEntriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchEntriesActionPerformed
        //Test for saves
        if (externalFrame.isDailyDairy()) {
            checkDiarySaved();
            if (!diarySaved) {
                return;
            }
        }
        if (externalFrame.isPersonalNotes()) {
            checkPersonalNotesSaved();
        }

        //Prepare date list to sebd
        ArrayList<Date> dateList;
        if (externalFrame.isDailyDairy()) {
            dateList = this.getAllDiaryEntryDates(true);
        } else if (externalFrame.isPersonalNotes()) {
            dateList = this.getAllDiaryEntryDates(false);
        } else {
            dateList = this.getAllDatesWithVisibleClassNotes();
        }

        if (dateList.isEmpty()) {
            showInfoPane("There are no existing selections available.",
                    lblExistingDate.getText());
            return;
        }

        //Get time zone for date-choosing form.
        TimeZone timeZone = DiaryManager.getResource().getTimeZone()
                .getTimeZone();

        //Choose from dates with existing entries for the current resource.
        new ExistingNoteDateChooser(this, dateList, timeZone);
    }//GEN-LAST:event_btnSearchEntriesActionPerformed

    private void btnExpandNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandNotesActionPerformed
        openExternalWindow(ExternalNotesForm.CLASS_NOTES);
    }//GEN-LAST:event_btnExpandNotesActionPerformed

    private void btnRefreshNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshNotesActionPerformed
        setClassNotes();
    }//GEN-LAST:event_btnRefreshNotesActionPerformed

    private void btnExpandPersonalNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandPersonalNotesActionPerformed
        checkPersonalNotesSaved();
        openExternalWindow(ExternalNotesForm.PERSONAL_NOTES);
    }//GEN-LAST:event_btnExpandPersonalNotesActionPerformed

    private void btnDiaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiaryActionPerformed
        checkPersonalNotesSaved();
        openExternalWindow(ExternalNotesForm.DAILY_DIARY);
    }//GEN-LAST:event_btnDiaryActionPerformed

    private void btnSaveLocalNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveLocalNotesActionPerformed
        savePersonalNotesAndNotify();
    }//GEN-LAST:event_btnSaveLocalNotesActionPerformed

    private void btnEditNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditNoteActionPerformed
        editClassNotes();
    }//GEN-LAST:event_btnEditNoteActionPerformed

    private void btnCreateNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateNoteActionPerformed
        createClassNote();
    }//GEN-LAST:event_btnCreateNoteActionPerformed

    private void fldMaxTempKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fldMaxTempKeyReleased
        fldTempRange.setText(getRange(fldMaxTemp.getText(), fldMinTemp.getText()));
    }//GEN-LAST:event_fldMaxTempKeyReleased

    private void fldMinTempKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fldMinTempKeyReleased
        fldTempRange.setText(getRange(fldMaxTemp.getText(), fldMinTemp.getText()));
    }//GEN-LAST:event_fldMinTempKeyReleased

    private void fldStartBPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fldStartBPKeyReleased
        fldBPRange.setText(getRange(fldEndBP.getText(), fldStartBP.getText()));
    }//GEN-LAST:event_fldStartBPKeyReleased

    private void fldEndBPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fldEndBPKeyReleased
        fldBPRange.setText(getRange(fldEndBP.getText(), fldStartBP.getText()));
    }//GEN-LAST:event_fldEndBPKeyReleased

    private void fldStartDPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fldStartDPKeyReleased
        fldDPRange.setText(getRange(fldEndDP.getText(), fldStartDP.getText()));
    }//GEN-LAST:event_fldStartDPKeyReleased

    private void fldEndDPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fldEndDPKeyReleased
        fldDPRange.setText(getRange(fldEndDP.getText(), fldStartDP.getText()));
    }//GEN-LAST:event_fldEndDPKeyReleased

    private void btnNewEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewEntryActionPerformed
        //Test for saves.
        if (externalFrame.isDailyDairy()) {
            checkDiarySaved();
            if (!diarySaved) {
                return;
            }
        }
        if (externalFrame.isPersonalNotes()) {
            checkPersonalNotesSaved();
        }

        //Get data for date-choosing form.
        TimeZone timeZone = DiaryManager.getResource().getTimeZone()
                .getTimeZone();
        Date newDate = (Date) cmbDateList.getSelectedItem();

        //Select the new date.
        new NewNoteDateChooser(this, newDate, timeZone);
    }//GEN-LAST:event_btnNewEntryActionPerformed

    private void txtLocalNotesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLocalNotesKeyTyped
        if (!isPrivilegedUser()) {
            personalNotesSaved = false;
        }
    }//GEN-LAST:event_txtLocalNotesKeyTyped

    private void setVideoDateAndTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setVideoDateAndTimeButtonActionPerformed
        this.updateMainWindowToDiaryResources();
        this.updateMainWindowToDiaryDate();
    }//GEN-LAST:event_setVideoDateAndTimeButtonActionPerformed

    private void refreshNotesSideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshNotesSideButtonActionPerformed
        setClassNotes();
    }//GEN-LAST:event_refreshNotesSideButtonActionPerformed

    private void createNoteSideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNoteSideButtonActionPerformed
        createClassNote();
    }//GEN-LAST:event_createNoteSideButtonActionPerformed

    private void editNoteSideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editNoteSideButtonActionPerformed
        editClassNotes();
    }//GEN-LAST:event_editNoteSideButtonActionPerformed

    private void saveLocalNotesSideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLocalNotesSideButtonActionPerformed
        savePersonalNotesAndNotify();
    }//GEN-LAST:event_saveLocalNotesSideButtonActionPerformed

    private void cameraComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraComboBoxActionPerformed
        //Do nothing if resetting for unsaved diary or not external.
        if (!externalWindowOpened || holdLoadOfNewResource) {
            return;
        }

        int panelIndex = currentPane.getSelectedIndex();

        //Check for unsaved daily diary.
        if (panelIndex == ExternalNotesForm.DAILY_DIARY) {
            checkDiarySaved();

            //If diary not saved, go back.
            if (!diarySaved) {
                for (int i = 0; i < cameraComboBox.getItemCount(); i++) {
                    if (DiaryManager.getResource().getName().equals(
                            cameraComboBox.getItemAt(i).toString())) {
                        holdLoadOfNewResource = true;
                        cameraComboBox.setSelectedIndex(i);
                        holdLoadOfNewResource = false;
                        return;
                    }
                }
            }
        }

        //Make sure old personal notes are saved.
        if (panelIndex == ExternalNotesForm.PERSONAL_NOTES) {
            checkPersonalNotesSaved();
        }

        //Get selected resource.
        DiaryManager.setResource(getCameraFromName(cameraComboBox
                .getSelectedItem().toString()));

        //Update rendering time zone.
        TimeZone timezone = DiaryManager.getResource().getTimeZone().
                getTimeZone();
        this.dateFormatMMDDYYYY.setTimeZone(timezone);
        this.renderer.setTimeZone(timezone);

        //Update date selection box.
        updateDateSelectionBox(panelIndex);

        //Select current diary date.
        this.holdChangeMainWindowTimeSpan = true;
        updateSelectedDate(DiaryManager.getDate());
        this.holdChangeMainWindowTimeSpan = false;

        //Change main window if option is checked.
        if (this.changeLocationCheckBox.isSelected()) {
            this.updateMainWindowToDiaryResources();
        }
    }//GEN-LAST:event_cameraComboBoxActionPerformed

    /**
     * Finds the camera that corresponds to the given name.
     *
     * @param name The name of the resource to find.
     * @return The camera that corresponds to the given name or null if no
     * resource is found.
     */
    private Resource getCameraFromName(String name) {
        Vector<Resource> resources = service.getWeatherCameraResources();
        for (Resource resource : resources) {
            if (resource.getName().equals(name)) {
                return resource;
            }
        }
        return null; // Resource of that name not found
    }

    /**
     * Opens the form that creates a new class note.
     */
    private void createClassNote() {
        new CreateInstructorNoteWindow(service, this);
    }

    /**
     * Saves the personal notes and notifies the user.
     */
    private void savePersonalNotesAndNotify() {
        if (savePersonalNotes()) {
            showInfoPane("Your personal notes have been saved", "Notes Saved");
        }
    }

    /**
     * Opens the form that lets the user edit class notes. If the user has no
     * notes to edit, a message box replaces the form.
     */
    private void editClassNotes() {
        //Get all possible notes from data base.
        Vector<InstructorNote> allNotes;
        if (user.getUserType() == UserType.instructor) {
            allNotes = service.getDBMSSystem().getNoteManager().getNotesByInstructor(user);
        } else {
            allNotes = service.getDBMSSystem().getNoteManager().getAllNotes();
        }

        //Restrict notes to those for the currnt diary resource.
        Vector<InstructorNote> resourceNotes = DiaryManager
                .restrictToCurrentResource(allNotes);

        if (resourceNotes.isEmpty()) {
            showInfoPane("You have no notes to edit for this resource.",
                    "No Notes Found");
            return;
        }
        new EditInstructorNoteWindow(service, this, resourceNotes);
    }

    /**
     * Updates the fields on the form. This method is only for use when the date
     * selection box is already set or does not need to be set.
     *
     * @param newDate The date to be updated.
     */
    public void updateFieldContent(Date newDate) {
        if (!personalNotesSaved) {
            savePersonalNotes();
        }
        DiaryManager.setDate(newDate);
        setPersonalNotes();
        setClassNotes();
        setDiaryFieldsAndFlags();
        lblDate.setText(dateFormatMMDDYYYY.format(newDate));
    }

    /**
     * Adds listeners that are common to several components.
     */
    private void addCommonListeners() {
        // Fields
        JTextComponent[] fields = new JTextComponent[]{
            fldDailyPrecipitation, fldStartBP, fldStartDP, fldMaxGustSpeed, fldMaxHeatIndex,
            fldMaxRH, fldMaxTemp, fldEndBP, fldEndDP, fldMinRH, fldMinTemp, fldMinWindChill,
            txtDiaryNotes
        };

        RightClickMenu.addMenuTo(fields);

        DocumentListener docLsr = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkToMarkDiaryUnsaved();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkToMarkDiaryUnsaved();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };

        for (JTextComponent f : fields) {
            f.getDocument().addDocumentListener(docLsr);
        }

        // Combo boxes
        JComboBox[] boxes = new JComboBox[]{
            cmbTempTrend, cmbBPTrend, cmbDPTrend, cmbRHTrend, cmbEndBPTime, cmbEndDPTime,
            cmbMaxRHTime, cmbMaxTempTime, cmbStartBPTime, cmbStartDPTime, cmbMinRHTime,
            cmbMinTempTime, cmbPrimaryAfternoonClouds, cmbPrimaryMorningClouds, cmbPrimaryNightClouds,
            cmbSecondaryAfternoonClouds, cmbSecondaryMorningClouds, cmbSecondaryNightClouds, cmbUpperAirWindDirection,
            cmbWindDirectionSummary, cmbWindSpeed
        };

        ActionListener actLsr = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkToMarkDiaryUnsaved();
            }
        };

        for (JComboBox box : boxes) {
            box.addActionListener(actLsr);
        }

        // Radio buttons
        JCheckBox[] checkBoxes = getWindDirectionButtonArray();
        WindDirectionType[] types = WindDirectionType.values();

        for (int i = 0; i < checkBoxes.length; i++) {
            final WindDirectionType type = types[i + 1];
            checkBoxes[i].addActionListener(
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (((JCheckBox) e.getSource()).isSelected()) {
                        addSurfaceWindDirection(type);
                    } else {
                        removeSurfaceWindDirection(type);
                    }
                    checkToMarkDiaryUnsaved();
                }
            }
            );
        }
    }

    /**
     * Function to be called by listeners.
     */
    private void attemptClose() {
        //check for unsaved data
        if (externalFrame.isDailyDairy()) {
            checkDiarySaved();
            if (!diarySaved) {
                return;
            }
        }
        if (externalFrame.isPersonalNotes()) {
            checkPersonalNotesSaved();
        }

        //uncheck check bowes so event handlers doesn't fire when date and time 
        //are changed
        this.changeLocationCheckBox.setSelected(false);
        this.changeDateCheckBox.setSelected(false);

        //set notes to current resource.
        externalWindowOpened = false;
        mainWin.initializeNotesPanel();
        updateInternalDiaryResource(appControl.getGeneralService()
                .getCurrentWeatherCameraResource());

        //set notes to current date
        updateSelectedDate(mc.getCurrentAbsoluteTime());

        //close frame
        externalFrame.dispose();
        internalize();
    }

    /**
     * An action listener class used for performing functions on windows that
     * contain any of the panels defined in this class.
     */
    private class NoteAndDiaryCloseListener implements ActionListener {

        /**
         * Sets the window that this class will be used with.
         *
         * @param newFrame The JFrame that will contain the panels.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            attemptClose();
        }
    }

    /**
     * A window listener class used for performing functions on windows that
     * contain any of the panels defined in this class.
     */
    private class NoteAndDiaryWindowListener implements WindowListener {

        /**
         * Called when the window is attempting to close as a result of the user
         * clicking the X button on the window.
         *
         * @param e The event of the window trying to close.
         */
        @Override
        public void windowClosing(WindowEvent e) {
            attemptClose();
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosed(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
    }

    /**
     * Returns button panel.
     */
    public JPanel getButtonPanel() {
        return pnlButtons;
    }

    /**
     * A ChangeListener class that is used for actions of tabs being changed in
     * a JTabbedPane.
     */
    private class NoteAndDiaryChangeListener implements ChangeListener {

        /**
         * Called when the selected tab is changed in a JTabbedPane.
         *
         * @param e The event of the selected tab being changed.
         */
        @Override
        public void stateChanged(ChangeEvent e) {
            //Needed because date box is changed.
            holdChangeMainWindowTimeSpan = true;

            if (externalFrame.isDailyDairy()) {
                checkPersonalNotesSaved();  //personal notes to diary
                externalFrame.showButtonPanel();
                //Set top to diary dates and set bottom to current date.
                loadPanel(ExternalNotesForm.DAILY_DIARY);
                updateSelectedDate(DiaryManager.getDate());
            } else {
                externalFrame.showCloseButton();
                //If leaving personal notes, this will catch.
                if (!getPersonalNotesSaved()) {
                    checkPersonalNotesSaved();  //personal notes to class notes
                    loadPanel(ExternalNotesForm.CLASS_NOTES);
                    updateSelectedDate(DiaryManager.getDate());
                    return;
                }
                //if leaving daily diary, code gets here.
                //(could be laaving other tabs whren below check should
                //have no effect)
                checkDiarySaved();

                //if diary not saved, go back; else load combo box
                if (!diarySaved) {
                    externalFrame.setToDailyDiary();
                } else {
                    if (externalFrame.isPersonalNotes()) {
                        loadPanel(ExternalNotesForm.PERSONAL_NOTES);
                    } else {
                        loadPanel(ExternalNotesForm.CLASS_NOTES);
                    }
                    updateSelectedDate(DiaryManager.getDate());
                }
            }

            //Undo hold
            holdChangeMainWindowTimeSpan = false;
        }
    }

    /**
     * A function to report whether or not the manager is in external mode.
     *
     * @return True if the manager is external, false otherwise.
     */
    public boolean isExternal() {
        return externalWindowOpened;
    }

    /**
     * A function to updateFieldContent class note date box after notes are
     * made, edited or deleted.
     */
    public void updateClassNoteNoteDates() {
        if (externalWindowOpened) {
            //This line neccessary for change back to trigger reload of dates.
            externalFrame.setToDailyDiary();

            externalFrame.setToClassNotes();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel additionalVariablesTitle;
    private javax.swing.JLabel barometricPressureLink;
    private javax.swing.JLabel barometricPressureTitle;
    private javax.swing.JButton btnCreateNote;
    private javax.swing.JButton btnDiary;
    private javax.swing.JButton btnEditNote;
    private javax.swing.JButton btnExpandNotes;
    private javax.swing.JButton btnExpandPersonalNotes;
    private javax.swing.JButton btnExportEntry;
    private javax.swing.JButton btnNewEntry;
    private javax.swing.JButton btnRefreshNotes;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSaveAndClose;
    private javax.swing.JButton btnSaveEntry;
    private javax.swing.JButton btnSaveLocalNotes;
    private javax.swing.JButton btnSearchEntries;
    private javax.swing.JComboBox<ResourceListCellItem> cameraComboBox;
    private javax.swing.JCheckBox changeDateCheckBox;
    private javax.swing.JCheckBox changeLocationCheckBox;
    private javax.swing.JCheckBox checkCalm;
    private javax.swing.JCheckBox checkEast;
    private javax.swing.JCheckBox checkNorth;
    private javax.swing.JCheckBox checkNorthEast;
    private javax.swing.JCheckBox checkNorthWest;
    private javax.swing.JCheckBox checkSouth;
    private javax.swing.JCheckBox checkSouthEast;
    private javax.swing.JCheckBox checkSouthWest;
    private javax.swing.JCheckBox checkWest;
    private javax.swing.JPanel classNotePanel;
    private javax.swing.JLabel cloudsLink;
    private javax.swing.JLabel cloudsTitle;
    private javax.swing.JComboBox cmbBPTrend;
    private javax.swing.JComboBox cmbDPTrend;
    private javax.swing.JComboBox cmbDailyHumidityQ1;
    private javax.swing.JComboBox cmbDailyPressureQ1;
    private javax.swing.JComboBox cmbDailyPressureQ2;
    private javax.swing.JComboBox<Date> cmbDateList;
    private javax.swing.JComboBox cmbEndBPTime;
    private javax.swing.JComboBox cmbEndDPTime;
    private javax.swing.JComboBox cmbMaxRHTime;
    private javax.swing.JComboBox cmbMaxTempTime;
    private javax.swing.JComboBox cmbMinRHTime;
    private javax.swing.JComboBox cmbMinTempTime;
    private javax.swing.JComboBox cmbPredictionsQ1;
    private javax.swing.JComboBox cmbPredictionsQ2;
    private javax.swing.JComboBox cmbPrimaryAfternoonClouds;
    private javax.swing.JComboBox cmbPrimaryMorningClouds;
    private javax.swing.JComboBox cmbPrimaryNightClouds;
    private javax.swing.JComboBox cmbRHTrend;
    private javax.swing.JComboBox cmbSecondaryAfternoonClouds;
    private javax.swing.JComboBox cmbSecondaryMorningClouds;
    private javax.swing.JComboBox cmbSecondaryNightClouds;
    private javax.swing.JComboBox cmbStartBPTime;
    private javax.swing.JComboBox cmbStartDPTime;
    private javax.swing.JComboBox cmbTempProfileQ1;
    private javax.swing.JComboBox cmbTempTrend;
    private javax.swing.JComboBox cmbUpperAirWindDirection;
    private javax.swing.JComboBox cmbWindDirectionSummary;
    private javax.swing.JComboBox cmbWindSpeed;
    private javax.swing.JButton createNoteSideButton;
    private javax.swing.JPanel daily_Diary_Part2_JPanel;
    private javax.swing.JPanel daily_diary_Part1_JPanel;
    private javax.swing.JLabel dewPointLink;
    private javax.swing.JLabel dewPointTitle;
    private javax.swing.JLabel diaryNotesLabel;
    private javax.swing.JScrollPane diaryNotesScrollPane;
    private javax.swing.JTabbedPane diaryTabbedPane;
    private javax.swing.JLabel doNotUseLabel;
    private javax.swing.JButton editNoteSideButton;
    private javax.swing.JCheckBox expandCheckBox;
    private javax.swing.JTextField fldBPRange;
    private javax.swing.JTextField fldDPRange;
    private javax.swing.JTextField fldDailyPrecipitation;
    private javax.swing.JTextField fldEndBP;
    private javax.swing.JTextField fldEndDP;
    private javax.swing.JTextField fldMaxGustSpeed;
    private javax.swing.JTextField fldMaxHeatIndex;
    private javax.swing.JTextField fldMaxRH;
    private javax.swing.JTextField fldMaxTemp;
    private javax.swing.JTextField fldMinRH;
    private javax.swing.JTextField fldMinTemp;
    private javax.swing.JTextField fldMinWindChill;
    private javax.swing.JTextField fldRHRange;
    private javax.swing.JTextField fldStartBP;
    private javax.swing.JTextField fldStartDP;
    private javax.swing.JTextField fldTempRange;
    private javax.swing.JLabel heatIndexLink;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lastModifiedLabel;
    private javax.swing.JLabel lastModifiedStatusLabel;
    private javax.swing.JLabel lblAfternoonClouds;
    private javax.swing.JLabel lblBPRange;
    private javax.swing.JLabel lblBPTrend;
    private javax.swing.JLabel lblDPRange;
    private javax.swing.JLabel lblDPTrend;
    private javax.swing.JLabel lblDailyHumidityProfile;
    private javax.swing.JLabel lblDailyHumidityQ1;
    private javax.swing.JLabel lblDailyPrecip;
    private javax.swing.JLabel lblDailyPressureChange;
    private javax.swing.JLabel lblDailyPressureQ1;
    private javax.swing.JLabel lblDailyPressureQ2;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDateExplanation;
    private javax.swing.JLabel lblEast;
    private javax.swing.JLabel lblEndBP;
    private javax.swing.JLabel lblEndDP;
    private javax.swing.JLabel lblExistingDate;
    private javax.swing.JLabel lblGapFill1;
    private javax.swing.JLabel lblGapFill2;
    private javax.swing.JLabel lblMaxBPTime;
    private javax.swing.JLabel lblMaxDPTime;
    private javax.swing.JLabel lblMaxGustSpeed;
    private javax.swing.JLabel lblMaxHI;
    private javax.swing.JLabel lblMaxRH;
    private javax.swing.JLabel lblMaxRHTime;
    private javax.swing.JLabel lblMaxTemp;
    private javax.swing.JLabel lblMaxTempTime;
    private javax.swing.JLabel lblMinBPTime;
    private javax.swing.JLabel lblMinDPTime;
    private javax.swing.JLabel lblMinRH;
    private javax.swing.JLabel lblMinRHTime;
    private javax.swing.JLabel lblMinTemp;
    private javax.swing.JLabel lblMinTempTime;
    private javax.swing.JLabel lblMinWindChill;
    private javax.swing.JLabel lblMorningClouds;
    private javax.swing.JLabel lblNewDate;
    private javax.swing.JLabel lblNightClouds;
    private javax.swing.JLabel lblNorth;
    private javax.swing.JLabel lblPredictions;
    private javax.swing.JLabel lblPredictionsQ1;
    private javax.swing.JLabel lblPredictionsQ2;
    private javax.swing.JLabel lblPrimary;
    private javax.swing.JLabel lblRHRange;
    private javax.swing.JLabel lblRHTrend;
    private javax.swing.JLabel lblSecondary;
    private javax.swing.JLabel lblSouth;
    private javax.swing.JLabel lblStartBP;
    private javax.swing.JLabel lblStartDP;
    private javax.swing.JLabel lblTempProfileQ1;
    private javax.swing.JLabel lblTempRange;
    private javax.swing.JLabel lblTempTrend;
    private javax.swing.JLabel lblTemperatureProfile;
    private javax.swing.JLabel lblWest;
    private javax.swing.JLabel lblWindDirectionSummary;
    private javax.swing.JLabel lblWindDirectionUp;
    private javax.swing.JLabel lblWindPanelHeader;
    private javax.swing.JLabel lblWindSpeed;
    private javax.swing.JScrollPane noteScrollPane;
    private javax.swing.JTable noteTable;
    private javax.swing.JScrollPane notesScrollPane;
    private javax.swing.JPanel personalNotePanel;
    private javax.swing.JPanel pnlAdditionalVariables;
    private javax.swing.JPanel pnlBarometricPressure;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlClassNotes;
    private javax.swing.JPanel pnlCloudTypes;
    private javax.swing.JPanel pnlCompass;
    private javax.swing.JPanel pnlCreatedDate;
    private javax.swing.JPanel pnlDate;
    private javax.swing.JPanel pnlDewPoint;
    private javax.swing.JPanel pnlDiary;
    private javax.swing.JPanel pnlExistintngDate;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlNewDate;
    private javax.swing.JPanel pnlOptionsAndNotes;
    private javax.swing.JTabbedPane pnlPanels;
    private javax.swing.JPanel pnlPersonalNotes;
    private javax.swing.JPanel pnlRelativeHumidity;
    private javax.swing.JPanel pnlTemperature;
    private javax.swing.JPanel pnlWind;
    private javax.swing.JLabel precipitationLink;
    private javax.swing.JButton refreshNotesSideButton;
    private javax.swing.JLabel relativeHumidityLink;
    private javax.swing.JLabel relativeHumidityTitle;
    private javax.swing.JButton saveLocalNotesSideButton;
    private javax.swing.JLabel selectCameraLabel;
    private javax.swing.JButton setVideoDateAndTimeButton;
    private javax.swing.JLabel temperatureLink;
    private javax.swing.JLabel temperatureTitle;
    private javax.swing.JTextArea txtDiaryNotes;
    private javax.swing.JTextArea txtLocalNotes;
    private javax.swing.JLabel windChillLink;
    private javax.swing.JLabel windDirectionLink;
    private javax.swing.JLabel windSpeedAndDirectionLink;
    private javax.swing.JLabel windSpeedAndDirectionTitle;
    // End of variables declaration//GEN-END:variables
}
