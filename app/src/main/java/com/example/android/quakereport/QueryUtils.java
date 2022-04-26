package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list {@link EarthquakeEvent} objects
     */
    public static List<EarthquakeEvent> fetchEarthquakeEvents(String requestUrl){
/*        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        // Create URL object
        URL url = createURL(requestUrl);

        // Perform an HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing the input stream", e);
        }

        // Extract the earthquake events array
        List<EarthquakeEvent> earthquakes = extractEarthquakes(jsonResponse);

        return earthquakes;
    }
    /**
     * Return a list of {@link EarthquakeEvent} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<EarthquakeEvent> extractEarthquakes(String response) {

        if (TextUtils.isEmpty(response)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<EarthquakeEvent> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject rootObject = new JSONObject(response);
            JSONArray featuresArray = rootObject.optJSONArray("features");

            for (int i = 0; i < featuresArray.length(); i++) {
                JSONObject earthquakeObject = featuresArray.optJSONObject(i);
                JSONObject propertiesObject = earthquakeObject.optJSONObject("properties");

                double magnitude = propertiesObject.optDouble("mag");
                String place = propertiesObject.optString("place");
                long time = propertiesObject.optLong("time");
                String url = propertiesObject.optString("url");

                EarthquakeEvent currentEarthquake = new EarthquakeEvent(magnitude, place, time, url);
                earthquakes.add(currentEarthquake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

    private static URL createURL(String urlString) {
        URL url = null;

        //Returning null if the url string is empty or null
        if (urlString == null || urlString.isEmpty()) {
            return url;
        }

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.i(LOG_TAG, "Error creating URL");
        }

        return url;
    }

    /**
     * Make an HTTP request to the given URL and get a String response back
     */
    private static String makeHTTPRequest(URL url) throws IOException {
        String jsonResponse = "";

        //Checking if the URL is null
        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful, then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Earthquakes JSOn response");
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Converts the {@link InputStream} object into a String which contains the JSON response coming from the server
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder responseString = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                responseString.append(line);
                line = reader.readLine();
            }
        }
        return responseString.toString();
    }
}
