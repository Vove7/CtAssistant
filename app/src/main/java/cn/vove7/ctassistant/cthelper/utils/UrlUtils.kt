package cn.vove7.ctassistant.cthelper.utils

import cn.vove7.ctassistant.BuildConfig

object UrlUtils {
    //    private const val URL_BASE = "http://10.0.0.2:5000/"
    private val URL_BASE = if (BuildConfig.DEBUG)
        "http://192.168.2.124:5000/"
    else "http://118.89.112.146:5000/"

    val URL_GET_CT = URL_BASE + "getClassTable"
    val URL_LOGIN = URL_BASE + "login"
    val URL_GET_SUPPORT_SCHOOLS = URL_BASE + "getSupportSchools"
    val URL_GET_AY_INFO = URL_BASE + "getAyInfo"
    val URL_GET_BASE_WEEK = URL_BASE + "getDateOfBaseWeek"
    val URL_GET_TIME_TABLE = URL_BASE + "getTimeTable"
    val URL_POST_APPLY_ADAPTER = URL_BASE + "postApplyAdapter"
}
