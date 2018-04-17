package cn.vove7.cthelper.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.vove7.cthelper.R
import cn.vove7.cthelper.events.ActionEvent
import cn.vove7.cthelper.events.ActionEvent.Companion.ACTION_GET_TT
import cn.vove7.cthelper.events.ActionEvent.Companion.ACTION_INIT_AY
import cn.vove7.cthelper.events.NetEvent
import cn.vove7.cthelper.events.StatusCodes.STATUS_OK
import cn.vove7.cthelper.events.WhatRequest.WHAT_GET_BASE_WEEK
import cn.vove7.cthelper.events.WhatRequest.WHAT_INIT_AY
import cn.vove7.cthelper.openct.model.AcademicYear
import cn.vove7.cthelper.openct.utils.VLog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 选择学期，第一周一
 */
@SuppressLint("ValidFragment")
class AyInfoFragment(viewPager: ViewPager) : BaseFragment(viewPager), View.OnClickListener {
    internal var view: View? = null
    internal var parent: ViewGroup? = null
    private var aySpinner: Spinner? = null
    internal var academicYears: Array<AcademicYear>? = null
    private var baseDate: TextView? = null
    private var arrayAdapter:ArrayAdapter<String>?=null

    private var ayList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        parent = container
        view = inflater.inflate(R.layout.fragment_ayinfo, container, false)
        initView()
        return view
    }

    private fun initView() {
        aySpinner = f(view, R.id.select_academic_year)
        aySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {

                schoolAdapter?.selectAcademicYear = academicYears!![i]
                schoolAdapter?.requestBaseWeek()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        baseDate = f(view, R.id.select_baseDate)
        baseDate?.setOnClickListener(this)
        val next = f<Button>(view, R.id.btn_next)
        next?.setOnClickListener(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(event: NetEvent) {
        VLog.d(this, "get NetEvent message ----> " + event.message)
        when (event.what) {
            WHAT_INIT_AY -> {
                if (event.statusCode == STATUS_OK) {
                    academicYears = schoolAdapter?.allAcademicYears
                    ayList.clear()
                    for (ay in academicYears!!) {
                        ayList.add(ay.name)
                    }
                    arrayAdapter = ArrayAdapter(context!!,
                            android.R.layout.simple_list_item_single_choice, ayList)
                    aySpinner?.adapter = arrayAdapter
                    arrayAdapter?.notifyDataSetChanged()

                } else {
                    VLog.d(this, "  ----> 获取学期信息失败")
                    showSnack(parent, "获取学期信息失败", false)
                }
            }
            WHAT_GET_BASE_WEEK -> {
                if (event.statusCode == STATUS_OK) {
                    baseDate?.text = schoolAdapter?.baseWeekStr
                    val pos = arrayAdapter!!.getPosition(schoolAdapter?.selectAcademicYear?.name)
                    aySpinner?.setSelection(if (pos >= 0) pos else 0)
                } else {
                    showSnack(parent, "获取学期第一天失败，请手动选取", true)
                }
            }
        }
    }

    override fun onMessageEvent(event: ActionEvent) {
        VLog.d(this, " ActionEvent action ----> " + event.action)
        if (ACTION_INIT_AY == event.action) {
            schoolAdapter?.initAcademicYearInfo()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.select_baseDate -> {
                val calendar = schoolAdapter?.dateOfBaseWeek ?: Calendar.getInstance()

                DatePickerDialog(context!!, { _, i, i1, i2 ->
                    val selectDate = Calendar.getInstance()
                    selectDate.set(i, i1, i2, 0, 0, 0)
                    selectDate.set(Calendar.MILLISECOND, 0)
                    schoolAdapter?.dateOfBaseWeek = selectDate
                    baseDate?.text = schoolAdapter?.baseWeekStr
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.btn_next -> {
                postActionEvent(ACTION_GET_TT)
                viewPager.currentItem = 2
            }
        }
    }
}
