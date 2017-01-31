package weather.clientside.utilities;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * Extends majority of DefaultTableModel's functionality with a few exceptions.
 * Does not allow cells to be edited by double clicking on them.
 * 
 * @author Nikita Maizet
 */
public class StudentListTableModel extends DefaultTableModel {
    
    public StudentListTableModel(int rowCount, int columnCount) {
        this(newVector(columnCount), rowCount);
    }

    public StudentListTableModel(Object[][] data, String[] columnNames) {
        this.setDataVector(data, columnNames);
    }
    
    public StudentListTableModel(Vector columnNames, int rowCount) {
        this.setDataVector(newVector(rowCount), columnNames);
    }
    
    private static Vector newVector(int size) {
        Vector v = new Vector(size);
        v.setSize(size);
        return v;
    }
    
    
    
    /**
     * Makes all cells of the table using this table model not editable.
     * 
     * @param row
     * @param column
     * @return boolean
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
