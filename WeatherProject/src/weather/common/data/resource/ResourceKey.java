package weather.common.data.resource;

/**
 * Contains a date and a number that can uniquely identify a weather resource
 * instance.  This is commonly used as a key to a map to ensure resources are
 * only downloaded once.
 *
 * @author Bloomsburg University Software Engineering
 * @author Tyler Lushby(2008)
 * @author Dustin Jones (2010)
 *
 * @version Spring 2010
 */
public class ResourceKey implements Comparable
{
    /**
     * The resrouceNumber of this resource.
     */
    private int resourceNumber;
    /**
     * The start date of this instance of the resource represented by this
     * resource key.
     */
    private long startDate;

    /**
     * Constructor that initializes the fields with the given data.
     * 
     * @param resourceNumber The resource number for the resource that is
     * specified by this resource key.
     * @param startDate The start date of the resource represented by this
     * resource key.
     */
    public ResourceKey(int resourceNumber, long startDate)
    {
        this.resourceNumber = resourceNumber;
        this.startDate = startDate;
    }

    /**
     * Constructor that initializes the fields to that of the given key.
     * Effectively used as a copy constructor.
     * @param key The <code>ResourceKey</code> who's data is being copied to this key.
     */
    public ResourceKey(ResourceKey key) {
        this.resourceNumber = key.getResourceNumber();
        this.startDate = key.getStartDate();
    }
    /**
     * Gets the date the resource was started.
     * @return The date the resource was started.
     */

    public long getStartDate()
    {
        return startDate;
    }
    /**
     * Sets the date the resource was started.
     * @param startDate The date the resource was started.
     */

    public void setStartDate(long startDate)
    {
        this.startDate = startDate;
    }
    /**
     * Gets the number of the resource.
     * @return The number of the resource.
     */
    public int getResourceNumber()
    {
        return resourceNumber;
    }
    /**
     * Sets the number of the resource.
     * @param resourceNumber The number of the resource.
     */
    public void setResourceNumber(int resourceNumber)
    {
        this.resourceNumber = resourceNumber;
    }

    /**
     * Implements the compareTo method to make this object Comparable.
     * This enables objects of this class to be compared and sorted.
     * 
     * @param comparable The object we are comparing this object to.
     * @return -1 if this object is less than comparable, 1 if this object is
     * greater than comparable, 0 otherwise (if they are equal).
     */
    @Override
    public int compareTo(Object comparable)    {
        ResourceKey comparableMovieKey = (ResourceKey)comparable;
        if(this.equals(comparableMovieKey)) return 0;
        if(this.startDate < comparableMovieKey.startDate) return -1;
        if(this.startDate > comparableMovieKey.startDate) return  1;
        return this.getResourceNumber()- comparableMovieKey.getResourceNumber();
        
        /*
        if(comparableMovieKey.getStartDate() < this.startDate ||comparableMovieKey.resourceNumber < this.resourceNumber ) {
            return 1;
        }
        if(comparableMovieKey.getStartDate() > this.startDate ||comparableMovieKey.resourceNumber > this.resourceNumber )    {
            return -1;
        }
        return 0;
                */
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + this.resourceNumber;
        hash = 61 * hash + (int) (this.startDate ^ (this.startDate >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourceKey other = (ResourceKey) obj;
        if (this.resourceNumber != other.resourceNumber) {
            return false;
        }
        if (this.startDate != other.startDate) {
            return false;
        }
        return true;
    }
    
}
