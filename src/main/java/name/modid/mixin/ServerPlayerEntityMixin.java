package name.modid.mixin;

import name.modid.CombatTag;
import name.modid.Config;
import name.modid.access.ServerPlayerEntityAccess;
import name.modid.events.PlayerDamageCallback;
import name.modid.events.PlayerDeathCallback;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
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
        return combat;
    }

    @Unique
    public void combat_tag$setCombat(boolean combat) {
        this.combat = combat;
        if (combat) {
            ticksSinceCombat = 0;
        }
    }

    @Unique
    public float combat_tag$getProgress() {
        return ((float) Config.COMBAT_DURATION - ticksSinceCombat) / Config.COMBAT_DURATION;
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        MinecraftServer server = player.getServer();
        if (combat && server != null && !server.isStopping() && !server.isSaving() && !server.isPaused()) {
            CombatTag.logoutPunish(player);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        if (combat) {
            ticksSinceCombat++;
            if (ticksSinceCombat >= Config.COMBAT_DURATION) {
                combat = false;
                ticksSinceCombat = 0;
            }
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerDamageCallback.EVENT.invoker().onPlayerDamaged(player, source);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerDeathCallback.EVENT.invoker().onPlayerDeath(player, source);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(COMBAT_TAG_KEY, combat);
        nbt.putInt(COMBAT_TAG_TICKS_KEY, ticksSinceCombat);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(COMBAT_TAG_KEY)) {
            combat = nbt.getBoolean(COMBAT_TAG_KEY);
        }
        else {
            combat = false;
        }

        if (nbt.contains(COMBAT_TAG_TICKS_KEY)) {
            ticksSinceCombat = nbt.getInt(COMBAT_TAG_TICKS_KEY);
        } else {
            ticksSinceCombat = 0;
        }
    }
}