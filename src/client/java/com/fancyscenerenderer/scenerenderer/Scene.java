package com.fancyscenerenderer.scenerenderer;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Scene {
    ArrayList<SceneBlock> blocks = new ArrayList<>();

    public SceneBlock getBlock(int i){
        return blocks.get(i);
    }

    public void addBlock(@NotNull BlockPos blockPos,@NotNull BlockState blockState,@Nullable BlockEntity blockEntity){
        blocks.add(new SceneBlock(blockPos,blockState,blockEntity));
    }

    public int getCount(){
        return blocks.size();
    }

    public static class SceneBlock{
        BlockPos blockPos;
        BlockState blockState;
        BlockEntity blockEntity = null;

        public SceneBlock(@NotNull BlockPos blockPos, @NotNull BlockState blockState, @Nullable BlockEntity blockEntity){
            this.blockPos = blockPos;
            this.blockState = blockState;
            this.blockEntity = blockEntity;
        }
    }

    public Scene(String input){
        try{
            CompoundTag tag = TagParser.parseTag(input);

            ListTag blocks = (ListTag)tag.get("blocks");

            for(int i = 0; i < Objects.requireNonNull(blocks).size(); i++){
                CompoundTag block = blocks.getCompound(i);

                BlockPos blockPos = NbtUtils.readBlockPos(block.getCompound("pos"));

                BlockEntity be = null;
                BlockState blockState = blockStateFromTag(block.getCompound("blockstate"));
                if(block.contains("blockentity")){
                    if(blockState.getBlock() instanceof EntityBlock entityBlock){
                        be = entityBlock.newBlockEntity(new BlockPos(0,0,0),blockState);
                        if(be !=null)
                            be.load(block.getCompound("blockentity"));
                    }
                }

                this.addBlock(blockPos,blockState, be);
            }

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public BlockState blockStateFromTag(CompoundTag compoundTag){
        BlockState bs;
        String name = compoundTag.getString("Name");

        Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(name));
        bs = block.defaultBlockState();

        if (compoundTag.contains("Properties", 10)) {
            CompoundTag properties = compoundTag.getCompound("Properties");
            StateDefinition<Block, BlockState> stateDef = block.getStateDefinition();

            for(String key : properties.getAllKeys()) {
                Property<?> property = stateDef.getProperty(key);
                if (property != null) {
                    bs = setValueHelper(bs,property,key,properties);
                }
            }
        }
        return bs;
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S c, Property<T> property, String name, CompoundTag tag) {
        Optional<T> p = property.getValue(tag.getString(name));
        return p.map(t -> c.setValue(property, t)).orElse(c);
    }
}
