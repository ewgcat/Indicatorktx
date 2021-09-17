package com.lishuaihua.customtextviewindicator

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.lishuaihua.indicator.navigator.Indicator
import com.lishuaihua.indicator.ViewPagerHelper
import com.lishuaihua.indicator.navigator.CommonNavigator
import com.lishuaihua.indicator.common.CommonNavigatorAdapter
import com.lishuaihua.indicator.common.indicators.IPagerIndicator
import com.lishuaihua.indicator.common.IPagerTitleView
import com.lishuaihua.indicator.common.titleview.ClipPagerTitleView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val indicator = findViewById<Indicator>(R.id.indicator)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        var fragments=ArrayList<Fragment>()
        fragments.add(CustomFragment("标题1"))
        fragments.add(CustomFragment("标题2"))
        fragments.add(CustomFragment("标题3"))
        fragments.add(CustomFragment("标题4"))
        fragments.add(CustomFragment("标题5"))
        fragments.add(CustomFragment("标题6"))
        fragments.add(CustomFragment("标题7"))
        fragments.add(CustomFragment("标题8"))
        fragments.add(CustomFragment("标题9"))
        val customViewPagerAdapter = CustomViewPagerAdapter(supportFragmentManager,lifecycle,fragments)
        viewPager.adapter= customViewPagerAdapter

        var mDataList=ArrayList<String>()
        mDataList.add("标题1")
        mDataList.add("标题2")
        mDataList.add("标题3")
        mDataList.add("标题4")
        mDataList.add("标题5")
        mDataList.add("标题6")
        mDataList.add("标题7")
        mDataList.add("标题8")
        mDataList.add("标题9")
      var  mCommonNavigator = CommonNavigator(this)
        mCommonNavigator.isSkimOver = true
        mCommonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mDataList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val clipPagerTitleView = ClipPagerTitleView(context)
                clipPagerTitleView.text = mDataList[index]
                clipPagerTitleView.textColor = Color.BLACK
                clipPagerTitleView.clipColor = Color.RED
                clipPagerTitleView.setOnClickListener { viewPager.currentItem = index }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                return null
            }
        }
        indicator.navigator = mCommonNavigator
        ViewPagerHelper.bind(indicator, viewPager)

    }



}