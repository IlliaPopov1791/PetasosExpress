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
import android.widget.Button;
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

    // Constants for shared preferences keys
    protected static final String PREFS_NAME = "UserSettings";
    public static final String THEME_KEY = "theme_mode";
    protected static final String ORIENTATION_KEY = "orientation_mode";
    public static final String ADDRESS_KEY = "default_address";
    protected static final String NOTIFICATIONS_KEY = "notifications";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Determine device orientation
        int orientation = getResources().getConfiguration().orientation;
        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.app_settings, container, false);
        } else {
            view = inflater.inflate(R.layout.app_setttings_landscape, container, false);
        }

        // Initialize UI elements
        ToggleButton toggleTheme = view.findViewById(R.id.toggle_theme);
        ToggleButton toggleOrientation = view.findViewById(R.id.toggle_portrait_landscape);
        ToggleButton toggleNotifications = view.findViewById(R.id.switch_notifications);
        EditText editTextAddress = view.findViewById(R.id.editTextText);
        Button buttonSave = view.findViewById(R.id.buttonSave);

        // Retrieve saved settings and apply them
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        applySavedSettings(getActivity());

        // Retrieve saved values from shared preferences
        boolean isDarkMode = settings.getBoolean(THEME_KEY, false);
        boolean isLandscape = settings.getBoolean(ORIENTATION_KEY, false);
        boolean areNotificationsEnabled = settings.getBoolean(NOTIFICATIONS_KEY, true);
        String savedAddress = settings.getString(ADDRESS_KEY, "");

        // Set UI elements based on saved values
        toggleTheme.setChecked(isDarkMode);
        toggleOrientation.setChecked(isLandscape);
        toggleNotifications.setChecked(areNotificationsEnabled);
        editTextAddress.setText(savedAddress);

        // Theme toggle button click listener
        toggleTheme.setOnClickListener(v -> {
            boolean isChecked = toggleTheme.isChecked();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            DisplayToast(isChecked ? getString(R.string.dark_theme_switched) : getString(R.string.light_theme_switched));
            getActivity().recreate();
            settings.edit().putBoolean(THEME_KEY, isChecked).apply();
        });

        // Orientation toggle button click listener
        toggleOrientation.setOnClickListener(v -> {
            boolean isChecked = toggleOrientation.isChecked();
            getActivity().setRequestedOrientation(isChecked ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DisplayToast(isChecked ? getString(R.string.landscape_orientation) : getString(R.string.portrait_orientation));
            settings.edit().putBoolean(ORIENTATION_KEY, isChecked).apply();
        });

        // Notifications toggle button click listener
        toggleNotifications.setOnClickListener(v -> {
            boolean isEnabled = toggleNotifications.isChecked();
            settings.edit().putBoolean(NOTIFICATIONS_KEY, isEnabled).apply();
            DisplayToast(isEnabled ? "Notifications enabled" : "Notifications disabled");
        });

        // Address text field change listener
        // Save button click listener
        buttonSave.setOnClickListener(v -> {
            String address = editTextAddress.getText().toString();
            settings.edit().putString(ADDRESS_KEY, address).apply();
            DisplayToast("Address saved");
        });

        return view;
    }

    // Display Toast message
    private void DisplayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // Apply saved settings to the activity
    public static void applySavedSettings(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean(THEME_KEY, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        activity.setRequestedOrientation(settings.getBoolean(ORIENTATION_KEY, false) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
