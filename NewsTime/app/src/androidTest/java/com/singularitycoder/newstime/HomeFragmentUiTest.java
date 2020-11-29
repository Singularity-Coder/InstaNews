package com.singularitycoder.newstime;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.singularitycoder.newstime.helper.retrofit.StateMediator;
import com.singularitycoder.newstime.helper.retrofit.UiState;
import com.singularitycoder.newstime.helper.WebViewFragment;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.home.view.HomeFragment;
import com.singularitycoder.newstime.home.viewmodel.NewsViewModel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class HomeFragmentUiTest {

    @NonNull
    private static final String TAG = "MainActivityUiTest";

    @NonNull
    private static final String APP_PACKAGE_NAME = "com.singularitycoder.newstime";

    @Nullable
    private HomeFragment homeFragment;

    @Nullable
    private TextView tvNoInternet, tvNothing;

    @Nullable
    private TextView tvChooseCountry;

    @Nullable
    private IdlingResource idlingResource;

    @Nullable
    private NewsViewModel newsViewModel;

    @Nullable
    private CountingIdlingResource countingIdlingResource;

    @Nullable
    private UiDevice uiDevice;

    @Rule
    public ActivityTestRule<HomeFragment> activityTestRule = new ActivityTestRule<>(HomeFragment.class);

    @Before
    public void setUp() throws Exception {
        homeFragment = activityTestRule.getActivity();

        idlingResource = activityTestRule.getActivity().getWaitingState();
        IdlingRegistry.getInstance().register(idlingResource);

        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        newsViewModel = new ViewModelProvider(activityTestRule.getActivity()).get(NewsViewModel.class);

        tvNoInternet = homeFragment.findViewById(R.id.tv_no_internet);
        tvNothing = homeFragment.findViewById(R.id.tv_nothing);
        tvChooseCountry = homeFragment.findViewById(R.id.tv_choose_country);
    }

    @Test
    public void string_equals_packageNameFromContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals(APP_PACKAGE_NAME, appContext.getPackageName());
    }

    @Test
    public void views_onCreate_notNull() {
        Assert.assertNotNull(tvNoInternet);
    }

    @Test
    public void tvNoInternetView_onCreate_viewGone() {
        // Since internet is not actively listened.
        onView(withId(R.id.tv_no_internet)).check(matches(not(isDisplayed())));
    }

    @Test
    public void tvChooseCountry_onClick_showCountryDialog() {

        getInstrumentation().waitForIdleSync();

        // Click Choose Country TextView
        onView(withId(R.id.tv_choose_country))
                .perform(click());

        // Get title of the dialog on screen
        int titleId = activityTestRule.getActivity().getResources()
                .getIdentifier("android.R.id.alertTitle", "id", "android");

        // Assert Country Dialog
        onView(withId(titleId))
                .inRoot(isDialog())
                .check(matches(withText("Choose Country")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void newsRecyclerView_onCreate_isVisible() {
        // Verify news recycler view is visible on screen
        onView(withId(R.id.recycler_news))
                .check(matches(isDisplayed()));
    }

    @Test
    public void newsRecyclerView_onClickingJustSecondItem() {
        // Just click on 2nd position item
        onView(withId(R.id.recycler_news))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()))
                .check(matches(is(instanceOf(WebViewFragment.class))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void newsRecyclerView_onClickingJustSecondItemWithDataOfTypeNewsArticle() {
        // Click on 2nd position item where data is of type NewsArticle
        onData(allOf(is(instanceOf(NewsItem.NewsArticle.class))))
                .atPosition(1)
                .perform(click());
    }

    @Test
    public void newsRecyclerView_onClickingItemWithText() {
        // Click on recycler view item with text
        onData(allOf(is(instanceOf(String.class)), is("Source")))
                .perform(click());
    }

    @Test
    public void newsRecyclerView_onClickingItemWithTextEndingWith() {
        onView(withId(R.id.recycler_news))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(endsWith(""))), click()));
    }

    @Test
    public void newsRecyclerView_onCreate_scrollToItemWithTextThatEndsWith() {
        onView(withId(R.id.recycler_news))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(endsWith("")))));
    }

    @Test
    public void observer_onCreate_newsList() {
        // check internet label gone
        onView(withId(R.id.tv_no_internet))
                .check(matches(not(isDisplayed())));

        MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData = new MutableLiveData<>();

        // Observe UI state changes
        activityTestRule.getActivity().runOnUiThread(() -> {
            Observer<StateMediator<Object, UiState, String, String>> liveDataObserver = stateMediator -> {

                if (UiState.LOADING == stateMediator.getStatus()) {
                    // loading dialog visible
                    onView(withText("Loading..."))
                            .inRoot(isDialog())
                            .check(matches(isDisplayed()));
                }

                if (UiState.SUCCESS == stateMediator.getStatus()) {
                    // loading dialog gone
                    onView(withText("Loading..."))
                            .inRoot(isDialog())
                            .check(matches(not(isDisplayed())));

                    // check response Toast appeared
                    onView(withText("Got Data!"))
                            .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                            .check(matches(isDisplayed()));
                }

                if (UiState.EMPTY == stateMediator.getStatus()) {
                    // loading dialog gone
                    onView(withText("Loading..."))
                            .inRoot(isDialog())
                            .check(matches(not(isDisplayed())));
                }

                if (UiState.ERROR == stateMediator.getStatus()) {
                    // loading dialog gone
                    onView(withText("Loading..."))
                            .inRoot(isDialog())
                            .check(matches(not(isDisplayed())));
                }
            };

            mutableLiveData.observeForever(liveDataObserver);
        });
    }

    @After
    public void tearDown() throws Exception {
        if (null != idlingResource) IdlingRegistry.getInstance().unregister(idlingResource);
        homeFragment = null;
    }
}