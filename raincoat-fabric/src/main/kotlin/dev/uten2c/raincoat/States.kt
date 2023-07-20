package dev.uten2c.raincoat

object States {
    @JvmStatic
    var isOnServer = false
        private set

    @JvmStatic
    var isHandshakeReceived = false

    var directionSendRequestedTime: Long? = null

    @JvmStatic
    var isRecoiling = false

    fun reset() {
        isOnServer = false
        isHandshakeReceived = false
        directionSendRequestedTime = null
        isRecoiling = false
    }

    fun onJoinServer() {
        isOnServer = true
    }
}
