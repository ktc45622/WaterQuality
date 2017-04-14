
package servlets.filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.PropertyManager;

/**
 * This filter converts all HTTP requests to HTTPS requests.
 * @author cmr98507
 * @author Joseph Picataggio
 */
public class HttpsRedirectFilter implements Filter {

    private static final boolean debug = true;

    /* 
     * The filter configuration object we are associated with.  If
     * this value is null, this filter instance is not currently
     * configured. 
     */
    private FilterConfig filterConfig = null;

    /**
     * Handles HTTPS redirection
     */
    public HttpsRedirectFilter() {
    }

    /**
     * doBeforeProcessing
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @throws IOException
     * @throws ServletException 
     */
    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("HttpsRedirectFilter:DoBeforeProcessing");
        }
    }

    /**
     * doAfterProcessing
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @throws IOException
     * @throws ServletException 
     */
    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("HttpsRedirectFilter:DoAfterProcessing");
        }
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String getProtocol = req.getScheme();
        String getDomain = req.getServerName();
        String portNumber = Integer.toString(req.getServerPort());
        StringBuffer url = req.getRequestURL();

        if (getProtocol.toLowerCase().equals("http")) {

            // Set response content type
            response.setContentType("text/html");

            // New location to be redirected
            //    String httpsPath = PropertyManager.getProperty("BaseSSLURL")
            //            + uri;
            url.replace(0, 4, "https");
            int portStartIndex = url.indexOf(portNumber);
            url.replace(portStartIndex, portStartIndex+4,PropertyManager.getProperty("HTTPSPort"));
            String site = url.toString();
            //System.out.println("url is "+site );
            //    String site = new String(httpsPath);
            res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            res.setHeader("Location", site);
        }

        // Pass request back down the filter chain
        chain.doFilter(req, res);
    }

    /**
     * Return the filter configuration object for this filter.
     * @return Returns the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
            return (this.filterConfig);
        }

    /**
     * Set the filter configuration object for this filter.
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {        
    }

    /**
     * Initialize method for this filter
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {                
                log("HttpsRedirectFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
        public String toString() {
        if (filterConfig == null) {
            return ("HttpsRedirectFilter()");
        }
        StringBuffer sb = new StringBuffer("HttpsRedirectFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    /**
     * Sends a caught exception to the response OutputStream.
     * @param t error/exception to be handled
     * @param response The servlet response we are creating
     */    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);        
        
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);                
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                
                pw.print(stackTrace);                
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }
    
    /**
     * Returns the active stack frames.
     * @param t Throwable object
     * @return String of stack frames
     */
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
    
    /**
     * Logs a passed message.
     * @param msg the message to be logged.
     */
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
    
}
