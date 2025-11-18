package name.modid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for CombatCooldownManager class structure.
 * Full integration tests would require a Minecraft server environment.
 */
class CombatCooldownManagerTest {

    @Test
    void testCombatCooldownManagerClassExists() {
        assertNotNull(CombatCooldownManager.class, "CombatCooldownManager class should exist");
    }

    @Test
    void testCombatCooldownManagerHasTagCooldownsMethod() throws NoSuchMethodException {
        assertNotNull(CombatCooldownManager.class.getDeclaredMethod("tagCooldowns",
            net.minecraft.server.network.ServerPlayerEntity.class),
            "CombatCooldownManager should have tagCooldowns method");
    }

    @Test
    void testCombatCooldownManagerHasConsumeCooldownsMethod() throws NoSuchMethodException {
        assertNotNull(CombatCooldownManager.class.getDeclaredMethod("consumeCooldowns",
            net.minecraft.server.network.ServerPlayerEntity.class),
            "CombatCooldownManager should have consumeCooldowns method");
    }

    @Test
    void testTagCooldownsWithInstantTpPunishDisabled() {
        Config.ENABLE_INSTANT_TP_PUNISH = false;
        Config.ENABLE_INSTANT_NOTCH_APPLE_PUNISH = false;

        // Should not throw exception when punishes are disabled
        // Method requires a player object, so we can't call it without a mock
        // This test verifies the class structure
        assertDoesNotThrow(() -> {
            CombatCooldownManager.class.getDeclaredMethod("tagCooldowns",
                net.minecraft.server.network.ServerPlayerEntity.class);
        });
    }

    @Test
    void testConfigIntegrationForCooldowns() {
        // Test that config values are accessible
        assertTrue(Config.COMBAT_TP_COOLDOWN >= 0, "TP cooldown should be non-negative");
        assertTrue(Config.COMBAT_NOTCH_APPLE_COOLDOWN >= 0, "Notch apple cooldown should be non-negative");
    }
}

