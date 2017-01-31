package weather.common.data.forecasterlesson;

/**
 * This class provides access to templates that are used to make instances of 
 * the class <code>Question</code>. Each creation of a <code>Question</code>
 * requires that a template be passed to its constructor. All template data
 * is preset in the database and cannot be changed without database access. The
 * constructor of the class is designed to store a row of database fields in the
 * object it makes. The methods of the class provide access to the fields. 
 * 
 * @author Joshua Whiteman
 */
public class QuestionTemplate{

    /**
     * The primary key of the row providing data for the object in the database.
     */
    private final String questionTemplateID;
    
    /**
     * The text of the template, which may contain %s as a placeholder for the
     * zulu time provided by the <code>Question</code> created with this 
     * template.
     */
    private final String questionTemplateText;
    
    /**
     * The standardized part of the key indicating where the answer can be found
     * in the answer data obtained from the web. It may be complete like PRECIP
     * of need a zulu time added like CLOUD. Code in <code>Question</code> is
     * responsible for determining this and adding the time if necessary. 
     */
    private final String dataKey;
    
    /**
     * The name of the template, which may contain %s as a placeholder for the
     * zulu time provided by the <code>Question</code> created with this
     * template.
     */
    private final String questionName;
    
    /**
     * The location on a preset web page where the user can get more information
     * about the <code>Question</code> created with this template.
     */
    private final String urlLocation;
    
    /**
     * The text of a hyperlink provided by the template for purposes of
     * providing more information about the <code>Question</code> created with
     * this template, which may contain %s as a placeholder for the zulu time
     * provided by the <code>Question</code> created with this template.
     */
    private final String urlText;
    
    /**
     * The type of answers that must be provided to the <code>Question</code>
     * created with this template. It is indicative of either a text box, radio 
     * buttons, or check boxes. 
     */
    private final AnswerType answerType;

    /**
     * The constructor that takes the database information as parameters.
     * 
     * @param questionTemplateID The primary key of the row providing data for 
     * the object in the database.
     * @param questionTemplateText The text of the template, which may contain
     * %s as a placeholder for the zulu time provided by the
     * <code>Question</code> created with this template.
     * @param dataKey The standardized part of the key indicating where the
     * answer can be found in the answer data obtained from the web. It may be
     * complete like PRECIP of need a zulu time added like CLOUD. Code in
     * <code>Question</code> is responsible for determining this and adding the
     * time if necessary.
     * @param questionName The name of the template, which may contain %s as a
     * placeholder for the zulu time provided by the <code>Question</code>
     * created with this template.
     * @param urlLocation The location on a preset web page where the user can
     * get more information about the <code>Question</code> created with this
     * template.
     * @param urlText The text of a hyperlink provided by the template for
     * purposes of providing more information about the <code>Question</code>
     * created with this template, which may contain %s as a placeholder for the
     * zulu time provided by the <code>Question</code> created with this
     * template.
     * @param answerType The type of answers that must be provided to the
     * <code>Question</code> created with this template. It is indicative of
     * either a text box, radio buttons, or check boxes.
     */
    public QuestionTemplate(String questionTemplateID, 
            String questionTemplateText, String dataKey, String questionName,
            String urlLocation, String urlText, AnswerType answerType) {
        this.questionTemplateID = questionTemplateID;
        this.questionTemplateText = questionTemplateText;
        this.dataKey = dataKey;
        this.questionName = questionName;
        this.urlLocation = urlLocation; 
        this.urlText = urlText;
        this.answerType = answerType;
    }
    
    /**
     * Gets the text of the template, which may contain %s as a placeholder for
     * the zulu time provided by the <code>Question</code> created with this
     * template.
     * 
     * @return The text of the template, which may contain %s as a placeholder
     * for the zulu time provided by the <code>Question</code> created with this
     * template.
     */
    public String getText() {
        return questionTemplateText;
    }

    /**
     * Gets the primary key of the row providing data for the object in the
     * database.
     * 
     * @return The primary key of the row providing data for the object in the
     * database.
     */
    public String getID() {
        return questionTemplateID;
    }

    /**
     * Gets the standardized part of the key indicating where the answer can be
     * found in the answer data obtained from the web. It may be complete like
     * PRECIP of need a zulu time added like CLOUD. Code in
     * <code>Question</code> is responsible for determining this and adding the
     * time if necessary.
     *
     * @return The standardized part of the key indicating where the answer can
     * be found in the answer data obtained from the web. It may be complete
     * like PRECIP of need a zulu time added like CLOUD. Code in
     * <code>Question</code> is responsible for determining this and adding the
     * time if necessary.
     */
    public String getDataKey() {
        return dataKey;
    }
    
    /**
     * Gets the name of the template, which may contain %s as a placeholder for
     * the zulu time provided by the <code>Question</code> created with this
     * template.
     * 
     * @return The name of the template, which may contain %s as a placeholder
     * for the zulu time provided by the <code>Question</code> created with this
     * template. 
     */
    public String getQuestionName() {
        return questionName;
    }
    
    /**
     * Gets the location on a preset web page where the user can get more
     * information about the <code>Question</code> created with this template.
     * 
     * @return The location on a preset web page where the user can get more
     * information about the <code>Question</code> created with this template. 
     */
    public String getURLLocation() {
        return urlLocation;
    }
    
    /**
     * Gets the text of a hyperlink provided by the template for purposes of
     * providing more information about the <code>Question</code> created with
     * this template, which may contain %s as a placeholder for the zulu time
     * provided by the <code>Question</code> created with this template.
     * 
     * @return The text of a hyperlink provided by the template for purposes of
     * providing more information about the <code>Question</code> created with
     * this template, which may contain %s as a placeholder for the zulu time
     * provided by the <code>Question</code> created with this template.
     */
    public String getURLText() {
        return urlText;
    }

    /**
     * Gets the type of answers that must be provided to the
     * <code>Question</code> created with this template. It is indicative of
     * either a text box, radio buttons, or check boxes.
     * 
     * @return The type of answers that must be provided to the
     * <code>Question</code> created with this template. It is indicative of
     * either a text box, radio buttons, or check boxes.
     */
    public AnswerType getAnswerType() {
        return answerType;
    }
    
    /**
     * Gets a String representation for the <code>QuestionTemplate</code> to be used
     * during testing.
     *
     * @return a String representation for the <code>QuestionTemplate</code> to be used
     * during testing.
     */
    @Override
    public String toString() {
        return "QuestionTemplate{" + "questionTemplateID=" + questionTemplateID 
                + ", questionName=" + questionName
                + ", questionTemplateText=" + questionTemplateText 
                + ", urlText=" + urlText + ", urlLocation=" + urlLocation
                + ", dataKey=" + dataKey + ", answerType=" + answerType + '}';
    }
    
    
}
