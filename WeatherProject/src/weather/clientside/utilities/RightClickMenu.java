package weather.clientside.utilities;

import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

/**
 * This class creates a right-click menu that currently contains cut,
 * copy, paste, and select all functions. 
 * 
 * Users of this class can add this menu to a text component or
 * an array of text components by calling the <code>addMenuTo</code> method
 * with either the component or the array as the argument.
 *
 * @author Bloomsburg University Software Engineering
 * @author Joe Van Lente
 *
 * @version Spring 2010
 */
public class RightClickMenu {
    private static JMenuItem cut =
            createItem("Cut", new DefaultEditorKit.CutAction());
    private static JMenuItem copy =
            createItem("Copy", new DefaultEditorKit.CopyAction());
    private static JMenuItem paste =
            createItem("Paste", new DefaultEditorKit.PasteAction());
    private static JMenuItem selectAll = createSelectAllItem();
    private static Clipboard clipboard =
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
    private static JPopupMenu menu = createMenu();
    private static MouseAdapter mouseAdapter = createMouseAdapter();

    /**
     * Initializes the menu.
     * @return The JPopupMenu object for the menu.
     */
    private static JPopupMenu createMenu() {
        JPopupMenu m = new JPopupMenu();
        m.add(cut);
        m.add(copy);
        m.add(paste);
        m.add(new JPopupMenu.Separator());
        m.add(selectAll);
        return m;
    }

    /**
     * Creates a menu item.
     * @param text The menu item's text.
     * @param action The action to be taken when the item is clicked.
     * @return The JMenuItem object for the menu item.
     */
    private static JMenuItem createItem(String text, Action action) {
        JMenuItem item = new JMenuItem(action);
        item.setText(text);
        return item;
    }

    /**
     * Creates the Select All menu item.
     * @return The JMenuItem object for the Select All menu item.
     */
    private static JMenuItem createSelectAllItem() {
        JMenuItem item = new JMenuItem("Select All");
        item.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((JTextComponent)menu.getInvoker()).selectAll();
                }
            }
        );
        return item;
    }

    /**
     * Creates the mouse adapter used for the right-click menu.
     * @return The mouse adapter.
     */
    private static MouseAdapter createMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!e.isPopupTrigger()) return;

                JTextComponent tc = (JTextComponent)e.getComponent();
                tc.grabFocus();
                boolean editable = tc.isEditable();
                boolean selected = tc.getSelectedText() != null;
                Transferable tr = clipboard.getContents(null);

                cut.setEnabled(editable && selected);
                copy.setEnabled(selected);
                paste.setEnabled(editable && tr != null && tr.isDataFlavorSupported(DataFlavor.stringFlavor));
                selectAll.setEnabled(!tc.getText().isEmpty());

                menu.show(tc, e.getX(), e.getY());
            }
        };
    }

    /**
     * Adds right-click menu to all text components in the supplied array.
     * @param tcs An array of text components.
     */
    public static void addMenuTo(JTextComponent[] tcs) {
        for (JTextComponent tc : tcs) {
            addMenuTo(tc);
        }
    }

    /**
     * Adds right-click menu to the supplied text component.
     * @param tc A text component.
     */
    public static void addMenuTo(JTextComponent tc) {
        tc.addMouseListener(mouseAdapter);
    }
}
