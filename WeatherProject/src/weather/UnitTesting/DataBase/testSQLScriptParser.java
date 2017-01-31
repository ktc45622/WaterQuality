package weather.UnitTesting.DataBase;

import weather.common.dbms.mysql.MySQLImpl;

/**
 * This is a test script parser for using .sql files with java.
 * @author Joe Horro
 */
public class testSQLScriptParser {

    public static void main(String[] args) {

        // To test, run this file with a script name
        // NOTE there is no file cannot be found check - testing :)
        try{
            // now the database part
            MySQLImpl dbms =  MySQLImpl.getMySQLDMBSSystem();
         //   dbms.getMySQLScriptManager().runScripts("scripts.txt");
        }catch(Exception e){
            System.out.println("Something broke - possibly file not found.");
        }

//        try {
//            BufferedReader br = new BufferedReader(new FileReader("sqlScripts\\weblinks.sql"));
//            ArrayList<String> al = new ArrayList<String>();
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            String fileContents;
//            String sqlCommands[];
//
//            line = br.readLine();
//            while (line != null) {
//                sb.append(line);
//                line = br.readLine();
//            }
//            fileContents = sb.toString();
//            System.out.println("SQL -> " + fileContents);
//            sqlCommands = sb.toString().split(";");
//
//            // now the database part
//            MySQLImpl dbms = new MySQLImpl();
//
//
//            for (String s : sqlCommands) {
//                System.out.println("Executing -> " + s + ";");
//                dbms.getWebLinkManager().runScript(s + ";", dbms);
//            }
//
//        } catch (Exception e) {
//            // Terrible error catching, only used in unit testing.
//            Debug.println("Error in Bookmark unit test.");
//            Debug.println(e.toString());
//        } finally {
//        }

    }
}
