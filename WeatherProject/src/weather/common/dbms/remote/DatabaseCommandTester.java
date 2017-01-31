
package weather.common.dbms.remote;

import weather.common.dbms.DBMSNoteManager;
import weather.common.dbms.remote.managers.NoteManager;

/**
 * Used to test the DatabaseCommandType class.
 * @author Brian Zaiser
 * Tested 2013-June-13: fromString and toString - successful
 * Tested 2013-June-13: getCommandType and getCommandName - successful
 */
public class DatabaseCommandTester {
    
    public static void main(String[] args) {
        
        /*Set up 2 Strings to test both creating a DatabaseCommandType from strings 
         * using its fromString method and printing using its toString method.
         */
        String type = "User";
        String method = "addUser";
        //test "implicit" object
        System.out.println("The command string is: " 
                + DatabaseCommandType.fromString(type, method));
        
        //test "explicit" object and other methods
        String type2, method2;
        DatabaseCommandType command;
        command = DatabaseCommandType.fromString("Note", "getNote");
        type2 = command.getCommandType();
        method2 = command.getCommandName();
        System.out.println("Command: " + type2 + ", " + method2);
        
        //test assignment not using fromString()
        DatabaseCommandType command2;
        command2 = DatabaseCommandType.BookmarkCategories_Add;
        System.out.println("Command: " + command2);
        
        //TODO: test getting the correct Manager; then, the correct method - with parameters
        if ("Note".equalsIgnoreCase(command.getCommandType())) {
            //Create a DBMSNoteManager
            DBMSNoteManager noteManager = new NoteManager();
            //Call the correct method
            if ("getNote".equalsIgnoreCase(command.getCommandName())) {
                noteManager.getNote(1);
            }
        }
        
        System.out.println("DatabaseCommandTester finished.");
        
    }

}
