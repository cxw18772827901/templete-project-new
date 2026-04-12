package com.templete.project.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.util.List;

/**
 * Date         2026/4/10.
 *
 * @author xxx
 */

public class NavBean implements Parcelable {
    public Pair<Integer, Integer> indexPair;
    public String name;
    public String data;
    public boolean isSelect;
    public List<NavBean> navs;

    public NavBean(String name, String data, boolean isSelect, List<NavBean> navs) {
        this.name = name;
        this.data = data;
        this.isSelect = isSelect;
        this.navs = navs;
    }

    protected NavBean(Parcel in) {
        name = in.readString();
        data = in.readString();
        isSelect = in.readByte() != 0;
        navs = in.createTypedArrayList(NavBean.CREATOR);
    }

    public static final Creator<NavBean> CREATOR = new Creator<NavBean>() {
        @Override
        public NavBean createFromParcel(Parcel in) {
            return new NavBean(in);
        }

        @Override
        public NavBean[] newArray(int size) {
            return new NavBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(data);
        parcel.writeByte((byte) (isSelect ? 1 : 0));
        parcel.writeTypedList(navs);
    }
}
