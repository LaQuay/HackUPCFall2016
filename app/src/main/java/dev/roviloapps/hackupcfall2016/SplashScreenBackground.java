package dev.roviloapps.hackupcfall2016;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import dev.roviloapps.hackupcfall2016.controllers.FrontController;
import dev.roviloapps.hackupcfall2016.model.Airport;

public class SplashScreenBackground extends AsyncTask<Void, Void, Void> {
    private static final String TAG = SplashScreenBackground.class.getSimpleName();
    private final LoadingTaskFinishedListener finishedListener;
    private Context context;

    public SplashScreenBackground(LoadingTaskFinishedListener finishedListener, Context context) {
        this.finishedListener = finishedListener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        readJSONAirports();
        return null;
    }

    private void readJSONAirports() {
        try {
            JSONArray jsonArray = new JSONArray(loadJSON());

            ArrayList<Airport> airportArrayList = new ArrayList<>();
            Log.e(TAG, "READING AIRPORT-JSON");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject airport = jsonArray.getJSONObject(i);

                String code = airport.getString("code");
                double latitude = airport.getDouble("lat");
                double longitude = airport.getDouble("lon");
                String name = airport.getString("name");
                String city = airport.getString("city");
                String country = airport.getString("country");
                airportArrayList.add(new Airport(code, latitude, longitude, name, city, country));
            }
            Log.e(TAG, "READING END AIRPORT-JSON");
            FrontController.getInstance(context).setAirports(airportArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSON() {
        String json;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.airports);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onPostExecute(Void status) {
        super.onPostExecute(status);

        finishedListener.onTaskFinished();
    }

    public interface LoadingTaskFinishedListener {
        void onTaskFinished();
    }
}
