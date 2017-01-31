package weather.common.data.resource;

import java.io.Serializable;

/**
 * An enumeration containing the possible types of resource formats for our system.
 * 
 * example use:
 * 
 * <code>
 * ResourceFileFormatType type = ResourceFileFormatType.text;
 * 
 * if(type.equals(ResourceFileFormatType.text))...
 * </code>
 * 
 * @author Bloomsburg University Software Engineering
 * @author David Moser
 * @version Spring 2011
 */
public enum ResourceFileFormatType implements Serializable {
    unknown,
    comma_separated_values, 
    space_separated_values,
    text, //txt file extension
    jpeg, //jpg or jpeg file extension
    gif,//gif file extension
    png, //png file extension
    mjpg, //mjpg file extension
    avi, // avi file extension
    mp4, // mp4 file extension
    webpage, // any legal web page extension -- just added this one 4-2-2012
    zip, // zip or jar extension -- just added this one 4-2-2012
    image;  // any legal image extension -- not likely to be used use specific file formats above.

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

    /**
     * Return the enumeration value with the corresponding string value.
     *
     * @param str String representing the desired enumeration.
     * @return The desired enumeration or defaults to unknown if none exists.
     */
    public static ResourceFileFormatType enumFromString (String str) {
        for (ResourceFileFormatType formatType : ResourceFileFormatType.values()) {
            if (formatType.toString().equals(str)) {
                return formatType;
            }
        }
        return ResourceFileFormatType.unknown;
    }
}
