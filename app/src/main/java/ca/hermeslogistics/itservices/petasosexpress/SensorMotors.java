package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SensorMotors extends Fragment {

    public SensorMotors() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor_motors, container, false);

        // TODO: Initialize and control your Sensor Motors here if necessary

        return view;
    }
}
