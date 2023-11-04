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

    // TextView for displaying the value
    private TextView txtValue;

    // Firestore database reference
    private FirebaseFirestore db;

    public SensorDistance() {
        // Required empty public constructor
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

        // Get a reference to the TextView
        txtValue = view.findViewById(R.id.txtValue);

        // Get the document reference from Firestore
        DocumentReference docRef = db.collection("Distance (Ultrasonic Sensor)").document("HChcHZOZwRMcrFVLnar4");

        // Listen for realtime updates
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error - !DO Later!
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
}

