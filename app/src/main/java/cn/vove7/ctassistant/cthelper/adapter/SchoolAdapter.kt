package cn.vove7.ctassistant.cthelper.adapter

import android.content.Context
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.cthelper.model.*
import cn.vove7.ctassistant.cthelper.utils.*
import cn.vove7.ctassistant.events.ActionEvent.Companion.ACTION_ADD_EVENT
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_ADD_ACCOUNT_FAILED
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_FAILED
import cn.vove7.ctassistant.events.ActionEvent.Companion.CODE_SUCCESS
import cn.vove7.ctassistant.events.EventUtils.postActionEvent
import cn.vove7.ctassistant.events.EventUtils.sendNetEvent
import cn.vove7.ctassistant.events.StatusCodes
import cn.vove7.ctassistant.events.StatusCodes.STATUS_FAILED
import cn.vove7.ctassistant.events.StatusCodes.STATUS_LOGIN_FAILED
import cn.vove7.ctassistant.events.StatusCodes.STATUS_OK
import cn.vove7.ctassistant.events.StatusCodes.STATUS_PARAM_NULL
import cn.vove7.ctassistant.events.StatusCodes.STATUS_SERVER_ERR
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_BASE_WEEK
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_CLASS_TABLE
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_SUPPORT_SCHOOLS
import cn.vove7.ctassistant.events.WhatRequest.WHAT_GET_TIME_TABLE
import cn.vove7.ctassistant.events.WhatRequest.WHAT_INIT_AY
import cn.vove7.ctassistant.events.WhatRequest.WHAT_LOGIN
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.LitePal
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 学校
 */
class SchoolAdapter(private val context: Context) {
    var schCode: String? = null
    private var stuNo: String? = null
    var dateOfBaseWeek: Calendar = Calendar.getInstance()
    var allAcademicYears: Array<AcademicYear>? = null

    private var password: String? = null
    var classInfoTable: Array<ClassInfo>? = null
    var timeTables = mutableListOf<TimeTable>()
    private val spUtil: SPUtil?
    var selectAcademicYear: AcademicYear = AcademicYear(2017, 0, "20170")

    val baseWeekStr: String
        get() = dateOfBaseWeek!!.get(Calendar.YEAR).toString() + "-" +
                (dateOfBaseWeek!!.get(Calendar.MONTH) + 1) + "-" +
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
        if (spUtil != null && spUtil.getBoolean(R.string.key_has_schools, false)) {
            readFromDatabase()
            sendNetEvent(WHAT_GET_SUPPORT_SCHOOLS, STATUS_OK, "STATUS_OK")
        } else {
            requestSchools()
        }
    }

    private fun readFromDatabase() {
        supportSchools = HashMap()
        val schools = LitePal.findAll(School::class.java)
        for (school in schools) {
            val info = LitePal.where("school_id=?", school.id.toString()).findFirst(SchoolInfo::class.java)
            supportSchools!![school.sName] = info
        }
        Vog.d("加载学校数据完成 :", " from local ----> " + Gson().toJson(supportSchools))
    }

    fun requestSchools() {
        val call = HttpHelper.buildCall(UrlUtils.URL_GET_SUPPORT_SCHOOLS, null)
        call.enqueue(object : MyCallback(WHAT_GET_SUPPORT_SCHOOLS) {
            override fun onSuccess(data: String) {
                try {
                    val model = Gson().fromJson(data, SupportSchoolModel::class.java)
                    val status =
                        if (model != null) {
                            supportSchools = model.supportSchools
                            SPUtil(context).setValue(R.string.key_has_schools, true)
                            LitePal.deleteAll(School::class.java)
                            LitePal.deleteAll(SchoolInfo::class.java)
                            for ((key, value) in supportSchools!!) {
                                School(key, value).save()
                                value.save()
                            }
                            STATUS_OK
                        } else {
                            STATUS_SERVER_ERR
                        }
                    sendNetEvent(WHAT_GET_SUPPORT_SCHOOLS, status)
                } catch (e: Exception) {
                    e.printStackTrace()
                    sendNetEvent(WHAT_GET_SUPPORT_SCHOOLS, STATUS_FAILED, "发生错误")
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
                sendNetEvent(WHAT_LOGIN, StatusCodes.STATUS_NET_ERR, "")

                //notifyRequestFinish(WHAT_LOGIN, STATUS_NET_ERR);
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var status = STATUS_SERVER_ERR
                try {
                    val responseBody = response.body()
                    if (responseBody == null) {
                        sendNetEvent(WHAT_LOGIN, StatusCodes.STATUS_BODY_NULL, "STATUS_BODY_NULL")
                        //notifyRequestFinish(WHAT_LOGIN, STATUS_BODY_NULL);
                        return
                    }
                    val b = responseBody.string()
                    val message = Gson().fromJson(b, BaseReturnMessage::class.java)

                    status = if (message.status == "success") {
                        STATUS_OK
                    } else {
                        STATUS_LOGIN_FAILED
                    }
                } catch (e: Exception) {
                    status = STATUS_SERVER_ERR
                    e.printStackTrace()
                } finally {
                    sendNetEvent(WHAT_LOGIN, status, "")
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
                sendNetEvent(WHAT_INIT_AY, STATUS_OK, "STATUS_OK")
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
                sendNetEvent(WHAT_GET_BASE_WEEK, STATUS_OK, "OK")
            }
        })
    }

    fun requestTimeTable() {
        val params = buildBaseParams()

        val call = HttpHelper.buildCall(UrlUtils.URL_GET_TIME_TABLE, params)
        call.enqueue(object : MyCallback(WHAT_GET_TIME_TABLE) {
            override fun onSuccess(data: String) {
                val model = Gson().fromJson(data, TimeTableModel::class.java)
                if (model == null) {
                    callFailed("model null")
                    return
                }
                when (model.status) {
                    "success" -> {
                        timeTables = model.timetables
                        sendNetEvent(WHAT_GET_TIME_TABLE, STATUS_OK, "STATUS_OK")
                    }
                    "failed" ->
                        sendNetEvent(WHAT_GET_TIME_TABLE, STATUS_FAILED, "STATUS_FAILED")
                    else ->
                        sendNetEvent(WHAT_GET_TIME_TABLE, STATUS_FAILED, "STATUS UNKNOWN - ${model.status}")
                }


            }
        })

    }


    fun requestClassTable() {
        val params = buildBaseParams()
        params["semester"] = selectAcademicYear.code

        val call = HttpHelper.buildCall(UrlUtils.URL_GET_CT, params)
        call.enqueue(object : MyCallback(WHAT_GET_CLASS_TABLE) {
            override fun onSuccess(data: String) {
                val model = Gson().fromJson(data, ClassTableModel::class.java)
                if (model == null) {
                    callFailed("model null")
                    return
                }
                val s = model.status
                if (s == null) {
                    callFailed("login status null")
                    return
                }

                val status: Int
                when (s) {
                    "login_failed" -> status = STATUS_LOGIN_FAILED
                    "none" -> status = STATUS_PARAM_NULL
                    "success" -> {
                        classInfoTable = model.classTable
                        status = STATUS_OK
                    }
                    else -> status = STATUS_SERVER_ERR
                }

                sendNetEvent(WHAT_GET_CLASS_TABLE, status, "")
            }
        })
    }

    val dateFormat = SimpleDateFormat()
    fun add2Calendar(isRemin: Boolean, isShowWeek: Boolean) {
        dateOfBaseWeek?.set(Calendar.HOUR, 0)
        if (classInfoTable == null) {
            postActionEvent(ACTION_ADD_EVENT, CODE_FAILED)
            return
        }
        //检查账户

        val calendarHelper = CalendarHelper(context, stuNo!!)
        // 获取日历账户的id
        val calendarId = calendarHelper.checkAndAddCalendarAccount()
        if (calendarId < 0) {
            postActionEvent(ACTION_ADD_EVENT, CODE_ADD_ACCOUNT_FAILED)
            return
//            return CalendarHelper.RESULT_NO_ACCOUNT
        }

        for (c in classInfoTable!!) {
            val cDay = Calendar.getInstance()//当前周日期

            Vog.d(this, "添加课程 : ${c.className} - ${c.classRoom} 周${c.week}")

            for (w in c.weeks) {
                cDay.time = dateOfBaseWeek.time
                cDay.add(Calendar.WEEK_OF_YEAR, w - 1)
                cDay.add(Calendar.DAY_OF_WEEK, c.week - 1)//课当天
                val timeTable = getTimeTable(cDay)//当天作息表
                val begin = cDay.time.time + timeTable!![c.node[0]].beginMillis
                val end = cDay.time.time + timeTable[c.node[c.node.size - 1]].endMillis

                val data = String.format("第%d周  %s-%s", w,
                        dateFormat.format(begin), dateFormat.format(end))
                Vog.d(this, "---- : $data")

                if (calendarHelper.addCalendarEvent(calendarId,
                                (if (isShowWeek) "[$w]" else "") + "${c.className}@${c.classRoom}"
                                , c.teacher, begin, end, isRemin) == CalendarHelper.RESULT_ADD_FAILED) {
                    postActionEvent(ACTION_ADD_EVENT, CODE_FAILED)
                    return
                }

            }
        }
        postActionEvent(ACTION_ADD_EVENT, CODE_SUCCESS)
    }


    companion object {
        var supportSchools: MutableMap<String, SchoolInfo>? = null
    }


    fun buildSummary(): String {
        val builder = StringBuilder()
        builder.append("学号：").appendln(stuNo)
        builder.append("学期：").appendln(selectAcademicYear.name)
        builder.append("第一周周一：").appendln(baseWeekStr)

        return builder.toString()
    }

    //根据日期选择作息表
    private fun getTimeTable(date: Calendar): MutableList<TimeTableNode>? {
//        val cDate = Calendar.getInstance()
//        cDate.time = date.time
        val size = timeTables.size
        if (size == 1)
            return timeTables[0].nodeList

        for ((index, t) in timeTables.withIndex()) {
            if (date < t.getBeginDate(date.get(Calendar.YEAR))) {
                return timeTables[(index + size - 1) % size].nodeList
            }
        }
        return timeTables[size - 1].nodeList
    }

    fun getAyPosition(): Int {
        for ((index, ay) in allAcademicYears!!.withIndex()) {
            if (selectAcademicYear?.code == ay.code)
                return index
        }
        return -1
    }

}
