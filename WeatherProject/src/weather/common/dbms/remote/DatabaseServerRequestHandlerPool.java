
package weather.common.dbms.remote;

import weather.StorageControlSystem;
import weather.common.dbms.DBMSSystemManager;

/**
 *
 * @author Curt Jones
 */
public final class DatabaseServerRequestHandlerPool {
    private int numberOfHandlers=0;
    private int activeHandlerCount=0;
    private int loc = 0;
    private DatabaseServerRequestHandler handlers[] = null;
    private StorageControlSystem storageControlSystem;

    private DBMSSystemManager dbms = null;// Local database management system that will execute our commands.

    /** Creates a new instance of StorageHandlerPool
     * @param numberOfHandlers the number of handlers that are used
     * @param dbms the storage management to use
     */
    public DatabaseServerRequestHandlerPool(int numberOfHandlers,
                                           DBMSSystemManager dbms,
                                           StorageControlSystem storageControlSystem) {        
        this.setDatabaseSystem(dbms);
        this.setNumberOfHandlers(numberOfHandlers); 
        this.storageControlSystem = storageControlSystem;
    }

    public final int getNumberOfHandlers() {
        if (getHandlers() == null) return 0;
        return getHandlers().length; // Actual array length
    }

    public final void setNumberOfHandlers(int numberOfHandlers) {
        this.numberOfHandlers = numberOfHandlers;
        
        DatabaseServerRequestHandler oldHandlers[] = getHandlers();
        setHandlers(new DatabaseServerRequestHandler[numberOfHandlers]);
        DatabaseServerRequestHandler newHandlers[] = getHandlers();
        if (oldHandlers == null)
          for(int i = 0; i< numberOfHandlers; i++) {
              newHandlers[i] = new DatabaseServerRequestHandler(dbms,storageControlSystem);
              newHandlers[i].available = true;
          }
        else {
          for(int i=0; i<oldHandlers.length; i++){
              newHandlers[i] = oldHandlers[i];
              oldHandlers[i] = null;
           }
          for(int i = oldHandlers.length; i<getHandlers().length; i++){
              newHandlers[i] = new DatabaseServerRequestHandler(dbms,storageControlSystem);
              newHandlers[i].available = true;
          }
        }
    }
    
    public int getAvailableHandlerCount(){
        int count = 0;
        for(int i=0; i < getHandlers().length; i++)
            if(getHandlers()[i].isAvailable())count++;
        return count;
    }
    
    public DatabaseServerRequestHandler getHandler(){
        int numOfHandlers = getHandlers().length;      
        
        setLoc((getLoc() + 1) % numOfHandlers);
              
        int countBusy=0;       
        while (countBusy < numOfHandlers && !getHandlers()[getLoc()].isAvailable() ){
            setLoc((getLoc() + 1) % numOfHandlers);
        }
        
        if(getHandlers()[getLoc()].isAvailable())
           return getHandlers()[getLoc()];
        else return null;
    }

    public int getActiveHandlerCount() {
        return activeHandlerCount;
    }

    public void setActiveHandlerCount(int activeHandlerCount) {
        this.activeHandlerCount = activeHandlerCount;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public DatabaseServerRequestHandler[] getHandlers() {
        return handlers;
    }

    public void setHandlers(DatabaseServerRequestHandler[] handlers) {
        this.handlers = handlers;
    }

    public DBMSSystemManager getDatabaseSystem() {
        return dbms;
    }

    public void setDatabaseSystem(DBMSSystemManager dbms) {
        this.dbms = dbms;
    }
    
    
    public StorageControlSystem getStorageControlSystem() {
        return storageControlSystem;
    }

    public void setStorageControlSystem(StorageControlSystem storageControlSystem) {
        this.storageControlSystem = storageControlSystem;
    }
    
}
