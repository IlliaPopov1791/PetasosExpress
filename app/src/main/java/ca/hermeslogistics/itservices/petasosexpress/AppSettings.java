package ca.hermeslogistics.itservices.petasosexpress;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
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


        ToggleButton toggleTheme = view.findViewById(R.id.toggle_theme);
        toggleTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleTheme.isChecked()) {
                    // Dark Theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    DisplayToast(getString(R.string.dark_theme_switched));
                } else {
                    // Light Theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    DisplayToast(getString(R.string.light_theme_switched));
                }
                getActivity().recreate();
            }
        });

        // ToggleButton for orientation
        ToggleButton toggleOrientation = view.findViewById(R.id.toggle_portrait_landscape);
        toggleOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleOrientation.isChecked()) {
                    // Landscape
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    DisplayToast(getString(R.string.landscape_orientation));
                } else {
                    // Portrait
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    DisplayToast(getString(R.string.portrait_orientation));
                }
            }
        });

        return view;
    }

    private void DisplayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
