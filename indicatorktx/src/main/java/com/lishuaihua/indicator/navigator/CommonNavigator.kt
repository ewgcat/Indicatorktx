package com.lishuaihua.indicator.navigator

import android.content.Context
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.lishuaihua.indicator.PositionData
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lishuaihua.indicator.*
import com.lishuaihua.indicator.common.CommonNavigatorAdapter
import com.lishuaihua.indicator.common.indicators.IPagerIndicator
import com.lishuaihua.indicator.common.IPagerTitleView
import com.lishuaihua.indicator.scrollstate.ScrollState
import java.util.ArrayList

/**
 * 通用的ViewPager指示器，包含PagerTitle和PagerIndicator
 */
class CommonNavigator(context: Context) : FrameLayout(context),
    IPagerNavigator, OnNavigatorScrollListener {
    private var mScrollView: HorizontalScrollView? = null
    private var mTitleContainer: LinearLayout? = null
    private var mIndicatorContainer: LinearLayout? = null
    private var mIndicator: IPagerIndicator? = null
    private var mAdapter: CommonNavigatorAdapter? = null
    private var mNavigatorHelper: NavigatorHelper?=null
    /**
     * 提供给外部的参数配置
     */
    /** */
    private var mAdjustMode // 自适应模式，适用于数目固定的、少量的title
            = false
    private var mEnablePivotScroll // 启动中心点滚动
            = false
    private var mScrollPivotX = 0.5f // 滚动中心点 0.0f - 1.0f
    private var mSmoothScroll = true // 是否平滑滚动，适用于 !mAdjustMode && !mFollowTouch
    private var mFollowTouch = true // 是否手指跟随滚动
    private var mRightPadding = 0
    private var mLeftPadding = 0
    private var mIndicatorOnTop // 指示器是否在title上层，默认为下层
            = false
    private var mSkimOver // 跨多页切换时，中间页是否显示 "掠过" 效果
            = false
    private var mReselectWhenLayout = true // PositionData准备好时，是否重新选中当前页，为true可保证在极端情况下指示器状态正确

    /** */ // 保存每个title的位置信息，为扩展indicator提供保障
    private val mPositionDataList: MutableList<PositionData> = ArrayList()
    private val mObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            mNavigatorHelper!!.totalCount=mAdapter!!.count// 如果使用helper，应始终保证helper中的totalCount为最新
            init()
        }

        override fun onInvalidated() {
            // 没什么用，暂不做处理
        }
    }

    override fun notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter!!.notifyDataSetChanged()
        }
    }

    fun isAdjustMode(): Boolean {
        return mAdjustMode
    }

    fun setAdjustMode(`is`: Boolean) {
        mAdjustMode = `is`
    }

    fun getAdapter(): CommonNavigatorAdapter? {
        return mAdapter
    }

    fun setAdapter(adapter: CommonNavigatorAdapter) {
        if (mAdapter === adapter) {
            return
        }
        if (mAdapter != null) {
            mAdapter!!.unregisterDataSetObserver(mObserver)
        }
        mAdapter = adapter
        if (mAdapter != null) {
            mAdapter!!.registerDataSetObserver(mObserver)
            mNavigatorHelper!!.totalCount=mAdapter!!.count
            if (mTitleContainer != null) {  // adapter改变时，应该重新init，但是第一次设置adapter不用，onAttachToMagicIndicator中有init
                mAdapter!!.notifyDataSetChanged()
            }
        } else {
            mNavigatorHelper!!.totalCount=0
            init()
        }
    }

    private fun init() {
        removeAllViews()
        val root: View
        root = if (mAdjustMode) {
            LayoutInflater.from(context).inflate(R.layout.pager_navigator_layout_no_scroll, this)
        } else {
            LayoutInflater.from(context).inflate(R.layout.pager_navigator_layout, this)
        }
        mScrollView =
            root.findViewById<View>(R.id.scroll_view) as HorizontalScrollView // mAdjustMode为true时，mScrollView为null
        mTitleContainer = root.findViewById<View>(R.id.title_container) as LinearLayout
        mTitleContainer!!.setPadding(mLeftPadding, 0, mRightPadding, 0)
        mIndicatorContainer = root.findViewById<View>(R.id.indicator_container) as LinearLayout
        if (mIndicatorOnTop) {
            mIndicatorContainer!!.parent.bringChildToFront(mIndicatorContainer)
        }
        initTitlesAndIndicator()
    }

    /**
     * 初始化title和indicator
     */
    private fun initTitlesAndIndicator() {
        var i = 0
        val j: Int = mNavigatorHelper!!.totalCount
        while (i < j) {
            val v = mAdapter!!.getTitleView(context, i)
            if (v is View) {
                val view = v as View
                var lp: LinearLayout.LayoutParams
                if (mAdjustMode) {
                    lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.weight = mAdapter!!.getTitleWeight(context, i)
                } else {
                    lp = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT)
                }
                mTitleContainer!!.addView(view, lp)
            }
            i++
        }
        if (mAdapter != null) {
            mIndicator = mAdapter!!.getIndicator(context)
            if (mIndicator is View) {
                val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
                mIndicatorContainer!!.addView(mIndicator as View?, lp)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mAdapter != null) {
            preparePositionData()
            if (mIndicator != null) {
                mIndicator!!.onPositionDataProvide(mPositionDataList)
            }
            if (mReselectWhenLayout && mNavigatorHelper!!.scrollState === ScrollState.SCROLL_STATE_IDLE) {
                onPageSelected(mNavigatorHelper!!.currentIndex)
                onPageScrolled(mNavigatorHelper!!.scrollState, 0.0f, 0)
            }
        }
    }

    /**
     * 获取title的位置信息，为打造不同的指示器、各种效果提供可能
     */
    private fun preparePositionData() {
        mPositionDataList.clear()
        var i = 0
        val j: Int = mNavigatorHelper!!.totalCount
        while (i < j) {
            val data = PositionData()
            val v = mTitleContainer!!.getChildAt(i)
            if (v != null) {
                data.mLeft = v.left
                data.mTop = v.top
                data.mRight = v.right
                data.mBottom = v.bottom
                if (v is IMeasurablePagerTitleView) {
                    val view = v as IMeasurablePagerTitleView
                    data.mContentLeft = view.getContentLeft()
                    data.mContentTop = view.getContentTop()
                    data.mContentRight = view.getContentRight()
                    data.mContentBottom = view.getContentBottom()
                } else {
                    data.mContentLeft = data.mLeft
                    data.mContentTop = data.mTop
                    data.mContentRight = data.mRight
                    data.mContentBottom = data.mBottom
                }
            }
            mPositionDataList.add(data)
            i++
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mAdapter != null) {
            mNavigatorHelper!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            if (mIndicator != null) {
                mIndicator!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            // 手指跟随滚动
            if (mScrollView != null && mPositionDataList.size > 0 && position >= 0 && position < mPositionDataList.size) {
                if (mFollowTouch) {
                    val currentPosition = Math.min(mPositionDataList.size - 1, position)
                    val nextPosition = Math.min(mPositionDataList.size - 1, position + 1)
                    val current = mPositionDataList[currentPosition]
                    val next = mPositionDataList[nextPosition]
                    val scrollTo = current.horizontalCenter() - mScrollView!!.width * mScrollPivotX
                    val nextScrollTo = next.horizontalCenter() - mScrollView!!.width * mScrollPivotX
                    mScrollView!!.scrollTo((scrollTo + (nextScrollTo - scrollTo) * positionOffset).toInt(),
                        0)
                } else if (!mEnablePivotScroll) {
                    // TODO 实现待选中项完全显示出来
                }
            }
        }
    }

    fun getScrollPivotX(): Float {
        return mScrollPivotX
    }

    fun setScrollPivotX(scrollPivotX: Float) {
        mScrollPivotX = scrollPivotX
    }

    override fun onPageSelected(position: Int) {
        if (mAdapter != null) {
            mNavigatorHelper!!.onPageSelected(position)
            if (mIndicator != null) {
                mIndicator!!.onPageSelected(position)
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (mAdapter != null) {
            mNavigatorHelper!!.onPageScrollStateChanged(state)
            if (mIndicator != null) {
                mIndicator!!.onPageScrollStateChanged(state)
            }
        }
    }

    override fun onAttachToIndicator() {
        init()

    }

    override fun onDetachFromIndicator() {
    }


    fun isEnablePivotScroll(): Boolean {
        return mEnablePivotScroll
    }

    fun setEnablePivotScroll(`is`: Boolean) {
        mEnablePivotScroll = `is`
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (mTitleContainer == null) {
            return
        }
        val v = mTitleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (mTitleContainer == null) {
            return
        }
        val v = mTitleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    fun isSmoothScroll(): Boolean {
        return mSmoothScroll
    }

    fun setSmoothScroll(smoothScroll: Boolean) {
        mSmoothScroll = smoothScroll
    }

    fun isFollowTouch(): Boolean {
        return mFollowTouch
    }

    fun setFollowTouch(followTouch: Boolean) {
        mFollowTouch = followTouch
    }

    fun isSkimOver(): Boolean {
        return mSkimOver
    }

    fun setSkimOver(skimOver: Boolean) {
        mSkimOver = skimOver
        mNavigatorHelper!!.setSkimOver(skimOver)
    }

    override fun onSelected(index: Int, totalCount: Int) {
        if (mTitleContainer == null) {
            return
        }
        val v = mTitleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onSelected(index, totalCount)
        }
        if (!mAdjustMode && !mFollowTouch && mScrollView != null && mPositionDataList.size > 0) {
            val currentIndex = Math.min(mPositionDataList.size - 1, index)
            val current = mPositionDataList[currentIndex]
            if (mEnablePivotScroll) {
                val scrollTo = current.horizontalCenter() - mScrollView!!.width * mScrollPivotX
                if (mSmoothScroll) {
                    mScrollView!!.smoothScrollTo(scrollTo.toInt(), 0)
                } else {
                    mScrollView!!.scrollTo(scrollTo.toInt(), 0)
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (mScrollView!!.scrollX > current.mLeft) {
                    if (mSmoothScroll) {
                        mScrollView!!.smoothScrollTo(current.mLeft, 0)
                    } else {
                        mScrollView!!.scrollTo(current.mLeft, 0)
                    }
                } else if (mScrollView!!.scrollX + width < current.mRight) {
                    if (mSmoothScroll) {
                        mScrollView!!.smoothScrollTo(current.mRight - width, 0)
                    } else {
                        mScrollView!!.scrollTo(current.mRight - width, 0)
                    }
                }
            }
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (mTitleContainer == null) {
            return
        }
        val v = mTitleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onDeselected(index, totalCount)
        }
    }

    fun getPagerTitleView(index: Int): IPagerTitleView? {
        return if (mTitleContainer == null) {
            null
        } else mTitleContainer!!.getChildAt(index) as IPagerTitleView
    }

    fun getTitleContainer(): LinearLayout? {
        return mTitleContainer
    }

    fun getRightPadding(): Int {
        return mRightPadding
    }

    fun setRightPadding(rightPadding: Int) {
        mRightPadding = rightPadding
    }

    fun getLeftPadding(): Int {
        return mLeftPadding
    }

    fun setLeftPadding(leftPadding: Int) {
        mLeftPadding = leftPadding
    }

    fun isIndicatorOnTop(): Boolean {
        return mIndicatorOnTop
    }

    fun setIndicatorOnTop(indicatorOnTop: Boolean) {
        mIndicatorOnTop = indicatorOnTop
    }

    fun isReselectWhenLayout(): Boolean {
        return mReselectWhenLayout
    }

    fun setReselectWhenLayout(reselectWhenLayout: Boolean) {
        mReselectWhenLayout = reselectWhenLayout
    }

    init {
        mNavigatorHelper = NavigatorHelper()
        mNavigatorHelper!!.setNavigatorScrollListener(this)
    }


    fun getPagerIndicator(): IPagerIndicator? {
        return mIndicator
    }
}
