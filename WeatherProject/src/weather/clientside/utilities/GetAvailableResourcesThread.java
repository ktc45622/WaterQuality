package weather.clientside.utilities;

import java.util.Vector;
import weather.ApplicationControlSystem;
import weather.GeneralService;
import weather.common.data.resource.Resource;

/**
 * Thread that gets a list of all available resources camera, weather map loop,
 * and weather station resources.
 * 
 * @author Eric Subach
 * @version Spring 2011
 */
public class GetAvailableResourcesThread extends Thread {
    private GeneralService gs;

    private Vector<Resource> camResources;
    private Vector<Resource> mapLoopResources;
    private Vector<Resource> weatherStationResources;

    public GetAvailableResourcesThread(GeneralService gs) {
        this.gs = gs;
    }
    //TODO: JavaDocs
    @Override
    public void run() {
        camResources = gs.getWeatherCameraResources();
        mapLoopResources = gs.getWeatherMapLoopResources();
        weatherStationResources = gs.getWeatherStationResources();
    }

    public Vector<Resource> getCameraResources() {
        return camResources;
    }

    public Vector<Resource> getMapLoopResources() {
        return mapLoopResources;
    }

    public Vector<Resource> getWeatherStationResources() {
        return weatherStationResources;
    }
}
