package ca.hermeslogistics.itservices.petasosexpress;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.content.res.Configuration;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_splash);
        } else {
            setContentView(R.layout.splash_landscape);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}
