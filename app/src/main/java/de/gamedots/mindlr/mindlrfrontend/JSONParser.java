package de.gamedots.mindlr.mindlrfrontend;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * JSONParser to make HTTP Requests and get JSON back
 */

public class JSONParser {

    String charset = "UTF-8";
    HttpURLConnection conn;
    DataOutputStream wr;
    StringBuilder result = new StringBuilder();
    URL urlObj;
    JSONObject jObj = null;
    StringBuilder sbParams;
    String paramsString;

    public JSONObject makeHttpRequest(String url, String method,
                                      HashMap<String, String> params) {
        createParameters(params);

        if (method.equals("POST")) {
            Log.d(LOG.CONNECTION, "About to make a POST request to an URL");
            // request method is POST
            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                try {
                    Log.d(LOG.CONNECTION, "Try to connect to the URL");
                    conn.connect();
                } catch(Throwable t){
                    Log.d(LOG.CONNECTION, "ERROR: Can't connect to server");
                }

                Log.d(LOG.CONNECTION, "Try to write parameters to server");
                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();
                Log.d(LOG.CONNECTION, "Writer closed");

            } catch (IOException e) {
                Log.d(LOG.CONNECTION, "IOException while trying to make POST request to server");
                e.printStackTrace();
            }
        }
        else if(method.equals("GET")){
            // request method is GET
            Log.d(LOG.CONNECTION, "About to make a GET request to an URL");

            if (sbParams.length() != 0) {
                Log.d(LOG.CONNECTION, "Append parameters to URL");
                url += "?" + sbParams.toString();
            }

            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setConnectTimeout(15000);

                try {
                    Log.d(LOG.CONNECTION, "Try to connect to the URL");
                    conn.connect();
                } catch(Throwable t){
                    Log.d(LOG.CONNECTION, "ERROR: Probably no Internet allowed");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(LOG.CONNECTION, "Method parameter neither equals POST nor GET. Return empty JSONObject.");
            return jObj;
        }

        try {
            Log.d(LOG.CONNECTION, "Try to receive response from server and parse into a String");
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d(LOG.CONNECTION, "JSONObject: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();
        Log.d(LOG.CONNECTION, "Successfully disconnected the connection.");

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.d(LOG.CONNECTION, "Error parsing data into JSONObject: " + e.toString());
        }

        // return JSON Object
        Log.d(LOG.CONNECTION, "Return the JSONObject (might be null)");
        return jObj;
    }

    public void createParameters(HashMap<String, String> params){
        Log.d(LOG.CONNECTION, "In method createParameters()");
        sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=").append(URLEncoder.encode(params.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                Log.d(LOG.CONNECTION, "Unsupported Encoding Exception");
                e.printStackTrace();
            }
            i++;
        }
        paramsString = sbParams.toString();
        Log.d(LOG.CONNECTION, "End of method createParameters()");
    }
}