package weather.serverside.retrieval;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.WeatherTracer;

/**
 * A class that obtains and executes commands sent to 
 * a RetrievalServer. Each command is executed in its own thread. 
 *
 * @author Bloomsburg University Software Engineering
 * @author Dave Moser (2008)
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class RetrievalCommandHandler implements Runnable{

    private Socket clientSocket;
    private ResourceRetriever retriever;

    private static WeatherTracer log;

    /**
     * Constructor for the RetrievalCommandHandler object.
     * 
     * @param clientSocket a socket that represents the client
     * @param retriever the ResourceRetriever the command will be executed on
     */
    public RetrievalCommandHandler(Socket clientSocket, ResourceRetriever retriever) {
        this.clientSocket = clientSocket;
        this.retriever = retriever;

        // Get logger.
        log = WeatherTracer.getRetrievalLog ();
    }

    /**
     * The command request thread.
     * 
     */
    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            RetrievalCommand command = (RetrievalCommand) ois.readObject();
            execute(command);
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, "I/O Error Reading Input  Stream.", ex);
            log.severe ("I/O Error Reading Input  Stream.", ex);
        } catch (ClassNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, "Incompatable versions of our code.", ex);
            log.severe ("Incompatable versions of our code.", ex);
        }
        try{
            clientSocket.close();
        }catch(Exception e){
            WeatherLogger.log(Level.WARNING, "Could not close client socket.", e);
            log.severe ("Could not close client socket.", e);
        }
    }

    /**
     * Executes the desired command on the ResourceRetriever specified by the constructor.
     * @param command the desired command
     */
    private void execute(RetrievalCommand command) {
        switch(command.getCommandType())
        {
            case START:
                //retriever.reloadResources(); //Should not be called
                retriever.startRetrieval(command.getResource());
                break;
            case STOP:
                retriever.stopRetrieval(command.getResource());
                break;
        }
    }
    
    
}
