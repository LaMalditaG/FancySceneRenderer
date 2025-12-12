package com.fancyscenerenderer.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class AllCommands {
    public static void register(){
        ClientCommandRegistrationCallback.EVENT.register(AllCommands::registerAll);
    }

    private static void registerAll(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
        CreateSceneCommand.register(dispatcher);
    }
}
