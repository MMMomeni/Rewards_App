package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
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

public class AllProfilesRunnable implements Runnable {

    private static final String TAG = "GetAllProfiles";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/GetAllProfiles";

    private final LeaderBoardActivity lBActivity;


    private String apiKey;
    private List<Profile> profile;

    //
    // Don't forget in manifest:
    //          android:usesCleartextTraffic="true"
    //
    public AllProfilesRunnable(LeaderBoardActivity activity , List<String> key) {

        this.lBActivity = activity;
        this.apiKey = key.get(0);
    }

    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
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
                //reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
            }

            profile = threadHandler(sb.toString());




            Log.d(TAG, "run: " + sb.toString());
            lBActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (profile != null)
                        lBActivity.threadResult(profile);
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

    private List<Profile> threadHandler(String sb){
        List<Profile> resultList = new ArrayList<>();

        try {
            JSONArray data = new JSONArray(sb);
            int size = data.length();

            for(int i = 0; i < size; i++){
                int amount = 0;
                JSONObject jo = data.getJSONObject(i);

                JSONArray ja = jo.getJSONArray("rewardRecordViews");


                for (int j = 0; j < ja.length(); j++){
                    JSONObject joReceiver = ja.getJSONObject(j);

                    amount += Integer.parseInt(joReceiver.getString("amount"));
                }

                resultList.add(new Profile(jo.getString("firstName"), jo.getString("lastName"),
                        jo.getString("userName"), jo.getString("department"),
                        jo.getString("story"), jo.getString("position"),
                        "","",
                        "", jo.getString("imageBytes"),
                        jo.getJSONArray("rewardRecordViews"), String.format("%s", amount)));
            }

            return  resultList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}

