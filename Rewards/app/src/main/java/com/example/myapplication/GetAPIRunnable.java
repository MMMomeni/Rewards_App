package com.example.myapplication;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetAPIRunnable implements Runnable {

    // The HTTP GET method is used to read (or retrieve) a representation of a resource.

    private final MainActivity mainActivity;
    private final String param1;

    GetAPIRunnable(MainActivity mainActivity, String param1) {
        this.mainActivity = mainActivity;
        this.param1 = param1;

    }

    @Override
    public void run() {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            Uri.Builder buildURL = Uri.parse(param1).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
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
            String api = data.getString("apiKey");

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (api != null)
                        mainActivity.resultDialog(api);
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
}

