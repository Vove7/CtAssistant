package cn.vove7.cthelper.openct.utils

import android.util.Log

object VLog {
    private const val v = 0
    private const val d = 1
    private const val i = 2
    private const val w = 3
    private const val e = 4
    private const val a = 5
    private const val output_level = 0

    fun d(o: Any, msg: String) {
        if (output_level <= d) {
            Log.d(o.javaClass.name, "  ----> $msg")
        }
    }

    fun v(o: Any, msg: String) {
        if (output_level <= v) {
            Log.v(o.javaClass.name, "  ----> $msg")
        }
    }

    fun i(o: Any, msg: String) {
        if (output_level <= i) {
            Log.i(o.javaClass.name, "  ----> $msg")
        }
    }

    fun w(o: Any, msg: String) {
        if (output_level <= w) {
            Log.w(o.javaClass.name, "  ----> $msg")
        }
    }

    fun e(o: Any, msg: String) {
        if (output_level <= e) {
            Log.e(o.javaClass.name, "  ----> $msg")
        }
    }

    fun a(o: Any, msg: String) {
        if (output_level <= a) {
            Log.wtf(o.javaClass.name, "  ----> $msg")
        }
    }
}
