package name.modid.mixin;

import name.modid.CombatBar;
import name.modid.access.ServerPlayerEntityAccess;
import name.modid.events.PlayerAttackCallback;
import name.modid.events.PlayerDamageCallback;

import name.modid.events.PlayerDeathCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
    private static final String ENDER_PEARLS_KEY = "PearlCooldown";
    @Unique
    private static final String COMBAT_TAG_TICKS_KEY = "CombatTagTicks";
    
    @Unique
    private boolean combat = false;
    @Unique
    private int ticksSinceCombat = 0;
    
    @Unique
    private static final int DEFAULT_PEARL_COOLDOWN = 20;
    @Unique
    private int pearlCooldown = DEFAULT_PEARL_COOLDOWN;

    @Unique
    private CombatBar combatBar;

    @Unique
    private static final int POISON_DURATION = 20 * 20;
    
    @Unique
    public int combat_tag$getPearlCooldown() {
        return pearlCooldown;
    }
    
    @Unique
    public void combat_tag$setPearlCooldown(int duration) {
        pearlCooldown = duration;
    }

    @Unique
    public boolean combat_tag$inCombat() {
        return combat;
    }

    @Unique
    public void combat_tag$setCombat(boolean combat) {
        this.combat = combat;
        if (combat) {
            ticksSinceCombat = 0;
            if (combatBar != null) {
                combatBar.remove();
            }
            combatBar = new CombatBar((ServerPlayerEntity) (Object) this);
            combatBar.update(1.0F);
        } else {
            if (combatBar != null) {
                combatBar.remove();
            }
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci) {
        if (combat) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            player.setHealth(1.0F);
            player.setAbsorptionAmount(0.0F);
            StatusEffectInstance poison = new StatusEffectInstance(StatusEffects.POISON, POISON_DURATION);
            player.setStatusEffect(poison, null);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        final int COMBAT_TICK_RESET = 20 * 60;

        if (combat) {
            ticksSinceCombat++;
            if (ticksSinceCombat >= COMBAT_TICK_RESET) {
                combat = false;
                ticksSinceCombat = 0;
                pearlCooldown = DEFAULT_PEARL_COOLDOWN;
                combatBar.remove();
            } else {
                if (combatBar == null) {
                    combatBar = new CombatBar((ServerPlayerEntity) (Object) this);
                }
                combatBar.update(((float) COMBAT_TICK_RESET - ticksSinceCombat) / COMBAT_TICK_RESET);
            }
        }
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void onAttack(Entity target, CallbackInfo ci) {
        if (ci != null) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            PlayerAttackCallback.EVENT.invoker().onPlayerAttack(player);
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            PlayerDamageCallback.EVENT.invoker().onPlayerDamaged(player, source);

            if (player.getHealth() - amount <= 0.0F) {
                this.combat_tag$setCombat(false);
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if (ci != null) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            this.combat_tag$setCombat(false);
            PlayerDeathCallback.EVENT.invoker().onPlayerDeath(player);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(COMBAT_TAG_KEY, combat);
        nbt.putInt(COMBAT_TAG_TICKS_KEY, ticksSinceCombat);
        nbt.putInt(ENDER_PEARLS_KEY, pearlCooldown);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(COMBAT_TAG_KEY)) {
            combat = nbt.getBoolean(COMBAT_TAG_KEY);
        }
        else {
            combat = false;
            ticksSinceCombat = 0;
        }
        
        if (nbt.contains(ENDER_PEARLS_KEY)) {
            pearlCooldown = nbt.getInt(ENDER_PEARLS_KEY);
        }

        if (nbt.contains(COMBAT_TAG_TICKS_KEY)) {
            ticksSinceCombat = nbt.getInt(COMBAT_TAG_TICKS_KEY);
        } else {
            ticksSinceCombat = 0;
        }
    }
}