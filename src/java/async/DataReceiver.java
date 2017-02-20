/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package async;

import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.javatuples.Pair;
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
    
    public static Data getData(Instant start, Instant end, String ...keys) {
        // Since we may potentially have many keys, we need to obtain
        // the data for each of them. As 
        return new Data(Observable
                // For each key
                .fromArray(keys)
                // A Unit is a tuple consisting of a single element. So we wrap the key in a Unit, or key -> (key)
                .flatMap((String key) ->
                    getData(getParameterURL(start, end, idMap.get(key)))
                            // See above for why
                            .map((JSONObject obj) -> (JSONArray) obj.get("data"))
                            .flatMap(Observable::fromIterable)
                            // We take both the timestamp (X-Axis) and the value (Y-Axis).
                            // Note that the data sent is already sorted, so there is no need
                            // to do so ourselves.
                            .map(obj -> new DataValue(key, (String)((JSONObject) obj).get("timestamp"), (Double) ((JSONObject) obj).get("value")))
                ).replay());
    }
    
    public static String generateDescriptions(Data source) {
        StringBuilder descriptions = new StringBuilder();
        source.getData()
                .map(DataValue::getParameter)
                .distinct()
                .map((String parameter) -> "\n<center><h1>" + parameter + "</center></h1>\n" + descriptionMap.get(parameter.substring(0, parameter.indexOf("(") - 1)))
                .blockingSubscribe(descriptions::append);
        
        return descriptions.toString();
    }
    
    public static String generateChartJS(Data source) {
        StringBuilder chartJS =  new StringBuilder("<script>" +
                "var ctx = document.getElementById('myChart').getContext('2d');\n" + 
                "var myChart = new Chart(ctx, {\n" +
                 "  type: 'line',\n" +
                 "  data: {\n");
        
        StringBuilder labels = new StringBuilder("    labels: [");
        source.getData()
                .map(DataValue::getTimestamp)
                .distinct()
                .sorted()
                .map((Instant ts) -> "\"" + ts.toString().replace("T", " ").replace("Z", "") + "\",")
                .blockingSubscribe(labels::append);
        
        // Replace the last ',' for a end bracket ']'
        labels.replace(labels.length()-1, labels.length(), "]");
        chartJS.append(labels.toString());
        chartJS.append(",\n    datasets: [");
        
        // Add all data to the dataset for each element
        source.getData()
                .groupBy((DataValue dv) -> dv.parameter)
                .sorted((GroupedObservable<String, DataValue> group1, GroupedObservable<String, DataValue> group2) -> 
                        group1.getKey().compareTo(group2.getKey())
                )
                .flatMap((GroupedObservable<String, DataValue> group) -> {
                    int rgb[] = new Random().ints(0, 256).limit(3).toArray();
                    return group
                            .buffer(Integer.MAX_VALUE)
                            .map((List<DataValue> data) -> "{\n" +
                                    "      label: '" + group.getKey() + "',\n" +
                                    "      data: [" + data.stream().map(DataValue::getValue).map(Object::toString).collect(Collectors.joining(",")) + "],\n" +
                                    "      backgroundColor: 'transparent', borderColor: 'rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")'\n" +
                                    "},"
                            );
                })
                .blockingSubscribe(chartJS::append);
        
        // Replace the last ',' with a ']' and finish off chartJS
        chartJS.replace(labels.length()-1, labels.length(), "]");
        chartJS.append("]\n  }\n});</script>");
        
        return chartJS.toString();
    }
    
    public static String generateTable(Data source) {
        StringBuilder table = new StringBuilder("<table border='1'>\n\t<tr>\n\t\t<th>Timestamp</th>");
        
        // Generate the headers first
        source.getData()
                .map(DataValue::getParameter)
                .distinct()
                .sorted()
                .map((String parameter) -> "\n\t\t<th>\n\t\t" + parameter + "\n\t\t</th>")
                .blockingSubscribe(table::append);
        
        // Generate the body next
        source.getData()
                .groupBy(DataValue::getTimestamp)
                .sorted((GroupedObservable<Instant, DataValue> group1, GroupedObservable<Instant, DataValue> group2) -> 
                        group1.getKey().compareTo(group2.getKey())
                )
                .flatMap((GroupedObservable<Instant, DataValue> group) -> {
                    return Observable
                            .just("\n\t<tr>\n\t\t<td>" + group.getKey().toString().replace("T", " ").replace("Z", "") + "</td>")
                            .flatMap(str -> group.map((DataValue dv) -> "\n\t\t<td>" + dv.getValue() + "</td>")
                                    .buffer(Integer.MAX_VALUE)
                                    .map(list -> str + list
                                            .stream()
                                            .collect(Collectors.joining())
                                    )
                            )
                            .map(str -> str + "\n\t</tr>");
                }).blockingSubscribe(table::append);
        table.append("</table>");
        
        return table.toString();
    }
    
    private static String getParameterURL(Instant start, Instant end, long id) {
       return "https://ienvironet.com/api/data/" + start.getEpochSecond() + ":" + end.getEpochSecond() + "/" + id + ".json?auth_token=avfzf6dn7xgv48qnpdhqzvlkz5ke7184";
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
