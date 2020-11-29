package com.singularitycoder.newstime.helper;

public final class AppConstants {

    public static final String BASE_URL = "https://newsapi.org/v2/";
    public static final String NEWS_API_KEY = "c39be54286e4427c9f8cd00f97a96398";
    //    public static final String NEWS_API_KEY = "YOUR_NEWSAPI.ORG_API_KEY";

    public static final String KEY_GET_NEWS_LIST_API_SUCCESS_STATE = "NEWS_LIST";
    public static final String KEY_GET_NEWS_LIST_FROM_SOURCE_API_SUCCESS_STATE = "NEWS_LIST_FROM_SOURCE";
    public static final String KEY_GET_SOURCE_LIST_API_SUCCESS_STATE = "SOURCE_LIST";

    public static final String[] countriesArray = {"in", "jp", "cn", "ru", "us", "gb", "il", "de", "br", "au"};
    public static final String[] countriesArrayAlias = {"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"};
    public static final String[] layoutsArray = {"Standard", "All Details", "Compact", "Fancy", "Only Text", "Only Image", "Only Headlines", "Fancy Headlines", "Image Headlines", "Headlines Plus", "Grid View", "Vertical Swipe"};
    public static final String[] languageArray = {"English", "Hindi", "Japanese", "Chinese"};
    public static final String[] themeArray = {"Light Mode", "Dark Mode"};

    private AppConstants() {
    }
}
