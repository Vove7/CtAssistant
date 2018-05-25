package cn.vove7.ctassistant.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.events.ActionEvent
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_GET_TT
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_INIT_AY
import cn.vove7.ctassistant.events.EventUtils.postActionEvent
import cn.vove7.ctassistant.events.NetEvent
import cn.vove7.ctassistant.events.StatusCodes.STATUS_OK
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_BASE_WEEK
import cn.vove7.ctassistant.events.WhatRequest.WHAT_INIT_AY
import cn.vove7.ctassistant.openct.model.AcademicYear
import cn.vove7.ctassistant.openct.utils.VLog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 选择学期，第一周一
 */
@SuppressLint("ValidFragment")
class AyInfoFragment(viewPager: ViewPager) : BaseFragment(viewPager), View.OnClickListener {
    internal var parent: ViewGroup? = null
    private lateinit var ayButton: Button
    var academicYears: Array<AcademicYear>? = null
    private lateinit var baseDate: TextView

    private var ayList = ArrayList<String>()


    override fun layout(): Int = R.layout.fragment_ayinfo

    override fun initData() {
        changeBottomLayout(SHOW_PROCESS)
    }

    override fun initView() {
        initBottomSheetView(resources.getString(R.string.text_choose_ay))
        ayButton = f(R.id.select_academic_year)
        baseDate = f(R.id.select_baseDate)
        ayButton.setOnClickListener(this)
        baseDate.setOnClickListener(this)
        f<Button>(R.id.btn_next).setOnClickListener(this)
        bottomToolbar.inflateMenu(R.menu.menu_toolbar_with_refresh)
        bottomToolbar.setOnMenuItemClickListener(this::onOptionsItemSelected)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh_icon->{
                changeBottomLayout(SHOW_PROCESS)
                schoolAdapter.initAcademicYearInfo()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(pos: Int, content: String) {
        ayButton.text = content
        schoolAdapter.selectAcademicYear = academicYears!![pos]
        schoolAdapter.requestBaseWeek()
        hideBottom()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onNetEvent(event: NetEvent) {
        VLog.d(this, "get NetEvent message ----> " + event.message)
        when (event.what) {
            WHAT_INIT_AY -> {
                if (event.statusCode == STATUS_OK) {
                    academicYears = schoolAdapter.allAcademicYears
                    ayList.clear()
                    for (ay in academicYears!!) {
                        ayList.add(ay.name)
                    }
                    setBottomListData(ayList)
                    val pos = schoolAdapter.getAyPosition()
                    if (pos >= 0)
                        ayButton.text = ayList[pos]
                    changeBottomLayout(SHOW_LIST)

                } else {
                    VLog.d(this, "  ----> 获取学期信息失败")
//                    showSnack("获取学期信息失败")
                    changeBottomLayout(SHOW_ERR)
                }
            }
            WHAT_GET_BASE_WEEK -> {
                if (event.statusCode == STATUS_OK) {
                    baseDate.text = schoolAdapter.baseWeekStr
                } else {
                    showSnack("获取学期第一天失败，请手动选取",
                            listener = View.OnClickListener { baseDate.callOnClick() })
                }
            }
        }
    }

    override fun onActionEvent(event: ActionEvent) {
        VLog.d(this, " ActionEvent action ----> " + event.action)
        if (ACTION_INIT_AY == event.action) {
            schoolAdapter.initAcademicYearInfo()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.select_baseDate -> {
                val calendar = schoolAdapter.dateOfBaseWeek ?: Calendar.getInstance()

                DatePickerDialog(context!!, { _, i, i1, i2 ->
                    val selectDate = Calendar.getInstance()
                    selectDate.set(i, i1, i2, 0, 0, 0)
                    selectDate.set(Calendar.MILLISECOND, 0)
                    schoolAdapter.dateOfBaseWeek = selectDate
                    baseDate.text = schoolAdapter.baseWeekStr
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.btn_next -> {
                postActionEvent(ACTION_GET_TT)
                viewPager.currentItem = 2
            }
            R.id.select_academic_year -> {
                showBottom()
            }
        }
    }
}
