package dev.roviloapps.hackupcfall2016;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.roviloapps.hackupcfall2016.controllers.ForecastController;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForecastController forecastController = new ForecastController(getApplicationContext());
                forecastController.forecastRequest("35","139");
                skyscannerpriceapi("BCN","MAD","2017-02-11","2017-02-15");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void skyscannerpriceapi(String source, String destination, String inDate, String outDate) {

        String URL = "http://partners.api.skyscanner.net/apiservices/browsequotes/v1.0/GB/GBP/en-GB/"
                + source + "/"
                + destination + "/"
                + inDate + "/"
                + outDate + "?apiKey=prtl6749387986743898559646983194";

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SkyScannerResponse result = new SkyScannerResponse(response);

                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {



            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(objectRequest);
    }

    private class SkyScannerResponse {
        ArrayList<JSONObject> quotes;
        ArrayList<JSONObject> places;
        ArrayList<JSONObject> carriers;
        ArrayList<JSONObject> curriencies;

        public SkyScannerResponse() {
            this.quotes = new ArrayList<>();
            this.places = new ArrayList<>();
            this.carriers = new ArrayList<>();
            this.curriencies = new ArrayList<>();
        }

        public SkyScannerResponse(JSONObject obj) {
            this.quotes = new ArrayList<>();
            this.places = new ArrayList<>();
            this.carriers = new ArrayList<>();
            this.curriencies = new ArrayList<>();

            try {
                JSONArray quotes = obj.getJSONArray("Quotes");
                for (int i = 0; i < quotes.length(); i++) {
                    this.quotes.add(quotes.getJSONObject(i));
                }

                JSONArray places = obj.getJSONArray("Places");
                for (int i = 0; i < places.length(); i++) {
                    this.places.add(places.getJSONObject(i));
                }

                JSONArray carriers = obj.getJSONArray("Carriers");
                for (int i = 0; i < carriers.length(); i++) {
                    this.carriers.add(carriers.getJSONObject(i));
                }

                JSONArray curriencies = obj.getJSONArray("Currencies");
                for (int i = 0; i < curriencies.length(); i++) {
                    this.curriencies.add(curriencies.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
