package com.lishuaihua.indicator.common.titleview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import com.lishuaihua.indicator.UIUtil

/**
 * 类似今日头条切换效果的指示器标题
 */
class ClipPagerTitleView(context: Context) : View(context), IPagerTitleView {
    private var mText: String? = null
    private var mTextColor = 0
    private var mClipColor = 0
    private var mLeftToRight = false
    private var mClipPercent = 0f
    private var mPaint: Paint? = null
    private val mTextBounds = Rect()
    private fun init(context: Context) {
        val textSize = UIUtil.dip2px(context, 16.0)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.textSize = textSize.toFloat()
        val padding = UIUtil.dip2px(context, 10.0)
        setPadding(padding, 0, padding, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureTextBounds()
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec)
        var result = size
        when (mode) {
            MeasureSpec.AT_MOST -> {
                val width = mTextBounds.width() + paddingLeft + paddingRight
                result = Math.min(width, size)
            }
            MeasureSpec.UNSPECIFIED -> result = mTextBounds.width() + paddingLeft + paddingRight
            else -> {
            }
        }
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        var result = size
        when (mode) {
            MeasureSpec.AT_MOST -> {
                val height = mTextBounds.height() + paddingTop + paddingBottom
                result = Math.min(height, size)
            }
            MeasureSpec.UNSPECIFIED -> result = mTextBounds.height() + paddingTop + paddingBottom
            else -> {
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        val x = (width - mTextBounds.width()) / 2
        val fontMetrics = mPaint!!.fontMetrics
        val y = ((height - fontMetrics.bottom - fontMetrics.top) / 2).toInt()

        // 画底层
        mPaint!!.color = mTextColor
        canvas.drawText(mText!!, x.toFloat(), y.toFloat(), mPaint!!)

        // 画clip层
        canvas.save()
        if (mLeftToRight) {
            canvas.clipRect(0f, 0f, width * mClipPercent, height.toFloat())
        } else {
            canvas.clipRect(width * (1 - mClipPercent), 0f, width.toFloat(), height.toFloat())
        }
        mPaint!!.color = mClipColor
        canvas.drawText(mText!!, x.toFloat(), y.toFloat(), mPaint!!)
        canvas.restore()
    }

    override fun onSelected(index: Int, totalCount: Int) {}
    override fun onDeselected(index: Int, totalCount: Int) {}
    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        mLeftToRight = !leftToRight
        mClipPercent = 1.0f - leavePercent
        invalidate()
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        mLeftToRight = leftToRight
        mClipPercent = enterPercent
        invalidate()
    }

    private fun measureTextBounds() {
        mPaint!!.getTextBounds(mText, 0, if (mText == null) 0 else mText!!.length, mTextBounds)
    }

    var text: String?
        get() = mText
        set(text) {
            mText = text
            requestLayout()
        }
    var textSize: Float
        get() = mPaint!!.textSize
        set(textSize) {
            mPaint!!.textSize = textSize
            requestLayout()
        }
    var textColor: Int
        get() = mTextColor
        set(textColor) {
            mTextColor = textColor
            invalidate()
        }
    var clipColor: Int
        get() = mClipColor
        set(clipColor) {
            mClipColor = clipColor
            invalidate()
        }

    override fun getContentLeft(): Int {
        val contentWidth = mTextBounds.width()
        return left + width / 2 - contentWidth / 2
    }

    override fun getContentTop(): Int {
        val metrics = mPaint!!.fontMetrics
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 - contentHeight / 2).toInt()
    }

    override fun getContentRight(): Int {
        val contentWidth = mTextBounds.width()
        return left + width / 2 + contentWidth / 2
    }

    override fun getContentBottom(): Int {
        val metrics = mPaint!!.fontMetrics
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 + contentHeight / 2).toInt()
    }

    init {
        init(context)
    }
}