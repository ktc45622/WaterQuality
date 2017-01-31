package weather.clientside.gui.component;

import java.util.TimeZone;
import javax.swing.Icon;
import weather.GeneralService;
import weather.common.data.resource.Resource;
import weather.common.gui.component.IconProperties;

/**
 * Represents a resource with an icon that will be displayed in a combo box.
 *
 * @author Eric Subach (2011)
 */
public class ResourceListCellItem implements Comparable {
    private String name;
    private Icon icon;
    private boolean active;
    // Flag for if this is actually a resource or just a spacer.
    private boolean isResource;

    
    public ResourceListCellItem (String name, Icon icon, boolean active) {
        this.name = name;
        this.icon = icon;
        this.active = active;
        isResource = true;
    }

    public ResourceListCellItem () {
        name = "None";
        icon = IconProperties.getResourceSpacerIconImage();
        active = false;
        isResource = false;
    }

    public ResourceListCellItem (String name) {
        this.name = name;
    }

    /**
     * Compare a ResourceListCellItem to another or to a String.
     *
     * @param obj ResourceListCellItem or String.
     * @return True if they have the same name, false otherwise.
     */
    @Override
    public boolean equals (Object obj) {
        if (obj instanceof ResourceListCellItem) {
            return (name != null && name.equals(((ResourceListCellItem) obj).getName()));
        }
        else if(obj instanceof String) {
            return (name.equals((String)obj));
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode () {
        return name.hashCode();
    }

    @Override
    public String toString () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    public void setIcon (Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon () {
        return icon;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setIsResource(boolean value) {
        isResource = value;
    }

    public boolean getIsResource() {
        return isResource;
    }
    
    /**
     * Returns the <code>TimeZone</code> or the <code>Resource</code> named in
     * this instance.
     * @param genService The program's <code>GeneralService</code>.
     * @return The <code>TimeZone</code> or the <code>Resource</code> named in
     * this instance.
     */
    public TimeZone getResourceTimeZone(GeneralService genService) {
        //Get Resource from this instance.
        Resource thisResource = null;
        for(Resource resource : genService.getDBMSSystem()
                .getResourceManager().getResourceList()) {
            if(resource.getName().equals(name)) {
                thisResource = resource;
                break;
            }
        }
        
        //Get TimeZone of Resouce or local TimeZone if Resouce is "None."
        if (thisResource == null) {
            return TimeZone.getDefault();
        } else {
            return thisResource.getTimeZone().getTimeZone();
        }
    }

    /**
     * Compare two ResourceListCellItems based on whether the resource is active
     * or inactive.
     *
     * @param o ResourceListCellItem to which we compare this item.
     * @return A negative integer, zero, or a positive integer as this object 
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Object o) {
        ResourceListCellItem other = null;

        // Make sure it is of the right type.
        if (o instanceof ResourceListCellItem) {
            other = (ResourceListCellItem) o;
        } else {
            return 0;
        }


        if (isResource && active) {
            if (other.isResource && other.active) {
                return name.compareTo(other.name);
            } else {
                return -1;
            }
        } else if (isResource && !active) {
            if (other.isResource && other.active) {
                return 1;
            } else if (other.isResource && !other.active) {
                return name.compareTo(other.name);
            } else {
                return -1;
            }
        } else {
            if (other.isResource) {
                return 1;
            } else {
                return name.compareTo(other.name);
            }
        }
    }
}
