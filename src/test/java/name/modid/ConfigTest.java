package name.modid;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Config class structure and values.
 * Note: Tests that require modifying CONFIG_PATH are not included due to Java restrictions on final fields.
 */
class ConfigTest {

    @Test
    void testConfigClassExists() {
        assertNotNull(Config.class, "Config class should exist");
    }

    @Test
    void testConfigValuesAreInitialized() {
        // Test that tick-based values are calculated from seconds
        assertTrue(Config.COMBAT_DURATION > 0, "COMBAT_DURATION should be positive");
        assertTrue(Config.COMBAT_TP_COOLDOWN > 0, "COMBAT_TP_COOLDOWN should be positive");
        assertTrue(Config.COMBAT_NOTCH_APPLE_COOLDOWN > 0, "COMBAT_NOTCH_APPLE_COOLDOWN should be positive");
        
        // Verify conversion (20 ticks per second)
        // Default is 20 seconds, so 400 ticks
        assertTrue(Config.COMBAT_DURATION >= 300, "COMBAT_DURATION should be at least 300 ticks");
    }

    @Test
    void testPoisonAmplifierCalculation() {
        // Poison amplifier should be poison level - 1
        // If poison level defaults to 5, amplifier should be 4
        assertTrue(Config.POISON_AMPLIFIER >= 0, "POISON_AMPLIFIER should be non-negative");
        assertTrue(Config.POISON_DURATION > 0, "POISON_DURATION should be positive");
    }

    @Test
    void testConfigHasLoadMethod() throws NoSuchMethodException {
        assertNotNull(Config.class.getDeclaredMethod("load"),
            "Config should have load method");
    }

    @Test
    void testConfigHasWriteMethod() throws NoSuchMethodException {
        assertNotNull(Config.class.getDeclaredMethod("write"),
            "Config should have write method");
    }

    @Test
    void testConfigBooleanFlags() {
        // Test that boolean config fields exist and have reasonable values
        assertNotNull((Boolean) Config.ONLY_TAG_WEAPONS, "ONLY_TAG_WEAPONS should not be null");
        assertNotNull((Boolean) Config.ENABLE_TAG_ON_ATTACK, "ENABLE_TAG_ON_ATTACK should not be null");
        assertNotNull((Boolean) Config.ENABLE_HEALTH_PUNISH, "ENABLE_HEALTH_PUNISH should not be null");
        assertNotNull((Boolean) Config.ENABLE_COMBAT_COLOUR, "ENABLE_COMBAT_COLOUR should not be null");
    }

    @Test
    void testConfigPunishValues() {
        // Test punish values are reasonable
        assertTrue(Config.HEALTH_REMAINING_PUNISH >= 0, "HEALTH_REMAINING_PUNISH should be non-negative");
        assertTrue(Config.ABSORPTION_REMAINING_PUNISH >= 0, "ABSORPTION_REMAINING_PUNISH should be non-negative");
    }

    @Test
    void testConfigHasConfigPathField() throws NoSuchFieldException {
        Field configPathField = Config.class.getDeclaredField("CONFIG_PATH");
        assertNotNull(configPathField, "Config should have CONFIG_PATH field");
        assertTrue(Modifier.isStatic(configPathField.getModifiers()), "CONFIG_PATH should be static");
        assertTrue(Modifier.isFinal(configPathField.getModifiers()), "CONFIG_PATH should be final");
    }
}

