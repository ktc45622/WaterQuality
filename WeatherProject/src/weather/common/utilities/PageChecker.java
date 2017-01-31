package weather.common.utilities;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A class with a static method to determine if a web page exists
 * 
 * @author Brian Bankes
 */
public class PageChecker {
    
    
    /**
     * Test if a page exists.  (Adopted from java-tips.org)
     * @param pageName The name of the page to be checked.
     * @return True if the page exists, false otherwise.
     */
    public static boolean doesPageExist(String pageName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection) new URL(pageName).openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception ex) {
            Debug.println("Could not text URL.");
            return false;
        }
    }
    
}
