package com.lishuaihua.indicator.navigator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.lishuaihua.indicator.IPagerNavigator
import android.view.animation.LinearInterpolator
import android.graphics.PointF
import android.view.ViewConfiguration
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import com.lishuaihua.indicator.UIUtil
import java.util.ArrayList

/**
 * 圆圈式的指示器
 */
class CircleNavigator(context: Context) : View(context), IPagerNavigator {
    private var mRadius = 0
    private var mCircleColor = 0
    private var mStrokeWidth = 0
    private var mCircleSpacing = 0
    private var mCurrentIndex = 0

    // 此处不调用invalidate，让外部调用notifyDataSetChanged
    var circleCount = 0
    private var mStartInterpolator: Interpolator? = LinearInterpolator()
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCirclePoints: MutableList<PointF> = ArrayList()
    private var mIndicatorX = 0f

    // 事件回调
    var isTouchable = false
    private var mCircleClickListener: OnCircleClickListener? = null
    private var mDownX = 0f
    private var mDownY = 0f
    private var mTouchSlop = 0
    var isFollowTouch = true // 是否跟随手指滑动
    private fun init(context: Context) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mRadius = UIUtil.dip2px(context, 3.0)
        mCircleSpacing = UIUtil.dip2px(context, 8.0)
        mStrokeWidth = UIUtil.dip2px(context, 1.0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var result = 0
        when (mode) {
            MeasureSpec.EXACTLY -> result = width
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> result =
                circleCount * mRadius * 2 + (circleCount - 1) * mCircleSpacing + paddingLeft + paddingRight + mStrokeWidth * 2
            else -> {
            }
        }
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        var result = 0
        when (mode) {
            MeasureSpec.EXACTLY -> result = height
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> result =
                mRadius * 2 + mStrokeWidth * 2 + paddingTop + paddingBottom
            else -> {
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = mCircleColor
        drawCircles(canvas)
        drawIndicator(canvas)
    }

    private fun drawCircles(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        var i = 0
        val j = mCirclePoints.size
        while (i < j) {
            val pointF = mCirclePoints[i]
            canvas.drawCircle(pointF.x, pointF.y, mRadius.toFloat(), mPaint)
            i++
        }
    }

    private fun drawIndicator(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        if (mCirclePoints.size > 0) {
            val fl = height / 2.0f + 0.5f

            canvas.drawCircle(
                mIndicatorX,
                fl,
                mRadius.toFloat(),
                mPaint
            )
        }
    }

    private fun prepareCirclePoints() {
        mCirclePoints.clear()
        if (circleCount > 0) {
            val y = (height / 2.0f + 0.5f).toInt()
            val centerSpacing = mRadius * 2 + mCircleSpacing
            var startX = mRadius + (mStrokeWidth / 2.0f + 0.5f).toInt() + paddingLeft
            for (i in 0 until circleCount) {
                val pointF = PointF(startX.toFloat(), y.toFloat())
                mCirclePoints.add(pointF)
                startX += centerSpacing
            }
            mIndicatorX = mCirclePoints[mCurrentIndex].x
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (isFollowTouch) {
            if (mCirclePoints.isEmpty()) {
                return
            }
            val currentPosition = Math.min(mCirclePoints.size - 1, position)
            val nextPosition = Math.min(mCirclePoints.size - 1, position + 1)
            val current = mCirclePoints[currentPosition]
            val next = mCirclePoints[nextPosition]
            mIndicatorX = current.x + (next.x - current.x) * mStartInterpolator!!.getInterpolation(
                positionOffset
            )
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (isTouchable) {
                mDownX = x
                mDownY = y
                return true
            }
            MotionEvent.ACTION_UP -> if (mCircleClickListener != null) {
                if (Math.abs(x - mDownX) <= mTouchSlop && Math.abs(y - mDownY) <= mTouchSlop) {
                    var max = Float.MAX_VALUE
                    var index = 0
                    var i = 0
                    while (i < mCirclePoints.size) {
                        val pointF = mCirclePoints[i]
                        val offset = Math.abs(pointF.x - x)
                        if (offset < max) {
                            max = offset
                            index = i
                        }
                        i++
                    }
                    mCircleClickListener!!.onClick(index)
                }
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onPageSelected(position: Int) {
        mCurrentIndex = position
        if (!isFollowTouch) {
            mIndicatorX = mCirclePoints[mCurrentIndex].x
            invalidate()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        prepareCirclePoints()
    }

    override fun onAttachToIndicator() {}
    override fun notifyDataSetChanged() {
        prepareCirclePoints()
        invalidate()
    }

    override fun onDetachFromIndicator() {}
    var radius: Int
        get() = mRadius
        set(radius) {
            mRadius = radius
            prepareCirclePoints()
            invalidate()
        }
    var circleColor: Int
        get() = mCircleColor
        set(circleColor) {
            mCircleColor = circleColor
            invalidate()
        }
    var strokeWidth: Int
        get() = mStrokeWidth
        set(strokeWidth) {
            mStrokeWidth = strokeWidth
            invalidate()
        }
    var circleSpacing: Int
        get() = mCircleSpacing
        set(circleSpacing) {
            mCircleSpacing = circleSpacing
            prepareCirclePoints()
            invalidate()
        }
    var startInterpolator: Interpolator?
        get() = mStartInterpolator
        set(startInterpolator) {
            mStartInterpolator = startInterpolator
            if (mStartInterpolator == null) {
                mStartInterpolator = LinearInterpolator()
            }
        }
    var circleClickListener: OnCircleClickListener?
        get() = mCircleClickListener
        set(circleClickListener) {
            if (!isTouchable) {
                isTouchable = true
            }
            mCircleClickListener = circleClickListener
        }

    init {
        init(context)
    }
    interface OnCircleClickListener {
        fun onClick(index: Int)
    }
}