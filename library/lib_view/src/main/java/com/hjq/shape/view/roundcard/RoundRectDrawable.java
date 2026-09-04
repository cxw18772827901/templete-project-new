package com.hjq.shape.view.roundcard;

import android.content.res.ColorStateList;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(21)
class RoundRectDrawable extends Drawable {
    private static final float SHADOW_MULTIPLIER = 1.5f;
    /** Soften opaque colors to ~35% alpha for glow. */
    private static final int OPAQUE_SHADOW_ALPHA = 0x59;

    private float mRadius;
    private final float[] mRadii = new float[8];
    private final float[] mClampedRadii = new float[8];
    private final Paint mPaint;
    private final Paint mShadowPaint;
    private final RectF mBoundsF;
    private final Rect mBoundsI;
    private final Path mPath;
    private float mPadding;
    private boolean mInsetForPadding = false;
    private boolean mInsetForRadius = true;
    private boolean mUniformRadius = true;
    private float mShadowSize;
    private float mBlurRadius = -1f;
    @Nullable
    private BlurMaskFilter mBlurMaskFilter;
    @Nullable
    private ColorStateList mCompatShadowColor;
    private ColorStateList mBackground;
    private PorterDuffColorFilter mTintFilter;
    private ColorStateList mTint;
    private PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;

    RoundRectDrawable(ColorStateList backgroundColor, float radius) {
        mRadius = Math.max(0f, radius);
        setRadiiInternal(mRadius, mRadius, mRadius, mRadius);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
        setBackground(backgroundColor);
        mBoundsF = new RectF();
        mBoundsI = new Rect();
        mPath = new Path();
    }

    private void setBackground(ColorStateList color) {
        mBackground = (color == null) ? ColorStateList.valueOf(Color.TRANSPARENT) : color;
        mPaint.setColor(mBackground.getColorForState(getState(), mBackground.getDefaultColor()));
    }

    void setPadding(float padding, boolean insetForPadding, boolean insetForRadius) {
        if (padding == mPadding && mInsetForPadding == insetForPadding
                && mInsetForRadius == insetForRadius) {
            return;
        }
        mPadding = padding;
        mInsetForPadding = insetForPadding;
        mInsetForRadius = insetForRadius;
        updateBounds(null);
        invalidateSelf();
    }

    float getPadding() {
        return mPadding;
    }

    void setShadowSize(float shadowSize) {
        if (shadowSize < 0f) {
            shadowSize = 0f;
        }
        if (mShadowSize == shadowSize) {
            return;
        }
        mShadowSize = shadowSize;
        mBlurRadius = -1f;
        mBlurMaskFilter = null;
        invalidateSelf();
    }

    float getShadowSize() {
        return mShadowSize;
    }

    void setCompatShadowColor(@Nullable ColorStateList color) {
        if (mCompatShadowColor == color) {
            return;
        }
        mCompatShadowColor = color;
        invalidateSelf();
    }

    @Nullable
    ColorStateList getCompatShadowColor() {
        return mCompatShadowColor;
    }

    boolean usesCompatShadow() {
        return mCompatShadowColor != null && mShadowSize > 0f;
    }

    @NonNull
    Path getClipPath() {
        buildPath();
        return mPath;
    }

    @Override
    public void draw(Canvas canvas) {
        final Paint paint = mPaint;
        final boolean clearColorFilter;
        if (mTintFilter != null && paint.getColorFilter() == null) {
            paint.setColorFilter(mTintFilter);
            clearColorFilter = true;
        } else {
            clearColorFilter = false;
        }

        // API < 28 colored shadow: BlurMaskFilter.OUTER gives an even halo.
        // Avoid Paint#setShadowLayer — on API 26 it still forms a heavy bottom band.
        if (usesCompatShadow()) {
            final float blur = Math.max(mShadowSize * 0.75f, 1f);
            if (mBlurMaskFilter == null || mBlurRadius != blur) {
                mBlurRadius = blur;
                mBlurMaskFilter = new BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL);
            }
            mShadowPaint.setMaskFilter(mBlurMaskFilter);
            mShadowPaint.setColor(softenShadowColor(mCompatShadowColor.getColorForState(
                    getState(), mCompatShadowColor.getDefaultColor())));
            drawShape(canvas, mShadowPaint);
            mShadowPaint.setMaskFilter(null);
        }

        paint.clearShadowLayer();
        drawShape(canvas, paint);

        if (clearColorFilter) {
            paint.setColorFilter(null);
        }
    }

    private void drawShape(Canvas canvas, Paint paint) {
        if (mUniformRadius) {
            canvas.drawRoundRect(mBoundsF, mRadius, mRadius, paint);
        } else {
            buildPath();
            canvas.drawPath(mPath, paint);
        }
    }

    private void buildPath() {
        mPath.reset();
        clampRadiiInto(mClampedRadii);
        mPath.addRoundRect(mBoundsF, mClampedRadii, Path.Direction.CW);
    }

    private void clampRadiiInto(float[] out) {
        final float maxX = Math.max(0f, mBoundsF.width() / 2f);
        final float maxY = Math.max(0f, mBoundsF.height() / 2f);
        for (int i = 0; i < 8; i += 2) {
            out[i] = Math.min(Math.max(0f, mRadii[i]), maxX);
            out[i + 1] = Math.min(Math.max(0f, mRadii[i + 1]), maxY);
        }
    }

    private void updateBounds(Rect bounds) {
        if (bounds == null) {
            bounds = getBounds();
        }
        mBoundsF.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
        mBoundsI.set(bounds);
        if (mInsetForPadding) {
            final float inset;
            if (mCompatShadowColor != null) {
                // Symmetric inset so the even glow is not clipped unevenly.
                inset = Math.max(mPadding, mShadowSize) * SHADOW_MULTIPLIER;
                int pad = (int) Math.ceil(inset);
                mBoundsI.inset(pad, pad);
            } else {
                float radiusForPadding = getMaxRadius();
                float vInset = RoundRectDrawableWithShadow.calculateVerticalPadding(mPadding,
                        radiusForPadding, mInsetForRadius);
                float hInset = RoundRectDrawableWithShadow.calculateHorizontalPadding(mPadding,
                        radiusForPadding, mInsetForRadius);
                mBoundsI.inset((int) Math.ceil(hInset), (int) Math.ceil(vInset));
            }
            mBoundsF.set(mBoundsI);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(bounds);
    }

    @Override
    public void getOutline(Outline outline) {
        if (mUniformRadius) {
            outline.setRoundRect(mBoundsI, mRadius);
            return;
        }
        buildPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(mPath);
        } else {
            try {
                outline.setConvexPath(mPath);
            } catch (IllegalArgumentException ignored) {
                float r = Math.min(getMaxRadius(),
                        Math.min(mBoundsI.width(), mBoundsI.height()) / 2f);
                outline.setRoundRect(mBoundsI, r);
            }
        }
    }

    void setRadius(float radius) {
        if (mUniformRadius && radius == mRadius) {
            return;
        }
        mRadius = radius;
        setRadiiInternal(radius, radius, radius, radius);
        mUniformRadius = true;
        updateBounds(null);
        invalidateSelf();
    }

    void setCornerRadii(float[] radii) {
        if (radii == null || radii.length < 8) {
            throw new IllegalArgumentException("radii must contain 8 values");
        }
        for (int i = 0; i < 8; i++) {
            if (radii[i] < 0f) {
                throw new IllegalArgumentException("radii must be >= 0");
            }
        }
        System.arraycopy(radii, 0, mRadii, 0, 8);
        mUniformRadius = isUniform(mRadii);
        mRadius = mUniformRadius ? mRadii[0] : getMaxRadius();
        updateBounds(null);
        invalidateSelf();
    }

    float[] getCornerRadii() {
        return mRadii.clone();
    }

    private void setRadiiInternal(float topLeft, float topRight, float bottomRight,
            float bottomLeft) {
        mRadii[0] = mRadii[1] = topLeft;
        mRadii[2] = mRadii[3] = topRight;
        mRadii[4] = mRadii[5] = bottomRight;
        mRadii[6] = mRadii[7] = bottomLeft;
    }

    private static boolean isUniform(float[] radii) {
        float first = radii[0];
        for (int i = 1; i < 8; i++) {
            if (radii[i] != first) {
                return false;
            }
        }
        return true;
    }

    float getMaxRadius() {
        float max = mRadii[0];
        for (int i = 2; i < 8; i += 2) {
            if (mRadii[i] > max) {
                max = mRadii[i];
            }
        }
        return max;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setColor(@Nullable ColorStateList color) {
        setBackground(color);
        invalidateSelf();
    }

    public ColorStateList getColor() {
        return mBackground;
    }

    @Override
    public void setTintList(ColorStateList tint) {
        mTint = tint;
        mTintFilter = createTintFilter(mTint, mTintMode);
        invalidateSelf();
    }

    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        mTintMode = tintMode;
        mTintFilter = createTintFilter(mTint, mTintMode);
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        final int newColor = mBackground.getColorForState(stateSet, mBackground.getDefaultColor());
        final boolean colorChanged = newColor != mPaint.getColor();
        if (colorChanged) {
            mPaint.setColor(newColor);
        }
        boolean shadowStateful = mCompatShadowColor != null && mCompatShadowColor.isStateful();
        if (mTint != null && mTintMode != null) {
            mTintFilter = createTintFilter(mTint, mTintMode);
            return true;
        }
        return colorChanged || shadowStateful;
    }

    @Override
    public boolean isStateful() {
        return (mTint != null && mTint.isStateful())
                || (mBackground != null && mBackground.isStateful())
                || (mCompatShadowColor != null && mCompatShadowColor.isStateful())
                || super.isStateful();
    }

    private PorterDuffColorFilter createTintFilter(ColorStateList tint, PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }
        final int color = tint.getColorForState(getState(), Color.TRANSPARENT);
        return new PorterDuffColorFilter(color, tintMode);
    }

    /**
     * Fully opaque colors look like a solid slab after blur. Keep explicit alpha as-is.
     */
    private static int softenShadowColor(int color) {
        if (Color.alpha(color) < 255) {
            return color;
        }
        return Color.argb(OPAQUE_SHADOW_ALPHA, Color.red(color), Color.green(color),
                Color.blue(color));
    }
}
