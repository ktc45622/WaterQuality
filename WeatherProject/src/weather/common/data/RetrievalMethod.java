package weather.common.data;

import java.io.Serializable;

/**
 * This enumerated type specifies the valid retrieval methods
 * for our weather resources. 
 * 
 * Valid retrieval methods are:   undefined, url, fileload, 
 * and manual. Undefined is the default value. 
 * 
 *@author Bloomsburg University Software Engineering
 *@author Jacob Kelly (2007)
 *@version Spring 2008
 */
public enum RetrievalMethod implements Serializable {
    /**
     * Means the retrieval method was undefined.
     */
    undefined,
    /**
     * Means the retrieval method was a website.
     */
    url,
    /**
     * Means the retrieval method was from a file.
     */
    fileload,
    /**
     * Means the retrieval method was manual.
     */
    manual;
    private static final long serialVersionUID = 1L;
}
