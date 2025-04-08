package name.modid;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class CombatCooldownManager {
    private static final String ENDER_PEARL = "minecraft:ender_pearl";
    private static final String CHORUS_FRUIT = "minecraft:chorus_fruit";
    private static final String NOTCH_APPLE = "minecraft:enchanted_golden_apple";

    private static void punishEnderPearlCooldown(ServerPlayerEntity player) {
        ItemCooldownManager ItemCDM = player.getItemCooldownManager();
        ItemCDM.set(Identifier.of(ENDER_PEARL), Config.COMBAT_TP_COOLDOWN);
    }

    private static void punishChorusFruitCooldown(ServerPlayerEntity player) {
        ItemCooldownManager ItemCDM = player.getItemCooldownManager();
        ItemCDM.set(Identifier.of(CHORUS_FRUIT), Config.COMBAT_TP_COOLDOWN);
    }

    private static void punishTpCooldown(ServerPlayerEntity player) {
        punishEnderPearlCooldown(player);
        punishChorusFruitCooldown(player);
    }

    private static void punishNotchAppleCooldown(ServerPlayerEntity player) {
        ItemCooldownManager ItemCDM = player.getItemCooldownManager();
        ItemCDM.set(Identifier.of(NOTCH_APPLE), Config.COMBAT_NOTCH_APPLE_COOLDOWN);
    }

    public static void tagCooldowns(ServerPlayerEntity player) {
        if (Config.ENABLE_INSTANT_TP_PUNISH) {
            punishTpCooldown(player);
        }

        if (Config.ENABLE_INSTANT_NOTCH_APPLE_PUNISH) {
            punishNotchAppleCooldown(player);
        }
    }

    private static boolean activeItemMatches(ServerPlayerEntity player, Item item) {
        return (player.getActiveHand().equals(Hand.MAIN_HAND) &&
                player.getMainHandStack().getItem().equals(item)) ||
                (player.getActiveHand().equals(Hand.OFF_HAND) &&
                        player.getOffHandStack().getItem().equals(item));
    }

    public static void consumeCooldowns(ServerPlayerEntity player) {
        if (Config.ENABLE_NOTCH_APPLE_PUNISH) {
            if (activeItemMatches(player, Items.ENCHANTED_GOLDEN_APPLE)) {
                punishNotchAppleCooldown(player);
            }
        }

        if (Config.ENABLE_TP_PUNISH) {
            if (activeItemMatches(player, Items.CHORUS_FRUIT)) {
                punishChorusFruitCooldown(player);
            }
        }
    }
}
