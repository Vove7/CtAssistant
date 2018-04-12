package cn.vove7.cthelper.events

class NetEvent(var what: Int, var statusCode: Int, var message: String)
object StatusCodes {
    const val STATUS_OK = 928
    const val STATUS_NET_ERR = 730
    const val STATUS_PARSE_ERR = 790
    const val STATUS_BODY_NULL = 777
    const val STATUS_LOGIN_FAILED = 505
    const val STATUS_PARAM_NULL = 27
    const val STATUS_SERVER_ERR = 356
}

object Where {
    const val WHAT_GET_SUPPORT_SCHOOLS = 1
    const val WHAT_GET_CLASS_TABLE = 2
    const val WHAT_LOGIN = 3
    const val WHAT_INIT_AY = 4
    const val WHAT_GET_BASE_WEEK = 5

}
