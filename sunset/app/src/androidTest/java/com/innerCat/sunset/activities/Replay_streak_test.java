package com.innerCat.sunset.activities;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.innerCat.sunset.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Replay_streak_test {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void replay_streak_test() {
        ViewInteraction materialButton = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton.perform(scrollTo(), click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.floatingActionButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Today Task"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction materialCheckBox = onView(
                allOf(withId(R.id.checkBox),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        materialCheckBox.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.streakCounter), withText("1"),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.archiveButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nestedScrollView),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.nameView), withText("Today Task"),
                        withParent(withParent(withId(R.id.archiveLinearLayout))),
                        isDisplayed()));
        textView2.check(matches(withText("Today Task")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.replayTextView), withText("Completed today"),
                        withParent(withParent(withId(R.id.archiveLinearLayout))),
                        isDisplayed()));
        textView3.check(matches(withText("Completed today")));

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.replayButton),
                        childAtPosition(
                                allOf(withId(R.id.archiveLinearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.cardview.widget.CardView")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.goBackToMainButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.streakCounter), withText("0"),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView4.check(matches(withText("0")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.nameView), withText("Today Task"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        textView5.check(matches(withText("Today Task")));

        ViewInteraction materialCheckBox2 = onView(
                allOf(withId(R.id.checkBox),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        materialCheckBox2.perform(click());

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.streakCounter), withText("1"),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView6.check(matches(withText("1")));

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.archiveButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nestedScrollView),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.nameView), withText("Today Task"),
                        withParent(withParent(withId(R.id.archiveLinearLayout))),
                        isDisplayed()));
        textView7.check(matches(withText("Today Task")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position ) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo( Description description ) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely( View view ) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
