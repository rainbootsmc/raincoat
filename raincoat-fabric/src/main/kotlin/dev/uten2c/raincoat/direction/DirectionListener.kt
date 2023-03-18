package dev.uten2c.raincoat.direction

import dev.uten2c.raincoat.States
import dev.uten2c.raincoat.network.Networking
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object DirectionListener {
    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register listener@{ client ->
            if (States.directionSendRequestedTime == null) {
                return@listener
            }
            val player = client.player ?: return@listener
            Networking.sendDirectionUpdate(player.yaw, player.pitch)
        }
    }
}
