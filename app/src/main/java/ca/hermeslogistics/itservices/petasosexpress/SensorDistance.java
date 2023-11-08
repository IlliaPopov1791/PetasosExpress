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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

public class SensorDistance extends Fragment {
    private TextView txtValue;
    private ImageView statusImg;
    private Double lastDistance = 0.0;
    private Double rpmValue = null;
    private FirebaseFirestore db;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dateList = new ArrayList<>();

    public SensorDistance() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        int orientation = getResources().getConfiguration().orientation;
        View view;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.sensor_distance, container, false);
        } else {
            view = inflater.inflate(R.layout.sensor_distance_landscape, container, false);
        }

        txtValue = view.findViewById(R.id.txtValue);
        statusImg = view.findViewById(R.id.imgStatus);
        listView = view.findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dateList);
        listView.setAdapter(adapter);

        DocumentReference rpmRef = db.collection("Motors").document("PMfVBrNcmgOhjYlQZ4Ih");
        rpmRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Double rpm = snapshot.getDouble("rpm");
                    if (rpm != null) {
                        rpmValue = rpm;
                    }
                }
            }
        });

        DocumentReference docRef = db.collection("Distance (Ultrasonic Sensor)").document("HChcHZOZwRMcrFVLnar4");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
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

        return view;
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
}


