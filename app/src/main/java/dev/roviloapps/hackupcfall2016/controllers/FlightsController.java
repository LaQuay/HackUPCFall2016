package dev.roviloapps.hackupcfall2016.controllers;

import android.content.Context;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dev.roviloapps.hackupcfall2016.model.Airport;
import dev.roviloapps.hackupcfall2016.model.Flight;
import dev.roviloapps.hackupcfall2016.model.FlightQuote;


public class FlightsController {
    private final String TAG = ForecastController.class.getSimpleName();
    private String SKYSCANNER_KEY = "prtl6749387986743898559646983194";

    private final Context context;

    public FlightsController(Context context) {
        this.context = context;
    }

    public void flightsRequest(String source, String destination, String inDate, String outDate) {

        String URL = "http://partners.api.skyscanner.net/apiservices/browsequotes/v1.0/GB/GBP/en-GB/"
                + source + "/"
                + destination + "/"
                + inDate + "/"
                + outDate + "?apiKey=" + SKYSCANNER_KEY;

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<FlightQuote> quotes = new ArrayList<>();

                        try {
                            JSONArray jquotes = response.getJSONArray("Quotes");
                            for (int i = 0; i < jquotes.length(); i++) {
                                try
                                {
                                    int minPrice = jquotes.getJSONObject(i).getInt("MinPrice");
                                    boolean direct = jquotes.getJSONObject(i).getBoolean("Direct");
                                    Flight outboundLeg = legToFlight(jquotes.getJSONObject(i).getJSONObject("OutboundLeg"), response);
                                    Flight inboundLeg = legToFlight(jquotes.getJSONObject(i).getJSONObject("InboundLeg"), response);

                                    quotes.add(new FlightQuote(minPrice,direct,outboundLeg,inboundLeg));
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        requestQueue.add(objectRequest);
    }

    private Flight legToFlight(JSONObject leg,JSONObject fullResponse) throws Exception {
        ArrayList<String> carriers_ = new ArrayList<>();
        JSONArray carriers = leg.getJSONArray("CarrierIds");
        for (int i = 0; i < carriers.length(); i++) {
            carriers_.add(findRowById(fullResponse.getJSONArray("Carriers"),"CarrierId", carriers.getInt(i)).getString("Name"));
        }
        Airport origin;
        String iataOrigin = findRowById(fullResponse.getJSONArray("Places"),"PlaceId", leg.getInt("OriginId")).getString("IataCode");
        Airport destination;
        String iataDestination = findRowById(fullResponse.getJSONArray("Places"),"PlaceId", leg.getInt("DestinationId")).getString("IataCode");
        Date date = new Date(/*leg.getString("DepartureDate").split("T")[0]*/);

        return new Flight(carriers_, null, null, date);
    }

    private JSONObject findRowById (JSONArray object, String idName, int id) throws Exception {
        for (int i = 0; i < object.length(); i++) {
            try {
                if (object.getJSONObject(i).getInt(idName) == id)
                {
                    return object.getJSONObject(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        throw new Exception("asda");
    }
}
