package dev.uten2c.raincoat.direction;

import dev.uten2c.raincoat.Networking;
import dev.uten2c.raincoat.States;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class DirectionListener {
    private DirectionListener() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (States.directionSendRequestedTime == null) {
                return;
            }
            var player = client.player;
            if (player == null) {
                return;
            }
            Networking.sendDirectionUpdate(player.getYaw(), player.getPitch());
        });
    }
}
