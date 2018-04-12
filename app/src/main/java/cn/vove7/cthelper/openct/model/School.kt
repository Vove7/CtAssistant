package cn.vove7.cthelper.openct.model

import org.litepal.crud.DataSupport

class School(internal var sName: String, internal var sCode: String) : DataSupport() {

    fun getsName(): String {
        return sName
    }

    fun setsName(sName: String) {
        this.sName = sName
    }

    fun getsCode(): String {
        return sCode
    }

    fun setsCode(sCode: String) {
        this.sCode = sCode
    }
}
