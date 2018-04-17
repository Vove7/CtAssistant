package cn.vove7.cthelper.events

class ActionEvent(var action: String) {
    companion object {
        const val ACTION_INIT_AY = "initAcademicYearInfo"
        const val ACTION_GET_TT = "getTimeTable"
        const val ACTION_SUMMARY = "buildSummary"
    }
}
