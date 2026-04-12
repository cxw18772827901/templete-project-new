package com.example.app

import com.example.app.databinding.ActivityVoteListBinding
import com.lib.base.ui.activity.BaseActivity

/**
 * Date         2025/10/19.
 *
 * @author xxx
 */
class VoteListActivity : BaseActivity<ActivityVoteListBinding?>() {
    override fun viewBinding(): ActivityVoteListBinding {
        return ActivityVoteListBinding.inflate(layoutInflater)
    }

    override fun inits() {
    }

    override fun initView() {
    }

    override fun initEvent() {
    }

    override fun initData() {
        mViewBinding?.apply {
            voteView.setTitle("我是新的标题我是新的标题我是新的标题我是新的标题我是新的标题我是新的标题")
            voteView.setData(
                listOf(
                    VoteItem("苹果", 1, true),
                    VoteItem("香蕉", 2),
                    VoteItem("橙子", 3)
                )
            )
            voteView.setSubtitle("我是小标题我是小标题我是小标题我是小标题我是小标题我是小标题我是小标题我是小标题我是小标题我是小标题我是小标题我是小标题")
        }
    }
}
