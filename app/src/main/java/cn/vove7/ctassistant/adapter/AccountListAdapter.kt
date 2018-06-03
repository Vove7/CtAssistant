package cn.vove7.ctassistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.openct.model.CalendarAccount

class AccountListAdapter(val context: Context, var accList: List<CalendarAccount>,
                         private val delListener: OnItemClickListener) : BaseAdapter() {
    var inflate: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflate.inflate(R.layout.account_list_item, null)
        val account_name = view.findViewById<TextView>(R.id.account_name)
        account_name.text = getItem(position).accName
        view.findViewById<View>(R.id.del_acc).setOnClickListener({
            delListener.onClick(position, getItem(position))
        })

        return view
    }

    override fun getItem(position: Int): CalendarAccount = accList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = accList.size
    public interface OnItemClickListener {
        fun onClick(pos: Int, account: CalendarAccount)
    }
}


