/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package async;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utilities.FileUtils;

/**
 *
 * @author lpj11535
 */
public class DataReceiver {
    
    // TODO: Stop being a lazy idiot and just make a specific class or tuple to hold
    // data about each parameter.
    private static final Map<String, String> descriptionMap = new HashMap<>();
    
    private static final Map<String, Long> idMap = new HashMap<>();
    
    static {
        // The file 'descriptions.json' contains the descriptions for parameters,
        // which need to be displayed when selected. This is also used to filter
        // out any API data that we do not have a description of.
        String filename = "resources/descriptions.json";
        
        Observable
                .just(filename)
                .map(FileUtils::readAll)
                .map(str -> (JSONObject) new JSONParser().parse(str))
                .map(obj -> (JSONArray) obj.get("descriptions"))
                .flatMap(Observable::fromIterable)
                .blockingSubscribe(obj -> descriptionMap.put((String) ((JSONObject) obj).get("name"), ((String) ((JSONObject) obj).get("description")).replaceAll("\\P{Print}", "")));
    }
    
    static {
        // Fill the map so we only need to obtain it once on startup.
        // The map will contain a list of identifiers (being the very same
        // identifier displayed in dashboard.jsp) to their actual identifier. This
        // MUST be refactored before actual release, but should be sufficient for now.
        
        // This is the URL used to obtain ALL data for the sensor within the past
        // 15 minutes. This is important as, again, it holds ALL of the data. So
        // we can use this to construct our map.
        String url = "https://ienvironet.com/api/data/last/0A178632?auth_token=avfzf6dn7xgv48qnpdhqzvlkz5ke7184";
        
        // We want to make some API calls next. Given the above URL, we can then
        // make a connection and obtain the JSON data sent.
        getData(url)
                // For an example of the format given see: https://gist.github.com/LouisJenkinsCS/cca0069178f194329d55aabf33c28418
                // We need to obtain the "data" parameter, which a JSONArray.
                .map((JSONObject obj) -> (JSONArray) obj.get("data"))
                // A JSONArray implements the Iterable interface, just like any Collection such as an ArrayList would.
                // Because of this we can go from the Collection itself to the items it contains. Hence, a collection
                // containing ({1, 2, 3}) will be converted into the respective elements, (1, 2, 3).
                .flatMap(Observable::fromIterable)
                .filter(obj -> descriptionMap.containsKey(((JSONObject) obj).get("name")))
                // With the JSONObjects being the data we need, we can easily parse it into tuples
                // of (Name, Id) pairs, or rather Pair<String, Long>
                .map(obj -> Pair.with(((JSONObject) obj).get("name") + " (" + ((JSONObject)obj).get("unit") + ")", (Long) ((JSONObject)obj).get("id")))
                .doOnNext(System.out::println)
                .subscribe(pair -> idMap.put(((Pair<String, Long>) pair).getValue0(), ((Pair<String, Long>)pair).getValue1()));
    };
    
    /**
     * Obtain all sensor parameter names.
     * @return All sensor parameter names.
     */
    public static Observable<String> getParameters() {
        return Observable.fromIterable(idMap.keySet());
    }
    
    /**
     * Generates the chart, data table, and description for a given sensor parameter. 
     * Currently, it will obtain all data over a month's period of time. 
     * Note: The sensor MUST exist, as it does not (currently) perform any checks. 
     * @param key Sensor parameter
     * @return Tuple of (Chart, Table, Description) HTML as String.
     */
    public static Triplet<String, String, String> generateGraph(String key) {
        long id = idMap.get(key);
        List<Pair<String, Double>> data = new ArrayList<>();
        String url = "https://ienvironet.com/api/data/" + Instant.now().minus(Period.ofWeeks(4)).getEpochSecond() 
                + ":" + Instant.now().getEpochSecond() + "/" + id + ".json?auth_token=avfzf6dn7xgv48qnpdhqzvlkz5ke7184";
        
        // Obtain the JSON of the data sent. 
        getData(url)
                // See above for why
                .map((JSONObject obj) -> (JSONArray) obj.get("data"))
                .flatMap(Observable::fromIterable)
                // We take both the timestamp (X-Axis) and the value (Y-Axis).
                // Note that the data sent is already sorted, so there is no need
                // to do so ourselves.
                .map(obj -> Pair.with((String)((JSONObject) obj).get("timestamp"), (Double) ((JSONObject) obj).get("value")))
                // This ensures that the observable's computations are performed immediately.
                // Observables are 'lazy' by nature and are evaluated on need; this enforces
                // that need. In the future, we will do this with the help of an AsyncContext
                // and will be non-blocking.
                .blockingSubscribe(pair -> data.add((Pair<String, Double>) pair));
                
        // Maps each timestamp and value as a string that can be used in HTML.
        String timeStr = data
                .stream()
                .map(p -> "\"" +  p.getValue0() + "\"")
                .collect(Collectors.joining(","));
        String dataStr = data
                .stream()
                .map(p -> "" + p.getValue1())
                .collect(Collectors.joining(","));
        
        // Create the Chart.js needed code
        String chart = 
            "<script>" +
            "var ctx = document.getElementById('myChart').getContext('2d');\n" + 
            "var myChart = new Chart(ctx, {\n" +
             "  type: 'line',\n" +
             "  data: {\n" +
             "    labels: [" + timeStr + "],\n" +
             "    datasets: [{\n" +
             "      label: '" + key + "',\n" +
             "      data: [" + dataStr + "],\n" +
             "      backgroundColor: 'transparent', borderColor: 'orange'\n" +
             "    }]\n" +
             "  }\n" +
             "});" + 
             "</script>";
        
        // Create the data table.
        String table = "<script>"
                + "var table = document.getElementById('Table').innerHTML = "
                + "\"<table border='1'><tr><th>Timestamp</th><th>Value</th></tr>";
        table += data
                .stream()
                .map(p -> "<tr><td>" + p.getValue0() + "</td><td>" + p.getValue1() + "</td></tr>")
                .collect(Collectors.joining());
        table += "</table>\"</script>";
        
        return Triplet.with(chart, table, descriptionMap.get(key.substring(0, key.indexOf("(") - 1)));
    }
    
    /**
     * Obtain the JSON from the passed URL. The URL SHOULD be one that corresponds to
     * the environet API. The data is taken as JSON.
     * @param url
     * @return 
     */
    public static Observable<JSONObject> getData(String url) {
        return Observable.just(url)
                // From the URL, construct an actual URL (remember, the above is a String)
                .map(URL::new)
                // From the actual URL, form a connection with the endpoint (URL -> URLConnection)
                .map(URL::openConnection)
                // Need to set this or else API will refuse our request. We need to act like a web browser
                .doOnNext(conn -> conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"))
                // Attempt to form actual connection
                .doOnNext(URLConnection::connect)
                // Obtain the input stream, of which we can obtain the data it is sending
                .map(URLConnection::getInputStream)
                // Obtain the data from the Stream as a String
                .map(stream -> {
                    BufferedReader r  = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }
                    
                    return sb.toString();
                })
                // Remove any and all unicode characters
                .map(str -> str.replaceAll("\\P{Print}", ""))
                // From that received string, since it is in JSON, we can parse it into a JSONObject.
                .map(str -> (JSONObject) new JSONParser().parse(str));
    }
}
