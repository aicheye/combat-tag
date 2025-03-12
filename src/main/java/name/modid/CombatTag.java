package name.modid;

import name.modid.access.ServerPlayerEntityAccess;
import name.modid.events.PlayerDamageCallback;
import name.modid.events.PlayerDeathCallback;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CombatTag implements ModInitializer {
	public static final String MOD_ID = "combat-tag";

	private static final String ENDER_PEARL = "minecraft:ender_pearl";
	private static final String CHORUS_FRUIT = "minecraft:chorus_fruit";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("[{}] loading config...", MOD_ID);

		try {
			Config.load();
			LOGGER.info("[{}] config loaded", MOD_ID);
		} catch (IOException e1) {
            LOGGER.info("[{}] generating new config file...", MOD_ID);
			try {
				Config.write();
			} catch (IOException e2) {
				LOGGER.error("[{}] could not write new config file... {}", MOD_ID, e2.getMessage());
			}
        }

        PlayerDeathCallback.EVENT.register(CombatTag::onPlayerDeath);
		PlayerDamageCallback.EVENT.register(CombatTag::onPlayerDamage);

		if (Config.ENABLE_COMBAT_COLOUR) {
			ServerTickEvents.END_SERVER_TICK.register(ScoreboardManager::tickScoreboard);
		}
		ServerTickEvents.END_SERVER_TICK.register(CombatBarManager::tickCombatBars);

		LOGGER.info("[{}] listening on event channels", MOD_ID);
	}

	private static void setPearlCooldown(ServerPlayerEntity player) {
		ItemCooldownManager ItemCDM = player.getItemCooldownManager();
		ItemCDM.set(Identifier.of(ENDER_PEARL), Config.COMBAT_TP_COOLDOWN);
		ItemCDM.set(Identifier.of(CHORUS_FRUIT), Config.COMBAT_TP_COOLDOWN);
	}

	private static void combatTag(ServerPlayerEntity player) {
		ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
		String log = combatAccessor.combat_tag$setCombat(true);

		if (Config.ENABLE_INSTANT_TP_PUNISH) {
			setPearlCooldown(player);
		}

		if (log != null) {
			LOGGER.info(log);
		}
	}

	private static void removeCombatTag(ServerPlayerEntity player) {
		ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
		combatAccessor.combat_tag$setCombat(false);
	}

	private static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		removeCombatTag(player);
	}

	private static void onPlayerDamage(ServerPlayerEntity player, DamageSource source) {
		ServerPlayerEntity attacker = player;

		if (source.getAttacker() instanceof ServerPlayerEntity) {
            attacker = (ServerPlayerEntity) source.getAttacker();
		} else if (source.getAttacker() instanceof ProjectileEntity projectile) {
			if (projectile.getOwner() instanceof ServerPlayerEntity) {
				attacker = (ServerPlayerEntity) projectile.getOwner();
 			}
		}

		if (!attacker.equals(player)) {
			combatTag(player);
			combatTag(attacker);
		}
	}

	public static void logoutPunish(ServerPlayerEntity player) {
		if (Config.ENABLE_HEALTH_PUNISH) {
			player.setHealth(Config.HEALTH_REMAINING_PUNISH);
		}
		if (Config.ENABLE_ABSORPTION_PUNISH) {
			player.setAbsorptionAmount(Config.ABSORPTION_REMAINING_PUNISH);
		}
		if (Config.ENABLE_POISON_PUNISH) {
			StatusEffectInstance poison = new StatusEffectInstance(
					StatusEffects.POISON,
					Config.POISON_DURATION,
					Config.POISON_AMPLIFIER
			);
			player.addStatusEffect(poison);
		}
	}
}