package weather.clientside.gui.administrator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import weather.ApplicationControlSystem;
import weather.clientside.gui.client.LessonAddEditWindow;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.lesson.Lesson;
import weather.common.data.lesson.LessonEntry;
import weather.common.dbms.DBMSLessonCategoryManager;
import weather.common.dbms.DBMSLessonEntryManager;
import weather.common.dbms.DBMSLessonManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.gui.component.BUDialog;

/**
 * The <code>ListLessons</code> creates a form that 
 * lists all lessons available.
 * @author Ty Vanderstappen (2012)
 * @author Justin Enslin (2012)
 * @version 2012
 */
public class ManageCombinedBookmarkLessons extends BUDialog {

    //used to determine how many columns in the table there are
    private static int TABLE_LENGTH = 3;
    public static int LESSON_COLUMN = 0;
    public static int OWNER_COLUMN = 1;
    public static int ACCESS_COLUMN = 2;
    

    private MyDefaultTableModel lessonModel;
    private TableRowSorter<MyDefaultTableModel> lessonSorter;
    private Vector<RowFilter<MyDefaultTableModel, Object>> filters;
    
    private DBMSUserManager userManager;
    private DBMSLessonManager lessonManager;
    private DBMSLessonEntryManager entryManager;
    private DBMSLessonCategoryManager categoryManager;
    
    private SearchLessons searchWindow;
    private Vector<Lesson> lessons;
    private int categoryType;
    
    /**
     * Creates a new Manage Lessons window displaying all lessons the
     * current user can edit.
     * 
     * @param appControl The application control system.
     */
    public ManageCombinedBookmarkLessons(ApplicationControlSystem appControl, 
            int categoryType) 
    {
        super(appControl);
        initComponents();
        initialize(appControl, categoryType);
        filters = new Vector<RowFilter<MyDefaultTableModel, Object>>();
        super.postInitialize(false);
    }
    
    /**
     * Helper method containing common initialization code.
     * 
     * @param appControl The application control system.
     */
    private void initialize(final ApplicationControlSystem appControl, 
            final int categoryType){
        this.appControl = appControl;
        this.categoryType = categoryType;
        userManager = appControl.getDBMSSystem().getUserManager();
        lessonManager = appControl.getDBMSSystem().getLessonManager();
        categoryManager = appControl.getDBMSSystem().getLessonCategoryManager();
        entryManager = appControl.getDBMSSystem().getLessonEntryManager();
        setTitle("Weather Viewer - Manage " + categoryManager.
                get(categoryType).getCategoryName());
        filters = new Vector<RowFilter<MyDefaultTableModel, Object>>();
        lessonTable.getTableHeader().setResizingAllowed(false);
        lessonTable.getTableHeader().setReorderingAllowed(false);
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
                    new LessonAddEditWindow(appControl, categoryType, 
                            (Lesson)target.getValueAt(
                            target.getSelectedRow(), LESSON_COLUMN));
                
            }
        };
        lessonTable.addMouseListener(doubleClick);
    }
    
    /**
     * Updates the table.
     */
    private void updateTable() {
        User currentUser = appControl.getGeneralService().getUser();
        Vector<LessonEntry> entries = entryManager.obtainAll();
        Vector<Lesson> temp;
        lessons = new Vector<Lesson>();
        if(currentUser.getUserType() == UserType.administrator)
            temp = lessonManager.obtainAll();
        else if(currentUser.getUserType() == UserType.instructor)
            temp = lessonManager.obtainByUser(currentUser.getUserNumber());
        else
            temp = new Vector<Lesson>();
        
        for(Lesson lesson : temp){
            if(lesson.getLessonCategoryNumber() == categoryType){
                lesson.setLessonCollection(entryManager.
                        obtainByLessonNumber(lesson.getLessonNumber()));
                lessons.add(lesson);
            }
        }
        
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
        lessonTable.getColumnModel().getColumn(ACCESS_COLUMN).
                setHeaderValue("Access");
        
        //Add lessons
        for(int i=0; i<lessons.size(); i++){
            Lesson lesson = lessons.get(i);
            lessonTable.setValueAt(lesson, i, LESSON_COLUMN);
            lessonTable.setValueAt(userManager.obtainUser(
                    lesson.getInstructorNumber()).getLoginId(), i, OWNER_COLUMN);
            lessonTable.setValueAt(lesson.getAccessRights().toString(), i, 
                    ACCESS_COLUMN);
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
        addButton = new javax.swing.JButton();
        editLesson = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        deleteLesson = new javax.swing.JButton();
        editLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();
        manageCategoriesButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Weather Viewer - Manage Lessons");
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

        addButton.setText("Add Lesson");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editLesson.setText("Edit Lesson");
        editLesson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLessonActionPerformed(evt);
            }
        });

        searchButton.setText("Search Lessons");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        deleteLesson.setText("Delete Lesson(s)");
        deleteLesson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteLessonActionPerformed(evt);
            }
        });

        editLabel.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        editLabel.setText("Double click to edit a lesson.");

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        errorLabel.setForeground(new java.awt.Color(255, 0, 51));
        errorLabel.setText("Must select lesson(s)");

        manageCategoriesButton.setText("Manage Categories");
        manageCategoriesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageCategoriesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lessonPanelLayout = new javax.swing.GroupLayout(lessonPanel);
        lessonPanel.setLayout(lessonPanelLayout);
        lessonPanelLayout.setHorizontalGroup(
            lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lessonPanelLayout.createSequentialGroup()
                .addGroup(lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lessonScrollPanel)
                    .addGroup(lessonPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(lessonPanelLayout.createSequentialGroup()
                                .addComponent(addButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editLesson))
                            .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(deleteLesson, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(manageCategoriesButton))
                        .addGroup(lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lessonPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(errorLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(lessonPanelLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(editLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addComponent(closeButton)))))
                .addContainerGap())
        );
        lessonPanelLayout.setVerticalGroup(
            lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lessonPanelLayout.createSequentialGroup()
                .addComponent(lessonScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(editLesson)
                    .addComponent(manageCategoriesButton)
                    .addComponent(errorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lessonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(searchButton)
                    .addComponent(deleteLesson)
                    .addComponent(editLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lessonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lessonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * LessonAddEditWindow will be brought up if the user wants to add a new
     * lesson.
     * @param evt 
     */
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        new LessonAddEditWindow(appControl, categoryType);
    }//GEN-LAST:event_addButtonActionPerformed

    /**
     * Edit Lesson window opens.
     * @param evt 
     */
    private void editLessonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLessonActionPerformed
        if(lessonTable.getSelectedRowCount() == 0){
            errorLabel.setVisible(true);
            return;
        }
        errorLabel.setVisible(false);
        Lesson selectedLesson = ((Lesson)lessonModel.getValueAt(
                lessonTable.getSelectedRow(), LESSON_COLUMN));
        new LessonAddEditWindow(appControl, categoryType, selectedLesson);
    }//GEN-LAST:event_editLessonActionPerformed

    /**
     * Deletes selected lessons.
     * @param evt 
     */
    private void deleteLessonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLessonActionPerformed
        if(lessonTable.getSelectedRowCount() == 0){
            errorLabel.setVisible(true);
            return;
        }
        errorLabel.setVisible(false);
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to"
                + " remove\nthe selected lessons?", "Delete Lessons", 
                JOptionPane.YES_NO_OPTION);
        if(ans != JOptionPane.YES_OPTION) return;
        for(int i = 0; i < lessonTable.getSelectedRowCount(); i++){
            Lesson toDelete = (Lesson)lessonTable.getModel().getValueAt(
                    lessonTable.getSelectedRows()[i], LESSON_COLUMN);
            for(LessonEntry entry:entryManager.obtainByLessonNumber(
                    toDelete.getLessonNumber()))
                entryManager.delete(entry);
            lessonManager.delete(toDelete);
        }
    }//GEN-LAST:event_deleteLessonActionPerformed

    /**
     * The Manage Lessons window will close.
     * @param evt 
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * The Search Lessons window will pop up.
     * @param evt 
     */
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        if(searchWindow == null){
            searchWindow = new SearchLessons(super.appControl, this, 
                    categoryType);
        }
        else searchWindow.setVisible(true);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        updateTable();
    }//GEN-LAST:event_formWindowGainedFocus

    private void manageCategoriesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageCategoriesButtonActionPerformed
        new ManageLessonCategoryDialog(appControl);
    }//GEN-LAST:event_manageCategoriesButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteLesson;
    private javax.swing.JLabel editLabel;
    private javax.swing.JButton editLesson;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JPanel lessonPanel;
    private javax.swing.JScrollPane lessonScrollPanel;
    private javax.swing.JTable lessonTable;
    private javax.swing.JButton manageCategoriesButton;
    private javax.swing.JButton searchButton;
    // End of variables declaration//GEN-END:variables
}
