package weather.common.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import weather.common.utilities.WeatherException;

/**
 * This class stores the information about a file that an instructor might want
 * to keep with one of the instructor data types: Notes, Lessons, Bookmarks,
 * Events, and Private. The information about the file is kept in the database.
 *
 * @author cjones
 * @version Spring 2010
 */
public class InstructorFileInstance implements Serializable {
    
   private static final long serialVersionUID = 1;
   
   // The number of the entry for this file in the database
   private int fileNumber;
   
   // What data type does this instructor file belong to
   private InstructorDataType instructorDataType;

   // The number for the data type entry this file is associated with
   // Example: if stored with a Lesson, this means lessonNumber
   private int dataTypeNumber;
   
   // The instructor number (current the same as user number)
   private int instructorNumber;

   //The name of this instructor file -- for example Notes1.doc
   private String fileName;

   // A byte array representing the contents of this instructor file
   private byte[] fileData;

   /**
    * Creates an instance of the InstructorFileInstance class with the given instructor
    * number, instructor data type, file name, and the contents of a file.
    *
    * @param instructorNumber The number of the instructor using this file.
    * @param instructorDataType The data type this file belong to.
    * @param fileName The name of this file.
    * @param file The contents of this file.
    */
   public InstructorFileInstance(int instructorNumber,InstructorDataType instructorDataType,
        String fileName,byte[] file  ){
        setFileData(file);
        setFileName(fileName);
        setInstructorDataType(instructorDataType);
        setInstructorNumber(instructorNumber);
   }
   
   /**
    * Creates an instance of InstructorFileInstance class with the given instructor number,
    * data type, associated data type number, file name, and contents.
    * 
    * @param instructorNumber The number of the instructor using this file.
    * @param type The data type this file belongs to.
    * @param dataTypeNumber The number of the data object this file is associated with.
    * @param fileName The name of this file.
    * @param fileContents The contents of this file.
    */
   public InstructorFileInstance(int instructorNumber, InstructorDataType type, 
           int dataTypeNumber, String fileName, byte[] fileContents) {
       fileNumber = -1;
       setInstructorNumber(instructorNumber);
       setInstructorDataType(type);
       setDataNumber(dataTypeNumber);
       setFileName(fileName);
       setFileData(fileContents);
   }
   
   /**
    * Creates a private file for the instructor given by the instructorNumber.
    * @param instructorNumber The userNumber of the instructor who owns this file.
    * @param fileName The name of this file.
    * @param fileContents The contents of this file.
    */
   public InstructorFileInstance(int instructorNumber, String fileName, byte[] fileContents) {
       this.fileNumber = -1;
       setInstructorDataType(InstructorDataType.Private);
       setFileName(fileName);
       setFileData(fileContents);
   }
   
   /**
    * Creates an instance of InstructorFileInstance class with the given instructor number,
    * data type, associated data type number, file name, and contents.
    * 
    * @param fileNumber The number for this file entry in the database.
    * @param instructorNumber The number of the instructor using this file.
    * @param type The data type this file belongs to.
    * @param dataTypeNumber The number of the data type this file is associated with.
    * @param fileName The name of this file.
    * @param fileContents The contents of this file.
    */
   public InstructorFileInstance(int fileNumber, int instructorNumber, 
           InstructorDataType type, int dataTypeNumber, String fileName, 
           byte[] fileContents) {
       this.fileNumber = fileNumber;
       setInstructorNumber(instructorNumber);
       setInstructorDataType(type);
       setDataNumber(dataTypeNumber);
       setFileName(fileName);
       setFileData(fileContents);
   }
   
   /**
    * Returns the number of this file entry in the database.
    * @return The number of this file entry in the database.
    */
   public int getFileNumber() {
       return fileNumber;
   }

   /**
    * Retrieves this file contents.
    *
    * @return A byte array representing this file contents.
    */
    public byte[] getFileData() {
        return fileData;
    }

    /**
     * Returns the length of the fileData attribute.
     *
     * @return The length of the byte array that represents the file data.
     */
    public int length() {
        return fileData.length;
    }

    /**
     * Sets the contents of this file.
     *
     * @param fileData The data to be set.
     */
    public final void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * Retrieves the name of this file.
     *
     * @return A String representation of the name of this file.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of this file.
     *
     * @param fileName The name of this file to be set.
     */
    public final void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Retrieves the data type this file belong to.
     *
     * @return An <code>InstructorDataType</code> object representing the data
     * type of this file.
     */
    public InstructorDataType getInstructorDataType() {
        return instructorDataType;
    }

    /**
     * Sets the data type for this file.
     *
     * @param instructorDataType The instructor data type to be set for this
     * file.
     */
    public final void setInstructorDataType(InstructorDataType instructorDataType) {
        this.instructorDataType = instructorDataType;
    }

    /**
     * Retrieves the number of the instructor using this file.
     *
     * @return The instructor number.
     */
    public int getInstructorNumber() {
        return instructorNumber;
    }

    /**
     * Sets the instructor number to the given number.
     *
     * @param instructorNumber The instructor number to be set.
     */
    public final void setInstructorNumber(int instructorNumber) {
        this.instructorNumber = instructorNumber;
    }

    /**
     * Reads the given file.
     *
     * @param file The file to read.
     * @throws WeatherException If the file does not exist, is a directory, or
     * for some other reason cannot be opened for reading.
     */
    public void readFile(File file) throws WeatherException {
        try {
            FileInputStream in = new FileInputStream(file);
            FileChannel fc = in.getChannel();

            fileData = new byte[(int) fc.size()];

            ByteBuffer bb = ByteBuffer.wrap(fileData);
            fc.read(bb);
            in.close();
        } catch (IOException ex) {
            throw new WeatherException(4003, ex);
        }

    }

    /**
     * Writes the given file.
     *
     * @param file The file to write.
     * @throws WeatherException If the file exists but is a directory rather
     * than a regular file, does not exist but cannot be created or cannot be
     * opened for any other reason.
     */
    public void writeFile(File file) throws WeatherException {
        try {
            BufferedOutputStream out =
                         new BufferedOutputStream(new FileOutputStream(file));
            out.write(fileData);
            out.close();
        } catch (FileNotFoundException ex1) {
            throw new WeatherException(4001, ex1);
        } catch (IOException ex2) {
            throw new WeatherException(4001, ex2);
        }
    }

    /**
     * Sets the data object number for this file instance. The data type number
     * is the number of the data object this file is associated with. For example,
     * if this file is stored with a Note, this refers to the noteNumber.
     * @param dataNumber The number of the data object to associate this file with.
     */
    public final void setDataNumber(int dataNumber) {
        this.dataTypeNumber = dataNumber;
    }
    
    /**
     * Returns the data object number that this file is associated with.
     * @return The data object number that this file is associated with.
     */
    public int getDataNumber() {
        return dataTypeNumber;
    }

}
