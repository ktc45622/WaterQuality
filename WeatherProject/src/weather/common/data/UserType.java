package weather.common.data;

import java.io.Serializable;

/**
 * An enumeration containing the possible types of users for our system.
 * 
 * Example:
 * 
 * <code>
 * UserType type = UserType.student;
 * 
 * if(type.equals(UserType.student))...
 * </code>
 * 
 * Values are not_registered, student, instructor, and administrator.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Jacob Kelly (2007)
 * @author Dave Moser (2008)
 * @version Spring 2010
 */
public enum UserType implements Serializable{

    /**
     * Means the user is a guest on the system.
     */
    guest,
    /**
     * Means the user is a student on the system.
     */
    student,
    /**
     * Means the user is an instructor on the system.
     */
    instructor,
    /**
     * Means the user is an administrator on the system.
     */
    administrator;

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
     */
    private static final long serialVersionUID = 1L;
}
