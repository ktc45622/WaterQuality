package weather.serverside.retrieval;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import weather.common.utilities.PropertyManager;

/**
 * A client class for the RetrievalServer. All the configuration is done through
 * the GeneralWeatherPropertiesSingleton, so to use this class one only needs to
 * create a RetrievalCommand object and invoke the sendCommand method.
 * @author Bloomsburg University Software Engineering
 * @author Dave Moser (2008)
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class RetrievalClient
{

    /**
     * A method to send commands to the RetrievalServer.
     * @param command the desired command specified by a RetrievalCommandType enum
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException 
     */
    public static void sendCommand(RetrievalCommand command) throws UnknownHostException, IOException
    {
        Socket socket = new Socket(PropertyManager.getGeneralProperty("retrievalHost"),
                Integer.parseInt(PropertyManager.getGeneralProperty("retrievalPort")));

        OutputStream out = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);

        oos.writeObject(command);

        socket.close();
    }
}
