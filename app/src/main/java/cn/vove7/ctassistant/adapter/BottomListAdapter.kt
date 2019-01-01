package cn.vove7.ctassistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.interfaces.OnBottomItemClickListener
import java.util.*

class BottomListAdapter(private val items: ArrayList<String>, private val listener: OnBottomItemClickListener)
    : BaseAdapter() {

    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View {
        var v = view
        if (v == null) {
            v = LayoutInflater.from(parent?.context).inflate(R.layout.school_list_item, parent, false)
        }
        val textView: TextView = v!!.findViewById(R.id.school_name)
        textView.text = getItem(pos)
        v.setOnClickListener { listener.onItemClick(pos, getItem(pos)) }
        return v
    }

    override fun getItem(p0: Int): String = items[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = items.size

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.school_list_item, parent, false)
//
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.schoolNameView.text = items[position]
//        holder.itemView.setOnClickListener { _ -> listener.onItemClick(position, items[position]) }
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }

}
