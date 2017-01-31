package BBTest;

import java.io.File;
import java.util.ArrayList;
import weather.common.utilities.Debug;
import weather.serverside.FFMPEG.VideoConcatenator;
import weather.serverside.FFMPEG.VideoLowQualityCopier;
import weather.serverside.FFMPEG.VideoTrimmer;

public class MakeSystemDayLongDefaults {

    public static void main(String[] args) {
        Debug.setEnabled(true);
        
        //Setup image instances..
        File hourLongDefault = new File("C:\\Generic Movies\\NoData.mp4");
        if (hourLongDefault.exists()) {
            Debug.println("Hour-long video data exists.");
        } else {
            Debug.println("Hour-long video data does NOT exist.");
            return;
        }
        
        //Provide location and name of result file to be made.
        File targetFile = new File("C:\\Generic Movies\\GenericDayVideo.mp4");
        
        //Make input array.
        ArrayList<File> inputFiles = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            inputFiles.add(hourLongDefault);
        }
        
        boolean result = VideoConcatenator.concatenateVideoFiles(inputFiles, 
                targetFile, 1296, 960); 
        if (result) {
            Debug.println("Concatenation successful.");
        } else {
            Debug.println("Concatenation NOT successful.");
        }
        
        //Make low-quality copy.
        File lqCopy =new File("C:\\Generic Movies\\GenericDayVideo_low.mp4");
        result = VideoLowQualityCopier.makeLowQualityCopy(targetFile, lqCopy);
        if (result) {
            Debug.println("Low-quality copying successful.");
        } else {
            Debug.println("Low-quality copying NOT successful.");
        }
        
        //Trim videos
        result = VideoTrimmer.trimVideo(targetFile, 144) && VideoTrimmer
                .trimVideo(lqCopy, 144);
        if (result) {
            Debug.println("Video trimming successful.");
        } else {
            Debug.println("Video trimming NOT successful.");
        }
        
    }
}
