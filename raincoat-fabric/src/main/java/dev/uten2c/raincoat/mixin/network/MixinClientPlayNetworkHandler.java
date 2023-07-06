package dev.uten2c.raincoat.mixin.network;

import dev.uten2c.raincoat.network.PingListener;
import dev.uten2c.raincoat.recipebook.RecipeManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayPingS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(method = "onPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"), cancellable = true)
    private void onPing(PlayPingS2CPacket packet, CallbackInfo ci) {
        if (!PingListener.onPing(packet.getParameter())) {
            ci.cancel();
        }
    }

    @Inject(method = "onSynchronizeRecipes", at = @At("TAIL"))
    private void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
        RecipeManager.declareRecipes(packet.getRecipes());
    }
}
