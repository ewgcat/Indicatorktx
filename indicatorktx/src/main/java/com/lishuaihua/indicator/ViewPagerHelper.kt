package com.lishuaihua.indicator


import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.lishuaihua.indicator.navigator.Indicator

/**
 * 简化和ViewPager ViewPager2 绑定
 */
object ViewPagerHelper {

    @JvmStatic
    fun bind(indicator: Indicator, viewPager: ViewPager) {
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                indicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                indicator.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                indicator.onPageScrollStateChanged(state)
            }
        })
    }

    @JvmStatic
    fun bind(indicator: Indicator, viewPager: ViewPager2) {
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                indicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicator.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                indicator.onPageScrollStateChanged(state)
            }
        })
    }
}