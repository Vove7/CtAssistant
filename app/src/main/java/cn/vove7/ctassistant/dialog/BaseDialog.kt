package cn.vove7.ctassistant.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.vove7.ctassistant.R

/**
 * Dialog with
 * <include layout="@layout/dialog_header" />
 * <include layout="@layout/dialog_footer" />
 */
open class BaseDialog(context: Context) : Dialog(context), DialogInterface {

    private lateinit var titleView: TextView
    private lateinit var iconView: ImageView
    private lateinit var buttonPositive: TextView
    private lateinit var buttonNegative: TextView
    private lateinit var buttonNeutral: TextView

    var widthP: Double = 0.7
    var heightP: Double = -1.0
    var gravity: Int = Gravity.CENTER

    var title: String = ""
    var iconDrawable: Drawable? = null

    val buttonList = mutableListOf<ButtonModel>()

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initView()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        initView()
    }


    override fun setContentView(view: View?) {
        super.setContentView(view)
        initView()
    }

    override fun setTitle(stringId: Int) {
        setTitle(context.getString(stringId))

    }

    override fun setTitle(title: CharSequence) {
        this.title = title.toString()
    }

    private fun initView() {
        titleView = findViewById(R.id.dialog_title)
        iconView = findViewById(R.id.dialog_icon)
        buttonPositive = findViewById(R.id.dialog_button_positive)
        buttonNegative = findViewById(R.id.dialog_button_negative)
        buttonNeutral = findViewById(R.id.dialog_button_neutral)
    }

    fun setIcon(@DrawableRes drawableId: Int): BaseDialog {
        setIcon(context.getDrawable(drawableId))
        return this
    }

    fun setIcon(drawable: Drawable): BaseDialog {
        iconDrawable = drawable
        return this
    }

    fun setButton(whichButton: Int, @StringRes resId: Int, lis: View.OnClickListener): BaseDialog {
        return setButton(whichButton, context.getString(resId), lis)
    }

    fun setButton(whichButton: Int, text: String, lis: View.OnClickListener): BaseDialog {
        buttonList.add(ButtonModel(whichButton, text, lis))
        return this
    }

    private fun onSetButton() {
        buttonList.forEach {

            when (it.whichButton) {
                BUTTON_POSITIVE -> {
                    buttonPositive.text = it.text
                    buttonPositive.visibility = View.VISIBLE
                    buttonPositive.setOnClickListener(it.lis)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    buttonNegative.text = it.text
                    buttonNegative.visibility = View.VISIBLE
                    buttonNegative.setOnClickListener(it.lis)
                }
                DialogInterface.BUTTON_NEUTRAL -> {
                    buttonNeutral.text = it.text
                    buttonNeutral.visibility = View.VISIBLE
                    buttonNeutral.setOnClickListener(it.lis)
                }
            }
        }

    }

    override fun show() {
        super.show()
        onSetButton()
        titleView.text = title
        if (iconDrawable != null) {
            iconView.visibility = View.VISIBLE
            iconView.setImageDrawable(iconDrawable)
        }
        onSetHeight()
        onSetWidth()
        window!!.setGravity(gravity)
    }

    private fun onSetWidth() {
        val m = window!!.windowManager
        val d = m.defaultDisplay
        val p = window!!.attributes
        val metrics = DisplayMetrics()
        d.getMetrics(metrics)
        p.width = (metrics.widthPixels * widthP).toInt() //设置dialog的宽度为当前手机屏幕的宽度
        window!!.attributes = p
    }

    private fun onSetHeight() {
        if (heightP < 0)
            return
        val m = window!!.windowManager
        val d = m.defaultDisplay
        val p = window!!.attributes
        val metrics = DisplayMetrics()
        d.getMetrics(metrics)
        val h = metrics.heightPixels
        p.height = (h * heightP).toInt()
        window!!.attributes = p
    }

    fun setWidth(f: Double) {
        widthP = f
    }

    fun setHeight(f: Double) {
        heightP = f
    }

    fun fullScreen() {
        setWidth(1.0)
        setHeight(0.94)
        bottom()
    }

    fun bottom() {
        gravity = Gravity.BOTTOM
    }

    fun top() {
        gravity = Gravity.TOP
    }


}

class ButtonModel(var whichButton: Int, var text: String, var lis: View.OnClickListener)