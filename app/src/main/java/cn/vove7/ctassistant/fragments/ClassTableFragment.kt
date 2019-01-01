package cn.vove7.ctassistant.fragments


import android.annotation.SuppressLint
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.cthelper.model.ClassInfo
import cn.vove7.ctassistant.cthelper.utils.Vog
import cn.vove7.ctassistant.events.ActionEvent
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_ADD_EVENT
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_SUMMARY
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_ADD_ACCOUNT_FAILED
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_FAILED
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_SUCCESS
import cn.vove7.ctassistant.events.NetEvent
import cn.vove7.ctassistant.events.StatusCodes.STATUS_OK
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_CLASS_TABLE
import com.bin.david.form.core.SmartTable
import kotlinx.android.synthetic.main.fragment_class_table.*
import java.util.*

@SuppressLint("ValidFragment")
class ClassTableFragment(viewPager: ViewPager) : BaseFragment(viewPager), View.OnClickListener {

    override fun layout(): Int = R.layout.fragment_class_table
    private lateinit var classTable: SmartTable<ClassInfo>

    override fun initData() {}

    override fun onActionEvent(event: ActionEvent) {
        when (event.action) {
            ACTION_SUMMARY -> {
                summary.text = schoolAdapter.buildSummary()

                showSnack(getString(R.string.text_geting_the_classtable))
                schoolAdapter.requestClassTable()
            }
            ACTION_ADD_EVENT -> {
                showSnack(
                        when (event.code) {
                            CODE_SUCCESS -> getString(R.string.text_add_success)
                            CODE_FAILED -> getString(R.string.text_add_failed)
                            CODE_ADD_ACCOUNT_FAILED -> getString(R.string.text_calendar_account_create_failed)
                            else -> getString(R.string.text_unknow_error)
                        }
                )
            }
        }
    }

    private fun <A> array2List(arr: Array<A>): List<A> {
        val l = ArrayList<A>()

        arr.forEach {
            l.add(it)
        }
        return l
    }

    override fun onNetEvent(event: NetEvent) {
        if (event.what == WHAT_GET_CLASS_TABLE) {
            if (event.statusCode == STATUS_OK) {
                Vog.d(this, getString(R.string.text_get_success))
                classTable.setData(array2List(schoolAdapter.classInfoTable!!))

                showSnack(getString(R.string.text_get_success))
            } else {
                showSnack(getString(R.string.text_get_failed))
                Log.d("Debug :", "onNetEvent  ----> ${event.message}")
            }
        }
    }

    override fun initView() {
        f<Button>(R.id.add2Calendar).setOnClickListener(this)
        buildPreview(false)
        classTable = f(R.id.classTable)
        f<CheckBox>(R.id.is_show_week).setOnCheckedChangeListener { _, isChecked ->
            buildPreview(isChecked)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun buildPreview(isShow: Boolean) {
        f<TextView>(R.id.preview).text = (if (isShow) "[1]" else "") + getString(R.string.sample_ct)
    }

    private fun newThread(): Thread = Thread(Runnable {
        showSnack(getString(R.string.begin_import))
        schoolAdapter.add2Calendar(is_remind.isChecked, is_show_week.isChecked)
    })

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
                    Thread.State.RUNNABLE -> showSnack(getString(R.string.text_running))
                    else -> showSnack(getString(R.string.text_running))
                }
            }
        }
    }
}
