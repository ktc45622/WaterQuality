package weather.common.data.bookmark;

/**
 * A simple enumeration class for bookmarkAlternative on bookmarks.
 *
 * NOTE: The string names mirror the database names.
 *
 * @author Joseph Horro (Format taken from BookmarkImageRank by Eric Subach)
 * @version Spring 2011
 */
public enum BookmarkDuration implements java.io.Serializable {
    instance("Instance"),
    event("Event");

   private String stringValue;


    BookmarkDuration (String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString () {
        return (stringValue);
    }

    /**
     * Return the BookmarkImageRank with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     * @return An enumeration with that string value.
     */
    public static BookmarkDuration getEnum (String value) {
       BookmarkDuration type = null;

        for (BookmarkDuration e : BookmarkDuration.values ()) {
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
