package cn.vove7.ctassistant

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import cn.vove7.ctassistant.cthelper.utils.CalendarHelper
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented add2Calendar, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under add2Calendar.
        val appContext = InstrumentationRegistry.getTargetContext()
        val calendarHelper = CalendarHelper(appContext, "123")
        calendarHelper.addOpenCTAccount()


    }
}
