package cn.vove7.cthelper.openct.adapter

import android.content.Context
import cn.vove7.cthelper.R
import cn.vove7.cthelper.events.NetEvent
import cn.vove7.cthelper.events.StatusCodes
import cn.vove7.cthelper.events.StatusCodes.STATUS_BODY_NULL
import cn.vove7.cthelper.events.StatusCodes.STATUS_LOGIN_FAILED
import cn.vove7.cthelper.events.StatusCodes.STATUS_NET_ERR
import cn.vove7.cthelper.events.StatusCodes.STATUS_OK
import cn.vove7.cthelper.events.StatusCodes.STATUS_PARAM_NULL
import cn.vove7.cthelper.events.StatusCodes.STATUS_PARSE_ERR
import cn.vove7.cthelper.events.StatusCodes.STATUS_SERVER_ERR
import cn.vove7.cthelper.events.Where.WHAT_GET_BASE_WEEK
import cn.vove7.cthelper.events.Where.WHAT_GET_CLASS_TABLE
import cn.vove7.cthelper.events.Where.WHAT_GET_SUPPORT_SCHOOLS
import cn.vove7.cthelper.events.Where.WHAT_INIT_AY
import cn.vove7.cthelper.events.Where.WHAT_LOGIN
import cn.vove7.cthelper.openct.model.*
import cn.vove7.cthelper.openct.utils.*
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.litepal.crud.DataSupport
import java.io.IOException
import java.util.*

/**
 * 基类
 */
class SchoolAdapter(private val context: Context) {
    var schCode: String? = null
    var stuNo: String? = null
    var dateOfBaseWeek: Calendar? = null
    var allAcademicYears: Array<AcademicYear>? = null


    private var password: String? = null
    private var classInfoTable: Array<ClassInfo>? = null
    private val timeTable: ArrayList<TimeTableNode>? = null
    private val spUtil: SPUtil?
    var selectAcademicYear: AcademicYear? = null

    val baseWeekStr: String
        get() = dateOfBaseWeek!!.get(Calendar.YEAR).toString() + "-" +
                (dateOfBaseWeek!!.get(Calendar.MONTH)+1) + "-" +
                dateOfBaseWeek!!.get(Calendar.DAY_OF_MONTH)

    init {
        spUtil = SPUtil(context)
    }


    fun login(no: String, pa: String) {
        stuNo = no
        password = pa
        doLogin()
    }


    fun initSupportSchools() {
        //
        if (spUtil != null && spUtil.getBoolean(R.string.key_has_schools)) {
            readFromDatabase()
            sendEvent(WHAT_GET_SUPPORT_SCHOOLS, STATUS_OK, "STATUS_OK")
        } else {
            requestSchools()
        }
    }

    private fun readFromDatabase() {
        supportSchools = HashMap()
        val schools = DataSupport.findAll(School::class.java)
        for (school in schools) {
            supportSchools!![school.getsName()] = school.getsCode()
        }
        VLog.d("加载学校数据完成 :", " from local ----> " + Gson().toJson(supportSchools))
    }

    fun requestSchools() {
        val call = HttpHelper.buildCall(UrlUtils.URL_GET_SUPPORT_SCHOOLS, null)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                sendEvent(WHAT_GET_SUPPORT_SCHOOLS, STATUS_NET_ERR, "STATUS_NET_ERR")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()
                if (responseBody == null) {
                    sendEvent(WHAT_GET_SUPPORT_SCHOOLS, STATUS_BODY_NULL, "STATUS_BODY_NULL")
                    return
                }
                var status = STATUS_SERVER_ERR
                try {
                    val data = responseBody.string()
                    val model = Gson().fromJson(data, SupportSchoolModel::class.java)
                    if (model != null) {
                        supportSchools = model.supportSchools
                        status = STATUS_OK
                        SPUtil(context).setValue(R.string.key_has_schools, true)

                        DataSupport.deleteAll(School::class.java)

                        for ((key, value) in supportSchools!!) {
                            School(key, value).save()
                        }


                    } else
                        status = STATUS_SERVER_ERR

                } catch (e: Exception) {
                    status = STATUS_PARSE_ERR
                } finally {
                    sendEvent(WHAT_GET_SUPPORT_SCHOOLS, status, "")
                }
            }
        })
    }

    private fun buildBaseParams(): MutableMap<String, String> {
        val params = mutableMapOf<String, String>()
        params["sCode"] = schCode ?: ""
        params["sNo"] = stuNo ?: ""
        params["pa"] = password ?: ""
        return params
    }


    private fun doLogin() {
        val params = buildBaseParams()
        val call = HttpHelper.buildCall(UrlUtils.URL_LOGIN, params)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                sendEvent(WHAT_LOGIN, StatusCodes.STATUS_NET_ERR, "")

                //notifyRequestFinish(WHAT_LOGIN, STATUS_NET_ERR);
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var status = STATUS_SERVER_ERR
                try {
                    val responseBody = response.body()
                    if (responseBody == null) {
                        sendEvent(WHAT_LOGIN, StatusCodes.STATUS_BODY_NULL, "STATUS_BODY_NULL")
                        //notifyRequestFinish(WHAT_LOGIN, STATUS_BODY_NULL);
                        return
                    }
                    val b = responseBody.string()
                    val message = Gson().fromJson(b, BaseReturnMessage::class.java)

                    if (message.status == "success")
                        status = STATUS_OK
                    else
                        status = STATUS_LOGIN_FAILED
                } catch (e: Exception) {
                    status = STATUS_SERVER_ERR
                    e.printStackTrace()
                } finally {
                    sendEvent(WHAT_LOGIN, status, "")
                    //notifyRequestFinish(WHAT_LOGIN, status);
                }
            }
        })
    }

    //初始化学年信息
    fun initAcademicYearInfo() {
        val params = buildBaseParams()
        val call = HttpHelper.buildCall(UrlUtils.URL_GET_AY_INFO, params)
        call.enqueue(object : MyCallback(WHAT_INIT_AY) {
            override fun onSuccess(data: String) {
                val infoModel = Gson().fromJson(data, AyInfoModel::class.java)
                if (infoModel == null) {
                    callFailed("model null")
                    return
                }
                allAcademicYears = infoModel.allAy
                selectAcademicYear = infoModel.currentAy
                sendEvent(WHAT_INIT_AY, STATUS_OK, "STATUS_OK")
                requestBaseWeek()

            }
        })
    }

    fun requestBaseWeek() {
        val params = buildBaseParams()
        params["semester"] = selectAcademicYear!!.code

        val call = HttpHelper.buildCall(UrlUtils.URL_GET_BASE_WEEK, params)
        call.enqueue(object : MyCallback(WHAT_GET_BASE_WEEK) {
            override fun onSuccess(data: String) {
                super.onSuccess(data)
                val baseWeekModel = Gson().fromJson(data, BaseWeekModel::class.java)
                if (baseWeekModel == null) {
                    callFailed("model null")
                    return
                }
                dateOfBaseWeek = baseWeekModel.getBaseWeek()
                sendEvent(WHAT_GET_BASE_WEEK, STATUS_OK, "OK")
            }
        })
    }


    private fun requestClassTable() {
        val params = buildBaseParams()
        params["semester"] = selectAcademicYear!!.code

        val call = HttpHelper.buildCall(UrlUtils.URL_GET_CT, params)
        call.enqueue(object : MyCallback(WHAT_GET_CLASS_TABLE) {
            override fun onSuccess(data: String) {
                var status = STATUS_LOGIN_FAILED
                val returnBody = Gson().fromJson(data, ClassTableModel::class.java)
                if (returnBody == null) {
                    callFailed("model null")
                    return
                }
                val s = returnBody.status
                if (s == null) {
                    callFailed("login status null")
                    return
                }

                when (s) {
                    "login_failed" -> status = STATUS_LOGIN_FAILED
                    "none" -> status = STATUS_PARAM_NULL
                    "success" -> {
                        classInfoTable = returnBody.classTable
                        status = STATUS_OK
                    }
                    else -> status = STATUS_SERVER_ERR
                }

                sendEvent(WHAT_GET_CLASS_TABLE, status, "")
            }
        })
    }

    fun add2Calendar() {
        val calendarHelper = CalendarHelper(context, stuNo!!)

        var minWeek = 1
        var maxWeek = 1
        for (c in classInfoTable!!) {
            VLog.d(this, "add2Calendar: $c")
            val weeks = c.weeks
            if (minWeek > weeks[0])
                minWeek = weeks[0]
            if (maxWeek < weeks[weeks.size - 1])
                maxWeek = weeks[weeks.size - 1]
            //
        }
        val nowWeekIndex = IntArray(classInfoTable!!.size)//下标
        Arrays.fill(nowWeekIndex, 0)
        VLog.d(this, "add2Calendar: $minWeek -> $maxWeek")

        for (w in minWeek..maxWeek) {//周
            val weekBase = Calendar.getInstance()//当前周日期
            weekBase.time = dateOfBaseWeek!!.time
            weekBase.add(Calendar.WEEK_OF_YEAR, w - 1)

            val index = 0
            val classDay = Calendar.getInstance()
            for (c in classInfoTable!!) {//课
                if (w == c.weeks[nowWeekIndex[index]]) {
                    nowWeekIndex[index]++
                }
                val weekOffset = c.week//星期

                classDay.time = weekBase.time
                classDay.add(Calendar.DAY_OF_WEEK, weekOffset - 1)//课当天

                val nodes = c.node
                val begin = classDay.time.time + timeTable!![nodes[0]].beginMillis

                val end = classDay.time.time + timeTable[nodes[nodes.size - 1]].endMillis

                val data = String.format("%s\t%s\t%s\t%s", c.className,
                        c.classRoom,
                        Date(begin), Date(end))
                VLog.d(this, "add2Calendar: $data")

                //calendarHelper.addCalendarEvent(
                //        c.getClassName(),
                //        c.getClassRoom(),
                //        begin, begin + TWO_HOURS,false
                //);
            }

        }
    }

    fun sendEvent(what: Int, status: Int, message: String) {
        EventBus.getDefault().post(NetEvent(what, status, message))
    }

    companion object {
        var supportSchools: MutableMap<String, String>? = null
    }
}
