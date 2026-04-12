package com.templete.project.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.viewpager2.widget.ViewPager2

/**
 * Date         2026/4/11.
 * @author      xxx
 */
class ViewPager2Container @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var viewPager2: ViewPager2? = null

    private var startX = 0f
    private var startY = 0f

    // 是否在 DOWN 时就禁止父拦截（一般 false）
    private var disallowParentInterceptDownEvent = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewPager2) {
                viewPager2 = child
                break
            }
        }
        requireNotNull(viewPager2) {
            "ViewPager2Container 必须包含一个 ViewPager2"
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val vp = viewPager2 ?: return super.onInterceptTouchEvent(ev)

        // 不需要处理的情况
        if (!vp.isUserInputEnabled ||
            vp.adapter == null ||
            vp.adapter!!.itemCount <= 1
        ) {
            return super.onInterceptTouchEvent(ev)
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                parent.requestDisallowInterceptTouchEvent(!disallowParentInterceptDownEvent)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = ev.x - startX
                val dy = ev.y - startY

                if (vp.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    handleHorizontal(dx, dy)
                } else {
                    handleVertical(dx, dy)
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    private fun handleHorizontal(dx: Float, dy: Float) {
        val vp = viewPager2 ?: return
        // val absDx = kotlin.math.abs(dx)
        // val absDy = kotlin.math.abs(dy)

        // if (absDx > absDy) {
        val current = vp.currentItem
        val last = (vp.adapter?.itemCount ?: 0) - 1

        when {
            // 👉 左边界 & 往右滑 → 给父
            current == 0 && dx > 0 -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
            // 👉 右边界 & 往左滑 → 给父
            current == last && dx < 0 -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
            // 👉 中间 → 自己吃
            else -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        // } else {
        //     parent.requestDisallowInterceptTouchEvent(false)
        // }
    }

    private fun handleVertical(dx: Float, dy: Float) {
        val vp = viewPager2 ?: return
        // val absDx = kotlin.math.abs(dx)
        // val absDy = kotlin.math.abs(dy)

        // if (absDy > absDx) {
        val current = vp.currentItem
        val last = (vp.adapter?.itemCount ?: 0) - 1

        when {
            current == 0 && dy > 0 -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }

            current == last && dy < 0 -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }

            else -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        // } else {
        //     parent.requestDisallowInterceptTouchEvent(false)
        // }
    }

    fun setDisallowParentInterceptDownEvent(disallow: Boolean) {
        disallowParentInterceptDownEvent = disallow
    }
}