package name.modid.mixin;

import name.modid.Config;
import name.modid.access.ServerPlayerEntityAccess;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
    private void onExecuteCommand(String command, CallbackInfo ci) {
        if (Config.COMMAND_BLACKLIST.isEmpty()) return;

        ServerPlayerEntityAccess access = (ServerPlayerEntityAccess) player;
        if (!access.combat_tag$inCombat()) return;

        String cmd = command.startsWith("/") ? command.substring(1) : command;
        String baseCommand = cmd.split(" ", 2)[0];

        for (String blacklisted : Config.COMMAND_BLACKLIST) {
            if (baseCommand.equalsIgnoreCase(blacklisted)) {
                player.sendMessage(Text.literal(Config.COMMAND_BLACKLIST_MESSAGE).formatted(Formatting.RED), false);
                ci.cancel();
                return;
            }
        }
    }
}
