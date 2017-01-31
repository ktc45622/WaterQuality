package weather.common.dbms;

import weather.common.data.resource.Resource;

/**
 * This class is set up to manage the resource_relation table.  This table
 * contains data that links WeatherCameras to WeatherStations.  This relation
 * is a many to one relation in that many WeatherCameras can be related to
 * a single WeatherStation, but only one WeatherStation can be related to any
 * individual WeatherCamera.
 *
 * @author Joe Sharp (2009)
 */
public interface DBMSResourceRelationManager {

    /**
     * Retrieves the WeatherStation resource that is linked to this WeatherCamera.
     *
     * @param cameraResource The WeatherCamera resource for which to find a
     * WeatherStation resource.
     * @return The WeatherStation resource linked to the given WeatherCamera, or
     * null, if no WeatherStation resource is found for the given resource.
     */
    public Resource getRelatedStationResource(Resource cameraResource);

    /**
     * Given valid WeatherCamera, and WeatherStation resources, this method
     * adds the relationship into the resource relation table linking the
     * camera to the station.  If a relation already exists for the given
     * camera, that link is overwritten.
     *
     * @param cameraResource The WeatherCamera resource for the link.
     * @param weatherStationResource The WeatherStation resource for the link.
     */
    public void setResourceRelation(Resource cameraResource, Resource weatherStationResource);

    /**
     * Given a valid WeatherCamera, this method will remove any data in the
     * database that would link this camera to a WeatherStation resource.
     *
     * @param cameraResource The WeatherCamera for which to remove all
     * resource relation data.
     */
    public void removeResourceRelation(Resource cameraResource);
}
