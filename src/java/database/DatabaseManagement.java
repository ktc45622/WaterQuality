
package database;

/**
 * Represents a <code>interface</code> of <code>DatabaseManagement</code> to manipulates operations on 
 * tables, courses, students, wait lists, users.
 * 
 * @author cjones
 */
public interface DatabaseManagement {
    
    public void CreateTables();
    public void initializeDatabaseManagement();
    public UserManager getUserManager();

}
