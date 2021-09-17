package com.lishuaihua.indicator.common.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator
import com.lishuaihua.indicator.PositionData
import android.graphics.RectF
import android.view.View
import android.view.animation.Interpolator
import com.lishuaihua.indicator.FragmentContainerHelper
import com.lishuaihua.indicator.UIUtil

/**
 * 包裹住内容区域的指示器，类似天天快报的切换效果，需要和IPagerTitleView配合使用
 */
class WrapPagerIndicator(context: Context) : View(context), IPagerIndicator {
    var verticalPadding = 0
    var horizontalPadding = 0
    var fillColor = 0
    private var mRoundRadius = 0f
    private var mStartInterpolator: Interpolator? = LinearInterpolator()
    private var mEndInterpolator: Interpolator? = LinearInterpolator()
    private var mPositionDataList: List<PositionData>? = null
    var paint: Paint? = null
        private set
    private val mRect = RectF()
    private var mRoundRadiusSet = false
    private fun init(context: Context) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.style = Paint.Style.FILL
        verticalPadding = UIUtil.dip2px(context, 6.0)
        horizontalPadding = UIUtil.dip2px(context, 10.0)
    }

    override fun onDraw(canvas: Canvas) {
        paint!!.color = fillColor
        canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, paint!!)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算锚点位置
        val current = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position)
        val next = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position + 1)
        mRect.left =
            current.mContentLeft - horizontalPadding + (next.mContentLeft - current.mContentLeft) * mEndInterpolator!!.getInterpolation(
                positionOffset
            )
        mRect.top = (current.mContentTop - verticalPadding).toFloat()
        mRect.right =
            current.mContentRight + horizontalPadding + (next.mContentRight - current.mContentRight) * mStartInterpolator!!.getInterpolation(
                positionOffset
            )
        mRect.bottom = (current.mContentBottom + verticalPadding).toFloat()
        if (!mRoundRadiusSet) {
            mRoundRadius = mRect.height() / 2
        }
        invalidate()
    }

    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    var roundRadius: Float
        get() = mRoundRadius
        set(roundRadius) {
            mRoundRadius = roundRadius
            mRoundRadiusSet = true
        }
    var startInterpolator: Interpolator?
        get() = mStartInterpolator
        set(startInterpolator) {
            mStartInterpolator = startInterpolator
            if (mStartInterpolator == null) {
                mStartInterpolator = LinearInterpolator()
            }
        }
    var endInterpolator: Interpolator?
        get() = mEndInterpolator
        set(endInterpolator) {
            mEndInterpolator = endInterpolator
            if (mEndInterpolator == null) {
                mEndInterpolator = LinearInterpolator()
            }
        }

    init {
        init(context)
    }
}