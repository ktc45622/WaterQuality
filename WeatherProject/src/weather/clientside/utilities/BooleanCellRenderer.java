package weather.clientside.utilities;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * This class allows for a <code>JCheckBox</code> to be easily inserted into a
 * <code>JTable</code>.
 *
 * The class can be found at:
 * http://forums.sun.com/thread.jspa?threadID=5366190&tstart=1
 */
public class BooleanCellRenderer extends DefaultTableCellRenderer {

    /**
     * The JCheckBox that is being rendered.
     */
    private final JCheckBox checkBox;

    /**
     * Creates a new check-box based boolean cell renderer. The check-box is
     * centered by default.
     */
    public BooleanCellRenderer() {
        checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Get the check box.
     *
     * @return the JCheckBox to be rendered
     */
    public JCheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * Returns the component (the JCheckBox) that is used for rendering the 
     * value.
     *
     * @param table the JTable containing the cell to be rendered
     * @param value the value of the checkbox if it is selected
     * @param isSelected true if the checkbox is selected, false otherwise
     * @param hasFocus true if current cell has focus, false otherwise
     * @param row number of the row containing the cell to be rendered
     * @param column number of the column containing the cell to be rendered
     * @return the default table cell renderer component
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected,
            boolean hasFocus, int row,
            int column) {
        if (isSelected) {
            checkBox.setBackground(table.getSelectionBackground());
            checkBox.setForeground(table.getSelectionForeground());
        } else {
            checkBox.setBackground(table.getBackground());
            checkBox.setForeground(table.getForeground());
        }

        if (hasFocus) {
            checkBox.setBorder(
                    UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column)) {
                checkBox.setBackground(
                        UIManager.getColor("Table.focusCellBackground"));
                checkBox.setForeground(
                        UIManager.getColor("Table.focusCellForeground"));
            }
        } else {
            checkBox.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        // Null is rendered as false.
        if (value == null) {
            checkBox.setSelected(false);
        } else {
            Boolean boolValue = (Boolean) value;
            checkBox.setSelected(boolValue.booleanValue());
        }
        return checkBox;
    }
}
