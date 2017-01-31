package weather.common.gui.component;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import weather.common.utilities.Debug;

/**
 * This class is designed to eliminate hard-coding of icons to be used through-out
 * the BU Weather Project program and if an Icon gets changed, only one file needs
 * to be changed rather than many files.
 * 
 * @author Alex Funk
 * @author Mitch Gordner(2012)
 * @version 2012
 */
public class IconProperties {
    private static final String iconDir = "Icons";
    
    private static String getPathToImageFile(String filename) {
        File imageFile = new File(iconDir + File.separator + filename);
        return imageFile.getAbsolutePath();
    }


    private static ImageIcon getImageIcon(String filename) {
        try{
            return new ImageIcon(iconDir + File.separator + filename);
        }
        catch (Exception ex){
            Debug.println("Cannot find icon image");
            return null;
        }
    }

    private static Image getImage(String filename) {
        ImageIcon icon = getImageIcon(filename);

        if (icon != null)
            return icon.getImage();
        else
            return null;
    }

    /**
     * Uses a String filename path to get the title bar icon image.
     * @return An Image of the BU icon to be used for the title bar.
     */
    public static Image getTitleBarIconImage(){
        return getImage("BU.png");
    }

    /**
     * Uses a String filename path to get Husky mascot image icon.
     * @return An ImageIcon of the BU Husky.
     */
    public static ImageIcon getHuskyIconImage(){
        return getImageIcon("HuskyIcon.png");
    }

    /**
     * Uses a String filename path to get Husky mascot image icon.
     * @return An ImageIcon of the BU Husky.
     */
    public static ImageIcon getHuskySmallIconImage(){
        return getImageIcon("HuskyIconSmall.png");
    }

    /**
     * Uses a String filename path of the splash screen to get a splash screen
     * image icon.
     * @return The splash screen image icon.
     */
    public static ImageIcon getSplashScreenIconImage(){
        return getImageIcon("splash.png");
    }

    /**
     * Gets the full path to the time zone map image.
     * @return The full path to the time zone map image.
     */
    public static String getTimeZoneMapImagePath(){
        return getPathToImageFile("TimeZoneMap.png");
    }

    /**
     * Uses a String filename path of the attachment icon.
     * @return The attachment icon as an icon image.
     */
    public static ImageIcon getAttachmentIconImage(){
        return getImageIcon("Attachment.png");
    }

    /**
     * Uses a String filename path to get Movie image icon.
     * @return An ImageIcon of the Movie icon.
     */
    public static ImageIcon getMovieIconImage(){
        return getImageIcon("Movie.png");
    }

    /**
     * Uses a String filename path to get  image icon.
     * @return An ImageIcon of the image.
     */
    public static ImageIcon getPictureIconImage(){
        return getImageIcon("Picture.png");
    }

    /**
     * Uses a String filename path to get a text document image icon.
     * @return An ImageIcon of the image.
     */
    public static ImageIcon getTextDocumentIconImage(){
        return getImageIcon("TextDocument.png");
    }
    
    /**
     * Uses a String filename path to get the External Window image icon.
     * @return An ImageIcon of a box with an arrow pointing to the upper right.
     */
    public static ImageIcon getExternalWindowIconImage(){
        return getImageIcon("ExternalWindow.png");
    }
    
    /**
     * Uses a String filename path to get the Globe image icon.
     * @return An ImageIcon of a globe.
     */
    public static ImageIcon getGlobeIconImage(){
        return getImageIcon("Globe.png");
    }
    
    /**
     * Uses a String filename path to get the Add Bookmark image icon.
     * @return An ImageIcon of a bookmarker.
     */
    public static ImageIcon getAddBookmarkIconImage(){
        return getImageIcon("AddBookmark.png");
    }
    
    /**
     * Uses a String filename path to get Open Bookmark image icon.
     * @return An ImageIcon of a opened book with a bookmarker.
     */
    public static ImageIcon getOpenBookmarkIconImage(){
        return getImageIcon("OpenBookmark.png");
    }

    /**
     * Uses a String filename path of the notes icon.
     * @return An ImageIcon of a piece of paper.
     */
    public static ImageIcon getNotesIconImage(){
        return getImageIcon("Notes.png");
    }
    
    /**
     * Uses a String filename path to get new note image icon.
     * @return An ImageIcon of a piece of paper with a plus symbol.
     */
    public static ImageIcon getNewNoteIconImage() {
        return getImageIcon("notebook--plus.png");
    }
    
    /**
     * Uses a String filename path to get the edit note image icon.
     * @return An ImageIcon of a piece of paper with a pencil.
     */
    public static ImageIcon getEditNoteIconImage() {
        return getImageIcon("notebook--pencil.png");
    }

    /**
     * Uses a String filename path of the small husky mascot icon.
     * @return A smaller version of the husky icon image.
     */
    public static ImageIcon getSmallHuskyIconImage(){
        return getImageIcon("SmallHuskyIcon.png");
    }

    /**
     * Uses a String filename path of the camera icon.
     * @return The camera icon as an image icon.
     */
    public static ImageIcon getCameraIconImage(){
        return getImageIcon("SnapshotPreview.png");
    }
    
    /**
     * Uses a String filename path to get the open image icon.
     * @return An ImageIcon of an open folder.
     */
    public static ImageIcon getOpenDataIconImage() {
        return getImageIcon("OpenData.png");
    }

    /**
     * Uses a String filename path of the play movie icon.
     * @return An image icon of the play movie picture.
     */
    public static ImageIcon getPlayMovieIconImage(){
        return getImageIcon("Play.png");
    }

    /**
     * Uses a String filename path to get the frame forward image icon.
     * @return An ImageIcon of a frame forward icon.
     */    
    public static ImageIcon getFrameForwardIconImage(){
        return getImageIcon("FrameForward.png");
    }
    
    /**
     * Uses a String filename path to get the frame backward image icon.
     * @return An ImageIcon of a frame backward icon.
     */
    public static ImageIcon getFrameBackwardIconImage(){
        return getImageIcon("FrameBackward.png");
    }

    /**
     * Uses a String filename path of the record movie icon.
     * @return An image icon of the record movie picture.
     */
    public static ImageIcon getRecordMovieIconImage(){
        return getImageIcon("Record.png");
    }

    /**
     * Give the image needed to get the image icon contains
     * the specified image.
     * @param img The image needed.
     * @return The image icon contains the image.
     */
    public static ImageIcon getSnapShotImage(Image img){
            ImageIcon icon=new ImageIcon();
            icon.setImage(img);
            return icon;
    }

    /**
     * Uses a String filename path of the stop movie icon.
     * @return An image icon of the stop movie picture.
     */
    public static ImageIcon getTick() {
        return getImageIcon("Tick.png");
    }

    /**
     * Uses a String filename path to get the Cross image icon.
     * @return An ImageIcon of a X(Cross/Close) icon.
     */    
    public static ImageIcon getCross() {
        return getImageIcon("Cross.png");
    }
    
    /**
     * Uses a String filename path to get the pause image icon.
     * @return An ImageIcon of a pause icon.
     */
    public static ImageIcon getStopMovieIconImage(){
        return getImageIcon("Pause.png");
    }
    
    /**
     * Uses a String filename path to get the resource visible image icon.
     * @return An ImageIcon of a green circle.
     */
    public static ImageIcon getResourceVisibleIconImage() {
        return getImageIcon("BulletGreen.png");
    }
    
    /**
     * Uses a String filename path to get the resource invisible image icon.
     * @return An ImageIcon of a green circle.
     */
    public static ImageIcon getResourceInvisibleIconImage() {
        return getImageIcon("BulletYellow.png");
    }
    
    /**
     * Uses a String filename path to get resource inactive image icon.
     * @return An ImageIcon of a red circle.
     */
    public static ImageIcon getResourceInactiveIconImage() {
        return getImageIcon("BulletRed.png");
    }
    
    /**
     * Uses a String filename path to get None image icon.
     * @return An ImageIcon of a blank cell.
     */
    public static ImageIcon getResourceSpacerIconImage() {
        return getImageIcon("CellSpacer.png");
    }
    
    /**
     * Uses a String filename path to get the save image icon.
     * @return An ImageIcon of a floppy disc.
     */
    public static ImageIcon getSaveIconImage() {
        return getImageIcon("Save.png");
    }
    
    /**
     * Uses a String filename path to get the snapshot preview image icon.
     * @return An ImageIcon of a camera.
     */
    public static ImageIcon getSnapshotPreviewIconImage() {
        return getImageIcon("SnapshotPreview.png");
    }
    
    /**
     * Uses a String filename path to get the printer image icon.
     * @return An ImageIcon of a printer.
     */
    public static ImageIcon getSnapshotPrintIconImage() {
        return getImageIcon("SnapshotPrint.png");
    }
    
    /**
     * Get icon from WeatherProject\srC\weather\clientsidE\guI\client
     * @return An ImageIcon of a camera.
     */
    public static ImageIcon getClientCameraPhotoIcon() {
        return(getSnapshotPreviewIconImage());
    }

    /**
     * 
     * @return An ImageIcon of a camera.
     */
    public static ImageIcon getClientCameraNormalIcon() {
        return(getSnapshotPreviewIconImage());
    }

    /**
     * Uses a String filename path to get the play image icon.
     * @return An ImageIcon of a play icon.
     */    
    public static ImageIcon getClientPlayNormalIcon() {
        return getImageIcon("Play.png");
    }
    
    /**
     * Uses a String filename path to get the record image icon.
     * @return An ImageIcon of a red record icon.
     */
    public static ImageIcon getClientRecordNormalIcon() {
        return getImageIcon("Record.png");
    }
    
    /**
     * Uses a String filename path to get the stop image icon.
     * @return An ImageIcon of a stop icon.
     */
    public static ImageIcon getClientStopNormalRedIcon() {
        return getImageIcon("Stop.png");
    }
    
    /**
     * Uses a String filename path to get the copy image icon.
     * @return An ImageIcon of a copy icon.
     */
    public static ImageIcon getCopyIcon() {
        return getImageIcon("Copy.png");
    }
    
    /**
     * Uses a String filename path to get the export image icon.
     * @return An ImageIcon of a export icon.
     */
    public static ImageIcon getExportIcon() {
        return getImageIcon("Export.png");
    }
    /**
     * Uses a String filename path to get the arrow down image icon.
     * @return An ImageIcon of a smaller arrow pointing down.
     */
    public static ImageIcon getArrowDownSmallIcon(){
        return getImageIcon("ArrowDownSmall.png");
    }
    
    /**
     * Uses a String filename path to get the arrow up image icon.
     * @return An ImageIcon of a smaller arrow pointing up.
     */
    public static ImageIcon getArrowUpSmallIcon(){
        return getImageIcon("ArrowUpSmall.png");
    }
    /**
     * Uses a String filename path to get the arrow left image icon.
     * @return An ImageIcon of a smaller arrow pointing left.
     */
    public static ImageIcon getArrowLeftSmallIcon(){
        return getImageIcon("ArrowLeft.png");
    }
    /**
     * Uses a String filename path to get the arrow right image icon.
     * @return An ImageIcon of a smaller arrow pointing right.
     */
    public static ImageIcon getArrowRightSmallIcon(){
        return getImageIcon("ArrowRight.png");
    }
    /**
     * Uses a String filename path to get the calendar image icon.
     * @return An ImageIcon of a calendar icon.
     */
    public static ImageIcon getCalendarLargeIcon(){
        return getImageIcon("CalendarLarge.png");
    }
    
    /**
     * Uses a String filename path to get the calendar selected image icon.
     * @return An ImageIcon of a calendar with selected days icon.
     */
    public static ImageIcon getCalendarIcon(){
        return getImageIcon("calendar-select-days-span.png");
    }
    
    /**
     * Uses a String filename path to get the information image icon.
     * @return An ImageIcon of a i in a blue circle icon.
     */
    public static ImageIcon getInformationIcon(){
        return getImageIcon("information.png");
    }
    
    public static ImageIcon getDefaultSettingIcon()
    {
        return getImageIcon("Default_Setting.png");
    }
    
    public static ImageIcon getDefaultCameraIcon()
    {
        return getImageIcon("Default_Camera.png");
    }
    
    public static ImageIcon getDefaultMapIcon()
    {
        return getImageIcon("Default_Map.png");
    }
    
    public static ImageIcon getDefaultWeatherStationIcon()
    {
        return getImageIcon("Default_Weather_Station.png");
    }
    
    public static ImageIcon getDefaultHoursToLoadIcon()
    {
        return getImageIcon("Default_Hours.png");
    }
    
    public static ImageIcon getMinusIcon(){
        return getImageIcon("minus.png");
    }
    
    public static ImageIcon getPlusIcon(){
        return getImageIcon("plus.png");
    }
}
