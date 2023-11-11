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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dateList = new ArrayList<>();

    // Balance Sensor
    private ProgressBar xAxisProgressBar, yAxisProgressBar, zAxisProgressBar;
    private TextView xAxisValue, yAxisValue, zAxisValue;

    // Proximity Sensor
    private TextView txtProximity, txtTime;
    private ImageView imgStatus;
    private  ProgressBar progressBar;

    // Motor Sensor
    private ProgressBar motorProgressBar;
    private TextView textViewMotorSpeed;

    public SensorScreen() {
        // Required empty public constructor
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
        setupSensors();


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
    private void setupSensors() {
        // Document references for both sensors
        DocumentReference distanceDocRef = db.collection("Distance (Ultrasonic Sensor)").document("HChcHZOZwRMcrFVLnar4");
        DocumentReference proximityDocRef = db.collection("Proximity (IR Sensor)").document("0NdvEw2YdE4G8BqV6GcI");

        // Listener for the Distance Sensor
        distanceDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    txtValue.setText(R.string.server_error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Double pulseDuration = snapshot.getDouble("Pulse Duration");
                    Double speedOfSound = snapshot.getDouble("Speed of Sound ");

                    if (pulseDuration != null && speedOfSound != null) {
                        Double distance = pulseDuration * speedOfSound;
                        if (distance > 20) {
                            txtProximity.setText(String.format(Locale.getDefault(), "%.2f", distance));
                        }
                    } else {
                        txtValue.setText(R.string.no_data);
                    }
                } else {
                    txtValue.setText(R.string.no_data);
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
                    Number proximity = snapshot.getLong("Proximity");

                    if (proximity != null && proximity.intValue() < 20) {
                        txtProximity.setText(String.format(Locale.getDefault(), "%dcm", proximity.intValue()));
                        updateImageViewBasedOnProximity(imgStatus, proximity.intValue());
                        updateProgressBar(progressBar, proximity.intValue());
                    } else {
                        txtProximity.setText(R.string.no_data);
                    }
                } else {
                    txtProximity.setText(R.string.no_data);
                }
            }
        });
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
        if (proximityValue < 5) {
            // If the proximity is less than 5, set the image source to drawable_close
            imageView.setImageResource(R.mipmap.statsu_cancel_round);
        } else if (proximityValue > 15) {
            // If the proximity is greater than 15, set the image source to drawable_far
            imageView.setImageResource(R.mipmap.status_good_round);
        } else {
            // Otherwise, set the image source to drawable_medium
            imageView.setImageResource(R.mipmap.status_warning_round);
        }
    }

    private void updateProgressBar(ProgressBar progressBar, int proximityValue) {
        final int maxSensorValue = 20; // Max value from the proximity sensor
        final int maxProgressValue = 20; // Max value for the ProgressBar
        // Invert the proximity value (e.g., 19 becomes 1, 1 becomes 19)
        int invertedValue = maxSensorValue - proximityValue;
        // Now scale the inverted value to fit the ProgressBar scale
        int scaledValue = (invertedValue * (maxProgressValue - 1)) / (maxSensorValue - 1) + 1;
        progressBar.setProgress(scaledValue);
    }


}
