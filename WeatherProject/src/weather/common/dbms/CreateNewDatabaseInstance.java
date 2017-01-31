package weather.common.dbms;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An interface that creates a database with the given connection information.
 *
 * @author Ioulia Lee (2010)
 * @author Mike Young (2014)
 * @version Spring_2014
 */
public interface CreateNewDatabaseInstance {

    /**
     * Creates a database.  Recommended usage of this method is to collect the
     * database information (URL, administrator username, password), create a 
     * <code>Connection</code> object, attempt to connect to the database, and 
     * if successful, call <code>createDatabase(Connection conn)</code> and 
     * return the result.
     *
     * @return true if a database was created, false otherwise
     * @throws SQLException if an error occurs while accessing the database
     */
    public boolean createDatabase() throws SQLException;

    /**
     * Creates tables for a database with the given connection information.
     * Recommended usage of this method is to sequentially create the tables, 
     * checking that each one has been successfully created.
     *
     * @param conn the <code>Connection</code> to the database
     * @return true if a database was created, false otherwise
     * @throws SQLException if an error occurs while accessing the database
     */
    public boolean createDatabase(Connection conn) throws SQLException;
}
