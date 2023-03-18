package dev.uten2c.raincoat

object States {
    @JvmStatic
    var isOnServer = false
        private set

    @JvmStatic
    var isHandshakeReceived = false

    var directionSendRequestedTime: Long? = null

    fun reset() {
        isOnServer = false
        isHandshakeReceived = false
        directionSendRequestedTime = null
    }

    fun onJoinServer() {
        isOnServer = true
    }
}
