package com.ae.apps.stickerapp.analytics;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.ae.apps.stickerapp.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;

/**
 * A basic analytics component
 */
public class Analytics {
    private static Analytics sInstance;
    private static FirebaseAnalytics firebaseAnalytics;

    public static final String VIEW_ITEM = "view_item";
    private static final String APP_START = "app_start";

    private String PARAM_NAME_START_TIME;
    private String PARAM_NAME_APP_VERSION;
    private String PARAM_VALUE_APP_VERSION;

    private Analytics() {
    }

    private Analytics(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        initConstants(context);
    }

    public static Analytics getInstance(final Context context) {
        if (null == sInstance) {
            sInstance = new Analytics(context);
        }
        return sInstance;
    }

    private void initConstants(Context context) {
        Resources resources = context.getResources();
        PARAM_NAME_START_TIME = resources.getString(R.string.analytics_key_start_time);
        PARAM_NAME_APP_VERSION = resources.getString(R.string.analytics_key_app_version);
        PARAM_VALUE_APP_VERSION = resources.getString(R.string.app_version);
    }

    public void logAppStart() {
        Calendar calendar = Calendar.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_NAME_START_TIME, calendar.getTime().toString());
        bundle.putString(PARAM_NAME_APP_VERSION, PARAM_VALUE_APP_VERSION);
        logEvent(APP_START, bundle);
    }

    public void logEvent(final String eventType, final Bundle bundle) {
        firebaseAnalytics.logEvent(eventType, bundle);
    }

    public void logEvent(final String eventType, final String paramName, String paramValue) {
        Bundle bundle = new Bundle();
        bundle.putString(paramName, paramValue);
        logEvent(eventType, bundle);
    }
}
