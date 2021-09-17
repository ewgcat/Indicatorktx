package com.lishuaihua.indicator.common.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.view.animation.Interpolator
import com.lishuaihua.indicator.PositionData
import android.view.animation.LinearInterpolator
import com.lishuaihua.indicator.FragmentContainerHelper
import com.lishuaihua.indicator.UIUtil

/**
 * 带有小尖角的直线指示器
 */
class TriangularPagerIndicator(context: Context) : View(context), IPagerIndicator {
    private var mPositionDataList: List<PositionData>? = null
    private var mPaint: Paint? = null
    var lineHeight = 0
    var lineColor = 0
    var triangleHeight = 0
    var triangleWidth = 0
    var isReverse = false
    var yOffset = 0f
    private val mPath = Path()
    private var mStartInterpolator: Interpolator? = LinearInterpolator()
    private var mAnchorX = 0f
    private fun init(context: Context) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.FILL
        lineHeight = UIUtil.dip2px(context, 3.0)
        triangleWidth = UIUtil.dip2px(context, 14.0)
        triangleHeight = UIUtil.dip2px(context, 8.0)
    }

    override fun onDraw(canvas: Canvas) {
        mPaint!!.color = lineColor
        if (isReverse) {
            canvas.drawRect(
                0f,
                height - yOffset - triangleHeight,
                width.toFloat(),
                height - yOffset - triangleHeight + lineHeight,
                mPaint!!
            )
        } else {
            canvas.drawRect(
                0f,
                height - lineHeight - yOffset,
                width.toFloat(),
                height - yOffset,
                mPaint!!
            )
        }
        mPath.reset()
        if (isReverse) {
            mPath.moveTo(mAnchorX - triangleWidth / 2, height - yOffset - triangleHeight)
            mPath.lineTo(mAnchorX, height - yOffset)
            mPath.lineTo(mAnchorX + triangleWidth / 2, height - yOffset - triangleHeight)
        } else {
            mPath.moveTo(mAnchorX - triangleWidth / 2, height - yOffset)
            mPath.lineTo(mAnchorX, height - triangleHeight - yOffset)
            mPath.lineTo(mAnchorX + triangleWidth / 2, height - yOffset)
        }
        mPath.close()
        canvas.drawPath(mPath, mPaint!!)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算锚点位置
        val current = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position)
        val next = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position + 1)
        val leftX = (current.mLeft + (current.mRight - current.mLeft) / 2).toFloat()
        val rightX = (next.mLeft + (next.mRight - next.mLeft) / 2).toFloat()
        mAnchorX = leftX + (rightX - leftX) * mStartInterpolator!!.getInterpolation(positionOffset)
        invalidate()
    }

    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    var startInterpolator: Interpolator?
        get() = mStartInterpolator
        set(startInterpolator) {
            mStartInterpolator = startInterpolator
            if (mStartInterpolator == null) {
                mStartInterpolator = LinearInterpolator()
            }
        }

    init {
        init(context)
    }
}