package weather.clientside.utilities;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * A custom cell renderer for a JComboBox which displays a specified prompt
 * in the combo box while an element has not been selected.
 * 
 * @author Nikita Maizet
 */
public class CustomPromptComboBoxRenderer extends BasicComboBoxRenderer {

    private final String prompt;

    /**
     * Sets prompt to be displayed.
     * 
     * @param prompt 
     */
    public CustomPromptComboBoxRenderer(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Method is modified to display a prompt chosen by programmer whenever
     * an element in the combobox has using this renderer has not been selected.
     * 
     * @param list
     * @param value - value of element selected.
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return The cell renderer component.
     */
    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // if value is null means no element has been selected, so custom
        // prompt will be displayed
        if (value == null) {
            setText(prompt);
        }

        return this;
    }
}
