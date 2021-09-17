package com.lishuaihua.indicator.navigator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.lishuaihua.indicator.IPagerNavigator

/**
 * 整个框架的入口，核心
 */
class Indicator : FrameLayout {
    var navigator: IPagerNavigator? = null
        private set

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (navigator != null) {
            navigator!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    fun onPageSelected(position: Int) {
        if (navigator != null) {
            navigator!!.onPageSelected(position)
        }
    }

    fun onPageScrollStateChanged(state: Int) {
        if (navigator != null) {
            navigator!!.onPageScrollStateChanged(state)
        }
    }

    fun setNavigator(navigator: IPagerNavigator) {
        if (this.navigator === navigator) {
            return
        }
        if (this.navigator != null) {
            navigator.onDetachFromIndicator()
        }
        this.navigator = navigator
        removeAllViews()
        if (this.navigator is View) {
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(this.navigator as View?, lp)
            navigator.onAttachToIndicator()
        }
    }
}