package com.lishuaihua.indicator

import com.lishuaihua.indicator.common.IPagerTitleView

interface IMeasurablePagerTitleView : IPagerTitleView {
     fun getContentLeft(): Int
     fun getContentTop(): Int
     fun getContentRight(): Int
     fun getContentBottom(): Int
}