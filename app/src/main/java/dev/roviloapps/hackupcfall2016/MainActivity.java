package dev.roviloapps.hackupcfall2016;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;

import dev.roviloapps.hackupcfall2016.controllers.AirportController;
import dev.roviloapps.hackupcfall2016.controllers.ForecastController;
import dev.roviloapps.hackupcfall2016.model.Airport;
import dev.roviloapps.hackupcfall2016.model.Forecast;

public class MainActivity extends AppCompatActivity implements ForecastController.Listener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ForecastController forecastController;

    private double LATITUDE = 35;
    private double LONGITUDE = 139;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        forecastController = new ForecastController(getApplicationContext());
        forecastController.setListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forecastController.forecastRequest(LATITUDE, LONGITUDE);

                showSearchLayout();

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
    }

    private void showSearchLayout() {
        final View dialogView = getLayoutInflater().inflate(R.layout.main_activity_dialog_search, null);
        final AutoCompleteTextView dialogAutoComplete = (AutoCompleteTextView) dialogView.findViewById(R.id.main_activity_dialog_search_autocomplete);

        final ArrayList<Airport> airportArrayList = AirportController.getInstance(this).getAirports();

        ArrayAdapter<Airport> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, airportArrayList);
        dialogAutoComplete.setAdapter(adapter);

        dialogAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Airport airportSelected = (Airport) arg0.getAdapter().getItem(arg2);
                Toast.makeText(getBaseContext(), airportSelected.getCode() + "-" + airportSelected.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Search");
        alertDialog.setMessage("Search");
        alertDialog.setView(dialogView);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "GO!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

    @Override
    public void forecastListener(ArrayList<Forecast> forecastArray) {
        Log.e(TAG, "Forecast on MainActivity :D");
        Log.e(TAG, forecastArray.toString());

        for (int i = 0; i < forecastArray.size(); ++i) {
            Log.e(TAG, forecastArray.get(i).getDate().toString());
        }
    }
}
