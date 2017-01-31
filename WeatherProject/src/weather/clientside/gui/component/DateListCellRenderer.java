package weather.clientside.gui.component;

import java.awt.Component;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import weather.common.utilities.CalendarFormatter;

/**
 * Renderer for Date objects in a <code>JComboBox</code>.
 *
 * @author Eric Subach
 */
public class DateListCellRenderer implements ListCellRenderer {
    private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    

    @Override
    public Component getListCellRendererComponent (JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        Date date = null;
        GregorianCalendar cal = (GregorianCalendar)Calendar.getInstance();

        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);


        if (value instanceof Date) {
            date = (Date)value;
        }

        if (date != null) {
            cal.setTime(date);
            renderer.setText(CalendarFormatter.formatDateLong(cal));
        }

        return renderer;
    }
}
