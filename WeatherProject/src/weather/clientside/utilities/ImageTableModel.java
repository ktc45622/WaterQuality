package weather.clientside.utilities;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 * This class designates one column of all <code>JTable</code>s that are set to 
 * use this model as a column to show <code>ImageIcon</code>s. It also disables
 * editing of cells.
 *
 * @author Brian Bankes
 */
public class ImageTableModel extends DefaultTableModel {

    private final int imageColumn;
    
    /**
     * Constructor for the ImageTableModel. This takes in the number of rows and 
     * columns the new table is to contain as well as the index of the column to
     * hold icons.
     *
     * @param row number of rows in the table
     * @param col number of columns in the table
     * @param imageColumn index of the column to hold icons
     */
    public ImageTableModel(int row, int col, int imageColumn) {
        super(row, col);
        this.imageColumn = imageColumn;
    }

    /**
     * returns the class of the renderer a given colum should use.
     * @param column index of the column to check
     * @return the class of the renderer a given colum should use
     */
    @Override
    public Class<?> getColumnClass(int column) {
        if (column == imageColumn) {
            return ImageIcon.class;
        } else {
            return String.class;
        }
    }

    /**
     * This method returns whether or not a JTable cell is editable. It returns
     * false so all cells in a table created using this model are non-editable.
     *
     * @param row row number of the cell being checked
     * @param col column number of the cell being checked
     * @return false (the cell cannot be edited)
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
