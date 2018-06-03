package cn.vove7.ctassistant.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import cn.vove7.ctassistant.R


class VToast(val context: Context) {
    private lateinit var msgView: TextView
    private val animations = -1
    private val isShow = false
    private lateinit var toast: Toast
    @SuppressLint("ShowToast")
    fun init(): VToast {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)!!
        val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflate.inflate(R.layout.v_toast_layout, null)
        msgView = v.findViewById(R.id.message) as TextView
        toast.view = v
        try {
            val mTN: Any? = getField(toast, "mTN")
            if (mTN != null) {
                val mParams = getField(mTN, "mParams")
                if (mParams != null && mParams is WindowManager.LayoutParams) {
                    val params = mParams as WindowManager.LayoutParams
                    params.windowAnimations = R.style.Lite_Animation_Toast
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bottom()
    }

    /**
     * 反射字段
     * @param object 要反射的对象
     * @param fieldName 要反射的字段名称
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun getField(`object`: Any, fieldName: String): Any? {
        val field = `object`.javaClass.getDeclaredField(fieldName)
        if (field != null) {
            field.isAccessible = true
            return field.get(`object`)
        }
        return null
    }

    fun showShort(msg: String) {
        show(msg, Toast.LENGTH_SHORT)
    }

    fun top(): VToast {
        toast.setGravity(Gravity.TOP, 0, 40)
        return this
    }

    fun bottom(): VToast {
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        return this
    }

    fun center(): VToast {
        toast.setGravity(Gravity.CENTER, 0, 0)
        return this
    }


    fun showLong(msg: String) {
        show(msg, Toast.LENGTH_LONG)
    }

    private fun show(msg: String, d: Int = Toast.LENGTH_SHORT) {
        toast.duration = d
        msgView.text = msg

        toast.show()
    }

    companion object {
        fun with(context: Context): VToast {
            return VToast(context).init()
        }
    }

}