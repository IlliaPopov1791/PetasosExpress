package ca.hermeslogistics.itservices.petasosexpress;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;

import javax.annotation.Nullable;

public class SensorDistance extends Fragment {
    private TextView txtValue;
    private ImageView statusImg;
    private Double lastDistance = 0.0;
    private Double rpmValue = null;

    // Firestore database reference
    private FirebaseFirestore db;

    public SensorDistance() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        View view;
        // Load the appropriate layout based on orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.sensor_distance, container, false);
        } else {
            view = inflater.inflate(R.layout.sensor_distance_landscape, container, false);
        }

        // Get a reference to UI objects
        txtValue = view.findViewById(R.id.txtValue);
        statusImg = view.findViewById(R.id.imgStatus);
        //Ref for max speed
        DocumentReference rpmRef = db.collection("Motors").document("PMfVBrNcmgOhjYlQZ4Ih");
        rpmRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Double rpm = snapshot.getDouble("rpm");
                    if (rpm != null) {
                        rpmValue = rpm; // Store the RPM for use in distance update logic
                    }
                }
            }
        });
        // Get the document reference from Firestore
        DocumentReference docRef = db.collection("Distance (Ultrasonic Sensor)").document("HChcHZOZwRMcrFVLnar4");

        // Listen for realtime updates
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    txtValue.setText(R.string.server_error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    // Retrieve the 'Pulse Duration' and 'Speed of Sound' fields
                    Double pulseDuration = snapshot.getDouble("Pulse Duration");
                    Double speedOfSound = snapshot.getDouble("Speed of Sound ");

                    // Check if both fields are retrieved successfully
                    if (pulseDuration != null && speedOfSound != null) {
                        // Calculate the distance
                        Double distance = pulseDuration * speedOfSound;

                        txtValue.setText(String.format(Locale.getDefault(), "%.2f", distance));
                        updateStatus(statusImg, distance);
                        lastDistance = distance;
                    } else {
                        //If the fields are not found or null
                        txtValue.setText(R.string.no_data);
                    }
                } else {
                    //If document does not exist
                    txtValue.setText(R.string.no_data);
                }
            }
        });

        return view;
    }
    private void updateStatus(ImageView imageView, double distance) {
        boolean isAccelerating = lastDistance != null && distance > lastDistance;

        if (distance < 0.1) {
            imageView.setImageResource(R.mipmap.speed_stop_round);
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
}

