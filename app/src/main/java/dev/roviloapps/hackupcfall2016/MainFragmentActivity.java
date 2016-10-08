package dev.roviloapps.hackupcfall2016;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import dev.roviloapps.hackupcfall2016.controllers.AirportController;
import dev.roviloapps.hackupcfall2016.controllers.FlightsController;
import dev.roviloapps.hackupcfall2016.controllers.ForecastController;
import dev.roviloapps.hackupcfall2016.controllers.LocationController;
import dev.roviloapps.hackupcfall2016.model.Airport;
import dev.roviloapps.hackupcfall2016.model.FlightQuote;
import dev.roviloapps.hackupcfall2016.model.Forecast;
import dev.roviloapps.hackupcfall2016.utility.MathUtils;

public class MainFragmentActivity extends Fragment implements FlightsController.FlightsRequestResolvedCallback, ForecastController.ForecastResolvedCallback, OnMapReadyCallback, LocationController.OnNewLocationCallback {
    private static final String TAG = MainFragmentActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 13;
    private static final int DEFAULT_NO_LOCATION_ZOOM = 10;
    private Location userLocation;
    private View rootView;
    private AutoCompleteTextView autoCompleteOriginAirport;
    private GoogleMap mMap;
    private MapView mapView;
    private LatLngBounds CAT = new LatLngBounds(
            new LatLng(40.3, 0.2),
            new LatLng(42.5, 3.4));
    private CardView currentPositionCardView;

    private FlightsController flightsController;
    private ForecastController forecastController;
    private final FlightsController.FlightsRequestResolvedCallback flightsRequestResolvedCallback = this;
    private final ForecastController.ForecastResolvedCallback forecastResolvedCallback = this;
    private int MAX_FLIGHTS = 1;
    private int actForecastFlightRequestPos = -1;
    private ArrayList<FlightQuote> flightQuoteArray;
    private ArrayList<FlightQuote> filteredFlightQuoteArray;

    public static MainFragmentActivity newInstance() {
        return new MainFragmentActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocationController.getInstance(getActivity()).startLocation(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocationController.getInstance(getActivity()).stopLocation();
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

        final ArrayList<Airport> airportArrayList = AirportController.getInstance(getActivity()).getAirports();
        Log.e(TAG, "AIRPORTS: " + airportArrayList.size());
        ArrayAdapter<Airport> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, airportArrayList);
        autoCompleteOriginAirport.setAdapter(adapter);

        autoCompleteOriginAirport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Airport airportSelected = (Airport) arg0.getAdapter().getItem(arg2);
                autoCompleteOriginAirport.setHint(airportSelected.getCode() + " - " + airportSelected.getName() + ", " + airportSelected.getCountry());
                //Toast.makeText(getActivity(), airportSelected.getCode() + "-" + airportSelected.getName(), Toast.LENGTH_SHORT).show();

                flightsController.flightsRequest(airportSelected.getCode(), "anywhere", "anytime", "anytime", flightsRequestResolvedCallback);

                autoCompleteOriginAirport.setText("");
                hideKeyboard(rootView);
                autoCompleteOriginAirport.clearFocus();

                selectAirport(airportSelected);
            }
        });

        autoCompleteOriginAirport.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    autoCompleteOriginAirport.showDropDown();
                }
            }
        });

        return rootView;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setUpElements() {
        autoCompleteOriginAirport = (AutoCompleteTextView) rootView.findViewById(R.id.content_main_origin_autocomplete);
        mapView = (MapView) rootView.findViewById(R.id.fragment_main_map_google);

        currentPositionCardView = (CardView) rootView.findViewById(R.id.fragment_main_current_position_cardview);
    }

    private void setUpListeners() {
        currentPositionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Airport airport = checkNearestAirport(userLocation);
                selectAirport(airport);

                autoCompleteOriginAirport.setHint(airport.getCode() + " - " + airport.getName() + ", " + airport.getCountry());
            }
        });
    }

    @Override
    public void onflightsRequestResolved(ArrayList<FlightQuote> flightQuoteArray) {
        Log.e(TAG, "Flight request in MainFragment :D");

        this.flightQuoteArray = filterFlights16Days(flightQuoteArray);
        this.filteredFlightQuoteArray = new ArrayList<>();

        Log.e(TAG, "Num SkyScanner flights: " + flightQuoteArray.size() + " Filtered: " + this.flightQuoteArray.size());

        if (this.flightQuoteArray.size() > 0) {
            actForecastFlightRequestPos = 0;
            callNextForecastRequest();
        }
        else {
            Toast.makeText(getActivity(), "No flight found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onForecastResolved(ArrayList<Forecast> forecastArray) {
        Log.e(TAG, "Forecast on MainActivity :D");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String flightDateString = dateFormat.format(flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().getDate());

        for (int i = 0; i < forecastArray.size(); ++i) {
            String forecastDateString = dateFormat.format(forecastArray.get(i).getDate());
            if (flightDateString.equals(forecastDateString)) {
                // forecast for flight day
                if (flightSatisfyFilters(forecastArray.get(i)))
                    filteredFlightQuoteArray.add(flightQuoteArray.get(actForecastFlightRequestPos));

                break;
            }
        }

        actForecastFlightRequestPos++;
        if (filteredFlightQuoteArray.size() < MAX_FLIGHTS && actForecastFlightRequestPos < flightQuoteArray.size()) callNextForecastRequest();
        else {
            if (filteredFlightQuoteArray.size() == 0) {
                Toast.makeText(getActivity(), "No flight found", Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < filteredFlightQuoteArray.size(); ++i) {
                FlightQuote flightQuote = filteredFlightQuoteArray.get(i);
                Toast.makeText(getActivity(), "Price: " + flightQuote.getMinPrice() +
                        " from " + flightQuote.getInboundLeg().getOrigin().getCode() +
                        " to " + flightQuote.getInboundLeg().getDestination().getCode(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(CAT, DEFAULT_NO_LOCATION_ZOOM));
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        mMap.getUiSettings().setAllGesturesEnabled(false);
    }

    private void callNextForecastRequest() {
        double latitude = flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().getDestination().getLatitude();
        double longitude = flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().getDestination().getLongitude();
        forecastController.forecastRequest(latitude, longitude, forecastResolvedCallback);
    }

    private ArrayList<FlightQuote> filterFlights16Days(ArrayList<FlightQuote> flightQuotes) {
        ArrayList<FlightQuote> filteredArray = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        for (int i = 0; i < flightQuotes.size(); ++i) {
            Date flightDate = flightQuotes.get(i).getInboundLeg().getDate();
            long diff = flightDate.getTime() - today.getTime();
            long numDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (numDays > 0 && numDays <= 16) filteredArray.add(flightQuotes.get(i));
        }

        return filteredArray;
    }

    private boolean flightSatisfyFilters(Forecast forecast) {
        //if (forecastArray.get(i).getTemperatureScale()  Forecast.TEMP_LOW)
        return true;
    }

    @Override
    public void onNewLocation(Location location) {
        this.userLocation = location;
        if (location != null) {
            Log.e(TAG, "New location: " + location.getLatitude() + ", " + location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            addMarkerUser(latLng);
            animateCamera(latLng);

            Airport airport = checkNearestAirport(location);
            if (airport != null) {
                addMarkerAirport(new LatLng(airport.getLatitude(), airport.getLongitude()));
            }
        }
    }

    private Airport checkNearestAirport(Location location) {
        Airport airport = null;
        ArrayList<Airport> airportArrayList = AirportController.getInstance(getActivity()).getAirports();

        if (location != null) {
            double userLatitude = location.getLatitude();
            double userLongitude = location.getLongitude();
            double distAirport = Double.MAX_VALUE;
            for (int i = 0; i < airportArrayList.size(); ++i) {
                Airport currentAirport = airportArrayList.get(i);
                if (distAirport > MathUtils.distanceBetweenTwoLocations(userLatitude, userLongitude, currentAirport.getLatitude(), currentAirport.getLongitude())) {
                    distAirport = MathUtils.distanceBetweenTwoLocations(userLatitude, userLongitude, currentAirport.getLatitude(), currentAirport.getLongitude());
                    airport = currentAirport;
                }
            }
        }
        return airport;
    }

    private void selectAirport(Airport airport) {
        LatLng latLng = new LatLng(airport.getLatitude(), airport.getLongitude());
        addMarkerAirport(latLng);
        animateCamera(latLng);
    }

    private void addMarkerUser(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation_blue_icon)));
    }

    private void addMarkerAirport(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.common_full_open_on_phone)));
    }

    private void animateCamera(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }
}
