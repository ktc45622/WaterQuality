package mysql;


/**
 * Represents a MySQL database that implements the <code>DatabaseManagement</code>
 * interface.
 *
 * @author cjones
 */
public class DatabaseManagement implements database.DatabaseManagement {

    private static final database.UserManager userManager
            = new UserManager();

    @Override
    public void initializeDatabaseManagement() {

    }

    @Override
    public void CreateTables() {
        throw new UnsupportedOperationException("Not yet a supported operation!"); 
    }

    /**
     * Returns a <code>UserManager</code> object for this
     * <code>DatabaseManagement</code>.
     *
     * @return A <code>UserManager</code>  object.
     * @see common.User 
     */
    @Override
    public database.UserManager getUserManager() {
        return userManager;
    }

 }
