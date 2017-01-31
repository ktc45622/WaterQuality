/**
 * This package contains classes that implement a watchdog service used to
 * handle errors in the storage, retrieval, movie maker and remote database 
 * systems as well as log the errors for future tracking and statistics.
 * 
 * The class <code>ServerWatchdogService</code> is a main class that runs each
 * watchdog thread periodically. Ideally, this is run as a Windows Service.
 * 
 * The <code>SeverWatchdog</code> abstract class is a thread that contains the 
 * skeleton logic and common functionalities for the extending watchdog threads.
 * 
 * The <code>ServerWatchdogErrorEvent</code> class that is used to keep track of
 * information when errors are encountered by a watchdog. Information about
 * the system, error type, action(s) taken, the relevant resource and other
 * information is stored for further tracking. The class is fully comparable and
 * consistent with equals. 
 * 
 * <code>MovieMakerWatchdog</code> is a class used for checking the movie maker
 * system for errors. It checks if image producing resources have a responding
 * url, available images and a made movie.
 * 
 * <code>RemoteDatabaseWatchdog</code> is a class used for checking the 
 * remote database server. It checks if the system is running and can process
 * queries correctly.
 * 
 * <code>RetrievalWatchdog</code> is a class used for checking the resource
 * retrieval system. It checks to see if the last stored resource instance is 
 * within a certain time period from the current time.
 * 
 * <code>StorageWatchdog</code> is a class used for checking the storage system.
 */
package weather.serverside.watchdog;