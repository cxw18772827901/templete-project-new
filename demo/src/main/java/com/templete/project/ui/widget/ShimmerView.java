package com.templete.project.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.skydoves.androidveil.VeilLayout;
import com.templete.project.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * PackageName  com.bbb.fastcloud.mvp.ui.widget.holder
 * ProjectName  FastCloud
 * Date         2022/11/15.
 *
 * @author xwchen
 */

@SuppressLint("ViewConstructor")
public class ShimmerView extends FrameLayout {
    public static final String TAG = "ShimmerViewTag";
    public static final int TYPE_LIST = 0;
    public static final int TYPE_GRID = 1;
    private List<VeilLayout> layouts = new ArrayList<>();

    public ShimmerView(@NonNull Context context) {
        super(context);
    }

    public ShimmerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShimmerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int type) {
        if (layouts.size() > 0) {
            stopAnim();
            layouts.clear();
        }
        removeAllViews();
        if (type == TYPE_GRID) {
            LayoutInflater.from(getContext()).inflate(R.layout.viewholder_shimmer_grid, this, true);
            LinearLayout view = findViewById(R.id.root);
            for (int i = 0; i < view.getChildCount(); i++) {
                ConstraintLayout constraintLayout = (ConstraintLayout) view.getChildAt(i);
                layouts.add(constraintLayout.findViewById(R.id.sl1));
                layouts.add(constraintLayout.findViewById(R.id.sl2));
            }
        } else {
            LayoutInflater.from(getContext()).inflate(R.layout.viewholder_shimmer_list, this, true);
            LinearLayout view = findViewById(R.id.root);
            for (int i = 0; i < view.getChildCount(); i++) {
                ConstraintLayout constraintLayout = (ConstraintLayout) view.getChildAt(i);
                layouts.add(constraintLayout.findViewById(R.id.sl1));
                layouts.add(constraintLayout.findViewById(R.id.sl2));
                layouts.add(constraintLayout.findViewById(R.id.sl3));
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void startAnim() {
        for (VeilLayout layout : layouts) {
            layout.veil();
        }
    }

    public void stopAnim() {
        for (VeilLayout layout : layouts) {
            layout.unVeil();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (View.VISIBLE != visibility) {
            stopAnim();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }
}
