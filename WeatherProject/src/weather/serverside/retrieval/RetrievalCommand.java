package weather.serverside.retrieval;

import java.io.Serializable;
import weather.common.data.resource.Resource;

/**
 * A class that represents a command that can be executed on the RetrievalServer.
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class RetrievalCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private RetrievalCommandType commandType;
    private Resource resource;
    
    /**
     * A constructor for RetrievalCommand.
     * @param commandType an enum that specifies the desired command
     * @param resource a resource that the command will act on
     */
    public RetrievalCommand(RetrievalCommandType commandType, Resource resource)
    {
        this.commandType = commandType;
        this.resource = resource;
    }

    /**
     * Returns the command type.
     * @return the current RetrievalCommandType
     */
    public RetrievalCommandType getCommandType() {
        return commandType;
    }

    /**
     * Sets the command type.
     * @param commandType the desired RetrievalCommandType
     */
    public void setCommandType(RetrievalCommandType commandType) {
        this.commandType = commandType;
    }
    
    /**
     * Returns the SerialVersionUID.
     * @return the SerialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Returns the Resource associated with this RetrievalCommand.
     * @return the Resource
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets the Resource associated with this RetrievalCommand.
     * @param resource the Resource
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }
}