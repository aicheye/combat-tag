package name.modid.mixin;

import name.modid.access.ServerPlayerEntityAccess;
import name.modid.events.PlayerDamageCallback;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityAccess {
    @Unique
    private static final String COMBAT_TAG_KEY = "CombatTag";
    @Unique
    private static final String COMBAT_TAG_TICKS_KEY = "CombatTagTicks";
    @Unique
    private boolean combat = false;
    @Unique
    private int ticksSinceCombat = 0;

    @Unique
    public boolean combat_tag$inCombat() {
        return this.combat;
    }

    @Unique
    public void combat_tag$setCombat(boolean combat) {
        this.combat = combat;
        if (combat) {
            this.ticksSinceCombat = 0;
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        final int COMBAT_TICK_RESET = 20 * 60;

        if (this.combat) {
            this.ticksSinceCombat++;
            if (this.ticksSinceCombat >= COMBAT_TICK_RESET) {
                this.combat = false;
                this.ticksSinceCombat = 0;
            }
        }
    }

    @Unique
    public int combat_tag$getTicksSinceCombat() {
        return ticksSinceCombat;
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            PlayerDamageCallback.EVENT.invoker().onPlayerDamaged(player, source);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(COMBAT_TAG_KEY, this.combat);
        nbt.putInt(COMBAT_TAG_TICKS_KEY, this.ticksSinceCombat);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(COMBAT_TAG_KEY)) {
            this.combat = nbt.getBoolean(COMBAT_TAG_KEY);
        }
        else {
            this.combat = false;
            this.ticksSinceCombat = 0;
        }

        if (nbt.contains(COMBAT_TAG_TICKS_KEY)) {
            this.ticksSinceCombat = nbt.getInt(COMBAT_TAG_TICKS_KEY);
        } else {
            this.ticksSinceCombat = 0;
        }
    }
}