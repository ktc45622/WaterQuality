/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BZTempTestStuff;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Tests methods to determine data types and set values in a PreparedStatement.
 * PreparedStatement not working because of connection issues.
 * @author Brian Zaiser
 */
public class TestObjects {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        ArrayList<Object> myList = new ArrayList<Object>();
        int one = 1;
        String aString = "some string";
        double twoPoint5 = 2.5;
        boolean aBoole = false;
        myList.add(one);
        myList.add(aString);
        myList.add(twoPoint5);
        myList.add(aBoole);
        printOut(myList);

        System.out.println("finished");
        
    }
    
    public static void printOut(Object object) {
        
        ArrayList list = (ArrayList) object;
        Iterator<Object> iterator = list.iterator();
        Integer int1 = 0;
        Double doub1 = 0.0;
        String string1 = "";
        Boolean boole1 = true;
        int count = 0;
        String sql = "SELECT * FROM My_Table WHERE "
                + "Manager = ? AND "
                + "Description = ? AND "
                + "Cash_Value = ? AND "
                + "Out_Of_Stock = ?";
        String url = "jdbc:mysql://hermes.buad.bloomu.edu:3306/SolarTracker";
        String user = "STSoftware";
        String password = "SoftwareEngineering2013";
        Connection conn;
        PreparedStatement ps;
//        try {
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
  //          conn = DriverManager.getConnection(url, user, password);
    //        ps = conn.prepareStatement(sql);
        /*} catch (SQLException ex) {
            ex.printStackTrace();
        }*/
        while (iterator.hasNext()) {
            ++count;
            Object obj = iterator.next();
            System.out.println("Object " + count + ": " + obj.toString());
            System.out.println("of type: " + obj.getClass());
      //      try {
            if (obj.getClass() == int1.getClass()) {
                int1 = (Integer)obj;
                System.out.println("count: " + count + " obj: " + int1);
                int realInt = int1.intValue();
                System.out.println("Value of int: " + realInt);
//                ps.setInt(count, int1);
            }
            else if (obj.getClass() == doub1.getClass()) {
                doub1 = (Double)obj;
                System.out.println("count: " + count + " obj: " + doub1);
                //ps.setDouble(count, doub1);
            }
            else if (obj.getClass() == string1.getClass()) {
                System.out.println("count: " + count + " obj: " + obj);
                //ps.setString(count, obj.toString());
            }
            else if (obj.getClass() == boole1.getClass()) {
                boole1 = (Boolean) obj;
                System.out.println("count: " + count + " obj: " + boole1);
                //ps.setBoolean(count, boole1);
            }
            }
        }
/*            catch (SQLException e) {
                e.printStackTrace();
            }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
        
//    }
}
