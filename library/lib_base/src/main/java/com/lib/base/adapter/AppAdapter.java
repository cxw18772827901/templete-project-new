package com.lib.base.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.lib.base.util.ContextUtil;
import com.lib.base.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;


/**
 * 单类型adapter,多类型对应{@link MultiAppAdapter}
 *
 * @author: Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/12/19
 * desc   : RecyclerView 适配器业务基类
 */
public class AppAdapter<T> extends BaseAdapter<BaseAdapter<?>.ViewHolder> implements DefaultLifecycleObserver {
    public static final String TAG = "AppAdapter";

    /**
     * 列表数据
     */
    private List<T> mDataSet;

    public AppAdapter(@NonNull Context context) {
        super(context);
        addObserver(context);
    }

    private void addObserver(Context context) {
        try {
            LifecycleOwner lifecycleOwner = ContextUtil.getLifecycleOwnerByContext(context);
            if (lifecycleOwner != null) {
                lifecycleOwner.getLifecycle().addObserver(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * clear list,释放内存.注意Arrays.asList()返回的是数组.
     */
    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        try {
            DebugUtil.logD(TAG, "pop onDestroy");
            if (mDataSet != null && mDataSet instanceof ArrayList) {
                mDataSet.clear();
            }
            mDataSet = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    /**
     * 获取数据总数
     */
    public int getCount() {
        if (mDataSet == null) {
            return 0;
        }
        return mDataSet.size();
    }

    /**
     * 设置新的数据
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setData(@Nullable List<T> data) {
        mDataSet = data;
        notifyDataSetChanged();
    }

    /**
     * 获取当前数据
     */
    @Nullable
    public List<T> getData() {
        return mDataSet;
    }

    /**
     * 追加一些数据
     */
    public void addData(List<T> data) {
        if (data == null || data.size() == 0) {
            return;
        }

        if (mDataSet == null || mDataSet.size() == 0) {
            setData(data);
            return;
        }

        mDataSet.addAll(data);
        notifyItemRangeInserted(mDataSet.size() - data.size(), data.size());
    }

    /**
     * 清空当前数据
     */
    @SuppressLint("NotifyDataSetChanged")
    public void clearData() {
        if (mDataSet == null || mDataSet.size() == 0) {
            return;
        }

        mDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * 是否包含了某个位置上的条目数据
     */
    public boolean containsItem(@IntRange(from = 0) int position) {
        return containsItem(getItem(position));
    }

    /**
     * 是否包含某个条目数据
     */
    public boolean containsItem(T item) {
        if (mDataSet == null || item == null) {
            return false;
        }
        return mDataSet.contains(item);
    }

    /**
     * 获取某个位置上的数据
     */
    public T getItem(@IntRange(from = 0) int position) {
        if (mDataSet == null) {
            return null;
        }
        return mDataSet.get(position);
    }

    /**
     * 更新某个位置上的数据
     */
    public void setItem(@IntRange(from = 0) int position, @NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        mDataSet.set(position, item);
        notifyItemChanged(position);
    }

    /**
     * 添加单条数据
     */
    public void addItem(@NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        addItem(mDataSet.size(), item);
    }

    public void addItem(@IntRange(from = 0) int position, @NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }

        if (position < mDataSet.size()) {
            mDataSet.add(position, item);
        } else {
            mDataSet.add(item);
            position = mDataSet.size() - 1;
        }
        notifyItemInserted(position);
    }

    /**
     * 删除单条数据
     */
    public void removeItem(@NonNull T item) {
        int index = mDataSet.indexOf(item);
        if (index != -1) {
            removeItem(index);
        }
    }

    public void removeItem(@IntRange(from = 0) int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
        if (position != getCount() - 1) {
            notifyItemRangeChanged(position, getCount() - position);
        }
    }

    /**
     * 两条数据交换,不能简单的使用
     * mDataSet.add(toPosition, mDataSet.remove(fromPosition));//数据更换
     * notifyItemMoved(fromPosition, toPosition);//执行动画
     * notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1);//受影响的itemd都刷新下
     *
     * @param fromPosition
     * @param toPosition
     */
    public void itemMove(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        int from = Math.min(fromPosition, toPosition);
        int to = Math.max(fromPosition, toPosition);
        T removeFrom = mDataSet.get(from);
        T removeTo = mDataSet.get(to);
        mDataSet.set(from, removeTo);
        mDataSet.set(to, removeFrom);
        notifyItemMoved(from, to);//执行动画
        notifyItemRangeChanged(from, to - from + 1);//受影响的itemd都刷新下
    }

    public final class SimpleHolder extends ViewHolder {

        public SimpleHolder(@LayoutRes int id) {
            super(id);
        }

        public SimpleHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(int position) {
        }
    }
}