package cn.vove7.ctassistant

import android.Manifest
import android.animation.ArgbEvaluator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import cn.vove7.ctassistant.adapter.FragmentAdapter
import cn.vove7.ctassistant.fragments.AyInfoFragment
import cn.vove7.ctassistant.fragments.ClassTableFragment
import cn.vove7.ctassistant.fragments.MainFragment
import cn.vove7.ctassistant.fragments.VerifyTimeTableFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null

    private var colors = intArrayOf(R.color.page_1, R.color.page_2, R.color.page_3, R.color.page_4)
    private var fragments: Array<Fragment?>? = null
    private var mainFragment: MainFragment? = null
    private var ayInfoFragment: AyInfoFragment? = null
    private var verifyTimeTableFragment: VerifyTimeTableFragment? = null
    private var classTableFragment: ClassTableFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initFragment()
        requestPermission()
    }

    private fun initFragment() {
        mainFragment = MainFragment(this, viewPager!!)

        ayInfoFragment = AyInfoFragment(viewPager!!)
        verifyTimeTableFragment = VerifyTimeTableFragment(viewPager!!)
        classTableFragment = ClassTableFragment(viewPager!!)
        fragments = arrayOf(mainFragment, ayInfoFragment, verifyTimeTableFragment, classTableFragment)

        val adapter = FragmentAdapter(
                supportFragmentManager, Arrays.asList(*fragments ?: arrayOf<Fragment>()))
        viewPager?.adapter = adapter
    }


    private fun initView() {
        viewPager = findViewById(R.id.view_pager)
        viewPager?.setBackgroundColor(c(colors[0]))
        //viewPager.setOnTouchListener((view, motionEvent) -> true);

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                val evaluator = ArgbEvaluator() // ARGB求值器
                var evaluate = c(colors[position]) // 初始默认颜色

                if (position < colors.size - 1) {
                    evaluate = evaluator// 根据positionOffset和第pos页~第pos+1页的颜色转换范围取颜色值
                            .evaluate(positionOffset, c(colors[position]), c(colors[position + 1])) as Int
                }
                viewPager?.setBackgroundColor(evaluate) // 为ViewPager的父容器设置背景色
            }

            override fun onPageSelected(position: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onBackPressed() {
        if (viewPager?.currentItem == 0 && mainFragment?.isBottomSheetShowing == true) {
            mainFragment?.hideBottom()
            return
        }
        super.onBackPressed()
    }

    private fun c(cId: Int): Int = resources.getColor(cId)

    private fun requestPermission() {
        val needRequest = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (p in permissions) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    needRequest.add(p)
                }
                if (!needRequest.isEmpty()) {
                    requestPermissions(needRequest.toTypedArray(), 0)
                }
            }
        }
    }

    companion object {

        private val permissions = arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
    }
}
