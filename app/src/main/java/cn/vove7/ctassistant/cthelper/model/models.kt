package cn.vove7.ctassistant.cthelper.model

import android.graphics.Paint
import cn.vove7.ctassistant.cthelper.utils.Utils
import cn.vove7.vtp.log.Vog
import com.bin.david.form.annotation.SmartColumn
import com.bin.david.form.annotation.SmartTable
import java.util.*

class AcademicYear {
    var year: Int = 0//2018
    var termCode: Int = 0//0,1
    var name: String = ""


    val code: String
        get() = year.toString() + termCode.toString()

    constructor(year: Int, termCode: Int, name: String) {
        this.year = year
        this.termCode = termCode
        this.name = name
    }

    constructor()


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

@SmartTable(name = "课表")
class ClassInfo {
    @SmartColumn(id = 1, name = "课程")
    val className: String = ""
    @SmartColumn(id = 2, name = "教师")
    val teacher: String = ""
    @SmartColumn(id = 5, name = "教室")
    val classRoom: String = ""
    @SmartColumn(id = 6, name = "周", align = Paint.Align.CENTER)
    val week: Int = 0//节数
    val weeks: Array<Int> = arrayOf()
    val node: Array<Int> = arrayOf()
    @SmartColumn(id = 4, name = "节数", align = Paint.Align.RIGHT)
    private var nodeStr: String = ""
        get() {
            val s = Arrays.toString(node)
            return s.substring(1, s.length - 1)
        }
    @SmartColumn(id = 3, name = "周数", align = Paint.Align.RIGHT)
    private var weeksStr: String = ""
        get() {
            val s = Arrays.toString(weeks)
            return s.substring(1, s.length - 1)
        }

    init {
        Vog.d(this, "init ---> $weeksStr")
        Vog.d(this, "init ---> $nodeStr")
    }

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

class CalendarAccount(val id: Long, val accName: String) {
    override fun toString(): String {
        return "CalendarAccount(id=$id, accName='$accName')"
    }
}