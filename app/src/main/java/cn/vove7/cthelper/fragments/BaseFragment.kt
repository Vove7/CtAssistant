package cn.vove7.cthelper.fragments

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.View

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import cn.vove7.cthelper.R
import cn.vove7.cthelper.events.ActionEvent
import cn.vove7.cthelper.events.NetEvent
import cn.vove7.cthelper.openct.utils.VLog

open class BaseFragment : Fragment() {
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: NetEvent) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: ActionEvent) {
    }

    internal fun showSnack(v: View?, msg: String, hasAction: Boolean) {
        if(v==null) {
            VLog.e(this,"showSnack --> view null")
            return
        }
        val bar = Snackbar.make(v, msg, if (hasAction) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        if (hasAction)
            bar.setAction("OK") { view -> bar.dismiss() }
        bar.show()
    }

    internal fun <T : View> `$`(v: View?, id: Int): T? {
        return v?.findViewById(id)
    }
}
