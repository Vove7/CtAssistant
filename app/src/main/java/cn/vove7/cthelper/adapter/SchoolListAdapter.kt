package cn.vove7.cthelper.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

import cn.vove7.cthelper.R
import cn.vove7.cthelper.interfaces.OnSchoolItemClickListener

class SchoolListAdapter(private val schools: ArrayList<String>, private val listener: OnSchoolItemClickListener) : RecyclerView.Adapter<SchoolListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.school_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.schoolNameView.text = schools[position]
        holder.itemView.setOnClickListener { _ -> listener.onItemClick(position, schools[position]) }
    }

    override fun getItemCount(): Int {
        return schools.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val schoolNameView: TextView = itemView.findViewById(R.id.school_name)

    }
}
