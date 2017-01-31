package weather.clientside.utilities;

import weather.clientside.gradebook.GBForecastingLessonEntry;

/**
 * Holds necessary objects to render a JTree row in the AssignmentGradeReport class.
 * 
 * @author Nikita Maizet
 */
public class FGBLessonViewLNode implements JTreeCustomNode {
    
    private GBForecastingLessonEntry lesson;
    
    public FGBLessonViewLNode(GBForecastingLessonEntry lesson) {
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
