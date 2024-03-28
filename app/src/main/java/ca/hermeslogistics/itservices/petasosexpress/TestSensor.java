// Import necessary packages
package ca.hermeslogistics.itservices.petasosexpress;

// Import Android classes and components
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

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
public class TestSensor extends Fragment {
    private FirebaseFirestore db;
    private DatabaseReference dbRef;

    // Distance Sensor
    private TextView txtValue;
    private Double distance;
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
    private ProgressBar progressBar;

    // Motor Sensor
    private ProgressBar motorProgressBar;
    private TextView textViewMotorSpeed;
    private String AssignedPetasos = "Petasos000";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;

    // Constructor
    public TestSensor() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.test_sensors, container, false);
        } else {
            view = inflater.inflate(R.layout.test_sensors_landscape, container, false);
        }

        // Check and initialize assigned order
        checkAssignedOrder(view);
        // Initialize components for each sensor

        // Initialize the buttons and their listeners
        Button buttonNorth = view.findViewById(R.id.buttonNorth);
        Button buttonSouth = view.findViewById(R.id.buttonSouth);
        Button buttonEast = view.findViewById(R.id.buttonEast);
        Button buttonWest = view.findViewById(R.id.buttonWest);
        Button buttonNorthEast = view.findViewById(R.id.buttonNorthEast);
        Button buttonSouthWest = view.findViewById(R.id.buttonSouthWest);
        Button buttonNorthWest = view.findViewById(R.id.buttonNorthWest);
        Button buttonSouthEast = view.findViewById(R.id.buttonSouthEast);
        ToggleButton buttonToggle = view.findViewById(R.id.buttonToggle);

        // Reference to your direction node in the database
        DatabaseReference directionRef = dbRef.child("Petasos001").child("direction");

        // Set listeners for directional buttons
        setDirectionalButtonListener(buttonNorth, directionRef, "Y", 1);
        setDirectionalButtonListener(buttonSouth, directionRef, "Y", -1);
        setDirectionalButtonListener(buttonEast, directionRef, "X", 1);
        setDirectionalButtonListener(buttonWest, directionRef, "X", -1);

        // Set listeners for combination buttons
        setCombinedButtonListener(buttonNorthEast, directionRef, 1, 1, true);
        setCombinedButtonListener(buttonSouthWest, directionRef, -1, -1, true);
        setCombinedButtonListener(buttonNorthWest, directionRef, -1, 1, true);
        setCombinedButtonListener(buttonSouthEast, directionRef, 1, -1, true);

        // Set listener for the toggle button
        buttonToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            directionRef.child("stop").setValue(isChecked);
        });

        return view;
    }

    /* Method to initialize Distance Sensor components
    private void initializeDistanceSensor(View view) {
        listView = view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dateList);
        listView.setAdapter(adapter);
    }

     */

    // Method to initialize Balance Sensor components
    private void initializeBalanceSensor(View view) {
        xAxisProgressBar = view.findViewById(R.id.xAxisProgressBar);
        yAxisProgressBar = view.findViewById(R.id.yAxisProgressBar);
        zAxisProgressBar = view.findViewById(R.id.zAxisProgressBar);
        xAxisValue = view.findViewById(R.id.xAxisValue);
        yAxisValue = view.findViewById(R.id.yAxisValue);
        zAxisValue = view.findViewById(R.id.zAxisValue);
    }

    // Method to initialize Proximity Sensor components
    private void initializeProximitySensor(View view) {
        txtProximity = view.findViewById(R.id.textView2);
        imgStatus = view.findViewById(R.id.imgStatus);
        progressBar = view.findViewById(R.id.proximityProgressBar);
    }

    // Method to initialize Motor Sensor components
    private void initializeMotorSensor(View view) {
        motorProgressBar = view.findViewById(R.id.motorProgressBar);
        textViewMotorSpeed = view.findViewById(R.id.textViewMotorSpeed);
    }

    private void initializeSensors(View view){
        // Initialize components
        //initializeDistanceSensor(view);
        initializeBalanceSensor(view);
        initializeProximitySensor(view);
        initializeMotorSensor(view);

        // Set up Realtime Database references and listeners for each sensor
        setupSensorListeners();
    }


    // Method to set up listeners for all sensors
    private void setupSensorListeners() {
        DatabaseReference sensorRef = dbRef.child("Petasos001");

        // Set up listeners for each sensor
        setupBalanceSensor(sensorRef);
        setupMotorSensor(sensorRef);
        setupRangeSensors(sensorRef);
    }

    private void setupBalanceSensor(DatabaseReference sensorRef) {
        DatabaseReference balanceSensorRef = sensorRef.child("BalanceSensor");

        balanceSensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && isAdded()) {
                    // If data snapshot exists, update Balance Sensor values
                    Float xAxis = dataSnapshot.child("x").getValue(Float.class);
                    Float yAxis = dataSnapshot.child("y").getValue(Float.class);
                    Float zAxis = dataSnapshot.child("z").getValue(Float.class);

                    updateAxis(xAxisProgressBar, xAxisValue, xAxis);
                    updateAxis(yAxisProgressBar, yAxisValue, yAxis);
                    updateAxis(zAxisProgressBar, zAxisValue, zAxis);
                } else {
                    // If no data, display appropriate message
                    xAxisValue.setText(R.string.no_data);
                    yAxisValue.setText(R.string.no_data);
                    zAxisValue.setText(R.string.no_data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.w(TAG, "loadBalanceSensor:onCancelled", databaseError.toException());
                xAxisValue.setText(R.string.server_error);
                yAxisValue.setText(R.string.server_error);
                zAxisValue.setText(R.string.server_error);
            }
        });
    }


    // Method to set up listener for Motor Sensor and determine the lowest value
    private void setupMotorSensor(DatabaseReference sensorRef) {
        ValueEventListener motorSensorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("MotorSensorFront").exists() && dataSnapshot.child("MotorSensorRear").exists()) {
                    Double motorFrontValue = dataSnapshot.child("MotorSensorFront/value").getValue(Double.class);
                    Double motorRearValue = dataSnapshot.child("MotorSensorRear/value").getValue(Double.class);

                    // Determine the lowest value between the two
                    if (motorFrontValue != null && motorRearValue != null) {
                        double lowestValue = Math.min(motorFrontValue, motorRearValue);

                        // Update UI with the lowest motor speed value
                        updateMotorSpeedUI(lowestValue);
                    } else {
                        // Handle null case or set default
                        updateMotorSpeedUI(0.0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.w(TAG, "loadMotorSensor:onCancelled", databaseError.toException());
            }
        };

        sensorRef.addValueEventListener(motorSensorListener);
    }

    private void updateMotorSpeedUI(double motorSpeed) {
        // Update your UI with the motorSpeed value
        // This can be your ProgressBar and TextView
        motorProgressBar.setProgress((int) motorSpeed);
        String formattedRPM = String.format(Locale.getDefault(), "%.2f", motorSpeed);
        textViewMotorSpeed.setText(getString(R.string.rpm_value_placeholder, formattedRPM));
    }

    /* Method to set up listeners for Range Sensors (Distance and Proximity Sensors) (Old)
    private void setupRangeSensors(DocumentReference sensorDocRef) {
        sensorDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle server error
                    txtProximity.setText(R.string.server_error);
                    return;
                }
                if ((snapshot != null && snapshot.exists())) {
                    // If snapshot exists, update Distance Sensor values
                    Double pulseDuration = snapshot.getDouble("Pulse Duration");
                    Double speedOfSound = snapshot.getDouble("Speed of Sound");

                    // Check if both values are not null before using them
                    if (pulseDuration != null && speedOfSound != null) {
                        distance = pulseDuration * speedOfSound;
                        updateDistanceUI();
                    } else {
                        distance = 0.0;
                        txtProximity.setText(R.string.no_data);
                    }
                }
            }
        });

        // Listener for the Proximity Sensor
        sensorDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle server error
                    txtProximity.setText(R.string.server_error);
                    return;
                }

                if ((snapshot != null && snapshot.exists() && isAdded())) {
                    // If snapshot exists, update Proximity Sensor values
                    proximity = snapshot.getLong("proximity");
                    updateProximityUI();
                } else {
                    // If no data, display appropriate message
                    txtProximity.setText(String.format(Locale.getDefault(), "%.2f m", distance));
                    updateImageViewBasedOnDistance(imgStatus, distance);
                    updateProgressBarOnDis(progressBar, distance);
                }
            }
        });
    }

     */

    private void setupRangeSensors(DatabaseReference sensorRef) {
        DatabaseReference distanceSensorRef = sensorRef.child("DistanceSensor");
        DatabaseReference proximitySensorRef = sensorRef.child("ProximitySensor");

        // Listener for the Distance Sensor
        distanceSensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("distance")) {
                    distance = dataSnapshot.child("distance").getValue(Double.class);
                    if (distance != null) {
                        updateDistanceUI();
                    }
                } else {
                    // Handle case where the distance data is not available
                    txtProximity.setText(R.string.no_data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadDistanceSensor:onCancelled", databaseError.toException());
                txtProximity.setText(R.string.server_error);
            }
        });

        // Listener for the Proximity Sensor
        proximitySensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("prox")) {
                    proximity = dataSnapshot.child("prox").getValue(Double.class);
                    if (proximity != null) {
                        updateProximityUI();
                    }
                } else {
                    // Handle case where the proximity data is not available
                    txtProximity.setText(String.format(Locale.getDefault(), "%.2f m", distance));
                    updateImageViewBasedOnDistance(imgStatus, distance);
                    updateProgressBarOnDis(progressBar, distance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadProximitySensor:onCancelled", databaseError.toException());
                txtProximity.setText(R.string.server_error);
            }
        });
    }

    // Method to update Distance Sensor UI components
    private void updateDistanceUI() {
        if (distance != null) {
            if (distance >= getResources().getInteger(R.integer.min_distance_threshold) / 100.0f && distance <= getResources().getInteger(R.integer.max_distance_threshold) / 100.0f) {
                txtProximity.setText(String.format(Locale.getDefault(), "%.2f m", distance));
            } else if (distance > getResources().getInteger(R.integer.max_distance_threshold) / 100.0f) {
                txtProximity.setText(R.string.no_obstacles);
            }
            updateImageViewBasedOnDistance(imgStatus, distance);
            updateProgressBarOnDis(progressBar, distance);
        } else {
            // If no data, display appropriate message
            txtProximity.setText(R.string.no_data);
        }
    }

    // Method to update Proximity Sensor UI components
    private void updateProximityUI() {
        if (proximity != null && proximity.intValue() <= getResources().getInteger(R.integer.proximity_max_value)) {
            txtProximity.setText(String.format(Locale.getDefault(), "%d cm", proximity.intValue()));
            updateImageViewBasedOnProximity(imgStatus, proximity.intValue());
            updateProgressBarOnProx(progressBar, proximity.intValue());
        } else {
            // If no data or proximity is greater than 20, update Distance UI components
            updateDistanceUI();
        }
    }

    // Method to update Balance Sensor axis values and progress bars
    private void updateAxis(ProgressBar progressBar, TextView valueText, Number axisValue) {
        if (axisValue != null) {
            int value = axisValue.intValue();
            int normalizedProgress = value + 100; // Normalize to 0-200 range
            progressBar.setProgress(normalizedProgress);
            valueText.setText(getString(R.string.placeholder, value));
        } else {
            // If no data, display appropriate message
            valueText.setText(R.string.no_data);
        }
    }

    // Method to update Proximity Sensor image based on proximity value
    private void updateImageViewBasedOnProximity(ImageView imageView, int proximityValue) {
        if (proximityValue < 10) {
            imageView.setImageResource(R.mipmap.statsu_cancel_round);
            //emergencyStop();
        } else {
            imageView.setImageResource(R.mipmap.status_warning_round);
        }
    }

    // Method to update Distance Sensor image based on distance value
    private void updateImageViewBasedOnDistance(ImageView imageView, double distance) {
        if (distance > getResources().getInteger(R.integer.min_distance_threshold) / 100.0f) {
            imageView.setImageResource(R.mipmap.status_good_round);
        } else {
            imageView.setImageResource(R.mipmap.status_warning_round);
        }
    }

    // Method to update progress bar for Proximity Sensor based on proximity value
    private void updateProgressBarOnProx(ProgressBar progressBar, Integer proximityValue) {
        final int minProximity = getResources().getInteger(R.integer.proximity_max_value); // Min value for proximity to be used in cm
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

    private void updateProgressBarOnDis(ProgressBar progressBar, Double distance) {
        final int maxDistance = getResources().getInteger(R.integer.max_distance_threshold); // Max distance in cm (4 meters)
        int progressValue;

        if (distance != null && distance <= getResources().getInteger(R.integer.max_distance_threshold) / 100.0f && distance >= getResources().getInteger(R.integer.min_distance_threshold) / 100.0f) {
            // Convert distance to cm and scale it for the progress bar
            double scaledDistance = (getResources().getInteger(R.integer.max_distance_threshold) / 100.0f - distance) * 100; // Convert to cm and invert
            progressValue = (int) (scaledDistance * getResources().getInteger(R.integer.max_distance_threshold) / maxDistance);
        } else {
            progressValue = 0;
        }
        progressBar.setProgress(progressValue);
    }


    private void sendNotification() {
        String channelId = "delivery_notifications";
        String channelName = "Delivery Notifications";
        String notificationTitle = "Delivery Update";
        String notificationText = "Your delivery may be late due to obstacles on Petasos' way";
        SharedPreferences settings = getActivity().getSharedPreferences(AppSettings.PREFS_NAME, 0);
        boolean areNotificationsEnabled = settings.getBoolean(AppSettings.NOTIFICATIONS_KEY, true);

        if (!areNotificationsEnabled) {
            //Exit if notifications are disabled
            return;
        }
        // Proceed and create the NotificationChannel (required for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, builder.build());
            } else {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            notificationManager.notify(1, builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendNotification();
            } else {
                Toast.makeText(getContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkAssignedOrder(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            Query orderedQuery = db.collection("orderRecord")
                    .whereEqualTo("User", userEmail)
                    .whereEqualTo("status", "ordered")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .limit(1); // Only fetch the oldest one

            orderedQuery.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    //Found the oldest 'ordered' order
                    DocumentSnapshot orderedDocument = task.getResult().getDocuments().get(0);
                    handleOrderedOrder(orderedDocument, view);
                } else {
                    Query inQueueQuery = db.collection("orderRecord")
                            .whereEqualTo("User", userEmail)
                            .whereEqualTo("status", "in a queue");

                    inQueueQuery.get().addOnCompleteListener(queueTask -> {
                        if (queueTask.isSuccessful() && !queueTask.getResult().isEmpty()) {
                            initializeSensors(view);
                            showQueueAlertDialog();
                        } else {
                            //No orders found at all
                            initializeSensors(view);
                            showNoOrderAlertDialog();
                        }
                    });
                }
            });
        }
    }
    private void handleOrderedOrder(DocumentSnapshot document,View view) {
        // Extract Petasos ID from the delivery reference
        DocumentReference deliveryRef = (DocumentReference) document.get("delivery");
        String petasosId = deliveryRef.getId();
        AssignedPetasos = petasosId;
        Toast.makeText(getContext(), "Tracking your oldest order", Toast.LENGTH_LONG).show();
        initializeSensors(view);
    }
    private void showQueueAlertDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.queue_alert_title))
                .setMessage(getString(R.string.queue_alert_message))
                .setPositiveButton(getString(R.string.ok), null)
                .setIcon(R.drawable.ic_cart_foreground)
                .show();
    }

    private void showNoOrderAlertDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.no_order_alert_title))
                .setMessage(getString(R.string.no_order_alert_message))
                .setPositiveButton(getString(R.string.ok), null)
                .setIcon(R.drawable.ic_cart_foreground)
                .show();
    }

    private void setDirectionalButtonListener(Button button, DatabaseReference ref, String axis, int value) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ref.child(axis).setValue(value);
                    break;
                case MotionEvent.ACTION_UP:
                    ref.child(axis).setValue(0);
                    break;
            }
            return true;
        });
    }

    private void setCombinedButtonListener(Button button, DatabaseReference ref, int xValue, int yValue, boolean rotateValue) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ref.child("X").setValue(xValue);
                    ref.child("Y").setValue(yValue);
                    ref.child("rotate").setValue(rotateValue);
                    break;
                case MotionEvent.ACTION_UP:
                    ref.child("X").setValue(0);
                    ref.child("Y").setValue(0);
                    ref.child("rotate").setValue(false);
                    break;
            }
            return true;
        });
    }

}
