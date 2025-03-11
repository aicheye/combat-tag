package name.modid;

import name.modid.access.ServerPlayerEntityAccess;
import name.modid.events.PlayerAttackCallback;
import name.modid.events.PlayerDeathCallback;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CombatTag implements ModInitializer {
	public static final String MOD_ID = "combat-tag";
	private static final String COMBAT_TAG = "combat";

	private static Scoreboard scoreboard;
	private static Team combatTeam;

	private static final String ENDER_PEARL = "minecraft:ender_pearl";
	private static final String CHORUS_FRUIT = "minecraft:chorus_fruit";
	private static final int DEFAULT_TP_COOLDOWN = 20;
	private static final int COMBAT_TP_COOLDOWN = 20 * 20;

	private static final int POISON_DURATION = 20 * 20;
	private static final int POISON_AMPLIFIER = 5;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("{} successfully loaded", MOD_ID);

		PlayerDeathCallback.EVENT.register(CombatTag::onPlayerDeath);
		PlayerAttackCallback.EVENT.register(CombatTag::onPlayerAttack);
		ServerTickEvents.END_SERVER_TICK.register(CombatTag::tickScoreboard);

		LOGGER.info("{} listening on event channels", MOD_ID);
	}

	private static void setPearlCooldown(ServerPlayerEntity player, int duration, boolean startNow) {
		ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
		combatAccessor.combat_tag$setPearlCooldown(duration);

		ItemCooldownManager ItemCDM = player.getItemCooldownManager();

		if (startNow) {
			ItemCDM.set(Identifier.of(ENDER_PEARL), duration);
			ItemCDM.set(Identifier.of(CHORUS_FRUIT), duration);
		}
	}

	public static int getPearlCooldown(ServerPlayerEntity player) {
		ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
		return combatAccessor.combat_tag$getPearlCooldown();
	}

	private static void combatTag(ServerPlayerEntity player) {
		ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
		combatAccessor.combat_tag$setCombat(true);
		setPearlCooldown(player, COMBAT_TP_COOLDOWN, true);
	}

	private static void removeCombatTag(ServerPlayerEntity player) {
		ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
		combatAccessor.combat_tag$setCombat(false);
		setPearlCooldown(player, DEFAULT_TP_COOLDOWN, false);
	}

	private static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		removeCombatTag(player);
		cleanBossBars(Objects.requireNonNull(player.getServer()));

		if (source.getAttacker() instanceof ServerPlayerEntity attacker) {
			combatTag(attacker);
			if (combatTeam != null && scoreboard != null) {
				if (!combatTeam.getPlayerList().contains(attacker.getNameForScoreboard())) {
					combatTeam.getPlayerList().add(attacker.getNameForScoreboard());
				}
				scoreboard.updateScoreboardTeamAndPlayers(combatTeam);
			}
		}
	}

	private static void cleanBossBars(MinecraftServer server) {
		BossBarManager bossBarManager = server.getBossBarManager();

        for (ServerBossBar bar : bossBarManager.getAll()) {
            if (bar instanceof CombatBar) {
                if (bar.getPlayers().isEmpty()) {
                    bar.clearPlayers();
                    bar.setVisible(false);
                }
            }
        }
    }

	private static void onPlayerAttack(ServerPlayerEntity player, Entity target) {
		if (target instanceof PlayerEntity) {
			combatTag(player);
			combatTag((ServerPlayerEntity) target);
		}
	}

	public static void logoutPunish(ServerPlayerEntity player) {
		player.setHealth(1.0F);
		player.setAbsorptionAmount(0.0F);
		StatusEffectInstance poison = new StatusEffectInstance(StatusEffects.POISON, POISON_DURATION, POISON_AMPLIFIER);
		player.addStatusEffect(poison);
	}

	private static void tickScoreboard(MinecraftServer server) {
		if (scoreboard == null) {
			scoreboard = server.getScoreboard();
		}

		if (combatTeam == null) {
			combatTeam = scoreboard.getTeam(COMBAT_TAG);
			if (combatTeam == null) {
				combatTeam = scoreboard.addTeam(COMBAT_TAG);
			}
			combatTeam.setColor(Formatting.RED);
		}

		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;

			if (combatAccessor.combat_tag$inCombat() && !combatTeam.getPlayerList().contains(player.getNameForScoreboard())) {
				LOGGER.info("Added combat tag to {}", player.getName().getString());
				combatTeam.getPlayerList().add(player.getNameForScoreboard());
				scoreboard.updateScoreboardTeamAndPlayers(combatTeam);
			}

			else if (!combatAccessor.combat_tag$inCombat() && combatTeam.getPlayerList().contains(player.getNameForScoreboard())) {
				LOGGER.info("Removed combat tag from {}", player.getName().getString());
				combatTeam.getPlayerList().remove(player.getNameForScoreboard());
				scoreboard.updateRemovedTeam(combatTeam);
			}
		}
	}
}