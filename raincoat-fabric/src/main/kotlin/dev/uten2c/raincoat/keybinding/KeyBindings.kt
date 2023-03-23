package dev.uten2c.raincoat.keybinding

import dev.uten2c.raincoat.NamedKey
import dev.uten2c.raincoat.network.Networking.sendKeyPressedPacket
import dev.uten2c.raincoat.network.Networking.sendKeyReleasedPacket
import dev.uten2c.raincoat.option.OptionManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW
import java.util.concurrent.ConcurrentHashMap

object KeyBindings {
    private const val RAINCOAT_KEYBINDING_CATEGORY = "key.categories.raincoat"
    private const val RELOAD_KEYBINDING_KEY = "key.raincoat.reload"
    private val pressedKeys = ConcurrentHashMap.newKeySet<NamedKey>()
    private val queue: MutableList<() -> Unit> = ArrayList()

    fun register() {
        val reloadKeyBinding = KeyBindingHelper.registerKeyBinding(KeyBinding(RELOAD_KEYBINDING_KEY, GLFW.GLFW_KEY_R, RAINCOAT_KEYBINDING_CATEGORY))
        ClientTickEvents.START_CLIENT_TICK.register { client ->
            listen(NamedKey.RELOAD, reloadKeyBinding.isPressed)

            val leftClickKey = if (!OptionManager.options.isInvertAttackKey) NamedKey.ATTACK else NamedKey.USE
            listen(leftClickKey, client.options.attackKey.isPressed)

            val rightClickKey = if (!OptionManager.options.isInvertAttackKey) NamedKey.USE else NamedKey.ATTACK
            listen(rightClickKey, client.options.useKey.isPressed)
        }
        ClientTickEvents.END_CLIENT_TICK.register {
            queue.removeIf {
                it()
                true
            }
        }
    }

    private fun listen(key: NamedKey, isPressed: Boolean) {
        if (isPressed) {
            if (key !in pressedKeys) {
                queue.add { sendKeyPressedPacket(key) }
                pressedKeys.add(key)
            }
        } else {
            if (key in pressedKeys) {
                queue.add { sendKeyReleasedPacket(key) }
                pressedKeys.remove(key)
            }
        }
    }
}
