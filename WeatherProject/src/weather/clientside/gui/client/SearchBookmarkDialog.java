package weather.clientside.gui.client;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import weather.ApplicationControlSystem;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.manager.MovieController;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkType;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.dbms.DBMSEnrollmentManager;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.ScreenInteractionUtility;

/**
 * The
 * <code>SearchBookmarkDialog</code> class creates a form that allows for
 * searching for bookmarks.
 *
 * @author kc70024
 * @version 2011
 */
public class SearchBookmarkDialog extends BUDialog {

    private DBMSBookmarkEventTypesManager typesManager;
    private DBMSBookmarkInstanceManager bookmarkManager;
    private Vector<Bookmark> bookmarks;
    private DBMSEnrollmentManager courseManager;
    private Vector<Course> courses;
    private Vector<User> instructors;
    private int userId;
    private User user;
    private String dateTitle;
    private String categoryTitle;
    private MovieController controller;
    private AdvancedSearchBookmarkDialog asbwindow;
    private Vector<BookmarkType> types;
    private Vector<BookmarkCategory> categories;
    private boolean isLocalSearch;

    /**
     * Search dialog for bookmarks stored on the database.
     *
     * @param appControl Control system for the application.
     * @param mc The movie controller.
     * @param isLocalSearch Sets the search criteria to be for local bookmarks
     * instead database stored bookmarks.
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    public SearchBookmarkDialog(ApplicationControlSystem appControl,
            MovieController mc, boolean isLocalSearch, boolean shouldCenter) {
        super(appControl);
        controller = mc;
        typesManager = appControl.getDBMSSystem().getBookmarkTypesManager();
        bookmarkManager = (DBMSBookmarkInstanceManager) appControl.getDBMSSystem().getBookmarkManager();
        types = typesManager.obtainAllbyUserID(userId);
        categories = appControl.getAdministratorControlSystem().getGeneralService()
                .getDBMSSystem().getBookmarkCategoriesManager().obtainAll();
        courseManager = appControl.getDBMSSystem().getEnrollmentManager();
        instructors = new Vector<User>();
        initComponents();
        
        //Sizing and listener
        int width = 498 + this.getInsets().left + this.getInsets().right;
        int height = 176 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        noResultLabel.setVisible(false);
        user = appControl.getGeneralService().getUser();
        initialComboBox();
        userId = user.getUserNumber();
        this.isLocalSearch = isLocalSearch;
        if (user.getUserType() == UserType.student || user.getUserType() == UserType.guest) {
            if (user.getUserType() == UserType.student) {
                courses = courseManager.getCoursesForStudent(user);
            } //The else is for guests, so a guest will get all courses, no matter
            //what.
            else if (user.getUserType() == UserType.guest) {
                courses = appControl.getAdministratorControlSystem()
                        .getGeneralService().getDBMSSystem().getCourseManager()
                        .obtainAllCourses();
            }

            for (Course c : courses) {
                boolean isBroken = false;
                for (User i : instructors) {
                    if (i.getUserNumber() == c.getInstructor().getUserNumber()) {
                        isBroken = true;
                        break;
                    }
                }
                if (!isBroken) {
                    instructors.add(c.getInstructor());
                }
            }
        }
        if (user.getUserType() == UserType.administrator || user.getUserType() == UserType.instructor) {
            instructors = new Vector<User>();
        }
        super.postInitialize(shouldCenter);
    }

    /**
     * Creates the initial ComboBox.
     */
    private void initialComboBox() {
        categoryComboBox.addItem("All");
        User user = appControl.getGeneralService().getUser();
        GUIComponentFactory.initBookmarkCategoryBox(categoryComboBox,
                eventManager, user);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        noResultLabel = new javax.swing.JLabel();
        searchPanel = new javax.swing.JPanel();
        categoryLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        subcategoryComboBox = new javax.swing.JComboBox<String>();
        categoryComboBox = new javax.swing.JComboBox<String>();
        cancelButton = new javax.swing.JButton();
        advancedOptionJButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setTitle("Weather Viewer - Search Bookmark Window");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        noResultLabel.setForeground(new java.awt.Color(255, 0, 51));
        noResultLabel.setIcon(IconProperties.getResourceInactiveIconImage());
        noResultLabel.setText("There are no bookmarks with these conditions");
        getContentPane().add(noResultLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 148, 474, -1));

        searchPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        categoryLabel.setText("Select a Category to Search:");

        typeLabel.setText("Select Bookmark Sub-Category:");

        subcategoryComboBox.setMaximumRowCount(20);
        subcategoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subcategoryComboBoxActionPerformed(evt);
            }
        });

        categoryComboBox.setMaximumRowCount(20);
        categoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryComboBoxActionPerformed(evt);
            }
        });

        cancelButton.setText("Close");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        advancedOptionJButton.setText("Advanced Options ");
        advancedOptionJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedOptionJButtonActionPerformed(evt);
            }
        });

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(categoryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(typeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addComponent(searchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(advancedOptionJButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(subcategoryComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 215, Short.MAX_VALUE)
                        .addComponent(categoryComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryLabel)
                    .addComponent(categoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(subcategoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(cancelButton)
                    .addComponent(advancedOptionJButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(searchPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 474, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        categoryTitle = categoryComboBox.getSelectedItem().toString();
        //Stops unexpected behavior when more than one instance of this window are open.
        this.setModalityType(ModalityType.MODELESS);
        if (!isLocalSearch) {
            String category = (String) categoryComboBox.getSelectedItem();
            if ((asbwindow != null) && asbwindow.get_OK_Cancel()) {
                if (asbwindow.byName()) {
                    searchByName(asbwindow.getSpecifyName());
                    if (!bookmarks.isEmpty()) {
                        this.setVisible(false);
                        new OpenManageBookmarkDialog(appControl,
                                bookmarks, asbwindow.getSpecifyName(), controller, 
                                category);
                    } else {
                        this.noResultLabel.setVisible(true);
                    }
                } else if (asbwindow.byTimeRange()) {
                    searchByCategoryAndTimeRangeAndType();
                    if (!bookmarks.isEmpty()) {
                        this.setVisible(false);
                        new OpenManageBookmarkDialog(appControl,
                                bookmarks, dateTitle, controller, category);
                    } else {
                        this.noResultLabel.setVisible(true);
                    }
                } else {
                    searchByCategoryAndType();
                    if (!bookmarks.isEmpty()) {
                        this.setVisible(false);
                        new OpenManageBookmarkDialog(appControl,
                                bookmarks, categoryTitle, controller, category);
                    } else {
                        this.noResultLabel.setVisible(true);
                    }
                }
            } else {
                searchByCategoryAndType();
                if (!bookmarks.isEmpty()) {
                    this.setVisible(false);
                    new OpenManageBookmarkDialog(appControl,
                            bookmarks, categoryTitle, controller, category);
                } else {
                    this.noResultLabel.setVisible(true);
                }
            }
        }
        if (isLocalSearch) {
            OpenManageBookmarkDialog ombd;
            String category = (String) categoryComboBox.getSelectedItem();
            String subCategory = (String) subcategoryComboBox.getSelectedItem();
            if ((asbwindow != null) && asbwindow.get_OK_Cancel()) {
                if (asbwindow.byName()) {
                    ombd = new OpenManageBookmarkDialog(appControl, asbwindow.getSearchInformation(),
                            controller, asbwindow.getSpecifyName(), null, null, category, subCategory,
                            this);
                } else if (asbwindow.byTimeRange()) {
                    ombd = new OpenManageBookmarkDialog(appControl, asbwindow.getSearchInformation(),
                            controller, null, asbwindow.getStartTime(), asbwindow.getEndTime(), category, subCategory,
                            this);
                } else {
                    ombd = new OpenManageBookmarkDialog(appControl, asbwindow.getSearchInformation(),
                        controller, null, null, null, category, subCategory, this);
                }
            } else {
                ombd = new OpenManageBookmarkDialog(appControl, "By Category",
                        controller, null, null, null, category, subCategory, this);
            }
            ombd.checkForLocalResults();    //Undo display if no results
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void categoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryComboBoxActionPerformed
        this.noResultLabel.setVisible(false);
        subcategoryComboBox.removeAllItems();
        String cat = categoryComboBox.getSelectedItem().toString();
        categoryTitle = cat;
        if (categoryComboBox.getSelectedIndex() == 0) {
            subcategoryComboBox.addItem("All");
        } else {
            for (BookmarkCategory bc : categories) {
                if (bc.getName().matches(cat)) {
                    types = typesManager.obtainAll(bc.getBookmarkCategoryNumber());
                    break;
                }
            }
            subcategoryComboBox.removeAllItems();
            subcategoryComboBox.addItem("All");
            for (BookmarkType t : types) {
                subcategoryComboBox.addItem(t.getName());
            }
        }
    }//GEN-LAST:event_categoryComboBoxActionPerformed

    private void subcategoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subcategoryComboBoxActionPerformed
        this.noResultLabel.setVisible(false);
    }//GEN-LAST:event_subcategoryComboBoxActionPerformed

    private void advancedOptionJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedOptionJButtonActionPerformed
        this.noResultLabel.setVisible(false);
        asbwindow = new AdvancedSearchBookmarkDialog(this, controller);
    }//GEN-LAST:event_advancedOptionJButtonActionPerformed

    /**
     * Searches for a Bookmark by category and type.
     */
    private void searchByCategoryAndType() {
        BookmarkType type;
        Vector<Bookmark> temp = new Vector<>();
        String t = subcategoryComboBox.getSelectedItem().toString();
        searchByCategory();
        if (!t.matches("All")) {
            type = typesManager.searchByName(subcategoryComboBox.getSelectedItem().toString(),
                    categoryComboBox.getSelectedItem().toString());
            int q = bookmarks.size();
            for (int i = 0; i < q; i++) {
                if (bookmarks.get(i).getTypeNumber() == type.getInstanceTypeNumber()) {
                    temp.add(bookmarks.get(i));
                }
            }
            bookmarks = temp;
        }
    }

    /**
     * Searches for a Bookmark by category, date range and type.
     */
    private void searchByCategoryAndTimeRangeAndType() {
        searchByCategoryAndType();
        Calendar startTime = new GregorianCalendar();
        startTime.setTimeZone(asbwindow.getTimeZone());
        startTime.setTime(asbwindow.getStartTime().getTime());
        Calendar endTime = new GregorianCalendar();
        endTime.setTimeZone(asbwindow.getTimeZone());
        endTime.setTime(asbwindow.getEndTime().getTime());
        dateTitle = "From: " + CalendarFormatter.format(startTime,
                CalendarFormatter.DisplayFormat.DATE) + " "
                + CalendarFormatter.format(startTime,
                CalendarFormatter.DisplayFormat.TIME_12) + " "
                + CalendarFormatter.format(startTime,
                CalendarFormatter.DisplayFormat.TIME_ZONE)
                + " To: " + CalendarFormatter.format(endTime,
                CalendarFormatter.DisplayFormat.DATE) + " "
                + CalendarFormatter.format(endTime,
                CalendarFormatter.DisplayFormat.TIME_12) + " "
                + CalendarFormatter.format(endTime,
                CalendarFormatter.DisplayFormat.TIME_ZONE);
        for (int i = 0; i < bookmarks.size(); i++) {
            if (bookmarks.get(i).getStartTime().getTime() < startTime.getTimeInMillis()
                    || bookmarks.get(i).getStartTime().getTime() > endTime.getTimeInMillis())  {
                bookmarks.removeElementAt(i);
                i--;
            }
        }
    }

    /**
     * Searches for a Bookmark by category.
     */
    private void searchByCategory() {
        String category = this.categoryComboBox.getSelectedItem().toString();
        if (!category.equals("All")) {
            BookmarkCategory bc = eventManager.getBookmarkCategoryByName(category);
            bookmarks = bookmarkManager.searchByCategoryNumberForUser(user,
                    bc.getBookmarkCategoryNumber(), instructors);
        } else {
            bookmarks = bookmarkManager.searchAllBookmarksViewableByUser(user, instructors);
        }
    }

    /**
     * Searches for a Bookmark by name.
     *
     * @param name The name of the bookmark.
     */
    private void searchByName(String name) {
        this.searchByCategoryAndType();
        for (int i = 0; i < bookmarks.size(); i++) {
            if (!bookmarks.get(i).getName().equals(name)) {
                bookmarks.removeElementAt(i);
                i--;
            }
        }
    }
    
    //Shows no results label
    public void showNoResultsLabel(){
        noResultLabel.setVisible(true);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton advancedOptionJButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> categoryComboBox;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel noResultLabel;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JComboBox<String> subcategoryComboBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
}
