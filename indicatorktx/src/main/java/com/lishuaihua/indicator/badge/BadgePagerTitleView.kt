package com.lishuaihua.indicator.badge

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.lishuaihua.indicator.IMeasurablePagerTitleView
import com.lishuaihua.indicator.common.IPagerTitleView

/**
 * 支持显示角标的title，角标布局可自定义
 */
class BadgePagerTitleView(context: Context?) : FrameLayout(context!!), IMeasurablePagerTitleView {
    var innerPagerTitleView: IPagerTitleView? = null
        private set
    private var mBadgeView: View? = null
    var isAutoCancelBadge = true
    private var mXBadgeRule: BadgeRule? = null
    private var mYBadgeRule: BadgeRule? = null
    override fun onSelected(index: Int, totalCount: Int) {
        if (innerPagerTitleView != null) {
            innerPagerTitleView!!.onSelected(index, totalCount)
        }
        if (isAutoCancelBadge) {
            badgeView = null
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (innerPagerTitleView != null) {
            innerPagerTitleView!!.onDeselected(index, totalCount)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (innerPagerTitleView != null) {
            innerPagerTitleView!!.onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (innerPagerTitleView != null) {
            innerPagerTitleView!!.onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    fun setInnerPagerTitleView(innerPagerTitleView: IPagerTitleView) {
        if (this.innerPagerTitleView === innerPagerTitleView) {
            return
        }
        this.innerPagerTitleView = innerPagerTitleView
        removeAllViews()
        if (this.innerPagerTitleView is View) {
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(this.innerPagerTitleView as View?, lp)
        }
        if (mBadgeView != null) {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            addView(mBadgeView, lp)
        }
    }

    var badgeView: View?
        get() = mBadgeView
        set(badgeView) {
            if (mBadgeView === badgeView) {
                return
            }
            mBadgeView = badgeView
            removeAllViews()
            if (innerPagerTitleView is View) {
                val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                addView(innerPagerTitleView as View?, lp)
            }
            if (mBadgeView != null) {
                val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                addView(mBadgeView, lp)
            }
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (innerPagerTitleView is View && mBadgeView != null) {
            val position = IntArray(14) // 14种角标定位方式
            val v = innerPagerTitleView as View
            position[0] = v.left
            position[1] = v.top
            position[2] = v.right
            position[3] = v.bottom
            if (innerPagerTitleView is IMeasurablePagerTitleView) {
                val view = innerPagerTitleView as IMeasurablePagerTitleView
                position[4] = view.getContentLeft()
                position[5] = view.getContentTop()
                position[6] = view.getContentRight()
                position[7] = view.getContentBottom()
            } else {
                for (i in 4..7) {
                    position[i] = position[i - 4]
                }
            }
            position[8] = v.width / 2
            position[9] = v.height / 2
            position[10] = position[4] / 2
            position[11] = position[5] / 2
            position[12] = position[6] + (position[2] - position[6]) / 2
            position[13] = position[7] + (position[3] - position[7]) / 2

            // 根据设置的BadgeRule调整角标的位置
            if (mXBadgeRule != null) {
                val x = position[mXBadgeRule!!.anchor.ordinal]
                val offset = mXBadgeRule!!.offset
                val newLeft = x + offset
                mBadgeView!!.offsetLeftAndRight(newLeft - mBadgeView!!.left)
            }
            if (mYBadgeRule != null) {
                val y = position[mYBadgeRule!!.anchor.ordinal]
                val offset = mYBadgeRule!!.offset
                val newTop = y + offset
                mBadgeView!!.offsetTopAndBottom(newTop - mBadgeView!!.top)
            }
        }
    }

    override fun getContentLeft(): Int {
        return if (innerPagerTitleView is IMeasurablePagerTitleView) {
            left + (innerPagerTitleView as IMeasurablePagerTitleView).getContentLeft()
        } else left
    }

    override fun getContentTop(): Int {
        return if (innerPagerTitleView is IMeasurablePagerTitleView) {
            (innerPagerTitleView as IMeasurablePagerTitleView).getContentTop()
        } else top
    }

    override fun getContentRight(): Int {
        return if (innerPagerTitleView is IMeasurablePagerTitleView) {
            left + (innerPagerTitleView as IMeasurablePagerTitleView).getContentRight()
        } else right
    }

    override fun getContentBottom(): Int {
        return if (innerPagerTitleView is IMeasurablePagerTitleView) {
            (innerPagerTitleView as IMeasurablePagerTitleView).getContentBottom()
        } else bottom
    }

    var xBadgeRule: BadgeRule?
        get() = mXBadgeRule
        set(badgeRule) {
            if (badgeRule != null) {
                val anchor = badgeRule.anchor
                require(!(anchor !== BadgeAnchor.LEFT && anchor !== BadgeAnchor.RIGHT && anchor !== BadgeAnchor.CONTENT_LEFT && anchor !== BadgeAnchor.CONTENT_RIGHT && anchor !== BadgeAnchor.CENTER_X && anchor !== BadgeAnchor.LEFT_EDGE_CENTER_X && anchor !== BadgeAnchor.RIGHT_EDGE_CENTER_X)) { "x badge rule is wrong." }
            }
            mXBadgeRule = badgeRule
        }
    var yBadgeRule: BadgeRule?
        get() = mYBadgeRule
        set(badgeRule) {
            if (badgeRule != null) {
                val anchor = badgeRule.anchor
                require(!(anchor !== BadgeAnchor.TOP && anchor !== BadgeAnchor.BOTTOM && anchor !== BadgeAnchor.CONTENT_TOP && anchor !== BadgeAnchor.CONTENT_BOTTOM && anchor !== BadgeAnchor.CENTER_Y && anchor !== BadgeAnchor.TOP_EDGE_CENTER_Y && anchor !== BadgeAnchor.BOTTOM_EDGE_CENTER_Y)) { "y badge rule is wrong." }
            }
            mYBadgeRule = badgeRule
        }
}