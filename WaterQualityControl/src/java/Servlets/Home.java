package Servlets;

/* BSD 3-Clause License
 *
 * Copyright (c) 2017, Louis Jenkins <LouisJenkinsCS@hotmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     - Neither the name of Louis Jenkins, Bloomsburg University nor the names of its 
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import Misc.GraphData;
import Providers.StubbedDataProvider;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Louis Jenkins
 */
@WebServlet(urlPatterns = {"/Home"})
public class Home extends HttpServlet {
    
    private List<Integer> generateRandomData(int n) {
        Random rand = new Random();
        return rand
                .ints(n)
                .map(i -> Math.abs((i % 100)) + 1)
                .boxed()
                .collect(Collectors.toList());
    }
    
    private List<String> generateTimes(int n) {
        int num_hours = n / 4;
        int current = 1;
        
        List<String> list = new ArrayList<>();
        for (int i = 0; i < num_hours; i++) {
            list.add("" + current + ":" + "00PM");
            list.add("" + current + ":" + "15PM");
            list.add("" + current + ":" + "30PM");
            list.add("" + current + ":" + "45PM");
        }
        
        return list;
    }

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
        
        List<GraphData> data = new StubbedDataProvider().get();
        String timeStr = data
                .stream()
                .map(gd -> "\"" + DateTimeFormatter.ISO_LOCAL_TIME.format(ZonedDateTime.ofInstant(gd.timestamp, ZoneId.systemDefault())) + "\"")
                .collect(Collectors.joining(","));
        String dataStr = data
                .stream()
                .map(gd -> "" + gd.value)
                .collect(Collectors.joining(","));
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.min.js\"></script>");
            out.println("<title>Servlet Home</title>");    
            out.println("</head>");
            out.println("<body>");
            out.println("<h1><center>Generated Data Test</center></h1>");
            out.println("<canvas id=\"myChart\"></canvas>");
            out.println("<script>" +
                    "var ctx = document.getElementById('myChart').getContext('2d');\n" +
                    "var myChart = new Chart(ctx, {\n" +
                    "  type: 'line',\n" +
                    "  data: {\n" +
                    "    labels: [" + timeStr + "],\n" +
                    "    datasets: [{\n" +
                    "      label: 'Generated Data',\n" +
                    "      data: [" + dataStr + "],\n" +
                    "      backgroundColor: 'transparent', borderColor: 'orange'\n" +
                    "    }]\n" +
                    "  }\n" +
                    "});" + 
                    "</script>"
            );
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
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
