package cn.vove7.ctassistant.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.adapter.TimeTableNodeAdapter
import cn.vove7.ctassistant.events.ActionEvent
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_GET_TT
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_SUMMARY
import cn.vove7.ctassistant.events.EventUtils.postActionEvent
import cn.vove7.ctassistant.events.NetEvent
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_TIME_TABLE
import cn.vove7.ctassistant.cthelper.model.Time
import cn.vove7.ctassistant.cthelper.model.TimeTable
import cn.vove7.ctassistant.cthelper.model.TimeTableNode
import java.util.*


@SuppressLint("ValidFragment")
class VerifyTimeTableFragment(viewPager: ViewPager) : BaseFragment(viewPager), View.OnClickListener {
    private lateinit var ttList1: ListView
    private lateinit var ttList2: ListView
    private lateinit var tt2Layout: View
    private lateinit var beginDate1: TextView
    private lateinit var beginDate2: TextView

    private var timeTables = mutableListOf<TimeTable>()

    override fun layout(): Int = R.layout.fragment_verify_time_table

    override fun initData() {
    }

    private val addClickListener = View.OnClickListener { p0 ->
        val tag = p0?.tag as Int
        val nodeList = timeTables[tag].nodeList
        val lastNode = nodeList[nodeList.size - 1]
        nodeList.add(TimeTableNode(nodeList.size + 1,
                lastNode.timeOfEndClass!!.add(Time.TYPE_MINUTE, 10),
                lastNode.timeOfEndClass!!.add(Time.TYPE_MINUTE, 55)
        ))
        (if (tag == 0)
            adapter1
        else adapter2)
                ?.notifyDataSetChanged()
    }
    private val delClickListener = View.OnClickListener { p0 ->
        val tag = p0?.tag as Int
        val nodeList = timeTables[tag].nodeList
        nodeList.removeAt(nodeList.size - 1)
        (if (tag == 0)
            adapter1
        else adapter2)
                ?.notifyDataSetChanged()
    }

    override fun initView() {
        ttList1 = this.f(R.id.timetable1)
        ttList2 = f(R.id.timetable2)
        val footerView1 = LayoutInflater.from(context).inflate(R.layout.time_table_list_footer, ttList1, false)
        val footerView2 = LayoutInflater.from(context).inflate(R.layout.time_table_list_footer, ttList1, false)
        val addBtn1 = f<View>(footerView1, R.id.add_new_node)
        val delBtn1 = f<View>(footerView1, R.id.delete_new_node)
        val addBtn2 = f<View>(footerView2, R.id.add_new_node)
        val delBtn2 = f<View>(footerView2, R.id.delete_new_node)
        addBtn1.tag = 0
        addBtn2.tag = 1
        delBtn1.tag = 0
        delBtn2.tag = 1
        delBtn1.setOnClickListener(delClickListener)
        delBtn2.setOnClickListener(delClickListener)
        addBtn1.setOnClickListener(addClickListener)
        addBtn2.setOnClickListener(addClickListener)
        ttList1.addFooterView(footerView1)
        ttList2.addFooterView(footerView2)
        tt2Layout = f(R.id.tt2_layout)
        beginDate1 = f(R.id.begin_date_1)
        beginDate1.setOnClickListener(this)
        beginDate2 = f(R.id.begin_date_2)
        beginDate2.setOnClickListener(this)
        f<Button>(R.id.check_classTable).setOnClickListener(this)
    }

    private var adapter1: TimeTableNodeAdapter? = null
    private var adapter2: TimeTableNodeAdapter? = null

    override fun onNetEvent(event: NetEvent) {
        if (event.what == WHAT_GET_TIME_TABLE) {
            timeTables = schoolAdapter.timeTables
            if (timeTables.size == 1) {
                ttList2.visibility = GONE
            } else {
                adapter2 = TimeTableNodeAdapter(context!!, timeTables[1].nodeList)
                ttList2.adapter = adapter2
            }
            adapter1 = TimeTableNodeAdapter(context!!, timeTables[0].nodeList)
            ttList1.adapter = adapter1
        }
    }

    override fun onActionEvent(event: ActionEvent) {
        if (event.action == ACTION_GET_TT) {
            schoolAdapter.requestTimeTable()
        }
    }

    override fun onClick(p0: View?) {
        var i = 0
        when (p0?.id) {
            R.id.begin_date_1 -> {
                i = 1
            }
            R.id.begin_date_2 -> {
                i = 2
            }
            R.id.check_classTable -> {
                viewPager.currentItem = 3
                postActionEvent(ACTION_SUMMARY)
            }
        }
        if (i != 0) {
            val date = timeTables[i - 1].beginDate.split(".")
            val datePicker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, _, j, k ->
                val d = String.format("%d.%d", j + 1, k)
                timeTables[i - 1].beginDate = d
                (if (i == 1) beginDate1 else beginDate2).text = d
            }, Calendar.getInstance().get(Calendar.YEAR), date[0].toInt() - 1, date[1].toInt())
            datePicker.show()
        }
    }
}
