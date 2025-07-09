package name.modid.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerAttackCallback {
    Event<PlayerAttackCallback> EVENT = EventFactory.createArrayBacked(PlayerAttackCallback.class,
            (listeners) -> (player, target) -> {
                for (PlayerAttackCallback event : listeners) {
                    event.onPlayerAttack(player, target);
                }
            });

    void onPlayerAttack(ServerPlayerEntity player, Entity target);
}
