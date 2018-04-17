package cn.vove7.cthelper.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cn.vove7.cthelper.R
import cn.vove7.cthelper.events.ActionEvent
import cn.vove7.cthelper.events.ActionEvent.Companion.ACTION_SUMMARY
import cn.vove7.cthelper.events.NetEvent
import cn.vove7.cthelper.events.StatusCodes.STATUS_OK
import cn.vove7.cthelper.events.WhatRequest.WHAT_GET_CLASS_TABLE
import cn.vove7.cthelper.openct.utils.VLog

@SuppressLint("ValidFragment")
class ClassTableFragment(viewPager: ViewPager) : BaseFragment(viewPager), View.OnClickListener {
    internal var view: View? = null
    var summaryText: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_class_table, container, false)
        initView()
        return view
    }

    override fun onMessageEvent(event: ActionEvent) {
        if (event.action == ACTION_SUMMARY) {
            summaryText?.text = schoolAdapter?.buildSummary()
            showSnack(view,"正在获取课表...")
            schoolAdapter?.requestClassTable()
        }
    }

    override fun onMessageEvent(event: NetEvent) {
        if (event.what == WHAT_GET_CLASS_TABLE) {
            if (event.statusCode == STATUS_OK) {
                VLog.d(this,"获取成功")
            } else {
                showSnack(view, "获取失败")
            }

        }
    }

    private fun initView() {
        f<Button>(view, R.id.add2Calendar)?.setOnClickListener(this)

        summaryText = f(view, R.id.summary)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.add2Calendar -> {
                if (schoolAdapter?.add2Calendar() == true) {
                    showSnack(view,"成功")
                } else {
                    showSnack(view,"失败")

                }
            }
        }
    }
}
