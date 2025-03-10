package name.modid.mixin;

import name.modid.CombatTag;

import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UseCooldownComponent.class)
public class UseCooldownComponentMixin {

    @Inject(method = "set", at = @At("RETURN"))
    public void set(ItemStack stack, LivingEntity user, CallbackInfo ci) {
        if (user instanceof PlayerEntity playerEntity) {
            if (stack.getItem().equals(Items.ENDER_PEARL) || stack.getItem().equals(Items.CHORUS_FRUIT)) {
                playerEntity.getItemCooldownManager().set(stack, CombatTag.getPearlCooldown((ServerPlayerEntity) user));
            }
        }
    }
}