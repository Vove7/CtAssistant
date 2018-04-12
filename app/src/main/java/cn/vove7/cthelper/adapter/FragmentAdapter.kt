package cn.vove7.cthelper.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class FragmentAdapter(fm: FragmentManager, private val fragmentList: List<Fragment?>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return fragmentList[position]
    }


    override fun getCount(): Int {
        return fragmentList.size
    }
}
