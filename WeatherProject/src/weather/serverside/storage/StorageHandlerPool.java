package weather.serverside.storage;

/**
 *
 * @author Bloomsburg University Software Engineering
 * @version Spring 2008
 */
public class StorageHandlerPool {
    
    private int numberOfHandlers=0;
    private int activeHandlerCount;
    private int loc = 0;
    private StorageRequestHandler handlers[] = null;

    private StorageManagement storageManagement;
    
    
    /** Creates a new instance of StorageHandlerPool
     * @param numberOfHandlers the number of handlers that are used
     * @param storageManagement the storage management to use
     */
    public StorageHandlerPool(int numberOfHandlers,
                                StorageManagement storageManagement) {        
        this.setStorageManagement(storageManagement);
        this.setNumberOfHandlers(numberOfHandlers);   
    }

    public int getNumberOfHandlers() {
        if (getHandlers() == null) return 0;
        return getHandlers().length; // Actual array length
    }

    public void setNumberOfHandlers(int numberOfHandlers) {
        this.numberOfHandlers = numberOfHandlers;
        
         StorageRequestHandler oldHandlers[] = getHandlers();
        setHandlers(new StorageRequestHandler[numberOfHandlers]);
        if (oldHandlers == null)
          for(int i = 0; i< numberOfHandlers; i++)
              getHandlers()[i] = new StorageRequestHandlerImpl(storageManagement);
        else {
          for(int i=0; i<oldHandlers.length; i++){
              getHandlers()[i] = oldHandlers[i];
              oldHandlers[i] = null;
          }
          for(int i = oldHandlers.length; i<getHandlers().length; i++){
              getHandlers()[i] = new StorageRequestHandlerImpl(storageManagement);
          }
        }
    }
    
    public int getAvailableHandlerCount(){
        int count = 0;
        for(int i=0; i < getHandlers().length; i++)
            if(getHandlers()[i].isAvailable())count++;
        return count;
    }
    
    public StorageRequestHandler getHandler(){
        int numOfHandlers = getHandlers().length;      
        
        setLoc((getLoc() + 1) % numOfHandlers);
        //boolean allBusy = false;        
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

    public StorageRequestHandler[] getHandlers() {
        return handlers;
    }

    public void setHandlers(StorageRequestHandler[] handlers) {
        this.handlers = handlers;
    }

    public StorageManagement getStorageManagement() {
        return storageManagement;
    }

    public void setStorageManagement(StorageManagement storageManagement) {
        this.storageManagement = storageManagement;
    }
}
