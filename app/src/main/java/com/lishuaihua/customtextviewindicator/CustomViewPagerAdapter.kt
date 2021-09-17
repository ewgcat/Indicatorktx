package com.lishuaihua.customtextviewindicator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class CustomViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var fragments: List<Fragment>
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}