package com.example.app;

import android.util.Pair;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app.databinding.ActivityMainBinding;
import com.lib.base.ui.activity.BaseActivity;

import java.util.Arrays;

public class VotePKActivity extends BaseActivity<ActivityMainBinding> {
    @Override
    public void inits() {
        setTitleStr("首页");
    }

    @Override
    public void initView() {
        // ①获取图片
        Pair<ImageView, ImageView> pair = mViewBinding.pkView.getImageViews();
        // 左侧图片
        ImageView ivLeft = pair.first;
        // 右侧图片
        ImageView ivRight = pair.second;


        // ②设置投票数据
        mViewBinding.pkView.setData(
                Arrays.asList(
                        // new VoteItem("苹果", 0, false, 0f), // false,没投过票,可点击投票
                        // new VoteItem("香蕉", 0, false, 0f)

                        new VoteItem("苹果", 11, false, 0f), // true,投过票,不能再投票了
                        new VoteItem("香蕉", 22, false, 0f)
                )
        );


        // ③点击投票
        mViewBinding.pkView.setOnItemChooseListener(left -> {
            Toast.makeText(this, "投票了" + (left ? "左边" : "右边"), Toast.LENGTH_SHORT).show();
            // todo 投票api操作
            // ...

            mViewBinding.pkView.choose(left ? VoteSplitView.Side.LEFT : VoteSplitView.Side.RIGHT);
        });
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected ActivityMainBinding viewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }
}