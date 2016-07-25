package de.gamedots.mindlr.mindlrfrontend.util;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import de.gamedots.mindlr.mindlrfrontend.logging.LOG;

/**
 * Created by Max Wiechmann on 08.11.15.
 */
public class ServerComUtil {

    public static JSONObject getMetaDataJSON() {
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("BRAND", android.os.Build.BRAND);
            parameter.put("MODEL", android.os.Build.MODEL);
            parameter.put("PRODUCT", Build.PRODUCT);
            parameter.put("SDK", "" + Build.VERSION.SDK_INT);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return parameter;
    }

    public static JSONObject httpPostRequest(String url, JSONObject parameter) {
        StringBuilder result = null;
        int status = -1;
        HttpURLConnection connection = null;
        try {
            //Setup connection
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);

            //Setup output writer
            OutputStreamWriter writer;
            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(parameter.toString());
            writer.close();

            //Send request (not needed)
            connection.connect();

            //Read response
            Log.d(LOG.JSON, "Response Header:");
            for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                Log.d(LOG.JSON, header.getKey() + "=" + header.getValue());
            }

            status = connection.getResponseCode();
            Log.d(LOG.CONNECTION, "Status Code: " + status);

            InputStream input;
            if (status == 200 || status == 201) {
                input = connection.getInputStream();
            } else if (status >= 400 && status < 600) {
                input = connection.getErrorStream();
            } else {
                throw new IOException("Status Code not supported, don't know what to do. Status " + status);
            }
            //Read content (either error or input stream, both are JSON)
            try {
                if (input != null) {
                    result = new StringBuilder();
                    Log.d(LOG.CONNECTION, "Try to receive response from server and parse into a String");
                    InputStream in = new BufferedInputStream(input);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d(LOG.CONNECTION, "JSONObject: " + result.toString());
                }
            } catch (IOException e) {
                Log.e(LOG.CONNECTION, "Error reading response");
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d(LOG.CONNECTION, "IOException while trying to make POST request to server");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
                Log.d(LOG.CONNECTION, "Successfully disconnected the connection.");
            }
        }
        if(result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                boolean hasStatus = jsonObject.has("STATUS");
                if(!hasStatus && status != -1){
                    jsonObject.put("STATUS", status);
                }
                Log.d(LOG.JSON, "JSONObject Response: " + jsonObject.toString());
                return jsonObject;
            } catch (JSONException e) {
                Log.d(LOG.JSON, "Error parsing data into JSONObject: " + e.toString());
            }
        }
        return null;
    }
}
