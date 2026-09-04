package com.hjq.shape.view.roundcard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.Nullable;

class RoundCardViewBaseImpl implements RoundCardViewImpl {
    final RectF mCornerRect = new RectF();

    @Override
    public void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper =
                new RoundRectDrawableWithShadow.RoundRectHelper() {
                    @Override
                    public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
                            Paint paint) {
                        final float twoRadius = cornerRadius * 2;
                        final float innerWidth = bounds.width() - twoRadius - 1;
                        final float innerHeight = bounds.height() - twoRadius - 1;
                        if (cornerRadius >= 1f) {
                            float roundedCornerRadius = cornerRadius + .5f;
                            mCornerRect.set(-roundedCornerRadius, -roundedCornerRadius,
                                    roundedCornerRadius, roundedCornerRadius);
                            int saved = canvas.save();
                            canvas.translate(bounds.left + roundedCornerRadius,
                                    bounds.top + roundedCornerRadius);
                            canvas.drawArc(mCornerRect, 180, 90, true, paint);
                            canvas.translate(innerWidth, 0);
                            canvas.rotate(90);
                            canvas.drawArc(mCornerRect, 180, 90, true, paint);
                            canvas.translate(innerHeight, 0);
                            canvas.rotate(90);
                            canvas.drawArc(mCornerRect, 180, 90, true, paint);
                            canvas.translate(innerWidth, 0);
                            canvas.rotate(90);
                            canvas.drawArc(mCornerRect, 180, 90, true, paint);
                            canvas.restoreToCount(saved);
                            canvas.drawRect(bounds.left + roundedCornerRadius - 1f, bounds.top,
                                    bounds.right - roundedCornerRadius + 1f,
                                    bounds.top + roundedCornerRadius, paint);
                            canvas.drawRect(bounds.left + roundedCornerRadius - 1f,
                                    bounds.bottom - roundedCornerRadius,
                                    bounds.right - roundedCornerRadius + 1f, bounds.bottom, paint);
                        }
                        canvas.drawRect(bounds.left, bounds.top + cornerRadius, bounds.right,
                                bounds.bottom - cornerRadius, paint);
                    }
                };
    }

    @Override
    public void initialize(RoundCardViewDelegate cardView, Context context,
            ColorStateList backgroundColor, float radius, float elevation, float maxElevation) {
        RoundRectDrawableWithShadow background = new RoundRectDrawableWithShadow(
                context.getResources(), backgroundColor, radius, elevation, maxElevation);
        background.setAddPaddingForCorners(cardView.getPreventCornerOverlap());
        cardView.setCardBackground(background);
        updatePadding(cardView);
    }

    @Override
    public void updatePadding(RoundCardViewDelegate cardView) {
        Rect shadowPadding = new Rect();
        getShadowBackground(cardView).getMaxShadowAndCornerPadding(shadowPadding);
        cardView.setMinWidthHeightInternal((int) Math.ceil(getMinWidth(cardView)),
                (int) Math.ceil(getMinHeight(cardView)));
        cardView.setShadowPadding(shadowPadding.left, shadowPadding.top, shadowPadding.right,
                shadowPadding.bottom);
    }

    @Override
    public void onCompatPaddingChanged(RoundCardViewDelegate cardView) {
    }

    @Override
    public void onPreventCornerOverlapChanged(RoundCardViewDelegate cardView) {
        getShadowBackground(cardView).setAddPaddingForCorners(cardView.getPreventCornerOverlap());
        updatePadding(cardView);
    }

    @Override
    public void setBackgroundColor(RoundCardViewDelegate cardView, @Nullable ColorStateList color) {
        getShadowBackground(cardView).setColor(color);
    }

    @Override
    public ColorStateList getBackgroundColor(RoundCardViewDelegate cardView) {
        return getShadowBackground(cardView).getColor();
    }

    @Override
    public void setShadowColor(RoundCardViewDelegate cardView, @Nullable ColorStateList color) {
    }

    @Override
    public void setRadius(RoundCardViewDelegate cardView, float radius) {
        getShadowBackground(cardView).setCornerRadius(radius);
        updatePadding(cardView);
    }

    @Override
    public float getRadius(RoundCardViewDelegate cardView) {
        return getShadowBackground(cardView).getCornerRadius();
    }

    @Override
    public void setCornerRadii(RoundCardViewDelegate cardView, float[] radii) {
        float max = 0f;
        if (radii != null) {
            for (int i = 0; i < radii.length; i += 2) {
                if (radii[i] > max) {
                    max = radii[i];
                }
            }
        }
        setRadius(cardView, max);
    }

    @Override
    public float[] getCornerRadii(RoundCardViewDelegate cardView) {
        float r = getRadius(cardView);
        return new float[]{r, r, r, r, r, r, r, r};
    }

    @Override
    public void setElevation(RoundCardViewDelegate cardView, float elevation) {
        getShadowBackground(cardView).setShadowSize(elevation);
    }

    @Override
    public float getElevation(RoundCardViewDelegate cardView) {
        return getShadowBackground(cardView).getShadowSize();
    }

    @Override
    public void setMaxElevation(RoundCardViewDelegate cardView, float maxElevation) {
        getShadowBackground(cardView).setMaxShadowSize(maxElevation);
        updatePadding(cardView);
    }

    @Override
    public float getMaxElevation(RoundCardViewDelegate cardView) {
        return getShadowBackground(cardView).getMaxShadowSize();
    }

    @Override
    public float getMinWidth(RoundCardViewDelegate cardView) {
        return getShadowBackground(cardView).getMinWidth();
    }

    @Override
    public float getMinHeight(RoundCardViewDelegate cardView) {
        return getShadowBackground(cardView).getMinHeight();
    }

    private RoundRectDrawableWithShadow getShadowBackground(RoundCardViewDelegate cardView) {
        return (RoundRectDrawableWithShadow) cardView.getCardBackground();
    }
}
