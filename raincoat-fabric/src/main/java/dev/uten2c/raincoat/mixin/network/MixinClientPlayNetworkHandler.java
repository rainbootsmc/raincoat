package dev.uten2c.raincoat.mixin.network;

import dev.uten2c.raincoat.network.PingListener;
import dev.uten2c.raincoat.sign.SignListener;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayPingS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ClientWorld world;

    @Inject(method = "onPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"), cancellable = true)
    private void onPing(PlayPingS2CPacket packet, CallbackInfo ci) {
        if (!PingListener.onPing(packet.getParameter())) {
            ci.cancel();
        }
    }

    @Inject(method = "onBlockEntityUpdate", at = @At("TAIL"))
    private void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
        final var pos = packet.getPos();
        assert client.world != null;
        client.world.getBlockEntity(pos, packet.getBlockEntityType()).ifPresent(blockEntity -> {
            if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                SignListener.INSTANCE.onUpdate(signBlockEntity, world);
            }
        });
    }
}
