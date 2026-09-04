package com.hjq.shape.view.roundcard;

import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.Nullable;

interface RoundCardViewImpl {
    void initialize(RoundCardViewDelegate cardView, Context context,
            ColorStateList backgroundColor, float radius, float elevation, float maxElevation);

    void setRadius(RoundCardViewDelegate cardView, float radius);

    float getRadius(RoundCardViewDelegate cardView);

    void setCornerRadii(RoundCardViewDelegate cardView, float[] radii);

    float[] getCornerRadii(RoundCardViewDelegate cardView);

    void setElevation(RoundCardViewDelegate cardView, float elevation);

    float getElevation(RoundCardViewDelegate cardView);

    void initStatic();

    void setMaxElevation(RoundCardViewDelegate cardView, float maxElevation);

    float getMaxElevation(RoundCardViewDelegate cardView);

    float getMinWidth(RoundCardViewDelegate cardView);

    float getMinHeight(RoundCardViewDelegate cardView);

    void updatePadding(RoundCardViewDelegate cardView);

    void onCompatPaddingChanged(RoundCardViewDelegate cardView);

    void onPreventCornerOverlapChanged(RoundCardViewDelegate cardView);

    void setBackgroundColor(RoundCardViewDelegate cardView, @Nullable ColorStateList color);

    ColorStateList getBackgroundColor(RoundCardViewDelegate cardView);

    void setShadowColor(RoundCardViewDelegate cardView, @Nullable ColorStateList color);
}
