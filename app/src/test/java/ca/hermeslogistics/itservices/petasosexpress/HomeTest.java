/* package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Context;
import android.view.KeyEvent; // Add this import statement

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.hermeslogistics.itservices.petasosexpress.Home;
import ca.hermeslogistics.itservices.petasosexpress.R;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText; // Add this import statement

@RunWith(AndroidJUnit4.class)
public class HomeTest {

    @Rule
    public ActivityTestRule<Home> activityRule = new ActivityTestRule<>(Home.class);

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testSearchEditText() {
        // Your Espresso test code for searchEditText
        Espresso.onView(ViewMatchers.withId(R.id.searchEditText))
                .perform(ViewActions.typeText("Test Search Query"))
                .check(matches(ViewMatchers.withText("Test Search Query")));
    }

    @Test
    public void testFoodButton() {
        // Espresso test code for foodButton
        Espresso.onView(withId(R.id.foodButton)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.searchEditText)).check(matches(withText(R.string.food_1)));
    }




    @Test
    public void testElectronicsButton() {
        //Espresso test code for electronicsButton
        Espresso.onView(withId(R.id.electronicsButton)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.searchEditText)).check(matches(withText(R.string.technology_1)));
    }

    @Test
    public void testMedicalButton() {
        //Espresso test code for medicalButton
        Espresso.onView(withId(R.id.medicalButton)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.searchEditText)).check(matches(withText(R.string.medicine)));
    }

    @Test
    public void testOtherButton() {
        //Espresso test code for otherButton
        Espresso.onView(withId(R.id.otherButton)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.searchEditText)).check(matches(withText(R.string.other)));
    }

    @Test
    public void testHandleSearch() {
    // Espresso test code for handling search
    Espresso.onView(withId(R.id.searchEditText))
            .perform(ViewActions.typeText("Test Search Query"))
            .perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
    }


    @Test
    public void testLoadCartFragment() {
    // Espresso test code for loading cart fragment
    Espresso.onView(withId(R.id.cartButton)).perform(ViewActions.click());
    }
}
*/


