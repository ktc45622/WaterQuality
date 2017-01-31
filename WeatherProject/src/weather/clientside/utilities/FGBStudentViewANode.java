/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package weather.clientside.utilities;

import weather.clientside.gradebook.GBForecastingAttemptEntry;

/**
 *
 * @author nm38076
 */
public class FGBStudentViewANode implements JTreeCustomNode {
    
    private GBForecastingAttemptEntry attempt;
    
    public FGBStudentViewANode(GBForecastingAttemptEntry attempt) {
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
