package dev.uten2c.raincoat;

import dev.uten2c.raincoat.option.Options;
import dev.uten2c.raincoat.util.PacketId;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public final class Networking {
    private static final Logger LOGGER = LoggerFactory.getLogger("Raincoat Networking");
    private static long handshakeRequestedTime = 0L;

    private Networking() {
    }

    public static void registerListeners() {
        ClientPlayConnectionEvents.JOIN.register(Networking::onJoin);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> States.reset());
        registerReceiver(Protocol.HANDSHAKE_REQUEST, Networking::onHandshakeRequest);
        registerReceiver(Protocol.DIRECTION_SEND_REQUEST, Networking::onDirectionSendRequest);
        registerReceiver(Protocol.RECOIL, (client, handler, buf, listenerAdder) -> onRecoil(client, buf));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (handshakeRequestedTime != 0L && System.currentTimeMillis() - handshakeRequestedTime > 20000) {
            return;
        }
        States.onJoinServer();
        var metadata = FabricLoader.getInstance().getModContainer(RaincoatMod.MOD_ID).get().getMetadata();
        var version = metadata.getVersion().getFriendlyString();
        var resBuf = PacketByteBufs.create();
        resBuf.writeString(version);
        resBuf.writeVarInt(Protocol.PROTOCOL_VERSION);
        LOGGER.info("Send handshake packet (version: {}, protocol: {})", version, Protocol.PROTOCOL_VERSION);
        send(Protocol.HANDSHAKE_RESPONSE, resBuf);
        Networking.sendSettingsUpdate();
    }

    private static void onHandshakeRequest(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        handshakeRequestedTime = System.currentTimeMillis();
    }

    private static void onDirectionSendRequest(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        if (buf.readBoolean()) {
            States.directionSendRequestedTime = System.currentTimeMillis();
        } else {
            States.directionSendRequestedTime = null;
        }
    }

    private static void onRecoil(MinecraftClient client, PacketByteBuf buf) {
        var x = buf.readFloat();
        var y = buf.readFloat();
        var player = client.player;
        if (player != null) {
            player.setYaw(player.getYaw() + x);
            player.setPitch(player.getPitch() + y);
        }
    }

    public static void sendKeyPressedPacket(@NotNull NamedKey key) {
        send(Protocol.KEY_PRESSED, buf -> buf.writeEnumConstant(key));
    }

    public static void sendKeyReleasedPacket(@NotNull NamedKey key) {
        send(Protocol.KEY_RELEASED, buf -> buf.writeEnumConstant(key));
    }

    public static void sendDirectionUpdate(float yaw, float pitch) {
        var requestedTime = States.directionSendRequestedTime;
        if (requestedTime == null) {
            return;
        }
        send(Protocol.DIRECTION_UPDATE, buf -> {
            buf.writeVarInt((int) (System.currentTimeMillis() - requestedTime));
            buf.writeFloat(yaw);
            buf.writeFloat(pitch);
        });
    }

    public static void sendSettingsUpdate() {
        send(Protocol.SETTINGS_UPDATE, buf -> buf.writeBoolean(Options.isAdsHold()));
    }

    private static void registerReceiver(@NotNull PacketId id, ClientPlayNetworking.PlayChannelHandler channelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(identifier(id), channelHandler);
    }

    private static void send(@NotNull PacketId id, @NotNull PacketByteBuf buf) {
        if (States.isOnServer()) {
            ClientPlayNetworking.send(identifier(id), buf);
        }
    }

    private static void send(@NotNull PacketId id, @NotNull Consumer<PacketByteBuf> builder) {
        var buf = PacketByteBufs.create();
        builder.accept(buf);
        send(id, buf);
    }

    private static void sendEmpty(@NotNull PacketId id) {
        send(id, PacketByteBufs.empty());
    }

    private static Identifier identifier(@NotNull PacketId id) {
        return new Identifier(id.toString());
    }
}
