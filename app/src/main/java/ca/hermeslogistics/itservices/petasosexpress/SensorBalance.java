/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class SensorBalance extends Fragment implements SensorEventListener {

    private View circleX, circleY, circleZ;
    private Random random;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor_balance, container, false);

        circleX = view.findViewById(R.id.circleX);
        circleY = view.findViewById(R.id.circleY);
        circleZ = view.findViewById(R.id.circleZ);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        random = new Random();
    }

    @Override
    public void onResume() {
        super.onResume();
        simulateSensorChanges();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void simulateSensorChanges() {
        // Generate random values between -100 and 100 for each axis
        float x = random.nextFloat() * 200 - 100;
        float y = random.nextFloat() * 200 - 100;
        float z = random.nextFloat() * 200 - 100;

        Log.d("SimulatedMagnetometer", "X: " + x + ", Y: " + y + ", Z: " + z);

        updateCircleColor(circleX, x);
        updateCircleColor(circleY, y);
        updateCircleColor(circleZ, z);

        // Call this method again after a short delay to continuously update the circles
        circleX.postDelayed(this::simulateSensorChanges, 1000); // 1 second delay
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // No real sensor reading, using simulated values
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    private void updateCircleColor(View circle, float value) {
        GradientDrawable drawable = (GradientDrawable) circle.getBackground();

        int colorValue = (int) (Math.abs(value) * 2);  // Using absolute value and multiplying for emphasis
        colorValue = Math.min(255, Math.max(0, colorValue)); // Clamping to [0, 255]

        drawable.setColor(Color.rgb(colorValue, colorValue, colorValue));
    }
}
