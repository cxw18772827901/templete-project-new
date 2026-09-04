package com.hjq.shape.view.roundcard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(21)
class RoundCardViewApi21Impl implements RoundCardViewImpl {
    private static final int DEFAULT_OUTLINE_SHADOW_COLOR = Color.BLACK;

    @Override
    public void initialize(RoundCardViewDelegate cardView, Context context,
            ColorStateList backgroundColor, float radius, float elevation, float maxElevation) {
        final RoundRectDrawable background = new RoundRectDrawable(backgroundColor, radius);
        background.setShadowSize(elevation);
        cardView.setCardBackground(background);
        View view = cardView.getCardView();
        view.setClipToOutline(true);
        view.setElevation(elevation);
        setMaxElevation(cardView, maxElevation);
    }

    @Override
    public void setRadius(RoundCardViewDelegate cardView, float radius) {
        getCardBackground(cardView).setRadius(radius);
        if (needsShadowPadding(cardView)) {
            updatePadding(cardView);
        }
        invalidateOutline(cardView);
    }

    @Override
    public void setCornerRadii(RoundCardViewDelegate cardView, float[] radii) {
        getCardBackground(cardView).setCornerRadii(radii);
        setMaxElevation(cardView, getMaxElevation(cardView));
        invalidateOutline(cardView);
    }

    @Override
    public float[] getCornerRadii(RoundCardViewDelegate cardView) {
        return getCardBackground(cardView).getCornerRadii();
    }

    @Override
    public void initStatic() {
    }

    @Override
    public void setMaxElevation(RoundCardViewDelegate cardView, float maxElevation) {
        RoundRectDrawable background = getCardBackground(cardView);
        background.setPadding(maxElevation, needsShadowPadding(cardView),
                cardView.getPreventCornerOverlap());
        updatePadding(cardView);
    }

    @Override
    public float getMaxElevation(RoundCardViewDelegate cardView) {
        return getCardBackground(cardView).getPadding();
    }

    @Override
    public float getMinWidth(RoundCardViewDelegate cardView) {
        return getRadius(cardView) * 2;
    }

    @Override
    public float getMinHeight(RoundCardViewDelegate cardView) {
        return getRadius(cardView) * 2;
    }

    @Override
    public float getRadius(RoundCardViewDelegate cardView) {
        return getCardBackground(cardView).getRadius();
    }

    @Override
    public void setElevation(RoundCardViewDelegate cardView, float elevation) {
        RoundRectDrawable background = getCardBackground(cardView);
        background.setShadowSize(elevation);
        View view = cardView.getCardView();
        if (background.getCompatShadowColor() != null) {
            view.setElevation(0f);
            setMaxElevation(cardView, Math.max(getMaxElevation(cardView), elevation));
        } else {
            view.setElevation(elevation);
        }
    }

    @Override
    public float getElevation(RoundCardViewDelegate cardView) {
        RoundRectDrawable background = getCardBackground(cardView);
        if (background.getCompatShadowColor() != null) {
            return background.getShadowSize();
        }
        return cardView.getCardView().getElevation();
    }

    @Override
    public void updatePadding(RoundCardViewDelegate cardView) {
        if (!needsShadowPadding(cardView)) {
            cardView.setShadowPadding(0, 0, 0, 0);
            return;
        }
        RoundRectDrawable background = getCardBackground(cardView);
        if (background.getCompatShadowColor() != null) {
            // Match the even software glow: equal padding on all sides.
            float size = Math.max(getMaxElevation(cardView), background.getShadowSize());
            int pad = (int) Math.ceil(size * 1.5f);
            cardView.setShadowPadding(pad, pad, pad, pad);
            return;
        }
        float elevation = getMaxElevation(cardView);
        final float radius = background.getMaxRadius();
        int hPadding = (int) Math.ceil(RoundRectDrawableWithShadow.calculateHorizontalPadding(
                elevation, radius, cardView.getPreventCornerOverlap()));
        int vPadding = (int) Math.ceil(RoundRectDrawableWithShadow.calculateVerticalPadding(
                elevation, radius, cardView.getPreventCornerOverlap()));
        cardView.setShadowPadding(hPadding, vPadding, hPadding, vPadding);
    }

    @Override
    public void onCompatPaddingChanged(RoundCardViewDelegate cardView) {
        setMaxElevation(cardView, getMaxElevation(cardView));
    }

    @Override
    public void onPreventCornerOverlapChanged(RoundCardViewDelegate cardView) {
        setMaxElevation(cardView, getMaxElevation(cardView));
    }

    @Override
    public void setBackgroundColor(RoundCardViewDelegate cardView, @Nullable ColorStateList color) {
        getCardBackground(cardView).setColor(color);
    }

    @Override
    public ColorStateList getBackgroundColor(RoundCardViewDelegate cardView) {
        return getCardBackground(cardView).getColor();
    }

    @Override
    public void setShadowColor(RoundCardViewDelegate cardView, @Nullable ColorStateList color) {
        final View view = cardView.getCardView();
        final RoundRectDrawable background = getCardBackground(cardView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            background.setCompatShadowColor(null);
            view.setLayerType(View.LAYER_TYPE_NONE, null);
            view.setClipToOutline(true);
            view.setElevation(background.getShadowSize());
            final int shadowColor = color == null
                    ? DEFAULT_OUTLINE_SHADOW_COLOR
                    : color.getColorForState(view.getDrawableState(), color.getDefaultColor());
            view.setOutlineAmbientShadowColor(shadowColor);
            view.setOutlineSpotShadowColor(shadowColor);
            setMaxElevation(cardView, getMaxElevation(cardView));
            return;
        }

        background.setCompatShadowColor(color);
        if (color != null) {
            view.setElevation(0f);
            view.setClipToOutline(false);
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            view.setElevation(background.getShadowSize());
            view.setClipToOutline(true);
            view.setLayerType(View.LAYER_TYPE_NONE, null);
        }
        setMaxElevation(cardView, getMaxElevation(cardView));
        invalidateOutline(cardView);
    }

    private boolean needsShadowPadding(RoundCardViewDelegate cardView) {
        return cardView.getUseCompatPadding()
                || getCardBackground(cardView).getCompatShadowColor() != null;
    }

    private void invalidateOutline(RoundCardViewDelegate cardView) {
        cardView.getCardView().invalidateOutline();
    }

    private RoundRectDrawable getCardBackground(RoundCardViewDelegate cardView) {
        return (RoundRectDrawable) cardView.getCardBackground();
    }
}
