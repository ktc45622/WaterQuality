package weather.clientside.utilities;

import java.util.ArrayList;

/**
 * TODO: Remove this class.
 * This is a temporary class that was created for test purposes. A cleaner way
 * to write the code has been found, but this class is still needed until it
 * can be implemented.
 * 
 * @author Joshua Whiteman
 * @param <T>
 */
public class ForecasterNodeObject<T> {
    private ArrayList<String> listOfItemsToDisplay;
    private T objectDisplayed;

    /**
     * 
     * @param listOfItemsToDisplay
     * @param objectDisplayed 
     */
    public ForecasterNodeObject(T objectDisplayed,
            ArrayList<String> listOfItemsToDisplay) {
        this.listOfItemsToDisplay = listOfItemsToDisplay;
        this.objectDisplayed = objectDisplayed;
    }

    /**
     * 
     * @return ArrayList of strings representing the list of forecaster node
     * objects to display.
     */
    public ArrayList<String> getList() {
        return listOfItemsToDisplay;
    }

    /**
     * 
     * @param listOfItemsToDisplay 
     */
    public void setList(ArrayList<String> listOfItemsToDisplay) {
        this.listOfItemsToDisplay = listOfItemsToDisplay;
    }

    /**
     * 
     * @return The forecaster node object to be displayed
     */
    public T getObject() {
        return objectDisplayed;
    }

    /**
     * 
     * @param objectDisplayed 
     */
    public void setObject(T objectDisplayed) {
        this.objectDisplayed = objectDisplayed;
    }
    
    
}
