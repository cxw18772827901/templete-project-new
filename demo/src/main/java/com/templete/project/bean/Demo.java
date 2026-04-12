package com.templete.project.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * PackageName  com.templete.project.bean
 * ProjectName  TempleteProject-java
 * Date         2022/5/11.
 *
 * @author xwchen
 */

public class Demo extends BaseObservable {
    private int type;

    public Demo(int type) {
        this.type = type;
    }

    @Bindable
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        notifyPropertyChanged(com.templete.project.BR.type);
    }
}
