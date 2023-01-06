package dev.uten2c.raincoat.util;

import org.jetbrains.annotations.NotNull;

public record PacketId(@NotNull String value) {
    @Override
    public String toString() {
        return "raincoat:" + value;
    }
}
