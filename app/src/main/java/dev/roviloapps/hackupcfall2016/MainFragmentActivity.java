package dev.roviloapps.hackupcfall2016;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import dev.roviloapps.hackupcfall2016.controllers.AirportController;
import dev.roviloapps.hackupcfall2016.controllers.FlightsController;
import dev.roviloapps.hackupcfall2016.controllers.ForecastController;
import dev.roviloapps.hackupcfall2016.model.Airport;
import dev.roviloapps.hackupcfall2016.model.FlightQuote;
import dev.roviloapps.hackupcfall2016.model.Forecast;

public class MainFragmentActivity extends Fragment implements FlightsController.FlightsRequestResolvedCallback, ForecastController.ForecastResolvedCallback, OnMapReadyCallback {
    private static final String TAG = MainFragmentActivity.class.getSimpleName();
    private View rootView;
    private AutoCompleteTextView autoCompleteOriginAirport;
    private GoogleMap mMap;
    private MapView mapView;
    private LatLngBounds CAT = new LatLngBounds(
            new LatLng(40.3, 0.2),
            new LatLng(42.5, 3.4));

    private FlightsController flightsController;
    private ForecastController forecastController;
    private final FlightsController.FlightsRequestResolvedCallback flightsRequestResolvedCallback = this;
    private final ForecastController.ForecastResolvedCallback forecastResolvedCallback = this;
    private int MAX_FLIGHTS = 15;

    public static MainFragmentActivity newInstance() {
        return new MainFragmentActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setUpElements();
        setUpListeners();

        flightsController = new FlightsController(getActivity());
        forecastController = new ForecastController(getActivity());

        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        ArrayList<Airport> airportArrayList = AirportController.getInstance(getActivity()).getAirports();
        Log.e(TAG, airportArrayList.size() + "");
        ArrayAdapter<Airport> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, airportArrayList);
        autoCompleteOriginAirport.setAdapter(adapter);

        autoCompleteOriginAirport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Airport airportSelected = (Airport) arg0.getAdapter().getItem(arg2);
                Toast.makeText(getActivity(), airportSelected.getCode() + "-" + airportSelected.getName(), Toast.LENGTH_SHORT).show();

                flightsController.flightsRequest(airportSelected.getCode(), "anywhere", "anytime", "anytime", flightsRequestResolvedCallback);
            }
        });

        return rootView;
    }

    private void setUpElements() {
        autoCompleteOriginAirport = (AutoCompleteTextView) rootView.findViewById(R.id.content_main_origin_autocomplete);

        mapView = (MapView) rootView.findViewById(R.id.fragment_main_map_google);
    }

    private void setUpListeners() {

    }

    @Override
    public void onflightsRequestResolved(ArrayList<FlightQuote> flightQuoteArray) {
        Log.e(TAG, "Flight request in MainFragment :D");
        Log.e(TAG, "Num flights: " + flightQuoteArray.size());

        ArrayList<FlightQuote> minFlightQuoteArray = filterFlighQuotes(flightQuoteArray);
        Log.e(TAG, "Num flights: " + minFlightQuoteArray.size());
    }

    @Override
    public void onForecastResolved(ArrayList<Forecast> forecastArray) {
        Log.e(TAG, "Forecast on MainActivity :D");
        Log.e(TAG, forecastArray.toString());

        for (int i = 0; i < forecastArray.size(); ++i) {
            Log.e(TAG, Double.toString(forecastArray.get(i).getTemperature()) + " " + Integer.toString(forecastArray.get(i).getTemperatureScale()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(CAT, 6));

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        mMap.getUiSettings().setAllGesturesEnabled(false);
    }

    private ArrayList<FlightQuote> filterFlighQuotes(ArrayList<FlightQuote> flightQuoteArray) {
        ArrayList<FlightQuote> minPriceFlights = new ArrayList<>();
        for (int i = 0; i < flightQuoteArray.size(); ++i) {
            FlightQuote flightQuote = flightQuoteArray.get(i);

            boolean satisfyFilters = flightSatisfyFilters(flightQuote);
            if (satisfyFilters) {
                minPriceFlights.add(flightQuote);
                Log.e(TAG, "Price: " + flightQuote.getMinPrice() +
                        " from " + flightQuote.getInboundLeg().getOrigin().getCode() +
                        " to " + flightQuote.getInboundLeg().getDestination().getCode());
            }

            if (minPriceFlights.size() == MAX_FLIGHTS) break;
        }

        return minPriceFlights;
    }

    private boolean flightSatisfyFilters(FlightQuote flightQuote) {
        return true;
    }
}
