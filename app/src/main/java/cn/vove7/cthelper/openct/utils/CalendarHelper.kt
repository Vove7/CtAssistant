package cn.vove7.cthelper.openct.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract
import android.text.TextUtils
import java.util.*

class CalendarHelper(private val context: Context, private val suffix: String) {

    private fun hasOpenCTAccount(): Long {
        val cursor = context.contentResolver
                .query(Uri.parse(CALENDAR_URL), null, null, null, null)
        cursor.use { c ->
            if (c == null)
                return -1
            while (c.moveToNext()) {
                if (CALENDARS_DISPLAY_NAME + suffix == c.getString(
                                c.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))) {
                    return c.getLong(c.getColumnIndex(CalendarContract.Calendars._ID))
                }
            }
            return -1
        }
    }

    public fun getAllOpenCTAccount(): MutableList<String> {
        val accounts = mutableListOf<String>()

        val cursor = context.contentResolver
                .query(Uri.parse(CALENDAR_URL), null, null, null, null)
        cursor.use { c ->
            if (c == null)
                return accounts
            while (c.moveToNext()) {
                if (CALENDARS_DISPLAY_NAME + suffix == c.getString(
                                c.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))) {
                    accounts.add(c.getString(c.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)))
                }
            }
            return accounts
        }
    }


    fun printAllAccount() {
        val cursor = context.contentResolver
                .query(Uri.parse(CALENDAR_URL), null, null, null, null)
        try {
            if (cursor == null) {
                VLog.d(this, "printAllAccount: -------null")
                return
            }
            println("****************************************")
            while (cursor.moveToNext()) {
                println(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)))
            }
        } finally {
            cursor?.close()
        }
    }

    fun deleteOpenCtAccount() {
        val cursor = context.contentResolver
                .query(Uri.parse(CALENDAR_URL), null, null, null, null)
        try {
            if (cursor == null)
                return
            while (cursor.moveToNext()) {
                if (CALENDARS_DISPLAY_NAME + suffix == cursor.getString(
                                cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))) {
                    val id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
                    val uri = ContentUris.withAppendedId(Uri.parse(CALENDAR_URL), id)
                    context.contentResolver.delete(uri, null, null)
                    VLog.d(this, "deleteOpenCtAccount: $id")
                }
            }
        } finally {
            cursor?.close()
        }
    }

    fun addOpenCTAccount(): Long {
        val timeZone = TimeZone.getDefault()
        val value = ContentValues()
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME)

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME + suffix)
        value.put(CalendarContract.Calendars.VISIBLE, 1)
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.id)
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0)

        var calendarUri = Uri.parse(CALENDAR_URL)
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build()

        val result = context.contentResolver.insert(calendarUri, value)
        return if (result != null) ContentUris.parseId(result) else -1
    }

    //检查是否已经添加了日历账户，如果没有添加先添加一个日历账户
    private fun checkAndAddCalendarAccount(): Long {
        val id = hasOpenCTAccount()
        return if (id > 0)
            id
        else
            addOpenCTAccount()
    }

    fun addCalendarEvent(title: String, description: String, beginTime: Long, endTime: Long, isRemind: Boolean): Int {
        // 获取日历账户的id
        val calendarId = checkAndAddCalendarAccount()
        if (calendarId < 0)
            return RESULT_NO_ACCOUNT


        val event = ContentValues()
        event.put("title", title)
        event.put("description", description)
        // 插入账户的id
        event.put("calendar_id", calendarId)

        event.put(CalendarContract.Events.DTSTART, beginTime + 10000)
        event.put(CalendarContract.Events.DTEND, endTime)
        event.put(CalendarContract.Events.HAS_ALARM, 1)//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai")  //这个是时区，必须有，
        //添加事件
        val newEvent = context.contentResolver.insert(Uri.parse(CALENDAR_EVENT_URL), event)
                ?: // 添加日历事件失败直接返回
                return RESULT_ADD_FAILED
        //事件提醒的设定
        if (isRemind) {
            val values = ContentValues()
            values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent))
            // 提前10分钟有提醒
            values.put(CalendarContract.Reminders.MINUTES, 10)
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            val uri = context.contentResolver.insert(Uri.parse(CALENDAR_REMINDER_URL), values)
                    ?: // 添加闹钟提醒失败直接返回
                    return RESULT_ALARM_FAILED
        }
        return RESULT_OK
    }

    fun deleteCalendarEvent(title: String): Boolean {
        val eventCursor = context.contentResolver
                .query(Uri.parse(CALENDAR_EVENT_URL), null, null, null, null)
        eventCursor.use { cursor ->
            if (cursor != null && cursor.count > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val eventTitle = cursor.getString(cursor.getColumnIndex("title"))
                    if (!TextUtils.isEmpty(title) && title == eventTitle) {
                        val id = cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars._ID))//取得id
                        val deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDAR_EVENT_URL), id.toLong())
                        val rows = context.contentResolver.delete(deleteUri, null, null)
                        return rows > 0
                    }
                    cursor.moveToNext()
                }
            }
            return false
        }
    }

    companion object {
        private const val CALENDAR_URL = "content://com.android.calendar/calendars"
        private const val CALENDAR_EVENT_URL = "content://com.android.calendar/events"
        private const val CALENDAR_REMINDER_URL = "content://com.android.calendar/reminders"

        private const val CALENDARS_NAME = "OpenCT"
        private const val CALENDARS_ACCOUNT_NAME = "openct@gmail.com"
        private const val CALENDARS_ACCOUNT_TYPE = "com.android.exchange"
        private const val CALENDARS_DISPLAY_NAME = "OpenCT"

        const val RESULT_OK = 0
        const val RESULT_NO_ACCOUNT = 1
        const val RESULT_ADD_FAILED = 2
        const val RESULT_ALARM_FAILED = 3
    }

}
