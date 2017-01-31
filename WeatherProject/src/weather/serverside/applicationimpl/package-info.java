

/**
 * This package contains the implementation files for all of our
 * server side control systems. We currently have three such systems:
 * our <b>Moviemaker</b> system, our <b>Storage</b> system and our
 * <b>Data Retrieval</b> system. Clients of these systems need to use the classes
 * in this package!
 *
 * Since all three of these systems could reside on their own physical server,
 * we have two implementations of our <b>Storage</b> system. Our regular
 * implementation (<code>StorageControlSystemImpl</code>) assumes our
 * storage system resides on a machine that is different from the code
 * that is using it. Our main application clients will use this implementation.
 * Our local implementation (<code>StorageControlSystemLocalImpl</code>) assumes
 * it is on the same machine as the code that us using it.
 *
 * <code>DataRetrievalSystemImpl</code> assumes the storage system is not
 * on the same machine as the data retrieval system.
 * <code>DataRetrievalSystemLocalImpl</code> does assume the storage
 * system is on the same machine as the data retrieval system.
 * This local implementation is preferred.
 *
 * <code>MovieMakerSystemImpl</code> assumes the movie making system is remote
 * to the code sending it commands. The commands  that the movie maker server
 * will accept are to start or stop making movies for a particular resource.
 *
 * @see weather.serverside.imageprocessing
 * @see weather.serverside.movie
 * @see weather
 * 
 * @since 1.0
 */

package weather.serverside.applicationimpl;

