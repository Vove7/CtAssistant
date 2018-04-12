package cn.vove7.cthelper.openct.utils

import android.util.Log
import cn.vove7.cthelper.events.NetEvent
import cn.vove7.cthelper.events.StatusCodes.STATUS_NET_ERR
import cn.vove7.cthelper.events.StatusCodes.STATUS_PARSE_ERR
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import java.io.IOException

object HttpHelper {
    private var client = OkHttpClient()

    fun buildCall(url: String, params: Map<String, String>?): Call {
        val formBodyBuilder = FormBody.Builder()
        if (params != null)
            for ((key, value) in params)
                formBodyBuilder.add(key, value)

        val request = Request.Builder()
                .url(url)
                .post(formBodyBuilder.build())
                .build()
        return client.newCall(request)
    }
}

open class MyCallback(val what: Int) : Callback {
    open fun onFailed(call: Call?, message: String) {
        VLog.e(this, message)
        EventBus.getDefault().post(NetEvent(what, STATUS_NET_ERR, message))
    }

    fun callFailed(e: String) {
        VLog.e(this, e)
        EventBus.getDefault().post(NetEvent(what, STATUS_PARSE_ERR, e))
    }

    override fun onFailure(call: Call?, e: IOException?) {
        onFailed(call, e?.message ?: "e is null")
    }

    override fun onResponse(call: Call?, response: Response?) {
        if (response == null) {
            onFailure(call, IOException("response null"))
            return
        }
        val body = response.body()
        if (body == null) {
            Log.d(this.javaClass.name, "onResponse: body is null")
            onFailure(call, IOException("body null"))
            return
        }
        val data = body.string()
        VLog.d(this, data)
        onSuccess(data)
    }

    open fun onSuccess(data: String) {
    }
}