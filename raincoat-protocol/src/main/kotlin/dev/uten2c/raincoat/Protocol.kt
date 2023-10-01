package dev.uten2c.raincoat

import dev.uten2c.raincoat.util.PacketId

object Protocol {
    const val PROTOCOL_VERSION = 6

    // C2S
    val handshakeResponse = id("handshake/response")
    val keyPressed = id("key/pressed")
    val keyReleased = id("key/released")
    val directionUpdate = id("direction/update")
    val settingsUpdate = id("settings/update")

    // S2C
    val handshakeRequest = id("handshake/request")
    val recoilCamera = id("recoil/camera")
    val recoilAnimation = id("recoil/animation")
    val directionSendRequest = id("direction/request")
    val outdated = id("outdated")
    val openUrl = id("open_url")
    val shapeDisplay = id("shape/display")
    val shapeDiscard = id("shape/discard")
    val shapeClear = id("shape/clear")

    private fun id(id: String): PacketId {
        return PacketId(id)
    }
}
