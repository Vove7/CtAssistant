package cn.vove7.ctassistant

import android.app.Application

import org.litepal.LitePal

import cn.vove7.ctassistant.openct.adapter.SchoolAdapter

class VApplication : Application() {

    override fun onCreate() {
        instance = this
        LitePal.initialize(this)

        schoolAdapter = SchoolAdapter(this)
        super.onCreate()
    }

    lateinit var schoolAdapter: SchoolAdapter
    companion object {
        lateinit var instance: VApplication
            internal set

    }
}
