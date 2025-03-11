package name.modid;

import name.modid.access.ServerPlayerEntityAccess;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class ScoreboardManager {
    private static final String COMBAT_TAG = "combat";

    private static Scoreboard scoreboard;
    private static Team combatTeam;

    public static void tickScoreboard(MinecraftServer server) {
        if (scoreboard == null) {
            scoreboard = server.getScoreboard();
        }

        if (combatTeam == null) {
            combatTeam = scoreboard.getTeam(COMBAT_TAG);
            if (combatTeam == null) {
                combatTeam = scoreboard.addTeam(COMBAT_TAG);
            }
            combatTeam.setColor(Formatting.RED);
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayerEntityAccess combatAccessor = (ServerPlayerEntityAccess) player;

            if (combatAccessor.combat_tag$inCombat() && !combatTeam.getPlayerList().contains(player.getNameForScoreboard())) {
                combatTeam.getPlayerList().add(player.getNameForScoreboard());
                scoreboard.updateScoreboardTeamAndPlayers(combatTeam);
            }

            else if (!combatAccessor.combat_tag$inCombat() && combatTeam.getPlayerList().contains(player.getNameForScoreboard())) {
                combatTeam.getPlayerList().remove(player.getNameForScoreboard());
                scoreboard.updateRemovedTeam(combatTeam);
            }
        }
    }
}
