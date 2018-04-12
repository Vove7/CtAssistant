package cn.vove7.cthelper.fragments


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import cn.vove7.cthelper.R
import cn.vove7.cthelper.adapter.SchoolListAdapter
import cn.vove7.cthelper.events.ActionEvent
import cn.vove7.cthelper.events.NetEvent
import cn.vove7.cthelper.events.StatusCodes
import cn.vove7.cthelper.events.Where.WHAT_GET_SUPPORT_SCHOOLS
import cn.vove7.cthelper.events.Where.WHAT_LOGIN
import cn.vove7.cthelper.interfaces.OnSchoolItemClickListener
import cn.vove7.cthelper.openct.adapter.SchoolAdapter
import cn.vove7.cthelper.openct.utils.SPUtil
import cn.vove7.cthelper.openct.utils.VLog
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Choose school,login
 */
class MainFragment : BaseFragment, View.OnClickListener, View.OnFocusChangeListener {

    private var textSno: EditText? = null
    private var textPass: EditText? = null
    private var parent: View? = null
    internal var view: View? = null
    internal var viewPager: ViewPager? = null
    internal var schoolAdapter: SchoolAdapter? = null
    internal var schoolText: TextView? = null
    internal var schoolListView: RecyclerView? = null
    internal var bottomToolbar: Toolbar? = null
    internal var listAdapter: SchoolListAdapter? = null
    internal var progressBar: View? = null
    internal var errLayout: View? = null

    internal var spUtil: SPUtil? = null

    internal var behavior: BottomSheetBehavior<*>? = null

    internal var loginProgressDialog: ProgressDialog? = null

    val isBottomSheetShowing: Boolean
        get() = behavior?.state != BottomSheetBehavior.STATE_HIDDEN

    //学校点击
    private val listener: OnSchoolItemClickListener = OnSchoolItemClickListener { pos, name ->
        setSchoolText(name)
        hideBottom()
    }

    constructor() {}

    @SuppressLint("ValidFragment")
    constructor(context: Context, schoolAdapter: SchoolAdapter?) {
        this.schoolAdapter = schoolAdapter
        spUtil = SPUtil(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_select_school, container, false)
        parent = container
        initView()
        initData()
        return view
    }

    override fun onFocusChange(view: View, b: Boolean) {
        if (b) hideBottom()
    }

    fun setViewPager(viewPager: ViewPager?) {
        this.viewPager = viewPager
    }

    private fun initView() {
        textSno = `$`(R.id.text_sno)
        textSno!!.onFocusChangeListener = this
        textPass = `$`(R.id.text_pa)
        textPass!!.onFocusChangeListener = this
        `$`<View>(R.id.btn_get).setOnClickListener(this)

        val bottomView = view!!.findViewById<View>(R.id.bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomView)
        hideBottom()

        behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //这里是bottomSheet 状态的改变，根据slideOffset可以做一些动画
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
                //bottomSheet.setAlpha(slideOffset);
            }
        })

        schoolListView = view!!.findViewById(R.id.school_list_view)
        schoolListView!!.layoutManager = LinearLayoutManager(context)
        schoolText = `$`(R.id.school_text)
        schoolText!!.setOnClickListener(this)
        progressBar = view!!.findViewById(R.id.refresh_bar)
        errLayout = view!!.findViewById(R.id.err_layout)

        bottomToolbar = view!!.findViewById(R.id.bottom_toolbar)
        bottomToolbar!!.inflateMenu(R.menu.menul_bottom_toolbar)
        bottomToolbar!!.setOnMenuItemClickListener({ this.onOptionsItemSelected(it) })
        bottomToolbar!!.setNavigationOnClickListener { v -> hideBottom() }
        val searchItem = bottomToolbar!!.menu.findItem(R.id.search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView

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
        setSchoolData(result)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.refresh_school -> {
                changeBottomLayout(SHOW_PROCESS)
                Handler().postDelayed(
                        { schoolAdapter!!.requestSchools() }, 500)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //改变bottom布局，刷新，err
    private fun changeBottomLayout(showIndex: Int) {
        val views = arrayOf(schoolListView, progressBar, errLayout)

        var index = 0
        for (v in views) {
            if (showIndex == index++)
                v?.visibility = View.VISIBLE
            else
                v?.visibility = View.GONE
        }

    }

    private fun initData() {
        //schoolAdapter.setRequestListener(requestListener);
        schoolAdapter!!.initSupportSchools()
        val schName = spUtil!!.getString(R.string.key_old_school)
        setSchoolText(schName)
    }

    private fun login() {
        if (schoolAdapter!!.schCode == null) {
            Snackbar.make(view!!, "选择学校", Snackbar.LENGTH_SHORT)
                    .setAction("Do") { v -> showBottom() }
                    .show()
            return
        }
        val no = textSno!!.text.toString()
        val pa = textPass!!.text.toString()
        if (no.trim { it <= ' ' } == "" || pa.trim { it <= ' ' } == "") {
            Snackbar.make(view!!, "填写信息...", Snackbar.LENGTH_SHORT)
                    .show()
            return
        }

        loginProgressDialog = ProgressDialog(context)
        loginProgressDialog!!.setTitle("正在登陆..")
        loginProgressDialog!!.setCancelable(false)
        loginProgressDialog!!.show()
        schoolAdapter!!.login(no, pa)
    }

    fun closeProgressDialog() {
        if (loginProgressDialog != null && loginProgressDialog!!.isShowing)
            loginProgressDialog!!.dismiss()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_get -> {
                //schoolAdapter.setRequestListener(requestListener);
                login()
            }
            R.id.school_text -> {
                showBottom()
            }
        }
    }

    fun hideBottom() {
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showBottom() {
        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun <T : View> `$`(vId: Int): T {
        return view!!.findViewById(vId)
    }

    override fun onMessageEvent(event: NetEvent) {
        VLog.d(this, " message ----> " + event.message)
        when (event.what) {
            WHAT_LOGIN -> {
                closeProgressDialog()
                if (event.statusCode == StatusCodes.STATUS_OK) {
                    Snackbar.make(parent!!, "登陆成功", Snackbar.LENGTH_SHORT).show()
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    viewPager!!.setCurrentItem(1, true)
                    EventBus.getDefault().post(ActionEvent("ok"))
                } else {
                    Snackbar.make(parent!!, "登陆失败", Snackbar.LENGTH_SHORT).show()
                }
            }
            WHAT_GET_SUPPORT_SCHOOLS -> {
                if (event.statusCode == StatusCodes.STATUS_OK) {
                    VLog.d(this, "onRequestSuccess: 学校获取ok -> " + SchoolAdapter.supportSchools!!)
                    val schools = ArrayList(SchoolAdapter.supportSchools!!.keys)
                    setSchoolData(schools)
                    changeBottomLayout(SHOW_LIST)
                } else {
                    VLog.d(this, "onRequestFailed: 学校获取失败")
                    changeBottomLayout(SHOW_ERR)
                }
            }
        }
    }

    private fun setSchoolData(schools: ArrayList<String>) {
        listAdapter = SchoolListAdapter(schools, listener)
        schoolListView!!.adapter = listAdapter
        listAdapter!!.notifyDataSetChanged()
    }

    private fun setSchoolText(name: String?) {
        if (name == null)
            return
        val schCode = SchoolAdapter.supportSchools!![name]?:""
        VLog.d(this, "school code: -->$schCode")

        spUtil!!.setValue(R.string.key_old_school, name)
        spUtil!!.setValue(R.string.key_old_code, schCode)
        schoolAdapter!!.schCode = schCode
        schoolText!!.text = name
    }

    companion object {

        const val SHOW_LIST = 0
        const val SHOW_PROCESS = 1
        const val SHOW_ERR = 2
    }
}
