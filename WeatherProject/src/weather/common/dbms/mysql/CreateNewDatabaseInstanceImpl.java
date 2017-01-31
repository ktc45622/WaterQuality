package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import weather.common.dbms.CreateNewDatabaseInstance;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.Debug;

/**
 * This class creates a MySQL database with information obtained from the user
 * via dialog windows. The connection information contains the URL of the
 * database to be created, the user login, and the password. If all three are
 * valid, the connection is established and the tables are created, otherwise
 * the user is given an error message.
 *
 * @author Ioulia Lee (2010)
 * @author Eric Subach (2010)
 * @author Mike Young (2014)
 * @version Spring_2014
 */
public class CreateNewDatabaseInstanceImpl implements CreateNewDatabaseInstance {

    // Statement instance for issuing queries to the database.
    private Statement stmt;

    /**
     * Initializes the connection.
     */
    public CreateNewDatabaseInstanceImpl() {
        stmt = null;
    }

    /**
     * Displays dialog windows for a user to enter the following connection
     * information: a database URL of the form jdbc:mysql://host:port/database,
     * the database user requesting the connection, the user's password.
     * Attempts to establish a connection to a database with the entered
     * information. Calls <code>CreateDatabase(Connection conn)</code> method to
     * create tables.
     *
     * @return true if a database was created, false otherwise.
     * @throws SQLException if a database access error occurs, a connection to
     * the database could not be established
     */
    @Override
    public boolean createDatabase() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException e) {
            WeatherLogger.log(Level.SEVERE, "Missing expected class.", e);
            new WeatherException(0015, e, "Missing expected class.").show();
        } catch (InstantiationException e) {
            WeatherLogger.log(Level.SEVERE, "Failed instantiation.", e);
            new WeatherException(0016, e, "Failed instantiation.").show();
        } catch (IllegalAccessException e) {
            WeatherLogger.log(Level.SEVERE, "Program has just attempted to "
                    + "access something it shouldn't have.", e);
            new WeatherException(0017, e, "Program has just attempted to "
                    + "access something it shouldn't have.").show();
        }
        //Keep asking a user for the connection info until connection is
        //established. Display error message to a user in case of invalid url,
        //user, or password information.
        Connection conn = null;
        while (conn == null) {
            try {
                String url = JOptionPane.showInputDialog("Enter url:");
                String user = JOptionPane.showInputDialog("Enter user:");
                String password = JOptionPane.showInputDialog("Enter password:");

                conn = DriverManager.getConnection(url, user, password);

            } catch (SQLException e) {
                WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                        + "trying to connect to the database. The Connection "
                        + "object is null.", e);
                new WeatherException(4012, e, "Unable to connect to the database.").show();
            }
        }
        //Create the database when the connection is established        
        return createDatabase(conn);
    }

    /**
     * Creates tables for a database with the given <code>Connection</code>.
     * Tables are created in the order in which they are needed so they can be
     * properly linked together.
     *
     * @param conn the <code>Connection</code> to the database
     * @return true if the database was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * is called on a closed <code>Connection</code>
     */
    @Override
    public boolean createDatabase(Connection conn) throws SQLException {
        boolean success = false;
        try {
            stmt = conn.createStatement();
            Debug.println("Created statement object...");
            success = createUsersTable() && createCoursesTable()
                    && createEnrollmentTable() && createNotesTable()
                    && createResourcesTable() && createResourceRelationTable()
                    && createWeblinksTable() && createDefaultPicturesTable()
                    && createDefaultGenericNoDataPictureTable()
                    && createWeblinkCategoriesTable() && createStoredFilesTable()
                    && createTimeZoneInformationTable()
                    && createBookmarkCategoriesTable()
                    && createStaticBookmarkImagesTable();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                        + "trying to close a Connection object. The connection "
                        + "object is not null.", e);
            }
        }
        return success;
    }

    /**
     * Creates the <code>users</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createUsersTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "userNumber INT AUTO_INCREMENT PRIMARY KEY,"
                + "loginID VARCHAR(100) UNIQUE NOT NULL,"
                + "loginPassword VARCHAR(100) NOT NULL,"
                + "email VARCHAR(100),"
                + "firstName VARCHAR(20),"
                + "lastName VARCHAR(20),"
                + "userType ENUM('Unregistered','Student','Instructor','Administrator','Guest') NOT NULL DEFAULT 'Unregistered',"
                + "Notes TEXT) AUTO_INCREMENT = 100,"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>courses</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createCoursesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS courses ("
                + "courseNumber int(10) unsigned NOT NULL auto_increment,"
                + "departmentName varchar(25) default NULL,"
                + "classIdentifier varchar(25) default NULL,"
                + "section tinyint(3) unsigned default NULL,"
                + "className varchar(100) default NULL,"
                + "semesterType enum('Fall', 'Winter', 'Spring', 'Summer') NOT NULL DEFAULT 'Fall',"
                + "year int(4) NOT NULL,"
                + "instructorNumber int(10) unsigned default NULL,"
                + "PRIMARY KEY  (courseNumber)"
                + ") AUTO_INCREMENT=100,"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>enrollment</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createEnrollmentTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS enrollment ("
                + "userNumber int(10) unsigned NOT NULL,"
                + "courseNumber int(10) unsigned NOT NULL,"
                + "INDEX (userNumber, courseNumber))"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>notes</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createNotesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS notes ("
                + "noteNumber int(10) unsigned NOT NULL auto_increment,"
                + "noteTitle varchar(100) NOT NULL,"
                + "startTime datetime default NULL,"
                + "endTime datetime default NULL,"
                + "instructorNumber int(10) unsigned default NULL,"
                + "accessRights enum('Everyone','AllStudents','CourseStudents','Instructors','Private') default 'Everyone',"
                + "note text,"
                + "PRIMARY KEY  (noteNumber)"
                + ") AUTO_INCREMENT=100,"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>resources</code> table in the database. Since
     * <code>INTERVAL</code> is a keyword in MySQL, <code>time_interval</code>
     * is used as a column name instead.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createResourcesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS resources ("
                + "resourceNumber int(11) NOT NULL auto_increment,"
                + "type enum('undefined','WeatherCamera','WeatherSite','WeatherStation','WeatherStationValues','TextFile','WeatherStationDailyLog','WeatherStationHourlyLog','WeatherMovie','WeatherImage') NOT NULL default 'undefined',"
                + "name varchar(200) NOT NULL,"
                + "retrievalMethod enum('URL','FileLoad','Manual','undefined') NOT NULL default 'undefined',"
                + "storageFolderName varchar(200) default NULL,"
                + "format enum('unknown','comma_separated_values','txt','jpeg','gif','png','mov','mjpg','image','space_separated_values') default 'unknown',"
                + "URL blob default NULL,"
                + "timeInterval int(11) unsigned NOT NULL,"
                + "active tinyint(1) unsigned default '1',"
                + "valid tinyint(1) unsigned default '1',"
                + "dateInitiated date default NULL,"
                + "collectionSpan enum('DaylightHours','FullTime','SpecifiedTimes'),"
                + "startTime int(11) default '0',"
                + "endTime int(11) default '0',"
                + "latitude float default '41.0068',"
                + "longitude float default '-76.4143',"
                + "PRIMARY KEY  (resourceNumber),"
                + "UNIQUE KEY (name)) AUTO_INCREMENT=100,"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>resource_relation</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createResourceRelationTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS resource_relation ("
                + "cameraNumber int(10) unsigned NOT NULL,"
                + "stationNumber int(10) unsigned default NULL,"
                + "PRIMARY KEY (cameraNumber))"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>weblinks</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createWeblinksTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS weblinks ("
                + "linkNumber int(10) NOT NULL auto_increment PRIMARY KEY,"
                + "name varchar(45) NOT NULL,"
                + "URL varchar(100) NOT NULL,"
                + "type enum('LINK', 'FORECAST') NOT NULL default 'LINK',"
                + "linkCategoryNumber int(10) NOT NULL)"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>default_pictures</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createDefaultPicturesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS default_pictures ("
                + "resourceNumber int(11) NOT NULL,"
                + "defaultNighttimeImage long varbinary NOT NULL,"
                + "defaultDaytimeImage long varbinary NOT NULL,"
                + "PRIMARY KEY (resourceNumber))"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>default_generic_no_data_picture</code> table.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createDefaultGenericNoDataPictureTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS default_generic_no_data_picture("
                + "noDataImage mediumblob NOT NULL)"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>weblink_categories</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createWeblinkCategoriesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS weblink_categories ("
                + "weblinkCategoryNumber int(10) unsigned NOT NULL auto_increment,"
                + "weblinkCategory varchar(100) NOT NULL,"
                + "PRIMARY KEY (weblinkCategoryNumber))"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>stored_files</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createStoredFilesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS stored_files ("
                + "fileNumber int(11) unsigned NOT NULL auto_increment,"
                + "dataType varchar(30) NOT NULL,"
                + "dataNumber int(11) NOT NULL,"
                + "instructorNumber int(11) NOT NULL,"
                + "fileName varchar(255) NOT NULL,"
                + "fileContent mediumblob default NULL,"
                + "PRIMARY KEY (noteFileNumber))"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>time_zone_info</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createTimeZoneInformationTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS time_zone_information ("
                + "timeZoneInformationNumber int(10) unsigned NOT NULL auto_increment,"
                + "resourceNumber int(11) NOT NULL,"
                + "timeZone varchar(30) NOT NULL,"
                + "PRIMARY KEY (timeZoneInformationNumber))"
                + "ENGINE = InnoDB";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>bookmark_categories</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createBookmarkCategoriesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS bookmark_categories ("
                + "bookmarkCategoryNumber int(10) unsigned NOT NULL auto_increment,"
                + "name varchar(100) UNIQUE NOT NULL,"
                + "note varchar(250),"
                + "PRIMARY KEY (bookmarkCategoryNumber))"
                + "ENGINE = InnoDB, AUTO_INCREMENT = 100";

        return !stmt.execute(sql);
    }

    /**
     * Creates the <code>static_bookmark_images</code> table in the database.
     *
     * @return true if the table was created, false otherwise
     * @throws SQLException if a database access error occurs, or if this method
     * attempts to use a closed <code>Connection</code> or
     * <code>Statement</code>
     */
    private boolean createStaticBookmarkImagesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS static_bookmark_images ("
                + "bookmarkNumber int(10) unsigned NOT NULL auto_increment,"
                + "name varchar(100) UNIQUE NOT NULL,"
                + "categoryNumber int(10) unsigned NOT NULL,"
                + "instructorNumber INT NOT NULL,"
                + "resourceNumber int(11),"
                + "image long varbinary NOT NULL,"
                + "time time DEFAULT NULL,"
                + "ranking ENUM('Not Ranked', 'Acceptable', 'Average', 'Good', 'Excellent') NOT NULL DEFAULT 'Not Ranked',"
                + "note varchar(600),"
                + "PRIMARY KEY (bookmarkNumber))"
                + "ENGINE = InnoDB, AUTO_INCREMENT = 100";

        return !stmt.execute(sql);
    }
}
