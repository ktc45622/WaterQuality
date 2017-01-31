package weather.common.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import weather.common.utilities.PropertyManager;

/**
 * Instances of this class will represent a user in our system. One instance of
 * the class User will be equivalent to, and correspond to, one row in the User
 * database table.
 *
 * A User has a login identifier, a password, an email address, a first name, a
 * last name, and may contain notes about the user. Also, a user has a user type
 * and a user number.
 *
 * A user's login identifier is unique. A user's type is either 'student', 
 * 'instructor', 'administrator', or 'not_registered'.
 *
 * A user's number is a system supplied internal-identifying number for the
 * user. It is the user's primary key within the database.
 * 
 * A user's email address is currently not checked to be of a valid email format
 * (ex: mylogin@bloomu.edu).
 * 
 * Any program using this class must verify that the email address is valid
 * before using it. A user's email address should be checked by the
 * weather.utilities.DataFilter class elsewhere in the program.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Mike Graboske (2008)
 * @author Chad Hall (2008)
 * @author Joe Horro (2011)
 * @version Spring 2008
 * @see weather.common.utilities.DataFilter
 */
public class User implements java.io.Serializable {

    /**
     * The user number in our database.
     *
     * @serial
     */
    private int userNumber;
    /**
     * The login identifier of this user.
     *
     * @serial
     */
    private String loginId;
    /**
     * The password of this user.
     *
     * @serial
     */
    private String password;
    /**
     * The email address of this user.
     *
     * @serial
     */
    private String emailAddress;
    /**
     * The first name of this user.
     *
     * @serial
     */
    private String firstName;
    /**
     * The last name of this user.
     *
     * @serial
     */
    private String lastName;
    /**
     * The type of this user (i.e. Administrator, Instructor, ...).
     *
     * @serial
     */
    private UserType userType;
    /**
     * Any notes about this user.
     *
     * @serial
     */
    private String notes;
    /**
     * The number of logins for this user.
     *
     * @serial
     */
    private int numberOfLogins;
    /**
     * The last log in date for this user, before the current date.
     *
     * @serial
     */
    private Timestamp lastLogInDate;
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
    private static final long serialVersionUID = 1;

    /**
     * Constructor that initializes all of the fields of this class with the
     * given data given.
     *
     * @param userNumber The user number in our database for this user.
     * @param loginId The login identifier of this user.
     * @param password The password for this user.
     * @param emailAddress This user's contact email address.
     * @param firstName The first name of this user.
     * @param lastName The last name of this user.
     * @param userType The type of this user.
     * @param notes Any notes about this user.
     */
    public User(int userNumber, String loginId, String password,
            String emailAddress,
            String firstName, String lastName, UserType userType,
            String notes) {
        setUserNumber(userNumber);
        setLoginId(loginId);
        setPassword(password);
        setEmailAddress(emailAddress);
        setFirstName(firstName);
        setLastName(lastName);
        setUserType(userType);
        setNotes(notes);
        setNumberOfLogins(0);

        // Default properties for new users
        Calendar calendar = Calendar.getInstance();
        setLastLogInDate(new Timestamp(calendar.getTime().getTime()));
    }

    /**
     * Default constructor. 
     */
    public User() {
        setUserNumber(-1);
        setLoginId(null);
        setPassword(null);
        setEmailAddress(null);
        setFirstName(null);
        setLastName(null);
        setUserType(null);
        setNotes(null);
        setNumberOfLogins(0);
        Calendar calendar = Calendar.getInstance();
        setLastLogInDate(new Timestamp(calendar.getTime().getTime()));
    }

    /**
     * Sets the login identifier of this user.
     *
     * @param loginId The new login identifier of this User.
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * Returns the login identifier of this user.
     *
     * @return The loginId of the User.
     */
    public String getLoginId() {
        return this.loginId;
    }

    /**
     * Sets the user number for this user. 
     *
     * @param userNumber The new user number for this user.
     */
    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    /**
     * Returns this user's user number.
     *
     * @return The user number of this user.
     */
    public int getUserNumber() {
        return this.userNumber;
    }

    /**
     * Sets the password field of this user.
     * 
     * @param password The new password of this user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the password of this user.
     *
     * @return This user's password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the email address for this user.
     *
     * @param emailAddress The new email address of this user.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Returns the email address of this user.
     *
     * @return This user's email address.
     */
    public String getEmailAddress() {
        return this.emailAddress;
    }

    /**
     * Sets the first name of this user.
     *
     * @param firstName The new first name for this user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the first name of this user. 
     *
     * @return This user's first name.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the last name for this user.
     *
     * @param lastName This user's new last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the last name of this user.
     *
     * @return This user's last name.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the user type for this user. 
     *
     * @param userType The type for this User.
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Returns this user's current type.
     *
     * @return The current type of this user.
     */
    public UserType getUserType() {
        return this.userType;
    }

    /**
     * Sets the notes field of a User to a supplied String value. 
     * Any previous 'notes' are over-written.
     *
     * @param notes Any notes for this user.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the notes field of this user.
     *
     * @return The notes on or about this user.
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Returns the current number of logins.
     *
     * @return The number of logins for this user.
     */
    public int getNumberOfLogins() {
        return numberOfLogins;
    }

    /**
     * Sets the number of logins for this user.
     *
     * @param numberOfLogins - The number of logins this user has made.
     */
    public void setNumberOfLogins(int numberOfLogins) {
        this.numberOfLogins = numberOfLogins;
    }

    /**
     * A simple method to check if this is the first time a user logs in.
     * Note: When a new user is created, they are created with a 0 login count.
     *      When a user is retrieved from the database, the object retrieved has
     *      the original number of logins and is then updated.
     *
     * @return True if this is the first login by this this user object.
     */
     public boolean isFirstLogIn()
    {
         return (this.numberOfLogins == 0);
     }

    /**
     * Gets the timestamp for the last login date for this user, before the
     * current date.
     *
     * @return The last login date as a timestamp object.
     */
    public Timestamp getLastLogInDate() {
        return lastLogInDate;
    }

    /**
     * Gets a String representation for the last login date for this user, 
     * before the current date, in the format "MM/dd/yyyy 'at' h:mm a".
     * 
     * @return The last login date as a String formatted as 
     * "MM/dd/yyyy 'at' h:mm a"
     */
    public String getLastLogInDateInPrettyFormat()
    {
        return (new SimpleDateFormat("MM/dd/yyyy 'at' h:mm a").format(lastLogInDate));
    }

    /**
     * Sets the last login date for this user.
     *
     * @param lastLogInDate - The last login date for this user.
     */
    public void setLastLogInDate(Timestamp lastLogInDate) {
        this.lastLogInDate = lastLogInDate;
    }

    /**
     * This method overrides the default toString() method and returns a string
     * instance of a user as their fist name.
     *
     * @return A user's login ID.
     */
    @Override
    public String toString() {
        String message=null;
        if (userType == UserType.instructor ) message = " (Instructor)";
        else if(userType == UserType.administrator) message = " (Administrator)";
        else if(userType == UserType.student) message = " (Student)";
        else if(userType == UserType.guest) message = " (guest)";
        else message = "";
        return firstName + " " + lastName + message;
    }

    /**
     * This method compares users based on their user number.
     *
     * @param userNumber The user number to be compared with the current user.
     * @return True if the user numbers are the same, false otherwise.
     */
    public boolean equals(int userNumber) {
        if (this.userNumber == userNumber) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The hash value for a user is their user number.
     * @return The user number.
     */
    public int hash() {
        return userNumber;
    }

    /**
     * Sets the password field to the md5 encrypted version of the password
     * supplied
     *
     * @param password The password entered by the user
     */
    public void setEncryptedPassword(String password) {
        this.password = PropertyManager.encrypt(password);
    }

    /**
     * This method compares user's first and last names.
     *
     * @param firstName The first name of the second user.
     * @param lastName The last name of the second user.
     * @return -1, 0, or 1
     */
    public int compareTo(String firstName, String lastName) {
        if (this.lastName.compareTo(lastName) == 0) {
            if (this.firstName.compareTo(firstName) == 0) {
                return 0;
            } else {
                return this.firstName.compareTo(firstName);
            }
        } else {
            return this.lastName.compareTo(lastName);
        }
    }
    
    /**
     * This method compares user's first and last names.
     *
     * @param u The other user to be compared.
     * @return -1, 0, or 1
     */
    public int compareTo(User u) {
        if(this.lastName.toLowerCase().compareTo(u.getLastName().toLowerCase()) == 0) {
            if(this.firstName.toLowerCase().compareTo(u.getFirstName().toLowerCase()) == 0) {
                return 0;
            } else {
                return this.firstName.toLowerCase().compareTo(u.getFirstName().toLowerCase());
            }
        } else {
            return this.lastName.toLowerCase().compareTo(u.getLastName().toLowerCase());
        }
    }
}
