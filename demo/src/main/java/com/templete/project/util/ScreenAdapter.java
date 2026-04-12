package com.templete.project.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScreenAdapter {

    private static float designWidth;
    private static float sNonCompatDensity;
    private static float sNonCompatScaledDensity;

    public static void init(Application app, float uiWidth) {
        designWidth = uiWidth;

        DisplayMetrics appDm = app.getResources().getDisplayMetrics();
        sNonCompatDensity = appDm.density;
        sNonCompatScaledDensity = appDm.scaledDensity;

        // 监听字体变化（防止用户调字体导致错乱）
        app.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(@NonNull Configuration newConfig) {
                if (newConfig.fontScale > 0) {
                    sNonCompatScaledDensity = app.getResources().getDisplayMetrics().scaledDensity;
                }
            }

            @Override
            public void onLowMemory() {}
        });

        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                adapt(activity);
            }

            @Override public void onActivityStarted(@NonNull Activity activity) {}
            @Override public void onActivityResumed(@NonNull Activity activity) {}
            @Override public void onActivityPaused(@NonNull Activity activity) {}
            @Override public void onActivityStopped(@NonNull Activity activity) {}
            @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
            @Override public void onActivityDestroyed(@NonNull Activity activity) {}
        });
    }

    private static void adapt(@NonNull Activity activity) {
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();

        float width = Math.min(dm.widthPixels, dm.heightPixels); // 兼容横屏
        float targetDensity = width / designWidth;
        float targetScaledDensity = targetDensity * (sNonCompatScaledDensity / sNonCompatDensity);
        int targetDensityDpi = (int) (160 * targetDensity);

        // Activity
        dm.density = targetDensity;
        dm.scaledDensity = targetScaledDensity;
        dm.densityDpi = targetDensityDpi;

        // System（Dialog / Toast / PopupWindow 必须）
        DisplayMetrics sysDm = Resources.getSystem().getDisplayMetrics();
        sysDm.density = targetDensity;
        sysDm.scaledDensity = targetScaledDensity;
        sysDm.densityDpi = targetDensityDpi;
    }
}