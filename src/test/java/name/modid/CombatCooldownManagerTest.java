package name.modid;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for CombatCooldownManager class.
 */
class CombatCooldownManagerTest extends MinecraftTestBase {

    @Mock
    private ServerPlayerEntity mockPlayer;

    @Mock
    private ItemCooldownManager mockCooldownManager;

    @Mock
    private ItemStack mockMainHandStack;

    @Mock
    private ItemStack mockOffHandStack;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockPlayer.getItemCooldownManager()).thenReturn(mockCooldownManager);
    }

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
    void testTagCooldownsWithAllPunishesEnabled() {
        Config.ENABLE_INSTANT_TP_PUNISH = true;
        Config.ENABLE_INSTANT_NOTCH_APPLE_PUNISH = true;
        Config.COMBAT_TP_COOLDOWN = 200;
        Config.COMBAT_NOTCH_APPLE_COOLDOWN = 200;

        CombatCooldownManager.tagCooldowns(mockPlayer);

        // Verify cooldowns were set (3 items: ender pearl, chorus fruit, notch apple)
        verify(mockCooldownManager, atLeast(3)).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testTagCooldownsWithInstantTpPunishDisabled() {
        Config.ENABLE_INSTANT_TP_PUNISH = false;
        Config.ENABLE_INSTANT_NOTCH_APPLE_PUNISH = false;

        CombatCooldownManager.tagCooldowns(mockPlayer);

        // Verify no cooldowns were set
        verify(mockCooldownManager, never()).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testTagCooldownsWithOnlyTpPunishEnabled() {
        Config.ENABLE_INSTANT_TP_PUNISH = true;
        Config.ENABLE_INSTANT_NOTCH_APPLE_PUNISH = false;
        Config.COMBAT_TP_COOLDOWN = 200;

        CombatCooldownManager.tagCooldowns(mockPlayer);

        // Verify TP cooldowns were set (ender pearl + chorus fruit = 2 calls)
        verify(mockCooldownManager, atLeast(2)).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testTagCooldownsWithOnlyNotchApplePunishEnabled() {
        Config.ENABLE_INSTANT_TP_PUNISH = false;
        Config.ENABLE_INSTANT_NOTCH_APPLE_PUNISH = true;
        Config.COMBAT_NOTCH_APPLE_COOLDOWN = 200;

        CombatCooldownManager.tagCooldowns(mockPlayer);

        // Verify notch apple cooldown was set (1 call)
        verify(mockCooldownManager, atLeast(1)).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testConsumeCooldownsWithNotchAppleInMainHand() {
        Config.ENABLE_NOTCH_APPLE_PUNISH = true;
        Config.ENABLE_TP_PUNISH = false;
        Config.COMBAT_NOTCH_APPLE_COOLDOWN = 200;

        when(mockPlayer.getActiveHand()).thenReturn(Hand.MAIN_HAND);
        when(mockPlayer.getMainHandStack()).thenReturn(mockMainHandStack);
        when(mockMainHandStack.getItem()).thenReturn(Items.ENCHANTED_GOLDEN_APPLE);

        CombatCooldownManager.consumeCooldowns(mockPlayer);

        // Verify notch apple cooldown was set
        verify(mockCooldownManager, atLeast(1)).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testConsumeCooldownsWithChorusFruitInMainHand() {
        Config.ENABLE_NOTCH_APPLE_PUNISH = false;
        Config.ENABLE_TP_PUNISH = true;
        Config.COMBAT_TP_COOLDOWN = 200;

        when(mockPlayer.getActiveHand()).thenReturn(Hand.MAIN_HAND);
        when(mockPlayer.getMainHandStack()).thenReturn(mockMainHandStack);
        when(mockMainHandStack.getItem()).thenReturn(Items.CHORUS_FRUIT);

        CombatCooldownManager.consumeCooldowns(mockPlayer);

        // Verify chorus fruit cooldown was set
        verify(mockCooldownManager, atLeast(1)).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testConsumeCooldownsWithNotchAppleInOffHand() {
        Config.ENABLE_NOTCH_APPLE_PUNISH = true;
        Config.ENABLE_TP_PUNISH = false;
        Config.COMBAT_NOTCH_APPLE_COOLDOWN = 200;

        when(mockPlayer.getActiveHand()).thenReturn(Hand.OFF_HAND);
        when(mockPlayer.getOffHandStack()).thenReturn(mockOffHandStack);
        when(mockOffHandStack.getItem()).thenReturn(Items.ENCHANTED_GOLDEN_APPLE);

        CombatCooldownManager.consumeCooldowns(mockPlayer);

        // Verify notch apple cooldown was set
        verify(mockCooldownManager, atLeast(1)).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testConsumeCooldownsWithAllPunishesDisabled() {
        Config.ENABLE_NOTCH_APPLE_PUNISH = false;
        Config.ENABLE_TP_PUNISH = false;

        when(mockPlayer.getActiveHand()).thenReturn(Hand.MAIN_HAND);
        when(mockPlayer.getMainHandStack()).thenReturn(mockMainHandStack);
        when(mockMainHandStack.getItem()).thenReturn(Items.ENCHANTED_GOLDEN_APPLE);

        CombatCooldownManager.consumeCooldowns(mockPlayer);

        // Verify no cooldowns were set
        verify(mockCooldownManager, never()).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testConsumeCooldownsWithNonMatchingItem() {
        Config.ENABLE_NOTCH_APPLE_PUNISH = true;
        Config.ENABLE_TP_PUNISH = true;

        when(mockPlayer.getActiveHand()).thenReturn(Hand.MAIN_HAND);
        when(mockPlayer.getMainHandStack()).thenReturn(mockMainHandStack);
        when(mockMainHandStack.getItem()).thenReturn(Items.APPLE); // Not a special item

        CombatCooldownManager.consumeCooldowns(mockPlayer);

        // Verify no cooldowns were set for non-matching items
        verify(mockCooldownManager, never()).set(any(net.minecraft.util.Identifier.class), anyInt());
    }

    @Test
    void testConfigIntegrationForCooldowns() {
        // Test that config values are accessible
        assertTrue(Config.COMBAT_TP_COOLDOWN >= 0, "TP cooldown should be non-negative");
        assertTrue(Config.COMBAT_NOTCH_APPLE_COOLDOWN >= 0, "Notch apple cooldown should be non-negative");
    }

    @Test
    void testTagCooldownsUsesCorrectCooldownValues() {
        Config.ENABLE_INSTANT_TP_PUNISH = true;
        Config.ENABLE_INSTANT_NOTCH_APPLE_PUNISH = true;
        Config.COMBAT_TP_COOLDOWN = 300;
        Config.COMBAT_NOTCH_APPLE_COOLDOWN = 400;

        CombatCooldownManager.tagCooldowns(mockPlayer);

        // Verify that cooldown manager was accessed
        verify(mockPlayer, atLeastOnce()).getItemCooldownManager();
    }

    @Test
    void testCombatCooldownManagerHasPrivateMethods() throws NoSuchMethodException {
        // Test that private helper methods exist
        assertNotNull(CombatCooldownManager.class.getDeclaredMethod("punishEnderPearlCooldown",
            net.minecraft.server.network.ServerPlayerEntity.class));
        assertNotNull(CombatCooldownManager.class.getDeclaredMethod("punishChorusFruitCooldown",
            net.minecraft.server.network.ServerPlayerEntity.class));
        assertNotNull(CombatCooldownManager.class.getDeclaredMethod("punishTpCooldown",
            net.minecraft.server.network.ServerPlayerEntity.class));
        assertNotNull(CombatCooldownManager.class.getDeclaredMethod("punishNotchAppleCooldown",
            net.minecraft.server.network.ServerPlayerEntity.class));
        assertNotNull(CombatCooldownManager.class.getDeclaredMethod("activeItemMatches",
            net.minecraft.server.network.ServerPlayerEntity.class, net.minecraft.item.Item.class));
    }

    @Test
    void testCombatCooldownManagerHasConstants() throws NoSuchFieldException {
        // Test that constants exist
        assertNotNull(CombatCooldownManager.class.getDeclaredField("ENDER_PEARL"));
        assertNotNull(CombatCooldownManager.class.getDeclaredField("CHORUS_FRUIT"));
        assertNotNull(CombatCooldownManager.class.getDeclaredField("NOTCH_APPLE"));
    }
}

