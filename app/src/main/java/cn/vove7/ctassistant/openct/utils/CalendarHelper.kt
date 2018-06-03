package cn.vove7.ctassistant.openct.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.text.TextUtils
import cn.vove7.ctassistant.R
import cn.vove7.ctassistant.openct.model.CalendarAccount
import java.util.*

class CalendarHelper(private val context: Context, private val suffix: String = "") {

    private fun hasOpenCTAccount(): Long {
        val cursor = context.contentResolver
                .query(Uri.parse(CALENDAR_URL), null, null, null, null)
        cursor.use { c ->
            if (c == null) {
                return -1
            }
            var name: String
            while (c.moveToNext()) {
                name = c.getString(c.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                VLog.i(this, name)
                if ((CALENDARS_DISPLAY_NAME_PREFIX + suffix) == name) {
                    return c.getLong(c.getColumnIndex(CalendarContract.Calendars._ID))
                }
            }
            return -1
        }
    }

    fun getAllOpenCTAccount(): MutableList<CalendarAccount> {
        val accounts = mutableListOf<CalendarAccount>()

        val cursor = context.contentResolver
                .query(Uri.parse(CALENDAR_URL), null, null, null, null)
        cursor.use { c ->
            if (c == null) {
                return accounts
            }
            val indexName = c.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
            val indexId = c.getColumnIndex(CalendarContract.Calendars._ID)
            var accName: String
            var id: Long
            while (c.moveToNext()) {
                accName = c.getString(indexName)
                id = c.getLong(indexId)
                if (accName.startsWith(CALENDARS_DISPLAY_NAME_PREFIX)) {
                    accounts.add(CalendarAccount(id, accName))
                }
            }
            return accounts
        }
    }

    fun deleteCtAccountById(id: Long): Boolean {
        val uri = ContentUris.withAppendedId(Uri.parse(CALENDAR_URL), id)
        return 1 == context.contentResolver.delete(uri, null, null)
    }

    fun deleteThisCtAccount() {
        deleteCtAccountBySuffix(suffix)
    }

    fun deleteCtAccountBySuffix(suf: String): Boolean {
        val cursor = context.contentResolver
                .query(Uri.parse(CALENDAR_URL), null, null, null, null)
        cursor.use { cur ->
            if (cur == null) {
                return false
            }
            while (cur.moveToNext()) {
                val name = cur.getString(
                        cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                if (CALENDARS_DISPLAY_NAME_PREFIX + suf == name) {
                    val id = cur.getLong(cur.getColumnIndex(CalendarContract.Calendars._ID))
                    val uri = ContentUris.withAppendedId(Uri.parse(CALENDAR_URL), id)
                    VLog.d(this, "deleteCtAccountBySuffix: $id $name")
                    return 1 == context.contentResolver.delete(uri, null, null)
                }
            }
        }
        return false
    }

    fun addOpenCTAccount(suf: String? = null): Long {
        val postfix = suf ?: suffix
        val timeZone = TimeZone.getDefault()
        val value = ContentValues()
        value.put(CalendarContract.Calendars.NAME, context.packageName)

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME_PREFIX + postfix)
        value.put(CalendarContract.Calendars.VISIBLE, 1)
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, randomColor())
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
    fun checkAndAddCalendarAccount(): Long {
        val id = hasOpenCTAccount()
        if (id > 0) {
            deleteThisCtAccount()
        }
        return addOpenCTAccount()

    }

    fun addCalendarEvent(calendarId: Long, title: String, description: String, beginTime: Long, endTime: Long, isAlarm: Boolean): Int {

        val event = ContentValues()
        event.put("title", title)
        event.put("description", description)
        // 插入账户的id
        event.put("calendar_id", calendarId)

        event.put(CalendarContract.Events.DTSTART, beginTime + 10000)
        event.put(CalendarContract.Events.DTEND, endTime)
        event.put(CalendarContract.Events.HAS_ALARM, if (isAlarm) 1 else 0)//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_COLOR, randomColor())
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai")  //这个是时区，必须有
        //添加事件
        val newEvent = context.contentResolver.insert(Uri.parse(CALENDAR_EVENT_URL), event)
                ?: // 添加日历事件失败直接返回
                return RESULT_ADD_FAILED
        //事件提醒的设定
        if (isAlarm) {
            val values = ContentValues()
            values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent))
            // 提前10分钟有提醒
            values.put(CalendarContract.Reminders.MINUTES, 10)
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            context.contentResolver.insert(Uri.parse(CALENDAR_REMINDER_URL), values)
                    ?: // 添加闹钟提醒失败直接返回
                    return RESULT_ALARM_FAILED
        }
        return RESULT_OK
    }

    private fun randomColor(): Int {
        return context.resources.getColor(colors[(Math.random() * (colors.size - 1)).toInt()])


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

        private const val CALENDARS_NAME = "CTHelper"
        private const val CALENDARS_ACCOUNT_NAME = "cthelper@qq.com"
        private const val CALENDARS_ACCOUNT_TYPE = "LOCAL"
        private const val CALENDARS_DISPLAY_NAME_PREFIX = "CT-"

        const val RESULT_OK = 0
        const val RESULT_NO_ACCOUNT = 1
        const val RESULT_ADD_FAILED = 2
        const val RESULT_ALARM_FAILED = 3

        val colors = arrayOf(
                R.color.teal_A700,
                R.color.brown_800,
                R.color.orange_A700,
                R.color.deep_purple_A700,
                R.color.pink_A400,
                R.color.red_500,
                R.color.deep_purple_700,
                R.color.blue_500,
                R.color.light_blue_500,
                R.color.cyan_500,
                R.color.green_700,
                R.color.light_green_500,
                R.color.lime_600,
                R.color.yellow_A400,
                R.color.amber_500,
                R.color.orange_700,
                R.color.deep_orange_A400,
                R.color.blue_grey_500
        )
    }

}
