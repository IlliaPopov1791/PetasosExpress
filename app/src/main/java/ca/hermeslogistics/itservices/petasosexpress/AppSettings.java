
package ca.hermeslogistics.itservices.petasosexpress;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class AppSettings extends Fragment {

    public static void applySavedSettings(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);

        boolean isDarkMode = settings.getBoolean(THEME_KEY, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        boolean isLandscape = settings.getBoolean(ORIENTATION_KEY, false);
        if (isLandscape) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    // Constants for SharedPreferences keys
    private static final String PREFS_NAME = "UserSettings";
    private static final String THEME_KEY = "theme_mode";
    private static final String ORIENTATION_KEY = "orientation_mode";
    private static final String ADDRESS_KEY = "default_address";

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

        // Initialize ToggleButtons and EditText
        ToggleButton toggleTheme = view.findViewById(R.id.toggle_theme);
        ToggleButton toggleOrientation = view.findViewById(R.id.toggle_portrait_landscape);
        EditText editTextAddress = view.findViewById(R.id.editTextText);

        // Retrieve saved settings
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        applySavedSettings(getActivity());

        boolean isDarkMode = settings.getBoolean(THEME_KEY, false);
        boolean isLandscape = settings.getBoolean(ORIENTATION_KEY, false);
        String savedAddress = settings.getString(ADDRESS_KEY, "Address"); // Default value is "Address"

        // Set saved values
        toggleTheme.setChecked(isDarkMode);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        toggleOrientation.setChecked(isLandscape);
        if (isLandscape) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        editTextAddress.setText(savedAddress);

        // Set onClickListeners for the ToggleButtons
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

                // Save the theme setting
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(THEME_KEY, toggleTheme.isChecked());
                editor.apply();
            }
        });

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

                // Save the orientation setting
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(ORIENTATION_KEY, toggleOrientation.isChecked());
                editor.apply();
            }
        });

        // Save address when it changes
        editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(ADDRESS_KEY, s.toString());
                editor.apply();
            }
        });

        return view;
    }

    private void DisplayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
