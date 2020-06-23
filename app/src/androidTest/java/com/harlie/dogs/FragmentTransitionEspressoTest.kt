package com.harlie.dogs

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.harlie.dogs.ViewSynchronizer.viewExists
import com.harlie.dogs.view.DogsListAdapter
import com.harlie.dogs.view.MainActivity
import org.hamcrest.CoreMatchers.allOf
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class FragmentTransitionEspressoTest {

    @get:Rule var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        System.out.println("setup")
        waitForViewToAppear()
    }

    @Test
    fun checkAndClickListItems() {
        System.out.println("checkAndClickListItems")

        var position = 5
        onView(withId(R.id.dogsList))
            .perform(RecyclerViewActions.scrollToPosition<DogsListAdapter.DogViewHolder>(position))
            .noActivity()
        waitForViewToAppear()
        onView(withId(R.id.dogsList))
            .perform(RecyclerViewActions.scrollToPosition<DogsListAdapter.DogViewHolder>(position))
            .check(matches(hasDescendant(withText("Akita")))) // position 5
            .perform(RecyclerViewActions.actionOnItemAtPosition<DogsListAdapter.DogViewHolder>(position, click()))
        onView(withId(R.id.dogDetails))
            .check(matches(hasDescendant(withText("Hunting bears"))))
            .check(matches(hasDescendant(withText("10 - 14 years"))))
        slowDownSoWeCanSeeTheUI()
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        waitForViewToAppear()

        position = 7
        onView(withId(R.id.dogsList))
            .perform(RecyclerViewActions.scrollToPosition<DogsListAdapter.DogViewHolder>(position))
            .noActivity()
        waitForViewToAppear()
        onView(withId(R.id.dogsList))
            .perform(RecyclerViewActions.scrollToPosition<DogsListAdapter.DogViewHolder>(position))
            .check(matches(hasDescendant(withText("Alaskan Husky")))) // position 7
            .perform(RecyclerViewActions.actionOnItemAtPosition<DogsListAdapter.DogViewHolder>(position, click()))
        onView(withId(R.id.dogDetails))
            .check(matches(hasDescendant(withText("Sled pulling"))))
            .check(matches(hasDescendant(withText("10 - 13 years"))))
        slowDownSoWeCanSeeTheUI()
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        waitForViewToAppear()

        // NOTE: the 124th dogsList item (counting from 0) has id=188 for Pharaoh Hound
        position = 123
        onView(withId(R.id.dogsList))
            .perform(RecyclerViewActions.scrollToPosition<DogsListAdapter.DogViewHolder>(position))
            .noActivity()
        waitForViewToAppear()
        onView(withId(R.id.dogsList))
            .perform(RecyclerViewActions.scrollToPosition<DogsListAdapter.DogViewHolder>(position))
            .check(matches(hasDescendant(withText("Pharaoh Hound"))))
            .perform(RecyclerViewActions.actionOnItemAtPosition<DogsListAdapter.DogViewHolder>(position, click()))
        onView(withId(R.id.dogDetails))
            .check(matches(hasDescendant(withText("Hunting rabbits"))))
            .check(matches(hasDescendant(withText("12 - 14 years"))))
        slowDownSoWeCanSeeTheUI()
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        waitForViewToAppear()

        val recyclerView: RecyclerView = activityTestRule.getActivity().findViewById(R.id.dogsList)
        val itemCount = recyclerView.adapter!!.itemCount
        position = if (itemCount > 0) itemCount - 1 else 0
        System.out.println("last position=${position}")

        onView(withId(R.id.dogsList))
            .perform(RecyclerViewActions.scrollToPosition<DogsListAdapter.DogViewHolder>(position))
            .noActivity()
        waitForViewToAppear()
        onView(withId(R.id.dogsList))
            .perform(RecyclerViewActions.scrollToPosition<DogsListAdapter.DogViewHolder>(position))
            .check(matches(hasDescendant(withText("Yorkshire Terrier")))) // last position
            .perform(RecyclerViewActions.actionOnItemAtPosition<DogsListAdapter.DogViewHolder>(position, click()))
        onView(withId(R.id.dogDetails))
            .check(matches(hasDescendant(withText("Small vermin hunting"))))
            .check(matches(hasDescendant(withText("12 - 16 years"))))
        slowDownSoWeCanSeeTheUI()
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
    }

    @After
    fun teardown() {
        System.out.println("teardown")
    }

    private fun waitForViewToAppear() {
        System.out.println("waitForViewToAppear")
        Assert.assertTrue(
            viewExists(
                allOf(
                    withId(R.id.dogsList), withEffectiveVisibility(
                        Visibility.VISIBLE
                    )
                ), 10000
            )
        );
        slowDownSoWeCanSeeTheUI()
    }

    private fun slowDownSoWeCanSeeTheUI() {
        System.out.println("slowDownSoWeCanSeeTheUI")
        Thread.sleep(5000)
    }

}