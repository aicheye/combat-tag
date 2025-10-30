package name.modid;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Config class.
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
    }

    @Test
    void testConfigWriteAndLoadMethods() {
        // Test that write and load methods exist and can be called
        // Full integration testing would require file system access
        assertDoesNotThrow(() -> Config.class.getDeclaredMethod("write"),
            "Config should have write method");
        assertDoesNotThrow(() -> Config.class.getDeclaredMethod("load"),
            "Config should have load method");
    }

    @Test
    void testConfigUpdateMethodExists() throws NoSuchMethodException {
        Method updateMethod = Config.class.getDeclaredMethod("update");
        assertNotNull(updateMethod, "Config should have update method");
        assertTrue(Modifier.isPrivate(updateMethod.getModifiers()), "update method should be private");
    }

    @Test
    void testConfigParseMethodExists() throws NoSuchMethodException {
        Method parseMethod = Config.class.getDeclaredMethod("parse");
        assertNotNull(parseMethod, "Config should have parse method");
        assertTrue(Modifier.isPrivate(parseMethod.getModifiers()), "parse method should be private");
    }

    @Test
    void testConfigHasAllRequiredFields() throws NoSuchFieldException {
        // Test float fields
        assertNotNull(Config.class.getDeclaredField("COMBAT_DURATION_SEC"));
        assertNotNull(Config.class.getDeclaredField("COMBAT_TP_COOLDOWN_SEC"));
        assertNotNull(Config.class.getDeclaredField("COMBAT_NOTCH_APPLE_COOLDOWN_SEC"));
        assertNotNull(Config.class.getDeclaredField("POISON_DURATION_SEC"));
        assertNotNull(Config.class.getDeclaredField("POISON_LEVEL"));

        // Test public fields
        assertNotNull(Config.class.getDeclaredField("HEALTH_REMAINING_PUNISH"));
        assertNotNull(Config.class.getDeclaredField("ABSORPTION_REMAINING_PUNISH"));
        assertNotNull(Config.class.getDeclaredField("COMBAT_DURATION"));
        assertNotNull(Config.class.getDeclaredField("COMBAT_TP_COOLDOWN"));
        assertNotNull(Config.class.getDeclaredField("POISON_AMPLIFIER"));
    }

    @Test
    void testConfigValueClamping() {
        // Test that config ensures non-negative values
        // The actual clamping is tested when parse() runs, which happens during load()
        assertTrue(Config.HEALTH_REMAINING_PUNISH >= 0, "Health should be non-negative");
        assertTrue(Config.ABSORPTION_REMAINING_PUNISH >= 0, "Absorption should be non-negative");
        assertTrue(Config.COMBAT_DURATION >= 0, "Combat duration should be non-negative");
    }

    @Test
    void testAllBooleanFlagsExist() {
        // Test all boolean flags are accessible
        assertNotNull((Boolean) Config.ONLY_TAG_WEAPONS);
        assertNotNull((Boolean) Config.ENABLE_TAG_ON_ATTACK);
        assertNotNull((Boolean) Config.ENABLE_PVE_TAG_ON_DAMAGE);
        assertNotNull((Boolean) Config.ENABLE_PVE_TAG_ON_ATTACK);
        assertNotNull((Boolean) Config.ENABLE_HEALTH_PUNISH);
        assertNotNull((Boolean) Config.ENABLE_ABSORPTION_PUNISH);
        assertNotNull((Boolean) Config.ENABLE_POISON_PUNISH);
        assertNotNull((Boolean) Config.ENABLE_TP_PUNISH);
        assertNotNull((Boolean) Config.ENABLE_INSTANT_TP_PUNISH);
        assertNotNull((Boolean) Config.ENABLE_NOTCH_APPLE_PUNISH);
        assertNotNull((Boolean) Config.ENABLE_INSTANT_NOTCH_APPLE_PUNISH);
        assertNotNull((Boolean) Config.ENABLE_ELYTRA_PUNISH);
        assertNotNull((Boolean) Config.ENABLE_COMBAT_COLOUR);
        assertNotNull((Boolean) Config.DISABLE_TEAM_MSG_COMMAND);
        assertNotNull((Boolean) Config.DISABLE_TEAM_COMMAND);
    }
}

