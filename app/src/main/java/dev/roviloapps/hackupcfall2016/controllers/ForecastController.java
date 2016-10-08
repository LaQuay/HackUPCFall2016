package dev.roviloapps.hackupcfall2016.controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dev.roviloapps.hackupcfall2016.model.Forecast;

public class ForecastController {

    private final String TAG = ForecastController.class.getSimpleName();
    private final Context context;
    private String OPENWEATHER_KEY = "2ee176e182eecc4608f89e707774e5b7";

    public ForecastController(Context context) {
        this.context = context;
    }

    public static Date unixTimeStampToDate(long unixSeconds, SimpleDateFormat format) throws ParseException {
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);

        return (format.parse(formattedDate));
    }

    public void forecastRequest(double lat, double lon, final ForecastResolvedCallback forecastResolvedCallback) {
        Uri.Builder builder = new Uri.Builder();
        //builder.scheme("http")
        //        .authority("api.openweathermap.org")
        //        .appendPath("data")
        //        .appendPath("2.5")
        //        .appendPath("forecast")
        //        .appendQueryParameter("lat", Double.toString(lat))
        //        .appendQueryParameter("lon", Double.toString(lon))
        //        .appendQueryParameter("appid", OPENWEATHER_KEY)
        //        .fragment("section-name");
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("lat", Double.toString(lat))
                .appendQueryParameter("lon", Double.toString(lon))
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("cnt", "16")
                .appendQueryParameter("appid", OPENWEATHER_KEY)
                .fragment("section-name");
        String url = builder.build().toString();

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Forecast> forecastArray = parseForecastJSON16Day(response);
                        forecastResolvedCallback.onForecastResolved(forecastArray);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        VolleyController.getInstance(context).addToQueue(jsonObjectRequest);
    }

    private ArrayList<Forecast> parseForecastJSON16Day(JSONObject forecastJSONObject) {
        Log.e(TAG, "Forecast on ForecastController");

        ArrayList<Forecast> forecastArray = new ArrayList<Forecast>();

        try {
            JSONArray weatherArray = forecastJSONObject.getJSONArray("list");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject forecastObject = weatherArray.getJSONObject(i);

                Forecast forecast = new Forecast();

                long unixSeconds = forecastObject.getLong("dt");
                Date date = null;
                try {
                    date = unixTimeStampToDate(unixSeconds, format);
                    forecast.setDate(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                JSONObject forecastTemp = forecastObject.getJSONObject("temp");
                forecast.setTemperature(Double.parseDouble(forecastTemp.getString("eve")));
                forecast.setTemperatureMin(Double.parseDouble(forecastTemp.getString("min")));
                forecast.setTemperatureMax(Double.parseDouble(forecastTemp.getString("max")));

                forecast.setTemperatureScale();

                forecastArray.add(forecast);
            }
            return forecastArray;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return forecastArray;
    }

    private ArrayList<Forecast> parseForecastJSON5Day(JSONObject forecastJSONObject) {
        Log.e(TAG, "Forecast on ForecastController");

        ArrayList<Forecast> forecastArray = new ArrayList<Forecast>();

        try {
            JSONArray weatherArray = forecastJSONObject.getJSONArray("list");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatHour = new SimpleDateFormat("HH");

            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject forecastObject = weatherArray.getJSONObject(i);

                Forecast forecast = new Forecast();
                try {
                    Date date = format.parse(forecastObject.getString("dt_txt"));
                    if (!formatHour.format(date).equals("12")) continue;
                    forecast.setDate(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                JSONObject forecastMain = forecastObject.getJSONObject("main");
                forecast.setTemperature(Double.parseDouble(forecastMain.getString("temp")));
                forecast.setTemperatureMin(Double.parseDouble(forecastMain.getString("temp_min")));
                forecast.setTemperatureMax(Double.parseDouble(forecastMain.getString("temp_max")));

                forecast.setTemperatureScale();

                forecastArray.add(forecast);
            }

            return forecastArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return forecastArray;
    }

    public interface ForecastResolvedCallback {
        void onForecastResolved(ArrayList<Forecast> forecastArray);
    }
}
