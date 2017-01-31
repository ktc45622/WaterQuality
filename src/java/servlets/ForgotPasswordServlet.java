package servlets;

import common.User;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utilities.PropertyManager;

/**
 * <code>ForgotPasswordServlet</code> processes forgotten password requests
 * from <code>ForgotPassword.jsp</code>. After checking the validity of user 
 * credentials, this servlet generates an email that is sent to the user that
 * will direct them to a page to change their password.
 * 
 * @author tll92617
 */

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/ForgotPasswordServlet"})
public class ForgotPasswordServlet extends HttpServlet {

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
            throws ServletException, IOException {
        
        String email = request.getParameter("emailAddress");
        if(email == null) {
            request.setAttribute("message", "Invalid Email.");
            
            getServletContext()
                    .getRequestDispatcher("/ForgotPassword.jsp")
                    .forward(request, response);
            return;
        }
        database.UserManager um = database.Database.getDatabaseManagement().getUserManager();
        Collection<common.User> userList = um.getAllUsersWithEmailAddress(email.trim());
        
        HttpSession session = request.getSession();
        
        if(userList != null && userList.size() > 0)  {
            User user = null;
            Iterator<User> it = userList.iterator();
            String message = "";
            while(it.hasNext()){
                user = it.next();

                if(user.isLocked()) {
                    message += "Login for '" + user.getLoginName() 
                    + "' has been locked because of to many login attempts, Contact System admin to unlock this account.\r\n\r\n";
                    continue;
                }
                
                /*
                 *  Generates a salt that is attatched to a redirect link to the
                 *  ControlServlet along with the users ID number.
                 */ 
                String token = security.Encryption.generateSalt();

                String hashTok = security.Encryption.hashString(token);
                user.setUserPassword(hashTok);
                user.setSalt(token);
                um.updateUser(user); // Sets new password and salt for this user

                //create email message
                message += "Please Navigate to the following link to reset your password for '" + user.getLoginName() + "'.\r\n\r\n";

                message += PropertyManager.getProperty("BaseURL") + "SampleWebApplication/ControlServlet?control=resetpassword;" 
                        + "token="+hashTok +"; UID"+ user.getUserNumber();
                
                message += "\r\n\r\n";                
            }
            message += "If you did not request a password reset, please notify your system administrator.\r\n\r\n";
            message += "Please do not reply to this email.";
            message += " This is an automatic generated email and you will not get a response.";
            
            //send email and redirect to login screen.
            utilities.EmailUtility.email(user.getEmailAddress(), message, "Password Reset");
            
            request.setAttribute("errorMessage", "Forgot Password Email Sent.");
            getServletContext()
                    .getRequestDispatcher("/loginScreen.jsp")
                    .forward(request, response);
        }
        else {
            request.setAttribute("message", "Invalid Email.");
            
            getServletContext()
                    .getRequestDispatcher("/ForgotPassword.jsp")
                    .forward(request, response);
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
