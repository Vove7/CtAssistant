package cn.vove7.ctassistant.openct.model

import cn.vove7.ctassistant.openct.utils.Utils
import java.util.*

class AcademicYear {
    var year: Int = 0//2018
    var termCode: Int = 0//0,1
    var name: String = ""


    val code: String
        get() = year.toString() + termCode.toString()

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        other as AcademicYear
        return year == other.year && termCode == other.termCode
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + termCode
        result = 31 * result + name.hashCode()
        return result
    }
}

class ClassInfo(val teacher: String, val weeks: Array<Int>, val className: String, val classRoom: String, val node: Array<Int>//节数
                , val week: Int) {

    override fun toString(): String {
        return "ClassInfo{" +
                "teacher='" + teacher + '\''.toString() +
                ", weeks=" + Arrays.toString(weeks) +
                ", className='" + className + '\''.toString() +
                ", classRoom='" + classRoom + '\''.toString() +
                ", node=" + Arrays.toString(node) +
                ", week=" + week +
                "}\n"
    }
}

class TimeTable(var beginDate: String, var nodeList: MutableList<TimeTableNode>) {
    fun getBeginDate(year: Int): Calendar {
        val c = Calendar.getInstance()
        c.clear()
        val ts = beginDate.split(".")
        c.set(year, ts[0].toInt() - 1, ts[1].toInt(), 0, 0, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c
    }
}

class TimeTableNode(
        var nodeNum: Int,//1
//        var nodeName: String,//第一节
        var timeOfBeginClass: Time, //8：00
        var timeOfEndClass: Time? = null) {

    val nodeName: String
        get() = "第${Utils.buildLess10Thousand(nodeNum)}节"

    val beginMillis: Long
        get() = timeOfBeginClass.millis

    val endMillis: Long
        get() = timeOfEndClass?.millis ?: ((timeOfBeginClass.millis) + Time.MILLIS_OF_ONE_CLASS)
}

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

class CalendarAccount(val id: Long, val accName: String){
    override fun toString(): String {
        return "CalendarAccount(id=$id, accName='$accName')"
    }
}