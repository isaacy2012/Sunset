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
public class Streak_cheat_test {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void streak_cheat_test() {
        int desiredStreak = (int) (1000*Math.random());
        ViewInteraction materialButton = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.archiveButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nestedScrollView),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.settingsButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction linearLayout = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout.perform(scrollTo(), click());

        ViewInteraction linearLayout2 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout2.perform(scrollTo(), click());

        ViewInteraction linearLayout3 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout3.perform(scrollTo(), click());

        ViewInteraction linearLayout4 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout4.perform(scrollTo(), click());

        ViewInteraction linearLayout5 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout5.perform(scrollTo(), click());

        ViewInteraction linearLayout6 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout6.perform(scrollTo(), click());

        ViewInteraction linearLayout7 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout7.perform(scrollTo(), click());

        ViewInteraction linearLayout8 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout8.perform(scrollTo(), click());

        ViewInteraction linearLayout9 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout9.perform(scrollTo(), click());

        ViewInteraction linearLayout10 = onView(
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0),
                        2));
        linearLayout10.perform(scrollTo(), click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(String.valueOf(desiredStreak)), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.goBackToArchiveButton),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0)));
        appCompatImageButton3.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.goBackToMainButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.streakCounter), withText(String.valueOf(desiredStreak)),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView.check(matches(withText(String.valueOf(desiredStreak))));

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.floatingActionButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Task"), closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton3.perform(scrollTo(), click());

        ViewInteraction materialCheckBox = onView(
                allOf(withId(R.id.checkBox),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        materialCheckBox.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.streakCounter), withText(String.valueOf(desiredStreak+1)),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView2.check(matches(withText(String.valueOf(desiredStreak+1))));
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
