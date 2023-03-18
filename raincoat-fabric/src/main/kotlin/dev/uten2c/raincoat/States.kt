package dev.uten2c.raincoat

object States {
    @JvmStatic
    var isOnServer = false
        private set

    var directionSendRequestedTime: Long? = null

    fun reset() {
        isOnServer = false
        directionSendRequestedTime = null
    }

    fun onJoinServer() {
        isOnServer = true
    }
}
