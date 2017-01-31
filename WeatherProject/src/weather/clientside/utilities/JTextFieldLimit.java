package weather.clientside.utilities;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * A class to create an object that will limit the size of a 
 * <cod>JTextField</code> or <code>JTextArea</code>. The code is from 
 * http://stackoverflow.com/questions/10136794/limiting-the-number-of-characters-in-a-jtextfield
 * @author Stack Overflow
 */
public class JTextFieldLimit extends PlainDocument {

    private int limit;

    public JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }

        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }
}
