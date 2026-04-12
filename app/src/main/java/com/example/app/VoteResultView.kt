package com.example.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

data class VoteItem(
    val name: String,
    var count: Int,
    var isVoted: Boolean = false,
    var percent: Float = 0f
)

class VoteResultView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val items = mutableListOf<VoteItem>()

    // --- 可配置属性 ---
    private var itemHeight: Float
    private var itemSpacing: Float
    private var cornerRadius: Float
    private var colorBorderVoted: Int
    private var colorProgress: Int
    private var colorProgressVoted: Int
    private var colorText: Int
    private var colorTextVoted: Int
    private var title: String?
    private var titleTextSize: Float
    private var titleTextColor: Int
    private var titleMarginBottom: Float
    private var subtitle: String?
    private var subtitleTextSize: Float
    private var subtitleTextColor: Int
    private var subtitleMarginTop: Float

    // --- 绘制工具 ---
    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val overallBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    // --- 可复用对象 ---
    private val itemRect = RectF()
    private val progressRect = RectF()
    private val overallBgRect = RectF()
    private val textFm: Paint.FontMetrics
    private val titleFm: Paint.FontMetrics
    private val subtitleFm: Paint.FontMetrics

    private var titleLayout: StaticLayout? = null
    private var subtitleLayout: StaticLayout? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.VoteResultView)
        itemHeight = ta.getDimension(R.styleable.VoteResultView_itemHeight, dp(40f))
        itemSpacing = ta.getDimension(R.styleable.VoteResultView_itemSpacing, dp(10f))
        cornerRadius = ta.getDimension(R.styleable.VoteResultView_roundCornerRadius, dp(8f))
        val textSize = ta.getDimension(R.styleable.VoteResultView_voteTextSize, sp(14f))

        colorBorderVoted = ta.getColor(R.styleable.VoteResultView_borderColorVoted, Color.parseColor("#F30118"))
        colorProgress = ta.getColor(R.styleable.VoteResultView_progressColor, Color.parseColor("#EEEEEE"))
        colorProgressVoted = ta.getColor(R.styleable.VoteResultView_progressColorVoted, Color.parseColor("#FFE5E5"))
        colorText = ta.getColor(R.styleable.VoteResultView_voteTextColor, Color.parseColor("#191919"))
        colorTextVoted = ta.getColor(R.styleable.VoteResultView_voteTextColorVoted, Color.parseColor("#F30118"))

        title = ta.getString(R.styleable.VoteResultView_voteTitle)
        titleTextSize = ta.getDimension(R.styleable.VoteResultView_voteTitleSize, sp(14f))
        titleTextColor = ta.getColor(R.styleable.VoteResultView_voteTitleColor, Color.parseColor("#191919"))
        titleMarginBottom = ta.getDimension(R.styleable.VoteResultView_voteTitleMarginBottom, dp(10f))

        subtitle = ta.getString(R.styleable.VoteResultView_voteSubtitle)
        subtitleTextSize = ta.getDimension(R.styleable.VoteResultView_voteSubtitleSize, sp(12f))
        subtitleTextColor = ta.getColor(R.styleable.VoteResultView_voteSubtitleColor, Color.parseColor("#8D8D8D"))
        subtitleMarginTop = ta.getDimension(R.styleable.VoteResultView_voteSubtitleMarginTop, dp(10f))

        var bgColor = ta.getColor(R.styleable.VoteResultView_voteBgColor, Color.parseColor("#FAFAFA"))
        ta.recycle()

        textPaint.textSize = textSize
        textFm = textPaint.fontMetrics

        titlePaint.textSize = titleTextSize
        titlePaint.color = titleTextColor
        // titlePaint.isFakeBoldText = true
        titleFm = titlePaint.fontMetrics

        subtitlePaint.textSize = subtitleTextSize
        subtitlePaint.color = subtitleTextColor
        subtitleFm = subtitlePaint.fontMetrics

        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeWidth = dp(0.8f)
        paintBg.color = Color.WHITE
        overallBgPaint.color = bgColor//Color.parseColor("#FAFAFA")
    }

    fun setData(list: List<VoteItem>) {
        items.clear(); items.addAll(list); requestLayout(); invalidate()
    }

    fun setTitle(newTitle: String?) {
        this.title = newTitle; requestLayout(); invalidate()
    }

    fun setSubtitle(newSubtitle: String?) {
        this.subtitle = newSubtitle; requestLayout(); invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = w - paddingLeft - paddingRight

        // --- 重新创建或清除布局 ---
        // 如果有标题，就创建它的 StaticLayout
        titleLayout = if (!title.isNullOrEmpty()) {
            createStaticLayout(title!!, titlePaint, availableWidth)
        } else {
            null
        }

        // 如果有副标题，也创建它的 StaticLayout
        subtitleLayout = if (!subtitle.isNullOrEmpty()) {
            createStaticLayout(subtitle!!, subtitlePaint, availableWidth)
        } else {
            null
        }

        var h = (paddingTop + paddingBottom).toFloat()

        // --- 使用 StaticLayout 的高度进行测量 ---
        titleLayout?.let {
            h += it.height + titleMarginBottom
        }

        if (items.isNotEmpty()) {
            h += (items.size * itemHeight + (items.size - 1) * itemSpacing)
        }

        subtitleLayout?.let {
            if (items.isNotEmpty() || titleLayout != null) {
                h += subtitleMarginTop
            }
            h += it.height
        }

        if (titleLayout == null && items.isEmpty() && subtitleLayout == null) {
            h = 0f
        }

        setMeasuredDimension(w, h.toInt())
    }

    // --- 辅助方法：创建 StaticLayout ---
    private fun createStaticLayout(text: CharSequence, paint: TextPaint, width: Int): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width).build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (height == 0) return
        if (!isInEditMode) {
            overallBgRect.set(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawRoundRect(overallBgRect, cornerRadius, cornerRadius, overallBgPaint)
        }
        super.onDraw(canvas)

        var top = paddingTop.toFloat()
        val w = width.toFloat()

        // 2. 绘制主标题 (使用 StaticLayout)
        titleLayout?.let { layout ->
            canvas.save()
            // 将画布移动到标题该在的位置
            canvas.translate(paddingLeft.toFloat(), top)
            layout.draw(canvas)
            canvas.restore()
            // 更新 top 位置
            top += layout.height + titleMarginBottom
        }

        if (items.isNotEmpty()) {
            for (i in items.indices) {
                itemRect.set(paddingLeft.toFloat(), top, w - paddingRight, top + itemHeight)
                drawItem(canvas, items[i], itemRect)
                top += itemHeight + itemSpacing
            }
        }

        // 4. 绘制副标题 (使用 StaticLayout)
        subtitleLayout?.let { layout ->
            // 更新 top 位置以包含边距
            if (items.isNotEmpty() || titleLayout != null) {
                top += subtitleMarginTop
            }
            // 如果列表存在，top 在上次循环后多加了一个 itemSpacing，需要减掉
            if (items.isNotEmpty()) {
                top -= itemSpacing
            }

            canvas.save()
            // 将画布移动到副标题该在的位置
            canvas.translate(paddingLeft.toFloat(), top)
            layout.draw(canvas)
            canvas.restore()
        }
    }

    private fun drawItem(canvas: Canvas, item: VoteItem, rect: RectF) {
        val isVoted = item.isVoted
        paintBorder.color = colorBorderVoted
        paintProgress.color = if (isVoted) colorProgressVoted else colorProgress
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paintBg)
        val progressWidth = rect.width() * item.percent
        if (progressWidth > 0) {
            progressRect.set(rect.left, rect.top, rect.left + progressWidth, rect.bottom)
            canvas.save()
            canvas.clipRect(progressRect)
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paintProgress)
            canvas.restore()
        }
        if (isVoted) {
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paintBorder)
        }
        drawItemText(canvas, item, rect)
    }

    private fun drawItemText(canvas: Canvas, item: VoteItem, rect: RectF) {
        textPaint.color = if (item.isVoted) colorTextVoted else colorText /*subtitleTextColor*/
        val baseLine = rect.centerY() - (textFm.ascent + textFm.descent) / 2
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(item.name, rect.left + dp(12f), baseLine, textPaint)
        val percentValue = item.percent * 100
        val percentText = String.format("%d (%.1f%%)", item.count, percentValue)
        textPaint.textAlign = Paint.Align.RIGHT
        textPaint.color = if (item.isVoted) colorTextVoted else /*colorText*/ subtitleTextColor
        canvas.drawText(percentText, rect.right - dp(12f), baseLine, textPaint)
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density
    private fun sp(v: Float) = v * resources.displayMetrics.scaledDensity
}