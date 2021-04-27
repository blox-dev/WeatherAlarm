package com.example.weatheralarm;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;


import javax.net.ssl.HttpsURLConnection;

public class AsyncHTTPRequest extends AsyncTask<String, Void, String> {
    static String[] directions = new String[]{"N","NE","E","SE","S","SW","W","NE"};

    public AsyncResponse delegate;

    public AsyncHTTPRequest(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... strings) {
        for(String targetURL : strings) {
            URL url;
            HttpsURLConnection connection = null;
            try {
                url = new URL(targetURL);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream is;
                int status = connection.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK)
                    is = connection.getErrorStream();
                else
                    is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        return null;
    }

    private String parseJSON(String input)
    {
        try {
            StringBuilder output = new StringBuilder();
            JSONObject obj = new JSONObject(input);

            String city = obj.getString("name");
            String country = obj.getJSONObject("sys").getString("country");
            String description = obj.getJSONArray("weather").getJSONObject(0).getString("main");
            double temp = Double.parseDouble(obj.getJSONObject("main").getString("temp")) - 273.15;
            String windSpeed = obj.getJSONObject("wind").getString("speed");
            String windDirection = obj.getJSONObject("wind").getString("deg");
            String cloudPercentage = obj.getJSONObject("clouds").getString("all");

            int wd = Integer.parseInt(windDirection);
            for(int i=0 ; i<8 ; ++i)
            {
                if(wd >= i*45 - 22.5 && wd <= i*45 + 22.5)
                {
                    windDirection = directions[i];
                    break;
                }
            }
            if(wd > 337.5)
                windDirection = directions[0];

            output.append("Weather in ").append(city).append(",").append(country);
            output.append("\n").append(description);
            output.append("\n").append(Math.round(temp)).append("°C");
            output.append("\nWind: ").append(windSpeed).append("m/s ").append(windDirection);
            output.append("\nClouds: ").append(cloudPercentage).append("%");

            return output.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Weird weather lol";
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.processFinish(parseJSON(s));
    }
}