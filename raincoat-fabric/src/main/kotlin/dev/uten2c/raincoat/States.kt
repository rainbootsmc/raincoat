package dev.uten2c.raincoat

import dev.uten2c.raincoat.debug.DebugShapes
import dev.uten2c.raincoat.shake.ShakeEffect

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

    @JvmStatic
    var shakeEffect: ShakeEffect? = null

    fun reset() {
        isOnServer = false
        isHandshakeReceived = false
        directionSendRequestedTime = null
        isRecoiling = false
        shakeEffect = null
        DebugShapes.clearShapes()
    }

    fun onJoinServer() {
        isOnServer = true
    }
}
