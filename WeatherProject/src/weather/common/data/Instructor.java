

package weather.common.data;

import java.io.Serializable;

/**
 * An instructor is a user who has instructor rights in the system that
 * can manage classes, lessons, bookmarks, events, and images.
 *
 * @author Ioulia Lee
 * @version Spring 2010
 */
public class Instructor extends User implements Serializable {

    private static final long serialVersionUID = 1;

    /**
     * Creates an instructor with the given information.
     *
     * @param userNumber The userNumber that this instructor has in the system.
     * @param loginId The loginId that this instructor has.
     * @param password The password that this instructor has.
     * @param emailAddress The emailAddress that this instructor has.
     * @param firstName The first name that this instructor has.
     * @param lastName The last name that this instructor has.
     * @param userType The userType is instructor.
     * @param notes The notes that this instructor has.
     */
    public Instructor(int userNumber, String loginId, String password,
            String emailAddress,
            String firstName, String lastName, UserType userType,
            String notes) {
        super(userNumber, loginId, password, emailAddress, firstName,
                lastName, userType, notes);
    }

    /**
     * Creates an instructor with the field values of the supplied user.
     * 
     * @param user The user object with which to create an instructor object.
     */
    public Instructor(User user) {
        super(user.getUserNumber(), user.getLoginId(), user.getPassword(),
                user.getEmailAddress(), user.getFirstName(), user.getLastName(),
                user.getUserType(), user.getNotes());
    }

}
