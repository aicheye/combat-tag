package name.modid.mixin;

import name.modid.Config;
import name.modid.access.ServerPlayerEntityAccess;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamCommand;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/command/TeamCommand;register(Lcom/mojang/brigadier/CommandDispatcher;Lnet/minecraft/command/CommandRegistryAccess;)V"
            )
    )
    private void registerTeam(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        if (!Config.ENABLE_COMBAT_COLOUR || !Config.DISABLE_TEAM_COMMAND) {
            TeamCommand.register(dispatcher, registryAccess);
        }
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/command/TeamMsgCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V"
            )
    )
    private void registerTeamMsg(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!Config.ENABLE_COMBAT_COLOUR || !Config.DISABLE_TEAM_MSG_COMMAND) {
            TeamMsgCommand.register(dispatcher);
        }
    }

    @Inject(method = "parseAndExecute", at = @At("HEAD"), cancellable = true)
    private void onParseAndExecute(ServerCommandSource source, String command, CallbackInfo ci) {
        if (Config.COMMAND_BLACKLIST.isEmpty()) return;

        if (!(source.getEntity() instanceof ServerPlayerEntity player)) return;

        ServerPlayerEntityAccess access = (ServerPlayerEntityAccess) player;
        if (!access.combat_tag$inCombat()) return;

        String cmd = command.startsWith("/") ? command.substring(1) : command;
        String baseCommand = cmd.split(" ", 2)[0];

        for (String blacklisted : Config.COMMAND_BLACKLIST) {
            if (baseCommand.equalsIgnoreCase(blacklisted)) {
                source.sendError(Text.literal(Config.COMMAND_BLACKLIST_MESSAGE));
                ci.cancel();
                return;
            }
        }
    }
}
