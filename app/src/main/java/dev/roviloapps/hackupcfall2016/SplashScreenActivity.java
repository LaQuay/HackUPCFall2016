package dev.roviloapps.hackupcfall2016;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by LaQuay on 08/10/2016.
 */

public class SplashScreenActivity extends Activity implements SplashScreenBackground.LoadingTaskFinishedListener {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    @Override
    public void onTaskFinished() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen_activity);

        new SplashScreenBackground(this, this).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
