package com.ae.apps.stickerapp.ads;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ae.apps.stickerapp.R;
import com.google.android.gms.ads.AdRequest;
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

    public AdResources(){

    }

    void  getInterstitial(final Context context, final AdLoadedCallback callback){
        String testUnitAd = "ca-app-pub-3940256099942544/1033173712";
        String adUnitId = context.getResources().getString(R.string.google_admob_interstitial_id);

        if(null == interstitialAd){
            AdRequest adRequest = newAdRequest();

            InterstitialAd.load(context,adUnitId, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd ad) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            interstitialAd = ad;
                            callback.onAdLoaded(ad);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            interstitialAd = null;
                        }
                    });
        } else {
            callback.onAdLoaded(interstitialAd);
        }
    }

    public AdRequest newAdRequest(){
        return new AdRequest.Builder().build();
    }

}
