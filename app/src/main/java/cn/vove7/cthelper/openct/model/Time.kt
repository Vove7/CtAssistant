package cn.vove7.cthelper.openct.model

class Time(var hour: Int, var minute: Int/*, val second: Int*/) {

    val millis: Long
        get() = (//second * MILLIS_OF_SECOND
                +minute * MILLIS_OF_MINUTE
                        + hour * MILLIS_OF_HOUR).toLong()

    override fun toString(): String {
        return String.format("%d:%02d", hour, minute)
    }

    fun add(type: Int, value: Int): Time {
        return when (type) {
            TYPE_HOUR -> {
                val newHour = (hour + value) % 24
                Time(newHour, minute)
            }
            TYPE_MINUTE -> {
                var newMinute = minute + value
                val newHour = (hour + (newMinute / 60)) % 24
                newMinute %= 60
                Time(newHour, newMinute)
            }
            else -> Time(hour, minute)
        }
    }


    companion object {
        const val MILLIS_OF_SECOND = 1000
        const val MILLIS_OF_MINUTE = 60000
        const val MILLIS_OF_HOUR = 3600000
        const val MILLIS_OF_ONE_CLASS = 45 * MILLIS_OF_MINUTE
        const val TYPE_HOUR = 0
        const val TYPE_MINUTE = 1
    }

}
