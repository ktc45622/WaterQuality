package weather.common.dbms.mysql;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.dbms.*;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;


/**
 * Represents a DBMS query and the results generated from that query.
 *
 * @author Eric Subach (2010)
 */
public class MySQLQuery {
    // SQL statement.
    String sql;
    // Arguments to the query (for PreparedStatement) and their types.
    Vector<DBMSArg> args;

    MySQLImpl mySQLImpl;

    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    boolean flagSuccess;


    /**
     * Create a DBMSQuery.
     *
     * @param sql The SQL statement.
     * @param args Arguments to the query and their types.
     */
    public MySQLQuery (MySQLImpl mySQLImpl, String sql, Vector<DBMSArg> args) {
        this.mySQLImpl = mySQLImpl;
        this.sql = sql;
        this.args = args;
    }


    /**
     * Execute the query.
     */
    public void execute () throws SQLException {
        //try {
            // Make connection to DBMS.
            conn = mySQLImpl.getLocalConnection ();

            // Prepare the statement.
            ps = conn.prepareStatement(sql);
            prepareStatement ();

            // Execute statement.
            flagSuccess = !ps.execute ();

            // Get the results.
            rs = ps.getResultSet ();
        //}
        /*catch (SQLException e) {
            handleSQLException (e);
            close ();
        }
        finally {

        }*/
    }


    /**
     * Close the currently opened DBMS handles.
     *
     * NOTE: Must be called after execute.
     */
    public void close () {
        MySQLHelper.closeResultSet (rs);
        MySQLHelper.closePreparedStatement (ps);
    }


    /**
     * Fill in the SQL query with the arguments.
     */
    private void prepareStatement () throws SQLException {
        Iterator<DBMSArg> iter;
        // Index for PreparedStatement.
        int index;
        DBMSArg cur;
        Object arg;
        

        if (args == null)
        {
            return;
        }

        iter = args.iterator ();
        index = 1;

        while (iter.hasNext ()) {
            cur = iter.next ();
            arg = cur.getArg ();

            switch (cur.getType ()) {
                case INPUT_STREAM:
                    ps.setBinaryStream (index, (InputStream)(arg));
                    break;

                case INT:
                    ps.setInt (index, (Integer)(arg));
                    break;

                case STRING:
                    ps.setString (index, (String)(arg));
                    break;

                case TIME:
                    ps.setTime (index, (Time)(arg));
                    break;

                default:
                    throw new SQLException ("MySQLQuery: Unknown argument type");
            }

            index++;
        }
    }


    /**
     * Get the ResultSet.
     *
     * @return The ResultSet.
     */
    public ResultSet getResultSet () {
        return (rs);
    }


    /**
     * Handle an SQLException.
     *
     * @param e The SQLException to handle.
     */
    public void handleSQLException (SQLException e) {
        WeatherLogger.log (Level.SEVERE, "SQLException in MySQLEventManager; "
                                       + "check syntax.", e);
        new WeatherException (0012, e, "Cannot complete the requested operation"
                                     + " due to an internal problem.").show ();
    }

    /**
     * Returns if the execution of the SQL query is successful.
     * 
     * @return True if execution was successful, false otherwise.
     */
    public boolean isSuccess () {
        return (flagSuccess);
    }

    
    /**
     * Runs the script specified. 
     * @param script The script.
     * @param dbms The MySQLImpl object.
     */
    public void runScript(String script, MySQLImpl dbms)
    {
        Connection conn = null;
        PreparedStatement ps = null;
        Statement stmt = null;
        ResultSet rs = null;
        int orderRank = 0;
        try {
            conn = dbms.getLocalConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(script);
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
    }
}
