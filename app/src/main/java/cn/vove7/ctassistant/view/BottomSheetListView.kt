package cn.vove7.ctassistant.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ListView


class BottomSheetListView(context: Context, p_attrs: AttributeSet) : ListView(context, p_attrs) {
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mExpandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, mExpandSpec)
    }
}