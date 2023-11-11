package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class SensorScreen extends Fragment {
    private FirebaseFirestore db;

    // Distance Sensor
    private TextView txtValue;
    private Double distance = null;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dateList = new ArrayList<>();

    // Balance Sensor
    private ProgressBar xAxisProgressBar, yAxisProgressBar, zAxisProgressBar;
    private TextView xAxisValue, yAxisValue, zAxisValue;

    // Proximity Sensor
    private Number proximity;
    private TextView txtProximity, txtTime;
    private ImageView imgStatus;
    private  ProgressBar progressBar;

    // Motor Sensor
    private ProgressBar motorProgressBar;
    private TextView textViewMotorSpeed;

    public SensorScreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.sensor_screen, container, false);
        } else {
            view = inflater.inflate(R.layout.sensor_screen_landscape, container, false);
        }

        // Initialize components for each sensor
        initializeDistanceSensor(view);
        initializeBalanceSensor(view);
        initializeProximitySensor(view);
        initializeMotorSensor(view);

        // Set up Firestore document references and listeners for each sensor
        //setupDistanceSensor();
        setupBalanceSensor();
        //setupProximitySensor();
        setupMotorSensor();
        setupRangeSensors();


        return view;
    }

    private void initializeDistanceSensor(View view) {
        txtValue = view.findViewById(R.id.txtValue);
        //statusImg = view.findViewById(R.id.imgStatus);
        listView = view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dateList);
        listView.setAdapter(adapter);
    }

    private void initializeBalanceSensor(View view) {
        xAxisProgressBar = view.findViewById(R.id.xAxisProgressBar);
        yAxisProgressBar = view.findViewById(R.id.yAxisProgressBar);
        zAxisProgressBar = view.findViewById(R.id.zAxisProgressBar);
        xAxisValue = view.findViewById(R.id.xAxisValue);
        yAxisValue = view.findViewById(R.id.yAxisValue);
        zAxisValue = view.findViewById(R.id.zAxisValue);
    }

    private void initializeProximitySensor(View view) {
        txtProximity = view.findViewById(R.id.textView2);
        imgStatus = view.findViewById(R.id.imgStatus);
        progressBar = view.findViewById(R.id.proximityProgressBar);
    }

    private void initializeMotorSensor(View view) {
        motorProgressBar = view.findViewById(R.id.motorProgressBar);
        textViewMotorSpeed = view.findViewById(R.id.textViewMotorSpeed);
    }


    private void setupBalanceSensor() {
        DocumentReference docRef = db.collection("Balance").document("vW8usegJdf1HUrA1gFtk");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    xAxisValue.setText(R.string.server_error);
                    yAxisValue.setText(R.string.server_error);
                    zAxisValue.setText(R.string.server_error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Number xAxis = snapshot.getLong("X-axis");
                    Number yAxis = snapshot.getLong("Y-axis");
                    Number zAxis = snapshot.getLong("Z-axis");

                    updateAxis(xAxisProgressBar, xAxisValue, xAxis);
                    updateAxis(yAxisProgressBar, yAxisValue, yAxis);
                    updateAxis(zAxisProgressBar, zAxisValue, zAxis);
                } else {
                    xAxisValue.setText(R.string.no_data);
                    yAxisValue.setText(R.string.no_data);
                    zAxisValue.setText(R.string.no_data);
                }
            }
        });
    }



    private void setupMotorSensor() {
        DocumentReference motorRef = db.collection("Motors").document("PMfVBrNcmgOhjYlQZ4Ih");
        motorRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    textViewMotorSpeed.setText(R.string.server_error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Double rpm = snapshot.getDouble("rpm");
                    if (rpm != null) {
                        String formattedRPM = String.format(Locale.getDefault(), "%.2f", rpm);
                        motorProgressBar.setProgress((int) Math.round(rpm));motorProgressBar.setProgress((int) Math.round(rpm));
                        textViewMotorSpeed.setText(getString(R.string.rpm_value_placeholder, formattedRPM));
                    } else {
                        textViewMotorSpeed.setText(R.string.no_data);
                    }
                } else {
                    textViewMotorSpeed.setText(R.string.no_data);
                }
            }
        });
    }

    //distance and prox
    private void setupRangeSensors() {
        // Document references for both sensors
        DocumentReference distanceDocRef = db.collection("Distance (Ultrasonic Sensor)").document("HChcHZOZwRMcrFVLnar4");
        DocumentReference proximityDocRef = db.collection("Proximity (IR Sensor)").document("0NdvEw2YdE4G8BqV6GcI");

        // Listener for the Distance Sensor
        distanceDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    txtProximity.setText(R.string.server_error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Double pulseDuration = snapshot.getDouble("Pulse Duration");
                    Double speedOfSound = snapshot.getDouble("Speed of Sound ");

                    if (pulseDuration != null && speedOfSound != null) {
                        distance = pulseDuration * speedOfSound;
                        updateDistanceUI();
                    }
                }
            }
        });

        // Listener for the Proximity Sensor
        proximityDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    txtProximity.setText(R.string.server_error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    proximity = snapshot.getLong("Proximity");
                    updateProximityUI();
                } else {
                    txtProximity.setText(String.format(Locale.getDefault(), "%.2f m", distance));
                    updateImageViewBasedOnDistance(imgStatus, distance);
                    updateProgressBarOnDis(progressBar, distance);
                }
            }
        });
    }

    private void updateDistanceUI() {
        if (distance != null) {
            if (distance >= 0.2 && distance <= 4.0) {
                txtProximity.setText(String.format(Locale.getDefault(), "%.2f m", distance));
            } else if (distance > 4.0) {
                txtProximity.setText(R.string.no_obstacles);
            }

            updateImageViewBasedOnDistance(imgStatus, distance);
            updateProgressBarOnDis(progressBar, distance);
        } else {
            txtProximity.setText(R.string.no_data);
        }
    }

    private void updateProximityUI() {
        if (proximity != null && proximity.intValue() <= 20) {
            txtProximity.setText(String.format(Locale.getDefault(), "%d cm", proximity.intValue()));
            updateImageViewBasedOnProximity(imgStatus, proximity.intValue());
            updateProgressBarOnProx(progressBar, proximity.intValue());
        } else {
            updateDistanceUI();
        }
    }


    private void updateAxis(ProgressBar progressBar, TextView valueText, Number axisValue) {
        if (axisValue != null) {
            int value = axisValue.intValue();
            int normalizedProgress = value + 100; // Normalize to 0-200 range
            progressBar.setProgress(normalizedProgress);
            valueText.setText(getString(R.string.placeholder, value));
        } else {
            valueText.setText(R.string.no_data);
        }
    }

    private void updateImageViewBasedOnProximity(ImageView imageView, int proximityValue) {
        if (proximityValue < 10) {
            imageView.setImageResource(R.mipmap.statsu_cancel_round);
            addStopTimeToList();
        } else {
            imageView.setImageResource(R.mipmap.status_warning_round);
        }
    }
    private void updateImageViewBasedOnDistance(ImageView imageView, double distance) {
        if (distance > 0.4) {
            imageView.setImageResource(R.mipmap.status_good_round);
        } else {
            imageView.setImageResource(R.mipmap.status_warning_round);
        }
    }

    private void updateProgressBarOnProx(ProgressBar progressBar, Integer proximityValue){
        final int minProximity = 20; // Min value for proximity to be used in cm
        int progressValue;

         if (proximityValue != null && proximityValue <= minProximity) {
            // Scale based on proximity (20cm to 0cm)
            int invertedProximity = minProximity - proximityValue;
            progressValue = (invertedProximity * 100 / minProximity) + 360; // Offset by the distance covered by 0.21m to 4m
        } else {
            progressValue = 0;
        }

        progressBar.setProgress(progressValue);
    }

    private void updateProgressBarOnDis(ProgressBar progressBar, Double distance){
        final int maxDistance = 400; // Max distance in cm (4 meters)
        int progressValue;

        if (distance != null && distance <= 4.0 && distance >= 0.20) {
            // Convert distance to cm and scale it for the progress bar
            double scaledDistance = (4.0 - distance) * 100; // Convert to cm and invert
            progressValue = (int) (scaledDistance * 400 / maxDistance);
        } else {
            progressValue = 0;
        }
        progressBar.setProgress(progressValue);
    }


    private void addStopTimeToList() {
    String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
    dateList.add(currentDateTime);
    adapter.notifyDataSetChanged();
    listView.smoothScrollToPosition(dateList.size() - 1);
    }

}
