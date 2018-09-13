package com.example.aditya.newsapp_stage_1;

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

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getName();
    private QueryUtils() {

    }

    private static List<NewsData> extractNewsFeedsFromJson(String newsFeedJSON) {

        if (TextUtils.isEmpty(newsFeedJSON)) {
            return null;
        }

        List<NewsData> newsData = new ArrayList<>();

        try {
            JSONObject rootJsonObject = new JSONObject(newsFeedJSON);
            JSONObject responseObject = rootJsonObject.getJSONObject("response");

            JSONArray resultArray = responseObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {
                String author = "";
                JSONObject currentNewsFeed = resultArray.getJSONObject(i);

                String title = currentNewsFeed.getString("webTitle");
                String sectionName = currentNewsFeed.getString("sectionName");
                String date = currentNewsFeed.getString("webPublicationDate");
                String thumbnail = currentNewsFeed.getJSONObject("fields").getString("thumbnail");
                String webUrl = currentNewsFeed.getString("webUrl");
                JSONArray tags = currentNewsFeed.getJSONArray("tags");
                if (tags.length() > 0) {
                    author = tags.getJSONObject(0).getString("webTitle");
                }
                NewsData data = new NewsData(thumbnail, author, title, date, webUrl, sectionName);
                newsData.add(data);
            }

        } catch (JSONException e) {
            Log.e(QueryUtils.class.getName(), "Problem parsing with guardian data" + e.getMessage());
        }
        return newsData;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
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

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results." + e.getMessage());
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

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<NewsData> fetchEarthquakeData(String requestUrl) {
        URL url = createUrl(requestUrl);
        Log.d(LOG_TAG, "URL " + url);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<NewsData> earthquakes = extractNewsFeedsFromJson(jsonResponse);
        return earthquakes;
    }
}