package cn.vove7.ctassistant.events

class ActionEvent(var action: Int,var code: Int) {
    companion object {
        const val ACTION_INIT_AY = 0x00000000
        const val ACTION_GET_TT = 0x00000001
        const val ACTION_SUMMARY = 0x00000002
        const val ACTION_ADD_EVENT = 0x00000003
        const val CODE_SUCCESS = 0x0001
        const val CODE_FAILED = 0x000f
        const val CODE_ADD_ACCOUNT_FAILED = 0x00ff


    }
}
