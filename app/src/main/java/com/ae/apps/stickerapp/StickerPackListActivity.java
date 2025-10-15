/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.ae.apps.stickerapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ae.apps.stickerapp.ads.AdLoadedCallback;
import com.ae.apps.stickerapp.ads.AdResources;
import com.ae.apps.stickerapp.analytics.Analytics;
import com.ae.apps.stickerapp.reviews.AppReview;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StickerPackListActivity extends BaseActivity implements AdLoadedCallback {
    public static final String EXTRA_STICKER_PACK_LIST_DATA = "sticker_pack_list";
    private static final int STICKER_PREVIEW_DISPLAY_LIMIT = 5;
    private static final String TAG = "StickerPackList";
    private static final String INTENT_ACTION_ENABLE_STICKER_PACK = "com.whatsapp.intent.action.ENABLE_STICKER_PACK";

    private LinearLayoutManager packLayoutManager;
    private RecyclerView packRecyclerView;
    private StickerPackListAdapter allStickerPacksListAdapter;
    WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    ArrayList<StickerPack> stickerPackList;

    private AdResources adResources;

    private final boolean interstitialAdsEnabled = false;

    private StickerPack selectedStickerPack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack_list);

        packRecyclerView = findViewById(R.id.sticker_pack_list);
        stickerPackList = getIntent().getParcelableArrayListExtra(EXTRA_STICKER_PACK_LIST_DATA);
        showStickerPackList(stickerPackList);

        // Disabling ads on the main screen since Google Admob is complaining of
        // 'More ads or paid promotional material than publisher-content'
        // initAd();

        initAnalytics();
        initAppReview();

        setToolBar();
    }

    private void initAd() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus -> { });

        // Find and load the adView on this screen
        // https://developers.google.com/admob/android/banner
        AdView mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());

        // Disabling the Interstitial ads for now, as it doesn't provide very good
        // user experience, but keeping the code here for reference implementation

        // https://developers.google.com/admob/android/interstitial
        // Interstitial ads are setup here on load, and displayed when the user has made a
        // meaningful interaction on the screen like 'selected a card pack to view the details'
        // We are managing ads lifecycles and instances via AdResources
        if(interstitialAdsEnabled) {
            adResources = new AdResources();
            adResources.initInterstitial(this, this);
        }
    }

    private void showInterstitialAd(){
        // TODO Rather than showing the ad always upon invocation, add a backoff logic if
        // we prefer not to show ads too frequently to the user
        if(null != adResources ) {
            adResources.showInterstitialAd(StickerPackListActivity.this);
        }
    }

    private void initAnalytics() {
        Analytics.getInstance(this).logAppStart();
    }

    private void initAppReview() {
        AppReview.getInstance().init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_privacy_policy) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.url_app_privacy_policy)));
            startActivity(intent);
        }
        return true;
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.include);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_privacy_policy) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.url_app_privacy_policy)));
                startActivity(intent);
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        //noinspection unchecked
        whiteListCheckAsyncTask.execute(stickerPackList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
    }

    private void showStickerPackList(List<StickerPack> stickerPackList) {
        allStickerPacksListAdapter = new StickerPackListAdapter(stickerPackList, onAddButtonClickedListener, this);
        packRecyclerView.setAdapter(allStickerPacksListAdapter);
        packLayoutManager = new LinearLayoutManager(this);
        packLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // setDividerForRecyclerView();
        packRecyclerView.setLayoutManager(packLayoutManager);
        packRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this::recalculateColumnCount);
    }

    private final StickerPackListAdapter.OnAddButtonClickedListener onAddButtonClickedListener = pack -> {
        if(interstitialAdsEnabled) {
            // Save this selected pack for later and show add to whatsapp once the ad is dismissed
            selectedStickerPack = pack;
            // Show an Interstitial Ad when the user clicks the Add Button
            showInterstitialAd();
        } else {
            addStickerPack(pack);
        }

    };

    private void addStickerPack(StickerPack pack) {
        // Guard clause
        if(null == pack){
            return;
        }

        Intent intent = new Intent();
        intent.setAction(INTENT_ACTION_ENABLE_STICKER_PACK);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_ID, pack.identifier);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_NAME, pack.name);
        try {
            startActivityForResult(intent, StickerPackDetailsActivity.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(StickerPackListActivity.this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
        }
    }

    private void recalculateColumnCount() {
        final int previewSize = getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size);
        int firstVisibleItemPosition = packLayoutManager.findFirstVisibleItemPosition();
        StickerPackListItemViewHolder viewHolder = (StickerPackListItemViewHolder) packRecyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition);
        if (viewHolder != null) {
            final int max = Math.max(viewHolder.imageRowView.getMeasuredWidth() / previewSize, 1);
            int numColumns = Math.min(STICKER_PREVIEW_DISPLAY_LIMIT, max);
            allStickerPacksListAdapter.setMaxNumberOfStickersInARow(numColumns);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StickerPackDetailsActivity.ADD_PACK) {
            if (resultCode == Activity.RESULT_CANCELED && data != null) {
                final String validationError = data.getStringExtra("validation_error");
                if (validationError != null) {
                    if (BuildConfig.DEBUG) {
                        //validation error should be shown to developer only, not users.
                        MessageDialogFragment.newInstance(R.string.title_validation_error, validationError).show(getSupportFragmentManager(), "validation error");
                    }
                    Log.e(TAG, "Validation failed:" + validationError);
                }
            }
        }
    }

    @Override
    public void onAdDismissedFullScreenContent() {
        addStickerPack(selectedStickerPack);
    }

    @Override
    public void onAdFailedToShowFullScreenContent(AdError error) {
        addStickerPack(selectedStickerPack);
    }


    static class WhiteListCheckAsyncTask extends AsyncTask<List<StickerPack>, Void, List<StickerPack>> {
        private final WeakReference<StickerPackListActivity> stickerPackListActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackListActivity stickerPackListActivity) {
            this.stickerPackListActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @SafeVarargs
        @Override
        protected final List<StickerPack> doInBackground(List<StickerPack>... lists) {
            List<StickerPack> stickerPackList = lists[0];
            final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity == null) {
                return stickerPackList;
            }
            for (StickerPack stickerPack : stickerPackList) {
                stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(stickerPackListActivity, stickerPack.identifier));
            }
            return stickerPackList;
        }

        @Override
        protected void onPostExecute(List<StickerPack> stickerPackList) {
            final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity != null) {
                stickerPackListActivity.allStickerPacksListAdapter.notifyDataSetChanged();
            }
        }
    }
}
