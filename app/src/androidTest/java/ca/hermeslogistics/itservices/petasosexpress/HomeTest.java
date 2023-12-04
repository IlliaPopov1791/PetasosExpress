package ca.hermeslogistics.itservices.petasosexpress;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HomeTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testSearchFunctionality() {
        String searchQuery = "Test Search";
        onView(withId(R.id.searchEditText)).perform(typeText(searchQuery), pressImeActionButton());
        onView(withId(R.id.search_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testFoodButton() {
        onView(withId(R.id.foodButton)).perform(click());
        onView(withId(R.id.searchEditText)).check(matches(withText(R.string.food_1)));
        onView(withId(R.id.search_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testElectronicsButton() {
        onView(withId(R.id.electronicsButton)).perform(click());
        onView(withId(R.id.searchEditText)).check(matches(withText(R.string.technology_1)));
        onView(withId(R.id.search_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testMedicineButton() {
        onView(withId(R.id.medicalButton)).perform(click());
        onView(withId(R.id.searchEditText)).check(matches(withText(R.string.medicine)));
        onView(withId(R.id.search_list)).check(matches(isDisplayed()));
    }
    @Test
    public void testOtherButton() {
        onView(withId(R.id.otherButton)).perform(click());
        onView(withId(R.id.searchEditText)).check(matches(withText(R.string.other)));
        onView(withId(R.id.search_list)).check(matches(isDisplayed()));
    }
    @Test
    public void testFabCartButton() {
        onView(withId(R.id.fab_cart)).perform(click());
        onView(withId(R.id.cart_items_list)).check(matches(isDisplayed()));
    }
}
