package ca.hermeslogistics.itservices.petasosexpress;

<<<<<<< HEAD
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
=======

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


>>>>>>> f51349f8bba04d77c90b8883e2292c57ae9e4194

import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ToggleButton;

<<<<<<< HEAD
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class AppSettingsTest {

    private SharedPreferences sharedPreferences;
    private FragmentScenario<AppSettings> scenario;
    private AppSettings fragment;
    @Before
    public void setUp() {
        sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(AppSettings.PREFS_NAME, 0);
        sharedPreferences.edit().clear().commit();
        sharedPreferences.edit().clear().commit();
        sharedPreferences = ApplicationProvider.getApplicationContext().getSharedPreferences(AppSettings.PREFS_NAME, 0);
        sharedPreferences.edit().clear().commit();
        scenario = FragmentScenario.launchInContainer(AppSettings.class);
    }

    @Test
    public void testThemeToggleButton() {
        scenario.onFragment(fragment -> {
            ToggleButton toggleTheme = fragment.getView().findViewById(R.id.toggle_theme);
            toggleTheme.performClick();

            boolean isDarkMode = sharedPreferences.getBoolean(AppSettings.THEME_KEY, false);
            assertTrue(isDarkMode);
        });
    }

    @Test
    public void testOrientationToggleButton() {
        scenario.onFragment(fragment -> {
            ToggleButton toggleOrientation = fragment.getView().findViewById(R.id.toggle_portrait_landscape);
            toggleOrientation.performClick();

            boolean isLandscape = sharedPreferences.getBoolean(AppSettings.ORIENTATION_KEY, false);
            assertTrue(isLandscape);
        });
    }

    @Test
    public void testNotificationsToggleButton() {
        scenario.onFragment(fragment -> {
            ToggleButton toggleNotifications = fragment.getView().findViewById(R.id.switch_notifications);
            toggleNotifications.performClick();

            boolean areNotificationsEnabled = sharedPreferences.getBoolean(AppSettings.NOTIFICATIONS_KEY, false);
            assertTrue(areNotificationsEnabled);
        });
    }

    @Test
    public void testAddressTextEdit() {
        scenario.onFragment(fragment -> {
            EditText editTextAddress = fragment.getView().findViewById(R.id.editTextText);
            editTextAddress.setText("123 Test Street");

            String savedAddress = sharedPreferences.getString(AppSettings.ADDRESS_KEY, "");
            assertEquals("123 Test Street", savedAddress);
        });
    }

    @Test
    public void testApplySavedSettings() {
        sharedPreferences.edit().putBoolean(AppSettings.THEME_KEY, true).commit();
        sharedPreferences.edit().putBoolean(AppSettings.ORIENTATION_KEY, true).commit();

        AppSettings.applySavedSettings(ApplicationProvider.getApplicationContext());

        assertTrue(sharedPreferences.getBoolean(AppSettings.THEME_KEY, false));
        assertTrue(sharedPreferences.getBoolean(AppSettings.ORIENTATION_KEY, false));
    }
}
=======
import org.junit.Before;
import org.junit.Test;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;

import androidx.fragment.app.testing.FragmentScenario;


@Config(sdk = {28}) // Use the appropriate SDK version for your project
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
            // Code to start the fragment using Robolectric
        }
    }


>>>>>>> f51349f8bba04d77c90b8883e2292c57ae9e4194
