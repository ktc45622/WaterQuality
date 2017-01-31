package weather.clientside.utilities;

import weather.GeneralService;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.resource.Resource;

/**
 * This class holds static methods used to test what instances of
 * <code>Resource</code> is visible to a specific
 * <code>User</code>, Note that administrators and instructors can see all
 * resources.
 *
 * @author Brian Bankes
 */
public class ResourceVisibleTester {
    
    /**
     * A static method used to test if a
     * <code>Resource</code> is visible to a specific
     * <code>User</code>,
     * @param user The <code>User</code> being checked.
     * @param resource The <code>Resource</code> being checked.
     * @return True if the <code>User</code> can see the <code>Resource</code>,
     * False otherwise.
     */
    public static boolean canUserSeeResource(User user, Resource resource) {
        if (user == null) {
            return resource.isVisible();
        } else {
            boolean canSeeAllResources = user.getUserType() == UserType.administrator
                    || user.getUserType() == UserType.instructor;
            return canSeeAllResources || resource.isVisible();
        }
    }
    
    /**
     * A static method to test if one of each type of
     * <code>Resource</code> is visible to a specific
     * <code>User</code>,
     * @param user The <code>User</code> being checked.
     * @param gs The <code>GeneralService</code> of the program.
     * @return True if the <code>User</code> can see at least one of each type 
     * of <code>Resource</code>, False otherwise. 
     */
    public static boolean canUserSeeAllTypes(User user, GeneralService gs) {
        //Assume user can't see each type until the type is tested.
        boolean canSeeCamera = false;
        boolean canSeeMap = false;
        boolean canSeeStation = false;
        
        //Look for a camera the user can see.
        for (Resource resource : gs.getWeatherCameraResources()) {
            if (canUserSeeResource(user, resource)) {
                canSeeCamera = true;
                break;
            }
        }
        
        //Look for a map the user can see.
        for (Resource resource : gs.getWeatherMapLoopResources()) {
            if (canUserSeeResource(user, resource)) {
                canSeeMap = true;
                break;
            }
        }
        
        //Look for a station the user can see.
        for (Resource resource : gs.getWeatherStationResources()) {
            if (canUserSeeResource(user, resource)) {
                canSeeStation = true;
                break;
            }
        }
        
        return canSeeCamera && canSeeMap && canSeeStation;
    }
}
