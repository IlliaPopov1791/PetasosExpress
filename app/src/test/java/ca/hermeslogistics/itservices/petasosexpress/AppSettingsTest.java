package ca.hermeslogistics.itservices.petasosexpress;

import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class AppSettingsTest {

    private SharedPreferences sharedPreferences;
    private FragmentScenario<AppSettings> scenario;

    @Before
    public void setUp() {
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
