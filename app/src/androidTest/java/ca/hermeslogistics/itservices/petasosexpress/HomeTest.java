package ca.hermeslogistics.itservices.petasosexpress;

import static junit.framework.TestCase.assertNotNull;

import android.app.Activity;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

@RunWith(AndroidJUnit4.class)
public class HomeTest {
    @Test
    public void testHomeScreenLayoutLoaded() {
        FragmentScenario<Home> scenario = FragmentScenario.launchInContainer(Home.class);
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getView().findViewById(R.id.main_home));
            assertNotNull(fragment.getView().findViewById(R.id.foodButton));
            assertNotNull(fragment.getView().findViewById(R.id.electronicsButton));
            assertNotNull(fragment.getView().findViewById(R.id.medicalButton));
            assertNotNull(fragment.getView().findViewById(R.id.otherButton));
            assertNotNull(fragment.getView().findViewById(R.id.searchEditText));
            assertNotNull(fragment.getView().findViewById(R.id.fab_cart));
        });
    }


}
