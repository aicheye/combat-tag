package name.modid.mixin;

import name.modid.Config;
import name.modid.access.ServerPlayerEntityAccess;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "canGlide", at = @At("RETURN"), cancellable = true)
    protected void canGlide(CallbackInfoReturnable<Boolean> cir) {
        if (Config.ENABLE_ELYTRA_PUNISH) {
            if (((ServerPlayerEntityAccess) this).combat_tag$inCombat()) {
                cir.setReturnValue(false);
            }
        }
    }
}
