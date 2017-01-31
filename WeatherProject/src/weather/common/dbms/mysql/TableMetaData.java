package weather.common.dbms.mysql;

/**
 * This class is a data wrapper used to transfer meta data about a database table.
 * @author jjh35893
 */

/**
 * This class stores meta data information for a database table.
 * NOTE: this file used for some jTable models. Not in use at the moment, but
 * may be useful in the future. 
 * @author Joseph Horro.
 * @version Spring 2011
 */
public class TableMetaData {
    private int colCount;
    private String[] colNames;
    private int rowCount;

    public TableMetaData(int colCount, String[] colNames, int rowCount)
    {
        this.colCount = colCount;
        this.colNames = colNames;
        this.rowCount = rowCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    public String[] getColNames() {
        return colNames;
    }

    public void setColNames(String[] colNames) {
        this.colNames = colNames;
    }
    
}
