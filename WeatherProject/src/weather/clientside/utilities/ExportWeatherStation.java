package weather.clientside.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.clientside.gui.client.*;
import weather.common.data.resource.Resource;

/**
 * TODO: Remove unused fields
 * This class is used to create and export a weather station data file.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Fen Qin (2009)
 * @author Jason Hunter (2009)
 * @version Spring 2009
 */
public class ExportWeatherStation {
    private File file;
    private PlotData plotData;
    private Resource resource;
    private String name;
    private static final String DATE_FORMAT = "MM-dd-yyyy";
    
    /**
     * Constructor for ExportWeatherStation data.
     * @param file The file user selected from the MainApplication Window.
     * @param plotData The dataPlot from the WeatherStationPanelManager.
     * @param resource The resource from the WeatherStationPanelManager.
     */
    public ExportWeatherStation(File file, PlotData plotData, Resource resource) {
        this.file = file;
        name=file.getName();
        this.plotData = plotData;
        this.resource = resource;
    }
    
    /**
     * Exports the properties file.
     * This method will call the saveFile method.
     * 
     * @param string The file name which the user wants to export.
     */
    public void exportStation(String string){
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(string);
            saveFile(fileWriter);
        } catch (IOException ex) {
            Logger.getLogger(ExportWeatherStation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Pads a string S with a size of N with char C on the right.
     * 
     * @param s The string which user want pad.
     * @param n The size of the string which user want pad.
     * @return The string after pad.
     */
    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
    
    /**
     * Pads a string S with a size of N with char C on the left.
     * 
     * @param s The string which user want pad.
     * @param n The size of the string which user want pad.
     * @return The string after pad.
     */
    private static String padLeft(String s, int n) {
        return String.format("%1$#" + n + "s", s);
    }
    
    /**
     * This method is called by the method exportStation(). It first
     * writes the station data to the file. Next, the default filename is created.
     * Finally, the file is saved.
     * 
     * @param FileWriter The FileWriter object being used to write and save the file.
     */
    private void saveFile(FileWriter fileWriter){
        try {
            fileWriter.write(resource.getResourceName()+"\n");
            fileWriter.write("Start:"+plotData.getStartTime()+" End:"+plotData.getEndTime()+" \n");
            fileWriter.write(padRight(plotData.getValues().get(0).getWvProperties().getDisplayName(plotData.getValues().get(0).getVariableKey())+"",11));
            for(int i=1;i<plotData.getValues().size();i++){
                String s= plotData.getValues().get(i).getWvProperties().getDisplayName(plotData.getValues().get(i).getVariableKey())+"";
                fileWriter.write(padLeft(s,11));
            }
            fileWriter.write("\n");
            fileWriter.write("                     °        mph        mph" +
                        "          %         °F         °F         mb         in" +
                        "         in                 W/sqm         °F         °F" +
                        "         °F         °F         mb         ft         ft" +
                        "         ft         °F         mb         in         in" +
                        "         in      in/hr      miles         °F         °F" +
                        "                    in         °F         °F      miles" +
                        "         °F         °F      miles");
            fileWriter.write("\n");
            for(int i=0;i<plotData.getValues().get(0).size();i++){
                Date d = new Date(plotData.getValues().get(0).get(i).longValue());
                DateFormat df = new SimpleDateFormat("h:mm a");
                fileWriter.write(padRight(df.format(d),11));
                for(int j=1;j<plotData.getValues().size();j++){
                    fileWriter.write(padLeft(plotData.getValues().get(j).get(i)+"",11));
                }
                fileWriter.write("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(ExportWeatherStation.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                fileWriter.close();
            } catch (IOException ex) {

            }
        }
    }

    /**
     * Creates a filename for saving Weather Station files.  It
     * is in the format of "WeatherStationName startDate - endDate.txt".
     * 
     * @param startDate The start date in milliseconds.
     * @param endDate The ending date in milliseconds.
     * @param resourceName The name of the weather station.
     * 
     * @return The file name that was created.
     */
    public static String createFileName(java.sql.Date startDate, java.sql.Date endDate, String resourceName){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(startDate);
        end.setTime(endDate);
        
        String fileName = resourceName+ " ";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        fileName += sdf.format(start.getTime()) + " - ";
        fileName += sdf.format(end.getTime())+ ".txt";
        return fileName;
    }
}
