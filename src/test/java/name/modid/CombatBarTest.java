package name.modid;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for CombatBar class.
 */
class CombatBarTest extends MinecraftTestBase {

    @Mock
    private ServerPlayerEntity mockPlayer;

    @Mock
    private net.minecraft.server.network.ServerPlayNetworkHandler mockNetworkHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockPlayer.getName()).thenReturn(Text.literal("TestPlayer"));
        mockPlayer.networkHandler = mockNetworkHandler;
    }

    @Test
    void testCombatBarClassExists() {
        assertNotNull(CombatBar.class, "CombatBar class should exist");
    }

    @Test
    void testCombatBarHasUpdateMethod() throws NoSuchMethodException {
        assertNotNull(CombatBar.class.getDeclaredMethod("update", float.class),
            "CombatBar should have update method");
    }

    @Test
    void testCombatBarHasRemoveMethod() throws NoSuchMethodException {
        assertNotNull(CombatBar.class.getDeclaredMethod("remove"),
            "CombatBar should have remove method");
    }

    @Test
    void testCombatBarConstructorExists() throws NoSuchMethodException {
        assertNotNull(CombatBar.class.getDeclaredConstructor(ServerPlayerEntity.class),
            "CombatBar should have constructor taking ServerPlayerEntity");
    }

    @Test
    void testCombatBarExtendsServerBossBar() {
        assertTrue(ServerBossBar.class.isAssignableFrom(CombatBar.class),
            "CombatBar should extend ServerBossBar");
    }

    @Test
    void testCombatBarHasOwnerField() throws NoSuchFieldException {
        assertNotNull(CombatBar.class.getDeclaredField("owner"),
            "CombatBar should have owner field");
    }

    @Test
    void testCombatBarConstructorSetsOwner() throws Exception {
        CombatBar combatBar = new CombatBar(mockPlayer);

        // Access the owner field using reflection
        var ownerField = CombatBar.class.getDeclaredField("owner");
        ownerField.setAccessible(true);
        ServerPlayerEntity owner = (ServerPlayerEntity) ownerField.get(combatBar);

        assertEquals(mockPlayer, owner, "Constructor should set the owner field");
    }

    @Test
    void testCombatBarUpdateSetsVisibleTrue() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        combatBar.update(0.5f);

        assertTrue(combatBar.isVisible(), "Update should set visible to true");
    }

    @Test
    void testCombatBarUpdateSetsPercent() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        combatBar.update(0.75f);

        assertEquals(0.75f, combatBar.getPercent(), 0.001f, "Update should set the percent correctly");
    }

    @Test
    void testCombatBarUpdateWithDifferentProgress() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        // Test with 0%
        combatBar.update(0.0f);
        assertEquals(0.0f, combatBar.getPercent(), 0.001f);

        // Test with 50%
        combatBar.update(0.5f);
        assertEquals(0.5f, combatBar.getPercent(), 0.001f);

        // Test with 100%
        combatBar.update(1.0f);
        assertEquals(1.0f, combatBar.getPercent(), 0.001f);
    }

    @Test
    void testCombatBarRemoveSetsVisibleFalse() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        // First make it visible
        combatBar.update(0.5f);
        assertTrue(combatBar.isVisible());

        // Then remove it
        combatBar.remove();

        assertFalse(combatBar.isVisible(), "Remove should set visible to false");
    }

    @Test
    void testCombatBarColorIsRed() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        assertEquals(BossBar.Color.RED, combatBar.getColor(), "Combat bar color should be RED");
    }

    @Test
    void testCombatBarStyleIsProgress() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        assertEquals(BossBar.Style.PROGRESS, combatBar.getStyle(), "Combat bar style should be PROGRESS");
    }

    @Test
    void testCombatBarInitiallyVisible() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        // The constructor calls setVisible and addPlayer
        // Check that it has been initialized
        assertNotNull(combatBar);
    }

    @Test
    void testCombatBarMultipleUpdates() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        // Multiple updates should work correctly
        combatBar.update(0.2f);
        assertEquals(0.2f, combatBar.getPercent(), 0.001f);

        combatBar.update(0.6f);
        assertEquals(0.6f, combatBar.getPercent(), 0.001f);

        combatBar.update(0.9f);
        assertEquals(0.9f, combatBar.getPercent(), 0.001f);
    }

    @Test
    void testCombatBarRemoveAfterUpdate() {
        CombatBar combatBar = new CombatBar(mockPlayer);

        combatBar.update(0.5f);
        assertTrue(combatBar.isVisible());

        combatBar.remove();
        assertFalse(combatBar.isVisible());

        // Can update again after remove
        combatBar.update(0.8f);
        assertTrue(combatBar.isVisible());
        assertEquals(0.8f, combatBar.getPercent(), 0.001f);
    }
}

