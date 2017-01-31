
package servlets.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utilities.PropertyManager;

/**
 * This filter is used to check if the current session user is still logged
 * in.
 * @author Joseph Picataggio
 */
public class LoginFilter implements Filter {
    private FilterConfig filterConfig = null;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;   
        HttpServletResponse res = (HttpServletResponse) response;
        String file = req.getServletPath();
        HttpSession session = req.getSession(false);
        
        if (file.equals("/loginScreen.jsp") || 
                file.equals("/StudentLoginServlet") || 
                file.equals("/LoginServlet") || 
                file.equals("/ForgotPassword.jsp") ||
                file.equals("/ForgotPasswordServlet") ||
                file.equals("/ResetPasswordServlet"))
        {
            //log("LoginFilter.doFilter fired for: [" + file+"]");
            chain.doFilter(req, res);
        }
        else if (file.contains("Servlet"))// Servlet must be part of our file name --> ControlServlet
        {
            //session.setAttribute("user", null); // for testing
           // log("LoginFilter.doFilter fired for: [" + file+"]");
            if (session.getAttribute("user") == null) {
               // log("LoginFilter.TimeOut on " + file);
                String errorMessage = "Your session has timed out.";
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher(PropertyManager.getProperty("welcome-file")).forward(request, response);
                return;
            }
            chain.doFilter(req, res);
        }
        else
            chain.doFilter(req, res);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {        
        this.filterConfig = filterConfig;
        //log("LoginFilter: Initializing filter");
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
}
