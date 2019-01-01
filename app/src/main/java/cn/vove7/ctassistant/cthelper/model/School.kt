package cn.vove7.ctassistant.cthelper.model

import org.litepal.crud.LitePalSupport

data class School(var sName: String, var info: SchoolInfo) : LitePalSupport() {
    var id: Long = 0

}
