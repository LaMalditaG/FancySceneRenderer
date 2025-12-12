package com.fancyscenerenderer.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;

import static de.keksuccino.fancymenu.FancyMenu.MOD_ID;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;


public class CreateSceneCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        dispatcher.register(
            literal("fmsr-create")
                .then(argument("pos0", CBlockPosArgumentType.blockPos())
                    .then(argument("pos1", CBlockPosArgumentType.blockPos())
                        .executes(ctx -> {
                            BlockPos pos0 = CBlockPosArgumentType.getCBlockPos(ctx,"pos0");
                            BlockPos pos1 = CBlockPosArgumentType.getCBlockPos(ctx,"pos1");

                            String scene = createScene(pos0,pos1,ctx.getSource().getWorld());
                            LocalPlayer player = ctx.getSource().getPlayer();
                            printJsonToChat(player,scene);

                            return Command.SINGLE_SUCCESS;
                        })
                            .then(argument("filename", StringArgumentType.string())
                                .executes(ctx -> {
                                    BlockPos pos0 = CBlockPosArgumentType.getCBlockPos(ctx,"pos0");
                                    BlockPos pos1 = CBlockPosArgumentType.getCBlockPos(ctx,"pos1");
                                    String name = StringArgumentType.getString(ctx,"filename");

                                    String scene = createScene(pos0,pos1,ctx.getSource().getWorld());
                                    LocalPlayer player = ctx.getSource().getPlayer();
                                    printJsonToChat(player,scene);
                                    try {
                                        writeToFile(scene,name);
                                        printLocationToChat(player,name);
                                        LogManager.getLogger().info("Json saved");
                                    } catch (Exception e) {
                                        player.sendSystemMessage(Component.literal("Failed to write file: "+e.getMessage()));
                                        LogManager.getLogger().error(e);
                                    }

                                    return Command.SINGLE_SUCCESS;
        })))));
    }

    private static String createScene(BlockPos pos0, BlockPos pos1, ClientLevel level){
        Vec3i origin = getMin(pos0,pos1);
        Vec3i end = getMax(pos0,pos1);
        CompoundTag sceneTag = new CompoundTag();
        ListTag blocks = new ListTag();

        for(int i = origin.getX(); i < end.getX()+1; i++){
            for(int j = origin.getY(); j < end.getY()+1; j++){
                for(int k = origin.getZ(); k < end.getZ()+1; k++){
                    BlockPos pos = new BlockPos(new Vec3i(i,j,k));
                    BlockPos innerPos = pos.subtract(origin);

                    BlockState bs = level.getBlockState(pos);

                    CompoundTag block = new CompoundTag();
                    block.put("blockstate", NbtUtils.writeBlockState(bs));
                    block.put("pos",NbtUtils.writeBlockPos(innerPos));

                    if(bs.hasBlockEntity()){
                        BlockEntity be = level.getBlockEntity(pos);
                        block.put("blockentity",be.saveWithFullMetadata());
                    }
                    blocks.add(block);
                }
            }
        }

        sceneTag.put("blocks",blocks);

        return sceneTag.toString();
    }
    private static Vec3i getMin(BlockPos pos0, BlockPos pos1){
        return new Vec3i(
                Math.min(pos0.getX(), pos1.getX()),
                Math.min(pos0.getY(), pos1.getY()),
                Math.min(pos0.getZ(), pos1.getZ())
        );
    }

    private static Vec3i getMax(BlockPos pos0, BlockPos pos1){
        return new Vec3i(
                Math.max(pos0.getX(), pos1.getX()),
                Math.max(pos0.getY(), pos1.getY()),
                Math.max(pos0.getZ(), pos1.getZ())
        );
    }

    static private void writeToFile(String scene, String name) throws Exception {
        File dir = getDir();

        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, name);
        FileWriter writer = new FileWriter(file);
        writer.write(scene);
        writer.close();
    }

    static private File getDir(){
        return Paths.get("config", MOD_ID,"assets","scenes").toFile();
    }

    static private void printJsonToChat(LocalPlayer player, String text){
        var clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,text);
        player.sendSystemMessage(Component.literal("Json generated ").append(Component.literal("[Copy JSON]").setStyle(Style.EMPTY.withClickEvent(clickEvent).withColor(ChatFormatting.BLUE).withUnderlined(true))));
    }

    static private void printLocationToChat(LocalPlayer player,String filename){
        var clickEvent = new ClickEvent(ClickEvent.Action.OPEN_FILE,getDir().toString());
        player.sendSystemMessage(Component.literal("Also saved as ").append(Component.literal(getDir()+"/" + filename).setStyle(Style.EMPTY.withClickEvent(clickEvent).withUnderlined(true))));
    }
}
