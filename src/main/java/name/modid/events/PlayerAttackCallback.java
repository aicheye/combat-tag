package name.modid.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerAttackCallback {
    Event<PlayerAttackCallback> EVENT = EventFactory.createArrayBacked(PlayerAttackCallback.class,
            (listeners) -> (player) -> {
                for (PlayerAttackCallback event : listeners) {
                    event.onPlayerAttack(player);
                }
            });

    void onPlayerAttack(ServerPlayerEntity player);
}