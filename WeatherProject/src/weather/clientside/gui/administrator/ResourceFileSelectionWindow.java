package weather.clientside.gui.administrator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import weather.ApplicationControlSystem;
import weather.common.data.InstructorDataType;
import weather.common.data.User;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.note.InstructorNote;
import weather.common.gui.component.BUDialog;

/**
 * This form, for use be <code>ManageInstructorFilesWindow</code>, allows the
 * user to select to data object (class note, instructional lesson, or bookmark)
 * to which a file will be attached.  It has a private constructor and is 
 * accessible through a static method.
 * @author Brian Bankes
 */
public class ResourceFileSelectionWindow extends BUDialog {
    
    /**
     * Indicates which type of data (class note, instructional lesson, or 
     * bookmark) is to be shown by the current instance.
     */
    private final InstructorDataType windowType;
    
    /**
     * Holds the return value of the public static function, which should be -1
     * if no selection is made.
     */
    private static int dataNumber;
    
    /**
     * Return the database primary key of an object (class note, instructional
     * lesson, or bookmark) to which a file can be attached. The type (class
     * note, instructional lesson, or bookmark) is predetermined by a parameter,
     * but the user determines the specific object via a GUI form.
     * 
     * @param appControl The program's <code>ApplicationControlSystem</code>.
     * @param windowType An <code>InstructorDataType</code> indicative of the
     * type of object (class note, instructional lesson, or bookmark) to be
     * selected.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     * @return The database primary key of an object (class note, instructional
     * lesson, or bookmark) to which a file can be attached, or -1 if no object
     * was selected.
     */
    public static int selectNewResourceNumber(
            ApplicationControlSystem appControl,
            InstructorDataType windowType, Component parent) {
        dataNumber = -1; //No object has been selected.
        
        //Instructional lessons should be removed from this list in the future.
        if (windowType == InstructorDataType.Private 
                || windowType == InstructorDataType.InstructionalLessons) {
            JOptionPane.showMessageDialog(parent, "A data instane cannot be "
                    + "selected for this type.", "Error", JOptionPane
                            .WARNING_MESSAGE);
        } else {
            new ResourceFileSelectionWindow(appControl, windowType);
        }
        return dataNumber;
    }
    
    /**
     * Constructor.
     * 
     * @param appControl The program's <code>ApplicationControlSystem</code>.
     * @param windowType An <code>InstructorDataType</code> indicative of the
     * type of object (class note, instructional lesson, or bookmark) to be
     * selected. 
     */
    private ResourceFileSelectionWindow(ApplicationControlSystem appControl,
            InstructorDataType windowType) {
        super(appControl);
        this.windowType = windowType;
        initComponents();
        User user = appControl.getGeneralService().getUser();
        ButtonGroup buttonGroup = new ButtonGroup();
        
        //Instructional lessons need to be added as type choice.
        if (windowType == InstructorDataType.Bookmarks) {
            this.setTitle("Select Bookmark");
            dataScrollPane.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Available Bookmarks"));
            topLabel.setText("Please select a bookmark...");

            //Add radio buttons to date list panel.
            //Must make sure bookmarks are not events.
            Vector<Bookmark> allDatabase = appControl.getDBMSSystem()
                    .getBookmarkManager().searchByCreatedBy(user
                            .getUserNumber());
            Vector<Bookmark> bookmarksDatabase = new Vector<>();
            for (Bookmark bookmark : allDatabase) {
                if (bookmark.getType() == BookmarkDuration.instance) {
                    bookmarksDatabase.add(bookmark);
                }
            }
            for (final Bookmark bookmark : bookmarksDatabase) {
                final JRadioButton choice = new JRadioButton(bookmark
                        .getName());
                buttonGroup.add(choice);
                choice.addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (choice.isSelected()) {
                            dataNumber = bookmark.getBookmarkNumber();
                        }
                    }
                });
                
                this.dataListPanel.add(choice);
                choice.setSelected(false);
            }   //End of for loop.
        } else { //The window type is class notes.
            this.setTitle("Select Class Note");
             dataScrollPane.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Available Class Notes"));
            topLabel.setText("Please select a class note...");

            //Add radio buttons to date list panel.
            for (final InstructorNote note : appControl.getDBMSSystem()
                    .getNoteManager().getNotesByInstructor(user)) {
                final JRadioButton choice = new JRadioButton(note
                        .getNoteTitle());
                buttonGroup.add(choice);
                choice.addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (choice.isSelected()) {
                            dataNumber = note.getNoteNumber();
                        }
                    }
                });
                
                this.dataListPanel.add(choice);
                choice.setSelected(false);
            }   //End of for loop.
        }

        int width = 316 + this.getInsets().left + this.getInsets().right;
        int height = 228 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectButton = new javax.swing.JButton();
        topLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        dataScrollPane = new javax.swing.JScrollPane();
        dataListPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        selectButton.setText("Select");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        getContentPane().add(selectButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 190, 140, 26));

        topLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        getContentPane().add(topLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 292, 16));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(164, 190, 140, 26));

        dataScrollPane.setBorder(null);
        dataScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        dataListPanel.setLayout(new javax.swing.BoxLayout(dataListPanel, javax.swing.BoxLayout.Y_AXIS));
        dataScrollPane.setViewportView(dataListPanel);

        getContentPane().add(dataScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, 292, 138));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        if (dataNumber == -1) {
            //Instructional lessons need to be added as type choice.
            String type;
            if (windowType == InstructorDataType.Bookmarks) {
                type = "bookmark";
            } else {
                type = "class note";
            }
            String message = "Please select a " + type + ".";
            JOptionPane.showMessageDialog(this, message, "No Selection", 
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            //Only need to close the form as the public method will send the
            //data number to the calling code.
            this.dispose();
        }
    }//GEN-LAST:event_selectButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancelAndClose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelAndClose();
    }//GEN-LAST:event_formWindowClosing

    /**
     * This helper function closes the from if the user does not make a 
     * selection. It ensures the public method returns -1.
     */
   private void cancelAndClose() {
       dataNumber = -1;
       this.dispose();
   }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel dataListPanel;
    private javax.swing.JScrollPane dataScrollPane;
    private javax.swing.JButton selectButton;
    private javax.swing.JLabel topLabel;
    // End of variables declaration//GEN-END:variables
}
