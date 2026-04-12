package com.example.app;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ProgressBar extends View {
    private Paint bgPaint;
    private Paint progressPaint;
    private Paint textPaint;

    private int progress = 0; // 0 - 100
    private int barHeight = dpToPx(4);
    private int cornerRadius = dpToPx(2);
    // private int textSize = spToPx(12);
    // private int textMargin = dpToPx(5); // 进度条与文字之间的间距

    public ProgressBar(Context context) {
        super(context);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            progress = 50;
        }
        // 背景条
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#80FFFFFF"));

        // 进度条
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // progressPaint.setColor(Color.parseColor("#02C187")); // 绿色

        // 进度文字
        // textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // textPaint.setColor(Color.BLACK);
        // textPaint.setTextSize(textSize);
        // textPaint.setTextAlign(Paint.Align.RIGHT); // 右对齐
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, 100));
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = barHeight;

        // 背景条
        RectF bgRect = new RectF(0, 0, width, height);
        canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, bgPaint);

        // 进度条
        float progressWidth = width * (progress / 100f);
        LinearGradient gradient = new LinearGradient(
                0, 0, progressWidth, 0,
                // new int[]{Color.parseColor("#3DFFC4"), Color.parseColor("#3DFFC4"), Color.parseColor("#FFFFFF")},
                new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF")},
                null, Shader.TileMode.CLAMP);
        progressPaint.setShader(gradient);

        RectF progressRect = new RectF(0, 0, progressWidth, height);
        canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint);

        // 进度文字
        // String progressText = progress + "%";
        // Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        // float textHeight = fontMetrics.bottom - fontMetrics.top;
        // float baseline = height / 2f + (textHeight / 2f - fontMetrics.bottom);
        //
        // float textX = /*width*/progressWidth - textMargin;
        // canvas.drawText(progressText, textX, baseline, textPaint);
    }

    // 工具方法
    private int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int spToPx(float sp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
