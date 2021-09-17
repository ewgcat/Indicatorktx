package com.lishuaihua.indicator.common

/**
 * 抽象的指示器标题，适用于CommonNavigator
 */
interface IPagerTitleView {
    /**
     * 被选中
     */
    fun onSelected(index: Int, totalCount: Int)

    /**
     * 未被选中
     */
    fun onDeselected(index: Int, totalCount: Int)

    /**
     * 离开
     *
     * @param leavePercent 离开的百分比, 0.0f - 1.0f
     * @param leftToRight  从左至右离开
     */
    fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)

    /**
     * 进入
     *
     * @param enterPercent 进入的百分比, 0.0f - 1.0f
     * @param leftToRight  从左至右离开
     */
    fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)


}