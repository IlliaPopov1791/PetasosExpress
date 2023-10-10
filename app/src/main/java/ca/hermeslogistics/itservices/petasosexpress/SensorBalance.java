/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */

package ca.hermeslogistics.itservices.petasosexpress;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Random;

public class SensorBalance extends Fragment implements SensorEventListener {
    private ProgressBar xAxisProgressBar, yAxisProgressBar, zAxisProgressBar;
    private TextView xAxisValue, yAxisValue, zAxisValue;
    private Handler handler;
    private Random random;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int orientation = getResources().getConfiguration().orientation;
        View view;
        // Load the appropriate layout based on orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.sensor_balance, container, false);
        } else {
            view = inflater.inflate(R.layout.sensor_balance_landscape, container, false);
        }
        // Determine the device's current orientation


        xAxisProgressBar = view.findViewById(R.id.xAxisProgressBar);
        yAxisProgressBar = view.findViewById(R.id.yAxisProgressBar);
        zAxisProgressBar = view.findViewById(R.id.zAxisProgressBar);

        xAxisValue = view.findViewById(R.id.xAxisValue);
        yAxisValue = view.findViewById(R.id.yAxisValue);
        zAxisValue = view.findViewById(R.id.zAxisValue);

        handler = new Handler();
        random = new Random();

        view.findViewById(R.id.startSimulationButton).setOnClickListener(v -> simulateAxes());

        return view;
    }

    private void simulateAxes() {
        simulateAxis(xAxisProgressBar, xAxisValue);
        simulateAxis(yAxisProgressBar, yAxisValue);
        simulateAxis(zAxisProgressBar, zAxisValue);

        // Call this method again after a short delay for continuous simulation
        handler.postDelayed(this::simulateAxes, 1000); // 1-second delay
    }

    private void simulateAxis(ProgressBar progressBar, TextView valueText) {
        int axisValue = random.nextInt(201) - 100; // Simulating values between -100 and 100

        progressBar.setProgress(axisValue + 100); // Offset by 100 to make it positive for progress bar
        valueText.setText(getString(R.string.value) + axisValue); // Display the value in the TextView
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null); // Stop all callbacks when the fragment is destroyed
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Handle sensor changes if needed
    }
}
