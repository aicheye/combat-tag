package name.modid;

import name.modid.access.ServerPlayerEntityAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

public class CombatBarManager {
    private static final HashMap<ServerPlayerEntity, CombatBar> combatBars = new HashMap<>();

    public static void updateCombatBar(ServerPlayerEntity player, float progress) {
        CombatBar combatBar;

        if (combatBars.containsKey(player)) {
            combatBar = combatBars.get(player);
        } else {
            combatBar = new CombatBar(player);
            combatBars.put(player, combatBar);
        }

        combatBar.update(progress);
    }

    public static void tickCombatBars(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;

            if (combatAccessor.combat_tag$inCombat()) {
                float progress = combatAccessor.combat_tag$getProgress();
                updateCombatBar(player, progress);
            } else {
                CombatBar combatBar = combatBars.get(player);
                if (combatBar != null && combatBar.isVisible()) {
                    combatBar.remove();
                }
            }
        }
    }
}
