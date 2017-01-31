package weather.common.data.forecasterlesson;

/**
 * Sets the Instructions for a lesson that the instructor creates.
 * 
 * TODO: The instructions are just represented as a String for now, but they
 * could be done using HTML or in another way. Different fields could be used
 * so the instructor could set headings, body, etc.
 * 
 * @author Xiang Li
 * @author Nikita Maizet
 */
public class Instructions {

    private String instructionsID;
    private String instructionsText;

    /**
     * Constructor that will be used to create a Instructions object.
     * 
     * @param instructionsID a database identifier
     * @param instructionsText instructions text
     */
    public Instructions(String instructionsID, String instructionsText) {
        this.instructionsID = instructionsID;
        this.instructionsText = instructionsText;
    }
    
    /**
     * TODO: Remove this constructor once the SQLHelper.java is changed.
     */
    public Instructions(){
        
    }
    
    /**
     * The text representation of the instructions.
     * @return text representation of the instructions.
     */
    public String getInstructionsText() {
        return instructionsText;
    }
    
    /**
     * Gets the db id for the instructions to allow for updating.
     * @return database identifier
     */
    public String getInstructionsID() {
        return instructionsID;
    }
    
    /**
     * A text representation of the Instructions used during testing.
     * @return a text representation of the Instructions used during testing.
     */
    @Override
    public String toString() {
        return "Instructions{" + "instructionsID=" + instructionsID + ", instructionsText=" + instructionsText + '}';
    }
}
