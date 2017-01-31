package weather.common.dbms;

// @TODO Remove this file after planned MySQLEventManager update. JH 11-4-2

/**
 * Represents an argument to a SQL query and its type.
 *
 * @author Eric Subach (2010)
 */
public class DBMSArg {
    // The argument.
    Object arg;
    // The type of the argument.
    DBMSArgType type;
    // For the few PreparedStatement methods that require an extra argument.
    long arg2;
    boolean argExtra;


    public DBMSArg (Object arg, DBMSArgType type) {
        this.arg = arg;
        this.type = type;
        argExtra = false;
    }


    public DBMSArg (Object arg, DBMSArgType type, long arg2) {
        this.arg = arg;
        this.type = type;
        this.arg2 = arg2;
        argExtra = true;
    }


    public Object getArg () {
        return (arg);
    }


    public DBMSArgType getType () {
        return (type);
    }
}
