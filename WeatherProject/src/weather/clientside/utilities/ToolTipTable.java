package weather.clientside.utilities;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 * This class extends <code>JTable</code> so that the contents of the cell the
 * mouse pointer is on shows in a tool tip.
 * 
 * @author Brian Bankes via stackoverflow.com
 */
public class ToolTipTable extends JTable {
    //Implement table cell tool tips.           

    @Override
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);

        try {
            tip = getValueAt(rowIndex, colIndex).toString();
        } catch (RuntimeException e1) {
            //catch null pointer exception if mouse is over an empty line
        }

        //reset to null if string is empty
        if (tip != null && tip.trim().equals("")) {
            tip = null;
        }
        
        return tip;
    }
}
