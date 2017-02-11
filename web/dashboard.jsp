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
        <link rel="stylesheet" href="styles/dashboard.css" type="text/css">
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
                <li><a href="javascript:void(0)" class="tablinks" onclick="openTab(event, 'Graph'); hide();"
                       id="defaultOpen">Graph</a></li>
                <li><a href="javascript:void(0)" class="tablinks" onclick="openTab(event, 'Table'); hide();">
                        Table</a></li>
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
                <form id="data_type_form">
                    <div class="" id="select_all_toggle"><input type="checkbox" onclick="toggle(this);" 
                           id="select_all_data" value="select_all_data">Select all</div><br>
                    <input type="checkbox" onclick="if(current=='Graph')fullCheck('data1')" class="data" id="data1" value="data1">Data<br>
                    <input type="checkbox" onclick="if(current=='Graph')fullCheck('data2')" class="data" id="data2" value="data2">Data<br>
                    <input type="checkbox" onclick="if(current=='Graph')fullCheck('data3')" class="data" id="data3" value="data3">Data<br>
                    <input type="checkbox" onclick="if(current=='Graph')fullCheck('data4')" class="data" id="data4" value="data4">Data<br>
                    <input type="checkbox" onclick="if(current=='Graph')fullCheck('data5')" class="data" id="data5" value="data5">Data<br>
                    <input type="checkbox" onclick="if(current=='Graph')fullCheck('data6')" class="data" id="data6" value="data6">Data<br>
                    <input type="checkbox" onclick="if(current=='Graph')fullCheck('data7')" class="data" id="data7" value="data7">Data<br>
                    <input type="checkbox" onclick="if(current=='Graph')fullCheck('data8')" class="data" id="data8" value="data8">Data<br>
                    <br>
                    <div id="data_submit_holder"><input type="submit" id="data_type_submit"></div>
                </form>
            </aside><br> 
            
        </section> 
        
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
        
        <script type="text/javascript">
            document.getElementById("defaultOpen").click();
            var current;
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
                
                toggle(this);
                checkedBoxes=0;
                current=tabName;
            }
            
            function toggle(source) {
                var checkboxes = document.querySelectorAll('input[type="checkbox"]');
                for (var i = 0; i < checkboxes.length; i++) {
                    if (checkboxes[i] != source)
                        checkboxes[i].checked = source.checked;
                }
            }
            
            function hide(){
                var item=document.getElementById("select_all_toggle");
                if(item.className=='hide')
                    item.className='unhide';
                else
                    item.className='hide';
            }
            
            var checkedBoxes=0;
            function fullCheck(id){
                var item=document.getElementById(id);
                if(item.checked==true){
                    if(checkedBoxes<3)
                        checkedBoxes++;
                    else{
                        item.checked=false;
                    }
                }
                else
                    checkedBoxes--;
            }
        </script>
    </body>
</html>