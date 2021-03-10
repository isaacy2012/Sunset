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
public class Add_two_test {
    /*
        This test verifies that when adding two items, completing the first:
        - The first item moves to the second as it as completed
        - The streak does not change
        Then, when completing the first item (previously second):
        - The streak changes
     */

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void add_two_test() {
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
        appCompatEditText.perform(replaceText("Task 1"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.floatingActionButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Task 2"), closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton3.perform(scrollTo(), click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.messageTextView), withText("You have 2 tasks remaining today"),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView.check(matches(withText("You have 2 tasks remaining today")));

        //getting checkbox
        ViewInteraction materialCheckBox = onView(
                allOf(withId(R.id.checkBox),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                withId(R.id.rvTasks),
                                                0), //the first task inside rvTasks
                                        0),
                                2), //checkBox position inside linearLayout
                        isDisplayed()));
        materialCheckBox.perform(click());

        //checking the second one is task 2
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.nameView), withText("Task 2"),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                withId(R.id.rvTasks),
                                                1), //position inside rvTasks (1 is the second)
                                        0),
                                1), //nameView position inside linearLayout
                        isDisplayed()));
        textView2.check(matches(withText("Task 2")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.messageTextView), withText("You have 1 task remaining today"),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView3.check(matches(withText("You have 1 task remaining today")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.streakCounter), withText("0"),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView4.check(matches(withText("0")));

        ViewInteraction materialCheckBox2 = onView(
                allOf(withId(R.id.checkBox),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                withId(R.id.rvTasks),
                                                0),
                                        0),
                                2),
                        isDisplayed()));
        materialCheckBox2.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.messageTextView), withText("Well Done! All tasks completed today."),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView5.check(matches(withText("Well Done! All tasks completed today.")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.streakCounter), withText("1"),
                        withParent(withParent(withId(R.id.nestedScrollView))),
                        isDisplayed()));
        textView6.check(matches(withText("1")));
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
