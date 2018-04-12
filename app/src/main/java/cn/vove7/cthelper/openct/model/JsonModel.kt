package cn.vove7.cthelper.openct.model

import java.util.*

open class BaseReturnMessage {
    var status: String? = null
}

class ClassTableModel(var classTable: Array<ClassInfo>) : BaseReturnMessage()

class AyInfoModel(val currentAy: AcademicYear, val allAy: Array<AcademicYear>)
    : BaseReturnMessage()

class SupportSchoolModel(val supportSchools: MutableMap<String, String>) : BaseReturnMessage()

class BaseWeekModel(var dateOfBaseWeek: String) : BaseReturnMessage() {
    fun getBaseWeek():Calendar {
        val n = dateOfBaseWeek.split("-");
        val cal = Calendar.getInstance()
        cal.set(n[0].toInt(), n[1].toInt()-1, n[2].toInt()
                , 0, 0, 0)
        return cal
    }
}