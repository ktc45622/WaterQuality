package weather.common.data.note;

import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.WeatherResourceType;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * Represents a list of <code>Note</code>s.
 *
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 * @version 2010
 */
@SuppressWarnings("unchecked")
public class NoteList extends ResourceInstance {

    private ArrayList<Note> notes;
    
    /**
    * Determines if a de-serialized file is compatible with this class.
    *
    * Not necessary to include in first version of the class, but included here
    * as a reminder of its importance. Maintainers must change this value if
    * and only if the new version of this class is not compatible with old
    * versions.
    *
    * @see <a href="http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html">Java specification for
    * serialization</a>
    * @serial
    */
   private static final long serialVersionUID = 1L;

    /**
     * The constructor which configures the note ArrayList object and sets
     * the resource type to WeatherNotes.
     */
    public NoteList() {
        notes = new ArrayList<Note>();
        this.setResourceType(WeatherResourceType.WeatherNotes);
    }

    /**
     * Adds a new note to the list of local notes.
     *
     * @param note The new note object to be added to the ArrayList.
     */
    public void addNote(Note note) {
        Debug.println(notes.indexOf(note));
        if (notes.indexOf(note) == -1) {
            notes.add(note);
        } else {
            notes.set(notes.indexOf(note), note);
        }
    }

    /**
     * Gets a note at a specific location in the ArrayList of local notes.
     *
     * @param index The index of the requested note in the ArrayList.
     * @return The note at the specific location.
     */
    public Note getNote(int index) {
        return notes.get(index);
    }

    /**
     * Gets a note that has the specified date.Otherwise returns new Notes with given date.
     *
     * @param noteDate The date of the requested note in the ArrayList.
     * @return The note with the specific date.
     */
    public Note getNote(Date noteDate) {
        for (Note note : notes) {
            if (note.getDate().equals(noteDate)) {
                return note;
            }
        }
        return new Note(noteDate);
    }

    /**
     * Clears out all notes being stored in the ArrayList.
     */
    public void clear() {
        notes.clear();
    }

    /**
     * Gets the size of the list.
     *
     * @return The size of the array list.
     */
    public int size() {
        return notes.size();
    }

    /**
     * Reads a notes file in from the notes directory
     *
     * @param file A File object that essentially contains the directory path
     *      and file name of what is to be read in.
     * @throws WeatherException - The class of a notes object cannot be found.
     */
    @Override
    public void readFile(File file) throws WeatherException {
        notes.clear();
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            notes = (ArrayList<Note>)ois.readObject();
            ois.close();
            fis.close();
        } catch (EOFException ex) {
            notes = new ArrayList<Note>();
            writeFile(file);
        } catch (java.io.IOException ex) {
            //ex.printStackTrace();

            //No notes for student - Not fatal, not even dangerous
            //Not throwing a weather exception so that users with no notes
            //Are not bothered by a popup when they run the program.
            //In fact, its not really an exceptional event... quite common
            //really.
            //throw new WeatherException(2);
        } catch (ClassNotFoundException ex) {
            // ex.printStackTrace();
            //This, on the other hand, means something went wrong with
            //loading the notes. It is likely a corrupted notes file.
          WeatherLogger.log(Level.SEVERE, "Failed to read the student notes file " +
                  "because of a class mismatch problem.", ex);
          throw new WeatherException(3,"Failed to read the student notes file " +
                  "because of a class mismatch problem.\n");
        }
    }

    @Override
    public void readURL(URL url) throws IOException, WeatherException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Writes a notes file to the directory specified by file.
     *
     * @param file A File object that essentially contains the directory path
     *      and file name of what is to be saved.
     * @throws WeatherException  An error occurred while trying to write to the
     *      file.
     */
    @Override
    public void writeFile(File file) throws WeatherException {
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            ObjectOutputStream oOut = new ObjectOutputStream(fOut);
            oOut.writeObject(notes);
            oOut.close();
            fOut.close();
        } catch (java.io.IOException e) {
            throw new WeatherException(1);
        }
    }
}
