package dev.roviloapps.hackupcfall2016.controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import dev.roviloapps.hackupcfall2016.R;

public class ForecastController {

    private final String TAG = ForecastController.class.getSimpleName();
    private String OPENWEATHER_KEY = "2ee176e182eecc4608f89e707774e5b7";

    private final Context context;

    public ForecastController(Context context) {
        this.context = context;
    }

    public String forecastRequest(String lat, String lon) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("lon", lon)
                .appendQueryParameter("appid", OPENWEATHER_KEY)
                .fragment("section-name");
        String url = builder.build().toString();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.e(TAG, response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        VolleyController.getInstance(context).addToQueue(stringRequest);
        return null;
    }
}
