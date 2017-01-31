package weather.serverside.old_day_long_maker;

import java.io.File;
import java.sql.Date;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;
import weather.serverside.FFMPEG.LongVideoMaker;
import weather.serverside.FFMPEG.VideoLengthCalculator;
import weather.serverside.FFMPEG.VideoLowQualityCopier;


/**
 * This is a class with a utility method for making and saving a single day-long
 * video for a single day in the past for a given <code>Resource</code>,
 * possibly with a low-quality copy.
 * 
 * @author Brian Bankes
 */
public class OldDayLongVideoMaker {
    
    /**
     * The is a utility method for making and saving a single day-long video for
     * a single day in the past for a given <code>Resource</code>, possibly with
     * a low-quality copy. It may be asked not to override existing videos and
     * can still add a new low-quality copy when not overriding. The method will
     * also delete any day-long videos for the given <code>Resource</code> on
     * the given day which are not the correct length and replace them as asked.
     * It will return an integer indicative of its success.
     *
     * @param scs A <code>StorageControlSystem</code> with access to the file
     * system where all video storage occurs.  As of 5/27/16, this must be an 
     * instance of <code>StorageControlSystemLocalImpl</code>.
     * @param resource The given <code>Resource</code>.
     * @param startOfDay A <code>Date</code> indicating the start of the day in
     * the past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @param overrideExistingVideos True if existing videos should be 
     * overridden; False otherwise.
     * @param makeLowQualityCopy True if a low-quality copy should be made; 
     * False otherwise.
     * @return Zero if a video was made, one if there were not enough hour-long
     * videos to make a video, or two if there was a movie-making failure.
     */
    public static int makeOldVideo(StorageControlSystem scs, 
            Resource resource, Date startOfDay, boolean overrideExistingVideos, 
            boolean makeLowQualityCopy) {
        //Get paths where videos would be if they exist.
        File standardFile = scs.getFileForMP4Movie(resource.getResourceNumber(), 
                startOfDay, true, false);
        File lowQualityFile = scs.getFileForMP4Movie(resource
                .getResourceNumber(), startOfDay, true, true);
        
        //Clear old videos not of the correct length.
        if (standardFile.exists() && !isCorrectLength(standardFile)) {
            standardFile.delete();
            if (lowQualityFile.exists()) {
                lowQualityFile.delete();
            }
        }
        if (lowQualityFile.exists() && !isCorrectLength(lowQualityFile)) {
            lowQualityFile.delete();
        }
        
        //Check if there are enough source videos.
        if (!scs.hasHourLongMP4Videos(resource.getResourceNumber(),
                startOfDay)) {
            //Try to make MP4 hour-long videos from AVI videos.
            if (!scs.canMakeEnoughMP4VideosFromAVIVideos(resource
                    .getResourceNumber(), startOfDay)) {
                /**
                 * If the method is not overriding and a day-long video of the
                 * right length exists, show it is being retained, unless a
                 * low-quality copy if wanted and cannot be made.
                 */
                if (standardFile.exists() && !overrideExistingVideos) {
                    //Check if low-quality copy is wanted and does not exist in the
                    //correct length.
                    if (!lowQualityFile.exists() && makeLowQualityCopy) {
                        //Try to make file.
                        if (VideoLowQualityCopier
                                .makeLowQualityCopy(standardFile, 
                                        lowQualityFile)) {
                            //This is a successful copy of the old video.
                            return 0;
                        } else {
                            //This is an unsuccessful copy of the old video.
                            return 1;
                        }
                    } else {
                        //All that is needed is pressent, so indicate the videos 
                        //are retained.
                        return 0;
                    }
                } else { //This will be an error.
                    return 1;
                }
            }
        }

        //Assume a success; change later if not.
        boolean success = true;

        if (overrideExistingVideos) {
            //By itself, this call will override any existing videos. 
            success = makeAndSaveDayLongVideo(scs, resource, startOfDay,
                    makeLowQualityCopy);

            //Do not keep old low-quality copy if a new one is not wanted.
            if (lowQualityFile.exists() && !makeLowQualityCopy) {
                //Stop if standard video could not be made.
                if (success) {
                    //Try to delete old low-quality file.
                    success = lowQualityFile.delete();
                }
            }
        } else {    //Do NOT override videos.            
            //See if standard quality video has been made.
            if (!standardFile.exists()) {
                //Try to make file.
                success = makeAndSaveDayLongVideo(scs, resource, startOfDay,
                        false);
                //If there is a low-quaity video, it is erroneous, so try to
                //detete it.
                if (lowQualityFile.exists()) {
                    //Stop if standard video could not be made.
                    if (success) {
                        //Try to delete old low-quality file.
                        success = lowQualityFile.delete();
                    }
                }
            }

            //Check if low-quality copy is wanted and does not exist.
            if (!lowQualityFile.exists() && makeLowQualityCopy) {
                //Stop if standard video could not be made of an old video that
                //was incorrectly in the system. 
                if (success) {
                    //Try to make file.
                    success = VideoLowQualityCopier
                            .makeLowQualityCopy(standardFile, lowQualityFile);
                }
            }
        }

        //Return overall success state.
        if (success) {
            return 0;
        } else {
            return 2;
        }
    }
    
    /**
     * The is a helper method for making and saving a single day-long video for
     * a single day in the past for a given <code>Resource</code>, possibly with
     * a low-quality copy.  This method will always make a NEW standard-quality
     * video, so testing to avoid unwanted overriding must be done before this 
     * method is called.  The method will only make a low-quality copy when also
     * making a standard-quality video.  The conditions for overriding that 
     * apply to standard-quality videos also apply to low-quality videos.
     *
     * @param scs A <code>StorageControlSystem</code> with access to the file
     * system where all video storage occurs. As of 5/27/16, this must be an
     * instance of <code>StorageControlSystemLocalImpl</code>.
     * @param resource The given <code>Resource</code>.
     * @param startOfDay A <code>Date</code> indicating the start of the day in
     * the past. It should be midnight in the local time of the given
     * <code>Resource</code>.
     * @param makeLowQualityCopy True if a low-quality copy should be made;
     * False otherwise.
     * @return True if the method was fully successful; False otherwise.
     */
    private static boolean makeAndSaveDayLongVideo(StorageControlSystem scs, 
            Resource resource, Date startOfDay, boolean makeLowQualityCopy) {

        //Find the end of the available video for this day.
        long endMillis = ResourceTimeManager
                .getLastMilliOfExpectedVideo(startOfDay.getTime(),
                resource.getTimeZone().getTimeZone());

        //Return false if ending time is 0.
        if (endMillis == 0) {
            return false;
        }

        //Make ending time into date.
        Date endTime = new Date(endMillis);

        //Create and save day-long videos.
        LongVideoMaker lvm = new LongVideoMaker(scs, resource,
                endTime, makeLowQualityCopy, "the system to create day-long "
                + "videos for dates in the past", "Past-Days-Maker");

        boolean success = lvm.createDayLongVideo();

        //Return success status of long video maker.
        return success;
    }
    
    /**
     * This is a helper function to determine if an existing day-long video is 
     * the correct length.
     * 
     * @param file The <code>File</code> to check.
     * @return True if the given <code>File</code> is a video that is the 
     * correct length for a full day.
     */
    private static boolean isCorrectLength(File file) {
        int correctLength = 24 * Integer.parseInt(PropertyManager
                .getGeneralProperty("MOVIE_LENGTH"));
        int actualLength = VideoLengthCalculator
                .getLengthOfMP4FileInSeconds(file);
        return (correctLength == actualLength);
    }
}
