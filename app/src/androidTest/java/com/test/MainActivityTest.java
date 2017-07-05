package com.test;


import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.TextView;

import com.views.simpleutils.R;
import com.views.ui.customview.RecyclerviewDecorAndDrag.TestAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasHost;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * this Test is designed to test recyclerView
 * dependencies :com.android.support.test.espresso:espresso-contrib:2.2.2<br/>
 * onView(): get the target View
 *  notice:you should keep in mind that make sure one view can be detected in onView()<br/>
 * perform(): to operation
 * RecyclerViewActions.* : get item in recyclerView
 * {@link android.support.test.espresso.matcher.ViewMatchers} 寻找测试View<br/>
 * {@link android.support.test.espresso.action.ViewActions} 发送交互事件<br/>
 * {@link android.support.test.espresso.assertion.ViewAssertions} 检测测试结果<br/>
 * for more see:
 * <a href="http://google.github.io/android-testing-support-library/docs/espresso/index.html"><code>Espresso</code></a>
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<com.views.ui.customview.RecyclerviewDecorAndDrag.MainActivity> mActivityTestRule = new ActivityTestRule<>(com.views.ui.customview.RecyclerviewDecorAndDrag.MainActivity.class);

    //添加延时操作
    private IdlingResource idlingResource;

    //@Before
    public void registerIdlingResources(){
        idlingResource = mActivityTestRule.getActivity();
        if(idlingResource != null)
            Espresso.registerIdlingResources(idlingResource);
    }
    @After
    public void unregisterResources(){
        if(idlingResource != null)
            Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void mainActivityTest() {
//        ViewInteraction appCompatTextView = onView(
//                allOf(withId(R.id.recycle_item_text), withText("12"), isDisplayed()));
//        appCompatTextView.perform(click());

        //recyclerView test
        //usage of holder matcher
        onView(allOf(
                //withParent(withId(R.id.item_loader)),
                is(instanceOf(RecyclerView.class)),
                isDisplayed()))
                .perform(RecyclerViewActions.actionOnHolderItem(getHolderMatcher("1"), click()));
        onView(allOf(
                //withParent(withId(R.id.item_loader)),
                is(instanceOf(RecyclerView.class)),
                isDisplayed()))
                .perform(RecyclerViewActions.scrollToHolder(getHolderMatcher("15")),click());
        //usage of item view matcher
        onView(allOf(
                //withParent(withId(R.id.item_loader)),
                is(instanceOf(RecyclerView.class)),
                isDisplayed()))
                .perform(RecyclerViewActions.actionOnItem(getViewMatcher("1"), click()));
        onView(allOf(
                //withParent(withId(R.id.item_loader)),
                is(instanceOf(RecyclerView.class)),
                isDisplayed()))
                .perform(RecyclerViewActions.scrollTo(getViewMatcher("15")),click());
        //usage of position
        onView(allOf(
                //withParent(withId(R.id.item_loader)),
                is(instanceOf(RecyclerView.class)),
                isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

    }

    private Matcher<TestAdapter.ViewHolder> getHolderMatcher(final String arg){
        return new TypeSafeMatcher<TestAdapter.ViewHolder>() {
            @Override
            protected boolean matchesSafely(TestAdapter.ViewHolder item) {
                return item.getText().equals(arg);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    private Matcher<View> getViewMatcher(final String arg){
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return ((TextView)item.findViewById(R.id.recycle_item_text)).getText().equals(arg);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
