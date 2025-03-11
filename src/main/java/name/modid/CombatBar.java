package name.modid;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


public class CombatBar extends ServerBossBar {
    ServerPlayerEntity owner;

    public CombatBar(ServerPlayerEntity player) {
        super(Text.literal("Combat Tag").formatted(Formatting.RED), BossBar.Color.RED, BossBar.Style.PROGRESS);
        this.addPlayer(player);
        owner = player;
    }

    public void update(float progress) {
        this.setVisible(true);
        this.addPlayer(owner);
        this.setPercent(progress);
    }

    public void remove() {
        this.setVisible(false);
        this.clearPlayers();
    }
}