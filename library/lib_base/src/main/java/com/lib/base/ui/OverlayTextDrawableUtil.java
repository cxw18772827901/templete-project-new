package com.lib.base.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewOverlay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lib.base.util.ScreenUtil;

/**
 * 逆天工具类，在界面上以悬浮的形式显示当前的Activity和Fragment名称，方便调试界面层级关系。
 * <p>
 * Date         2026/4/22.
 *
 * @author xxx
 */

public class OverlayTextDrawableUtil {

    private static final boolean ENABLE = false;

    public static void debug(Application app) {
        if (!ENABLE) return;

        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public synchronized void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                if (!ENABLE || !(activity instanceof AppCompatActivity)) {
                    return;
                }
                // fragment 添加name
                ((AppCompatActivity) activity)
                        .getSupportFragmentManager()
                        .registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                            @Override
                            public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
                                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                                ViewOverlay overlay = v.getOverlay();
                                int topInset = getTopInset(v);
                                int depth = getFragmentDepth(f);
                                int offset = (depth + 1) * dp(v.getContext(), 22); // 跟activity错开，不重叠到一起
                                TipsDrawable drawable = new TipsDrawable(f.getContext(), f.getClass().getSimpleName(), topInset + offset);
                                overlay.add(drawable);
                                // 优化展示
                                v.addOnLayoutChangeListener((v1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                                    //because  ViewOverlay no update inner Drawable bounds。so myself setBounds and invalidate
                                    drawable.setBounds(left, top, right, bottom);
                                    drawable.invalidateSelf();
                                });
                            }
                        }, true);
                // activity 添加name
                TipsDrawable drawable = new TipsDrawable(activity, activity.getClass().getSimpleName(), ScreenUtil.getStatusBarSize());
                activity.findViewById(android.R.id.content)
                        .getRootView()
                        .getOverlay()
                        .add(drawable);
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }
        });
    }

    private synchronized static int getFragmentDepth(@NonNull Fragment f) {
        int depth = 0;
        Fragment parent = f.getParentFragment();
        while (parent != null) {
            depth++;
            parent = parent.getParentFragment();
        }
        return depth;
    }

    private static int getTopInset(@NonNull View v) {
        android.view.WindowInsets insets = v.getRootWindowInsets();
        if (insets != null) {
            return insets.getSystemWindowInsetTop();
        }
        // fallback
        return ScreenUtil.getStatusBarSize();
    }

    private static int dp(@NonNull Context c, int dp) {
        return (int) (dp * c.getResources().getDisplayMetrics().density);
    }

    private static class TipsDrawable extends ColorDrawable {

        private final String name;
        private final Paint bgPaint;
        private final TextPaint textPaint;
        private final int height;
        private final int padding;
        private int topMargin;

        TipsDrawable(Context context, String name, int topMargin) {
            super(Color.TRANSPARENT);
            this.name = name;
            this.topMargin = topMargin;

            height = dp(context, 20);
            padding = dp(context, 8);

            bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(0x33000000);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(dp(context, 12));
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        }

        @Override
        public void draw(Canvas canvas) {
            float textWidth = textPaint.measureText(name);

            float left = padding;
            float top = topMargin;
            float right = left + textWidth + padding * 2;
            float bottom = top + height;

            canvas.drawRect(left, top, right, bottom, bgPaint);

            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float baseline = top + height / 2f - (fm.top + fm.bottom) / 2;

            canvas.drawText(name, left + padding, baseline, textPaint);
        }
    }
}