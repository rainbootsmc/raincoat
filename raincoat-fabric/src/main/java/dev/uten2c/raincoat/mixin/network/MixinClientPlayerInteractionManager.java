package dev.uten2c.raincoat.mixin.network;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.util.StackUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Inject(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    private void cancelClick(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!States.isOnServer()) {
            return;
        }
        if (slotId < 0) {
            return;
        }
        var screenHandler = player.currentScreenHandler;
        var stack = screenHandler.getSlot(slotId).getStack();
        if (!StackUtils.canNotClick(stack)) {
            return;
        }
        final var packet = new ClickSlotC2SPacket(syncId, screenHandler.getRevision(), slotId, button, actionType, screenHandler.getCursorStack().copy(), new Int2ObjectOpenHashMap<>());
        networkHandler.sendPacket(packet);
        ci.cancel();
    }
}
