package cn.vove7.ctassistant.openct.utils


object Utils {
    fun strArr2IntArr(sArr: Array<String>): Array<Int> {
        val izc = arrayOf<Int>(sArr.size)
        var index = 0
        for (s in sArr) {
            try {
                izc[index] = Integer.parseInt(s)
            } catch (e: Exception) {
                izc[index] = 0
                e.printStackTrace()
            } finally {
                index++
            }
        }
        return izc
    }

    private val unitArray = arrayOf("", "十", "百", "千")
    private val oarray = arrayOf("", "万", "亿")

    private val numZhLowerArray = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九")

    fun num2Zh(num: Int): String {
        if (num < 10) {
            return numZhLowerArray[num]
        }
        val builder = StringBuilder()

        var s = num.toString()
        val headLen = s.length % 4

        var head = s.substring(0, headLen)
        if (headLen != 0) {
            if (head.isNotEmpty())
                builder.append(buildLess10Thousand(head.toInt())).append(oarray[s.length / 4])
            s = s.substring(headLen)
        }
        while (s.isNotEmpty()) {
            head = s.substring(0, 4)
            val n = head.toInt()
            s = s.substring(4)
            builder.append(if (n >= 1000 || n == 0) "" else numZhLowerArray[0])
                    .append(buildLess10Thousand(n))
                    .append(if (n == 0) "" else oarray[s.length / 4])
        }
        return builder.toString()
    }

    fun buildLess10Thousand(num: Int): String {
        val s = num.toString().toList()
        val builder = StringBuilder()
        var len = s.size
        var tmp = ""

        for (i in s) {
            val n = i - '0'
            len--
            tmp = if (n == 0) {
                if (tmp != numZhLowerArray[0])
                    numZhLowerArray[0]
                else
                    continue
            } else {
                builder.append(tmp).append(
                        when {
                            len == 0 -> numZhLowerArray[n]
                            (s.size == 2 && n == 1) -> unitArray[len]
                            else -> "${numZhLowerArray[n]}${unitArray[len]}"
                        }
                )
                ""
            }
        }
        return builder.toString()
    }

}
