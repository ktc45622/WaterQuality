<%@page import="java.util.stream.Collectors"%>
<%@page import="java.time.Period"%>
<%@page import="java.time.Instant"%>
<%@page import="async.DataReceiver"%>
<%@page import="java.util.List"%>
<%@page import="org.javatuples.Pair"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
    <head>
         <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="styles/dash2.css" type="text/css">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.min.js"></script>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <noscript>
            <meta http-equiv="refresh" content="0; URL=/html/javascriptDisabled.html">
        </noscript>
        <title>Dashboard</title>
    </head>
    <body>
        <img id="backPhoto" src="images/backgroundImage.JPG">
        <header class="title_bar_container"> 
            <div id="HeaderText">Water Quality</div>
        </header>
        <section class = "content_container1" id = "dashboard_container">
            <header class = "content_title_bar" id="login_header"> 
                <div class = "title" >
                    Dashboard
                </div> 
            </header>
            
            <section class = "content_container2" id = "graph_container">
            <header class = "content_title_bar" id="login_header"> 
                <div class = "title" >
                    Graph
                </div> 
            </header>     
            <ul class="tab">
                <li><a href="javascript:void(0)" class="tablinks" onclick="openTab(event, 'Graph')"
                       id="defaultOpen">Graph</a></li>
                <li><a href="javascript:void(0)" class="tablinks" onclick="openTab(event, 'Table')">Table</a></li>
            </ul>
                <div id="Graph" class="tabcontent">
                    <canvas id="myChart" width=25% height=20%></canvas>
                </div>
                <div id="Table" class="tabcontent" style="height:400px;overflow:auto;">
                </div>
            </section>
            
            <aside class = "content_container2" id = "dashboard_data_container">
            <header class = "content_title_bar" id="login_header"> 
                <div class = "title" >
                    Data Type
                </div> 
            </header> 
                <form>
                    <input type="checkbox" id="data" value="data">Data<br>
                    <input type="checkbox" id="data" value="data">Data<br>
                    <input type="checkbox" id="data" value="data">Data<br>
                    <input type="checkbox" id="data" value="data">Data<br>
                    <input type="checkbox" id="data" value="data">Data<br>
                    <input type="checkbox" id="data" value="data">Data<br>
                    <input type="checkbox" id="data" value="data">Data<br>
                    <input type="checkbox" id="data" value="data">Data<br>
                    <br>
                </form>
            </aside><br> 
            
        <%
        List<Pair<String, Double>> data = new ArrayList<>();
        DataReceiver.test(Instant.now().minus(Period.ofWeeks(4)), Instant.now())
                .blockingSubscribe(data::add);
        
        String timeStr = data
                .stream()
                .map(p -> "\"" +  p.getValue0() + "\"")
                .collect(Collectors.joining(","));
        String dataStr = data
                .stream()
                .map(p -> "" + p.getValue1())
                .collect(Collectors.joining(","));
        
        out.append("<script>" +
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
        
        out.append("<script>"
                + "var table = document.getElementById('Table').innerHTML = "
                + "\"<table border='1'><tr><th>Timestamp</th><th>Value</th></tr>");
        out.append(data
                .stream()
                .map(p -> "<tr><td>" + p.getValue0() + "</td><td>" + p.getValue1() + "</td></tr>")
                .collect(Collectors.joining())
        );
        out.append("</table>\"</script>");
        %>
        
            <script>
                var ctx = document.getElementById('myChart').getContext('2d');
                var myChart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: [timeStr],
                        datasets: [{
                            label: 'Current (~1 Month)',
                            data: [dataStr],
                            backgroundColor: 'transparent', borderColor: 'orange'
                        }]
                    }
                });
            </script>
        </section>   
                            
        <script>
            document.getElementById("defaultOpen").click();
            function openTab(evt, tabName) {
                var i, tabcontent, tablinks;
                tabcontent = document.getElementsByClassName("tabcontent");
                for (i = 0; i < tabcontent.length; i++) {
                    tabcontent[i].style.display = "none";
                }
                tablinks = document.getElementsByClassName("tablinks");
                for (i = 0; i < tablinks.length; i++) {
                    tablinks[i].className = tablinks[i].className.replace(" active", "");
                }
                document.getElementById(tabName).style.display = "block";
                evt.currentTarget.className += " active";
            }
        </script>
    </body>
</html>