package weather.clientside.gui.component;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import weather.common.gui.component.IconProperties;

/**
 * This is a utility class to display a <code>Resource</code> as a combobox item.
 * @author Eric Subach (2011)
 * @author Xiang Li (2014)
 */

public class ResourceListCellRenderer implements ListCellRenderer<ResourceListCellItem> {
    private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();


    @Override
    public Component getListCellRendererComponent (JList list, ResourceListCellItem value,
            int index, boolean isSelected, boolean cellHasFocus) {
        String text = null;
        Icon icon = null;

        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        
        if (value instanceof ResourceListCellItem) {
            text = ((ResourceListCellItem)value).getName();
            icon = ((ResourceListCellItem)value).getIcon();
        }

        if (icon != null) {
            renderer.setIcon(icon);
        }
        
        renderer.setText(text);

        return renderer;
    }
    
    
    public static void main (String args[]) {
        ListCellRenderer<ResourceListCellItem> renderer = new ResourceListCellRenderer ();
        JComboBox<ResourceListCellItem> comboBox = new JComboBox<ResourceListCellItem>();

        
        ResourceListCellItem item = new ResourceListCellItem ("String here",
                IconProperties.getResourceVisibleIconImage(), true);
        ResourceListCellItem item2 = new ResourceListCellItem ("String there",
                IconProperties.getResourceInactiveIconImage(), false);
        ResourceListCellItem item3 = new ResourceListCellItem ("String over here",
                IconProperties.getResourceInvisibleIconImage(), true);
        ResourceListCellItem item4 = new ResourceListCellItem("String over there",
                IconProperties.getResourceSpacerIconImage(), false);

        comboBox.setRenderer(renderer);
        comboBox.addItem(item);
        comboBox.addItem(item2);
        comboBox.addItem(item3);
        comboBox.addItem(item4);

        comboBox.setSelectedItem(new ResourceListCellItem ("String there"));

        JFrame frame = new JFrame ("test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(comboBox, BorderLayout.NORTH);

        frame.setSize(300, 200);
        frame.setVisible(true);
    }
}
