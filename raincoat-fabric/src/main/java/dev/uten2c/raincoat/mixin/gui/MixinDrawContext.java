package dev.uten2c.raincoat.mixin.gui;

import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class MixinDrawContext {
    @ModifyVariable(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private ItemStack swapGuiStackInDrawItemInSlot(ItemStack value) {
        return StackUtils.swapGuiStack(value);
    }

    @ModifyVariable(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private ItemStack swapGuiStackInDrawItem(ItemStack value) {
        return StackUtils.swapGuiStack(value);
    }
}
