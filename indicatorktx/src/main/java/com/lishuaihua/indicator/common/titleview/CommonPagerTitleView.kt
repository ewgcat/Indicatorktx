package com.lishuaihua.indicator.common.titleview

import android.content.Context
import android.widget.FrameLayout
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.lishuaihua.indicator.IMeasurablePagerTitleView
import com.lishuaihua.indicator.common.IPagerTitleView

/**
 * 通用的指示器标题，子元素内容由外部提供，事件回传给外部
 */
class CommonPagerTitleView(context: Context) : FrameLayout(context), IMeasurablePagerTitleView {
    var onPagerTitleChangeListener: OnPagerTitleChangeListener? = null
    var contentPositionDataProvider: ContentPositionDataProvider? = null
    override fun onSelected(index: Int, totalCount: Int) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onSelected(index, totalCount)
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onDeselected(index, totalCount)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    override fun getContentLeft(): Int {
        return if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentLeft
        } else left
    }

    override fun getContentTop(): Int {
        return if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentTop
        } else top
    }

    override fun getContentRight(): Int {
        return if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentRight
        } else right
    }

    override fun getContentBottom(): Int {
        return if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentBottom
        } else bottom
    }

    /**
     * 外部直接将布局设置进来
     *
     * @param contentView
     */
    fun setContentView(contentView: View?) {
        setContentView(contentView, null)
    }

    fun setContentView(contentView: View?, lp: LayoutParams?) {
        var lp = lp
        removeAllViews()
        if (contentView != null) {
            if (lp == null) {
                lp = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            addView(contentView, lp)
        }
    }

    fun setContentView(layoutId: Int) {
        val child = LayoutInflater.from(context).inflate(layoutId, null)
        setContentView(child, null)
    }
}