package cn.vove7.ctassistant


import cn.vove7.ctassistant.cthelper.utils.Utils.num2Zh
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())

        //String data="[{'haust': '河南科技大学'}]";
        //Map<String,String> m=JsonUtils.json2Map(data);
        //System.out.println(m);

//        for (i in 0..1000) {
//            println("$i ---> ${num2Zh(i)}")
//        }
        val f = File("out.txt")
        f.writeText("")
        for (i in 0..10) {
            f.appendText("$i ---> ${num2Zh(i)}\r\n")
        }
        print(f.absolutePath)

//        for (i in arrayOf(9110000, 1111111, 1111100031)) {
//            println("$i ---> ${num2Zh(i)}")
//        }
    }

    @Test
    fun testAddTime() {
//        val t = Time(4, 50)
//        println(t.add(Time.TYPE_HOUR,5))
//        println(t.add(Time.TYPE_HOUR,25))
//        println(t.add(Time.TYPE_MINUTE,25))
//        println(t.add(Time.TYPE_MINUTE,800))

        val c = Calendar.getInstance()
        c.set(2018, 5, 1, 2, 33, 0)

        println(SimpleDateFormat().format(c.time))
        c.set(Calendar.HOUR, 23)
        println(SimpleDateFormat().format(c.time))


    }

    @Test
    fun threadStatus() {
        val t = Thread(Runnable {
            var i = 100000000
            while (i > 0) {
                i--
            }
        })
        do {
            when (t.state!!) {
                Thread.State.NEW -> {
                    print("new start")
                    t.start()
                }
                Thread.State.BLOCKED -> println("BLOCKED")
                Thread.State.TERMINATED -> println("TERMINATED")
                Thread.State.TIMED_WAITING -> println("TIMED_WAITING")
                Thread.State.WAITING -> println("WAITING")
                Thread.State.RUNNABLE -> println("RUNNABLE")
            }
//            Thread.sleep(100)
        } while (t.isAlive)

        print(t.state.name)

    }

}