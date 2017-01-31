package weather.serverside.retrieval;

import java.io.Serializable;

/**
 * An enum that specifies the kinds of commands the RetrievalServer can execute.
 * 
 * START: Either starts or updates a Resource retrieving on the RetrievalServer
 * 
 * STOP: Causes a Resource on the RetrievalServer to stop retrieving
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public enum RetrievalCommandType implements Serializable{
    /**
     * Either starts or updates a Resource retrieving on the RetrievalServer
     */
    START,
    /**
     *Causes a Resource on the RetrievalServer to stop retrieving
     */
    STOP;
    public static final long serialVersionUID = 1L;
}
