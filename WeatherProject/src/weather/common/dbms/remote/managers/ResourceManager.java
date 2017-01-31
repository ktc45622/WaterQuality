
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSResourceManager;
import weather.common.dbms.ResourceChangeListener;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of the ResourceManager methods.
 * @author Brian Zaiser
 */
public class ResourceManager implements DBMSResourceManager {

    /**
     * Retrieves and returns a list of resources from the database.
     * @return A Vector of Resources from the database.
     */
    @Override
    public Vector<Resource> getResourceList() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_GetResourceList;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Resource>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement 
  
    }

    /**
     * Function to update of insert a <code>Resource</code> in the database.
     *
     * If the record received has a resource number below 1 then, it is new and
     * needs to be added to the database. If it has a resource number above 0
     * then this resource is already in the database and needs to be updated.
     * This operation returns the weather resource object as it would be
     * obtained from the database. This operation modifies the database and then
     * obtains this record from the database and returns it. The same object
     * passed to the operation is modified. This operation catches an
     * <code>SQLException</code> if there is an error in the SQL statement, logs
     * <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code>, displays it to a user, and returns an
     * empty <code>Resource</code>.
     *
     * @param resource The resource to be updated or inserted.
     * @return Either the <code>Resource</code> in its new state after a
     * successful operation or a empty <code>Resource</code> if the operation
     * fails.
     */
    @Override
    public Resource updateWeatherResource(Resource resource) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_UpdateWeatherResource;
       arguments = new ArrayList<Object>();
       arguments.add(resource);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return (Resource) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves and returns a WeatherResource by number, if it exists; null otherwise.
     * @param resourceNumber
     * @return The Resource requested by number.
     */
    @Override
    public Resource getWeatherResourceByNumber(int resourceNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_GetWeatherResourceByNumber;
       arguments = new ArrayList<Object>();
       arguments.add(resourceNumber);
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

    @Override
    public boolean removeResource(Resource resource) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_RemoveResourceByResource;
       arguments = new ArrayList<Object>();
       arguments.add(resource);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement 
    }

    @Override
    public boolean removeResource(int resourceNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_RemoveResourceByNumber;
       arguments = new ArrayList<Object>();
       arguments.add(resourceNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement 
    }

    @Override
    public ImageInstance getDefaultNighttimePicture(int resourceNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_GetDefaultNighttimePicture;
       arguments = new ArrayList<Object>();
       arguments.add(resourceNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (ImageInstance) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    @Override
    public ImageInstance getDefaultDaytimePicture(int resourceNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_GetDefaultDaytimePicture;
       arguments = new ArrayList<Object>();
       arguments.add(resourceNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (ImageInstance) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    @Override
    public boolean setDefaultNighttimePicture(int resourceNumber, ImageInstance imageInstance) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_SetDefaultNighttimePicture;
       arguments = new ArrayList<Object>();
       arguments.add(resourceNumber);
       arguments.add(imageInstance);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return (Boolean) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    @Override
    public boolean setDefaultDaytimePicture(int resourceNumber, ImageInstance imageInstance) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_SetDefaultDaytimePicture;
       arguments = new ArrayList<Object>();
       arguments.add(resourceNumber);
       arguments.add(imageInstance);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return (Boolean) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    @Override
    public ImageInstance getDefaultGenericNoDataImage() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_GetDefaultGenericNoDataImage;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (ImageInstance) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    @Override
    public boolean setDefaultGenericNoDataImage(ImageInstance image) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_SetDefaultGenericNoDataImage;
       arguments = new ArrayList<Object>();
       arguments.add(image);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return (Boolean) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    @Override
    public void addResourceChangeListener(ResourceChangeListener resourceChangeListener) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_AddResourceChangeListener;
       arguments = new ArrayList<Object>();
       arguments.add(resourceChangeListener);
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

    @Override
    public void removeResourceChangeListener(ResourceChangeListener resourceChangeListener) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Resource_RemoveResourceChangeListener;
       arguments = new ArrayList<Object>();
       arguments.add(resourceChangeListener);
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
