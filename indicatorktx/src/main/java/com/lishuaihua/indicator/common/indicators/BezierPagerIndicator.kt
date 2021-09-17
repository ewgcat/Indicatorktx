package com.lishuaihua.indicator.common.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import com.lishuaihua.indicator.PositionData
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import com.lishuaihua.indicator.common.ArgbEvaluatorHolder
import com.lishuaihua.indicator.FragmentContainerHelper
import com.lishuaihua.indicator.UIUtil

/**
 * 贝塞尔曲线ViewPager指示器，带颜色渐变
 */
class BezierPagerIndicator(context: Context) : View(context), IPagerIndicator {
    private var mPositionDataList: List<PositionData>? = null
    private var mLeftCircleRadius = 0f
    private var mLeftCircleX = 0f
    private var mRightCircleRadius = 0f
    private var mRightCircleX = 0f
    var yOffset = 0f
    var maxCircleRadius = 0f
    var minCircleRadius = 0f
    private var mPaint: Paint? = null
    private val mPath = Path()
    private var mColors: List<Int>? = null
    private var mStartInterpolator: Interpolator? = AccelerateInterpolator()
    private var mEndInterpolator: Interpolator? = DecelerateInterpolator()
    private fun init(context: Context) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.FILL
        maxCircleRadius = UIUtil.dip2px(context, 3.5).toFloat()
        minCircleRadius = UIUtil.dip2px(context, 2.0).toFloat()
        yOffset = UIUtil.dip2px(context, 1.5).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(
            mLeftCircleX,
            height - yOffset - maxCircleRadius,
            mLeftCircleRadius,
            mPaint!!
        )
        canvas.drawCircle(
            mRightCircleX,
            height - yOffset - maxCircleRadius,
            mRightCircleRadius,
            mPaint!!
        )
        drawBezierCurve(canvas)
    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param canvas
     */
    private fun drawBezierCurve(canvas: Canvas) {
        mPath.reset()
        val y = height - yOffset - maxCircleRadius
        mPath.moveTo(mRightCircleX, y)
        mPath.lineTo(mRightCircleX, y - mRightCircleRadius)
        mPath.quadTo(
            mRightCircleX + (mLeftCircleX - mRightCircleX) / 2.0f,
            y,
            mLeftCircleX,
            y - mLeftCircleRadius
        )
        mPath.lineTo(mLeftCircleX, y + mLeftCircleRadius)
        mPath.quadTo(
            mRightCircleX + (mLeftCircleX - mRightCircleX) / 2.0f,
            y,
            mRightCircleX,
            y + mRightCircleRadius
        )
        mPath.close() // 闭合
        canvas.drawPath(mPath, mPaint!!)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算颜色
        if (mColors != null && mColors!!.size > 0) {
            val currentColor = mColors!![Math.abs(position) % mColors!!.size]
            val nextColor = mColors!![Math.abs(position + 1) % mColors!!.size]
            val color = ArgbEvaluatorHolder.eval(positionOffset, currentColor, nextColor)
            mPaint!!.color = color
        }

        // 计算锚点位置
        val current = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position)
        val next = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position + 1)
        val leftX = (current.mLeft + (current.mRight - current.mLeft) / 2).toFloat()
        val rightX = (next.mLeft + (next.mRight - next.mLeft) / 2).toFloat()
        mLeftCircleX =
            leftX + (rightX - leftX) * mStartInterpolator!!.getInterpolation(positionOffset)
        mRightCircleX =
            leftX + (rightX - leftX) * mEndInterpolator!!.getInterpolation(positionOffset)
        mLeftCircleRadius =
            maxCircleRadius + (minCircleRadius - maxCircleRadius) * mEndInterpolator!!.getInterpolation(
                positionOffset
            )
        mRightCircleRadius =
            minCircleRadius + (maxCircleRadius - minCircleRadius) * mStartInterpolator!!.getInterpolation(
                positionOffset
            )
        invalidate()
    }

    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    fun setColors(vararg colors: Int?) {
        mColors = listOf(*colors) as List<Int>?
    }

    fun setStartInterpolator(startInterpolator: Interpolator?) {
        mStartInterpolator = startInterpolator
        if (mStartInterpolator == null) {
            mStartInterpolator = AccelerateInterpolator()
        }
    }

    fun setEndInterpolator(endInterpolator: Interpolator?) {
        mEndInterpolator = endInterpolator
        if (mEndInterpolator == null) {
            mEndInterpolator = DecelerateInterpolator()
        }
    }

    init {
        init(context)
    }
}