package dev.roviloapps.hackupcfall2016;

import android.location.Location;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import dev.roviloapps.hackupcfall2016.controllers.AirportController;
import dev.roviloapps.hackupcfall2016.controllers.ForecastController;
import dev.roviloapps.hackupcfall2016.controllers.LocationController;
import dev.roviloapps.hackupcfall2016.model.Airport;
import dev.roviloapps.hackupcfall2016.model.Forecast;
import dev.roviloapps.hackupcfall2016.utility.MathUtils;

public class MainFragmentActivity extends Fragment implements ForecastController.ForecastResolvedCallback, OnMapReadyCallback, LocationController.OnNewLocationCallback {
    private static final String TAG = MainFragmentActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 14;
    private View rootView;
    private AutoCompleteTextView autoCompleteOriginAirport;
    private GoogleMap mMap;
    private MapView mapView;
    private LatLngBounds CAT = new LatLngBounds(
            new LatLng(40.3, 0.2),
            new LatLng(42.5, 3.4));

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

        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        ArrayList<Airport> airportArrayList = AirportController.getInstance(getActivity()).getAirports();
        Log.e(TAG, "AIRPORTS: " + airportArrayList.size());
        ArrayAdapter<Airport> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, airportArrayList);
        autoCompleteOriginAirport.setAdapter(adapter);

        autoCompleteOriginAirport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Airport airportSelected = (Airport) arg0.getAdapter().getItem(arg2);
                Toast.makeText(getActivity(), airportSelected.getCode() + "-" + airportSelected.getName(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onNewLocation(Location location) {
        if (location != null) {
            Log.e(TAG, "New location: " + location.getLatitude() + ", " + location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

            checkNearestAirport(location);
        }
    }

    private Airport checkNearestAirport(Location location) {
        Airport airport = null;
        ArrayList<Airport> airportArrayList = AirportController.getInstance(getActivity()).getAirports();

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
        return airport;
    }
}
