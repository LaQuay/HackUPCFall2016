package dev.roviloapps.hackupcfall2016.controllers;

import android.content.Context;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dev.roviloapps.hackupcfall2016.R;
import dev.roviloapps.hackupcfall2016.model.Airport;
import dev.roviloapps.hackupcfall2016.model.Flight;
import dev.roviloapps.hackupcfall2016.model.FlightQuote;

public class FlightsController {
    private final String TAG = ForecastController.class.getSimpleName();
    private final Context context;

    public FlightsController(Context context) {
        this.context = context;
    }

    public void flightsRequest(String source, String destination, String inDate, String outDate, final FlightsRequestResolvedCallback flightsRequestResolvedCallback) {

        String URL = "http://partners.api.skyscanner.net/apiservices/browsequotes/v1.0/GB/GBP/en-GB/"
                + source + "/"
                + destination + "/"
                + inDate + "/"
                + outDate + "?apiKey=" + context.getString(R.string.skyscanner_key);

        Log.e(TAG, URL);

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

                                    Flight outboundLeg = null;
                                    if (jquotes.getJSONObject(i).has("OutboundLeg"))
                                        outboundLeg = legToFlight(jquotes.getJSONObject(i).getJSONObject("OutboundLeg"), response);
                                    Flight inboundLeg = null;
                                    if (jquotes.getJSONObject(i).has("InboundLeg"))
                                        inboundLeg = legToFlight(jquotes.getJSONObject(i).getJSONObject("InboundLeg"), response);

                                    FlightQuote flightQuote = new FlightQuote(minPrice,direct,outboundLeg,inboundLeg);
                                    int sortPos = binarySortPricePosition(quotes, flightQuote);
                                    quotes.add(sortPos, flightQuote);
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        flightsRequestResolvedCallback.onFlightsRequestResolved(quotes);
                        //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
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
        String iataOrigin = findRowById(fullResponse.getJSONArray("Places"),"PlaceId", leg.getInt("OriginId")).getString("IataCode");
        Airport origin = AirportController.getInstance(this.context).getAirport(iataOrigin);
        String iataDestination = findRowById(fullResponse.getJSONArray("Places"),"PlaceId", leg.getInt("DestinationId")).getString("IataCode");
        Airport destination = AirportController.getInstance(this.context).getAirport(iataDestination);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = format.parse(leg.getString("DepartureDate"));

        return new Flight(carriers_, origin, destination, date);
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

    private int binarySortPricePosition(ArrayList<FlightQuote> flightQuotes, FlightQuote flightQuote) {
        int ind = Collections.binarySearch(flightQuotes, flightQuote);

        // if same MinPrice, ind indicates its position
        // else ind indicates (- ind - 1) position
        if (ind < 0) ind = - ind - 1;
        //Log.e(TAG, ind + " " + flightQuote.getMinPrice());

        return ind;
    }

    public interface FlightsRequestResolvedCallback {
        void onFlightsRequestResolved(ArrayList<FlightQuote> flightQuotesArray);
    }
}
