package dev.roviloapps.hackupcfall2016;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dev.roviloapps.hackupcfall2016.controllers.AirportController;
import dev.roviloapps.hackupcfall2016.controllers.FlightsController;
import dev.roviloapps.hackupcfall2016.controllers.ForecastController;
import dev.roviloapps.hackupcfall2016.controllers.LocationController;
import dev.roviloapps.hackupcfall2016.model.Airport;
import dev.roviloapps.hackupcfall2016.model.FlightQuote;
import dev.roviloapps.hackupcfall2016.model.Forecast;
import dev.roviloapps.hackupcfall2016.utility.MathUtils;
import dev.roviloapps.hackupcfall2016.utility.SharedPreferencesManager;
import dev.roviloapps.hackupcfall2016.utility.Utility;

public class MainFragmentActivity extends Fragment implements FlightsController.FlightsRequestResolvedCallback, ForecastController.ForecastResolvedCallback, OnMapReadyCallback, LocationController.OnNewLocationCallback {
    private static final String TAG = MainFragmentActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 11;
    private static final int DEFAULT_NO_LOCATION_ZOOM = 10;
    private static final int MAX_FLIGHTS = 10;
    private final FlightsController.FlightsRequestResolvedCallback flightsRequestResolvedCallback = this;
    private final ForecastController.ForecastResolvedCallback forecastResolvedCallback = this;
    private LayoutInflater inflater;
    private Location userLocation;
    private View rootView;
    private AutoCompleteTextView autoCompleteOriginAirport;
    private GoogleMap mMap;
    private MapView mapView;
    private LatLngBounds CAT = new LatLngBounds(
            new LatLng(40.3, 0.2),
            new LatLng(42.5, 3.4));
    private CardView currentPositionCardView;
    private CardView settingsButtonCardView;
    private CardView settingsLayoutCardView;
    private LinearLayout flightsHolderLayout;
    private CheckBox sunCheckbox;
    private CheckBox rainCheckbox;
    private CheckBox snowCheckbox;
    private CheckBox hotCheckbox;
    private CheckBox coldCheckbox;
    private FlightsController flightsController;
    private ForecastController forecastController;
    private int actForecastFlightRequestPos = -1;
    private ArrayList<FlightQuote> flightQuoteArray;
    private ArrayList<FlightQuote> filteredFlightQuoteArray;
    private boolean isCurrentPositionActivated;
    private boolean isSettingsActivated;

    private Marker currentMarker;
    private Marker destinationMarker;
    private Polyline tripLine;
    private CardView cvSelected;

    private int weatherCondition = Forecast.WEATHER_CLEAR;
    private int temperatureScale = -1;//Forecast.TEMP_HIGH;
    private double temperature = 20;

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
        this.inflater = inflater;

        setUpElements();
        setUpListeners();

        loadCheckBox();

        flightsController = new FlightsController(getActivity());
        forecastController = new ForecastController(getActivity());
        isCurrentPositionActivated = false;
        isSettingsActivated = false;

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

                autoCompleteOriginAirport.setText("");
                Utility.hideKeyboard(getContext(), rootView);
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

    private void setUpElements() {
        autoCompleteOriginAirport = (AutoCompleteTextView) rootView.findViewById(R.id.content_main_origin_autocomplete);
        mapView = (MapView) rootView.findViewById(R.id.fragment_main_map_google);

        flightsHolderLayout = (LinearLayout) rootView.findViewById(R.id.fragment_main_container_flights);

        currentPositionCardView = (CardView) rootView.findViewById(R.id.fragment_main_current_position_cardview);
        settingsButtonCardView = (CardView) rootView.findViewById(R.id.fragment_main_settings_button_cardview);
        settingsLayoutCardView = (CardView) rootView.findViewById(R.id.fragment_main_settings_layout_cardview);

        sunCheckbox = (CheckBox) rootView.findViewById(R.id.fragment_main_sun_checkbox);
        rainCheckbox = (CheckBox) rootView.findViewById(R.id.fragment_main_rain_checkbox);
        snowCheckbox = (CheckBox) rootView.findViewById(R.id.fragment_main_snow_checkbox);
        hotCheckbox = (CheckBox) rootView.findViewById(R.id.fragment_main_hot_checkbox);
        coldCheckbox = (CheckBox) rootView.findViewById(R.id.fragment_main_cold_checkbox);
    }

    private void setUpListeners() {
        currentPositionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCurrentPositionActivated) {
                    Airport airport = checkNearestAirport(userLocation);
                    selectAirport(airport);
                } else {
                    Snackbar.make(v, "It's not possible to do a request, user location not available", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        settingsButtonCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSettingsActivated) {
                    settingsLayoutCardView.animate()
                            .translationY(0).alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    settingsLayoutCardView.setVisibility(View.VISIBLE);
                                    settingsLayoutCardView.setAlpha(0.0f);
                                }
                            });
                } else {
                    settingsLayoutCardView.animate()
                            .translationY(0).alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    settingsLayoutCardView.setVisibility(View.GONE);
                                }
                            });
                }
                isSettingsActivated = !isSettingsActivated;
            }
        });

        sunCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.setBooleanValue(getContext(),
                        SharedPreferencesManager.CHECKBOX_SUN_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX,
                        sunCheckbox.isChecked());
            }
        });
        rainCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.setBooleanValue(getContext(),
                        SharedPreferencesManager.CHECKBOX_RAIN_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX,
                        rainCheckbox.isChecked());
            }
        });
        snowCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.setBooleanValue(getContext(),
                        SharedPreferencesManager.CHECKBOX_SNOW_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX,
                        snowCheckbox.isChecked());
            }
        });
        hotCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.setBooleanValue(getContext(),
                        SharedPreferencesManager.CHECKBOX_HOT_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX,
                        hotCheckbox.isChecked());
            }
        });
        coldCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.setBooleanValue(getContext(),
                        SharedPreferencesManager.CHECKBOX_COLD_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX,
                        coldCheckbox.isChecked());
            }
        });
    }

    private void loadCheckBox() {
        sunCheckbox.setChecked(SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_SUN_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true));
        rainCheckbox.setChecked(SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_RAIN_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true));
        snowCheckbox.setChecked(SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_SNOW_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true));
        hotCheckbox.setChecked(SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_HOT_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true));
        coldCheckbox.setChecked(SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_COLD_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true));
    }

    @Override
    public void onFlightsRequestResolved(ArrayList<FlightQuote> flightQuoteArray) {
        Log.e(TAG, "Flight request in MainFragment :D");

        this.flightQuoteArray = filterFlights16Days(flightQuoteArray);
        this.filteredFlightQuoteArray = new ArrayList<>();

        Log.e(TAG, "Num SkyScanner flights: " + flightQuoteArray.size() + " Filtered: " + this.flightQuoteArray.size());

        if (this.flightQuoteArray.size() > 0) {
            actForecastFlightRequestPos = 0;
            callNextForecastRequest();
        } else {
            Toast.makeText(getActivity(), "No flight found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onForecastResolved(ArrayList<Forecast> forecastArray) {
        Log.e(TAG, "Forecast on MainActivity :D");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String flightDateString = dateFormat.format(flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().getDate());

        for (int i = 0; i < forecastArray.size(); ++i) {
            Forecast forecast = forecastArray.get(i);
            String forecastDateString = dateFormat.format(forecast.getDate());
            if (flightDateString.equals(forecastDateString)) {
                // forecast for flight day
                if (flightSatisfyFilters(forecastArray.get(i))) {
                    flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().setWeatherConditionDestination(forecast.getWeatherCondition());
                    flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().setTemperatureScaleDestination(forecast.getTemperatureScale());
                    flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().setTemperatureDestination(forecast.getTemperature());
                    filteredFlightQuoteArray.add(flightQuoteArray.get(actForecastFlightRequestPos));
                }
                break;
            }
        }

        actForecastFlightRequestPos++;
        if (filteredFlightQuoteArray.size() < MAX_FLIGHTS && actForecastFlightRequestPos < flightQuoteArray.size()) {
            callNextForecastRequest();
        } else {
            if (filteredFlightQuoteArray.size() == 0) {
                Toast.makeText(getActivity(), "No flight found", Toast.LENGTH_SHORT).show();
            }

            addFlightsToLayout();
        }
    }

    private void addFlightsToLayout() {
        flightsHolderLayout.removeAllViews();
        for (int i = 0; i < filteredFlightQuoteArray.size(); ++i) {
            final FlightQuote flightQuote = filteredFlightQuoteArray.get(i);

            View flightItemView = inflater.inflate(R.layout.flight_item_view, null);
            TextView locationView = (TextView) flightItemView.findViewById(R.id.flight_item_destination_text);
            TextView airportView = (TextView) flightItemView.findViewById(R.id.flight_item_airport_text);
            TextView dateView = (TextView) flightItemView.findViewById(R.id.flight_item_date_text);
            TextView temperatureView = (TextView) flightItemView.findViewById(R.id.flight_item_temperature_text);
            ImageView weatherView = (ImageView) flightItemView.findViewById(R.id.flight_item_weather_image);
            TextView priceView = (TextView) flightItemView.findViewById(R.id.flight_item_price_text);

            locationView.setText(flightQuote.getInboundLeg().getDestination().getCity());
            airportView.setText(flightQuote.getInboundLeg().getDestination().getName());

            int weatherCondition = flightQuote.getInboundLeg().getWeatherConditionDestination();
            int imageIcon = R.drawable.sun_rays_small;
            if (weatherCondition == Forecast.WEATHER_RAINY)
                imageIcon = R.drawable.cloud_dark_lightning_rain;
            else if (weatherCondition == Forecast.WEATHER_CLOUDS) imageIcon = R.drawable.cloud;
            weatherView.setImageResource(imageIcon);

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd", Locale.US);
            dateView.setText(dateFormat.format(flightQuote.getInboundLeg().getDate()));

            DecimalFormat numberFormat = new DecimalFormat("#.0");
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            numberFormat.setDecimalFormatSymbols(decimalFormatSymbols);
            temperatureView.setText(numberFormat.format(flightQuote.getInboundLeg().getTemperatureDestination()) + " ºC");

            priceView.setText(flightQuote.getMinPrice() + " €");

            flightItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng latLng = new LatLng(flightQuote.getInboundLeg().getDestination().getLatitude(), flightQuote.getInboundLeg().getDestination().getLongitude());
                    addMarkerDestinationAirport(latLng);

                    CardView flightCardView = (CardView) v.findViewById(R.id.flight_item_card);
                    if (cvSelected != null)
                        cvSelected.setBackgroundColor(Color.WHITE);

                    flightCardView.setBackgroundColor(Color.parseColor("#98e2ec"));
                    cvSelected = flightCardView;
                }
            });

            flightsHolderLayout.addView(flightItemView);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void callNextForecastRequest() {
        double latitude = flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().getDestination().getLatitude();
        double longitude = flightQuoteArray.get(actForecastFlightRequestPos).getInboundLeg().getDestination().getLongitude();
        forecastController.forecastRequest(latitude, longitude, forecastResolvedCallback);
    }

    private ArrayList<FlightQuote> filterFlights16Days(ArrayList<FlightQuote> flightQuotes) {
        ArrayList<FlightQuote> filteredArray = new ArrayList<>();

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
        boolean isSunChecked = SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_SUN_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true);
        boolean isRainChecked = SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_RAIN_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true);
        boolean isSnowChecked = SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_SNOW_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true);
        boolean isHotChecked = SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_HOT_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true);
        boolean isColdChecked = SharedPreferencesManager.getBooleanValue(getContext(), SharedPreferencesManager.CHECKBOX_COLD_KEY + SharedPreferencesManager.CHECKBOX_SUFFIX, true);

        if (!isSunChecked && forecast.getWeatherCondition() == Forecast.WEATHER_CLEAR) {
            return false;
        }
        if (!isRainChecked && forecast.getWeatherCondition() == Forecast.WEATHER_RAINY) {
            return false;
        }
        //TODO Change CLOUDS -> SNOW
        if (!isSnowChecked && forecast.getWeatherCondition() == Forecast.WEATHER_CLOUDS) {
            return false;
        }
        if (!isHotChecked && forecast.getTemperatureScale() == Forecast.TEMP_HIGH) {
            return false;
        }
        return !(!isColdChecked && forecast.getTemperatureScale() == Forecast.TEMP_LOW);
    }

    @Override
    public void onNewLocation(Location location) {
        this.userLocation = location;
        if (location != null) {
            Log.e(TAG, "New location: " + location.getLatitude() + ", " + location.getLongitude());

            Airport airport = checkNearestAirport(location);
            if (airport != null) {
                addMarkerSourceAirport(new LatLng(airport.getLatitude(), airport.getLongitude()));
                selectAirport(airport);
            }
            isCurrentPositionActivated = true;
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
        addMarkerSourceAirport(latLng);
        animateCamera(latLng);
        autoCompleteOriginAirport.setHint(airport.getCode() + " - " + airport.getName() + ", " + airport.getCountry());
        flightsController.flightsRequest(airport.getCode(), "anywhere", "anytime", "anytime", flightsRequestResolvedCallback);
    }

    private void addMarkerSourceAirport(LatLng latLng) {
        if (currentMarker != null)
            currentMarker.remove();
        if (destinationMarker != null)
            destinationMarker.remove();
        if (tripLine != null)
            tripLine.remove();

        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_airport_marker)));

        animateCamera(latLng);
    }

    private void addMarkerDestinationAirport(LatLng latLng) {
        if (destinationMarker != null)
            destinationMarker.remove();
        if (tripLine != null)
            tripLine.remove();

        destinationMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_airport_marker)));

        tripLine = mMap.addPolyline(new PolylineOptions()
                .add(currentMarker.getPosition(), destinationMarker.getPosition())
                .width(10)
                .color(Color.parseColor("#76c2af")));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(currentMarker.getPosition());
        builder.include(destinationMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int padding = 170; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }

    private void animateCamera(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }
}
