package weather.common.data.resource;

/**
 * This enumeration specifies the different types of Collection Spans for resources.
 * It mirrors a field in the database and the types are as follows:
 * <b>DaylightHours</b>: This resource should be collected only between sunrise and sunset
 * <b>FullTime</b>: This resource should be collected 24/7
 * <b>SpecifiedTimes</b>: Only collect this resource between the designated StartTime and EndTime
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @version Spring 2008
 */
public enum ResourceCollectionSpan implements java.io.Serializable{
    /**
     * Only the hours that are daylight during the day.
     */
    DaylightHours("Daylight Hours"),
    /**
     * The whole day.
     */
    FullTime("Full Time"),
    /**
     * Only certain specified hours.
     */
    SpecifiedTimes("Specified Times");
    
    private ResourceCollectionSpan(String name){
        this.stringValue=name;
    }

    public static ResourceCollectionSpan getEnum(String value) {
        ResourceCollectionSpan type = null;

        for (ResourceCollectionSpan t : ResourceCollectionSpan.values()) {
            if (t.stringValue.equals(value)) {
                type = t;
                break;
            }
        }

        return type;
    }
    
    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun documents
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but
     * included here as a reminder of its importance.
     */
    private static final long serialVersionUID = 1L;
    private String stringValue;
}