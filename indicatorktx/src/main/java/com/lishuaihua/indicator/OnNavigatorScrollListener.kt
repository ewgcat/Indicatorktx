package com.lishuaihua.indicator

interface OnNavigatorScrollListener {
    fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)
    fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)
    fun onSelected(index: Int, totalCount: Int)
    fun onDeselected(index: Int, totalCount: Int)
}