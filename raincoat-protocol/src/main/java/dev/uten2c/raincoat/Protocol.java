package dev.uten2c.raincoat;

import dev.uten2c.raincoat.util.PacketId;
import org.jetbrains.annotations.NotNull;

public final class Protocol {
    public static final int PROTOCOL_VERSION = 1;

    // C2S
    public static final @NotNull PacketId HANDSHAKE_RESPONSE = id("handshake/response");
    public static final @NotNull PacketId KEY_PRESSED = id("key/pressed");
    public static final @NotNull PacketId KEY_RELEASED = id("key/released");
    public static final @NotNull PacketId DIRECTION_UPDATE = id("direction/update");
    public static final @NotNull PacketId SETTINGS_UPDATE = id("settings/update");

    // S2C
    public static final @NotNull PacketId HANDSHAKE_REQUEST = id("handshake/request");
    public static final @NotNull PacketId RECOIL = id("recoil");
    public static final @NotNull PacketId DIRECTION_SEND_REQUEST = id("direction/request");
    public static final @NotNull PacketId OUTDATED = id("outdated");

    private Protocol() {
    }

    private static PacketId id(@NotNull String id) {
        return new PacketId(id);
    }
}
