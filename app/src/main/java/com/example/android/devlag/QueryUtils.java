package com.example.android.devlag;

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

/**
 * Created by ANGA KOKO on 9/9/2017.
 */

public class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the GitHub dataset and return an {@link Profile} object to represent searched profiles.
     */
    public static ArrayList<Profile> fetchGitHubData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Create an empty ArrayList that we can start adding profiles to
        ArrayList<Profile> profiles = extractProfiles(jsonResponse);

        // Return the {@link Event}
        return profiles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Profile} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Profile> extractProfiles(String githubJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(githubJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding profiles to
        ArrayList<Profile> profiles = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of profiles objects with the corresponding data.
            JSONObject root = new JSONObject(githubJSON);
            JSONArray gitHubArray = root.getJSONArray("items");
            if(gitHubArray.length() > 0) {
                for (int i = 0; i < gitHubArray.length(); i++) {
                    JSONObject currentProfile = gitHubArray.getJSONObject(i);

                    // Extract the User Name from the key called "login"
                    String logInName = currentProfile.getString("login");

                    /**
                     * Check if Json has key "avatar_url"
                     *  Extract the Avatar Url from the key called "avatar_url"
                     * */
                    String avatar_url = "";
                    if(currentProfile.has("avatar_url")){
                        avatar_url = currentProfile.getString("avatar_url");
                    }

                    // Extract the HTML Url from the key called "html_url"
                    String html_url = currentProfile.getString("html_url");
                    Profile profile = new Profile(logInName, avatar_url, html_url);
                    profiles.add(profile);
                }
                // Return the list of profiles
                return profiles;
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the GITHUB JSON results", e);
        }

        // default return value
        return null;
    }
}
