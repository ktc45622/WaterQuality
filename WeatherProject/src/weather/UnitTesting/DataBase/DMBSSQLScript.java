package weather.UnitTesting.DataBase;

/**
 *
 * @author Joseph Horro
 */
public interface DMBSSQLScript {

    /**
     * TODO: figure out what the parameter is for and document
     * Runs .sql scripts.
     * @param scriptsFile The sql script file
     */
    public void runScripts(String scriptsFile);
}
