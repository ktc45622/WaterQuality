package weather.clientside.utilities;

import weather.clientside.gradebook.GBForecastingAttemptEntry;

/**
 *
 * @author nm38076
 */
public class FGBLessonViewANode implements JTreeCustomNode {
    
    private GBForecastingAttemptEntry attempt;
    
    public FGBLessonViewANode(GBForecastingAttemptEntry attempt) {
        this.attempt = attempt;
    }

    @Override
    public void setSourceObject(Object obj) {
        attempt = (GBForecastingAttemptEntry) obj;
    }

    @Override
    public Object getSourceObject() {
        return attempt;
    }
}
