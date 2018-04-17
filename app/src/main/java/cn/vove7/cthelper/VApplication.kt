package cn.vove7.cthelper

import android.app.Application

import org.litepal.LitePal

import cn.vove7.cthelper.openct.adapter.SchoolAdapter

class VApplication : Application() {

    override fun onCreate() {
        instance = this
        LitePal.initialize(this)

        schoolAdapter = SchoolAdapter(this)
        super.onCreate()
    }

    var schoolAdapter: SchoolAdapter?=null
    companion object {
        var instance: VApplication?=null
            internal set

    }
}
