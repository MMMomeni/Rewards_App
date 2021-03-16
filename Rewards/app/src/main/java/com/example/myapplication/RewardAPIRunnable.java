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

public class RewardAPIRunnable implements Runnable {

    private static final String TAG = "RewardAPITask";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Rewards/AddRewardRecord";

    private final RewardActivity rewardActivity;

    private String receiver_username;
    private String giver_username;
    private String giver_fullName;
    private String amount;
    private String note;

    private String apiKey;
    private Profile profile;

    //
    // Don't forget in manifest:
    //          android:usesCleartextTraffic="true"
    //
    public RewardAPIRunnable(RewardActivity activity, List<String> infoList, List<String> loginInfo) {

        this.rewardActivity = activity;
        this.apiKey = loginInfo.get(0);

        receiver_username = infoList.get(0);
        giver_username = loginInfo.get(1);
        giver_fullName= String.format("%s %s", loginInfo.get(2), loginInfo.get(3));
        amount = infoList.get(1);
        note = infoList.get(2);
    }

    public void run() {

        // Here I delete he user since this is a sample.
        // You do NOT need to do this.

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            Log.d(TAG, "run: Initial URL: " + urlString);

            buildURL.appendQueryParameter("receiverUser", receiver_username);
            buildURL.appendQueryParameter("giverUser", giver_username);
            buildURL.appendQueryParameter("giverName", giver_fullName);
            buildURL.appendQueryParameter("amount", amount);
            buildURL.appendQueryParameter("note", note);

            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();


            final StringBuilder sb = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }

            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
            }

            //profile = threadHandler(sb.toString());




            Log.d(TAG, "run: " + sb.toString());
            rewardActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        rewardActivity.rewardThreadHandler();
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
/*
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

 */
}

