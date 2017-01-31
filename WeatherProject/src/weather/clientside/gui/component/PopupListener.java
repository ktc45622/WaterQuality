package weather.clientside.gui.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * Listener that triggers a <code>JPopupMenu</code> to appear on right-click.
 *
 * @author Eric Subach (2011)
 */
public class PopupListener extends MouseAdapter {
    private JPopupMenu menu;

    public PopupListener (JPopupMenu menu) {
        this.menu = menu;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            menu.show(e.getComponent(),
                       e.getX(), e.getY());
        }
    }
     /**
   * @return the popup menu.
   */
  public final JPopupMenu getPopup() {
    return this.menu;
  }
}