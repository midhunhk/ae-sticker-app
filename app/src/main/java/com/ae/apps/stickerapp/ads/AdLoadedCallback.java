package com.ae.apps.stickerapp.ads;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;

public interface AdLoadedCallback {

    void onAdDismissedFullScreenContent();

    void onAdFailedToShowFullScreenContent(AdError error);

}
