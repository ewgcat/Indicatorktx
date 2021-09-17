package com.lishuaihua.indicator

import com.lishuaihua.indicator.common.IPagerTitleView

interface IMeasurablePagerTitleView : IPagerTitleView {
    override fun getContentLeft(): Int
    override fun getContentTop(): Int
    override fun getContentRight(): Int
    override fun getContentBottom(): Int
}