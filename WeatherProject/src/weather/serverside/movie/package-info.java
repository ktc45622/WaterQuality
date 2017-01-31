


/**
 * This package contains the files needed to implement our movie maker system.
 * The class <code>MovieMakerServer</code> contains a main program 
 * that starts our movie maker server thread.
 * This server keeps a list of resources and schedules movies to be created
 * for each of these resources once every hour. Clients of the server can
 * request that resources be added or removed from this list.
 * Adding and removing resources to the list of movies to be created every hour
 * is accomplished by sending a
 * <code>MovieCommand</code> to the movie maker server. The server passes
 * this command off to an instance of the <code>MovieServerCommandHandler</code> class
 * which implements the command by modifying the list of movies to be made in the
 * future.
 *
 * <code>MovieMakerScheduler</code> is a
 * <code>java.util.concurrent.ScheduledThreadPoolExecutor</code>
 * that keeps track of the resources that require movie creation and
 * schedules them as <code>java.util.concurrent.Future;</code> events.
 *
 * <code> MovieMakerRunnable</code> is the runnable class that
 * obtains the files and has the movies created each hour. Right now the system
 * only makes one movie at a time, but the code is designed to allow for multiple
 *  instances of the <code> MovieMakerRunnable</code> class to be active. The limit
 * to 1 is set in <code>MovieMakerScheduler</code> which specifies the core pool
 * size in its constructor.
 *
 *
 * The <code>MovieMakerClient</code> class will send a <code>MovieCommand</code>
 * object to the movie maker server for this system. Any code needed to send
 * a command to the system's movie maker server will normally use this code.
 *
 *
 * @see java.util.concurrent.ScheduledThreadPoolExecutor
 * @see java.util.concurrent.Future
 * @see weather.serverside.imageprocessing
 * @see weather.serverside.applicationimpl
 * @since 1.0
 */

package weather.serverside.movie;

