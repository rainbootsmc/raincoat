package dev.uten2c.raincoat;

import org.jetbrains.annotations.Nullable;

public final class States {
    private static boolean isOnServer = false;
    public static @Nullable Long directionSendRequestedTime = null;

    private States() {
    }

    public static void reset() {
        isOnServer = false;
        directionSendRequestedTime = null;
    }

    public static boolean isOnServer() {
        return isOnServer;
    }

    public static void onJoinServer() {
        isOnServer = true;
    }
}
