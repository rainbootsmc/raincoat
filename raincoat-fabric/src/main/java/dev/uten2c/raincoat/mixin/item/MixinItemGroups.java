package dev.uten2c.raincoat.mixin.item;

import dev.uten2c.raincoat.VariablesKt;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemGroups.class)
public class MixinItemGroups {
    @Redirect(method = "updateDisplayContext", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup$DisplayContext;doesNotMatch(Lnet/minecraft/resource/featuretoggle/FeatureSet;ZLnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Z"))
    private static boolean updateFieldObjects(ItemGroup.DisplayContext instance, FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup lookup) {
        final var result = instance.doesNotMatch(enabledFeatures, hasPermissions, lookup);
        if (VariablesKt.getShouldUpdateCreativeTab()) {
            VariablesKt.setShouldUpdateCreativeTab(false);
            return true;
        }
        return result;
    }
}
