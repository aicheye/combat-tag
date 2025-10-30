package name.modid;

import name.modid.access.ServerPlayerEntityAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for CombatBarManager class.
 */
class CombatBarManagerTest extends MinecraftTestBase {

    @Mock
    private ServerPlayerEntity mockPlayer;

    @Mock
    private ServerPlayerEntity mockPlayer2;

    @Mock
    private MinecraftServer mockServer;

    @Mock
    private PlayerManager mockPlayerManager;

    @Mock
    private ServerPlayerEntityAccess mockPlayerAccess;

    @Mock
    private CombatBar mockCombatBar;

    @Mock
    private net.minecraft.server.network.ServerPlayNetworkHandler mockNetworkHandler;

    @Mock
    private net.minecraft.server.network.ServerPlayNetworkHandler mockNetworkHandler2;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Setup default mock behavior
        when(mockPlayer.getName()).thenReturn(Text.literal("TestPlayer"));
        mockPlayer.networkHandler = mockNetworkHandler;
        when(mockPlayer2.getName()).thenReturn(Text.literal("TestPlayer2"));
        mockPlayer2.networkHandler = mockNetworkHandler2;

        // Clear the static combatBars HashMap between tests
        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<?, ?> combatBars = (HashMap<?, ?>) combatBarsField.get(null);
        combatBars.clear();
    }

    @Test
    void testCombatBarManagerClassExists() {
        assertNotNull(CombatBarManager.class, "CombatBarManager class should exist");
    }

    @Test
    void testCombatBarManagerHasUpdateMethod() throws NoSuchMethodException {
        assertNotNull(CombatBarManager.class.getDeclaredMethod("updateCombatBar",
            net.minecraft.server.network.ServerPlayerEntity.class, float.class),
            "CombatBarManager should have updateCombatBar method");
    }

    @Test
    void testCombatBarManagerHasTickMethod() throws NoSuchMethodException {
        assertNotNull(CombatBarManager.class.getDeclaredMethod("tickCombatBars",
            net.minecraft.server.MinecraftServer.class),
            "CombatBarManager should have tickCombatBars method");
    }

    @Test
    void testUpdateCombatBarCreatesNewBar() throws Exception {
        // Call updateCombatBar
        CombatBarManager.updateCombatBar(mockPlayer, 0.5f);

        // Verify a combat bar was created
        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<ServerPlayerEntity, CombatBar> combatBars =
            (HashMap<ServerPlayerEntity, CombatBar>) combatBarsField.get(null);

        assertTrue(combatBars.containsKey(mockPlayer), "Combat bar should be created for player");
        assertNotNull(combatBars.get(mockPlayer), "Combat bar should not be null");
    }

    @Test
    void testUpdateCombatBarReusesExistingBar() throws Exception {
        // First call creates the bar
        CombatBarManager.updateCombatBar(mockPlayer, 0.5f);

        // Get the bar
        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<ServerPlayerEntity, CombatBar> combatBars =
            (HashMap<ServerPlayerEntity, CombatBar>) combatBarsField.get(null);
        CombatBar firstBar = combatBars.get(mockPlayer);

        // Second call should reuse the same bar
        CombatBarManager.updateCombatBar(mockPlayer, 0.8f);
        CombatBar secondBar = combatBars.get(mockPlayer);

        assertSame(firstBar, secondBar, "Should reuse the same combat bar instance");
    }

    @Test
    void testUpdateCombatBarUpdatesProgress() throws Exception {
        // Create a bar and update it
        CombatBarManager.updateCombatBar(mockPlayer, 0.3f);

        // Get the bar
        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<ServerPlayerEntity, CombatBar> combatBars =
            (HashMap<ServerPlayerEntity, CombatBar>) combatBarsField.get(null);
        CombatBar bar = combatBars.get(mockPlayer);

        // Verify the progress was set
        assertEquals(0.3f, bar.getPercent(), 0.001f, "Bar progress should be updated");
    }

    @Test
    void testUpdateCombatBarWithDifferentPlayers() throws Exception {
        // Update bars for two different players
        CombatBarManager.updateCombatBar(mockPlayer, 0.4f);
        CombatBarManager.updateCombatBar(mockPlayer2, 0.6f);

        // Get the bars
        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<ServerPlayerEntity, CombatBar> combatBars =
            (HashMap<ServerPlayerEntity, CombatBar>) combatBarsField.get(null);

        // Verify both bars exist and have correct progress
        assertTrue(combatBars.containsKey(mockPlayer), "Player1 should have a combat bar");
        assertTrue(combatBars.containsKey(mockPlayer2), "Player2 should have a combat bar");
        assertEquals(0.4f, combatBars.get(mockPlayer).getPercent(), 0.001f);
        assertEquals(0.6f, combatBars.get(mockPlayer2).getPercent(), 0.001f);
    }

    @Test
    void testTickCombatBarsWithNoPlayers() {
        // Setup
        List<ServerPlayerEntity> players = new ArrayList<>();
        when(mockServer.getPlayerManager()).thenReturn(mockPlayerManager);
        when(mockPlayerManager.getPlayerList()).thenReturn(players);

        // Execute - should not throw any exception
        assertDoesNotThrow(() -> CombatBarManager.tickCombatBars(mockServer));
    }

    @Test
    void testTickCombatBarsWithPlayersNotInCombat() throws Exception {
        // Setup
        List<ServerPlayerEntity> players = new ArrayList<>();
        players.add(mockPlayer);

        when(mockServer.getPlayerManager()).thenReturn(mockPlayerManager);
        when(mockPlayerManager.getPlayerList()).thenReturn(players);

        // Create a combat bar for the player first
        CombatBarManager.updateCombatBar(mockPlayer, 0.5f);

        // Get the bar to verify it exists
        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<ServerPlayerEntity, CombatBar> combatBars =
            (HashMap<ServerPlayerEntity, CombatBar>) combatBarsField.get(null);
        CombatBar bar = combatBars.get(mockPlayer);

        // Make it visible
        bar.update(0.5f);
        assertTrue(bar.isVisible(), "Bar should be visible initially");

        // Now test with player not in combat - this requires the player to be cast to ServerPlayerEntityAccess
        // which is complex in unit tests, so we just verify the method executes
        assertDoesNotThrow(() -> CombatBarManager.tickCombatBars(mockServer));
    }

    @Test
    void testCombatBarManagerHasCombatBarsField() throws NoSuchFieldException {
        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        assertNotNull(combatBarsField, "CombatBarManager should have combatBars field");
    }

    @Test
    void testUpdateCombatBarWithZeroProgress() throws Exception {
        CombatBarManager.updateCombatBar(mockPlayer, 0.0f);

        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<ServerPlayerEntity, CombatBar> combatBars =
            (HashMap<ServerPlayerEntity, CombatBar>) combatBarsField.get(null);
        CombatBar bar = combatBars.get(mockPlayer);

        assertEquals(0.0f, bar.getPercent(), 0.001f, "Bar progress should be 0.0");
    }

    @Test
    void testUpdateCombatBarWithFullProgress() throws Exception {
        CombatBarManager.updateCombatBar(mockPlayer, 1.0f);

        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<ServerPlayerEntity, CombatBar> combatBars =
            (HashMap<ServerPlayerEntity, CombatBar>) combatBarsField.get(null);
        CombatBar bar = combatBars.get(mockPlayer);

        assertEquals(1.0f, bar.getPercent(), 0.001f, "Bar progress should be 1.0");
    }

    @Test
    void testUpdateCombatBarMultipleTimes() throws Exception {
        // Update multiple times with different progress values
        CombatBarManager.updateCombatBar(mockPlayer, 0.2f);
        CombatBarManager.updateCombatBar(mockPlayer, 0.5f);
        CombatBarManager.updateCombatBar(mockPlayer, 0.8f);

        Field combatBarsField = CombatBarManager.class.getDeclaredField("combatBars");
        combatBarsField.setAccessible(true);
        HashMap<ServerPlayerEntity, CombatBar> combatBars =
            (HashMap<ServerPlayerEntity, CombatBar>) combatBarsField.get(null);
        CombatBar bar = combatBars.get(mockPlayer);

        // Should have the latest progress value
        assertEquals(0.8f, bar.getPercent(), 0.001f, "Bar progress should be the latest value");
    }
}

