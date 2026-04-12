package com.example.app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.max

/**
 * vs纯背景图案:
 * 1.未投票:两侧T型背景等高;
 * 2.投票了一侧,则这一侧高度增加,另一侧保留原始高度.
 */
class VoteSplitView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    enum class Side { NONE, LEFT, RIGHT }

    // --- 属性定义 (保留用户原有和省略的属性) ---
    // private var leftText = "左侧"
    // private var rightText = "右侧"
    private var leftColor = Color.parseColor("#F53446")
    private var leftColorEnd = Color.parseColor("#FF7582")
    private var rightColor = Color.parseColor("#5982FF")
    private var rightColorEnd = Color.parseColor("#0D71EB")
    // private var textColor = Color.WHITE
    private var cornerRadius = dp(8f)
    private var dividerSlopeWidth = dp(16f)
    private var dividerWidth = dp(3f) / 2
    private var selectedSide = Side.NONE
    private var baseHeight = dp(69f)
    private var heightIncrease = dp(8f)
    // ------------------------------------------

    private val paintLeft = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val paintRight = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    // private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    // private val percentPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // --- 新增：用于点击检测的 Region 对象 ---
    private var leftRegion: Region = Region()
    private var rightRegion: Region = Region()
    private var onItemChooseListener: OnItemChooseListener? = null
    // ------------------------------------------

    private var downX = 0f
    private var downY = 0f

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.VoteSplitView)
        leftColor = ta.getColor(R.styleable.VoteSplitView_leftColor, leftColor)
        leftColorEnd = ta.getColor(R.styleable.VoteSplitView_leftColorEnd, leftColorEnd)
        rightColor = ta.getColor(R.styleable.VoteSplitView_rightColor, rightColor)
        rightColorEnd = ta.getColor(R.styleable.VoteSplitView_rightColorEnd, rightColorEnd)
        cornerRadius = ta.getDimension(R.styleable.VoteSplitView_cornerRadius, cornerRadius)
        dividerSlopeWidth = ta.getDimension(R.styleable.VoteSplitView_dividerSlopeWidth, dividerSlopeWidth)
        baseHeight = ta.getDimension(R.styleable.VoteSplitView_baseHeight, baseHeight)
        heightIncrease = ta.getDimension(R.styleable.VoteSplitView_heightIncrease, heightIncrease)
        ta.recycle()

        // textPaint.color = textColor
        // textPaint.textAlign = Align.CENTER
        // textPaint.textSize = sp(14f)

        // percentPaint.color = textColor
        // percentPaint.textAlign = Align.CENTER
        // percentPaint.textSize = sp(12f)
    }

    // === 自动适配最大一侧高度 ===
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = when (selectedSide) {
            Side.NONE -> baseHeight
            else -> baseHeight + heightIncrease
        }.toInt()
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat() // 获取实际绘制高度
        val slope = dividerSlopeWidth
        val r = cornerRadius
        val divW = dividerWidth

        // === 计算两侧高度 ===
        val leftH: Float
        val rightH: Float
        when (selectedSide) {
            Side.NONE -> {
                leftH = baseHeight
                rightH = baseHeight
            }

            Side.LEFT -> {
                leftH = baseHeight + heightIncrease
                rightH = baseHeight
            }

            Side.RIGHT -> {
                leftH = baseHeight
                rightH = baseHeight + heightIncrease
            }
        }

        // === 计算总高度（取最高的） ===
        val totalH = max(leftH, rightH)
        val cy = totalH / 2f
        val leftTop = cy - leftH / 2
        val leftBottom = cy + leftH / 2
        val rightTop = cy - rightH / 2
        val rightBottom = cy + rightH / 2

        val leftWidth = w / 2f

        // 用于斜面计算的缩放因子
        val scaleFactor = baseHeight / (baseHeight + heightIncrease)

        // --- 绘制底层白色背景 ---
        // 为了确保点击检测区域正确，我们不再绘制透明背景，直接进行形状绘制
        // val backgroundPath = Path().apply {
        //     addRoundRect(0f, 0f, w, totalH, r, r, Path.Direction.CW)
        // }
        // val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        // canvas.drawPath(backgroundPath, backgroundPaint)


        // === 左背景：修正圆角和斜面衔接 (Path 定义保持不变) ===
        val leftGradient = LinearGradient(
            0f, 0f, leftWidth, 0f,
            intArrayOf(leftColor, leftColorEnd), null, Shader.TileMode.CLAMP
        )
        paintLeft.shader = leftGradient

        val slopeLeft = if (selectedSide == Side.LEFT) slope else slope * scaleFactor

        val leftPath = Path().apply {
            moveTo(r, leftTop)
            lineTo(leftWidth + slopeLeft - divW, leftTop)
            lineTo(leftWidth - slopeLeft - divW, leftBottom)
            lineTo(r, leftBottom)
            arcTo(RectF(0f, leftBottom - r * 2, r * 2, leftBottom), 90f, 90f)
            lineTo(0f, leftTop + r)
            arcTo(RectF(0f, leftTop, r * 2, leftTop + r * 2), 180f, 90f)
            close()
        }
        canvas.drawPath(leftPath, paintLeft)
        paintLeft.shader = null

        // **新增：将 Path 转换为 Region 用于点击检测**
        val clip = Region(0, 0, w.toInt(), h.toInt())
        leftRegion.setPath(leftPath, clip)


        // === 右背景：保留缩放逻辑并添加圆角 (Path 定义保持不变) ===
        val rightGradient = LinearGradient(
            leftWidth, 0f, w, 0f,
            intArrayOf(rightColor, rightColorEnd), null, Shader.TileMode.CLAMP
        )
        paintRight.shader = rightGradient

        val slopeRight = if (selectedSide == Side.RIGHT) slope else slope * scaleFactor

        val rightPath = Path().apply {
            moveTo(leftWidth + slopeRight + divW, rightTop)
            lineTo(w - r, rightTop)
            arcTo(RectF(w - r * 2, rightTop, w, rightTop + r * 2), 270f, 90f)
            lineTo(w, rightBottom - r)
            arcTo(RectF(w - r * 2, rightBottom - r * 2, w, rightBottom), 0f, 90f)
            lineTo(leftWidth - slopeRight + divW, rightBottom)
            close()
        }
        canvas.drawPath(rightPath, paintRight)
        paintRight.shader = null

        // **新增：将 Path 转换为 Region 用于点击检测**
        rightRegion.setPath(rightPath, clip)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (selectedSide != Side.NONE) {
            return super.onTouchEvent(event)
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            downX = event.x
            downY = event.y
            return true
        } else if (event.action == MotionEvent.ACTION_UP) {
            val upX = event.x.toInt()
            val upY = event.y.toInt()
            // 判断是否为一次有效的点击（移动范围很小）
            if (abs(upX - downX) < 20 && abs(upY - downY) < 20) {
                if (leftRegion.contains(upX, upY)) {
                    // 1. 检查点击是否在左侧区域内
                    performClick() // 触发辅助功能
                    onItemChooseListener?.choose(true)
                    return true
                } else if (rightRegion.contains(upX, upY)) {
                    // 2. 检查点击是否在右侧区域内
                    performClick() // 触发辅助功能
                    onItemChooseListener?.choose(false)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    // 由于重写了 onTouchEvent，为了辅助功能合规性，需要重写 performClick
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    // private fun drawTextCenter(canvas: Canvas, text: String, cx: Float, cy: Float, paint: Paint) {
    //     val fm = paint.fontMetrics
    //     val baseline = cy - (fm.ascent + fm.descent) / 2
    //     canvas.drawText(text, cx, baseline, paint)
    // }

    private fun dp(v: Float) = v * resources.displayMetrics.density
    private fun sp(v: Float) = v * resources.displayMetrics.scaledDensity

    // === 外部接口 ===
    fun setSelectedSide(side: Side) {
        selectedSide = side
        requestLayout() // 高度变化时需要重新测量
        invalidate()
    }

    fun setOnItemChooseListener(listener: OnItemChooseListener) {
        this.onItemChooseListener = listener
    }

    // fun setPercent(left: Float, right: Float) {
    //     leftPercentValue = left
    //     rightPercentValue = right
    //     invalidate()
    // }

    // fun setData(list: List<VoteItem>) {
    //     //isVoted = false
    //     checkData(list)
    //     // 参与count
    //     val total = list.sumOf { it.count }
    //     this@VoteResultView.subtitle = "${total}人参与  参与单选/${list.size}个选项"
    //     items.clear(); items.addAll(list); requestLayout(); invalidate()
    // }

    fun checkData(list: List<VoteItem>) {
        selectedSide = Side.NONE
        // val total = list.sumOf { it.count }
        for (item in list) {
            // item.percent = if (total > 0) item.count.toFloat() / total else 0f
            // if (item.isVoted) {
            //     isVoted = true
            // }
            if (item.isVoted) {
                selectedSide = if (list.indexOf(item) == 0) Side.LEFT else Side.RIGHT
            }
        }
        requestLayout()
        invalidate()
    }

    fun choose(side: Side) {
        selectedSide = side
        requestLayout()
        invalidate()
    }
}

fun interface OnItemChooseListener {
    fun choose(left: Boolean)
}