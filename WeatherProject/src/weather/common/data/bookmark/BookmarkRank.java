package weather.common.data.bookmark;

/**
 * Represents the ranking of a bookmark image.
 *
 * NOTE: The string names mirror the database names.
 *
 * @author Eric Subach (2010)
 */
public enum BookmarkRank implements java.io.Serializable {

    NOT_RANKED("Not Ranked"),
    ACCEPTABLE("Acceptable"),
    AVERAGE("Average"),
    GOOD("Good"),
    EXCELLENT("Excellent");
    private String stringValue;

    BookmarkRank(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return (stringValue);
    }

    /**
     * Return the BookmarkRank with the corresponding string value.
     *
     * @param value String value of the enumeration.
     * @return An enumeration with that string value.
     */
    public static BookmarkRank getEnum(String value) {
        BookmarkRank rank = null;

        for (BookmarkRank e : BookmarkRank.values()) {
            if (e.stringValue.equals(value)) {
                rank = e;
                break;
            }
        }

        return (rank);
    }
    private static final long serialVersionUID = 1L;
};
