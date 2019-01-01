package cn.vove7.ctassistant.cthelper.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object JsonUtils {
    fun <T> json2List(json: String): ArrayList<T> {
        return Gson().fromJson(json, object : TypeToken<List<T>>() {}.type)
    }

    fun <K, V> json2Map(json: String): Map<K, V> {
        return Gson().fromJson(json, object : TypeToken<Map<K, V>>() {}.type)
    }

    fun toJson(o: Any): String {
        return Gson().toJson(o)
    }
}
