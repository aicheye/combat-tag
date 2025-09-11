package name.modid.mixin;

import name.modid.Config;
import name.modid.access.ServerPlayerEntityAccess;

import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UseCooldownComponent.class)
public class UseCooldownComponentMixin {

    @Unique
    private boolean inCombat(ServerPlayerEntity player) {
        ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
        return combatAccessor.combat_tag$inCombat();
    }

    @Inject(method = "set", at = @At("RETURN"))
    public void set(ItemStack stack, LivingEntity user, CallbackInfo ci) {
        if (Config.ENABLE_TP_PUNISH) {
            if (user instanceof ServerPlayerEntity player && inCombat(player)) {
                if (stack.getItem().equals(Items.ENDER_PEARL) || stack.getItem().equals(Items.CHORUS_FRUIT)) {
                    player.getItemCooldownManager().set(stack, Config.COMBAT_TP_COOLDOWN);
                }
            }
        }
    }
}