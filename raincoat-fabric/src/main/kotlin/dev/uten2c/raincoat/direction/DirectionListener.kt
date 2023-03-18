package dev.uten2c.raincoat.direction

import dev.uten2c.raincoat.Networking.sendDirectionUpdate
import dev.uten2c.raincoat.States
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object DirectionListener {
    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register listener@{ client ->
            if (States.directionSendRequestedTime == null) {
                return@listener
            }
            val player = client.player ?: return@listener
            sendDirectionUpdate(player.yaw, player.pitch)
        }
    }
}
