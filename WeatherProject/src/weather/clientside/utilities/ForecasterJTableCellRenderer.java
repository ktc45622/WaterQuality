package weather.clientside.utilities;


import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import weather.clientside.gradebook.GBForecastingAttemptEntry;
import weather.clientside.gradebook.GBForecastingLessonEntry;
import weather.clientside.gradebook.GBLessonEntry;
import weather.common.data.Course;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.gui.component.IconProperties;

/**
 * Used to implement a JTable with expandable rows. Provided data is inserted
 * into a one row JTable which is inserted into the node of a JTree, constructing
 * a structure which looks like a JTable yet behaves like a JTree.
 * Code is very specific - must be adapted according to data that is intended to display.
 * 
 * @author Nikita Maizet
 */
public class ForecasterJTableCellRenderer extends DefaultTreeCellRenderer {

    private final Color SELECTED_COLOR = new Color(57, 154, 251);
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

        // Object is a FGBStudentViewLNode, render a certain way
        if (userObject.getClass() == FGBStudentViewLNode.class) {
            return renderFGBStudentViewLNode(userObject, tree);
        }
        
        // Object is a FGBStudentViewANode, render a certain way
        if (userObject.getClass() == FGBStudentViewANode.class) {
            return renderFGBStudentViewANode(userObject, tree);
        }
        
        // Object is a FGBLessonViewLNode, render a certain way
        if (userObject.getClass() == FGBLessonViewLNode.class) {
            return renderFGBLessonViewLNode(userObject, tree);
        }
        
        // Object is a FGBLessonViewANode, render a certain way
        if (userObject.getClass() == FGBLessonViewANode.class) {
            return renderFGBLessonViewANode(userObject, tree);
        }
        
        // Object is a String[] with length == 1, render a certain way
        if (userObject.getClass() == String[].class && ((String[]) userObject).length == 1) {
            return renderStringArrayOfLengthOne(userObject, tree);
        } // Object is a String[] with length > 1, render a certain way
        else if (userObject.getClass() == String[].class && ((String[]) userObject).length > 1) {
            return renderStringArray(userObject, tree);
        }
        
        // Object is a GBForecastingLessonEntry, render a certain way
        if (userObject.getClass() == GBForecastingLessonEntry.class) {
            return renderGradebookForecastingLessonEntry(userObject, tree);
        }

        // Object is a ForecasterNodeObject, render a certain way
        if (userObject.getClass() == ForecasterNodeObject.class) {
            return renderForecasterNodeObject(userObject, tree);
        }

        // Object is a ForecasterLesson, render a certain way
        if (userObject.getClass() == Course.class) {
            return renderCourseObject(userObject, tree, expanded);
        }
        
        if (userObject.getClass() == ForecasterLesson.class) {
            return renderLessonObject(userObject, tree);
        }
        
        // Object is a JSeparator, render
        if (userObject.getClass() == JSeparator.class) {
            return renderComponent(userObject, tree);
        }
        
        super.getTreeCellRendererComponent(
                tree, value, leaf, expanded, leaf, row, hasFocus);

        return this;
    }
    
    private JTable renderLessonObject(Object uncastUserObject, JTree tree) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();    
        
        ForecasterLesson lesson = (ForecasterLesson) uncastUserObject;
        
        currentDate.setTime(new Date());
        startDate.setTime(lesson.getLessonStartDate());
        endDate.setTime(lesson.getLessonEndDate());

        final ArrayList<String> listToDisplay = new ArrayList<>();
        
        if(currentDate.before(startDate)) {
            listToDisplay.add(lesson.getLessonName());
            listToDisplay.add("Status: Opens On");
            listToDisplay.add(lesson.getLessonStartDate() + "");
        } else if(currentDate.after(endDate)) {      
            listToDisplay.add(lesson.getLessonName());
            listToDisplay.add("Status: Closed On");
            listToDisplay.add(lesson.getLessonEndDate() + "");
        } else {
            listToDisplay.add(lesson.getLessonName());
            listToDisplay.add("Status: Open Until");
            listToDisplay.add(lesson.getLessonEndDate() + "");
        }
        
        
        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return listToDisplay.size();
            }

            @Override
            public Object getValueAt(int row, int column) {
                return listToDisplay.get(column);
            }
        });

        
        int extraSpace = 20;
        for (int i = 0; i < listToDisplay.size(); i++) {
            int preferredWidth = tree.getWidth() / (listToDisplay.size());
            // The space subtracted from a column should be adde to another
            switch (i) {
                case 0: // Lesson name
                    preferredWidth += extraSpace * 1;   //+20
                    break;
                    
                case 1: // Start date
                    preferredWidth -= extraSpace * 2;   //-40
                    break;

                case 2: // End date
                    preferredWidth -= extraSpace * 2;   //-40
                    break;
                    
                default:
                    break;
            }
            table.getColumnModel().getColumn(i).
                    setPreferredWidth(preferredWidth);
        }
        
        /* 
         * Compares the selected path with this node. 
         * 
         * The way this works requires for each node to be rendered again every
         * time a click occurs. This is not the most efficient way to do this,
         * but for the sake of time, it is the way that works.
         */
        if (tree.getSelectionPath() != null) {
            Object selectedNode = 
                    tree.getSelectionPath().getLastPathComponent();
            ForecasterJTreeNode choosenNode = null;

            if (selectedNode instanceof ForecasterJTreeNode) {
                choosenNode = (ForecasterJTreeNode) selectedNode;
                
                Object selectedObject = choosenNode.getUserObject();
                
                if (selectedObject instanceof ForecasterLesson) {
                    ForecasterLesson selectedLesson = 
                            (ForecasterLesson) selectedObject;
                    if (selectedLesson.equals(lesson)) {
                        table.setBackground(SELECTED_COLOR);
                    }
                }
            }
        }

        table.setOpaque(false);

        //Set the status of the gridlines 
        table.setShowGrid(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setFont(new java.awt.Font("Tahoma", 0, 12));
        tree.putClientProperty("JTree.lineStyle", "Angled");

        return table;
    }

    private JTable renderCourseObject(Object uncastUserObject,
            JTree tree, final boolean expanded) {        

        
        
        Course course = (Course) uncastUserObject;

        final ArrayList<String> listToDisplay = new ArrayList<>();

        listToDisplay.add(course.getClassName());
        listToDisplay.add(course.getClassIdentifier() + "");
        listToDisplay.add(course.getSemester().toString());
        listToDisplay.add(course.getInstructor().getLastName());

        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return listToDisplay.size() + 1;
            }

            @Override
            public Object getValueAt(int row, int column) {
                // Sets the first column in course to an icon
                if (column == 0) {
                    if (expanded) {
                        return IconProperties.getMinusIcon();
                    } return IconProperties.getPlusIcon();
                }
                return listToDisplay.get(column - 1);
            }
            
            @Override
            public Class getColumnClass(int column) {
                if (column == 0) {
                    return ImageIcon.class;
                }
                return Course.class;
            }
        });

        
        int extraSpace = 20;
        for (int i = 0; i < listToDisplay.size() + 1; i++) {
            int preferredWidth = tree.getWidth() / (listToDisplay.size() + 1);
            // The space subtracted from a column should be adde to another
            switch (i) {
                case 0: // The icon
                    preferredWidth -= extraSpace * 3;   //-60
                    break;
                case 1: // Course name
                    preferredWidth += extraSpace * 3;   //+60
                    break;

                case 2: // Course section
                    preferredWidth -= extraSpace * 2;   //-40
                    break;
                    
                case 3: // Course semester
                    preferredWidth -= extraSpace * 1;   //-20
                    break;
                
                case 4: // Course instructor
                    preferredWidth += extraSpace * 3;   //+60
                    break;
                    
                default:
                    break;
            }
            table.getColumnModel().getColumn(i).
                    setPreferredWidth(preferredWidth);
        }
        
        /* 
         * Compares the selected path with this node. 
         * 
         * The way this works requires for each node to be rendered again every
         * time a click occurs. This is not the most efficient way to do this,
         * but for the sake of time, it is the way that works.
         */
        if (tree.getSelectionPath() != null) {
            Object selectedNode = 
                    tree.getSelectionPath().getLastPathComponent();
            ForecasterJTreeNode choosenNode = null;

            if (selectedNode instanceof ForecasterJTreeNode) {
                choosenNode = (ForecasterJTreeNode) selectedNode;
                
                Object selectedObject = choosenNode.getUserObject();
                
                if (selectedObject instanceof Course) {
                    Course selectedCourse = (Course) selectedObject;
                    if (selectedCourse.equals(course)) {
                        table.setBackground(SELECTED_COLOR);
                    }
                }
            }

        }

        table.setOpaque(false);

        //Set the status of the gridlines 
        table.setShowGrid(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setFont(new java.awt.Font("Tahoma", 0, 12));
        tree.putClientProperty("JTree.lineStyle", "Angled");

        return table;
    }

    /**
     * Renders a ForecasterNodeObject into a label with the list separated by
     * '|'. The reason the another object was placed within the Object was to
     * have access to the object once the cell was clicked.
     *
     * TODO: This is a sloppy way of doing this. Check the type of the object
     * when it is retrieved and cast it to this type. Also, implement how to
     * display that object.
     *
     * @param uncastUserObject
     * @param tree
     * @return a JLabel that will be displayed
     */
    @SuppressWarnings("unchecked")
    private JLabel renderForecasterNodeObject(Object uncastUserObject,
            JTree tree) {
        // Gets rid of the default icons on the cell
        DefaultTreeCellRenderer icons = new DefaultTreeCellRenderer();
        icons.setLeafIcon(null);
        icons.setOpenIcon(null);
        icons.setClosedIcon(null);

        ForecasterNodeObject<ForecasterLesson> userObject = 
                (ForecasterNodeObject) uncastUserObject;
        final ArrayList<String> listToDisplay = (ArrayList<String>) userObject.getList();

        String textToDisplay = "";
        Iterator<String> i = listToDisplay.iterator();
        while (i.hasNext()) {
            textToDisplay += i.next();
            if (i.hasNext()) {
                textToDisplay += "   |   ";
            }
        }

        JLabel classType = new JLabel();

        classType.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        classType.setText(textToDisplay);

        return classType;
    }

    /**
     * Renders a String[] into a table format. Originally used for testing
     * rendering.
     *
     * @param userObject
     * @param tree
     * @return a JTable that will be displayed
     */
    private JTable renderStringArray(Object userObject, JTree tree) {
        /*
         make these objects take instance of ForecasterLesson and extract
         needed information and convert all table entries to array of strings:
         */

        String[] temp = (String[]) userObject;
        final String[] command = temp;
        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return command.length;
            }

            @Override
            public Object getValueAt(int row, int column) {
                return command[column];
            }
        });

        table.setOpaque(false);

//      To disable cell lines:  
//        table.setShowGrid(true);
//        table.setShowHorizontalLines(false);
//        table.setShowVerticalLines(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(260);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        // last row purposely extra wide to insure lines flow into border of form
        table.getColumnModel().getColumn(5).setPreferredWidth(800);
        

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setFont(new java.awt.Font("Tahoma", 0, 11));
        tree.putClientProperty("JTree.lineStyle", "Angled");

        return table;
    }
    
    private JTable renderStringArrayOfLengthOne(Object userObject, JTree tree) {
        String[] temp = (String[]) userObject;
        final String[] command = temp;
        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return command.length;
            }

            @Override
            public Object getValueAt(int row, int column) {
                return command[column];
            }
        });

        table.setOpaque(false);

//      To disable cell lines:  
//        table.setShowGrid(true);
//        table.setShowHorizontalLines(false);
//        table.setShowVerticalLines(false);
        
        // last row purposely extra wide to insure lines flow into border of form
        table.getColumnModel().getColumn(0).setPreferredWidth(5000);
        

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setFont(new java.awt.Font("Tahoma", 0, 11));
        tree.putClientProperty("JTree.lineStyle", "Angled");

        return table;
    }
    
    private JTable renderGradebookForecastingLessonEntry(Object userObject, JTree tree) {
        GBForecastingLessonEntry temp = (GBForecastingLessonEntry) userObject;
        final GBForecastingLessonEntry command = temp;
        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int row, int column) {
                //return command[column];
                return command.getEntryName();
            }
        });

        table.setOpaque(false);

//      To disable cell lines:  
//        table.setShowGrid(true);
//        table.setShowHorizontalLines(false);
//        table.setShowVerticalLines(false);
        
        // last row purposely extra wide to insure lines flow into border of form
        table.getColumnModel().getColumn(0).setPreferredWidth(800);
        

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setFont(new java.awt.Font("Tahoma", 0, 11));
        tree.putClientProperty("JTree.lineStyle", "Angled");
        return table;
    }

    private Component renderFGBStudentViewLNode(Object userObject, JTree tree) {
        FGBStudentViewLNode node = (FGBStudentViewLNode) userObject;
        GBForecastingLessonEntry temp = (GBForecastingLessonEntry) node.getSourceObject();
        final GBForecastingLessonEntry command = temp;
        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return 6;
            }

            @Override
            public Object getValueAt(int row, int column) {
                switch(column) {
                    case 0: return command.getEntryName() + " (Closing: " 
                            + command.getDateDue() + ")"; 
                    case 1: return command.getPointsEarned();
                    case 2: return command.getPercentage();
                    case 3: return "";
                    case 4: return "";
                    case 5: return command.getAssignmentStatus();
                }
                return "cell renderer indexing error.";
            }
        });

        table.setOpaque(false);

//      To disable cell lines:  
//        table.setShowGrid(true);
//        table.setShowHorizontalLines(false);
//        table.setShowVerticalLines(false);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(260);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        // last row purposely extra wide to insure lines flow into border of form
        table.getColumnModel().getColumn(5).setPreferredWidth(800);
        

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setFont(new java.awt.Font("Tahoma", 0, 11));
        tree.putClientProperty("JTree.lineStyle", "Angled");
        
        // change color of row to give selected appearance
        if (tree.getSelectionPath() != null) {
            Object selectedNode = 
                    tree.getSelectionPath().getLastPathComponent();
            ForecasterJTreeNode choosenNode = null;

            if (selectedNode instanceof ForecasterJTreeNode) {
                choosenNode = (ForecasterJTreeNode) selectedNode;
                
                Object selectedObject = choosenNode.getUserObject();
                
                if (selectedObject instanceof FGBStudentViewLNode) {
                    FGBStudentViewLNode selectedCourse = (FGBStudentViewLNode) selectedObject;
                    if (selectedCourse.equals(node)) {
                        table.setBackground(SELECTED_COLOR);
                    }
                }
            }

        }

        return table;
    }

    private Component renderFGBStudentViewANode(Object userObject, JTree tree) {
        FGBStudentViewANode node = (FGBStudentViewANode) userObject;
        GBForecastingAttemptEntry temp = (GBForecastingAttemptEntry) node.getSourceObject();
        final GBForecastingAttemptEntry command = temp;
        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return 6;
            }

            @Override
            public Object getValueAt(int row, int column) {
                //return command[column];
                switch(column) {
                    case 0: return command.getEntryName();
                    case 1: return command.getPointsEarned();
                    case 2: return command.getPercentage();
                    case 3: return command.getDateSubmitted();
                    case 4: return command.GetDateForecasted();
                    case 5: return command.getAssignmentStatus();
                }
                return "cell renderer indexing error.";
            }
        });

        table.setOpaque(false);

//      To disable cell lines:  
//        table.setShowGrid(true);
//        table.setShowHorizontalLines(false);
//        table.setShowVerticalLines(false);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(260);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        // last row purposely extra wide to insure lines flow into border of form
        table.getColumnModel().getColumn(5).setPreferredWidth(800);
        

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setFont(new java.awt.Font("Tahoma", 0, 11));
        tree.putClientProperty("JTree.lineStyle", "Angled");
        
        // change color of row to give selected appearance
        if (tree.getSelectionPath() != null) {
            Object selectedNode = 
                    tree.getSelectionPath().getLastPathComponent();
            ForecasterJTreeNode choosenNode = null;

            if (selectedNode instanceof ForecasterJTreeNode) {
                choosenNode = (ForecasterJTreeNode) selectedNode;
                
                Object selectedObject = choosenNode.getUserObject();
                
                if (selectedObject instanceof FGBStudentViewANode) {
                    FGBStudentViewANode selectedCourse = (FGBStudentViewANode) selectedObject;
                    if (selectedCourse.equals(node)) {
                        table.setBackground(new Color(12, 111, 251));
                    }
                }
            }

        }

        return table;
    }

    private Component renderFGBLessonViewLNode(Object userObject, JTree tree) {
        FGBLessonViewLNode node = (FGBLessonViewLNode) userObject;
        GBLessonEntry temp = (GBForecastingLessonEntry) node.getSourceObject();
        final GBLessonEntry command = temp;
        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return 6;
            }

            @Override
            public Object getValueAt(int row, int column) {
                return command.getEntryName();
            }
        });

        table.setOpaque(false);

//      To disable cell lines:  
//        table.setShowGrid(true);
//        table.setShowHorizontalLines(false);
//        table.setShowVerticalLines(false);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(1500);
        

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setFont(new java.awt.Font("Tahoma", 0, 11));
        tree.putClientProperty("JTree.lineStyle", "Angled");
        
        // change color of row to give selected appearance
        if (tree.getSelectionPath() != null) {
            Object selectedNode = 
                    tree.getSelectionPath().getLastPathComponent();
            ForecasterJTreeNode choosenNode = null;

            if (selectedNode instanceof ForecasterJTreeNode) {
                choosenNode = (ForecasterJTreeNode) selectedNode;
                
                Object selectedObject = choosenNode.getUserObject();
                
                if (selectedObject instanceof FGBLessonViewLNode) {
                    FGBLessonViewLNode selectedCourse = (FGBLessonViewLNode) selectedObject;
                    if (selectedCourse.equals(node)) {
                        table.setBackground(SELECTED_COLOR);
                    }
                }
            }

        }

        return table;
    }

    private Component renderFGBLessonViewANode(Object userObject, JTree tree) {
        FGBLessonViewANode node = (FGBLessonViewANode) userObject;
        GBForecastingAttemptEntry temp = (GBForecastingAttemptEntry) node.getSourceObject();
        final GBForecastingAttemptEntry command = temp;
        JTable table = new JTable();

        table.setModel(new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int row, int column) {
                return command.getEntryName();
            }
        });

        table.setOpaque(false);

//      To disable cell lines:  
//        table.setShowGrid(true);
//        table.setShowHorizontalLines(false);
//        table.setShowVerticalLines(false);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(1500);
        

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setFont(new java.awt.Font("Tahoma", 0, 11));
        tree.putClientProperty("JTree.lineStyle", "Angled");
        
        // change color of row to give selected appearance
        if (tree.getSelectionPath() != null) {
            Object selectedNode = 
                    tree.getSelectionPath().getLastPathComponent();
            ForecasterJTreeNode choosenNode = null;

            if (selectedNode instanceof ForecasterJTreeNode) {
                choosenNode = (ForecasterJTreeNode) selectedNode;
                
                Object selectedObject = choosenNode.getUserObject();
                
                if (selectedObject instanceof FGBLessonViewANode) {
                    FGBLessonViewANode selectedCourse = (FGBLessonViewANode) selectedObject;
                    if (selectedCourse.equals(node)) {
                        table.setBackground(SELECTED_COLOR);
                    }
                }
            }

        }

        return table;
    }
    
    private Component renderComponent(Object userObject, JTree tree) {
        
        return (Component) userObject;
    }
}
