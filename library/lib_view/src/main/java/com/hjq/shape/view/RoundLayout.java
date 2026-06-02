package com.hjq.shape.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hjq.shape.R;

/**
 * 裁剪容器，不会被子view撑开露出来
 *
 * Date         28/4/26.
 *
 * @author xxx
 */

public class RoundLayout extends ConstraintLayout {
    private final Path path = new Path();
    private final RectF rect = new RectF();

    // 支持四个角
    private final float[] radii = new float[8];

    public RoundLayout(Context context) {
        this(context, null);
    }

    public RoundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundLayout);
        float radius = ta.getDimension(R.styleable.RoundLayout_rl_radius, 0);
        float topLeft = ta.getDimension(R.styleable.RoundLayout_rl_topLeft, radius);
        float topRight = ta.getDimension(R.styleable.RoundLayout_rl_topRight, radius);
        float bottomLeft = ta.getDimension(R.styleable.RoundLayout_rl_bottomLeft, radius);
        float bottomRight = ta.getDimension(R.styleable.RoundLayout_rl_bottomRight, radius);
        ta.recycle();
        setRadius(topLeft, topRight, bottomRight, bottomLeft);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.set(0, 0, w, h);
        updatePath();
    }

    private void updatePath() {
        path.reset();
        path.addRoundRect(rect, radii, Path.Direction.CW);
        path.close();
    }

    @Override
    public void draw(Canvas canvas) {
        // 关键点：如果圆角为0，就没必要 clip，提升性能
        boolean hasRadius = false;
        for (float r : radii) {
            if (r > 0) {
                hasRadius = true;
                break;
            }
        }

        if (hasRadius) {
            canvas.save();
            canvas.clipPath(path);
            super.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas);
        }
    }

    // =============================
    // 对外 API（重点）
    // =============================

    /**
     * 全部圆角
     */
    public void setRadius(float radius) {
        for (int i = 0; i < 8; i++) {
            radii[i] = radius;
        }
        updatePath();
        invalidate();
    }

    /**
     * 单独设置四个角
     */
    public void setRadius(
            float topLeft,
            float topRight,
            float bottomRight,
            float bottomLeft
    ) {
        radii[0] = radii[1] = topLeft;
        radii[2] = radii[3] = topRight;
        radii[4] = radii[5] = bottomRight;
        radii[6] = radii[7] = bottomLeft;

        updatePath();
        invalidate();
    }
}
