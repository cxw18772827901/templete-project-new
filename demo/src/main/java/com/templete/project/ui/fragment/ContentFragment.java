package com.templete.project.ui.fragment;

import static com.templete.project.ui.fragment.ContainerFragment.NAV;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.greendao.db.util.GsonUtil;
import com.lib.base.ui.fragment.BaseFragment;
import com.lib.base.util.DebugUtil;
import com.templete.project.BuildConfig;
import com.templete.project.bean.NavBean;
import com.templete.project.databinding.ContentFragmentBinding;

/**
 * Date         2026/4/10.
 *
 * @author xxx
 */

public class ContentFragment extends BaseFragment<ContentFragmentBinding> {
    public static final String TAG = "ContentFragment";
    private NavBean nav;
    private boolean isRequested; // 是否请求过，目的是避免重复请求

    public static ContentFragment newInstance(NavBean nav) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NAV, nav);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected ContentFragmentBinding viewBinding(LayoutInflater inflater, ViewGroup container) {
        return ContentFragmentBinding.inflate(inflater, container, false);
    }

    @Override
    public void inits() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            nav = arguments.getParcelable(NAV);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {
        if (nav != null) {
            mViewBinding.tv.setText(nav.name + "的fragment");
        }
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {
        isRequested = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isRequested) {
            getData();
            isRequested = true;
        } else {
            DebugUtil.logD(TAG, "onResume,isRequested = " + true);
        }
    }

    private void getData() {
        // 这里是网络请求
        // xxx
        if (BuildConfig.DEBUG) {
            DebugUtil.logD(TAG, "getData,nav = " + GsonUtil.toJson(nav));
        }
    }
}