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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class SensorBalance extends Fragment {
    private ProgressBar xAxisProgressBar, yAxisProgressBar, zAxisProgressBar;
    private TextView xAxisValue, yAxisValue, zAxisValue;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        int orientation = getResources().getConfiguration().orientation;
        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.sensor_balance, container, false);
        } else {
            view = inflater.inflate(R.layout.sensor_balance_landscape, container, false);
        }

        xAxisProgressBar = view.findViewById(R.id.xAxisProgressBar);
        yAxisProgressBar = view.findViewById(R.id.yAxisProgressBar);
        zAxisProgressBar = view.findViewById(R.id.zAxisProgressBar);

        xAxisValue = view.findViewById(R.id.xAxisValue);
        yAxisValue = view.findViewById(R.id.yAxisValue);
        zAxisValue = view.findViewById(R.id.zAxisValue);

        DocumentReference docRef = db.collection("Balance").document("vW8usegJdf1HUrA1gFtk");

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
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

        return view;
    }

    private void updateAxis(ProgressBar progressBar, TextView valueText, Number axisValue) {
        if (axisValue != null) {
            int value = axisValue.intValue();
            progressBar.setProgress(value + 100);
            valueText.setText(getString(R.string.placeholder, value));
        } else {
            valueText.setText(R.string.no_data);
        }
    }
}
