package cn.vove7.ctassistant.events

import cn.vove7.ctassistant.cthelper.utils.Vog
import org.greenrobot.eventbus.EventBus

object EventUtils {
    fun postActionEvent(action: Int, code: Int = 0) {
        EventBus.getDefault().post(ActionEvent(action, code))
    }

    fun sendNetEvent(what: Int, status: Int, message: String = "") {
        Vog.d(this, "post message : $message")
        EventBus.getDefault().post(NetEvent(what, status, message))
    }
}