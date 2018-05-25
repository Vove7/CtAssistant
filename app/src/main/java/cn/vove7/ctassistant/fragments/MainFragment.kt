package cn.vove7.ctassistant.fragments


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.events.ActionEvent
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_INIT_AY
import cn.vove7.ctassistant.events.EventUtils.postActionEvent
import cn.vove7.ctassistant.events.NetEvent
import cn.vove7.ctassistant.events.StatusCodes
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_SUPPORT_SCHOOLS
import cn.vove7.ctassistant.events.WhatRequest.WHAT_LOGIN
import cn.vove7.ctassistant.openct.adapter.SchoolAdapter
import cn.vove7.ctassistant.openct.utils.SPUtil
import cn.vove7.ctassistant.openct.utils.VLog
import java.util.*

/**
 * Choose school,login
 */
@SuppressLint("ValidFragment")
class MainFragment constructor(context: Context, viewPager: ViewPager)
    : BaseFragment(viewPager), View.OnClickListener, View.OnFocusChangeListener {
    private lateinit var textSno: EditText
    private lateinit var textPass: EditText
    private lateinit var schoolText: TextView
    private lateinit var searchView: SearchView

    private var spUtil: SPUtil = SPUtil(context)

    private var loginProgressDialog: ProgressDialog? = null
    //学校点击
    override fun onItemClick(pos: Int, content: String) {
        setSchoolText(content)
        hideBottom()
    }

    override fun layout() = R.layout.fragment_select_school

    override fun onFocusChange(view: View, b: Boolean) {
        if (b) hideBottom()
    }

    override fun initView() {
        initBottomSheetView(resources.getString(R.string.text_choose_school))
        textSno = f(R.id.text_sno)
        textSno.onFocusChangeListener = this
        textPass = f(R.id.text_pa)
        textPass.onFocusChangeListener = this
        schoolText = f(R.id.school_text)
        schoolText.setOnClickListener(this)
        f<View>(R.id.btn_signin).setOnClickListener(this)
        f<View>(R.id.show_account).setOnClickListener(this)

        behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //这里是bottomSheet 状态的改变，根据slideOffset可以做一些动画
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    VLog.d(this, newState.toString())

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
                //bottomSheet.setAlpha(slideOffset);
            }
        })

        bottomToolbar.inflateMenu(R.menu.menu_toolbar_with_search_refresh)
        bottomToolbar.setOnMenuItemClickListener({ this.onOptionsItemSelected(it) })
        val searchItem = bottomToolbar.menu.findItem(R.id.search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.queryHint = "查找学校"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                VLog.d(this, "onQueryTextChange ----> $newText")
                querySchool(newText)

                return false
            }
        })
    }

    private fun querySchool(query: String) {
        val schools = ArrayList(SchoolAdapter.supportSchools!!.keys)
        var result = ArrayList<String>()
        if (query.trim { it <= ' ' } == "") {
            result = schools
        } else
            for (s in schools) {
                if (s.contains(query)) {
                    result.add(s)
                }
            }
        setBottomListData(result)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.refresh_icon -> {
                changeBottomLayout(SHOW_PROCESS)
                Handler().postDelayed(
                        { schoolAdapter.requestSchools() }, 500)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun initData() {
        changeBottomLayout(SHOW_PROCESS)
        schoolAdapter.initSupportSchools()
        val schName = spUtil.getString(R.string.key_old_school)
        setSchoolText(schName ?: return)
    }

    private fun login() {
        if (schoolAdapter.schCode == null) {
            showSnack("选择学校", listener = View.OnClickListener { _ -> showBottom() })
            return
        }
        val no = textSno.text.toString()
        val pa = textPass.text.toString()
        if (no.trim { it <= ' ' } == "" || pa.trim { it <= ' ' } == "") {
            showSnack("填写信息...")
            return
        }

        loginProgressDialog = ProgressDialog(context)
        loginProgressDialog!!.setTitle("正在登陆..")
        loginProgressDialog!!.setCancelable(false)
        loginProgressDialog!!.show()
        schoolAdapter.login(no, pa)
    }

    fun closeProgressDialog() {
        if (loginProgressDialog != null && loginProgressDialog!!.isShowing)
            loginProgressDialog!!.dismiss()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_signin -> {
                login()
            }
            R.id.school_text -> {
                showBottom()
            }
            R.id.show_account -> {
                showAccount()
            }
        }
    }
    private fun showAccount(){

    }

    override fun onActionEvent(event: ActionEvent) {
        return
    }

    override fun onNetEvent(event: NetEvent) {
        VLog.d(this, " message ----> " + event.message)
        when (event.what) {
            WHAT_LOGIN -> {
                closeProgressDialog()
                if (event.statusCode == StatusCodes.STATUS_OK) {
                    showSnack("登陆成功")
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    viewPager.setCurrentItem(1, true)
                    postActionEvent(ACTION_INIT_AY)
                } else {
                    showSnack("登陆失败")
                }
            }
            WHAT_GET_SUPPORT_SCHOOLS -> {
                if (event.statusCode == StatusCodes.STATUS_OK) {
                    VLog.d(this, "onRequestSuccess: 学校获取ok -> " + SchoolAdapter.supportSchools!!)
                    val schools = ArrayList(SchoolAdapter.supportSchools!!.keys)
                    setBottomListData(schools)
                    changeBottomLayout(SHOW_LIST)
                } else {
                    VLog.d(this, "onRequestFailed: 学校获取失败")
                    changeBottomLayout(SHOW_ERR)
                }
            }
        }
    }


    private fun setSchoolText(name: String) {

        val schCode = SchoolAdapter.supportSchools!![name] ?: ""
        VLog.d(this, "school code: -->$schCode")

        spUtil.setValue(R.string.key_old_school, name)
        spUtil.setValue(R.string.key_old_code, schCode)
        schoolAdapter.schCode = schCode
        schoolText.text = name
    }


}
