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
    private ImageView statusImg;
    private Double lastDistance = 0.0;
    private Double rpmValue = null;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dateList = new ArrayList<>();

    // Balance Sensor
    private ProgressBar xAxisProgressBar, yAxisProgressBar, zAxisProgressBar;
    private TextView xAxisValue, yAxisValue, zAxisValue;

    // Proximity Sensor
    private TextView txtProximity, txtTime;
    private ProgressBar proximityProgressBar;
    private SeekBar seekBar;
    private ImageView imgStatus;

    // Motor Sensor
    private ProgressBar motorProgressBar;
    private TextView textViewMotorSpeed;
    private Button btnEmergencyStop;

    public SensorScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        int orientation = getResources().getConfiguration().orientation;
        View view = inflater.inflate(orientation == Configuration.ORIENTATION_PORTRAIT ? R.layout.sensor_screen : R.layout.sensor_screen_landscape, container, false);

        // Initialize components for each sensor
        initializeDistanceSensor(view);
        initializeBalanceSensor(view);
        initializeProximitySensor(view);
        initializeMotorSensor(view);

        // Set up Firestore document references and listeners for each sensor
        setupDistanceSensor();
        setupBalanceSensor();
        setupProximitySensor();
        setupMotorSensor();

        return view;
    }

    private void initializeDistanceSensor(View view) {
        txtValue = view.findViewById(R.id.txtValue);
        statusImg = view.findViewById(R.id.imgStatus);
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
        proximityProgressBar = view.findViewById(R.id.progressBar);
        imgStatus = view.findViewById(R.id.imgStatus);
        seekBar = view.findViewById(R.id.seekBar);
    }

    private void initializeMotorSensor(View view) {
        motorProgressBar = view.findViewById(R.id.progressBar);
        textViewMotorSpeed = view.findViewById(R.id.textViewMotorSpeed);
        btnEmergencyStop = view.findViewById(R.id.btnEmergencyStop);
    }

    private void setupDistanceSensor() {
        DocumentReference docRef = db.collection("Distance (Ultrasonic Sensor)").document("HChcHZOZwRMcrFVLnar4");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                        txtValue.setText(String.format(Locale.getDefault(), "%.2f", distance));
                        updateStatus(statusImg, distance);
                        lastDistance = distance;
                    } else {
                        txtValue.setText(R.string.no_data);
                    }
                } else {
                    txtValue.setText(R.string.no_data);
                }
            }
        });
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

    private void setupProximitySensor() {
        DocumentReference docRef = db.collection("Proximity (IR Sensor)").document("0NdvEw2YdE4G8BqV6GcI");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    txtProximity.setText(R.string.server_error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    //Retrieving the 'Proximity' and 'Time' fields
                    Number proximity = snapshot.getLong("Proximity");
                    Date time = snapshot.getDate("Time");

                    // Check if success
                    if (proximity != null && time != null) {
                        txtProximity.setText(String.format(Locale.getDefault(), "%dcm", proximity.intValue()));
                        updateProgressBar(proximityProgressBar, proximity.intValue());
                        // Update the ImageView based on the proximity value
                        updateImageViewBasedOnProximity(imgStatus, proximity.intValue());
                        // Compare proximity value with the SeekBar's value and show a Toast if condition is met
                        compareProximityToSliderAndToast(proximity.intValue(), seekBar);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss z", Locale.getDefault());
                        //txtTime.setText(dateFormat.format(time));
                    } else {
                        //If the fields are not found or null
                        txtProximity.setText(R.string.no_data);
                        //txtTime.setText(R.string.no_data);
                    }
                } else {
                    //If document does not exist
                    txtProximity.setText(R.string.no_data);
                    //txtTime.setText(R.string.no_data);
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

    private void updateStatus(ImageView imageView, double distance) {
        boolean isAccelerating = lastDistance != null && distance > lastDistance;

        if (distance < 0.1) {
            imageView.setImageResource(R.mipmap.speed_stop_round);
            addStopTimeToList();
        } else if (distance >= 0.1 && distance <= 1.5) {
            if (rpmValue != null && rpmValue == 100) {
                imageView.setImageResource(R.mipmap.speed_max_round);
            } else {
                imageView.setImageResource(isAccelerating ? R.mipmap.speed_accelerate_round : R.mipmap.speed_slow_round);
            }
        } else if (distance > 1.5) {
            if (rpmValue != null && rpmValue == 100) {
                imageView.setImageResource(R.mipmap.speed_max_round);
            } else {
                imageView.setImageResource(R.mipmap.speed_accelerate_round);
            }
        }
    }

    private void addStopTimeToList() {
        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        dateList.add(currentDateTime);
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(dateList.size() - 1);
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

    private void updateProgressBar(ProgressBar progressBar, int proximityValue) {
        final int maxSensorValue = 20; // Max value from the proximity sensor
        final int maxProgressValue = 20; // Max value for the ProgressBar
        // Invert the proximity value (e.g., 19 becomes 1, 1 becomes 19)
        int invertedValue = maxSensorValue - proximityValue;
        // Now scale the inverted value to fit the ProgressBar scale
        int scaledValue = (invertedValue * (maxProgressValue - 1)) / (maxSensorValue - 1) + 1;
        progressBar.setProgress(scaledValue);
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

    private void compareProximityToSliderAndToast(int proximityValue, SeekBar seekBar) {
        int sliderValue = seekBar.getProgress();
        if (proximityValue < sliderValue) {
            Toast.makeText(getContext(), "Proximity value is smaller than the slider setting.", Toast.LENGTH_SHORT).show();
        }
    }
}
