package weather.clientside.utilities;

import weather.clientside.gradebook.GBForecastingLessonEntry;

/**
 * Holds necessary objects to render a JTree row in the 
 * GradebookStudentEntryWindow class.
 * 
 * @author Nikita Maizet
 */
public class FGBStudentViewLNode implements JTreeCustomNode {
    
    private GBForecastingLessonEntry lesson;
    
    public FGBStudentViewLNode(GBForecastingLessonEntry lesson) {
        this.lesson = lesson;
    }

    @Override
    public void setSourceObject(Object obj) {
        lesson = (GBForecastingLessonEntry) obj;
    }

    @Override
    public Object getSourceObject() {
        return lesson;
    }
    
}
