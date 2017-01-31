package weather.clientside.utilities;

import java.util.Vector;
import weather.ApplicationControlSystem;
import weather.clientside.manager.MovieController;
import weather.clientside.manager.WeatherStationPanelManager;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSResourceManager;

/**
 * This is a utility class that opens bookmarks and events in the main window.
 *
 * @author Brian Bamkes
 */
public class BookmarkEventOpener {

    /**
     * Function that opens bookmarks and events in the main window.
     *
     * @param appControl The program's <code>ApplicationControlSystem</code>.
     * @param bookmark The <code>Bookmark</code> to be opened (instance or
     * event.
     * @param controller The main program's <code>MovieController</code>.
     * @param mainRange a <code>ResourceRange</code> that specifies the range of
     * videos to be shown. This is needed because plain bookmarks have no video
     * range, but must always be given.
     */
    public static void openBooekmark(final ApplicationControlSystem appControl,
            final Bookmark bookmark, final MovieController controller,
            final ResourceRange mainRange) {
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                if (bookmark.getType() == BookmarkDuration.instance) {
                    return "Bookmark";
                } else {
                    return "Event";
                }
            }

            @Override
            protected void doLoading() {
                //Vriables to check for empty resources - assume none.
                boolean hasNoCamera = true;
                boolean hasNoMap = true;

                //Get resource manager
                DBMSResourceManager resources = appControl.getDBMSSystem()
                        .getResourceManager();

                //Get movie resources
                Vector<Resource> newResources = new Vector<>();
                Resource resource;
                resource = resources.getWeatherResourceByNumber(bookmark
                        .getWeatherCameraResourceNumber());
                if (resource != null && ResourceVisibleTester
                        .canUserSeeResource(appControl
                        .getGeneralService().getUser(), resource)) {
                    appControl.getGeneralService()
                            .setCurrentWeatherCameraResource(resource);
                    newResources.add(resource);
                    hasNoCamera = false;
                }

                resource = resources.getWeatherResourceByNumber(
                        bookmark.getWeatherMapLoopResourceNumber());
                if (resource != null && ResourceVisibleTester
                        .canUserSeeResource(appControl
                        .getGeneralService().getUser(), resource)) {
                    appControl.getGeneralService()
                            .setCurrentWeatherMapLoopResource(resource);
                    newResources.add(resource);
                    hasNoMap = false;
                }

                controller.setFutureVideoResources(newResources);

                //Update main range, but see that only videos are changed
                WeatherStationPanelManager targetManager = controller
                        .getPrimaryWeatherStationPanelManager();
                controller.removeWeatherStationPanel(targetManager);
                controller.updateRange(mainRange);
                controller.registerWeatherStationPanel(targetManager);

                //Set panets to none if resources aren't found
                controller.setVideoPanelsToNone(hasNoCamera, hasNoMap);

                //Update weather station panel manager
                ResourceRange plotRange = new ResourceRange(bookmark
                        .getPlotRangeStartTime(), bookmark
                        .getPlotRangeEndTime());
                targetManager.setLastManualRange(mainRange);
                targetManager.setResourceRange(plotRange);
                targetManager.setGraphFittedState(bookmark
                        .getGraphFittedOption());
                targetManager.setGraphByRadioText(bookmark
                        .getSelectedRadioName());
                targetManager.setDaySpanOptionByIndex(bookmark.
                        getPlotDaySpanComboBoxIndex());

                //Get weather station
                resource = resources.getWeatherResourceByNumber(bookmark.
                        getWeatherStationResourceNumber());

                if (resource != null && ResourceVisibleTester
                        .canUserSeeResource(appControl
                        .getGeneralService().getUser(), resource)) {
                    appControl.getGeneralService()
                            .setCurrentWeatherStationResource(resource);
                    controller.setWeatherStationResource(resource);
                } else {
                    appControl.getGeneralService()
                            .setCurrentWeatherStationResource(null);
                    controller.setWeatherStationResource(null);
                }
            }
        };
        loader.execute();
    }
}
