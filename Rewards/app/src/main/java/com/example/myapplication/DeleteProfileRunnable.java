package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

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

public class DeleteProfileRunnable implements Runnable {

    private static final String TAG = "DeleteProfileAsyncTask";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/DeleteProfile";

    private final ProfileActivity profileActivity;

    private String username;
    private String apiKey;

    //
    // Don't forget in manifest:
    //          android:usesCleartextTraffic="true"
    //
    public DeleteProfileRunnable(ProfileActivity activity, String username, List<String> ap) {

        this.profileActivity = activity;
        this.username = username;
        this.apiKey = ap.get(0);

    }

    public void run() {
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
            profileActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        profileActivity.deleteProfileResult();
                }
            });
            return;
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

