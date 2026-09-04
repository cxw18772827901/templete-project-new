package com.hjq.shape.view.roundcard;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.Nullable;

interface RoundCardViewDelegate {
    void setCardBackground(Drawable drawable);

    Drawable getCardBackground();

    boolean getUseCompatPadding();

    boolean getPreventCornerOverlap();

    @Nullable
    ColorStateList getShadowColor();

    void setShadowPadding(int left, int top, int right, int bottom);

    void setMinWidthHeightInternal(int width, int height);

    View getCardView();
}
