package dev.uten2c.raincoat.network

import dev.uten2c.raincoat.States
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket

// IDが0xFFかつHandshakeしていたら送らない
object PingListener {
    private const val FLAG_ID = 0xFF
    private var onFlagIdReceived = false

    @JvmStatic
    fun onPing(id: Int): Boolean {
        if (id == FLAG_ID) {
            onFlagIdReceived = true
            return false
        }
        return true
    }

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register listener@{ client ->
            if (!onFlagIdReceived) {
                return@listener
            }
            onFlagIdReceived = false
            if (States.isHandshakeReceived) {
                return@listener
            }
            val networkHandler = client.networkHandler ?: return@listener
            networkHandler.sendPacket(PlayPongC2SPacket(FLAG_ID))
        }
    }
}
