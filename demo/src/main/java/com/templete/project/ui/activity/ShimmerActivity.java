package com.templete.project.ui.activity;

import com.lib.base.bean.BtnBean;
import com.lib.base.ui.activity.BaseActivity;
import com.templete.project.databinding.ShimmerActivityBinding;
import com.templete.project.ui.widget.ShimmerView;

/**
 * PackageName  com.templete.project.ui.activity
 * ProjectName  TempleteProject-java
 * Date         2022/11/17.
 *
 * @author xwchen
 */

public class ShimmerActivity extends BaseActivity<ShimmerActivityBinding> {
    @Override
    public void inits() {
        setRightClickViews((position, view) -> {
            mViewBinding.shimmerView.init(position == 0 ? ShimmerView.TYPE_GRID : ShimmerView.TYPE_LIST);
        }, false, new BtnBean("grid"), new BtnBean("list"));
    }

    @Override
    public void initView() {
        mViewBinding.shimmerView.init(ShimmerView.TYPE_GRID);
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected ShimmerActivityBinding viewBinding() {
        return ShimmerActivityBinding.inflate(getLayoutInflater());
    }
}
