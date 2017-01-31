package weather.clientside.gui.client;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import weather.clientside.gui.component.PopupListener;
import weather.common.data.resource.Resource;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherLogger;

/**
 * This file is a form that show an image.
 *
 * @author Alex Funk
 * @version 2011
 */
public class PreviewImage extends weather.common.gui.component.BUDialog implements Printable {

    private static int imagePad = 100;
    private int width;
    private int height;
    private String previewImageTitle;
    private BufferedImage image;
    private String extension;
    private Resource resource;
    private Date date;
    private JLabel imageLabel;;
    private static final String DATE_FORMAT = "MM-dd-yyyy - hh-mm-ss a";

    /**
     * Creates a preview of the given image. Use showForm() to show.
     *
     * @param image The image.
     * @param previewImageTitle The name of the file for the title bar of a
     * PreviewImage. (can be null)
     * @param extension The file extension denoting the type of image. (includes
     * leading dot)
     * @param resource The resource providing the image. (null if N/A)
     * @param date The time of the image. (null if N/A)
     */
    public PreviewImage(BufferedImage image, String previewImageTitle,
            String extension, Resource resource, Date date) {
        super();
        initComponents();
        ImageIcon imageIcon;
        if (image != null) {
            this.image = image;
            //find new dimension that keeps aspect ratio.
            int scaledHeight = 800 * image.getHeight() / image.getWidth();
            BufferedImage scaledImage = scaleBufferedImage(800, scaledHeight);
            height = scaledImage.getHeight() + imagePad;
            width = scaledImage.getWidth() + imagePad;
            imageIcon = new ImageIcon(scaledImage);
            imageLabel = new JLabel(imageIcon);
        } else {
            imageLabel = new JLabel("Could not load image.");
            width = 400;
            height = 200;
        }
        
        //Copy parameters.
        this.previewImageTitle = previewImageTitle;
        this.extension = extension;
        this.resource = resource;
        this.date = date;

        //Set properties and size of window.
        this.setTitle("Weather Viewer - View " + makeFileName());
       
        //Place image and add right click menu.
        this.setLayout(new BorderLayout());
        this.add(imageLabel, BorderLayout.CENTER);
        
        //Make model.
        this.setModal(true);

        //Size window.
        Dimension imageSize = new Dimension(width, height);
        this.setSize(imageSize);
        this.setMinimumSize(imageSize);
        this.setMinimumSize(imageSize);
        this.setPreferredSize(imageSize);
        pack();
    }
    
    /**
     * Shows the created preview image.
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    public void showFrom(boolean shouldCenter) {
        imageLabel.addMouseListener(new PopupListener(
                new ImageRightClickMenu(image, previewImageTitle, extension,
                resource, date, false, this)));
        super.postInitialize(shouldCenter);
    }
    
     /**
     * This function creates a file name using the DATE_FORMAT presented in this
     * class.
     *
     * @param now The Calendar instance that represents the time right now.
     */
    //NOTE -- USE OUR class CalendarFormatter instead. 
    private static String createFileName(Calendar now) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(now.getTime()).toString();
    }

    /**
     * Method to create file name.
     * @return The default name for this picture when saved. 
     */
    public final String makeFileName() {
        if(previewImageTitle != null){
            return previewImageTitle;
        }
        //Get time of snapshot
        Calendar time = Calendar.getInstance();
        if (date != null) {
            time.setTime(date);
        }
        if (resource == null) {
            return "Image at " + createFileName(time);
        } else {
            return this.resource.getName() + " at " + createFileName(time);
        }
    }
    
    private void printToDebug(){
        Debug.println("width: " + width + " height: " + height + " ratio: " + ((float)width) / height );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        printMenuButton = new javax.swing.JMenuItem();
        closeMenuButton = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        fileMenu.setText("File");

        printMenuButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        printMenuButton.setText("Print");
        printMenuButton.setToolTipText("Print the currently displayed picture");
        printMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printMenuButtonActionPerformed(evt);
            }
        });
        fileMenu.add(printMenuButton);

        closeMenuButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        closeMenuButton.setText("Close");
        closeMenuButton.setToolTipText("Close the view of the current image and returns to the main program");
        closeMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuButtonActionPerformed(evt);
            }
        });
        fileMenu.add(closeMenuButton);

        viewMenuBar.add(fileMenu);

        setJMenuBar(viewMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void printMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printMenuButtonActionPerformed
        this.printSnapshotFile(image.getWidth(), image.getHeight(), makeFileName());
    }//GEN-LAST:event_printMenuButtonActionPerformed

    private void closeMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeMenuButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem closeMenuButton;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem printMenuButton;
    private javax.swing.JMenuBar viewMenuBar;
    // End of variables declaration//GEN-END:variables

    /**
     * This will print the file passed to the PreviewImage and displayed on the
     * screen.
     *
     * @param width The width of the picture to be printed.
     * @param height The height of the picture to be printed.
     * @param filename The filename of the picture to be printed.
     */
    public void printSnapshotFile(int width, int height, String filename) {
        //Find aspect ratio of picture.
        float aspectRatio = ((float)width) / height;
        
        PrinterJob printJob = PrinterJob.getPrinterJob();

        //Set the layout to landscape.
        PageFormat pageFormat = new PageFormat();
        pageFormat.setOrientation(PageFormat.LANDSCAPE);

        //Set name of print job
        if (filename != null) {
            printJob.setJobName("Print " + filename);
        } else {
            printJob.setJobName("Print Image");
        }
        //Set this to printable because it overrides the print function.
        printJob.setPrintable(this, pageFormat);
        this.width = width;
        this.height = height;
        Debug.println("Original:");
        this.printToDebug();

        //Scale until both dimensions fit on page.  use aspect ratio to keep in scale.
        Debug.println("Scaling height:");
        while (this.height > pageFormat.getImageableHeight()) {
            this.height -= 5;
            this.width = Math.round(this.height * aspectRatio);
            this.printToDebug();
        }
        Debug.println("Scaling width:");
        while (this.width > pageFormat.getImageableWidth()) {
            this.width -= 5;
            this.height = Math.round(this.width / aspectRatio);
            this.printToDebug();
        }
        

        //Show the print dialog on the screen.
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (java.awt.print.PrinterException ex) {
                WeatherLogger.log(Level.WARNING, "Unable to print image at this time", ex);
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex == 0) {

            //Scale the imageToPrint down to the width and heigh that was found
            //in the other print function.
            BufferedImage imageToPrint = scaleBufferedImage(this.width, this.height);

            g.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

            g.drawImage(imageToPrint, 0, 0, null);

            return Printable.PAGE_EXISTS;
        } else {
            return Printable.NO_SUCH_PAGE;
        }
    }

    /**
     * This function is used to scale a BufferedImage to user defined
     * dimensions. It is mainly used in the printing application. A user
     * specifies a width and height. The imageToPrint is then resized to fit the
     * specifications. This will allow an imageToPrint to be scaled to fit on
     * page to be printed.
     *
     * @param width The width the imageToPrint should be.
     * @param height The height the imageToPrint should be.
     * @return The scaled BufferedImage.
     */
    public final BufferedImage scaleBufferedImage(int width, int height) {
        BufferedImage source = image;
        BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = destination.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance((double) width / source.getWidth(), (double) height / source.getHeight());
        g.drawRenderedImage(source, at);
        return destination;
    }
}
