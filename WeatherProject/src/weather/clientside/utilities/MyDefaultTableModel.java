package weather.clientside.utilities;

import javax.swing.table.DefaultTableModel;

/**
 * This class disables editing of cells on all <code>JTable</code>s that are set
 * to use this model.
 *
 * @author Ora Merkel(2009)
 */
public class MyDefaultTableModel extends DefaultTableModel {

    /**
     * Constructor for the MyDefaultTableModel. This takes in the number of rows
     * and columns the new table is to contain.
     *
     * @param row number of rows in the table
     * @param col number of columns in the table
     */
    public MyDefaultTableModel(int row, int col) {
        super(row, col);
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
