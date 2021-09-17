package com.lishuaihua.indicator.common.titleview

import android.content.Context
import com.lishuaihua.indicator.common.ArgbEvaluatorHolder

/**
 * 两种颜色过渡的指示器标题
 */
class ColorTransitionPagerTitleView(context: Context) : SimplePagerTitleView(context) {
    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(leavePercent, selectedColor, normalColor)
        setTextColor(color)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(enterPercent, normalColor, selectedColor)
        setTextColor(color)
    }

    override fun onSelected(index: Int, totalCount: Int) {}
    override fun onDeselected(index: Int, totalCount: Int) {}
}