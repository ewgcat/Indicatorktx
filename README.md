# Indicatorktx ViewPager ViewPager2 指示器

  ```groovy
  repositories {
    
      maven {
          url "https://jitpack.io"
      }
  }
  
  dependencies {
      ...
      implementation 'com.github.ewgcat:Indicatorktx:1.0.0' // for androidx
  }
  '''
  
xml

 <com.lishuaihua.indicator.navigator.Indicator
        android:id="@+id/indicator"
        android:layout_width="wrap_content"
        android:layout_height="50dp"/>

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/view_pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
'''
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
        mCommonNavigator.setSkimOver(true) 
        mCommonNavigator.setAdapter(object : CommonNavigatorAdapter() {

            override val count: Int = mDataList.size

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val clipPagerTitleView = ClipPagerTitleView(context)
                clipPagerTitleView.text = mDataList[index]
                clipPagerTitleView.textColor = Color.BLACK
                clipPagerTitleView.clipColor = Color.RED
                clipPagerTitleView.setOnClickListener { viewPager.currentItem = index }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator?{
                return null
            }
        })
        indicator.setNavigator(mCommonNavigator)
        ViewPagerHelper.bind(indicator, viewPager)
'''
