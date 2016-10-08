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

import dev.roviloapps.hackupcfall2016.controllers.AirportController;
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

                airportArrayList.add(
                        new Airport(
                                airport.getString("code"),
                                airport.getDouble("lat"),
                                airport.getDouble("lon"),
                                airport.getString("name"),
                                airport.getString("city"),
                                airport.getString("country")));
            }
            Log.e(TAG, "READING END AIRPORT-JSON");
            AirportController.getInstance(context).setAirports(airportArrayList);

            Thread.sleep(500);
        } catch (JSONException | InterruptedException e) {
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
