package cn.vove7.ctassistant.cthelper.model

import cn.vove7.ctassistant.cthelper.utils.Vog
import java.text.SimpleDateFormat
import java.util.*

open class BaseReturnMessage {
    var status: String? = null
}

class ClassTableModel(var classTable: Array<ClassInfo>) : BaseReturnMessage()

class AyInfoModel(val currentAy: AcademicYear, val allAy: Array<AcademicYear>)
    : BaseReturnMessage()

class SupportSchoolModel(val supportSchools: MutableMap<String, SchoolInfo>) : BaseReturnMessage()

class BaseWeekModel(var dateOfBaseWeek: String) : BaseReturnMessage() {
    fun getBaseWeek(): Calendar {
        val n = dateOfBaseWeek.split("-")
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(n[0].toInt(), n[1].toInt() - 1, n[2].toInt()
                , 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        Vog.d(this, SimpleDateFormat().format(cal.time))
        return cal
    }
}

class TimeTableModel(var timetables: MutableList<TimeTable>) : BaseReturnMessage()