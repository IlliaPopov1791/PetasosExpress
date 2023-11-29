/*
package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
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
        //Espresso test code for foodButton
        Espresso.onView(withId(R.id.foodButton)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.searchEditText)).check(matches(withText(R.string.food_1)));
    }

}

 */


