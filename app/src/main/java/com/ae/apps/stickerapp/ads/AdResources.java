package com.ae.apps.stickerapp.ads;

import android.content.Context;

import com.ae.apps.stickerapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdResources {

    private InterstitialAd interstitialAd;

    public AdResources(){

    }

    public InterstitialAd getInterstitial(final Context context){
        if(null == interstitialAd){
            interstitialAd = new InterstitialAd(context);
        }
        // Test Ad Unit id
        //interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        interstitialAd.setAdUnitId( context.getResources()
                .getString(R.string.google_admob_interstitial_id));
        return interstitialAd;
    }

    public AdRequest newAdRequest(){
        return new AdRequest.Builder().build();
    }

}
