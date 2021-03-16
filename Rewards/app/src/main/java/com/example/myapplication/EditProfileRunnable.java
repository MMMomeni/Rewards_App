package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditProfileRunnable implements Runnable {

    private static final String TAG = "EditProfileAsyncTask";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/UpdateProfile";

    private final EditProfileActivity editActivity;

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String pointsToAward;
    private final String department;
    private String story;
    private Bitmap image;
    private String imageBase64;
    private String position;
    private String location;
    private String apiKey;
    private Profile profile;

    //
    // Don't forget in manifest:
    //          android:usesCleartextTraffic="true"
    //
    public EditProfileRunnable(EditProfileActivity activity, Profile pro, List<String> list) {

        this.editActivity = activity;
        this.apiKey = list.get(0);

        username = pro.getUserName();
        password = pro.getPassword();
        firstName = pro.getFirstName();
        lastName = pro.getLastName();
        pointsToAward = "1000";
        department = pro.getDepartment();
        story = pro.getStory();
        imageBase64 = pro.getImageBytes();
        position = pro.getPosition();
        location = pro.getLocation();
    }

    public void run() {

        // Here I delete he user since this is a sample.
        // You do NOT need to do this.

        //deleteUser();

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            Log.d(TAG, "run: Initial URL: " + urlString);

            buildURL.appendQueryParameter("firstName", firstName);
            buildURL.appendQueryParameter("lastName", lastName);
            buildURL.appendQueryParameter("userName", username);
            buildURL.appendQueryParameter("department", department);
            buildURL.appendQueryParameter("story", story);
            buildURL.appendQueryParameter("position", position);
            buildURL.appendQueryParameter("password", password);
            buildURL.appendQueryParameter("remainingPointsToAward", pointsToAward);
            buildURL.appendQueryParameter("location", location);
            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageBase64);
            out.close();

            final StringBuilder sb = new StringBuilder();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }

            } else {
                //reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
            }


            profile = threadHandler(sb.toString());



            Log.d(TAG, "run: " + sb.toString());
            editActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (profile != null)
                        editActivity.editThreadHandler(profile);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "doInBackground: Invalid URL: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }

    }

    private Profile threadHandler(String sb){

        try {
            JSONObject data = new JSONObject(sb);

            Profile result = new Profile(data.getString("firstName"), data.getString("lastName"),
                    data.getString("userName"), data.getString("department"),
                    data.getString("story"), data.getString("position"),
                    data.getString("password"),data.getString("remainingPointsToAward"),
                    data.getString("location"), data.getString("imageBytes"), null, null);

            return  result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    //////////////////////////////////////////////////////
    // This deleteUser method is only for sample purposes
    //////////////////////////////////////////////////////
    private void deleteUser() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + "Profile/DeleteProfile";
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            buildURL.appendQueryParameter("userName", username);
            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            Log.d(TAG, "deleteUser: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            final StringBuilder sb = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                Log.d(TAG, "deleteUser: " + sb.toString());

            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                Log.d(TAG, "deleteUser: " + sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "doInBackground: Invalid URL: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "deleteUser: Error closing stream: " + e.getMessage());
                }
            }
        }
    }
}
