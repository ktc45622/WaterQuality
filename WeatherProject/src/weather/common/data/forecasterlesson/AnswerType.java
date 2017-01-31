package weather.common.data.forecasterlesson;

/**
 * This is an enum that indicates how a student will respond to a question.
 * 
 * @author Joshua Whiteman
 */
public enum AnswerType {
    /**
     * Indicates that the students possible responses will be displayed as
     * radio buttons.
     */
    RadioButton("Radio Button"),
    /**
     * Indicates that the students possible responses will be displayed as a
     * text field.
     */
    TextField("Text Field"),
    /**
     * Indicates that the students possible responses will be displayed as
     * check boxes.
     */
    CheckBox("Check Box");

    private String stringValue;

    /**
     * 
     * @param stringValue 
     */
    AnswerType(String stringValue) {
        this.stringValue = stringValue;
    }
    
    /**
     * 
     * @return the string value of the answer type
     */
    @Override
    public String toString() {
        return (stringValue);
    }
}
