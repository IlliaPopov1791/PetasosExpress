package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import java.util.Random;

public class SensorMotors extends Fragment {

    private ProgressBar progressBar;
    private TextView textViewMotorSpeed;
    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());

    public SensorMotors() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return inflater.inflate(R.layout.sensor_motors, container, false);
        } else {
            return inflater.inflate(R.layout.sensor_motors_landscape, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the TextView and ProgressBar by ID
        progressBar = view.findViewById(R.id.progressBar);
        textViewMotorSpeed = view.findViewById(R.id.textViewMotorSpeed);
        Button btnEmergencyStop = view.findViewById(R.id.btnEmergencyStop);

        // Apply underline effect to the "Motor Speed" text
        SpannableString content = new SpannableString(getString(R.string.empty_space));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewMotorSpeed.setText(content);

        // Start updating ProgressBar and TextView with random values
        startUpdatingValues();

        // Set OnClickListener for Emergency Stop button
        btnEmergencyStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop updating values and set motor speed to 0.00 km/h
                handler.removeCallbacksAndMessages(null);
                progressBar.setProgress(0);
                textViewMotorSpeed.setText(getString(R.string.motor_speed_value_placeholder, "0.00"));
            }
        });

        // TODO: Initialize and control your Sensor Motors here if necessary
    }

    private void startUpdatingValues() {
        // Update values every second (1000 milliseconds)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int randomValue = random.nextInt(20) + 1; // Generate random number between 1 and 20
                // Convert random value to km/h
                double speedInKmPerHour = randomValue * 1.0; // Assuming a conversion factor
                // Update ProgressBar and TextView with the random value and "km/h"
                progressBar.setProgress(randomValue);
                textViewMotorSpeed.setText(String.format(getString(R.string._2f_km_h), speedInKmPerHour));
                // Call the method again after 1000 milliseconds
                startUpdatingValues();
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        // Remove callbacks to prevent memory leaks when the fragment is destroyed
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
