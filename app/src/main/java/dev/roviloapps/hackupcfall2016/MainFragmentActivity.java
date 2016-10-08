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

import java.util.ArrayList;

import dev.roviloapps.hackupcfall2016.controllers.AirportController;
import dev.roviloapps.hackupcfall2016.controllers.ForecastController;
import dev.roviloapps.hackupcfall2016.model.Airport;
import dev.roviloapps.hackupcfall2016.model.Forecast;

public class MainFragmentActivity extends Fragment implements ForecastController.ForecastResolvedCallback {
    private static final String TAG = MainFragmentActivity.class.getSimpleName();
    private View rootView;
    private AutoCompleteTextView autoCompleteOriginAirport;

    public static MainFragmentActivity newInstance() {
        return new MainFragmentActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setUpElements();
        setUpListeners();

        ArrayList<Airport> airportArrayList = AirportController.getInstance(getActivity()).getAirports();
        Log.e(TAG, airportArrayList.size() + "");
        ArrayAdapter<Airport> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, airportArrayList);
        autoCompleteOriginAirport.setAdapter(adapter);

        autoCompleteOriginAirport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Airport airportSelected = (Airport) arg0.getAdapter().getItem(arg2);
                Toast.makeText(getActivity(), airportSelected.getCode() + "-" + airportSelected.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        //ForecastController forecastController = new ForecastController(getActivity());
        //forecastController.forecastRequest(35, 139, this);

        return rootView;
    }

    private void setUpElements() {
        autoCompleteOriginAirport = (AutoCompleteTextView) rootView.findViewById(R.id.content_main_origin_autocomplete);
    }

    private void setUpListeners() {

    }

    @Override
    public void onForecastResolved(ArrayList<Forecast> forecastArray) {
        Log.e(TAG, "Forecast on MainActivity :D");
        Log.e(TAG, forecastArray.toString());

        for (int i = 0; i < forecastArray.size(); ++i) {
            Log.e(TAG, forecastArray.get(i).getDate().toString());
        }
    }
}
