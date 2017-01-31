package weather.common.data.note;

/**
 * This enumeration declares the type of a note. The choices are Unspecified,
 * General, Lesson, StaticEvent, DynamicEvent, and Personal.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @version Spring 2008
 */
public enum NoteType implements java.io.Serializable{

    /**
     * Means the type of the note was never specified.
     */
    Unspecified("Unspecified"),
    /**
     * Means the type of the note is general.
     */
    General("General"),
    /**
     * Means the note is a lesson note.
     */
    Lesson("Lesson"),
    /**
     * Means the note is about static, unchanging, event.
     */
    StaticEvent("StaticEvent"),
    /**
     * Means the not is about a dynamic, changing, event.
     */
    DynamicEvent("DynamicEvent"),
    /**
     * Means the note is a personal note.
     */
    Personal("Personal");
    /**
    * Determines if a de-serialized file is compatible with this class.
    *
    * Not necessary to include in first version of the class, but included here
    * as a reminder of its importance. Maintainers must change this value if
    * and only if the new version of this class is not compatible with old
    * versions.
    *
    * @see <a href="http://java.sun.com/products/jdk/1.1/docs/guide
    * /serialization/spec/version.doc.html">Java specification for
    * serialization</a>
    * @serial
    */
   private String stringValue;


    NoteType (String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString () {
        return (stringValue);
    }

    /**
     * Return the NoteType with the corresponding string value.
     *
     * @param value A String value of the enumeration.
     * @return An Enumeration with that String value.
     */
    public static NoteType getEnum (String value) {
       NoteType type = null;

        for (NoteType e : NoteType.values ()) {
            if (e.stringValue.equals (value))
            {
                type = e;
                break;
            }
        }
        return (type);
    }

    private static final long serialVersionUID = 1L;
};
