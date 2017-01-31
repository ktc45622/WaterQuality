package weather.clientside.utilities;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimeZone;
import java.util.Vector;
import weather.ApplicationControlSystem;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.dbms.DBMSResourceManager;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;

/**
 * Responsible for handling local file cleanup on client machines.
 *
 * @author Justin Gamble
 */
public class LocalFileCleaner {

    /**
     * The publicly viewable cleanup method, all other cleanups should be called
     * from here.
     *
     * @param appControl The program's <code>ApplicationControlSysyem</code>.
     * @param keepToday Whether or not the current day's weather station files 
     * should be kept.
     * @return Whether all the cleanup operations completed successfully.
     */
    public static boolean cleanup(ApplicationControlSystem appControl,
            boolean keepToday) {
        boolean ret1, ret2, ret3;
        ret1 = cleanupAVI();
        ret2 = cleanupMP4();
        ret3 = cleanupWeatherStations(appControl, keepToday);
        return ret1 && ret2 && ret3;
    }

    /**
     * Cleans up Video Files, AVI, keeping the latest <x> modified, where <x> is
     * in GeneralWeather.properties.
     *
     * @return Whether the AVI cleanup operation was completely successful or
     * not.
     */
    private static boolean cleanupAVI() {
        boolean ret = false;
        int numberToSave = Integer.parseInt(PropertyManager.getGeneralProperty("MAX_LOCAL_AVI_TO_KEEP"));
        //root temp_movie dir
        File dir = new File(CommonLocalFileManager.getAVIDir());
        ArrayList<File> files = new ArrayList<>(Arrays.asList(dir.listFiles(new FileFilter() {
            /**
             * Limits the file list to only allResourceFiles that end in ".avi".
             */
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getAbsolutePath().toLowerCase().endsWith(".avi");
            }
        })));

        Collections.sort(files, new Comparator<File>() {
            /**
             * Compares two allResourceFiles by their last modified date.
             */
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
        if (files.size() > numberToSave) {
            for (int i = numberToSave; i < files.size(); i++) {
                //Debug.println("deleting "+allResourceFiles.get(i).getName());
                if (!files.get(i).delete()) {
                    ret = false;
                }
            }
        }
        //camera dir
        dir = new File(CommonLocalFileManager.getAVICameraDir());
        files = new ArrayList<>(Arrays.asList(dir.listFiles(new FileFilter() {
            /**
             * Limits the file list to only allResourceFiles that end in ".avi".
             */
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getAbsolutePath().toLowerCase().endsWith(".avi");
            }
        })));

        Collections.sort(files, new Comparator<File>() {
            /**
             * Compares two allResourceFiles by their last modified date.
             */
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
        if (files.size() > numberToSave) {
            for (int i = numberToSave; i < files.size(); i++) {
                //Debug.println("deleting "+allResourceFiles.get(i).getName());
                if (!files.get(i).delete()) {
                    ret = false;
                }
            }
        }
        //maploop dir
        dir = new File(CommonLocalFileManager.getAVIMaploopDir());
        files = new ArrayList<>(Arrays.asList(dir.listFiles(new FileFilter() {
            /**
             * Limits the file list to only allResourceFiles that end in ".avi".
             */
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getAbsolutePath().toLowerCase().endsWith(".avi");
            }
        })));

        Collections.sort(files, new Comparator<File>() {
            /**
             * Compares two allResourceFiles by their last modified date.
             */
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
        if (files.size() > numberToSave) {
            for (int i = numberToSave; i < files.size(); i++) {
                //Debug.println("deleting "+allResourceFiles.get(i).getName());
                if (!files.get(i).delete()) {
                    ret = false;
                };
            }
        }
        return ret;
    }

    /**
     * Cleans up Video Files, MP4, keeping the latest <x> modified, where <x> is
     * in GeneralWeather.properties.
     *
     * @return Whether the MP4 cleanup operation was completely successful or
     * not.
     */
    private static boolean cleanupMP4() {
        boolean ret = false;
        int numberToSave = Integer.parseInt(PropertyManager.getGeneralProperty("MAX_LOCAL_AVI_TO_KEEP"));
        //root temp_movie dir
        File dir = new File(CommonLocalFileManager.getAVIDir());
        ArrayList<File> files = new ArrayList<>(Arrays.asList(dir.listFiles(new FileFilter() {
            /**
             * Limits the file list to only allResourceFiles that end in ".mp4".
             */
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getAbsolutePath().toLowerCase().endsWith(".mp4");
            }
        })));

        Collections.sort(files, new Comparator<File>() {
            /**
             * Compares two allResourceFiles by their last modified date.
             */
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
        if (files.size() > numberToSave) {
            for (int i = numberToSave; i < files.size(); i++) {
                //Debug.println("deleting "+allResourceFiles.get(i).getName());
                if (!files.get(i).delete()) {
                    ret = false;
                }
            }
        }
        //camera dir
        dir = new File(CommonLocalFileManager.getAVICameraDir());
        files = new ArrayList<>(Arrays.asList(dir.listFiles(new FileFilter() {
            /**
             * Limits the file list to only allResourceFiles that end in ".mp4".
             */
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getAbsolutePath().toLowerCase().endsWith(".mp4");
            }
        })));


        Collections.sort(files, new Comparator<File>() {
            /**
             * Compares two allResourceFiles by their last modified date.
             */
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
        if (files.size() > numberToSave) {
            for (int i = numberToSave; i < files.size(); i++) {
                //Debug.println("deleting "+allResourceFiles.get(i).getName());
                if (!files.get(i).delete()) {
                    ret = false;
                }
            }
        }
        //maploop dir
        dir = new File(CommonLocalFileManager.getAVIMaploopDir());
        files = new ArrayList<>(Arrays.asList(dir.listFiles(new FileFilter() {
            /**
             * Limits the file list to only allResourceFiles that end in ".mp4".
             */
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getAbsolutePath().toLowerCase().endsWith(".mp4");
            }
        })));
        Collections.sort(files, new Comparator<File>() {
            /**
             * Compares two allResourceFiles by their last modified date.
             */
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
        if (files.size() > numberToSave) {
            for (int i = numberToSave; i < files.size(); i++) {
                //Debug.println("deleting "+allResourceFiles.get(i).getName());
                if (!files.get(i).delete()) {
                    ret = false;
                }
            }
        }
        return ret;
    }

    /**
     * Cleans up Weather Station Files, keeping the latest <x> modified, where
     * <x> is in GeneralWeather.properties. These "latest files will not include
     * partial days with the exception of the fact the the function will keep
     * files for the current day if requested to do so.
     * 
     * @param appControl The program's <code>ApplicationControlSysyem</code>.
     * @param keepToday Whether or not the current day should be kept.
     * @return Whether the weather station cleanup operation was completely 
     * successful or not.
     */
    private static boolean cleanupWeatherStations(ApplicationControlSystem appControl,
            boolean keepToday) {
        //Get resource list
        DBMSResourceManager resourceManager = appControl.getDBMSSystem().
                getResourceManager();
        Vector<Resource> resources = resourceManager.getResourceList();

        boolean ret = false;
        int numberToSave = Integer.parseInt(PropertyManager
                .getGeneralProperty("MAX_LOCAL_WEATHER_STATION_DAYS_TO_KEEP"));

        //root weather station dir
        File dir = new File(CommonLocalFileManager.getWeatherStationsDirectory());
        for (final Resource resource : resources) {
            if(!resource.getResourceType().equals(WeatherResourceType
                    .WeatherStation) && !resource.getResourceType()
                    .equals(WeatherResourceType.WeatherStationValues)) {
                //Debug.println("Skipping: " + resource.getResourceName());
                continue;
            }
            
            //Debug.println("Checking: " + resource.getResourceName());
            ArrayList<File> allResourceFiles = new ArrayList<>(Arrays
                    .asList(dir.listFiles(new FileFilter() {
                /**
                 * Limits the file list to only files that are form the current
                 * resource.
                 */
                @Override
                public boolean accept(File pathName) {
                    return pathName.isFile() && getResourceNumber(pathName
                            .getName()) == resource.getResourceNumber();
                }
            })));
            
            //remove parial past days and, if we don't want to keep it, the
            //current day
            ArrayList<File> filesToSort = new ArrayList<>();
           
            //get resource time zone
            TimeZone timeZone = resource.getTimeZone().getTimeZone();
            
            for (int i = 0; i < allResourceFiles.size(); i++) {
                //Debug.println("Checking: " + allResourceFiles.get(i).getName());
                //assume file should be kept
                boolean keepFile = true;
                
                //mark partial days to be deleted
                long fileEndTime = getEndTime(allResourceFiles.get(i).
                        getName());
                if (fileEndTime < ResourceTimeManager.
                        getEndOfDayFromMilliseconds(fileEndTime, timeZone)) {
                    keepFile = false;
                }
                
                //either delete file, do nothing to keep current day if desired,
                //or add it to sorting list
                if(keepFile) {
                    filesToSort.add(allResourceFiles.get(i));
                } else if (keepToday && Calendar.getInstance().getTimeInMillis()
                        <= ResourceTimeManager.getEndOfDayFromMilliseconds(
                        fileEndTime, timeZone)) {
                    //do nothing - file will remain but is not sorted.
                } else {
                    //Debug.println("deleting " + allResourceFiles.get(i)
                    //        .getName() + " based on end time");
                    if (!allResourceFiles.get(i).delete()) {
                        ret = false;
                    }
                    
                    //repeat for "clean" file
                    String originialPath = allResourceFiles.get(i).getAbsolutePath();

                    //get parts of "clean" file path
                    String subDirectory = "CleanCSVFiles";
                    int splitIndex = originialPath.lastIndexOf(File.separator);
                    String pathStart = originialPath.substring(0, splitIndex);
                    String pathEnd = originialPath.substring(splitIndex);
                    String fullPath = pathStart + File.separator + subDirectory
                            + pathEnd;
                    //Debug.println("Clean path: " + fullPath);

                    //delete "clean" file
                    if (!(new File(fullPath).delete())) {
                        ret = false;
                    }
                }
            }
            
            //sort the files that are left
            Collections.sort(filesToSort, new Comparator<File>() {
                /**
                 * Compares two files by their last modified date.
                 */
                @Override
                public int compare(File o1, File o2) {
                    return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
                }
            });
            
            ///remove old files
            if (filesToSort.size() > numberToSave) {
                for (int i = numberToSave; i < filesToSort.size(); i++) {
                    //Debug.println("deleting " + filesToSort.get(i).getName()
                    //        + " based on file age");
                    if (!filesToSort.get(i).delete()) {
                        ret = false;
                    }
                    
                    //repeat for "clean" file
                    String originialPath = filesToSort.get(i).getAbsolutePath();
                    
                    //get parts of "clean" file path
                    String subDirectory = "CleanCSVFiles";
                    int splitIndex = originialPath.lastIndexOf(File.separator);
                    String pathStart = originialPath.substring(0, splitIndex);
                    String pathEnd = originialPath.substring(splitIndex);
                    String fullPath = pathStart + File.separator + subDirectory
                            + pathEnd;
                    //Debug.println("Clean path: " + fullPath);
                    
                    //delete "clean" file
                    if (!(new File(fullPath).delete())) {
                        ret = false;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * This method is used to get the resource number in a file name,
     *
     * @param fileName the filename containing the resource number between its
     * first and second commas.
     * @return the resource number.
     */
    private static int getResourceNumber(String fileName) {
        //get the index of the 1st comma in the given file name
        int indexOfHeadComma = nthIndexOfComma(fileName, ',', 1);
        //get the index of the 2nd comma in the given file name
        int indexOfEndComma = nthIndexOfComma(fileName, ',', 2);

        //use substring to get the string between the 1st comma and 2nd comma
        String stringOfValue = fileName.substring(indexOfHeadComma + 1,
                indexOfEndComma).trim();

        //parse return valse
        return Integer.parseInt(stringOfValue);
    }

    /**
     * This method is used to get the end time in a file name,
     *
     * @param fileName the filename containing the end time between its fourth
     * comma and its file extension.
     * @return the millisecond of the end time.
     */
    private static long getEndTime(String fileName) {
        //get the index of the 4th comma in the given file name
        int indexOfHeadComma = nthIndexOfComma(fileName, ',', 4);
        //get the index of the start of the file extension
        int indexOfEntension = fileName.lastIndexOf('.');

        //use substring to get the string between the 1st comma and 2nd comma
        String stringOfValue = fileName.substring(indexOfHeadComma + 1,
                indexOfEntension).trim();

        //parse return valse
        return Long.parseLong(stringOfValue);
    }

    /**
     * This method is used to get the nth index of the given character in one
     * string.
     *
     * @param text is the string we need to look for.
     * @param needle is the character we need to find.
     * @param n nth given character.
     * @return the index of the nth character we need to find
     */
    private static int nthIndexOfComma(String text, char needle, int n) {
        //read the given string from the head to the end
        for (int i = 0; i < text.length(); i++) {
            //find the index of the nth character
            if (text.charAt(i) == needle) {
                n--;
                if (n == 0) {
                    return i;
                }
            }
        }
        //return -1 if the given character can not be find
        return -1;
    }
}
