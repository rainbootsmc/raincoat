package dev.uten2c.raincoat.mixin.render;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @ModifyVariable(method = "innerRenderInGui(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private ItemStack renderItem(ItemStack value) {
        if (!States.isOnServer()) {
            return value;
        }
        if (value.isEmpty()) {
            return value;
        }
        var guiStack = StackUtils.getGuiStack(value);
        if (guiStack.isEmpty()) {
            return value;
        }
        return guiStack;
    }
}
