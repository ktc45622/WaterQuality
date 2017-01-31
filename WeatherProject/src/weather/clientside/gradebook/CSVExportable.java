package weather.clientside.gradebook;

/**
 * Interface for generating a .CSV file containing the  class' data. CSV format
 * organizes data by placing each record on its own row (separated by newlines)
 * and each value of each record separated by a comma.
 * 
 * @author Nikita Maizet
 */
public interface CSVExportable {
    
    /**
     * Using data from supplied object creates a .CSV file and writes data
     * to file in CSV format.
     * 
     * @param s String containing data from the object to be used when creating
     * the .CSV file.
     */
    public void exportToCSV(String s);
}
