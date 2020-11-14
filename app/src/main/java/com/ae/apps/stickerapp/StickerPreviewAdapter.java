/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.ae.apps.stickerapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class StickerPreviewAdapter extends RecyclerView.Adapter<StickerPreviewViewHolder> {

    @NonNull
    private StickerPack stickerPack;
    @NonNull
    final Context context;
    private final int cellSize;
    private int cellLimit;
    private int cellPadding;
    private final int errorResource;

    private final LayoutInflater layoutInflater;

    StickerPreviewAdapter(
            @NonNull final Context context,
            @NonNull final LayoutInflater layoutInflater,
            final int errorResource,
            final int cellSize,
            final int cellPadding,
            @NonNull final StickerPack stickerPack) {
        this.context = context;
        this.cellSize = cellSize;
        this.cellPadding = cellPadding;
        this.cellLimit = 0;
        this.layoutInflater = layoutInflater;
        this.errorResource = errorResource;
        this.stickerPack = stickerPack;

        MobileAds.initialize(context, initializationStatus -> {
        });
    }

    @NonNull
    @Override
    public StickerPreviewViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View itemView = layoutInflater.inflate(R.layout.sticker_image, viewGroup, false);
        StickerPreviewViewHolder vh = new StickerPreviewViewHolder(itemView);

        ViewGroup.LayoutParams layoutParams = vh.stickerPreviewView.getLayoutParams();
        layoutParams.height = cellSize;
        layoutParams.width = cellSize;
        vh.stickerPreviewView.setLayoutParams(layoutParams);
        vh.stickerPreviewView.setPadding(cellPadding, cellPadding, cellPadding, cellPadding);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final StickerPreviewViewHolder stickerPreviewViewHolder, final int i) {
        stickerPreviewViewHolder.stickerPreviewView.setImageResource(errorResource);
        final Uri stickerAssetUri = StickerPackLoader.getStickerAssetUri(stickerPack.identifier, stickerPack.getStickers().get(i).imageFileName);
        stickerPreviewViewHolder.stickerPreviewView.setImageURI(stickerAssetUri);
        stickerPreviewViewHolder.stickerPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "" + stickerAssetUri.toString(), Toast.LENGTH_SHORT).show();

 /*               final StickerDialog stickerDialog = new StickerDialog(context);
                stickerDialog.setStickerAsset(stickerAssetUri);
                stickerDialog.show();*/

                View dialogView = LayoutInflater.from(context).inflate(R.layout.sticker_dialog, null, false);
                SimpleDraweeView simpleDraweeView = dialogView.findViewById(R.id.sticker_preview_in_dialog);
                AdView mAdView = dialogView.findViewById(R.id.adView);
                View closeButton = dialogView.findViewById(R.id.btnClose);
                simpleDraweeView.setImageURI(stickerAssetUri);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                mAdView.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    @Override
    public int getItemCount() {
        int numberOfPreviewImagesInPack;
        numberOfPreviewImagesInPack = stickerPack.getStickers().size();
        if (cellLimit > 0) {
            return Math.min(numberOfPreviewImagesInPack, cellLimit);
        }
        return numberOfPreviewImagesInPack;
    }
}
