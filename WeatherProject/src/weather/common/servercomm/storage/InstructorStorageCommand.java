package weather.common.servercomm.storage;

import weather.common.data.Instructor;
import weather.common.data.InstructorDataType;
import weather.common.data.InstructorFileInstance;

/**
 *
 * @author Ryan
 */
public class InstructorStorageCommand extends StorageCommand{
    private Instructor instructor;
    private InstructorFileInstance data;
    private String filename;
    private InstructorDataType instructorDataType;

    public InstructorDataType getInstructorDataType() {
        return instructorDataType;
    }

    public void setInstructorDataType(InstructorDataType instructorDataType) {
        this.instructorDataType = instructorDataType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public InstructorFileInstance getData() {
        return data;
    }

    public void setData(InstructorFileInstance data) {
        this.data = data;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }


}
