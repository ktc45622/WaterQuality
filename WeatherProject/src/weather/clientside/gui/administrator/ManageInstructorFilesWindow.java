package weather.clientside.gui.administrator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.InstructorDataType;
import weather.common.data.InstructorFileInstance;
import weather.common.data.Instructor;
import weather.common.data.User;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.bookmark.BookmarkFileInstance;
import weather.common.data.note.NoteFileInstance;
import weather.common.dbms.DBMSFileManager;
import weather.common.gui.component.BUDialog;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.WeatherLogger;

/**
 * Allows an instructor to manage their files. This means files can be 
 * disassociated from notes, bookmarks, become private, go from private 
 * to be associated with any of the above resources, or be deleted.
 * 
 * @author Chris Vitello (2012)
 * @version Spring 2012
 */
public class ManageInstructorFilesWindow extends BUDialog {
    
    private final User instructor;
    private DBMSFileManager fileManager;
    private JTable fileTable = new JTable();
    private final MouseAdapter doubleClick;
    
    private Vector<InstructorFileInstance> fileInstances;
    
    private int fileTableNumberOfRows;    
    private final int fileTableNumberOfColumns = 3;
    private final int fileNameColumn = 0;    
    private final int fileNumberColumn = 1;
    private final int fileResourceColumn = 2;
    
    private int currentIndex = 0;
    private final int allFilesPaneIndex = 0;
    private final int instructorNotesPaneIndex = 1;
    private final int bookmarkPaneIndex = 2;
    private final int lessonPaneIndex = 3;
    private final int privatePaneIndex = 4;
    
    /**
     * Constructor.
     * @param appControl The program's <code>ApplicationControlSystem</code>.
     */
    public ManageInstructorFilesWindow(final ApplicationControlSystem appControl) {
        super(appControl);                      
        fileManager = appControl.getDBMSSystem().getFileManager();
        fileManager.setStorageSystem(StorageControlSystemImpl.getStorageSystem());
        this.instructor = appControl.getGeneralService().getUser();  
        
        //Setup GUI.
        initComponents();
        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileTable.setRowSelectionAllowed(true);
        fileTable.setAutoCreateRowSorter(true);   
        doubleClick = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                errorTextLabel.setVisible(false);                
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int fileNumber = (Integer) target.getModel()
                            .getValueAt(target.getSelectedRow(), 
                                    fileNumberColumn);
                    InstructorFileInstance fileInstance = null;
                    
                    //Get location from user.
                    fileInstance = fileManager.getFile(fileNumber);
                    String chosenDirectory = WeatherFileChooser
                            .openDirectoryChooser(new File(CommonLocalFileManager
                            .getDataDirectory()), "Save File...", null, 
                            getThisWindow());
                    if (chosenDirectory == null) {
                        return;
                    }
                    File dest = new File(chosenDirectory + File.separator 
                            + fileInstance.getFileName());
                    if (dest.exists()) {
                        JOptionPane.showMessageDialog(getThisWindow(), 
                                "The file is already saved at this location.");
                        return;
                    }

                    try {
                        FileOutputStream fout = new FileOutputStream(dest);
                        fout.write(fileInstance.getFileData());
                        fout.close();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(getThisWindow(), 
                                "An error occurred while trying to save the file.");
                        //Test for remaining space in application home, which has no effect
                        //if the save was not there.
                        StorageSpaceTester.testApplicationHome();
                        return;
                    }
                    JOptionPane.showMessageDialog(getThisWindow(), 
                            "File saved.");

                    //Test for remaining space in application home, which has no effect
                    //if the save was not there.
                    StorageSpaceTester.testApplicationHome();
                }
            }
        };
        fileTable.addMouseListener(doubleClick);
        initialize();  
        
        //Keep until instructor lessons are finished.
        typeSelectionPane.setEnabledAt(this.lessonPaneIndex, false);
        
        //Size window.
        int width = 837 + this.getInsets().left + this.getInsets().right;
        int height = 557 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();      
        super.postInitialize(true);    
    }
    
    /**
     * A function to make this object available to the code in the constructor
     * the initializes the mouse adaptor for the tables.
     * @return This object as a <code>Component</code>.
     */
    private Component getThisWindow() {
        return this;
    }
    
    /**
     * Initializes the window components.
     */
    private void initialize() {            
        //Reset file instance list
        fileInstances = new Vector<>();
        
        switch (currentIndex) {
            case instructorNotesPaneIndex:
                fileInstances.addAll(fileManager
                        .getAllNoteFilesForInstructor(instructor));
                break;
            case bookmarkPaneIndex:
                fileInstances.addAll(fileManager
                        .getAllBookmarkFilesForInstructor(instructor));
                break;
            case lessonPaneIndex:
                fileInstances.addAll(fileManager
                        .getAllLessonFilesForInstructor(instructor));
                break;        
            case privatePaneIndex:
                fileInstances.addAll(fileManager
                        .getAllPrivateFilesForInstructor(instructor
                                .getUserNumber()));
                break;
            case allFilesPaneIndex:
                fileInstances.addAll(fileManager
                        .getAllFilesForInstructor(instructor.getUserNumber()));
                break;
            default:
                break;
        }
        
        //Make table structure.
        fileTableNumberOfRows = getTableRows();               
        fileTable.setModel(new MyDefaultTableModel(fileTableNumberOfRows, fileTableNumberOfColumns));   
        fileTable.getColumnModel().getColumn(fileNumberColumn).setMinWidth(0);
        fileTable.getColumnModel().getColumn(fileNumberColumn).setMaxWidth(0);
        fileTable.getColumnModel().getColumn(fileNumberColumn).setWidth(0);
        fileTable.getColumnModel().getColumn(fileNameColumn).setHeaderValue("File Name");
        fileTable.getColumnModel().getColumn(fileResourceColumn).setHeaderValue("Attached To");  
        
        errorTextLabel.setVisible(false);
        
        //Set viewport.
        switch (currentIndex) {
            case instructorNotesPaneIndex:
                instructorNoteScrollPane.setViewportView(fileTable);
                break;
            case bookmarkPaneIndex:
                bookmarkScrollPane.setViewportView(fileTable);
                break;
            case lessonPaneIndex:
                lessonScrollPane.setViewportView(fileTable);
                break;
            case privatePaneIndex:
                privateScrollPane.setViewportView(fileTable);
                break;
            case allFilesPaneIndex:
                allFilesScrollPane.setViewportView(fileTable);
                break;
            default:
                break;
        }
        
        //Fill table with data.
        populateTable(fileInstances);
    }
    
    /**
     * Fill the JTable with the appropriate files depending on which pane has
     * focus.     
     */
    private void populateTable(Vector<InstructorFileInstance> files) {  
        for (int i = 0; i < files.size(); i++) {
            String attachedObjectName = fileManager.getAttachedObjectName(files
                    .get(i));
            if (attachedObjectName == null) {
                attachedObjectName = "Nothing - Private File";
            }
            fileTable.setValueAt(files.get(i).getFileNumber(), i, fileNumberColumn);
            fileTable.setValueAt(files.get(i).getFileName(), i, fileNameColumn);
            fileTable.setValueAt(attachedObjectName, i, fileResourceColumn);
        }                              
        
        fileTable.revalidate();
    }
    
    /**
     * Gets the number of rows for the table.
     * @return The number of files to be used as the total number of rows.
     */
    private int getTableRows()  {
        return fileInstances.size();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        typeSelectionPane = new javax.swing.JTabbedPane();
        allFilesScrollPane = new javax.swing.JScrollPane();
        instructorNoteScrollPane = new javax.swing.JScrollPane();
        bookmarkScrollPane = new javax.swing.JScrollPane();
        lessonScrollPane = new javax.swing.JScrollPane();
        privateScrollPane = new javax.swing.JScrollPane();
        errorTextLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        saveInstructionLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setTitle("Manage Instructor Files");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        typeSelectionPane.setMinimumSize(new java.awt.Dimension(600, 400));
        typeSelectionPane.setName(""); // NOI18N
        typeSelectionPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                typeSelectionPaneStateChanged(evt);
            }
        });
        typeSelectionPane.addTab("All Files", allFilesScrollPane);

        instructorNoteScrollPane.setName(""); // NOI18N
        typeSelectionPane.addTab("Class Note Files", instructorNoteScrollPane);
        typeSelectionPane.addTab("Bookmark Files", bookmarkScrollPane);
        typeSelectionPane.addTab("Instructional Lesson Files", lessonScrollPane);
        typeSelectionPane.addTab("Private Files", privateScrollPane);

        getContentPane().add(typeSelectionPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 813, 468));

        errorTextLabel.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        errorTextLabel.setForeground(new java.awt.Color(204, 0, 0));
        errorTextLabel.setText("Error goes here.");
        getContentPane().add(errorTextLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 492, 400, 16));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(746, 520, 79, 25));

        saveInstructionLabel.setText("Double-click on a file to save it to your local computer.");
        getContentPane().add(saveInstructionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 492, 400, 16));

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        addButton.setText("Add Private File");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(addButton);

        deleteButton.setText("Delete Selected");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(deleteButton);

        getContentPane().add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 520, 722, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        /**
         * This button defaults to adding a private note if all notes are shown. 
         */
        
        /**
         * Get specific data object (bookmark or class note if necessary.
         * Note: Instructional lessons must be added once implemented.
         */
        int dataNumber = 0; //will remain 0 if not needed.
        switch (currentIndex) {
            case instructorNotesPaneIndex:
                if (appControl.getDBMSSystem().getNoteManager()
                        .getNotesByInstructor(instructor).isEmpty()) {
                    String message = "You cannot attach a file to a class note"
                            + "\nuntil you create a class note";
                    JOptionPane.showMessageDialog(this, message,
                            "No Class Notes Available",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                int noteNumber = ResourceFileSelectionWindow
                        .selectNewResourceNumber(appControl,
                                InstructorDataType.Notes, this);
                if (noteNumber == -1) {
                    return;
                } else {
                    dataNumber = noteNumber;
                }
                break;
            case bookmarkPaneIndex:
                //Must make sure bookmarks are not events.
                Vector<Bookmark> allDatabase = appControl.getDBMSSystem()
                        .getBookmarkManager().searchByCreatedBy(instructor
                                .getUserNumber());
                Vector<Bookmark> bookmarksDatabase = new Vector<>();
                for (Bookmark bookmark : allDatabase) {
                    if (bookmark.getType() == BookmarkDuration.instance) {
                        bookmarksDatabase.add(bookmark);
                    }
                }
                if (bookmarksDatabase.isEmpty()) {
                    String message = "You cannot attach a file to a bookmark"
                            + "\nuntil you create a bookmark";
                    JOptionPane.showMessageDialog(this, message,
                            "No Bookmarks Available",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                int bookmarkNumber = ResourceFileSelectionWindow
                        .selectNewResourceNumber(appControl,
                                InstructorDataType.Bookmarks, this);
                if (bookmarkNumber == -1) {
                    return;
                } else {
                    dataNumber = bookmarkNumber;
                }
            default:
                break;
        }

        /**
         * Get file and file data.
         */
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                new File(CommonLocalFileManager.getRootDirectory()),
                "Upload File", this);
        if (file == null) {
            return;
        }
        
        FileInputStream fis;
        byte[] fileData;
        
        try {
            fis = new FileInputStream(file);
            int size = 0;
            while (fis.read() != -1) {
                size++;
            }
            fis.close();

            fileData = new byte[size];
            fis = new FileInputStream(file);
            fis.read(fileData);
            fis.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to load file.");
            WeatherLogger.log(Level.SEVERE, "Unable to load file.");
            return;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An I/O error has occurred.");
            WeatherLogger.log(Level.SEVERE, "I/O error :" + e.getMessage());
            return;
        }

        /**
         * Save file to database. 
         * Note: Instructional lessons must be added once implemented.
         */
        switch (currentIndex) {
            case instructorNotesPaneIndex:
                NoteFileInstance nfi = new NoteFileInstance(-1,
                        dataNumber, instructor.getUserNumber(),
                        file.getName(), fileData);
                if (fileManager.insertNotesFile(nfi)) {
                    JOptionPane.showMessageDialog(this,
                            "The file was saved.", "Save Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "The file was NOT saved.", "Save Not Successful",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
            case bookmarkPaneIndex:
                BookmarkFileInstance bfi = new BookmarkFileInstance(-1,
                        dataNumber, instructor.getUserNumber(),
                        file.getName(), fileData);
                if (fileManager.insertBookmarksFile(bfi)) {
                    JOptionPane.showMessageDialog(this,
                            "The file was saved.", "Save Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "The file was NOT saved.", "Save Not Successful",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
            default:
                if (fileManager.storePrivateFile(new Instructor(instructor),
                        file.getName(), fileData)) {
                    JOptionPane.showMessageDialog(this,
                            "The file was saved.", "Save Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "The file was NOT saved.", "Save Not Successful",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
        }
        initialize();
    }//GEN-LAST:event_addButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (fileTable.getSelectedRow() == -1) {
            errorTextLabel.setVisible(true);
            errorTextLabel.setText("Please select a note.");
        } else {
            int ans = JOptionPane.showConfirmDialog(this, "Are you sure you "
                    + "want to delete\nthe selected file?", "Delete File", 
                    JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) {
                return;
            }
            int fileNumber = (Integer) fileTable.getModel().getValueAt(fileTable
                    .getSelectedRow(), fileNumberColumn);            
            fileManager.removeFile(fileNumber);
            initialize();
        }        
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void typeSelectionPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_typeSelectionPaneStateChanged
        currentIndex = typeSelectionPane.getSelectedIndex();
        switch (currentIndex) {
            case instructorNotesPaneIndex:
                addButton.setText("Attach File To Class Note");
                break;
            case bookmarkPaneIndex:
                addButton.setText("Attach File To Bookmark");
                break;
            case lessonPaneIndex:
                addButton.setText("Attach File To Lesson");
                break;
            default:
                addButton.setText("Add Private File");
                break;
        }
        initialize();
    }//GEN-LAST:event_typeSelectionPaneStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JScrollPane allFilesScrollPane;
    private javax.swing.JScrollPane bookmarkScrollPane;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel errorTextLabel;
    private javax.swing.JScrollPane instructorNoteScrollPane;
    private javax.swing.JScrollPane lessonScrollPane;
    private javax.swing.JScrollPane privateScrollPane;
    private javax.swing.JLabel saveInstructionLabel;
    private javax.swing.JTabbedPane typeSelectionPane;
    // End of variables declaration//GEN-END:variables

}
