package cn.vove7.cthelper.openct.model

class Time(private val hour: Int, private val minute: Int, private val second: Int) {

    val millis: Long
        get() = (second * MILLIS_OF_SECOND
                + minute * MILLIS_OF_MINUTE
                + hour * MILLIS_OF_HOUR).toLong()

    companion object {

        const val MILLIS_OF_SECOND = 1000
        const val MILLIS_OF_MINUTE = 60000
        const val MILLIS_OF_HOUR = 3600000
        const val MILLIS_OF_ONE_CLASS = 45 * MILLIS_OF_MINUTE
    }

}
