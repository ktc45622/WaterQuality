package weather.common.data.bookmark;

/**
 * A simple enumeration class for view rights on bookmarks.
 *
 * NOTE: The string names mirror the database names.
 *
 * @author Joseph Horro (Format taken from BookmarkImageRank by Eric Subach)
 * @version Spring 2011
 */
public enum CategoryViewRights implements java.io.Serializable {
    system_wide("system_wide"),
    instructor_only("instructor_only");
    
   private String stringValue;

    
    CategoryViewRights (String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString () {
        return (stringValue);
    }

    /**
     * Return the BookmarkImageRank with the corresponding string value.
     *
     * @param value The string value of the enumeration.
     * @return The enumeration with that string value.
     */
    public static CategoryViewRights getEnum (String value) {
       CategoryViewRights access = null;

        for (CategoryViewRights e : CategoryViewRights.values ()) {
            if (e.stringValue.equals (value))
            {
                access = e;
                break;
            }
        }
        return (access);
    }

    private static final long serialVersionUID = 1L;
};
