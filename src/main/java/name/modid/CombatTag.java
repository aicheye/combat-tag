package name.modid;

import name.modid.access.ServerPlayerEntityAccess;
import name.modid.events.PlayerAttackCallback;
import name.modid.events.PlayerDamageCallback;
import name.modid.events.PlayerDeathCallback;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class CombatTag implements ModInitializer {
	public static final String MOD_ID = "combat-tag";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Set<Item> weapons = Set.of(
			Items.NETHERITE_SWORD,
			Items.DIAMOND_SWORD,
			Items.IRON_SWORD,
			Items.GOLDEN_SWORD,
			Items.STONE_SWORD,
			Items.WOODEN_SWORD,
			Items.NETHERITE_AXE,
			Items.DIAMOND_AXE,
			Items.IRON_AXE,
			Items.GOLDEN_AXE,
			Items.STONE_AXE,
			Items.WOODEN_AXE,
			Items.TRIDENT,
			Items.MACE,
			Items.BOW,
			Items.CROSSBOW
	);

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
			LOGGER.warn("[{}] could not load config", MOD_ID);
			try {
				LOGGER.info("[{}] generating new config file...", MOD_ID);
				Config.write();
				LOGGER.info("[{}] config generated successfully", MOD_ID);
			} catch (IOException e2) {
				LOGGER.error("[{}] could not write new config file {}", MOD_ID, e2.getMessage());
			}
        }

        PlayerDeathCallback.EVENT.register(CombatTag::onPlayerDeath);
		PlayerDamageCallback.EVENT.register(CombatTag::onPlayerDamage);
		PlayerAttackCallback.EVENT.register(CombatTag::onPlayerAttack);

		if (Config.ENABLE_COMBAT_COLOUR) {
			ServerTickEvents.END_SERVER_TICK.register(ScoreboardManager::tickScoreboard);
		}
		ServerTickEvents.END_SERVER_TICK.register(CombatBarManager::tickCombatBars);

		LOGGER.info("[{}] listening on event channels", MOD_ID);
	}

	private static void combatTag(ServerPlayerEntity player) {
		if (!player.isPartOfGame()) {
			LOGGER.warn("[{}] combat tag called on player {} who is not part of the game", MOD_ID, player.getName().getString());
			return;
		}

		if (player.isCreative()) {
			LOGGER.warn("[{}] combat tag called on player {} who is in creative mode", MOD_ID, player.getName().getString());
			return;
		}

		ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;

		if (!combatAccessor.combat_tag$inCombat()) {
			LOGGER.info("[{}] combat tagged {}", MOD_ID, player.getName().getString());
		}

		combatAccessor.combat_tag$setCombat(true);

		if (Config.ENABLE_INSTANT_TP_PUNISH) {
			CombatCooldownManager.tagCooldowns(player);
		}

		if (Config.ENABLE_ELYTRA_PUNISH) {
			player.stopGliding();
		}
	}

	public static void removeCombatTag(ServerPlayerEntity player) {
		ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;

		if (combatAccessor.combat_tag$inCombat()) {
			LOGGER.info("[{}] removed combat tag from {}", MOD_ID, player.getName().getString());
		}

		combatAccessor.combat_tag$setCombat(false);
	}

	private static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		removeCombatTag(player);
	}

	private static void onPlayerDamage(ServerPlayerEntity player, DamageSource source) {
		ServerPlayerEntity attacker = null;

		if (source.getAttacker() instanceof ServerPlayerEntity serverPlayerEntity) {
            attacker = serverPlayerEntity;
		} else if (source.getAttacker() instanceof ProjectileEntity projectile &&
				projectile.getOwner() instanceof ServerPlayerEntity serverPlayerEntity) {
			attacker = serverPlayerEntity;
		}

		if (attacker != null && !attacker.equals(player)) {
			if (Config.ONLY_TAG_WEAPONS) {
				try {
					LOGGER.info("[{}] player damaged by weapon {}", MOD_ID, Objects.requireNonNull(source.getWeaponStack()).getItem().toString());
					LOGGER.info("[{}] player damaged by damage type {}", MOD_ID, source.getType().toString());
					if (!(weapons.contains(Objects.requireNonNull(source.getWeaponStack()).getItem()))) {
						// check if damage type is explosion
						Optional<RegistryKey<DamageType>> damageTypeKey = source.getTypeRegistryEntry().getKey();
						if (damageTypeKey.isPresent() && !(damageTypeKey.get().equals(DamageTypes.PLAYER_EXPLOSION))) {
							// exit early
							return;
						}
					}
				} catch (NullPointerException ignored) {
					// check if damage type is explosion
					Optional<RegistryKey<DamageType>> damageTypeKey = source.getTypeRegistryEntry().getKey();
					if (damageTypeKey.isPresent() && !(damageTypeKey.get().equals(DamageTypes.PLAYER_EXPLOSION))) {
						// exit early
						return;
					}
				}
			}

			combatTag(player);
			if (Config.ENABLE_TAG_ON_ATTACK) {
				combatTag(attacker);
			}
			return;
		}

		// guard clause
		if (!Config.ENABLE_PVE_TAG_ON_DAMAGE) {
			return;
		}

		if (source.getAttacker() instanceof LivingEntity) {
			combatTag(player);
		} else if (source.getAttacker() instanceof ProjectileEntity projectile &&
				projectile.getOwner() instanceof LivingEntity) {
			combatTag(player);
		}
	}

	private static void onPlayerAttack(ServerPlayerEntity player, Entity target) {
		if (Config.ENABLE_PVE_TAG_ON_ATTACK && target instanceof LivingEntity && !player.equals(target)) {
			combatTag(player);
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
