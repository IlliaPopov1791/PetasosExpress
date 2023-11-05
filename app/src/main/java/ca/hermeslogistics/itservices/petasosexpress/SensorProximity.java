package ca.hermeslogistics.itservices.petasosexpress;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class SensorProximity extends Fragment {

    // TextView for displaying proximity value and timestamp
    private TextView txtProximity, txtTime;

    private FirebaseFirestore db;

    public SensorProximity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.sensor_proximity, container, false);
        } else {
            view = inflater.inflate(R.layout.sensor_proximity_landscape, container, false);
        }

        // Get a reference to the TextViews
        txtProximity = view.findViewById(R.id.textView2);

        /*
        This one is for your list of stops, Dylan

        txtTime = view.findViewById(R.id.textViewTime);
        */


        // Get the document reference from Firestore
        DocumentReference docRef = db.collection("Proximity (IR Sensor)").document("0NdvEw2YdE4G8BqV6GcI");

        // Listen for realtime updates
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
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

        return view;
    }
}
