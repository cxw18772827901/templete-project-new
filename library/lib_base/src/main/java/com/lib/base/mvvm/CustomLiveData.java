package com.lib.base.mvvm;

/**
 * 参考官方自带{@link MutableLiveData}自定义MutableLiveData,结合自定义{@link NoStickyLiveData}使用
 * <p>
 * ProjectName  XSCat
 * PackageName  com.bigheadhorse.xscat.utils
 * @author      xwchen
 * Date         2021/6/16.
 */

/**
 * 使用{@link CusLiveData}代替
 *
 * @param <T>
 */
@Deprecated
public class CustomLiveData<T> extends NoStickyLiveData<T> {
    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }
}
