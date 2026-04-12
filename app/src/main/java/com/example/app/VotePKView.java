package com.example.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app.databinding.VotePkViewBinding;

import java.util.List;

/**
 * Date         2025/10/20.
 *
 * @author xxx
 */

public class VotePKView extends FrameLayout {

    private com.example.app.databinding.VotePkViewBinding mBinding;
    private List<VoteItem> items;
    private boolean isVoted = false;

    public VotePKView(@NonNull Context context) {
        this(context, null);
    }

    public VotePKView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VotePKView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBinding = VotePkViewBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public void setOnItemChooseListener(OnItemChooseListener onItemChooseListener) {
        mBinding.voteView.setOnItemChooseListener(onItemChooseListener);
    }

    public void setData(List<VoteItem> items) {
        if (items == null || items.size() != 2) {
            throw new RuntimeException("error pk data, please check it");
        }
        isVoted = false;
        this.items = items;
        mBinding.voteView.checkData(items);
        calculatePercent();
        showInfo();
        showSubtitle();
    }

    private void calculatePercent() {
        long total = 0;
        for (VoteItem item : items) {
            total += item.getCount();
        }
        for (VoteItem item : items) {
            float percent;
            if (total > 0) {
                percent = (float) item.getCount() / total;
            } else {
                percent = 0f;
            }
            item.setPercent(percent);
            if (item.isVoted()) {
                isVoted = true;
            }
        }
    }

    public void choose(VoteSplitView.Side side) {
        isVoted = true;
        mBinding.voteView.choose(side);
        // 投票+1
        VoteItem item = items.get(side == VoteSplitView.Side.LEFT ? 0 : 1);
        item.setCount(item.getCount() + 1);
        item.setVoted(true);
        calculatePercent();
        showInfo();
        showSubtitle();
    }

    @SuppressLint("DefaultLocale")
    private void showInfo() {
        mBinding.tvSupportLeft.setVisibility(!isVoted ? View.VISIBLE : View.GONE);
        mBinding.tvSupportRight.setVisibility(!isVoted ? View.VISIBLE : View.GONE);
        mBinding.llLeft.setVisibility(isVoted ? View.VISIBLE : View.GONE);
        mBinding.llRight.setVisibility(isVoted ? View.VISIBLE : View.GONE);
        if (isVoted) {
            // 左侧
            VoteItem itemLeft = items.get(0);
            float percentValueLeft = itemLeft.getPercent() * 100;
            String percentTextLeft = String.format("%d (%.1f%%)", itemLeft.getCount(), percentValueLeft);
            if (percentTextLeft.contains("100")) {
                percentTextLeft = percentTextLeft.replace(".0", "");
            }
            mBinding.tvCountLeft.setText(percentTextLeft);
            mBinding.progressLeft.setProgress((int) percentValueLeft);

            //右侧
            VoteItem itemRight = items.get(1);
            float percentValueRight = itemRight.getPercent() * 100;
            String percentTextRight = String.format("%d (%.1f%%)", itemRight.getCount(), percentValueRight);
            if (percentTextRight.contains("100")) {
                percentTextRight = percentTextRight.replace(".0", "");
            }
            mBinding.tvCountRight.setText(percentTextRight);
            mBinding.progressRight.setProgress((int) percentValueRight);
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.viewHolder.getLayoutParams();
        params.height = dp(isVoted ? 8 : 12);
        mBinding.viewHolder.setLayoutParams(params);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    @SuppressLint("SetTextI18n")
    private void showSubtitle() {
        long total = 0;
        for (VoteItem item : items) {
            total += item.getCount();
        }
        mBinding.tvSubtitle.setText(total + "人参与  参与单选/2个选项");
    }

    public Pair<ImageView, ImageView> getImageViews() {
        return new Pair<>(mBinding.ivLeft, mBinding.ivRight);
    }

    public VoteSplitView getVoteSplitView() {
        return mBinding.voteView;
    }

    public TextView getSubtitle() {
        return mBinding.tvSubtitle;
    }
}
