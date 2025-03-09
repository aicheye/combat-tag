package name.modid;

import name.modid.access.ServerPlayerEntityAccess;
import name.modid.events.PlayerDamageCallback;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatTag implements ModInitializer {
	public static final String MOD_ID = "combat-tag";

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

		PlayerDamageCallback.EVENT.register(CombatTag::onPlayerDamaged);
		ServerTickEvents.END_SERVER_TICK.register(CombatTag::tick);
	}

	private static void onPlayerDamaged(ServerPlayerEntity player, DamageSource source) {
		if (!source.getType().msgId().equals("fall")) {
			LOGGER.info("{} damaged by {}", player.getName().getString(), source.getType());
			ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
			combatAccessor.combat_tag$setCombat(true);
		}
	}

	private static void tick(MinecraftServer server) {
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;
			if (combatAccessor.combat_tag$inCombat()) {
				LOGGER.info("{} in combat for {} ticks", player.getName().getString(), combatAccessor.combat_tag$getTicksSinceCombat());
			}
			else if (!combatAccessor.combat_tag$inCombat()) {
				LOGGER.info("{} not in combat", player.getName().getString());
			}
		}
	}
}