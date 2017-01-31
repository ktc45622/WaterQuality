package weather.clientside.utilities;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * This class defines a customized ListCellRenderer that will allow the user to 
 * easily display a date with the specified date format inside a comboBox or list. 
 * The user only has to add the date objects to the list, create a new object
 * of this class, and register the list's renderer with this new object. The
 * format string should be a general property.
 * <pre>
 * ListCellDateRenderer renderer = new ListCellDateRenderer(myDateFormatString);
 * myComboBox.setRenderer(renderer);
 * </pre>
 * 
 * @author Chris Vitello (2012)
 */
public class ListCellDateRenderer extends JLabel implements ListCellRenderer<Date> {

    private SimpleDateFormat dateFormat;

    /**
     * Initializes the various properties of the ListCellRenderer.
     * @param formatString The string to be used to format the dates for display.
     */
    public ListCellDateRenderer(String formatString) {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
        dateFormat = new SimpleDateFormat(formatString);
    }

    /**
     * Return a component that has been configured to display the specified value. 
     * That component's paint method is then called to "render" the cell. If it 
     * is necessary to compute the dimensions of a list because the list cells 
     * do not have a fixed size, this method is called to generate a component 
     * on which getPreferredSize can be invoked.
     * @param list The JList we're painting.
     * @param value The value returned by list.getModel().getElementAt(index).
     * @param index The cells index.
     * @param isSelected True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus. 
     * @return A component whose paint() method will render the specified value.
     */
    @Override
    public Component getListCellRendererComponent(JList list, Date value,
            int index, boolean isSelected, boolean cellHasFocus) {
        
        String selectedDate = dateFormat.format(value);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setText(selectedDate);
        return this;
    }
    
    /**
     * Set this class's SimpleDateFormat object to be used in rendering.
     * @param formatString The format to be used for the SimpleDateFormat object
     */
    public void setFormatString(String formatString) {
        dateFormat = new SimpleDateFormat(formatString);
    }
    
    /**
     * Set this TizeZone of the class's SimpleDateFormat object to be used in 
     * rendering.
     * @param timeZone The time zone to be used for the SimpleDateFormat object
     */
    public void setTimeZone(TimeZone timeZone) {
        dateFormat.setTimeZone(timeZone);
    }
}
