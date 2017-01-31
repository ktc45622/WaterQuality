/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.common.dbms.remote;

import java.util.ArrayList;

/**
 * Used to test the methods and construction of a 
 * <code>RemoteDatabaseCommand</code>.
 * @author Brian Zaiser
 * 2013-June-14: began code
 * 2013-June-17: tested code
 */
public class RemoteDatabaseCommandTester {

    /**
     * Tests the methods and construction of a RemoteDatabaseCommand object.
     * @param args the command line arguments - none
     */
    public static void main(String[] args) {
        //code application logic here
        RemoteDatabaseCommand rdc1;
        ArrayList<Object> al1 = new ArrayList<>();
        al1.add(1);
        al1.add("This is a test.");
        
        //Tests constructor
        rdc1 = new RemoteDatabaseCommand(DatabaseCommandType.User_AddUser, al1);
        
        //Test toString()
        System.out.println("rdc1 = " + rdc1);
        
        //Test getDatabaseCommand
        DatabaseCommandType dc1 = rdc1.getDatabaseCommand();
        
        //Test getArguments
        ArrayList<Object> al2 = rdc1.getArguments();
        
        //Print same
        System.out.println("Command: " + dc1);
        System.out.println("Arguments: " + al2);
        
        //Test setDatabaseCommand
        rdc1.setDatabaseCommand(DatabaseCommandType.BookmarkCategories_ObtainAll);
        
        //Test setArguments
        ArrayList<Object> al3 = new ArrayList<>();
        al3.add("This is another test.");
        al3.add(true);
        al3.add(25.63);
        rdc1.setArguments(al3);
        
        //Print
        System.out.println("Arguments: " + rdc1.getArguments());
        System.out.println("DatabaseCommand: " + rdc1.getDatabaseCommand());
        
        //end
        System.out.println("\nRemoteDatabaseCommandTester finished.");
    }
}
