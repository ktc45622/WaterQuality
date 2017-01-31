package weather.clientside.gui.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import weather.ApplicationControlSystem;
import weather.clientside.manager.MovieController;
import weather.clientside.utilities.BookmarkEventOpener;
import weather.clientside.utilities.ClientSideLocalFileManager;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.resource.ResourceRange;
import weather.common.utilities.*;

/**
 * Allows a bookmark to be opened locally.
 * @author Alex Funk
 * @version 2011
 */
public class FindLocalBookmark {
    private ApplicationControlSystem appControl;
    private MovieController movieController;
    private OpenManageBookmarkDialog parent;
    
    /**
     * Constructs a bookmark instance with a given application control system and 
     * movie controller.
     * @param appControl The application control system.
     * @param mc The movie controller of the main window.
     * @param parent The form that called this. 
     */
    public FindLocalBookmark(ApplicationControlSystem appControl, MovieController mc,
            OpenManageBookmarkDialog parent){
        this.appControl = appControl;
        this.movieController = mc;
        this.parent = parent;
    }
    
    //THESE TWO FUNCTIONS ARE FOR DEBUGING
    private void debugDate(Date date, String name) {
        String dateFormat = "MM/dd/yy hh:mm:ss a z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        if (date == null) {
            Debug.println(name + ": null");
        } else {
            Debug.println(name + ": " + simpleDateFormat.format(date));
        }
    }

    private void debugResourceRange(ResourceRange range) {
        debugDate(range.getStartTime(), "Range Start: ");
        debugDate(range.getStopTime(), "Range End: ");
    }
    
    /**
     * Attempts to openInstance a bookmark locally.
     */
    public void openInstance(){
        try {
            File file = ClientSideLocalFileManager.
                    loadSpecifiedFile(CommonLocalFileManager.
                    getBookmarksDirectory(), PropertyManager.
                    getGUIProperty("Bookmark"), parent);
            if (file == null) {
                return;
            }
            Bookmark bookmark = (Bookmark) new ObjectInputStream
                    (new FileInputStream(file)).readObject();
            
            if(!file.equals(new File(PropertyManager.getBookmarkProperty
                    (""+bookmark.getBookmarkNumber())))){
                if(PropertyManager.getBookmarkProperty(""+
                        bookmark.getBookmarkNumber()).equals("")) {
                            PropertyManager.setBookmarkProperty(""+
                                    bookmark.getBookmarkNumber(), file.getPath());
                        }
                else{
                    PropertyManager.setBookmarkProperty(PropertyManager.
                            getBookmarkProperty("next"), file.getPath());
                    PropertyManager.setBookmarkProperty("next", 
                            PropertyManager.getBookmarkProperty("next"));
                }
            }
            
            parent.dispose();
            new OpenBookmarkInstanceWindow(appControl, bookmark, 
                    movieController);
            
        } catch (WeatherException | ClassNotFoundException | IOException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void openEvent(){
        try{
            File file = ClientSideLocalFileManager.
                    loadSpecifiedFile(CommonLocalFileManager.
                    getBookmarksDirectory(), PropertyManager.
                    getGUIProperty("Event"), parent);
            if (file == null) {
                return;
            }
            Bookmark eventToOpenLocal = (Bookmark) new ObjectInputStream
                    (new FileInputStream(file)).readObject();
            
            if(!file.equals(new File(PropertyManager.getBookmarkProperty
                    ("" + eventToOpenLocal.getBookmarkNumber())))){
                if(PropertyManager.getBookmarkProperty(""+
                        eventToOpenLocal.getBookmarkNumber()).equals("")) {
                            PropertyManager.setBookmarkProperty(""+
                                    eventToOpenLocal.getBookmarkNumber(), file.getPath());
                        }
                else{
                    int next = Integer.parseInt(PropertyManager.
                            getBookmarkProperty("next"));
                    PropertyManager.setBookmarkProperty(""+next, file.getPath());
                    PropertyManager.setBookmarkProperty("next", ""+(next+1));
                }
            }
            
           ResourceRange mainRange = new ResourceRange(eventToOpenLocal
                   .getStartTime(), eventToOpenLocal.getEndTime());
           
           parent.dispose();
           BookmarkEventOpener.openBooekmark(appControl, eventToOpenLocal, 
                   movieController, mainRange);
        } catch (ClassNotFoundException | IOException ex){
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }
}
