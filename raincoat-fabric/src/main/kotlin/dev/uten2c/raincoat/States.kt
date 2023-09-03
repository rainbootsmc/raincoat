package dev.uten2c.raincoat

import dev.uten2c.raincoat.debug.DebugShapes

object States {
    @JvmStatic
    var isOnServer = false
        private set

    @JvmStatic
    var isHandshakeReceived = false

    var directionSendRequestedTime: Long? = null

    @JvmStatic
    var isRecoiling = false

    @JvmStatic
    var showDebugShape = false

    fun reset() {
        isOnServer = false
        isHandshakeReceived = false
        directionSendRequestedTime = null
        isRecoiling = false
        DebugShapes.clearShapes()
    }

    fun onJoinServer() {
        isOnServer = true
    }
}
