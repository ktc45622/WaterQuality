package weather.common.data.resource;

import java.io.*;
import java.net.URL;
import weather.common.utilities.WeatherException;

/**
 * This class represents a string that should be saved as is to a file. It is
 * used for both the HTML formatted diary entries, and the CSV formatted entries.
 *
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 *
 * @version Spring 2010
 */
public class HTMLFormattedString extends ResourceInstance {
    private String formattedString;

    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun documents
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but
     * included here as a reminder of its importance.
     */
    private static final long serialVersionUID = 1L;

    public HTMLFormattedString(String formattedString) {
        this.formattedString = formattedString;
    }

    /**
     * Reads an HTML string from a file.
     * @param file The file to read from.
     * @throws WeatherException An error occurred while trying to read the file.
     */
    @Override
    public void readFile(File file) throws WeatherException {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                sb.append(reader.readLine());
            }
            reader.close();
            formattedString = sb.toString();
        }
        catch (IOException ex) {
            throw new WeatherException("Error reading file.");
        }
    }

    @Override
    public void readURL(URL url) throws IOException, WeatherException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Writes the HTML string to a file.
     * @param file The file to write to.
     * @throws WeatherException An error occurred while trying to write to the file.
     */
    @Override
    public void writeFile(File file) throws WeatherException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(formattedString);
            writer.close();
        }
        catch (IOException ex) {
            throw new WeatherException("Error writing file.");
        }
    }
}
