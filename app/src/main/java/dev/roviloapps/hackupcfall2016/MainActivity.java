package dev.roviloapps.hackupcfall2016;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    Handler handler = new Handler();
    private boolean isDialogFinished;
    Runnable runnableLocation = new Runnable() {
        public void run() {
            if (isDialogFinished) {
                openFragment();
            } else {
                handler.postDelayed(this, 250);
            }
        }
    };

    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
    //.setAction("Action", null).show();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpElements();
        setUpListeners();

        isDialogFinished = false;

        if (savedInstanceState == null) {
            if (checkLocationServiceAvailable()) {
                openFragment();
            } else {
                handler.postDelayed(runnableLocation, 500);
            }
        }
    }

    private void setUpElements() {
    }

    private void setUpListeners() {
    }

    public void openFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, MainFragmentActivity.newInstance());
        ft.commit();
    }

    public boolean checkLocationServiceAvailable() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                } else {
                    // Permission denied, boo!
                    Toast.makeText(this, "Permision denied to Location :(", Toast.LENGTH_SHORT).show();
                }
                isDialogFinished = true;
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
