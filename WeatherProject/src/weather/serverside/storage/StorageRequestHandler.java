package weather.serverside.storage;

import java.net.Socket;

/**
 * This is an interface for a thread that will handle storage requests.
 *
 * @author Bloomsburg University Software Engineering
 * @version Spring 2008
 */
public interface StorageRequestHandler extends Runnable {

    public boolean isAvailable(); // available to handle a request

    public void setAvailable(boolean available);

    public void setSocket(Socket socket);
}
