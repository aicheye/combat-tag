package name.modid.mixin;

import name.modid.Config;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamCommand;
import net.minecraft.server.command.TeamMsgCommand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
}
