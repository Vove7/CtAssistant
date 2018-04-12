package cn.vove7.cthelper.openct.model

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
}

class ClassTable {
    val classInfos = ArrayList<ClassInfo>()

    fun add(c: ClassInfo) {
        classInfos.add(c)
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

class TimeTableNode {
    var nodeNum: String? = null//1
    var nodeName: String? = null//第一节
    var timeOfBeginClass: Time? = null//8：00
    var timeOfEndClass: Time? = null


    val beginMillis: Long
        get() = timeOfBeginClass!!.millis

    val endMillis: Long
        get() = timeOfEndClass?.millis ?: ((timeOfBeginClass?.millis
                ?: 0) + Time.MILLIS_OF_ONE_CLASS)
}
