package dev.uten2c.raincoat.keybinding;

import dev.uten2c.raincoat.NamedKey;
import dev.uten2c.raincoat.Networking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class KeyBindings {
    private static final String RAINCOAT_KEYBINDING_CATEGORY = "key.categories.raincoat";
    private static final String RELOAD_KEYBINDING_KEY = "key.raincoat.reload";

    private static boolean reloadKeyPressed;
    private static boolean attackKeyPressed;
    private static boolean useKeyPressed;
    private static final List<Runnable> queue = new ArrayList<>();

    private KeyBindings() {
    }

    public static void register() {
        var reloadKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(RELOAD_KEYBINDING_KEY, GLFW.GLFW_KEY_R, RAINCOAT_KEYBINDING_CATEGORY));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (reloadKeyBinding.isPressed()) {
                if (!reloadKeyPressed) {
                    queue.add(() -> Networking.sendKeyPressedPacket(NamedKey.RELOAD));
                    reloadKeyPressed = true;
                }
            } else {
                if (reloadKeyPressed) {
                    queue.add(() -> Networking.sendKeyReleasedPacket(NamedKey.RELOAD));
                    reloadKeyPressed = false;
                }
            }
            if (client.options.attackKey.isPressed()) {
                if (!attackKeyPressed) {
                    queue.add(() -> Networking.sendKeyPressedPacket(NamedKey.ATTACK));
                    attackKeyPressed = true;
                }
            } else {
                if (attackKeyPressed) {
                    queue.add(() -> Networking.sendKeyReleasedPacket(NamedKey.ATTACK));
                    attackKeyPressed = false;
                }
            }
            if (client.options.useKey.isPressed()) {
                if (!useKeyPressed) {
                    queue.add(() -> Networking.sendKeyPressedPacket(NamedKey.USE));
                    useKeyPressed = true;
                }
            } else {
                if (useKeyPressed) {
                    queue.add(() -> Networking.sendKeyReleasedPacket(NamedKey.USE));
                    useKeyPressed = false;
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> queue.removeIf(runnable -> {
            runnable.run();
            return true;
        }));
    }
}
