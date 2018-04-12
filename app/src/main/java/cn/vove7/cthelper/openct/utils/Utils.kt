package cn.vove7.cthelper.openct.utils


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

}
