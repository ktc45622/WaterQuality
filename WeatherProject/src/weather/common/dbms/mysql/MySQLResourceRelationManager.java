package weather.common.dbms.mysql;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSResourceRelationManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>MySQLResourceRelationManager</code> class interacts with
 * the Resource_Relation table. This table stores data that links WeatherCameras
 * to WeatherStations. This relation is a many to one relation in that many
 * WeatherCameras can be related to a single WeatherStation, but only one
 * WeatherStation can be related to any individual WeatherCamera. This class
 * is used to maintain information about WeatherStations and WeatherCameras
 * relations in the database.
 *
 * @author jjsharp
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public class MySQLResourceRelationManager implements DBMSResourceRelationManager{

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;

    public MySQLResourceRelationManager(MySQLImpl dbms){
        this.dbms = dbms;
    }

    /**
     * Retrieves the WeatherStation resource that is linked to the given
     * WeatherCamera.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * Catches <code>MalformedURLException</code> if a malformed URL
     * is found.  Log the exception in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param cameraResource The WeatherCamera to return the WeatherStation for.
     * @return A <code>Resource</code> object that represents WeatherStation
     * resource linked to the given WeatherCamera, or null, if no WeatherStation
     * resource is found for the given resource.
     */
    @Override
    public Resource getRelatedStationResource(Resource cameraResource){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Resource resource = null;
        try{
            conn = dbms.getLocalConnection();
            String sql = "SELECT * FROM resources r, resource_relation rr, time_zone_information tz "
                       + "WHERE r.resourceNumber = rr.stationNumber AND "
                       + "r.resourceNumber = tz.resourceNumber AND "
                       + "rr.cameraNumber = "
                       + cameraResource.getResourceNumber();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()){
                resource = MySQLHelper.makeResourceFromResultSet(rs);
            }
        }catch(SQLException e){
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                           "trying to execute an SQL statement. There may be an " +
                           "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        }catch(MalformedURLException e2){
            String str = "\nThe URL for resource with name "+
                          cameraResource.getName()+" is not formed correctly.";
            new WeatherException(3015, e2, str).show();
        }finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closeStatement(stmt);
        }
        return resource;
    }

    /**
     * Adds the relationship into the Resource_Relation table for the given
     * WeatherCamera resource and the WeatherStation resource linking the
     * camera to the station.  If a relation already exists for the given
     * camera, that link is overwritten.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param cameraResource The WeatherCamera to set the relation for.
     * @param weatherStationResource The WeatherStation to set the relation for.
     */
    @Override
    public void setResourceRelation(Resource cameraResource, Resource weatherStationResource){
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = dbms.getLocalConnection();
            String sql = "INSERT INTO resource_relation(cameraNumber,stationNumber)" +
                    " VALUES( "+cameraResource.getResourceNumber()+", "+
                    weatherStationResource.getResourceNumber()+") ON " +
                    "DUPLICATE KEY UPDATE stationNumber = " +
                    weatherStationResource.getResourceNumber();
            stmt = conn.createStatement();
            stmt.execute(sql);
        }catch(SQLException e){
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        }finally {
            MySQLHelper.closeStatement(stmt);
        }
    }

    /**
     * Removes any data in the database that would link the given WeatherCamera
     * resource to a WeatherStation resource.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param cameraResource The WeatherCamera for which to remove all resource
     * relation data.
     */
    @Override
    public void removeResourceRelation(Resource cameraResource){
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = dbms.getLocalConnection();
            String sql = "DELETE FROM WeatherProject.resource_relation " +
                    "WHERE cameraNumber = "+cameraResource.getResourceNumber();
            stmt = conn.createStatement();
            stmt.execute(sql);
        }catch(SQLException e){
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        }finally {
            MySQLHelper.closeStatement(stmt);
        }
    }
}
