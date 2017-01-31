

/**
 * This package contains the classes that implement our resource storage system.
 * Users of the storage system will not need these classes. Users of
 * the storage system will use the classes in
 * <code> weather.serverside.applicationimpl</code>.
 *
 * The actions of the storage system are controlled by a storage system server
 * which implements <code>StorageCommands</code> that are specified by
 * <code>StorageCommandType</code>. This package provides two implementations
 * of a storage system server. The class <code>StorageServerLocal</code> is
 * to be used by code running on the same system as the storage system. Normally,
 * our movie maker system and data retrieval system is on the same
 * machine as our storage system. See the class
 * <code>weather.serverside.applicationimpl.StorageControlSystemLocalImpl</code>
 * for the main class that uses this local server idea.
 *
 * The class <code>StorageServer</code> is a server that executes on the storage
 * system machine and accepts <code>StorageCommands</code>. It is used by
 * <code>weather.serverside.applicationimpl.StorageControlSystemImpl</code>.
 * The storage system server uses a
 * <code>java.util.concurrent.ScheduledThreadPoolExecutor</code> to manage the
 * executions of handler threads which are stored in a
 * <code>StorageHandlerPool</code>. the threads are kept in this pool to save
 * the time to create new threads and to limit the max number of threads that can
 * be active at any point in time.
 *
 * Commands to a storage system server are implemented by a
 * <code>StorageRequestHandlerImpl</code> object. This class uses the
 * <code>StorageManagement</code> class to execute storage commands.
 *
 * The class <code>StorageManagement</code> actually implements many of 
 * the operations required for storage system functionality.
 *
 * The class <code>StorageServerMainProgram</code> starts our storage
 * system server.
 * 
 *
 *
 *
 *
 * @see weather.serverside.applicationimpl.StorageControlSystemLocalImpl
 * @see weather.serverside.applicationimpl.StorageControlSystemImpl
 * @since 1.0
 */

package weather.serverside.storage;

