package com.ae.apps.stickerapp.reviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

/**
 * App class that manages when to trigger the In-App Review flow
 *
 * https://developer.android.com/guide/playcore/in-app-review/kotlin-java
 */
public class AppReview {

    private static final int REQUEST_REVIEW_THRESHOLD = 5;
    private static final String PREF_KEY_APP_LAUNCH_COUNT = "key_review_app_launch_count";
    private static final String PREF_KEY_TIMES_PROMPTED = "key_review_times_prompted";
    private SharedPreferences preferences;
    private int timesPrompted;
    private boolean showReview = false;
    private ReviewManager reviewManager;
    private ReviewInfo reviewInfo;

    public static AppReview sInstance;

    public static AppReview getInstance(){
        if(null == sInstance){
            sInstance = new AppReview();
        }

        return sInstance;
    }

    public void init(final Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int lastOpenCount = preferences.getInt(PREF_KEY_APP_LAUNCH_COUNT, 0);
        timesPrompted = preferences.getInt(PREF_KEY_TIMES_PROMPTED, 0);

        preferences.edit()
                .putInt(PREF_KEY_APP_LAUNCH_COUNT, ++lastOpenCount)
                .apply();

        if(lastOpenCount % REQUEST_REVIEW_THRESHOLD == 0) {
            if(timesPrompted < REQUEST_REVIEW_THRESHOLD || timesPrompted % REQUEST_REVIEW_THRESHOLD == 0){
                showReview = true;
                reviewManager = ReviewManagerFactory.create(context);
                Task<ReviewInfo> request = reviewManager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // We can get the ReviewInfo object
                        reviewInfo = task.getResult();
                    }
                });
            }
        }

    }

    public void launchReviewFlow(final android.app.Activity activity) {
        if(null == preferences){
            throw new IllegalStateException("Call AppReview.init() first");
        }
        preferences.edit()
                .putInt(PREF_KEY_TIMES_PROMPTED, ++timesPrompted)
                .apply();

        if(showReview){
            Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
            flow.addOnCompleteListener(task -> {
                // The flow has finished. The API does not indicate whether the user
                // reviewed or not, or even whether the review dialog was shown. Thus, no
                // matter the result, we continue our app flow.
            });
        }
    }


}
