package com.lishuaihua.indicator

import android.animation.Animator
import android.annotation.TargetApi
import android.os.Build
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.animation.Interpolator
import com.lishuaihua.indicator.navigator.Indicator
import kotlin.jvm.JvmOverloads
import java.util.ArrayList

/**
 * 使得Indicator在FragmentContainer中使用
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class FragmentContainerHelper {
    private val mIndicators: MutableList<Indicator> = ArrayList()
    private var mScrollAnimator: ValueAnimator? = null
    private var mLastSelectedIndex = 0
    private var mDuration = 150
    private var mInterpolator: Interpolator = AccelerateDecelerateInterpolator()
    private val mAnimatorListener: Animator.AnimatorListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            dispatchPageScrollStateChanged(ScrollState.SCROLL_STATE_IDLE)
            mScrollAnimator = null
        }
    }
    private val mAnimatorUpdateListener = AnimatorUpdateListener { animation ->
        val positionOffsetSum = animation.animatedValue as Float
        var position = positionOffsetSum.toInt()
        var positionOffset = positionOffsetSum - position
        if (positionOffsetSum < 0) {
            position = position - 1
            positionOffset = 1.0f + positionOffset
        }
        dispatchPageScrolled(position, positionOffset, 0)
    }

    constructor() {}
    constructor(indicator: Indicator) {
        mIndicators.add(indicator)
    }

    @JvmOverloads
    fun handlePageSelected(selectedIndex: Int, smooth: Boolean = true) {
        if (mLastSelectedIndex == selectedIndex) {
            return
        }
        if (smooth) {
            if (mScrollAnimator == null || !mScrollAnimator!!.isRunning) {
                dispatchPageScrollStateChanged(ScrollState.SCROLL_STATE_SETTLING)
            }
            dispatchPageSelected(selectedIndex)
            var currentPositionOffsetSum = mLastSelectedIndex.toFloat()
            if (mScrollAnimator != null) {
                currentPositionOffsetSum = mScrollAnimator!!.animatedValue as Float
                mScrollAnimator!!.cancel()
                mScrollAnimator = null
            }
            mScrollAnimator = ValueAnimator()
            mScrollAnimator!!.setFloatValues(
                currentPositionOffsetSum,
                selectedIndex.toFloat()
            ) // position = selectedIndex, positionOffset = 0.0f
            mScrollAnimator!!.addUpdateListener(mAnimatorUpdateListener)
            mScrollAnimator!!.addListener(mAnimatorListener)
            mScrollAnimator!!.interpolator = mInterpolator
            mScrollAnimator!!.duration = mDuration.toLong()
            mScrollAnimator!!.start()
        } else {
            dispatchPageSelected(selectedIndex)
            if (mScrollAnimator != null && mScrollAnimator!!.isRunning) {
                dispatchPageScrolled(mLastSelectedIndex, 0.0f, 0)
            }
            dispatchPageScrollStateChanged(ScrollState.SCROLL_STATE_IDLE)
            dispatchPageScrolled(selectedIndex, 0.0f, 0)
        }
        mLastSelectedIndex = selectedIndex
    }

    fun setDuration(duration: Int) {
        mDuration = duration
    }

    fun setInterpolator(interpolator: Interpolator?) {
        mInterpolator = interpolator ?: AccelerateDecelerateInterpolator()
    }

    fun attachIndicator(indicator: Indicator) {
        mIndicators.add(indicator)
    }

    private fun dispatchPageSelected(pageIndex: Int) {
        for (indicator in mIndicators) {
            indicator.onPageSelected(pageIndex)
        }
    }

    private fun dispatchPageScrollStateChanged(state: Int) {
        for (indicator in mIndicators) {
            indicator.onPageScrollStateChanged(state)
        }
    }

    private fun dispatchPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        for (indicator in mIndicators) {
            indicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    companion object {
        /**
         * IPagerIndicator支持弹性效果的辅助方法
         *
         * @param positionDataList
         * @param index
         * @return
         */
        fun getImitativePositionData(
            positionDataList: List<PositionData>,
            index: Int
        ): PositionData {
            return if (index >= 0 && index <= positionDataList.size - 1) { // 越界后，返回假的PositionData
                positionDataList[index]
            } else {
                val result = PositionData()
                val referenceData: PositionData
                val offset: Int
                if (index < 0) {
                    offset = index
                    referenceData = positionDataList[0]
                } else {
                    offset = index - positionDataList.size + 1
                    referenceData = positionDataList[positionDataList.size - 1]
                }
                result.mLeft = referenceData.mLeft + offset * referenceData.width()
                result.mTop = referenceData.mTop
                result.mRight = referenceData.mRight + offset * referenceData.width()
                result.mBottom = referenceData.mBottom
                result.mContentLeft = referenceData.mContentLeft + offset * referenceData.width()
                result.mContentTop = referenceData.mContentTop
                result.mContentRight = referenceData.mContentRight + offset * referenceData.width()
                result.mContentBottom = referenceData.mContentBottom
                result
            }
        }
    }
}