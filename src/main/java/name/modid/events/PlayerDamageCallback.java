package name.modid.events;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerDamageCallback {
    Event<PlayerDamageCallback> EVENT = EventFactory.createArrayBacked(PlayerDamageCallback.class,
            (listeners) -> (player, source) -> {
                for (PlayerDamageCallback event : listeners) {
                    event.onPlayerDamaged(player, source);
                }
            });

    void onPlayerDamaged(ServerPlayerEntity player, DamageSource source);
}