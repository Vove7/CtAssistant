package cn.vove7.ctassistant.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.VApplication
import cn.vove7.ctassistant.adapter.BottomListAdapter
import cn.vove7.ctassistant.events.ActionEvent
import cn.vove7.ctassistant.events.NetEvent
import cn.vove7.ctassistant.interfaces.OnBottomItemClickListener
import cn.vove7.ctassistant.cthelper.adapter.SchoolAdapter
import cn.vove7.ctassistant.view.VToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@SuppressLint("ValidFragment")
abstract class BaseFragment(var viewPager: ViewPager) : Fragment(), OnBottomItemClickListener {
    lateinit var schoolAdapter: SchoolAdapter
    var behavior: BottomSheetBehavior<*>? = null

    private lateinit var fragmentView: View
    var parentView: View? = null
    protected lateinit var bottomToolbar: Toolbar
    protected lateinit var bottomListView: ListView
    protected lateinit var errLayout: View
    protected lateinit var progressBar: View
    protected lateinit var listAdapter: BottomListAdapter

    val isBottomSheetShowing: Boolean
        get() = behavior?.state != BottomSheetBehavior.STATE_HIDDEN

    override fun onCreate(savedInstanceState: Bundle?) {
        registerEvent()
        super.onCreate(savedInstanceState)
        initToast()
        schoolAdapter = VApplication.instance.schoolAdapter
    }

    @SuppressLint("ShowToast")
    private fun initToast() {
        toast = VToast.with(context!!).top()
    }

    private fun registerEvent() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    /**
     * 指定布局id
     */
    abstract fun layout(): Int

    /**
     * 初始布局View
     */
    abstract fun initView()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        registerEvent()
        parentView = container
        fragmentView = inflater.inflate(layout(), container, false)
        initView()
        initData()
        return fragmentView
    }

    abstract fun initData()

    fun hideBottom() {
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun showBottom() {
        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onItemClick(pos: Int, content: String) {
    }

    companion object {
        const val SHOW_LIST = 0
        const val SHOW_PROCESS = 1
        const val SHOW_ERR = 2
    }

    /**
     * 复写onItemClick
     */
    fun initBottomSheetView(title: String) {
        bottomListView = f(R.id.bottom_list_view)
//        bottomListView!!.layoutManager = LinearLayoutManager(context)
        bottomToolbar = f(R.id.bottom_toolbar)
        progressBar = f(R.id.refresh_bar)
        errLayout = f(R.id.err_layout)
        bottomToolbar.title = title
        bottomToolbar.setNavigationOnClickListener { _ -> hideBottom() }
        val bottomView = f<View>(R.id.bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomView)
        hideBottom()
    }

    //设置BottomList数据
    fun setBottomListData(schools: ArrayList<String>) {
        listAdapter = BottomListAdapter(schools, this)
        bottomListView.adapter = listAdapter
        listAdapter.notifyDataSetChanged()
    }

    //改变bottom布局，刷新，err
    fun changeBottomLayout(showIndex: Int) {
        val views = arrayOf(bottomListView, progressBar, errLayout)

        for ((index, v) in views.withIndex()) {
            if (showIndex == index)
                v.visibility = View.VISIBLE
            else
                v.visibility = View.GONE
        }
    }

    override fun onStart() {
        registerEvent()
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    internal fun showSnack(msg: String, hasAction: Boolean = false, listener: View.OnClickListener? = null) {
        val bar = Snackbar.make(parentView!!, msg, if (hasAction) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_SHORT)
        if (hasAction)
            bar.setAction("OK") { _ -> bar.dismiss() }
        if (listener != null) {
            bar.setAction("DO", listener)
        }
        bar.show()
    }

    private lateinit var toast: VToast

    fun toast(textRes: Int) {
        toast(getString(textRes))
    }

    fun toast(msg: String) {
        toast.showShort(msg)
    }

    fun <T : View> f(v: View, id: Int): T {
        return v.findViewById(id)
    }

    fun <T : View> f(id: Int): T {
        return fragmentView.findViewById(id)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onNetEvent(event: NetEvent) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onActionEvent(event: ActionEvent) {
    }
}
