package weather.common.data.diary;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * TODO: DOCUMENTATION
 */
public class DailyDiaryWebLinks implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private String URL;
    private int linkNumber;
    /**
     * Constructor for creating DailyDiaryWebLinks.
     * @param name The name of the link.
     * @param URL The URL of the link.
     */
    public DailyDiaryWebLinks(int linkNumber,String name,String URL)
    {
        this.name=name;
        this.URL=URL;
        this.linkNumber=linkNumber;
    }
    /**
     * Method to set the name attribute.
     * @param name The name of the link.
     */
    public void setName(String name)
    {
        this.name=name;
    }
    /**
     * Method to set the URL of the link.
     * @param URL String to be assigned to URL attribute.
     */
    public void setURL(String URL)
    {
        this.URL=URL;
    }
    /**
     * Method to set primary key of the link.
     * @param number The number assigned to the link.
     */
    public void setLinkNumber(int number)
    {
        this.linkNumber=number;
    }
    /**
     * Method to get the name of the link.
     * @return The name of the link.
     */
    public String getName()
    {
        return name;
    }
    /**
     * Method to get the primary key of the link.
     * @return The primary key of that link.
     */
    public int getLinkNumber()
    {
        return linkNumber;
    }
    /**
     * Method to get the name attribute.
     * @return The URL of that link as a string.
     */
    public String getURLString()
    {
        return URL;
    }
    /**
     * Method to get the URL attribute.
     * @return The URL of the link.
     */
    public URL getURL()
    {
         URL url = null;
        try {
            url = new URL(this.URL);
        } catch (MalformedURLException e) {
            WeatherLogger.log(Level.SEVERE, "MalformedURLException is thrown "
                    + "while trying to retrieve URL for this WebLink.", e);
            String str = "\nThe URL for the web link with name "
                    + this.getName() + " is not formed correctly.";
            new WeatherException(3015, e, str).show();
        }
        return url;
    }
    
}
