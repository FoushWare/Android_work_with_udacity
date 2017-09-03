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

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {



    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Earthquake> extractFeatureFromJson(String earthquakeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < earthquakeArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that earthquake.
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // Extract the value for the key called "mag"
                double magnitude = properties.getDouble("mag");

                // Extract the value for the key called "place"
                String location = properties.getString("place");

                // Extract the value for the key called "time"
                long time = properties.getLong("time");

                // Extract the value for the key called "url"
                String url = properties.getString("url");

                // Create a new {@link Earthquake} object with the magnitude, location, time,
                // and url from the JSON response.
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);

                // Add the new {@link Earthquake} to the list of earthquakes.
                earthquakes.add(earthquake);
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


    /**
     * Query the USGS dataset and return a list of earthquakes
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) throws IOException {
        Log.i(LOG_TAG,"Hello mirna here we will will fetch the data of earthquakes using the specified URL i give it to fetchEarthquakeData Method");

        //make the thread sleep 2 seconds so we're simulating that the response take much time to show our spinner in the screen
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //create url object
        URL url= createUrl(requestUrl);
        //make Http request to get data from the server and receive a json response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Earthquake> Result=extractFeatureFromJson(jsonResponse);
        return Result;


    }

    /**createUrl (Method) => return the url from the given string */
    private static URL createUrl(String stringUrl){
        URL url=null;
        try {
            url =new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;

    }

    /**Make Http request to a given url and return a string as a response */
    private static String makeHttpRequest(URL url) throws IOException {
        String JsonResponse="";
        if(url==null){
            return JsonResponse;
        }
        //to make the request we need a class called HttpURLConnection
        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        //open the connection between me and the url i will request
        try {
            urlConnection=(HttpURLConnection)url.openConnection();
            /**Start of  the configuration of the HttpUrlConnection class */
            //set the read time out
            urlConnection.setReadTimeout(10000/*Millisecond*/);

            //set the connect time out
            urlConnection.setConnectTimeout(15000/*Millisecond*/);
            //set the connect method with the server
            urlConnection.setRequestMethod("GET");
            /**End of  the configuration of the HttpUrlConnection class */
            //then we will connect
            urlConnection.connect();

            /**Start the Response part */
            //if the response is successful (response=200) then we will read from the input stream and convert it to what dataType we want
            if(urlConnection.getResponseCode()==200){
                inputStream=urlConnection.getInputStream();
                JsonResponse= readFromStream(inputStream);
            }
            //if not a good response we will print a log message
            else{
                Log.e(LOG_TAG,"the Response code :"+urlConnection.getResponseCode());
            }
            /**End the Response part */
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return JsonResponse;





    }

/**convert the inputStream to string which contain the whole json response from the server*/
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output=new StringBuilder();
        if(inputStream !=null){
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        String line=bufferedReader.readLine();
        while(line!=null){
            output.append(line);
            line = bufferedReader.readLine();
        }
        }

        return output.toString();
    }

}