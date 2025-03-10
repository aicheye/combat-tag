package name.modid.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerDeathCallback {
    Event<PlayerDeathCallback> EVENT = EventFactory.createArrayBacked(PlayerDeathCallback.class,
            (listeners) -> (player, source) -> {
                for (PlayerDeathCallback event : listeners) {
                    event.onPlayerDeath(player, source);
                }
            });

    void onPlayerDeath(ServerPlayerEntity player, DamageSource source);
}