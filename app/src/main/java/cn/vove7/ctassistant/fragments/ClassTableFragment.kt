package cn.vove7.ctassistant.fragments


import android.annotation.SuppressLint
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Button
import android.widget.TextView
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.events.ActionEvent
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_ADD_EVENT
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_SUMMARY
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_ADD_ACCOUNT_FAILED
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_FAILED
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_SUCCESS
import cn.vove7.ctassistant.events.NetEvent
import cn.vove7.ctassistant.events.StatusCodes.STATUS_OK
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_CLASS_TABLE
import cn.vove7.ctassistant.openct.utils.VLog
import com.google.gson.Gson

@SuppressLint("ValidFragment")
class ClassTableFragment(viewPager: ViewPager) : BaseFragment(viewPager), View.OnClickListener {
    private lateinit var summaryText: TextView

    override fun layout(): Int = R.layout.fragment_class_table

    override fun initData() {}

    override fun onActionEvent(event: ActionEvent) {
        when (event.action) {
            ACTION_SUMMARY -> {
                summaryText.text = schoolAdapter.buildSummary()
                showSnack("正在获取课表...")
                schoolAdapter.requestClassTable()
            }
            ACTION_ADD_EVENT -> {
                showSnack(
                        when (event.code) {
                            CODE_SUCCESS -> "添加完成"
                            CODE_FAILED -> "添加失败"
                            CODE_ADD_ACCOUNT_FAILED -> "日历账户创建失败"
                            else -> "未知错误"
                        }
                )
            }
        }
    }

    override fun onNetEvent(event: NetEvent) {
        if (event.what == WHAT_GET_CLASS_TABLE) {
            if (event.statusCode == STATUS_OK) {
                VLog.d(this, "获取成功")
                summaryText.append(Gson().toJson(schoolAdapter.classInfoTable))
                showSnack("获取成功")
            } else {
                showSnack("获取失败")
            }
        }
    }

    override fun initView() {
        f<Button>(R.id.add2Calendar).setOnClickListener(this)

        summaryText = f(R.id.summary)

    }

    private fun newThread(): Thread = Thread(Runnable { schoolAdapter.add2Calendar() })

    private var thread = newThread()

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.add2Calendar -> {
                when (thread.state) {
                    Thread.State.TERMINATED -> {
                        thread = newThread()
                        thread.start()
                    }
                    Thread.State.NEW -> thread.start()
                    Thread.State.RUNNABLE -> showSnack("正在运行")

                    else -> showSnack("正在运行")
                }


            }
        }
    }
}
