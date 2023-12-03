// Import necessary packages
package ca.hermeslogistics.itservices.petasosexpress;

// Import Android classes and components
import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class SensorScreen extends Fragment {
    private FirebaseFirestore db;

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
    public SensorScreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize Firebase Firestore
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

        // Check and initialize assigned order
        checkAssignedOrder(view);
        // Initialize components for each sensor

        return view;
    }

    // Method to initialize Distance Sensor components
    private void initializeDistanceSensor(View view) {
        listView = view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dateList);
        listView.setAdapter(adapter);
    }

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

    // Method to initialize all sensors
    private void initializeSensors(View view){
        // Initialize components
        initializeDistanceSensor(view);
        initializeBalanceSensor(view);
        initializeProximitySensor(view);
        initializeMotorSensor(view);

        // Set up Firestore document references and listeners for each sensor
        String sensorDocumentPath = "PetasosRecord/" + AssignedPetasos;
        setupSensorListeners(sensorDocumentPath);
    }

    // Method to set up listeners for all sensors
    private void setupSensorListeners(String sensorDocumentPath) {
        DocumentReference sensorDocRef = db.document(sensorDocumentPath);

        // Set up listeners for each sensor
        setupBalanceSensor(sensorDocRef);
        setupMotorSensor(sensorDocRef);
        setupRangeSensors(sensorDocRef);
    }

    // Method to set up listener for Balance Sensor
    private void setupBalanceSensor(DocumentReference sensorDocRef) {
        sensorDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle server error
                    xAxisValue.setText(R.string.server_error);
                    yAxisValue.setText(R.string.server_error);
                    zAxisValue.setText(R.string.server_error);
                    return;
                }

                if ((snapshot != null && snapshot.exists() && isAdded())) {
                    // If snapshot exists, update Balance Sensor values
                    Number xAxis = snapshot.getLong("X-axis");
                    Number yAxis = snapshot.getLong("Y-axis");
                    Number zAxis = snapshot.getLong("Z-axis");

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
        });
    }

    // Method to set up listener for Motor Sensor
    private void setupMotorSensor(DocumentReference sensorDocRef) {
        sensorDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle server error
                    textViewMotorSpeed.setText(R.string.server_error);
                    return;
                }

                if ((snapshot != null && snapshot.exists() && isAdded())) {
                    // If snapshot exists, update Motor Sensor values
                    Double rpm = snapshot.getDouble("rps");
                    if (rpm != null) {
                        String formattedRPM = String.format(Locale.getDefault(), "%.2f", rpm);
                        motorProgressBar.setProgress((int) Math.round(rpm));
                        motorProgressBar.setProgress((int) Math.round(rpm));
                        textViewMotorSpeed.setText(getString(R.string.rpm_value_placeholder, formattedRPM));
                    } else {
                        // If no data, display appropriate message
                        textViewMotorSpeed.setText(R.string.no_data);
                    }
                } else {
                    // If no data, display appropriate message
                    textViewMotorSpeed.setText(R.string.no_data);
                }
            }
        });
    }

    // Method to set up listeners for Range Sensors (Distance and Proximity Sensors)
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

    // Method to update Distance Sensor UI components
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
            // If no data, display appropriate message
            txtProximity.setText(R.string.no_data);
        }
    }

    // Method to update Proximity Sensor UI components
    private void updateProximityUI() {
        if (proximity != null && proximity.intValue() <= 20) {
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
            emergencyStop();
        } else {
            imageView.setImageResource(R.mipmap.status_warning_round);
        }
    }

    // Method to update Distance Sensor image based on distance value
    private void updateImageViewBasedOnDistance(ImageView imageView, double distance) {
        if (distance > 0.4) {
            imageView.setImageResource(R.mipmap.status_good_round);
        } else {
            imageView.setImageResource(R.mipmap.status_warning_round);
        }
    }

    // Method to update progress bar for Proximity Sensor based on proximity value
    private void updateProgressBarOnProx(ProgressBar progressBar, Integer proximityValue) {
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

        private void updateProgressBarOnDis(ProgressBar progressBar, Double distance) {
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


    private void emergencyStop() {
        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        dateList.add(currentDateTime);
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(dateList.size() - 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                if (isAdded()) {
                    sendNotification();
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            if (isAdded()) {
                sendNotification();
            }
        }
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
}
