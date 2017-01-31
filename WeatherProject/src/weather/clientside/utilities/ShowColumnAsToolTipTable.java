package weather.clientside.utilities;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 * This class extends <code>JTable</code> so that the contents of the provided
 * column in the row where the mouse pointer is on shows in a tool tip.
 * 
 * @author Brian Bankes
 */
public class ShowColumnAsToolTipTable extends JTable {
    
    private final int columnToShow;
    
    /**
     * Constructor.
     * @param columnToShow The number of the column whose text should be shown 
     * as a tool tip.
     */
    public ShowColumnAsToolTipTable(int columnToShow) {
        super();
        this.columnToShow = columnToShow;
    }
    
    
    //Implement table cell tool tips.           

    @Override
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);

        try {
            tip = addLineBreaks(getValueAt(rowIndex, columnToShow).toString());
        } catch (RuntimeException e1) {
            //catch null pointer exception if mouse is over an empty line
        }

        //reset to null if string is empty
        if (tip != null && tip.trim().equals("")) {
            tip = null;
        }
        
        return tip;
    }
    /**
     * Helper function to place line breaks in the the cell text being shown.
     * @param cellText The cell text being shown.
     * @return The cell text being shown with line breaks.
     */
    private String addLineBreaks(String cellText) {
        //The portion of the cell text yet to be processed; the whole string at
        //first.
        String remainder = cellText;
        
        //The maximum number a charactor in a line,
        int maxLineLength = 100;
        
        //The string to return as a builder.
        StringBuilder result = new StringBuilder("<html>");
        
        //Replace spaces with new line characters.
        while (remainder.length() > maxLineLength) {
            //Get next line of characters.
            String thisLine = remainder.substring(0, maxLineLength);
            int lastSpaceIndex = thisLine.lastIndexOf(" ");
            if (lastSpaceIndex == -1) { //no spaces in line - very unlikely
                result.append(thisLine).append("<br/>");
                remainder = remainder.substring(maxLineLength);
            } else { // spaces in line
                result.append(thisLine.substring(0, lastSpaceIndex))
                        .append("<br/>");
                remainder = remainder.substring(lastSpaceIndex + 1);
            }
        }
        //Append final line.
        result.append(remainder).append("</html>");
        
        //Return result.
        return result.toString();
    }
}
