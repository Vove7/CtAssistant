package cn.vove7.cthelper.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import cn.vove7.cthelper.VApplication
import cn.vove7.cthelper.events.ActionEvent
import cn.vove7.cthelper.events.NetEvent
import cn.vove7.cthelper.openct.adapter.SchoolAdapter
import cn.vove7.cthelper.openct.utils.VLog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@SuppressLint("ValidFragment")
open class BaseFragment(var viewPager: ViewPager) : Fragment() {
    var schoolAdapter: SchoolAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
        schoolAdapter = VApplication.instance?.schoolAdapter
    }

    override fun onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
        super.onStart()
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

    internal fun showSnack(v: View?, msg: String, hasAction: Boolean = false) {
        if (v == null) {
            VLog.e(this, "showSnack --> view null")
            return
        }
        val bar = Snackbar.make(v, msg, if (hasAction) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        if (hasAction)
            bar.setAction("OK") { _ -> bar.dismiss() }
        bar.show()
    }

    fun <T : View> f(view: View?, id: Int): T? {
        return view?.findViewById(id)
    }

    fun postActionEvent(action: String) {
        EventBus.getDefault().post(ActionEvent(action))
    }
}
