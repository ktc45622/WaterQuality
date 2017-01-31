package weather.common.utilities;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;

/**
 * This class is a utility designed to find the size of the images produced by a
 * given <code>Resource</code>. It makes use of, and has the ability to, update
 * the database. It looks for dimensions in the database first. If it finds that
 * they have not been set, it tries to get the dimensions of an image from the
 * Internet. If it can successfully update the database, it returns those
 * dimensions. Otherwise, it returns null.
 *
 * @author Brian Bankes
 */

public class ImageDimensionFinder {

    /**
     * This is the public function of a utility designed to find the size of the
     * images produced by a given <code>Resource</code>. It makes use of, and
     * has the ability to, update the database. It looks for dimensions in the
     * database first. If it finds that they have not been set, it tries to get
     * the dimensions of an image from the Internet. If it can successfully
     * update the database, it returns those dimensions. Otherwise, it returns
     * null.
     *
     * @param dbms A <code>DBMSSystemManager</code> with access to the resource
     * data.
     * @param resource The given <code>Resource</code>, which must produce
     * videos. It will be updated if dimensions are found.
     * @return The <code>Dimension</code> of a images being produced by the
     * given <code>Resource</code> or null if that cannot be found.
     */
    public static Dimension 
        getDimensionOfResourceImage(DBMSSystemManager dbms, 
                Resource resource) {
            if (resource.getImageWidth() == 0 
                    || resource.getImageHeight() == 0) {
                Dimension webDim = getCurrentResourceDimension(resource);
                if (webDim.width == 0 || webDim.height == 0) {
                    return null;
                } else {
                    //Update the database.
                    //Update a copy is case of errors.
                    Resource copiedResource = new Resource(resource);
                    copiedResource.setImageWidth(webDim.width);
                    copiedResource.setImageHeight(webDim.height);
                    copiedResource = dbms.getResourceManager()
                            .updateWeatherResource(copiedResource);
                    if (copiedResource.getResourceNumber() != -1) {
                        //No errors - update original resource and return 
                        //webDim.
                        resource.setImageWidth(copiedResource.getImageWidth());
                        resource.setImageHeight(copiedResource
                                .getImageHeight());
                        return webDim;
                    } else {
                        //Database error - return null.
                        return null;
                    }
                }
            } else {
                return new Dimension(resource.getImageWidth(), 
                        resource.getImageHeight());
            }
        }

    
      /**
     * Looks on the Internet to find the dimensions of the images currently 
     * being produced by a <code>Resource</code>.
     * 
     * @param resource The new <code>Resource</code>, which should have yet
     * to be saved.
     * @return The dimensions in a <code>Dimension</code> object, which will
     * have width and height of zero if an error occurs.
     */
    private static Dimension getCurrentResourceDimension(Resource resource) {
        //Set defalt values in case of an error
        int width = 0;
        int height = 0;
        
        ImageInstance instance = new ImageInstance(resource);
        
        try {
            instance.readURL(resource.getURL());

            BufferedImage img = (BufferedImage) instance.getImage();
            width = img.getWidth();
            height = img.getHeight();
        } catch (ConnectException | SocketTimeoutException 
                | WeatherException ex) {
            //No work needed.
            Debug.println(ex.getMessage());
        }
        
        return new Dimension(width, height);
    }
    
    /**
     * For testing.
     * @param args 
     */
    public static void main(String[] args) {
        Debug.setEnabled(true);
        
        //Setup resource number.
        int resourceNumber = 129;
        
        ///Setup local storage
        DBMSSystemManager dbms = null;
        
        Debug.setEnabled(true);
        
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException 
                | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        
        //Do testing.
        Debug.println("Starting Test...");
        Resource resource = dbms.getResourceManager()
                .getWeatherResourceByNumber(resourceNumber);
        Dimension dimension = ImageDimensionFinder.
                getDimensionOfResourceImage(dbms, resource);
        if (dimension != null) {
            Debug.println("Testing result: (" + dimension.width + "x"
                + dimension.height + ").");
        } else {
            Debug.println("Testing result: NULL");
        }
    }
}
