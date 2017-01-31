


/**
 *
 * This package contains the classes that implement our data retrieval system.
 * Clients or users of the data retrieval system will use the classes in
 * <code> weather.serverside.applicationimpl</code>.
 *
 * The actions of the data retrieval system are controlled by an instance
 * of <code>RetrievalServer</code>. When this server receives a command it
 * hands it off to an instance of <code>RetrievalCommandHandler</code> which
 * obtains and executes the <code>RerievalCommand</code> which must be of
 * one of the command types listed in <code>RetrievalCommandType</code>.
 *
 * The class <code>RetrievalCommandHandler</code> uses an instance of the class
 * <code>ResourceRetriever</code> to execute the commands it receives.
 * <code>RetrievalCommandHandler</code> knows how to obtain information from a
 * <code>RerievalCommand</code> object and what methods in
 * <code>ResourceRetriever</code> should be invoked to execute that command. 
 *
 * <code>ResourceRetriever</code> is a
 * <code> java.util.concurrent.ScheduledThreadPoolExecutor</code>
 * that keeps a hash map of
 * <code>java.util.concurrent.Future</code> objects. These future objects
 * are scheduled to obtain resources at a fixed rate. Commands to the server
 * add or remove resources from this hash map. Each future object is executed
 * in a thread from a thread pool. The threads execute runnable objects from
 * the class <code>ResourceRetrieverRunnable</code>.
 *
 * <code>ResourceRetrieverRunnable</code> is a class that knows how to obtain
 * resource instances and place them in the storage system.
 * 
 *
 * The <code>RetrievalClient</code> class is used to send commands to a
 * Retrieval Server.
 *
 * @see java.util.concurrent.Future
 * @see java.util.concurrent.ScheduledThreadPoolExecutor
 * @see weather.serverside.applicationimpl
 * @since 1.0
 */

package weather.serverside.retrieval;

