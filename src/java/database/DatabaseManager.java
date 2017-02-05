/*
 * Includes various database managing 
 */
package waterquality;

import java.io.IOException;
import beans.DataValue;
import database.Web_MYSQL_Helper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Tyler Mutzek
 */
@WebServlet(name = "DatabaseManager", urlPatterns = {"/DatabaseManager"})
public class DatabaseManager extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        Web_MYSQL_Helper sql = new Web_MYSQL_Helper();
        HttpSession session = request.getSession();
        String url = "/index.html";
        try
        {
            Connection conn = sql.getConnection();
            String action = request.getParameter("action");
            
            if (action == null) 
            {
                action = "Menu";
            }
            else if(action == "ManualInput")
            {
                String insertSQL = "INSERT INTO WaterData vales(?,?,?,?,?,?)";
                int entryID;// = get next auto increment number
                String name = request.getParameter("name");
                String units = request.getParameter("units");
                String sensor = "Manual Entry";
                LocalDateTime time; // = request.getParameter()
                float value = request.getParameter("value");
                
                PreparedStatement p = conn.prepareStatement(insertSQL);
                p.setString(1, entryID);
                p.setString(2, name);
                p.setString(3, units);
                p.setString(4, sensor);
                p.setString(5, time);
                p.setString(6, value);
                p.executeUpdate();
                p.close();
                
                url = "/JSPs/ManualEntry2.jsp";
            }
            else if(action == "DisplayGraph")
            {
                String query = "Select * from WaterData Where name = ?"
                        + " AND time >= ? AND time <= ? AND sensor = ?";
                PreparedStatement p = conn.prepareStatement(query);
                p.setString(1, request.getParameter("name"));
                p.setString(2, request.getParameter("lowerTime"));
                p.setString(3, request.getParameter("upperTime"));
                p.setString(4, request.getParameter("sensor"));
                ResultSet rs = p.executeQuery();
                
                ArrayList<DataValue> graphData = new ArrayList<>();
                int entryID;
                String name;
                String units;
                String sensor;
                LocalDateTime time;
                float value;
                while(!rs.isAfterLast())
                {
                    entryID = rs.getInt(1);
                    name = rs.getString(2);
                    units = rs.getString(3);
                    sensor = rs.getString(4);
                    //time = rs.getTime(5);
                    value = rs.getFloat(6);
                    DataValue dV = new DataValue(entryID,name,units,sensor,time,value);
                    graphData.add(dV);
                    
                    rs.next();
                }
                rs.close();
                p.close();
                
                session.setAttribute("GraphData", graphData);
            }
            
        }
        catch (Exception ex)//SQLException ex 
        {
            System.out.println("Error processing request: " + request.getParameter("action"));
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        processRequest(request, response);
    }
}
