
package weather.common.data;

/**
 * An enumeration containing the possible types of web links in our system.
 *
 * Example:
 *
 * <pre>
 * WebLinkType type = WebLinkType.LINK;
 *
 * if(type.equals(WebLinkType.FORECAST))...
 * </pre>
 *
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public enum WebLinkType implements java.io.Serializable {

    /**
     * Means a web link is of LINK type.
     */
    LINK,

    /**
     * Means a web link is of FORECAST type.
     */
    FORECAST;
    
    private static final long serialVersionUID = 1L;

}
