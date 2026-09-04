package com.hjq.shape.view.roundcard;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.RequiresApi;

@RequiresApi(17)
class RoundCardViewApi17Impl extends RoundCardViewBaseImpl {
    @Override
    public void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper =
                new RoundRectDrawableWithShadow.RoundRectHelper() {
                    @Override
                    public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
                            Paint paint) {
                        canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
                    }
                };
    }
}
