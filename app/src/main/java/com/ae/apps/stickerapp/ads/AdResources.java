package com.ae.apps.stickerapp.ads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.ae.apps.stickerapp.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * Implemented in below files
 * See {@link com.ae.apps.stickerapp.StickerPackListAdapter}
 * See {@link com.ae.apps.stickerapp.StickerPackListActivity}
 */
public class AdResources {

    private InterstitialAd interstitialAd;

    private boolean isAdLoading = false;

    public AdResources() {
        // no-arg constructor
    }

    public void initInterstitial(final Context context, final AdLoadedCallback callback) {
        String testUnitAd = "ca-app-pub-3940256099942544/1033173712";
        String adUnitId = context.getResources().getString(R.string.google_admob_interstitial_id);

        if(isAdLoading || null != interstitialAd){
            return;
        }

        // Time to request a new Ad
        isAdLoading = true;
        AdRequest adRequest = newAdRequest();

        // Load an interstitial ad
        InterstitialAd.load(context, adUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        // The interstitialAd reference will be null until an ad is loaded.
                        interstitialAd = ad;
                        isAdLoading = false;
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        callback.onAdDismissedFullScreenContent();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        callback.onAdFailedToShowFullScreenContent(adError);
                                        interstitialAd = null;
                                    }
                                }
                        );
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // TODO Handle the error
                        interstitialAd = null;
                    }
                });
    }

    public AdRequest newAdRequest() {
        return new AdRequest.Builder().build();
    }

    static final AdError NO_AD_ERROR = new AdError(2024, "No ad to show", "sticker.app");
    public void showInterstitialAd(final Activity context) {
        if (null != interstitialAd) {
            interstitialAd.show(context);
        } else {
            // Get a new ad and then show it
            final AdLoadedCallback callback = (AdLoadedCallback) context;
            initInterstitial(context, callback);

            callback.onAdFailedToShowFullScreenContent(NO_AD_ERROR);
        }
    }

}
