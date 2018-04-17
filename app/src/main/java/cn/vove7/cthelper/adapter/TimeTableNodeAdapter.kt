package cn.vove7.cthelper.adapter

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cn.vove7.cthelper.R
import cn.vove7.cthelper.openct.model.TimeTableNode

class TimeTableNodeAdapter(val context: Context, private val timeTableNodes: MutableList<TimeTableNode>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return timeTableNodes.size
    }

    override fun getItem(i: Int): Any {
        return timeTableNodes[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val v = view ?: inflater.inflate(R.layout.tt_node_item, viewGroup, false)

        val nodeText = v?.findViewById<TextView>(R.id.node_num_text)
        val timeSectionText = v?.findViewById<TextView>(R.id.time_section)
        val timeNode = timeTableNodes[i]
        nodeText?.text = timeNode.nodeName
        timeSectionText?.text = "${timeNode.timeOfBeginClass} - " +
                "${timeNode.timeOfEndClass}"

        v!!.setOnClickListener { _ ->
            buildTimePicker(timeNode, true)
        }
        return v
    }

    private fun buildTimePicker(timeNode: TimeTableNode, begin: Boolean) {
        val time = if (begin) timeNode.timeOfBeginClass else timeNode.timeOfEndClass

        val timePicker = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            time?.hour = hour
            time?.minute = minute
            if (begin) {
                buildTimePicker(timeNode, false)
            }
            notifyDataSetChanged()
        }, time!!.hour, time.minute, true)
        timePicker.setTitle(if (begin) "设置开始时间" else "设置结束时间")
        timePicker.show()
    }
}
