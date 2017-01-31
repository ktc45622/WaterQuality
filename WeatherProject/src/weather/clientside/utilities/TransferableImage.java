package weather.clientside.utilities;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * This class is used to create a transferable image to copy to the clipboard
 * for the <code>ImageRightClickMenu</code> class.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Mitchell Gordner (2012)
 * @version Spring 2012
 */
public class TransferableImage implements Transferable
{
    private Image image;
    
    public TransferableImage(Image image) 
    {
        this.image = image;
    }
    
    /**
     * Returns supported flavors.
     * @return The supported data transfer flavors.
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() 
    {
        return new DataFlavor[] { DataFlavor.imageFlavor };
    }

    /**
     * Checks to see if the data flavor is supported.
     * @param flavor The data flavor to check.
     * @return True if the flavor is supported, false otherwise.
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) 
    {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    // Returns image
    /**
     * Gets the transfer data and returns the image.
     * @param flavor The data flavor.
     * @return The image.
     * @throws UnsupportedFlavorException
     * @throws IOException 
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
    {
        try{
        if (!isDataFlavorSupported(flavor)) 
        {
            throw new UnsupportedFlavorException(flavor);
        }}
        catch(UnsupportedFlavorException ex){}
        return image;
    }
}
