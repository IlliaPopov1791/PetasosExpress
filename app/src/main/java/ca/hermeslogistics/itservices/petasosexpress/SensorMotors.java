package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;

import java.util.Locale;
import java.util.Random;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.annotation.Nullable;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class SensorMotors extends Fragment {

    private ProgressBar progressBar;
    private TextView textViewMotorSpeed;
    private FirebaseFirestore db;
    private DocumentReference motorRef;

    public SensorMotors() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        motorRef = db.collection("Motors").document("PMfVBrNcmgOhjYlQZ4Ih");

        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load the appropriate layout based on orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return inflater.inflate(R.layout.sensor_motors, container, false);
        } else {
            return inflater.inflate(R.layout.sensor_motors_landscape, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the TextView and ProgressBar by ID
        progressBar = view.findViewById(R.id.progressBar);
        textViewMotorSpeed = view.findViewById(R.id.textViewMotorSpeed);
        Button btnEmergencyStop = view.findViewById(R.id.btnEmergencyStop);

        // Apply underline effect to the "Motor Speed" text
        SpannableString content = new SpannableString(getString(R.string.empty_space));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewMotorSpeed.setText(content);

        motorRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    textViewMotorSpeed.setText(R.string.server_error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Double rpm = snapshot.getDouble("rpm");
                    if (rpm != null) {
                        String formattedRPM = String.format(Locale.getDefault(), "%.2f", rpm);
                        progressBar.setProgress((int) Math.round(rpm));progressBar.setProgress((int) Math.round(rpm));
                        textViewMotorSpeed.setText(getString(R.string.rpm_value_placeholder, formattedRPM));
                    } else {
                        textViewMotorSpeed.setText(R.string.no_data);
                    }
                } else {
                    textViewMotorSpeed.setText(R.string.no_data);
                }
            }
        });

        // Set OnClickListener for Emergency Stop button
        btnEmergencyStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMotorSpeed.setText(getString(R.string.motor_speed_value_placeholder, "0"));
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
