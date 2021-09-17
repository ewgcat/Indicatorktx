package com.lishuaihua.indicator

/**
 * 保存指示器标题的坐标
 */
class PositionData {
    var mLeft = 0
    var mTop = 0
    var mRight = 0
    var mBottom = 0
    var mContentLeft = 0
    var mContentTop = 0
    var mContentRight = 0
    var mContentBottom = 0
    fun width(): Int {
        return mRight - mLeft
    }

    fun height(): Int {
        return mBottom - mTop
    }

    fun contentWidth(): Int {
        return mContentRight - mContentLeft
    }

    fun contentHeight(): Int {
        return mContentBottom - mContentTop
    }

    fun horizontalCenter(): Int {
        return mLeft + width() / 2
    }

    fun verticalCenter(): Int {
        return mTop + height() / 2
    }
}