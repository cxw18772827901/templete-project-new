package com.templete.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hjq.shape.view.recyclerList.PinnedSectionListView;
import com.templete.project.R;
import com.templete.project.bean.DoorBean;

import java.util.ArrayList;

/**
 * PackageName  com.bbb.fastcloud.mvp.ui.adapter
 * ProjectName  FastCloud
 * Date         2022/10/22.
 *
 * @author xwchen
 */

public class DoorEventAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter/*, LoadMoreModule*/ {
    public static final String TAG = "DoorEventAdapter";

    public static final int TYPE_HEAD = 0;

    public static final int TYPE_ITEM = 1;

    public static final int TYPE_LOADING = 2;

    public static final int TYPE_EMPTY = 3;

    private ArrayList<DoorBean> list;

    public DoorEventAdapter(ArrayList<DoorBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public DoorBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = list.get(position).itemType;
        if (TYPE_HEAD == itemViewType) {
            HeaderHolder headerHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_door_event_head, null);
                headerHolder = new HeaderHolder(convertView);
                convertView.setTag(headerHolder);
            } else {
                headerHolder = (HeaderHolder) convertView.getTag();
            }
            bindHeader(headerHolder);
        } else {
            ItemHolder itemHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_door_event_item, null);
                itemHolder = new ItemHolder(convertView);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) convertView.getTag();
            }
            bindItem(itemHolder);
        }
        return convertView;
    }

    private void bindItem(ItemHolder itemHolder) {

    }

    private void bindHeader(HeaderHolder headerHolder) {

    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).itemType;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
    }

    public void setList(ArrayList<DoorBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class HeaderHolder {

        public HeaderHolder(View convertView) {

        }
    }

    static class ItemHolder {
        public ItemHolder(View convertView) {

        }
    }
}
