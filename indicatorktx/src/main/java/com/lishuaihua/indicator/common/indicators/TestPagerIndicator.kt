package com.lishuaihua.indicator.common.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.lishuaihua.indicator.PositionData
import com.lishuaihua.indicator.FragmentContainerHelper

/**
 * 用于测试的指示器，可用来检测自定义的IPagerTitleView是否正确测量内容区域
 */
class TestPagerIndicator( context: Context) : View(context), IPagerIndicator {
    private var mPaint: Paint? = null
    var outRectColor = 0
    var innerRectColor = 0
    private val mOutRect = RectF()
    private val mInnerRect = RectF()
    private var mPositionDataList: List<PositionData>? = null
    private fun init(context: Context) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.STROKE
        outRectColor = Color.RED
        innerRectColor = Color.GREEN
    }

    override fun onDraw(canvas: Canvas) {
        mPaint!!.color = outRectColor
        canvas.drawRect(mOutRect, mPaint!!)
        mPaint!!.color = innerRectColor
        canvas.drawRect(mInnerRect, mPaint!!)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算锚点位置
        val current = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position)
        val next = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position + 1)
        mOutRect.left = current.mLeft + (next.mLeft - current.mLeft) * positionOffset
        mOutRect.top = current.mTop + (next.mTop - current.mTop) * positionOffset
        mOutRect.right = current.mRight + (next.mRight - current.mRight) * positionOffset
        mOutRect.bottom = current.mBottom + (next.mBottom - current.mBottom) * positionOffset
        mInnerRect.left =
            current.mContentLeft + (next.mContentLeft - current.mContentLeft) * positionOffset
        mInnerRect.top =
            current.mContentTop + (next.mContentTop - current.mContentTop) * positionOffset
        mInnerRect.right =
            current.mContentRight + (next.mContentRight - current.mContentRight) * positionOffset
        mInnerRect.bottom =
            current.mContentBottom + (next.mContentBottom - current.mContentBottom) * positionOffset
        invalidate()
    }

    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    init {
        init(context)
    }
}