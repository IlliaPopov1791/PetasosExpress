package ca.hermeslogistics.itservices.petasosexpress;


import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;



import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ToggleButton;

import org.junit.Before;
import org.junit.Test;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;

import androidx.fragment.app.testing.FragmentScenario;


@Config(sdk = {28})
@RunWith(RobolectricTestRunner.class)
public class AppSettingsTest {

    private AppSettings fragment;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        fragment = new AppSettings();
        // Start the fragment
        startFragment(fragment);
        sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(AppSettings.PREFS_NAME, 0);
    }

    @Test

    public void testThemeToggle() {
        // This assumes you're using FragmentScenario to set up your fragment
        FragmentScenario<AppSettings> scenario = FragmentScenario.launchInContainer(AppSettings.class);
        scenario.onFragment(fragment -> {
            // Now the fragment is attached, and we have a non-null view
            ToggleButton toggleButton = fragment.getView().findViewById(R.id.toggle_theme);
            toggleButton.performClick();
            // Perform assertions or further actions
        });
    }

    @Test

    public void testOrientationToggle() {
        FragmentScenario<AppSettings> scenario = FragmentScenario.launchInContainer(AppSettings.class);
        scenario.onFragment(fragment -> {
            ToggleButton toggleOrientation = fragment.getView().findViewById(R.id.toggle_portrait_landscape);
            toggleOrientation.performClick();
            assertTrue(sharedPreferences.getBoolean(AppSettings.ORIENTATION_KEY, false));
        });
    }

    @Test
    public void testNotificationsToggle() {
        ToggleButton toggleNotifications = fragment.getView().findViewById(R.id.switch_notifications);
        toggleNotifications.performClick();
        assertFalse(sharedPreferences.getBoolean(AppSettings.NOTIFICATIONS_KEY, true));
    }

    @Test
    public void testAddressChange() {
        EditText editTextAddress = fragment.getView().findViewById(R.id.editTextText);
        editTextAddress.setText("New Address");
        assertEquals("New Address", sharedPreferences.getString(AppSettings.ADDRESS_KEY, ""));
    }

    @Test
    public void testToastMessageOnThemeChange() {
        ToggleButton toggleTheme = fragment.getView().findViewById(R.id.toggle_theme);
        toggleTheme.performClick();
        assertEquals("Dark theme enabled", ShadowToast.getTextOfLatestToast());
    }

    private void startFragment(AppSettings fragment) {
    }
}


