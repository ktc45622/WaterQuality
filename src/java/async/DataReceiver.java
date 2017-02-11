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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import org.javatuples.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author lpj11535
 */
public class DataReceiver {
    private static final String JSON_URL = "https://ienvironet.com/api/data/last/0A178632?auth_token=avfzf6dn7xgv48qnpdhqzvlkz5ke7184";
    
    private static Observable<JSONObject> getData(String url) {
        return Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map(URL::new)
                .map(URL::openConnection)
                .doOnNext(conn -> conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"))
                .doOnNext(URLConnection::connect)
                .map(URLConnection::getInputStream)
                .map(stream -> {
                    BufferedReader r  = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }
                    
                    return sb.toString();
                })
                .map(str -> (JSONObject) new JSONParser().parse(str));
    }
    
    public static Observable<Pair<String, Double>> test(Instant start, Instant end) {
        String url = "https://ienvironet.com/api/data/" + start.getEpochSecond() + ":" + end.getEpochSecond() + "/637737849.json?auth_token=avfzf6dn7xgv48qnpdhqzvlkz5ke7184";
        System.out.println("Getting data from " + url);
        return getData(url)
                .map((JSONObject obj) -> (JSONArray) obj.get("data"))
                .flatMap(Observable::fromIterable)
                .doOnNext(obj -> System.out.println("Data Received: " + obj))
                .map(obj -> Pair.with((String)((JSONObject) obj).get("timestamp"), (Double) ((JSONObject) obj).get("value")));
                
    }
    
    public static void init() {
        getData(JSON_URL).subscribe(System.out::println);
                
    }
}
