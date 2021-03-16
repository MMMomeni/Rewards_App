package com.example.myapplication;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class LoginAPIRunnable implements Runnable {

    // The HTTP GET method is used to read (or retrieve) a representation of a resource.

    private final MainActivity mainActivity;
    private final String username;
    private final String password;
    private String apiKey;
    private Profile profile;
    private List<Reward> allRewards = new ArrayList<>();

    LoginAPIRunnable(MainActivity mainActivity, String username, String password , String key) {
        this.mainActivity = mainActivity;
        this.username = username;
        this.password = password;
        this.apiKey = key;
    }

    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Profile/Login";
            ;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            //Log.d(TAG, "run: Initial URL: " + urlString);

            buildURL.appendQueryParameter("userName", username);
            buildURL.appendQueryParameter("password", password);

            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            //Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }


            JSONObject data = new JSONObject(result.toString());
            profile = new Profile(data.getString("firstName"), data.getString("lastName"),
                                  data.getString("userName"), data.getString("department"),
                                  data.getString("story"), data.getString("position"),
                                  data.getString("password"),data.getString("remainingPointsToAward"),
                                  data.getString("location"), data.getString("imageBytes"), null, null);

            JSONArray ja = data.getJSONArray("rewardRecordViews");


            for (int i = 0; i <ja.length() ; i++){
                JSONObject jo = ja.getJSONObject(i);

                allRewards.add(0, new Reward(jo.getString("giverName"), jo.getString("amount"),
                        jo.getString("note"), jo.getString("awardDate")));

            }


           // String api = data.getString("apiKey");

            //List<String> resultList = threadHandler(result.toString());

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (profile != null)
                        mainActivity.loginThreadResult(profile, allRewards);
                }
            });
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        //mainActivity.showResults("Error performing GET request");
    }

    public List<String> threadHandler (String result){



        return null;
    }
}


