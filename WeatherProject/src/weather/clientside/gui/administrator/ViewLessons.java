package weather.clientside.gui.administrator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import weather.ApplicationControlSystem;
import weather.clientside.gui.client.LessonDisplay;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.User;
import weather.common.data.lesson.Lesson;
import weather.common.data.lesson.LessonEntry;
import weather.common.dbms.DBMSLessonCategoryManager;
import weather.common.dbms.DBMSLessonEntryManager;
import weather.common.dbms.DBMSLessonManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.Debug;

/**
 * The <code>ViewLessons</code> creates a form that 
 * lists all lessons available in a specified category
 * @author Ty Vanderstappen (2012)
 * @author Justin Enslin (2012)
 * @version 2012
 */
public class ViewLessons extends BUDialog {

    //used to determine how many columns in the table there are
    private static int TABLE_LENGTH = 2;
    public static int LESSON_COLUMN = 0;
    public static int OWNER_COLUMN = 1;
    
    private MyDefaultTableModel lessonModel;
    private TableRowSorter<MyDefaultTableModel> lessonSorter;
    private Vector<RowFilter<MyDefaultTableModel, Object>> filters;
    
    private DBMSUserManager userManager;
    private DBMSLessonManager lessonManager;
    private DBMSLessonCategoryManager categoryManager;
    private DBMSLessonEntryManager entryManager;
    
    private SearchLessons searchWindow;
    private Vector<Lesson> lessons;
    private int categoryType;
    
    /**
     * Creates a new Manage Lessons window displaying all lessons the
     * current user can edit.
     * 
     * @param appControl The application control system.
     */
    public ViewLessons(ApplicationControlSystem appControl, int category) 
    {
        super(appControl);
        categoryType = category;
        initComponents();
        initialize(appControl);
        filters = new Vector<RowFilter<MyDefaultTableModel, Object>>();
        super.postInitialize(false);
    }
    
    /**
     * Creates a new Manage Lessons window displaying all lessons the current 
     * user can edit that also meet the criteria in the given row filters.
     * 
     * @param appControl The application control system.
     * @param searchWindow The Search Lessons window.
     * @param category The categories number.
     */
    public ViewLessons(ApplicationControlSystem appControl, 
            SearchLessons searchWindow, int category){
        super(appControl);
        categoryType = category;
        initComponents();
        initialize(appControl);
        this.searchWindow = searchWindow;
        this.filters = searchWindow.getFilters();
        super.postInitialize(false);
    }
    
    /**
     * Helper method containing common initialization code.
     * 
     * @param appControl The application control system.
     */
    private void initialize(ApplicationControlSystem appControl){
        this.appControl = appControl;
        userManager = appControl.getDBMSSystem().getUserManager();
        lessonManager = appControl.getDBMSSystem().getLessonManager();
        categoryManager = appControl.getDBMSSystem().getLessonCategoryManager();
        entryManager = appControl.getDBMSSystem().getLessonEntryManager();
        filters = new Vector<RowFilter<MyDefaultTableModel, Object>>();
        
        setTitle("Weather Viewer - View " + categoryManager.get(categoryType).
                getCategoryName());
        updateTable();
        errorLabel.setVisible(false);
        MouseAdapter doubleClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();

                /*
                 * If the user double clicks anywhere in the table it 
                 * will take the user to the screen to edit the user they clicked on
                 */
                if (e.getClickCount() == 2)
                    openLesson();
            }
        };
        lessonTable.addMouseListener(doubleClick);
    }
    /**
     * Opens the currently selected Lesson.
     */
    private void openLesson(){
        Debug.println("Opening the selected lesson.");
        
        Lesson lessonToOpen = (Lesson)lessonTable.getValueAt
                (lessonTable.getSelectedRow(), LESSON_COLUMN);
        LessonDisplay lessonDisplay = new LessonDisplay(lessonToOpen,appControl);
        
    }
    
    /**
     * Updates the table.
     */
    private void updateTable() {
        User currentUser = appControl.getGeneralService().getUser();
        Vector<LessonEntry> entries = entryManager.obtainAll();
        Vector<Lesson> temp;
        lessons = lessonManager.obtainAll();
        
        //Set column model and sorter
        lessonModel = new MyDefaultTableModel(0, 0);
        lessonModel.setColumnCount(TABLE_LENGTH);
        lessonModel.setRowCount(lessons.size());
        lessonTable.setModel(lessonModel);
        lessonSorter = new TableRowSorter<MyDefaultTableModel>(lessonModel);
        lessonTable.setRowSorter(lessonSorter);
        
        //Set column names
        lessonTable.getColumnModel().getColumn(LESSON_COLUMN).
                setHeaderValue("Lesson");
        lessonTable.getColumnModel().getColumn(OWNER_COLUMN).
                setHeaderValue("Created By");
        
        //Add lessons
        for(int i=0; i<lessons.size(); i++){
            Lesson lesson = lessons.get(i);
            lessonTable.setValueAt(lesson, i, LESSON_COLUMN);
            lessonTable.setValueAt(userManager.obtainUser(
                    lesson.getInstructorNumber()).getLoginId(), i, OWNER_COLUMN);
        }
        
        applyFilters();
        
        //Grab row filters if search window exists
        
        lessonTable.revalidate();
        validate();
    }
    
    /**
     * Applies the stored filters to the lessons table.
     */
    private void applyFilters(){
        lessonSorter.setRowFilter(RowFilter.andFilter(filters));
    }

    public Vector<RowFilter<MyDefaultTableModel, Object>> getFilters() {
        return filters;
    }

    public void setFilters(Vector<RowFilter<MyDefaultTableModel, Object>> filters) {
        this.filters = filters;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lessonPanel = new javax.swing.JPanel();
        lessonScrollPanel = new javax.swing.JScrollPane();
        lessonTable = new javax.swing.JTable();
        openButton = new javax.swing.JButton();
        openLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Weather Viewer - View Lessons");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        lessonTable.setModel(new javax.swing.table.DefaultTableModel(
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
        lessonScrollPanel.setViewportView(lessonTable);

        openButton.setText("Open Selected Lesson(s)");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        openLabel.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        openLabel.setText("Double click to open a lesson.");

        errorLabel.setForeground(new java.awt.Color(255, 0, 51));
        errorLabel.setText("Must select lesson(s)");

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        searchButton.setText("Search Lessons");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lessonPanelLayout = new javax.swing.GroupLayout(lessonPanel);
        lessonPanel.setLayout(lessonPanelLayout);
        lessonPanelLayout.setHorizontalGroup(
            lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lessonScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lessonPanelLayout.createSequentialGroup()
                .addComponent(openButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorLabel)
                .addGap(128, 128, 128))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lessonPanelLayout.createSequentialGroup()
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(openLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(closeButton))
        );
        lessonPanelLayout.setVerticalGroup(
            lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lessonPanelLayout.createSequentialGroup()
                .addComponent(lessonScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(openButton)
                    .addComponent(errorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(openLabel)
                    .addComponent(searchButton)
                    .addComponent(closeButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lessonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lessonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * LessonAddEditWindow will be brought up if the user wants to add a new
     * lesson.
     * TODO: complete method to allow the user to open one to many lessons
     * @param evt 
     */
    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        openLesson();
    }//GEN-LAST:event_openButtonActionPerformed

    /**
     * The Manage Lessons window will close.
     * @param evt 
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * 
     * @param evt 
     */
    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        updateTable();
    }//GEN-LAST:event_formWindowGainedFocus

    /**
     * TODO: Complete method to allow the user to search/filter the lessons
     * @param evt 
     */
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        if(searchWindow == null){
            searchWindow = new SearchLessons(super.appControl, this, 
                    categoryType);
        }
        else searchWindow.setVisible(true);
    }//GEN-LAST:event_searchButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JPanel lessonPanel;
    private javax.swing.JScrollPane lessonScrollPanel;
    private javax.swing.JTable lessonTable;
    private javax.swing.JButton openButton;
    private javax.swing.JLabel openLabel;
    private javax.swing.JButton searchButton;
    // End of variables declaration//GEN-END:variables
}
