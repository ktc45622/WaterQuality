package weather.clientside.gui.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.AccessRights;
import weather.common.data.User;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.lesson.Lesson;
import weather.common.data.lesson.LessonEntry;
import weather.common.data.lesson.LessonFileInstance;
import weather.common.data.resource.WeatherResourceType;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.dbms.DBMSLessonEntryManager;
import weather.common.dbms.DBMSLessonManager;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.WeatherLogger;

/**
 * This window allows a user to create or edit a lesson.
 * 
 * @author John Lenhart
 * @author Xiang Li
 * 
 * @version Spring 2012
 */
public class LessonAddEditWindow extends  BUDialog {
    
    private User user;
    private ApplicationControlSystem appControl;
    private static final String NONE_SELECTED_TEXT = "None Selected";
    private static final String NOT_ENOUGH_PHENOMS_SELECTED_ERROR = "Error: You must select at least two bookmarks/events.";
    private static final String NO_LESSON_NAME_ERROR = "Error: You must have a name for this Lesson.";
    private Bookmark selectedPhenomenon1;
    private Bookmark selectedPhenomenon2;
    private Bookmark selectedPhenomenon3;
    private Bookmark selectedPhenomenon4;
    private Bookmark selectedPhenomenon5;
    private Bookmark selectedPhenomenon6;
    private Lesson lessonToEdit;
    private boolean isEditLessonScreen;
    private ArrayList<LessonFileInstance> attachedFiles;
    private DefaultListModel<String> fileListModel;
    private DBMSLessonManager lessonManager;
    private DBMSBookmarkInstanceManager bookmarkManager;
    private DBMSLessonEntryManager entryManager;
    private int categoryType;
    
    /**
     * Constructor for creating a new Lesson.
     * 
     * @param appControl ApplicationControl control system used in the program.
     */
    public LessonAddEditWindow(ApplicationControlSystem appControl, int category) {
        super(appControl);
        categoryType = category;
        this.appControl = appControl;
        initComponents();
        initMyComponents();
        lessonToEdit = new Lesson();
        isEditLessonScreen = false;
        setModalityType(ModalityType.APPLICATION_MODAL);
        this.setTitle("Weather Viewer" + " - " + "Create Lesson");
        user = appControl.getGeneralService().getUser();
        everyoneRadio.setSelected(true);
        errorLabel.setVisible(false);
        super.postInitialize(false);
    }
    
    /**
     * Constructor to edit a lesson.
     * 
     * @param appControl The application control.
     * @param lessonToEdit The <code>Lesson</code> this window will display for editing. 
     */
    public LessonAddEditWindow(ApplicationControlSystem appControl, int category, Lesson lessonToEdit) {
        super(appControl);
        categoryType = category;
        this.appControl = appControl;
        initComponents();
        initMyComponents();
        this.lessonToEdit = lessonToEdit;
        isEditLessonScreen = true;
        loadLesson();
        setModalityType(ModalityType.APPLICATION_MODAL);
        this.setTitle("Weather Viewer" + " - " + "Edit " + lessonToEdit.getName());
        createButton.setText("Save");
        user = appControl.getGeneralService().getUser();
        everyoneRadio.setSelected(true);
        errorLabel.setVisible(false);
        super.postInitialize(false);
    }

    /**
     * Helper method to initialize the default state of the components.
     */
    private void initMyComponents()
    {
        bookmarkManager = appControl.getDBMSSystem().getBookmarkManager();
        lessonManager = appControl.getDBMSSystem().getLessonManager();
        entryManager = appControl.getDBMSSystem().getLessonEntryManager();
        bookmarkEventNameTextField.setText(NONE_SELECTED_TEXT);
        bookmarkEventNameTextField2.setText(NONE_SELECTED_TEXT);
        bookmarkEventNameTextField3.setText(NONE_SELECTED_TEXT);
        bookmarkEventNameTextField4.setText(NONE_SELECTED_TEXT);
        bookmarkEventNameTextField5.setText(NONE_SELECTED_TEXT);
        bookmarkEventNameTextField6.setText(NONE_SELECTED_TEXT);
        bookmarkEventNameTextField.setEnabled(false);
        bookmarkEventNameTextField2.setEnabled(false);
        bookmarkEventNameTextField3.setEnabled(false);
        bookmarkEventNameTextField4.setEnabled(false);
        bookmarkEventNameTextField5.setEnabled(false);
        bookmarkEventNameTextField6.setEnabled(false);
        lessonToEdit = null;
        attachedFiles = new ArrayList<LessonFileInstance>();
        fileListModel = new DefaultListModel<String>();
        attachedFilesList.setModel(fileListModel);
        setButtonGroup(); 
    }
    /**
     * Sets the button grouping for the radio buttons.
     */
    private void setButtonGroup()
    {
        ButtonGroup resourceGroup = new ButtonGroup();
        resourceGroup.add(weatherCameraResourcesRadio);
        resourceGroup.add(radarMapRadio);
        resourceGroup.add(dataplotRadio);
        
        ButtonGroup resourceGroup2 = new ButtonGroup();
        resourceGroup2.add(weatherCameraResourcesRadio2);
        resourceGroup2.add(radarMapRadio2);
        resourceGroup2.add(dataplotRadio2);
        
        ButtonGroup resourceGroup3 = new ButtonGroup();
        resourceGroup3.add(weatherCameraResourcesRadio3);
        resourceGroup3.add(radarMapRadio3);
        resourceGroup3.add(dataplotRadio3);
        
        ButtonGroup resourceGroup4 = new ButtonGroup();
        resourceGroup4.add(weatherCameraResourcesRadio4);
        resourceGroup4.add(radarMapRadio4);
        resourceGroup4.add(dataplotRadio4);
        
        ButtonGroup resourceGroup5 = new ButtonGroup();
        resourceGroup5.add(weatherCameraResourcesRadio5);
        resourceGroup5.add(radarMapRadio5);
        resourceGroup5.add(dataplotRadio5);
        
        ButtonGroup resourceGroup6 = new ButtonGroup();
        resourceGroup6.add(weatherCameraResourcesRadio6);
        resourceGroup6.add(radarMapRadio6);
        resourceGroup6.add(dataplotRadio6);
        
        ButtonGroup accessRightsGroup = new ButtonGroup();
        accessRightsGroup.add(courseStudentsRadio);
        accessRightsGroup.add(allStudentsRadio);
        accessRightsGroup.add(instructorsRadio);
        accessRightsGroup.add(everyoneRadio);
        accessRightsGroup.add(privateRadio);
    }
    
    /**
     * Loads the given Lesson into the form.
     */
    private void loadLesson()
    {
        lessonNameTextField.setText(lessonToEdit.getName());
        Vector<LessonEntry> lessonEntry = entryManager.obtainByLessonNumber(lessonToEdit.getLessonNumber());
        
        for(int i = 0; i < lessonEntry.size(); i++)
        {
            LessonEntry currentEntry = lessonEntry.get(i);
            if(i ==0)
            {
                selectedPhenomenon1 = bookmarkManager.searchByBookmarkNumber(currentEntry.getBookmarkNumber());
                bookmarkEventNameTextField.setText(currentEntry.getLessonEntryName());
                bookmarkEventNameTextField.setEnabled(true);
                radarMapRadio.setSelected(true);
                if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherCamera)
                    weatherCameraResourcesRadio.setSelected(true);
                else if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherStation)
                    dataplotRadio.setSelected(true);
            }else if(i ==1)
            {
                selectedPhenomenon2 = bookmarkManager.searchByBookmarkNumber(currentEntry.getBookmarkNumber());
                bookmarkEventNameTextField2.setText(currentEntry.getLessonEntryName());
                bookmarkEventNameTextField2.setEnabled(true);
                radarMapRadio2.setSelected(true);
                if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherCamera)
                    weatherCameraResourcesRadio2.setSelected(true);
                else if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherStation)
                    dataplotRadio2.setSelected(true);
            }else if(i ==2)
            {
                selectedPhenomenon3 = bookmarkManager.searchByBookmarkNumber(currentEntry.getBookmarkNumber());
                bookmarkEventNameTextField3.setText(currentEntry.getLessonEntryName());
                bookmarkEventNameTextField3.setEnabled(true);
                radarMapRadio3.setSelected(true);
                if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherCamera)
                    weatherCameraResourcesRadio3.setSelected(true);
                else if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherStation)
                    dataplotRadio3.setSelected(true);
            }
             else if(i ==3)
            {
                selectedPhenomenon4 = bookmarkManager.searchByBookmarkNumber(currentEntry.getBookmarkNumber());
                bookmarkEventNameTextField4.setText(currentEntry.getLessonEntryName());
                bookmarkEventNameTextField4.setEnabled(true);
                radarMapRadio4.setSelected(true);
                if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherCamera)
                    weatherCameraResourcesRadio4.setSelected(true);
                else if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherStation)
                    dataplotRadio4.setSelected(true);
            }
             else if(i ==4)
            {
                selectedPhenomenon5 = bookmarkManager.searchByBookmarkNumber(currentEntry.getBookmarkNumber());
                bookmarkEventNameTextField5.setText(currentEntry.getLessonEntryName());
                bookmarkEventNameTextField5.setEnabled(true);
                radarMapRadio5.setSelected(true);
                if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherCamera)
                    weatherCameraResourcesRadio5.setSelected(true);
                else if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherStation)
                    dataplotRadio5.setSelected(true);
            }
             else if(i ==5)
            {
                selectedPhenomenon6 = bookmarkManager.searchByBookmarkNumber(currentEntry.getBookmarkNumber());
                bookmarkEventNameTextField6.setText(currentEntry.getLessonEntryName());
                bookmarkEventNameTextField6.setEnabled(true);
                radarMapRadio6.setSelected(true);
                if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherCamera)
                    weatherCameraResourcesRadio6.setSelected(true);
                else if(currentEntry.getBookmarkResourceID() == WeatherResourceType.WeatherStation)
                    dataplotRadio6.setSelected(true);
            }  
        }
        AccessRights rights =lessonToEdit.getAccessRights();
        privateRadio.setSelected(true);
        if(rights == AccessRights.AllStudents)
            allStudentsRadio.setSelected(true);
        else if(rights == AccessRights.CourseStudents)
            courseStudentsRadio.setSelected(true);
        else if(rights == AccessRights.Everyone)
            everyoneRadio.setSelected(true);
        else if(rights== AccessRights.Instructors)
            instructorsRadio.setSelected(true);
        
    }
    
    /**
     * Gets the selected resource from the window.
     * 
     * @param phenomenonNumber the number of the phenomenon to remove.
     * @return The phenomenon resource type.
     */
    private WeatherResourceType getWeatherResourceType(int phenomenonNumber)
    {
        if(phenomenonNumber == 1)
        {
            if(weatherCameraResourcesRadio.isSelected())
                return WeatherResourceType.WeatherCamera;
            else if(radarMapRadio.isSelected())
                return WeatherResourceType.WeatherMapLoop;
            else if(dataplotRadio.isSelected())
                return WeatherResourceType.WeatherStation;
        }
        else if(phenomenonNumber == 2)
        {
            if(weatherCameraResourcesRadio2.isSelected())
                return WeatherResourceType.WeatherCamera;
            else if(radarMapRadio2.isSelected())
                return WeatherResourceType.WeatherMapLoop;
            else if(dataplotRadio2.isSelected())
                return WeatherResourceType.WeatherStation;
        }
         else if(phenomenonNumber == 3)
        {
            if(weatherCameraResourcesRadio3.isSelected())
                return WeatherResourceType.WeatherCamera;
            else if(radarMapRadio3.isSelected())
                return WeatherResourceType.WeatherMapLoop;
            else if(dataplotRadio3.isSelected())
                return WeatherResourceType.WeatherStation;
        }
        else if(phenomenonNumber == 4)
        {
            if(weatherCameraResourcesRadio4.isSelected())
                return WeatherResourceType.WeatherCamera;
            else if(radarMapRadio4.isSelected())
                return WeatherResourceType.WeatherMapLoop;
            else if(dataplotRadio4.isSelected())
                return WeatherResourceType.WeatherStation;
        }
         else if(phenomenonNumber == 5)
        {
            if(weatherCameraResourcesRadio5.isSelected())
                return WeatherResourceType.WeatherCamera;
            else if(radarMapRadio5.isSelected())
                return WeatherResourceType.WeatherMapLoop;
            else if(dataplotRadio5.isSelected())
                return WeatherResourceType.WeatherStation;
        }
        else if(phenomenonNumber == 6)
        {
            if(weatherCameraResourcesRadio6.isSelected())
                return WeatherResourceType.WeatherCamera;
            else if(radarMapRadio6.isSelected())
                return WeatherResourceType.WeatherMapLoop;
            else if(dataplotRadio6.isSelected())
                return WeatherResourceType.WeatherStation;
        }
        return WeatherResourceType.undefined;
    }
    
    /**
     * Gets the currently selected access rights for the <code>Lesson</code>.
     * 
     * @return The currently selected AccessRights.
     */
    private AccessRights getAccessRights()
    {
        if(courseStudentsRadio.isSelected())
            return AccessRights.CourseStudents;
        else if(allStudentsRadio.isSelected())
            return AccessRights.AllStudents;
        else if(instructorsRadio.isSelected())
            return AccessRights.Instructors;
        else if(everyoneRadio.isSelected())
            return AccessRights.Everyone;
        else if(privateRadio.isSelected())
            return AccessRights.Private;
        return AccessRights.Everyone;
    }

    /**
     * Gets the number of selected bookmarks and events.
     * 
     * @return the number of selected bookmarks and events.
     */
    private int numberOfSelectedBookmarksEvents()
    {
        int selected = 0;
        if(!bookmarkEventNameTextField.getText().equals(NONE_SELECTED_TEXT))
            selected++;
        if(!bookmarkEventNameTextField2.getText().equals(NONE_SELECTED_TEXT))
            selected++;
        if(!bookmarkEventNameTextField3.getText().equals(NONE_SELECTED_TEXT))
            selected++;
        if(!bookmarkEventNameTextField4.getText().equals(NONE_SELECTED_TEXT))
            selected++;
        if(!bookmarkEventNameTextField5.getText().equals(NONE_SELECTED_TEXT))
            selected++;
        if(!bookmarkEventNameTextField6.getText().equals(NONE_SELECTED_TEXT))
            selected++;
        return selected;
    }
    
    /**
     * Helper method that displays the <code>leaveWithoutSaving()</code> dialog.
     */
    private void close(){
        if(!changed() 
                || appControl.getGeneralService().leaveWithoutSaving(this))
            dispose();
    }
    
    /**
     * Helper method to determine whether or not something has been changed in 
     * the window.
     * @return True if something was changed, false otherwise.
     */
    private boolean changed(){
        //TODO: finish this logic
        if(isEditLessonScreen){
            boolean entriesUnchanged = true;
            for(LessonEntry entry : lessonToEdit.getLessonCollection())
            {
                //if()
            }
            
            return !(lessonNameTextField.getText().
                    equals(lessonToEdit.getName()) && entriesUnchanged);
        }
        else{
            return !(lessonNameTextField.getText().isEmpty() &&
                    bookmarkEventNameLabel.getText().equals("None Selected") &&
                    bookmarkEventNameLabel2.getText().equals("None Selected") &&
                    bookmarkEventNameLabel3.getText().equals("None Selected") &&
                    bookmarkEventNameLabel4.getText().equals("None Selected") &&
                    bookmarkEventNameLabel5.getText().equals("None Selected") &&
                    bookmarkEventNameLabel6.getText().equals("None Selected") &&
                    weatherCameraResourcesRadio.isSelected() &&
                    weatherCameraResourcesRadio2.isSelected() &&
                    weatherCameraResourcesRadio3.isSelected() &&
                    weatherCameraResourcesRadio4.isSelected() &&
                    weatherCameraResourcesRadio5.isSelected() &&
                    weatherCameraResourcesRadio6.isSelected() &&
                    everyoneRadio.isSelected() && 
                    attachedFilesList.getModel().getSize() == 0);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lessonNameTextField = new javax.swing.JTextField();
        lessonLabel = new javax.swing.JLabel();
        lessonToAddPanel = new javax.swing.JPanel();
        bookmarkEventNameLabel = new javax.swing.JLabel();
        bookmarkEventNameTextField = new javax.swing.JTextField();
        selectBookmarkEventButton = new javax.swing.JButton();
        weatherCameraResourcesRadio = new javax.swing.JRadioButton();
        radarMapRadio = new javax.swing.JRadioButton();
        dataplotRadio = new javax.swing.JRadioButton();
        resourceLabel = new javax.swing.JLabel();
        bookmarkEventNameLabel2 = new javax.swing.JLabel();
        bookmarkEventNameTextField2 = new javax.swing.JTextField();
        selectBookmarkEventButton2 = new javax.swing.JButton();
        resourceLabel2 = new javax.swing.JLabel();
        weatherCameraResourcesRadio2 = new javax.swing.JRadioButton();
        radarMapRadio2 = new javax.swing.JRadioButton();
        dataplotRadio2 = new javax.swing.JRadioButton();
        bookmarkEventNameLabel3 = new javax.swing.JLabel();
        bookmarkEventNameTextField3 = new javax.swing.JTextField();
        selectBookmarkEventButton3 = new javax.swing.JButton();
        resourceLabel3 = new javax.swing.JLabel();
        weatherCameraResourcesRadio3 = new javax.swing.JRadioButton();
        radarMapRadio3 = new javax.swing.JRadioButton();
        dataplotRadio3 = new javax.swing.JRadioButton();
        bookmarkEventNameLabel4 = new javax.swing.JLabel();
        bookmarkEventNameTextField4 = new javax.swing.JTextField();
        selectBookmarkEventButton4 = new javax.swing.JButton();
        resourceLabel4 = new javax.swing.JLabel();
        weatherCameraResourcesRadio4 = new javax.swing.JRadioButton();
        radarMapRadio4 = new javax.swing.JRadioButton();
        dataplotRadio4 = new javax.swing.JRadioButton();
        bookmarkEventNameLabel5 = new javax.swing.JLabel();
        bookmarkEventNameTextField5 = new javax.swing.JTextField();
        selectBookmarkEventButton5 = new javax.swing.JButton();
        resourceLabel5 = new javax.swing.JLabel();
        weatherCameraResourcesRadio5 = new javax.swing.JRadioButton();
        radarMapRadio5 = new javax.swing.JRadioButton();
        dataplotRadio5 = new javax.swing.JRadioButton();
        bookmarkEventNameLabel6 = new javax.swing.JLabel();
        bookmarkEventNameTextField6 = new javax.swing.JTextField();
        selectBookmarkEventButton6 = new javax.swing.JButton();
        resourceLabel6 = new javax.swing.JLabel();
        weatherCameraResourcesRadio6 = new javax.swing.JRadioButton();
        radarMapRadio6 = new javax.swing.JRadioButton();
        dataplotRadio6 = new javax.swing.JRadioButton();
        closeButton = new javax.swing.JButton();
        createButton = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();
        accessRightsPanel = new javax.swing.JPanel();
        courseStudentsRadio = new javax.swing.JRadioButton();
        allStudentsRadio = new javax.swing.JRadioButton();
        instructorsRadio = new javax.swing.JRadioButton();
        everyoneRadio = new javax.swing.JRadioButton();
        privateRadio = new javax.swing.JRadioButton();
        attachFilesScrollPane = new javax.swing.JScrollPane();
        attachedFilesList = new javax.swing.JList<String>();
        attachFilesButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainPanel.setPreferredSize(new java.awt.Dimension(444, 599));

        lessonLabel.setText("Lesson Name:");

        lessonToAddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Bookmarks/Events To Add"));

        bookmarkEventNameLabel.setText("Phenomenon Name: ");

        bookmarkEventNameTextField.setText("None Selected");

        selectBookmarkEventButton.setText("Select Bookmark/Event");
        selectBookmarkEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBookmarkEventButtonActionPerformed(evt);
            }
        });

        weatherCameraResourcesRadio.setSelected(true);
        weatherCameraResourcesRadio.setText("WeatherCamera");

        radarMapRadio.setText("Radar Map");

        dataplotRadio.setText("Dataplot");

        resourceLabel.setText("Resource:");

        bookmarkEventNameLabel2.setText("Phenomenon Name: ");

        bookmarkEventNameTextField2.setText("None Selected");

        selectBookmarkEventButton2.setText("Select Bookmark/Event");
        selectBookmarkEventButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBookmarkEventButton2ActionPerformed(evt);
            }
        });

        resourceLabel2.setText("Resource:");

        weatherCameraResourcesRadio2.setSelected(true);
        weatherCameraResourcesRadio2.setText("WeatherCamera");

        radarMapRadio2.setText("Radar Map");

        dataplotRadio2.setText("Dataplot");

        bookmarkEventNameLabel3.setText("Phenomenon Name: ");

        bookmarkEventNameTextField3.setText("None Selected");

        selectBookmarkEventButton3.setText("Select Bookmark/Event");
        selectBookmarkEventButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBookmarkEventButton3ActionPerformed(evt);
            }
        });

        resourceLabel3.setText("Resource:");

        weatherCameraResourcesRadio3.setSelected(true);
        weatherCameraResourcesRadio3.setText("WeatherCamera");

        radarMapRadio3.setText("Radar Map");

        dataplotRadio3.setText("Dataplot");

        bookmarkEventNameLabel4.setText("Phenomenon Name: ");

        bookmarkEventNameTextField4.setText("None Selected");

        selectBookmarkEventButton4.setText("Select Bookmark/Event");
        selectBookmarkEventButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBookmarkEventButton4ActionPerformed(evt);
            }
        });

        resourceLabel4.setText("Resource:");

        weatherCameraResourcesRadio4.setSelected(true);
        weatherCameraResourcesRadio4.setText("WeatherCamera");

        radarMapRadio4.setText("Radar Map");

        dataplotRadio4.setText("Dataplot");

        bookmarkEventNameLabel5.setText("Phenomenon Name: ");

        bookmarkEventNameTextField5.setText("None Selected");

        selectBookmarkEventButton5.setText("Select Bookmark/Event");
        selectBookmarkEventButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBookmarkEventButton5ActionPerformed(evt);
            }
        });

        resourceLabel5.setText("Resource:");

        weatherCameraResourcesRadio5.setSelected(true);
        weatherCameraResourcesRadio5.setText("WeatherCamera");

        radarMapRadio5.setText("Radar Map");

        dataplotRadio5.setText("Dataplot");

        bookmarkEventNameLabel6.setText("Phenomenon Name: ");

        bookmarkEventNameTextField6.setText("None Selected");

        selectBookmarkEventButton6.setText("Select Bookmark/Event");
        selectBookmarkEventButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBookmarkEventButton6ActionPerformed(evt);
            }
        });

        resourceLabel6.setText("Resource:");

        weatherCameraResourcesRadio6.setSelected(true);
        weatherCameraResourcesRadio6.setText("WeatherCamera");

        radarMapRadio6.setText("Radar Map");

        dataplotRadio6.setText("Dataplot");

        javax.swing.GroupLayout lessonToAddPanelLayout = new javax.swing.GroupLayout(lessonToAddPanel);
        lessonToAddPanel.setLayout(lessonToAddPanelLayout);
        lessonToAddPanelLayout.setHorizontalGroup(
            lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bookmarkEventNameLabel)
                            .addComponent(resourceLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(weatherCameraResourcesRadio)
                                .addGap(18, 18, 18)
                                .addComponent(radarMapRadio)
                                .addGap(18, 18, 18)
                                .addComponent(dataplotRadio))
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(bookmarkEventNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectBookmarkEventButton))))
                    .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bookmarkEventNameLabel2)
                            .addComponent(resourceLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(weatherCameraResourcesRadio2)
                                .addGap(18, 18, 18)
                                .addComponent(radarMapRadio2)
                                .addGap(18, 18, 18)
                                .addComponent(dataplotRadio2))
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(bookmarkEventNameTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectBookmarkEventButton2))))
                    .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bookmarkEventNameLabel3)
                            .addComponent(resourceLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(weatherCameraResourcesRadio3)
                                .addGap(18, 18, 18)
                                .addComponent(radarMapRadio3)
                                .addGap(18, 18, 18)
                                .addComponent(dataplotRadio3))
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(bookmarkEventNameTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectBookmarkEventButton3))))
                    .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bookmarkEventNameLabel4)
                            .addComponent(resourceLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(weatherCameraResourcesRadio4)
                                .addGap(18, 18, 18)
                                .addComponent(radarMapRadio4)
                                .addGap(18, 18, 18)
                                .addComponent(dataplotRadio4))
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(bookmarkEventNameTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectBookmarkEventButton4))))
                    .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bookmarkEventNameLabel5)
                            .addComponent(resourceLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(weatherCameraResourcesRadio5)
                                .addGap(18, 18, 18)
                                .addComponent(radarMapRadio5)
                                .addGap(18, 18, 18)
                                .addComponent(dataplotRadio5))
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(bookmarkEventNameTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectBookmarkEventButton5))))
                    .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bookmarkEventNameLabel6)
                            .addComponent(resourceLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(bookmarkEventNameTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectBookmarkEventButton6))
                            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                                .addComponent(weatherCameraResourcesRadio6)
                                .addGap(18, 18, 18)
                                .addComponent(radarMapRadio6)
                                .addGap(18, 18, 18)
                                .addComponent(dataplotRadio6)))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        lessonToAddPanelLayout.setVerticalGroup(
            lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lessonToAddPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookmarkEventNameLabel)
                    .addComponent(bookmarkEventNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectBookmarkEventButton))
                .addGap(8, 8, 8)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weatherCameraResourcesRadio)
                    .addComponent(radarMapRadio)
                    .addComponent(dataplotRadio)
                    .addComponent(resourceLabel))
                .addGap(18, 18, 18)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookmarkEventNameLabel2)
                    .addComponent(bookmarkEventNameTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectBookmarkEventButton2))
                .addGap(8, 8, 8)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weatherCameraResourcesRadio2)
                    .addComponent(radarMapRadio2)
                    .addComponent(dataplotRadio2)
                    .addComponent(resourceLabel2))
                .addGap(18, 18, 18)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookmarkEventNameLabel3)
                    .addComponent(bookmarkEventNameTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectBookmarkEventButton3))
                .addGap(8, 8, 8)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weatherCameraResourcesRadio3)
                    .addComponent(radarMapRadio3)
                    .addComponent(dataplotRadio3)
                    .addComponent(resourceLabel3))
                .addGap(18, 18, 18)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookmarkEventNameLabel4)
                    .addComponent(bookmarkEventNameTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectBookmarkEventButton4))
                .addGap(8, 8, 8)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weatherCameraResourcesRadio4)
                    .addComponent(radarMapRadio4)
                    .addComponent(dataplotRadio4)
                    .addComponent(resourceLabel4))
                .addGap(18, 18, 18)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookmarkEventNameLabel5)
                    .addComponent(bookmarkEventNameTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectBookmarkEventButton5))
                .addGap(8, 8, 8)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weatherCameraResourcesRadio5)
                    .addComponent(radarMapRadio5)
                    .addComponent(dataplotRadio5)
                    .addComponent(resourceLabel5))
                .addGap(18, 18, 18)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookmarkEventNameLabel6)
                    .addComponent(bookmarkEventNameTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectBookmarkEventButton6))
                .addGap(8, 8, 8)
                .addGroup(lessonToAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weatherCameraResourcesRadio6)
                    .addComponent(radarMapRadio6)
                    .addComponent(dataplotRadio6)
                    .addComponent(resourceLabel6))
                .addGap(41, 41, 41))
        );

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        createButton.setText("Create");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        errorLabel.setForeground(new java.awt.Color(204, 0, 0));
        errorLabel.setText("Error: You must select at least two bookmarks/events.");

        accessRightsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Access Rights"));

        courseStudentsRadio.setText("Course Students");

        allStudentsRadio.setText("All Students");

        instructorsRadio.setText("Instructors");

        everyoneRadio.setText("Everyone");

        privateRadio.setText("Private");

        javax.swing.GroupLayout accessRightsPanelLayout = new javax.swing.GroupLayout(accessRightsPanel);
        accessRightsPanel.setLayout(accessRightsPanelLayout);
        accessRightsPanelLayout.setHorizontalGroup(
            accessRightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accessRightsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(accessRightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(everyoneRadio)
                    .addComponent(courseStudentsRadio))
                .addGap(46, 46, 46)
                .addGroup(accessRightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(accessRightsPanelLayout.createSequentialGroup()
                        .addComponent(privateRadio)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(accessRightsPanelLayout.createSequentialGroup()
                        .addComponent(allStudentsRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                        .addComponent(instructorsRadio)))
                .addGap(42, 42, 42))
        );
        accessRightsPanelLayout.setVerticalGroup(
            accessRightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accessRightsPanelLayout.createSequentialGroup()
                .addGroup(accessRightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(allStudentsRadio)
                    .addComponent(instructorsRadio)
                    .addComponent(everyoneRadio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(accessRightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(privateRadio)
                    .addComponent(courseStudentsRadio))
                .addContainerGap())
        );

        attachFilesScrollPane.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Files"));

        attachFilesScrollPane.setViewportView(attachedFilesList);

        attachFilesButton.setText("Attach File(s)");
        attachFilesButton.setEnabled(false);
        attachFilesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachFilesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(createButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(accessRightsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(attachFilesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(errorLabel)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lessonToAddPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                    .addGap(9, 9, 9)
                                    .addComponent(lessonLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lessonNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(8, 8, 8)))
                            .addComponent(attachFilesButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lessonNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lessonLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lessonToAddPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accessRightsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(attachFilesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(attachFilesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(closeButton)
                    .addComponent(createButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addGap(0, 26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 772, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectBookmarkEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBookmarkEventButtonActionPerformed

        OpenManageBookmarkDialog getPhenomenonWindow = new OpenManageBookmarkDialog(appControl,
                BookmarkDuration.instance, null, true);
        selectedPhenomenon1 = getPhenomenonWindow.getSelectedPhenomenon();
        
        if(selectedPhenomenon1!= null)
        {
            bookmarkEventNameTextField.setText(selectedPhenomenon1.getName());
            bookmarkEventNameTextField.setEnabled(true);
            bookmarkEventNameTextField.setEditable(true);
        }
        else
        {
            bookmarkEventNameTextField.setText(NONE_SELECTED_TEXT);
            bookmarkEventNameTextField.setEnabled(false);
            bookmarkEventNameTextField.setEditable(false);
        }
        getPhenomenonWindow.dispose();  
    }//GEN-LAST:event_selectBookmarkEventButtonActionPerformed

    private void selectBookmarkEventButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBookmarkEventButton2ActionPerformed
        OpenManageBookmarkDialog getPhenomenonWindow = new OpenManageBookmarkDialog(appControl,
                BookmarkDuration.instance, null, true);
        selectedPhenomenon2 = getPhenomenonWindow.getSelectedPhenomenon();
        
        if(selectedPhenomenon2!= null)
        {
            bookmarkEventNameTextField2.setText(selectedPhenomenon2.getName());
            bookmarkEventNameTextField2.setEnabled(true);
            bookmarkEventNameTextField2.setEditable(true);
        }
        else
        {
            bookmarkEventNameTextField2.setText(NONE_SELECTED_TEXT);
            bookmarkEventNameTextField2.setEnabled(false);
            bookmarkEventNameTextField2.setEditable(false);
        }
        getPhenomenonWindow.dispose();  
    }//GEN-LAST:event_selectBookmarkEventButton2ActionPerformed

    private void selectBookmarkEventButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBookmarkEventButton3ActionPerformed
        OpenManageBookmarkDialog getPhenomenonWindow = new OpenManageBookmarkDialog(appControl,
                BookmarkDuration.instance, null, true);
        selectedPhenomenon3 = getPhenomenonWindow.getSelectedPhenomenon();
        
        if(selectedPhenomenon3!= null)
        {
            bookmarkEventNameTextField3.setText(selectedPhenomenon3.getName());
            bookmarkEventNameTextField3.setEnabled(true);
            bookmarkEventNameTextField3.setEditable(true);
        }
        else
        {
            bookmarkEventNameTextField3.setText(NONE_SELECTED_TEXT);
            bookmarkEventNameTextField3.setEnabled(false);
            bookmarkEventNameTextField3.setEditable(false);
        }
        getPhenomenonWindow.dispose();  
    }//GEN-LAST:event_selectBookmarkEventButton3ActionPerformed

    private void selectBookmarkEventButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBookmarkEventButton4ActionPerformed
        OpenManageBookmarkDialog getPhenomenonWindow = new OpenManageBookmarkDialog(appControl,
                BookmarkDuration.instance, null, true);
        selectedPhenomenon4 = getPhenomenonWindow.getSelectedPhenomenon();
        
        if(selectedPhenomenon4!= null)
        {
            bookmarkEventNameTextField4.setText(selectedPhenomenon4.getName());
            bookmarkEventNameTextField4.setEnabled(true);
            bookmarkEventNameTextField4.setEditable(true);
        }
        else
        {
            bookmarkEventNameTextField4.setText(NONE_SELECTED_TEXT);
            bookmarkEventNameTextField4.setEnabled(false);
            bookmarkEventNameTextField4.setEditable(false);
        }
        getPhenomenonWindow.dispose();  
    }//GEN-LAST:event_selectBookmarkEventButton4ActionPerformed

    private void selectBookmarkEventButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBookmarkEventButton5ActionPerformed
        OpenManageBookmarkDialog getPhenomenonWindow = new OpenManageBookmarkDialog(appControl,
                BookmarkDuration.instance, null, true);
        selectedPhenomenon5 = getPhenomenonWindow.getSelectedPhenomenon();
        
        if(selectedPhenomenon5!= null)
        {
            bookmarkEventNameTextField5.setText(selectedPhenomenon5.getName());
            bookmarkEventNameTextField5.setEnabled(true);
            bookmarkEventNameTextField5.setEditable(true);
        }
        else
        {
            bookmarkEventNameTextField5.setText(NONE_SELECTED_TEXT);
            bookmarkEventNameTextField5.setEnabled(false);
            bookmarkEventNameTextField5.setEditable(false);
        }
        getPhenomenonWindow.dispose();  
    }//GEN-LAST:event_selectBookmarkEventButton5ActionPerformed

    private void selectBookmarkEventButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBookmarkEventButton6ActionPerformed
        OpenManageBookmarkDialog getPhenomenonWindow = new OpenManageBookmarkDialog(appControl,
                BookmarkDuration.instance, null, true);
        selectedPhenomenon6 = getPhenomenonWindow.getSelectedPhenomenon();
        
        if(selectedPhenomenon6!= null)
        {
            bookmarkEventNameTextField6.setText(selectedPhenomenon6.getName());
            bookmarkEventNameTextField6.setEnabled(true);
            bookmarkEventNameTextField6.setEditable(true);
        }
        else
        {
            bookmarkEventNameTextField6.setText(NONE_SELECTED_TEXT);
            bookmarkEventNameTextField6.setEnabled(false);
            bookmarkEventNameTextField6.setEditable(false);
        }
        getPhenomenonWindow.dispose();  
    }//GEN-LAST:event_selectBookmarkEventButton6ActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        close();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
       if(numberOfSelectedBookmarksEvents() < 2){
           errorLabel.setText(NOT_ENOUGH_PHENOMS_SELECTED_ERROR);
           errorLabel.setVisible(true);
       }
       else if(lessonNameTextField.getText().trim().isEmpty())
       {
           errorLabel.setText(NO_LESSON_NAME_ERROR);
           errorLabel.setVisible(true);
       }
       else
       {
           ArrayList<LessonEntry> newEntries = new ArrayList<LessonEntry>();
           ArrayList<LessonEntry> updateEntries = new ArrayList<LessonEntry>();
           ArrayList<LessonEntry> deleteEntries = new ArrayList<LessonEntry>();
           Vector<LessonEntry> existingEntries;
           if(isEditLessonScreen)
               existingEntries = (Vector<LessonEntry>) lessonToEdit.getLessonCollection();
           else
               existingEntries = new Vector<LessonEntry>();
           int i = 1;
           if(selectedPhenomenon1 !=null)
           {
               if(existingEntries.size()>=i){
                   LessonEntry entry = existingEntries.get(i-1);
                   entry.setLessonEntryName(bookmarkEventNameTextField.getText());
                   entry.setBookmarkNumber(selectedPhenomenon1.getBookmarkNumber());
                   entry.setBookmarkResourceID(getWeatherResourceType(i));
                   entry.setPreferredWindowLocation(i);
                   updateEntries.add(entry);
               }
               else
                   newEntries.add(new LessonEntry(bookmarkEventNameTextField.getText(), 
                       selectedPhenomenon1.getBookmarkNumber(), getWeatherResourceType(i), i));
               
           }
           else if(existingEntries.size()>=i){
               deleteEntries.add(existingEntries.get(i-1));
           }
           i++;
           if(selectedPhenomenon2 !=null)
           {
               if(existingEntries.size()>=i){
                   LessonEntry entry = existingEntries.get(i-1);
                   entry.setLessonEntryName(bookmarkEventNameTextField2.getText());
                   entry.setBookmarkNumber(selectedPhenomenon2.getBookmarkNumber());
                   entry.setBookmarkResourceID(getWeatherResourceType(i));
                   entry.setPreferredWindowLocation(i);
                   updateEntries.add(entry);
               }
               else
                   newEntries.add(new LessonEntry(bookmarkEventNameTextField2.getText(), 
                       selectedPhenomenon2.getBookmarkNumber(), getWeatherResourceType(i), i));
           }
           else if(existingEntries.size()>=i){
               deleteEntries.add(existingEntries.get(i-1));
           }
           i++;
           if(selectedPhenomenon3 !=null)
           {
               if(existingEntries.size()>=i){
                   LessonEntry entry = existingEntries.get(i-1);
                   entry.setLessonEntryName(bookmarkEventNameTextField3.getText());
                   entry.setBookmarkNumber(selectedPhenomenon3.getBookmarkNumber());
                   entry.setBookmarkResourceID(getWeatherResourceType(i));
                   entry.setPreferredWindowLocation(i);
                   updateEntries.add(entry);
               }
               else
                   newEntries.add(new LessonEntry(bookmarkEventNameTextField3.getText(), 
                       selectedPhenomenon3.getBookmarkNumber(), getWeatherResourceType(i), i));
           }
           else if(existingEntries.size()>=i){
               deleteEntries.add(existingEntries.get(i-1));
           }
           i++;
           if(selectedPhenomenon4 !=null)
           {
               if(existingEntries.size()>=i){
                   LessonEntry entry = existingEntries.get(i-1);
                   entry.setLessonEntryName(bookmarkEventNameTextField4.getText());
                   entry.setBookmarkNumber(selectedPhenomenon4.getBookmarkNumber());
                   entry.setBookmarkResourceID(getWeatherResourceType(i));
                   entry.setPreferredWindowLocation(i);
                   updateEntries.add(entry);
               }
               else
                   newEntries.add(new LessonEntry(bookmarkEventNameTextField4.getText(), 
                       selectedPhenomenon4.getBookmarkNumber(), getWeatherResourceType(i), i));
           } 
           else if(existingEntries.size()>=i){
               deleteEntries.add(existingEntries.get(i-1));
           }
           i++;
           if(selectedPhenomenon5 !=null)
           {
               if(existingEntries.size()>=i){
                   LessonEntry entry = existingEntries.get(i-1);
                   entry.setLessonEntryName(bookmarkEventNameTextField5.getText());
                   entry.setBookmarkNumber(selectedPhenomenon5.getBookmarkNumber());
                   entry.setBookmarkResourceID(getWeatherResourceType(i));
                   entry.setPreferredWindowLocation(i);
                   updateEntries.add(entry);
               }
               else
                   newEntries.add(new LessonEntry(bookmarkEventNameTextField5.getText(), 
                       selectedPhenomenon5.getBookmarkNumber(), getWeatherResourceType(i), i));
           }
           else if(existingEntries.size()>=i){
               deleteEntries.add(existingEntries.get(i-1));
           }
           i++;
           if(selectedPhenomenon6 !=null)
           {
               if(existingEntries.size()>=i){
                   LessonEntry entry = existingEntries.get(i-1);
                   entry.setLessonEntryName(bookmarkEventNameTextField6.getText());
                   entry.setBookmarkNumber(selectedPhenomenon6.getBookmarkNumber());
                   entry.setBookmarkResourceID(getWeatherResourceType(i));
                   entry.setPreferredWindowLocation(i);
                   updateEntries.add(entry);
               }
               else
                   newEntries.add(new LessonEntry(bookmarkEventNameTextField6.getText(), 
                       selectedPhenomenon6.getBookmarkNumber(), getWeatherResourceType(i), i));
           }
           else if(existingEntries.size()>=i){
               deleteEntries.add(existingEntries.get(i-1));
           }
           
           boolean success;
            lessonToEdit.setName(lessonNameTextField.getText());
            lessonToEdit.setInstructorNumber(user.getUserNumber());
            lessonToEdit.setAccessRights(getAccessRights());
            lessonToEdit.setLessonCollection(newEntries);
            lessonToEdit.setLessonCategoryNumber(categoryType);
            if(isEditLessonScreen){
                success = lessonManager.update(lessonToEdit);
            }
            else{
                success = lessonManager.add(lessonToEdit);
            }

            for(LessonEntry entry : newEntries){
                entry.setLessonNumber(lessonToEdit.getLessonNumber());
                success = entryManager.add(entry) && success;
            }

            for(LessonEntry entry : updateEntries){
                entry.setLessonNumber(lessonToEdit.getLessonNumber());
                success = entryManager.update(entry) && success;
            }

            for(LessonEntry entry : deleteEntries)
                success = entryManager.delete(entry) && success;

            if(success)
                JOptionPane.showMessageDialog(this, "Lesson saved successfully.",
                                "Lesson Added", JOptionPane.INFORMATION_MESSAGE);
           
           this.dispose();
       }
    }//GEN-LAST:event_createButtonActionPerformed

    private void attachFilesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachFilesButtonActionPerformed
        loadAttachmentFile();
    }//GEN-LAST:event_attachFilesButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing

    /**
     * Loads a file into the Lesson. 
     * This method was taken and modified from CreateInstructorNoteWindow.
     * 
     * @return True if the load was successful, false otherwise.
     */
    private boolean loadAttachmentFile() {
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                new File(CommonLocalFileManager.getRootDirectory()),
                "Attach File", this);
        if (file == null) {
            return false;
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

            LessonFileInstance lessonFileInstance = new LessonFileInstance(
                    -1, -1, user.getUserNumber(), file.getName(), fileData);
            attachedFiles.add(lessonFileInstance);
            fileListModel.addElement(lessonFileInstance.getFileName());
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to load file.");
            WeatherLogger.log(Level.SEVERE, "Unable to load file.");
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An I/O error has occurred.");
            WeatherLogger.log(Level.SEVERE, "I/O error :" + e.getMessage());
            return false;
        }
        return true;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel accessRightsPanel;
    private javax.swing.JRadioButton allStudentsRadio;
    private javax.swing.JButton attachFilesButton;
    private javax.swing.JScrollPane attachFilesScrollPane;
    private javax.swing.JList<String> attachedFilesList;
    private javax.swing.JLabel bookmarkEventNameLabel;
    private javax.swing.JLabel bookmarkEventNameLabel2;
    private javax.swing.JLabel bookmarkEventNameLabel3;
    private javax.swing.JLabel bookmarkEventNameLabel4;
    private javax.swing.JLabel bookmarkEventNameLabel5;
    private javax.swing.JLabel bookmarkEventNameLabel6;
    private javax.swing.JTextField bookmarkEventNameTextField;
    private javax.swing.JTextField bookmarkEventNameTextField2;
    private javax.swing.JTextField bookmarkEventNameTextField3;
    private javax.swing.JTextField bookmarkEventNameTextField4;
    private javax.swing.JTextField bookmarkEventNameTextField5;
    private javax.swing.JTextField bookmarkEventNameTextField6;
    private javax.swing.JButton closeButton;
    private javax.swing.JRadioButton courseStudentsRadio;
    private javax.swing.JButton createButton;
    private javax.swing.JRadioButton dataplotRadio;
    private javax.swing.JRadioButton dataplotRadio2;
    private javax.swing.JRadioButton dataplotRadio3;
    private javax.swing.JRadioButton dataplotRadio4;
    private javax.swing.JRadioButton dataplotRadio5;
    private javax.swing.JRadioButton dataplotRadio6;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JRadioButton everyoneRadio;
    private javax.swing.JRadioButton instructorsRadio;
    private javax.swing.JLabel lessonLabel;
    private javax.swing.JTextField lessonNameTextField;
    private javax.swing.JPanel lessonToAddPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButton privateRadio;
    private javax.swing.JRadioButton radarMapRadio;
    private javax.swing.JRadioButton radarMapRadio2;
    private javax.swing.JRadioButton radarMapRadio3;
    private javax.swing.JRadioButton radarMapRadio4;
    private javax.swing.JRadioButton radarMapRadio5;
    private javax.swing.JRadioButton radarMapRadio6;
    private javax.swing.JLabel resourceLabel;
    private javax.swing.JLabel resourceLabel2;
    private javax.swing.JLabel resourceLabel3;
    private javax.swing.JLabel resourceLabel4;
    private javax.swing.JLabel resourceLabel5;
    private javax.swing.JLabel resourceLabel6;
    private javax.swing.JButton selectBookmarkEventButton;
    private javax.swing.JButton selectBookmarkEventButton2;
    private javax.swing.JButton selectBookmarkEventButton3;
    private javax.swing.JButton selectBookmarkEventButton4;
    private javax.swing.JButton selectBookmarkEventButton5;
    private javax.swing.JButton selectBookmarkEventButton6;
    private javax.swing.JRadioButton weatherCameraResourcesRadio;
    private javax.swing.JRadioButton weatherCameraResourcesRadio2;
    private javax.swing.JRadioButton weatherCameraResourcesRadio3;
    private javax.swing.JRadioButton weatherCameraResourcesRadio4;
    private javax.swing.JRadioButton weatherCameraResourcesRadio5;
    private javax.swing.JRadioButton weatherCameraResourcesRadio6;
    // End of variables declaration//GEN-END:variables
}
