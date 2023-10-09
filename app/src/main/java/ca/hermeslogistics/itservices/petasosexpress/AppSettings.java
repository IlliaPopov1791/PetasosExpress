package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.res.Configuration;
import androidx.fragment.app.Fragment;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class AppSettings extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;
        View view;
        // Load the appropriate layout based on orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.app_settings, container, false);
        } else {
            view = inflater.inflate(R.layout.app_setttings_landscape, container, false);
        }

        // Initialize the spinner
        Spinner citiesSpinner = view.findViewById(R.id.spinner_cities);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.ontario_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(adapter);

        return view;
    }

}
