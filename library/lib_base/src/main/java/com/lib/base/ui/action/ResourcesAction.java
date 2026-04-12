package com.lib.base.ui.action;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import com.lib.base.ui.CtxAction;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

/**
 * @author: Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/09/15
 * desc   : 根据Context处理Resources
 */
public interface ResourcesAction extends CtxAction {

    default LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getCtx());
    }

    default Resources getResources() {
        return getCtx().getResources();
    }

    default float getDimen(int id) {
        return getResources().getDimension(id);
    }

    default String getString(@StringRes int id) {
        return getCtx().getString(id);
    }

    default String getString(@StringRes int id, Object... formatArgs) {
        return getResources().getString(id, formatArgs);
    }

    default Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(getCtx(), id);
    }

    @ColorInt
    default int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getCtx(), id);
    }

    default <S> S getSystemService(@NonNull Class<S> serviceClass) {
        return ContextCompat.getSystemService(getCtx(), serviceClass);
    }
}