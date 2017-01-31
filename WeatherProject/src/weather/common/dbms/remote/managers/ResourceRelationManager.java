
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSResourceRelationManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSResourceRelationManager.
 * @author Brian Zaiser
 */
public class ResourceRelationManager implements DBMSResourceRelationManager{

    /**
     * Retrieves the record for the specified camera resource.
     * @param cameraResource The specific camera resource.
     * @return A Resource object with all fields filled.
     */
    @Override
    public Resource getRelatedStationResource(Resource cameraResource) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.ResourceRelation_GetRelatedStationResource;
       arguments = new ArrayList();
       arguments.add(cameraResource);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (Resource) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Updates the record for the specified camera resource for the 
     * specified weather station.
     * @param cameraResource The specific camera resource.
     * @param weatherStationResource The specific weather station.
     */
    @Override
    public void setResourceRelation(Resource cameraResource, Resource weatherStationResource) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.ResourceRelation_SetResourcerelation;
       arguments = new ArrayList();
       arguments.add(cameraResource);
       arguments.add(weatherStationResource);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return ; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return ;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return ;
        } //end of if statement
    }

    /**
     * Deletes the record for the specified camera resource.
     * @param cameraResource The specific camera resource.
     */
    @Override
    public void removeResourceRelation(Resource cameraResource) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.ResourceRelation_RemoveResourceRelation;
       arguments = new ArrayList();
       arguments.add(cameraResource);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return ; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return ;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return ;
        } //end of if statement
    }
    
}
