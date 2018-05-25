package cn.vove7.ctassistant.events

import cn.vove7.ctassistant.openct.utils.VLog
import org.greenrobot.eventbus.EventBus

object EventUtils {
    fun postActionEvent(action: Int, code: Int = 0) {
        EventBus.getDefault().post(ActionEvent(action, code))
    }

    fun sendNetEvent(what: Int, status: Int, message: String) {
        VLog.d(this, "post message : $message")
        EventBus.getDefault().post(NetEvent(what, status, message))
    }
}