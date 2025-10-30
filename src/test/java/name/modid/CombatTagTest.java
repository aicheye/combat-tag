package name.modid;

import name.modid.access.ServerPlayerEntityAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for CombatTag class.
 */
class CombatTagTest extends MinecraftTestBase {

    @Mock
    private ServerPlayerEntity mockPlayer;

    @Mock
    private ServerPlayerEntity mockAttacker;

    @Mock
    private ServerPlayerEntityAccess mockPlayerAccess;

    @Mock
    private ServerPlayerEntityAccess mockAttackerAccess;

    @Mock
    private DamageSource mockDamageSource;

    @Mock
    private LivingEntity mockLivingEntity;

    @Mock
    private ProjectileEntity mockProjectile;

    @Mock
    private RegistryEntry<DamageType> mockDamageTypeEntry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup default player behavior
        when(mockPlayer.isPartOfGame()).thenReturn(true);
        when(mockPlayer.isCreative()).thenReturn(false);
        when(mockPlayer.getName()).thenReturn(Text.literal("TestPlayer"));

        when(mockAttacker.isPartOfGame()).thenReturn(true);
        when(mockAttacker.isCreative()).thenReturn(false);
        when(mockAttacker.getName()).thenReturn(Text.literal("TestAttacker"));
    }

    @Test
    void testCombatTagClassExists() {
        assertNotNull(CombatTag.class, "CombatTag class should exist");
    }

    @Test
    void testCombatTagHasModId() throws NoSuchFieldException {
        assertNotNull(CombatTag.class.getDeclaredField("MOD_ID"),
                "CombatTag should have MOD_ID field");
        assertEquals("combat-tag", CombatTag.MOD_ID);
    }

    @Test
    void testCombatTagHasLogger() throws NoSuchFieldException {
        assertNotNull(CombatTag.class.getDeclaredField("LOGGER"),
                "CombatTag should have LOGGER field");
        assertNotNull(CombatTag.LOGGER);
    }

    @Test
    void testWeaponsSetIsNotEmpty() {
        assertNotNull(CombatTag.weapons, "Weapons set should not be null");
        assertFalse(CombatTag.weapons.isEmpty(), "Weapons set should contain items");
        assertTrue(CombatTag.weapons.contains(Items.DIAMOND_SWORD), "Weapons should contain diamond sword");
        assertTrue(CombatTag.weapons.contains(Items.BOW), "Weapons should contain bow");
        assertTrue(CombatTag.weapons.contains(Items.TRIDENT), "Weapons should contain trident");
    }

    @Test
    void testRemoveCombatTagMethodExists() throws NoSuchMethodException {
        assertNotNull(CombatTag.class.getDeclaredMethod("removeCombatTag", ServerPlayerEntity.class),
                "CombatTag should have removeCombatTag method");
    }

    @Test
    void testLogoutPunishMethodExists() throws NoSuchMethodException {
        assertNotNull(CombatTag.class.getDeclaredMethod("logoutPunish", ServerPlayerEntity.class),
                "CombatTag should have logoutPunish method");
    }

    @Test
    void testLogoutPunishSetsHealthWhenEnabled() {
        // Setup
        Config.ENABLE_HEALTH_PUNISH = true;
        Config.HEALTH_REMAINING_PUNISH = 5.0f;
        Config.ENABLE_ABSORPTION_PUNISH = false;
        Config.ENABLE_POISON_PUNISH = false;

        // Execute
        CombatTag.logoutPunish(mockPlayer);

        // Verify
        verify(mockPlayer).setHealth(5.0f);
    }

    @Test
    void testLogoutPunishSetsAbsorptionWhenEnabled() {
        // Setup
        Config.ENABLE_HEALTH_PUNISH = false;
        Config.ENABLE_ABSORPTION_PUNISH = true;
        Config.ABSORPTION_REMAINING_PUNISH = 0.0f;
        Config.ENABLE_POISON_PUNISH = false;

        // Execute
        CombatTag.logoutPunish(mockPlayer);

        // Verify
        verify(mockPlayer).setAbsorptionAmount(0.0f);
    }

    @Test
    void testLogoutPunishAddsPoisonWhenEnabled() {
        // Setup
        Config.ENABLE_HEALTH_PUNISH = false;
        Config.ENABLE_ABSORPTION_PUNISH = false;
        Config.ENABLE_POISON_PUNISH = true;
        Config.POISON_DURATION = 100;
        Config.POISON_AMPLIFIER = 4;

        // Execute
        CombatTag.logoutPunish(mockPlayer);

        // Verify
        verify(mockPlayer).addStatusEffect(any());
    }

    @Test
    void testLogoutPunishAppliesAllPunishmentsWhenAllEnabled() {
        // Setup
        Config.ENABLE_HEALTH_PUNISH = true;
        Config.HEALTH_REMAINING_PUNISH = 1.0f;
        Config.ENABLE_ABSORPTION_PUNISH = true;
        Config.ABSORPTION_REMAINING_PUNISH = 0.0f;
        Config.ENABLE_POISON_PUNISH = true;
        Config.POISON_DURATION = 100;
        Config.POISON_AMPLIFIER = 4;

        // Execute
        CombatTag.logoutPunish(mockPlayer);

        // Verify
        verify(mockPlayer).setHealth(1.0f);
        verify(mockPlayer).setAbsorptionAmount(0.0f);
        verify(mockPlayer).addStatusEffect(any());
    }

    @Test
    void testLogoutPunishDoesNothingWhenAllDisabled() {
        // Setup
        Config.ENABLE_HEALTH_PUNISH = false;
        Config.ENABLE_ABSORPTION_PUNISH = false;
        Config.ENABLE_POISON_PUNISH = false;

        // Execute
        CombatTag.logoutPunish(mockPlayer);

        // Verify
        verify(mockPlayer, never()).setHealth(anyFloat());
        verify(mockPlayer, never()).setAbsorptionAmount(anyFloat());
        verify(mockPlayer, never()).addStatusEffect(any());
    }

    @Test
    void testWeaponsSetContainsAllExpectedWeapons() {
        // Test swords
        assertTrue(CombatTag.weapons.contains(Items.NETHERITE_SWORD));
        assertTrue(CombatTag.weapons.contains(Items.DIAMOND_SWORD));
        assertTrue(CombatTag.weapons.contains(Items.IRON_SWORD));
        assertTrue(CombatTag.weapons.contains(Items.GOLDEN_SWORD));
        assertTrue(CombatTag.weapons.contains(Items.STONE_SWORD));
        assertTrue(CombatTag.weapons.contains(Items.WOODEN_SWORD));

        // Test axes
        assertTrue(CombatTag.weapons.contains(Items.NETHERITE_AXE));
        assertTrue(CombatTag.weapons.contains(Items.DIAMOND_AXE));
        assertTrue(CombatTag.weapons.contains(Items.IRON_AXE));
        assertTrue(CombatTag.weapons.contains(Items.GOLDEN_AXE));
        assertTrue(CombatTag.weapons.contains(Items.STONE_AXE));
        assertTrue(CombatTag.weapons.contains(Items.WOODEN_AXE));

        // Test other weapons
        assertTrue(CombatTag.weapons.contains(Items.TRIDENT));
        assertTrue(CombatTag.weapons.contains(Items.MACE));
        assertTrue(CombatTag.weapons.contains(Items.BOW));
        assertTrue(CombatTag.weapons.contains(Items.CROSSBOW));
    }

    @Test
    void testWeaponsSetDoesNotContainNonWeapons() {
        assertFalse(CombatTag.weapons.contains(Items.DIRT));
        assertFalse(CombatTag.weapons.contains(Items.APPLE));
        assertFalse(CombatTag.weapons.contains(Items.STICK));
    }

    @Test
    void testModIdIsCorrect() {
        assertEquals("combat-tag", CombatTag.MOD_ID, "MOD_ID should be 'combat-tag'");
    }

    @Test
    void testLoggerIsInitialized() {
        assertNotNull(CombatTag.LOGGER, "LOGGER should be initialized");
        assertEquals("combat-tag", CombatTag.LOGGER.getName(), "Logger name should match MOD_ID");
    }

    @Test
    void testLogoutPunishWithDifferentHealthValues() {
        Config.ENABLE_HEALTH_PUNISH = true;
        Config.ENABLE_ABSORPTION_PUNISH = false;
        Config.ENABLE_POISON_PUNISH = false;

        // Test with different health values
        Config.HEALTH_REMAINING_PUNISH = 1.0f;
        CombatTag.logoutPunish(mockPlayer);
        verify(mockPlayer).setHealth(1.0f);

        reset(mockPlayer);
        Config.HEALTH_REMAINING_PUNISH = 10.0f;
        CombatTag.logoutPunish(mockPlayer);
        verify(mockPlayer).setHealth(10.0f);
    }

    @Test
    void testLogoutPunishWithDifferentAbsorptionValues() {
        Config.ENABLE_HEALTH_PUNISH = false;
        Config.ENABLE_ABSORPTION_PUNISH = true;
        Config.ENABLE_POISON_PUNISH = false;

        // Test with different absorption values
        Config.ABSORPTION_REMAINING_PUNISH = 0.0f;
        CombatTag.logoutPunish(mockPlayer);
        verify(mockPlayer).setAbsorptionAmount(0.0f);

        reset(mockPlayer);
        Config.ABSORPTION_REMAINING_PUNISH = 4.0f;
        CombatTag.logoutPunish(mockPlayer);
        verify(mockPlayer).setAbsorptionAmount(4.0f);
    }

    @Test
    void testOnInitializeMethodExists() throws NoSuchMethodException {
        assertNotNull(CombatTag.class.getMethod("onInitialize"),
                "CombatTag should have onInitialize method");
    }
}
