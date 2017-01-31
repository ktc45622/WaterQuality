package weather.common.dbms.mysql;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.AccessRights;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkRank;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class manages interactions with the bookmarks table in the
 * database.
 * 
 * @author Joseph Horro
 * @version Spring 2011
 */
public final class MySQLBookmarkManager implements DBMSBookmarkInstanceManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;

    public MySQLBookmarkManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    /**
     * Adds a new bookmark. Requires a pre-constructed bookmark image.
     * This method will update the object passed in with the new auto generated
     * key from the database as the objects bookmarkNumber field. Note:
     * this object will return false if it fails or if the object to be inserted
     * does not have a bookmarkNumber of -1.
     * 
     * @param bookmark The bookmark to add.
     * @return True if added, false otherwise.
     */
    @Override
    public boolean add(Bookmark bookmark) {
        // make sure this is a new object
        if (bookmark.getBookmarkNumber() != -1) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;

        try {
            conn = dbms.getLocalConnection();

            String sql = "INSERT INTO bookmarks ( bookmarkCategoryNumber , "
                    + "bookmarkTypeNumber, name, createdBy, accessRights, "
                    + "startTime, endTime, ranking, weatherCameraResourceNumber, "
                    + "weatherMapLoopResourceNumber, weatherStationResourceNumber, "
                    + "weatherCameraPicture, weatherMapPicture, " 
                    + "weatherStationPicture, notes, dpStartTime, dpEndTime,"
                    + "dpFitted, dpGraphSelection, dpDaySpanSelection) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, "
                    + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);

            ps.setInt(1, bookmark.getCategoryNumber());
            ps.setInt(2, bookmark.getTypeNumber());
            ps.setString(3, bookmark.getName());
            ps.setInt(4, bookmark.getCreatedBy());
            ps.setString(5, bookmark.getAccessRights().toString());
            ps.setTimestamp(6, new Timestamp(bookmark.getStartTime().getTime()));
            ps.setTimestamp(7, new Timestamp(bookmark.getEndTime().getTime()));
            ps.setString(8, bookmark.getRanking().toString());
            ps.setInt(9, bookmark.getWeatherCameraResourceNumber());
            ps.setInt(10, bookmark.getWeatherMapLoopResourceNumber());
            ps.setInt(11, bookmark.getWeatherStationResourceNumber());

            if (bookmark.getWeatherCameraPicture() == null) {
                ps.setBinaryStream(12, new ByteArrayInputStream(new byte[0]));
            } else {
                ps.setBinaryStream(12,
                        new ByteArrayInputStream(bookmark.getWeatherCameraPicture().getImageBytes()),
                        bookmark.getWeatherCameraPicture().length());
            }

            if (bookmark.getWeatherMapPicture() == null) {
                ps.setBinaryStream(13, new ByteArrayInputStream(new byte[0]));
            } else {
                ps.setBinaryStream(13,
                        new ByteArrayInputStream(bookmark.getWeatherMapPicture().getImageBytes()),
                        bookmark.getWeatherMapPicture().length());
            }

            if (bookmark.getWeatherStationPicture() == null) {
                ps.setBinaryStream(14, new ByteArrayInputStream(new byte[0]));
            } else {
                ps.setBinaryStream(14,
                        new ByteArrayInputStream(bookmark.getWeatherStationPicture().getImageBytes()),
                        bookmark.getWeatherStationPicture().length());
            }

            ps.setString(15, bookmark.getNotes());

            ps.setTimestamp(16, new Timestamp(bookmark.getPlotRangeStartTime().getTime()));
            ps.setTimestamp(17, new Timestamp(bookmark.getPlotRangeEndTime().getTime()));
            ps.setBoolean(18, bookmark.getGraphFittedOption());
            ps.setString(19, bookmark.getSelectedRadioName());
            ps.setInt(20, bookmark.getPlotDaySpanComboBoxIndex());
            
            int key = MySQLHelper.executeStatementAndReturnGeneratedKey(ps);

            if (key > 0) {
                bookmark.setBookmarkNumber(key);
                isSuccessful = true;
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
        } catch (Exception e) {
            //@Todo: this is horrible, I know, but it was the only way to find
            //out why and where the program was throwing an exception, we should
            //really be throwing a null pointer exception, but general was
            //easier to do.
            //e.printStackTrace();
            //Debug.println(e);
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }

    /**
     * Gets all the bookmark instances from the bookmark instances table.
     * 
     * @return A vector of all bookmark instances, empty if there are none.
     */
    @Override
    public Vector<Bookmark> obtainAll() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Removes the bookmark given.  Deletes any files associated
     * with the given bookmark from the stored_files table.
     * 
     * @param bookmark The bookmark to remove.
     * @return True if removed, false otherwise.
     */
    @Override
    public boolean removeOne(Bookmark bookmark) {
        // check to see if this is a valid bookmark
        if (bookmark.getBookmarkNumber() == -1) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isSuccessful = false;
        try {
            conn = dbms.getLocalConnection();
            String sql = "DELETE FROM bookmarks "
                    + "WHERE bookmarkNumber = (?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmark.getBookmarkNumber());
            // attached files removed via database trigger
            isSuccessful = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }

    /**
     * Removes all bookmarks in a category.
     * 
     * @param categoryNumber The category number used to filter the deletion.
     * @return The number of records effected.
     */
    @Override
    public int removeManyByCategoryNumber(int categoryNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM bookmarks "
                    + "WHERE bookmarkCategoryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);

            // if the update was successful, test whether any rows were affected,
            // and return how many
            if (!ps.execute()) {
                rowsAffected = ps.getUpdateCount();
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return rowsAffected;
    }

    /**
     * Remove all bookmarks with a certain type number.
     * 
     * @param typeNumber The type number to filter deletions by.
     * @return The number of records effected.
     */
    @Override
    public int removeManyByTypeNumber(int typeNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM bookmarks "
                    + "WHERE bookmarkTypeNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, typeNumber);

            // if the update was successful, test whether any rows were affected,
            // and return how many
            if (!ps.execute()) {
                rowsAffected = ps.getUpdateCount();
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return rowsAffected;
    }

    /**
     * Removes all bookmarks by a user.
     * 
     * @param userNumber The user number to filter deletion by.
     * @return The number of records effected.
     */
    @Override
    public int removeManyByUserNumber(int userNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM bookmarks "
                    + "WHERE createdBy = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userNumber);

            // if the update was successful, test whether any rows were affected,
            // and return how many
            if (!ps.execute()) {
                rowsAffected = ps.getUpdateCount();
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return rowsAffected;
    }

    /**
     * Removes all bookmarks with a specific rank.
     * 
     * @param rank The rank of bookmark to remove.
     * @return The number of records effected.
     */
    @Override
    public int removeManyByRanking(BookmarkRank rank) {
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM bookmarks "
                    + "WHERE ranking = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, rank.toString());

            // if the update was successful, test whether any rows were affected,
            // and return how many
            if (!ps.execute()) {
                rowsAffected = ps.getUpdateCount();
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return rowsAffected;
    }

    /**
     * Retrieves a bookmark by primary key.
     * 
     * @param bookmarkNumber The key to match in the search.
     * @return A new bookmark instance matching the number or null if there is none
     */
    @Override
    public Bookmark searchByBookmarkNumber(int bookmarkNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Bookmark bm = null;
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmarkNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                bm = MySQLHelper.makeBookmarkFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return bm;
    }

    /**
     * Finds all bookmarks created by a particular user.
     * 
     * @param createdBy The userNumber of the user who created the bookmarks.
     * @return A vector of bookmark instance objects created by a user, empty if none.
     */
    @Override
    public Vector<Bookmark> searchByCreatedBy(int createdBy) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();

        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE createdBy = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, createdBy);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Finds all bookmarks with a certain access right.
     * 
     * @param accessRights The access right to filter the search by.
     * @return A vector of bookmark instance objects, empty if none.
     */
    @Override
    public Vector<Bookmark> searchByAccessRights(AccessRights accessRights) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();

        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE accessRights = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, accessRights.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Finds all bookmarks with a certain rank.
     * 
     * @param ranking The rank to filter the search by.
     * @return A vector of bookmark instance objects, empty if none meeting criteria.
     */
    @Override
    public Vector<Bookmark> searchByRank(BookmarkRank ranking) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();

        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE ranking = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, ranking.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Finds all bookmarks within a certain category.
     * 
     * @param categoryNumber The bookmark category to filter search results by.
     * @return A vector of bookmark instance objects, empty if none.
     */
    @Override
    public Vector<Bookmark> searchByCategoryNumber(int categoryNumber) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();

        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkCategoryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Finds all bookmarks with a certain type.
     * @param typeNumber The type to filter search results by.
     * @return A vector of bookmark instance objects, empty if none meeting the criteria.
     */
    @Override
    public Vector<Bookmark> searchByTypeNumber(int typeNumber) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();

        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkTypeNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, typeNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Updates the given bookmark.
     * @param bookmark The bookmark to update.
     * @return True if successful, false otherwise.
     */
    @Override
    public boolean update(Bookmark bookmark) {
        // make sure this is not a new object
        if (bookmark.getBookmarkNumber() == -1) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;

        try {
            String sql = "UPDATE bookmarks SET "
                    + "bookmarkCategoryNumber = (?), "
                    + "bookmarkTypeNumber = (?), name = (?), createdBy = (?), "
                    + "accessRights = (?), startTime = (?), endTime = (?), "
                    + "ranking = (?), notes = (?)"
                    + "WHERE bookmarkNumber = (?)";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, bookmark.getCategoryNumber());
            ps.setInt(2, bookmark.getTypeNumber());
            ps.setString(3, bookmark.getName());
            ps.setInt(4, bookmark.getCreatedBy());
            ps.setString(5, bookmark.getAccessRights().toString());
            ps.setTimestamp(6, new Timestamp(bookmark.getStartTime().getTime()));
            ps.setTimestamp(7, new Timestamp(bookmark.getEndTime().getTime()));
            ps.setString(8, bookmark.getRanking().toString());
            ps.setString(9, bookmark.getNotes());
            ps.setInt(10, bookmark.getBookmarkNumber());

            isSuccessful = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }
        
    /** Returns all the bookmarks this user can view based on the user's
     * access rights and type. Providing the instructor list is necessary if dealing
     * with a student account, otherwise null may be used or an empty vector.
     * @param user - the user who wants to view all bookmarks
     * @param instructorList  - the list of instructors a student may have at a given time
     * available in the system to him/her.
     * @return A vector of BookmarkInstances viewable by the user, empty if none.
     */
    @Override
    public Vector<Bookmark> searchAllBookmarksViewableByUser(User user, Vector<User> instructorList) {
         Vector<Bookmark> temp = new Vector<Bookmark>();
         if (user.getUserType() == UserType.administrator) {
            temp.addAll(this.obtainAll());
        }
         else if (user.getUserType() == UserType.instructor){
             temp.addAll(this.searchAllBookmarksViewableByInstructors());
             temp.addAll(this.searchAllBookmarksViewableByEveryone());
             temp.addAll(this.searchByCreatedBy(user.getUserNumber()));
         }
         else if (user.getUserType() == UserType.guest){
             temp.addAll(this.searchAllBookmarksViewableByGuest());
         }
         else if (user.getUserType() == UserType.student){
             temp.addAll(this.searchAllBookmarksViewableByAStudentInClasses(instructorList));
         }
         return temp;
    }
    
    /**
     * Returns a vector of all the bookmark instances a user may see with a
     * given type number.
     * @param user the user
     * @param typeNumber the bookmark type number
     * @param instructorList the list of instructors a student may have in their courses
     * @return A vector of all the bookmark instance objects fitting this 
     * criteria, empty if none.
     */
    @Override
    public Vector<Bookmark> searchByTypeNumberForUser(User user, 
            int typeNumber, Vector<User> instructorList){
        if(user.getUserType() == UserType.administrator)
            return this.searchByTypeNumber(typeNumber);
        else if (user.getUserType() == UserType.instructor)
            return this.searchByTypeNumberForAnInstructor(typeNumber, user);
        else if (user.getUserType() == UserType.student)
            return this.searchByTypeNumberForAStudent(typeNumber, instructorList);
        else return this.searchByTypeNumberForAGuest(typeNumber);
    }

    /**
     * Returns a vector of all the bookmark instances a user may see with a given
     * category number.
     * @param user the user
     * @param categoryNumber the bookmark category number
     * @param instructorList a list of instructors if the user is a student, may be null
     * or an empty vector otherwise
     * @return a vector of all the bookmark instance objects fitting this criteria, empty if none.
     */
    @Override
    public Vector<Bookmark> searchByCategoryNumberForUser(User user, int categoryNumber,
            Vector<User> instructorList){
        if(user.getUserType() == UserType.administrator)
            return this.searchByCategoryNumber(categoryNumber);
        else if (user.getUserType() == UserType.instructor)
            return this.searchByCategoryNumberForAnInstructor(categoryNumber, user);
        else if (user.getUserType() == UserType.student)
            return this.searchByCategoryNumberForAStudent(categoryNumber, instructorList);
        else return this.searchByCategoryNumberForAGuest(categoryNumber);
    }

    /** Returns all the bookmarks in the provided time range that this user can
     * view based on the user's
     * access rights and type.
     * @param user - the user who wants to view all bookmarks
     * available in the system to him/her.
     * @param startDate - the date to begin the search.
     * @param endDate - the date to end the search.
     * @param instructorList  - the list of instructors a student may have over their courses
     * @return A vector of BookmarkInstances viewable by the user within a time range, empty if none.
     */
    @Override
    public Vector<Bookmark> searchAllBookmarksViewableByUserWithinTimeRange(User user, Date startDate,
            Date endDate, Vector<User> instructorList){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();

        try {
            String sql="";
            
            if (user.getUserType() == UserType.instructor){
                sql= "SELECT * FROM bookmarks "
                    + "WHERE startTime <= (?) AND startTime >= (?) AND ("
                        + "accessRights = (?) OR accessRights = (?) OR "
                        + "createdBy = (?) )";
                conn = dbms.getLocalConnection();
                ps = conn.prepareStatement(sql);
                ps.setDate(1, endDate);
                ps.setDate(2, startDate);
                ps.setString(3, AccessRights.Everyone.toString());
                ps.setString(4, AccessRights.Instructors.toString());
                ps.setInt(5, user.getUserNumber());
            }
            
            else if (user.getUserType() == UserType.student){
                sql= "SELECT * FROM bookmarks "
                    + "WHERE startTime <= (?) AND startTime >= (?) AND ("
                        + "accessRights = (?) OR accessRights = (?) )";
                conn = dbms.getLocalConnection();
                ps = conn.prepareStatement(sql);
                ps.setDate(1, endDate);
                ps.setDate(2, startDate);
                ps.setString(3, AccessRights.Everyone.toString());
                ps.setString(4, AccessRights.AllStudents.toString());
            }
            
            else if (user.getUserType() == UserType.guest){
                sql= "SELECT * FROM bookmarks "
                    + "WHERE startTime <= (?) AND startTime >= (?) AND ("
                        + "accessRights = (?) OR accessRights = (?) OR "
                        + "accessRights = (?) )";
                conn = dbms.getLocalConnection();
                ps = conn.prepareStatement(sql);
                ps.setDate(1, endDate);
                ps.setDate(2, startDate);
                ps.setString(3, AccessRights.Everyone.toString());
                ps.setString(4, AccessRights.AllStudents.toString());
                ps.setString(5, AccessRights.CourseStudents.toString());
            }
            
            else if (user.getUserType() == UserType.administrator){
                sql= "SELECT * FROM bookmarks "
                    + "WHERE startTime <= (?) AND startTime >= (?)";
                conn = dbms.getLocalConnection();
                ps = conn.prepareStatement(sql);
                ps.setDate(1, endDate);
                ps.setDate(2, startDate);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        if (user.getUserType() == UserType.student){
            for(User u: instructorList){
                list.addAll(this.searchByDateAndTimeForAStudentWithGivenInstructor(
                    u, startDate, endDate));
            }
        }
        return list;

    }

    private Vector<Bookmark> searchByDateAndTimeForAStudentWithGivenInstructor(User instructor,
            Date startDate, Date endDate){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql= "SELECT * FROM bookmarks "
                    + "WHERE startTime <= (?) AND startTime >= (?) AND "
                        + "accessRights = (?) AND createdBy = (?)";
                conn = dbms.getLocalConnection();
                ps = conn.prepareStatement(sql);
                ps.setDate(1, endDate);
                ps.setDate(2, startDate);
                ps.setString(3, AccessRights.CourseStudents.toString());
                ps.setInt(4, instructor.getUserNumber());
                rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }

        return list;
    }
    
    /**
     * TODO: Documentation needed here
     * @param categoryNumber
     * @param instructor
     * @return A vector of bookmarks.
     */
    private Vector<Bookmark> searchByCategoryNumberForAnInstructor(int categoryNumber, User instructor){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        list =this.searchByCategoryNumberWithAGivenInstructor(instructor, categoryNumber);
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkCategoryNumber = (?) AND "
                    + "( accessRights = (?) OR accessRights = (?) )";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            ps.setString(2, AccessRights.Everyone.toString());
            ps.setString(3, AccessRights.Instructors.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                boolean isBroken = false;
                for(Bookmark b: list){
                    if (MySQLHelper.makeBookmarkFromResultSet(rs).getBookmarkNumber() == b.getBookmarkNumber()){
                        isBroken = true;
                        break;
                    }
                }
                if (!isBroken)
                    list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }
    
    /**
     * TODO: Documentation needed here
     * @param typeNumber
     * @param instructor
     * @return A vector of bookmarks.
     */
    private Vector<Bookmark> searchByTypeNumberForAnInstructor(int typeNumber, User instructor){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        list= this.searchByTypeNumberWithAGivenInstructor(instructor, typeNumber);
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkTypeNumber = (?) AND ( accessRights = (?)"
                    + "OR accessRights = (?) )";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, typeNumber);
            ps.setString(2, AccessRights.Everyone.toString());
            ps.setString(3, AccessRights.Instructors.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                boolean isBroken = false;
                for(Bookmark b: list){
                    if (MySQLHelper.makeBookmarkFromResultSet(rs).getBookmarkNumber() == b.getBookmarkNumber()){
                        isBroken = true;
                        break;
                    }
                }
                if (!isBroken)
                    list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * TODO: Documentation needed here.
     * @param categoryNumber
     * @return A vector of bookmarks.
     */
    private Vector<Bookmark> searchByCategoryNumberForAGuest(int categoryNumber){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkCategoryNumber = (?) AND "
                    + "(accessRights = (?) OR accessRights = (?) OR accessRights = (?) )";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            ps.setString(2, AccessRights.Everyone.toString());
            ps.setString(3, AccessRights.AllStudents.toString());
            ps.setString(4, AccessRights.CourseStudents.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                    list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * TODO: Documentation needed here
     * @param categoryNumber
     * @param instructorList
     * @return A vector of bookmarks.
     */
    private Vector<Bookmark> searchByCategoryNumberForAStudent(int categoryNumber,
            Vector<User> instructorList){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkCategoryNumber = (?) AND "
                    + "(accessRights = (?) OR accessRights = (?) )";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            ps.setString(2, AccessRights.Everyone.toString());
            ps.setString(3, AccessRights.AllStudents.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                    list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        for(User u: instructorList)
            list.addAll(this.searchByCategoryNumberForAStudentWithAGivenInstructor(categoryNumber, u));

        return list;
    }

    /**
     * TODO: Documentation needed here.
     * @param typeNumber
     * @return A vector of bookmarks.
     */
    private Vector<Bookmark> searchByTypeNumberForAGuest(int typeNumber){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkTypeNumber = (?) AND "
                    + " (accessRights = (?) OR accessRights = (?) OR accessRights = (?) )";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, typeNumber);
            ps.setString(2, AccessRights.Everyone.toString());
            ps.setString(3, AccessRights.AllStudents.toString());
            ps.setString(4, AccessRights.CourseStudents.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }

        return list;
    }

    /**
     * TODO: Documentation needed here.
     * @param typeNumber
     * @param instructorList
     * @return Vector of bookmarks.
     */
    private Vector<Bookmark> searchByTypeNumberForAStudent(int typeNumber, Vector<User> instructorList){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkTypeNumber = (?) AND "
                    + " (accessRights = (?) OR accessRights = (?) OR accessRights = (?) )";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, typeNumber);
            ps.setString(2, AccessRights.Everyone.toString());
            ps.setString(3, AccessRights.AllStudents.toString());
            ps.setString(4, AccessRights.CourseStudents.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        for(User u: instructorList)
            list.addAll(this.searchByTypeNumberForAStudentWithAGivenInstructor(typeNumber, u));
        return list;
    }

    /**
     * Allows a user if student to search for their instructors (Course student) bookmarks
     * if given by category number
     * @param categoryNumber the category number
     * @param instructor the user instructor
     * @return a vector containing the result criteria, or an empty vector if non is present
     */
    private Vector<Bookmark> searchByCategoryNumberForAStudentWithAGivenInstructor(
            int categoryNumber, User instructor){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkCategoryNumber = (?) AND "
                    + "createdBy = (?) AND accessRights = (?) ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            ps.setInt(2, instructor.getUserNumber());
            ps.setString(3, AccessRights.CourseStudents.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }

        return list;
    }

    /**
     * This allows a student user to search for a list of bookmarkinstances given
     * by their instructor id and the student is in the course
     * @param typeNumber the type number
     * @param instructor the user instructor
     * @return a vector containing the matching criteria or an empty vector if there is none
     */
    private Vector<Bookmark> searchByTypeNumberForAStudentWithAGivenInstructor(
            int typeNumber, User instructor){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkTypeNumber = (?) AND "
                    + " accessRights = (?) AND createdBy = (?) ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, typeNumber);
            ps.setString(2, AccessRights.CourseStudents.toString());
            ps.setInt(3, instructor.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }

        return list;
    }
    
    /**
     * Returns the search results of a vector with a given type of category number.
     * @param instructor the user instructor
     * @param categoryNumber the category number
     * @return a vector of the bookmark instances with the given criteria
     */
    private Vector<Bookmark> searchByCategoryNumberWithAGivenInstructor(User instructor, int categoryNumber){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkCategoryNumber = (?) AND "
                    + "createdBy = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            ps.setInt(2, instructor.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        
        return list;
    }
    
    /**
     * Returns a vector of bookmarks with a given type number in common.
     * @param instructor the user instructor
     * @param typeNumber the type number
     * @return a vector of the bookmark instances with the given criteria
     */
    private Vector<Bookmark> searchByTypeNumberWithAGivenInstructor(User instructor, int typeNumber){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();
        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE bookmarkTypeNumber = (?) AND "
                    + "createdBy = (?) AND accessRights =  ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, typeNumber);
            ps.setInt(2, instructor.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        
        return list;
    }

    /**
     * Returns all the bookmarks that a guest can view. This list will be composed
     * of bookmark instances from everyone, all students, and from course students.
     * A guest won't be able to see private or instructor.
     * @return A vector list of all bookmark instance objects
     */
    private Vector<Bookmark> searchAllBookmarksViewableByGuest() {
        Vector<Bookmark> temp = new Vector<Bookmark>();
        temp.addAll(this.searchAllBookmarksViewableByAllStudents());
        temp.addAll(this.searchAllBookmarksViewableByCourseStudents());
        temp.addAll(this.searchAllBookmarksViewableByEveryone());
        return temp;
    }

    /**
     * Returns all the bookmark instances that only instructors can view.
     * @return A vector list of all bookmark instance objects
     */
    private Vector<Bookmark> searchAllBookmarksViewableByInstructors() {
        return this.searchByAccessRights(AccessRights.Instructors);
    }

    /**
     * Returns all the bookmark instances that are marked as all students can
     * view.
     * @return A vector list of all bookmark instance objects
     */
    private Vector<Bookmark> searchAllBookmarksViewableByAllStudents() {
        return this.searchByAccessRights(AccessRights.AllStudents);
    }

    /**
     * Returns all the bookmark instances that are marked as course students can
     * view.
     * @return A vector list of all the bookmark instance objects
     */
    private Vector<Bookmark> searchAllBookmarksViewableByCourseStudents() {
        return this.searchByAccessRights(AccessRights.CourseStudents);
    }

    /**
     * Returns all the bookmark instances that are marked for course students to view,
     * but under a given instructor
     * @param instructor the instructor of the course
     * @return a vector list of all the bookmark instance objects with given parameters
     */
    private Vector<Bookmark> searchAllBookmarksViewableByCourseStudentsWithInstructor(User instructor) {
        int createdBy = instructor.getUserNumber();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Bookmark> list = new Vector<Bookmark>();

        try {
            String sql = "SELECT * FROM bookmarks "
                    + "WHERE createdBy = (?) AND accessRights = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, createdBy);
            ps.setString(2, (AccessRights.CourseStudents).toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFromResultSet(rs));

            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }

        return list;
    }

    /**
     * Returns all the bookmark instances that mark as for everyone to view.
     * @return A vector list of all the bookmark instance objects
     */
    private Vector<Bookmark> searchAllBookmarksViewableByEveryone() {
        return this.searchByAccessRights(AccessRights.Everyone);
    }

    /**
     * Returns a vector of all the bookmark instances a student can view from a
     * list of their classes since they may be enrolled into more than one class
     * at a given period of time.
     * @param user the student user
     * @param classes the vector list of courses the student is currently enrolled
     * into
     * @return a vector of all bookmark instance objects fitting the criteria, empty if none
     */
    private Vector<Bookmark> searchAllBookmarksViewableByAStudentInClasses(Vector<User> instructor) {
        Vector<Bookmark> temp = new Vector<Bookmark>();
        temp.addAll(this.searchAllBookmarksViewableByAllStudents());
        temp.addAll(this.searchAllBookmarksViewableByEveryone());
        for(User u: instructor)
            temp.addAll(this.searchAllBookmarksViewableByCourseStudentsWithInstructor(u));
        return temp;
    }
}
