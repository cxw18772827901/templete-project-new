package com.hjq.shape.view.roundcard;

import android.graphics.drawable.Drawable;
import android.view.View;

interface RoundCardViewDelegate {
    void setCardBackground(Drawable drawable);

    Drawable getCardBackground();

    boolean getUseCompatPadding();

    boolean getPreventCornerOverlap();

    void setShadowPadding(int left, int top, int right, int bottom);

    void setMinWidthHeightInternal(int width, int height);

    View getCardView();
}
