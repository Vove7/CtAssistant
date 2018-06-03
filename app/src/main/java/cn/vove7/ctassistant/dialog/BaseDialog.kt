package cn.vove7.ctassistant.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.vove7.ctassistant.R

open class BaseDialog(context: Context) : Dialog(context), DialogInterface {

    private lateinit var titleView: TextView
    private lateinit var iconView: ImageView
    private lateinit var buttonPositive: TextView
    private lateinit var buttonNegative: TextView
    private lateinit var buttonNeutral: TextView

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
    }
}

class ButtonModel(var whichButton: Int, var text: String, var lis: View.OnClickListener)