package com.fancyscenerenderer.scenerenderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.MathUtils;
import de.keksuccino.fancymenu.util.resource.ResourceSupplier;
import de.keksuccino.fancymenu.util.resource.resources.text.IText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;

import net.minecraft.world.inventory.InventoryMenu;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;


//https://github.com/Fabricators-of-Create/Create/blob/mc1.20.1/fabric/flywheel-upgrade/src/main/java/com/simibubi/create/foundation/gui/element/GuiGameElement.java#L187

public class SceneElement extends AbstractElement {

    private static final Logger LOGGER = LogManager.getLogger();
    Vec3 rot = new Vec3(20,0,0);
    Vec3 offset = new Vec3(1,0,1);
    Vec3 scale = new Vec3(50,50,50);
    Scene scene = null;

    boolean updateRot = true;
    String rotXString = null;
    String rotYString = null;
    String rotZString = null;

    boolean updateOffset = true;
    String offsetXString = null;
    String offsetYString = null;
    String offsetZString = null;

    boolean updateScale = true;
    String scaleXString = null;
    String scaleYString = null;
    String scaleZString = null;

    public ResourceSupplier<IText> textFileSupplier;
    SourceMode sourceMode;
    String source;

    public SceneElement(@NotNull ElementBuilder<?, ?> builder) {
        super(builder);
    }

    void updateScene(){
        if ((this.source == null)) return;

        //TEXT FILE
        if (this.sourceMode == SourceMode.TEXT_FILE) {
            this.textFileSupplier = ResourceSupplier.text(source);
            IText text = this.textFileSupplier.get();
            if (text != null) {
                StringBuilder content = new StringBuilder();
                var lines = text.getTextLines();
                if(lines!=null){
                    for(var line : lines)
                        content.append(line);
                    this.scene = new Scene(content.toString());
                }
            }
        }

        //DIRECT
        if (this.sourceMode == SourceMode.DIRECT_TEXT) {
            this.scene = new Scene(source);
        }
    }

    private void tryUpdateTransform(){
        if(updateRot){
            rot = new Vec3(floatParser(rotXString),floatParser(rotYString),floatParser(rotZString));
        }
        if(updateOffset){
            offset = new Vec3(floatParser(offsetXString),floatParser(offsetYString),floatParser(offsetZString));
        }
        if(updateScale){
            scale = new Vec3(floatParser(scaleXString),floatParser(scaleYString),floatParser(scaleZString));
        }
    }

    void updateTransform(){
        rot = new Vec3(floatParser(rotXString),floatParser(rotYString),floatParser(rotZString));
        offset = new Vec3(floatParser(offsetXString),floatParser(offsetYString),floatParser(offsetZString));
        scale = new Vec3(floatParser(scaleXString),floatParser(scaleYString),floatParser(scaleZString));
    }

    @Override
    public void render(net.minecraft.client.gui.@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        tryUpdateTransform();

        if (this.shouldRender()&&scene!=null) {
            for(int i = 0; i < scene.getCount();i++){
                Scene.SceneBlock sceneBlock = scene.getBlock(i);
                innerRender(guiGraphics,sceneBlock.blockState,sceneBlock.blockPos,sceneBlock.blockEntity);
            }
        }
    }

    private void innerRender(net.minecraft.client.gui.@NotNull GuiGraphics guiGraphics, BlockState blockState,BlockPos pos, @Nullable BlockEntity blockEntity){

        PoseStack matrixStack = guiGraphics.pose();
        prepareMatrix(matrixStack);

        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
        BlockEntityRenderDispatcher blockEntityRenderer = mc.getBlockEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

        transformMatrix(matrixStack, pos);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        BakedModel blockModel = blockRenderer.getBlockModel(blockState);

        if(blockEntity==null){
            renderModel(blockRenderer, buffer, matrixStack,blockState,blockModel);
        } else{
            renderModel(blockEntityRenderer,buffer,matrixStack,blockState,blockEntity);
            renderModel(blockRenderer, buffer, matrixStack,blockState,blockModel);
        }

        cleanUpMatrix(matrixStack);
    }

    protected void prepareMatrix(PoseStack matrixStack) {
        matrixStack.pushPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        prepareLighting(matrixStack);
    }

    protected void transformMatrix(PoseStack matrixStack,BlockPos pos) {
        matrixStack.translate(+posOffsetX+anchorPoint.getOriginX(this),posOffsetY+anchorPoint.getOriginY(this),0);
        matrixStack.scale((float)scale.x,(float)scale.y,(float)scale.z);
        matrixStack.pushPose();

        matrixStack.scale(1, -1, 1);

        matrixStack.mulPose(Axis.ZP.rotationDegrees((float) rot.z));
        matrixStack.mulPose(Axis.XP.rotationDegrees((float) rot.x));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float) rot.y));

        matrixStack.translate(pos.getX()-offset.x,pos.getY()-offset.y,pos.getZ()-offset.z);

    }

    protected void cleanUpMatrix(PoseStack matrixStack) {
        matrixStack.popPose();
        matrixStack.popPose();
    }

    protected void prepareLighting(PoseStack matrixStack) {
        Lighting.setupLevel(new Matrix4f().rotate(180, new Vector3f(1,0,0)));
    }


    protected void renderModel(BlockRenderDispatcher blockRenderer, MultiBufferSource.BufferSource buffer, PoseStack ms, BlockState blockState, BakedModel blockModel) {

        if (blockState.getBlock() == Blocks.AIR) {
            RenderType renderType = Sheets.translucentCullBlockSheet();
            blockRenderer.getModelRenderer()
                    .renderModel(ms.last(), buffer.getBuffer(renderType), blockState, blockModel, 1, 1, 1,
                            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        } else {
            for (RenderType chunkType : RenderType.chunkBufferLayers()) {
                RenderType renderType = chunkType != RenderType.translucent() ? Sheets.cutoutBlockSheet() : Sheets.translucentCullBlockSheet();

                blockRenderer.getModelRenderer()
                    .renderModel(ms.last(), buffer.getBuffer(renderType), blockState, blockModel,
                        1,1, 1,
                            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            }
        }

        buffer.endBatch();
    }

    protected void renderModel(BlockEntityRenderDispatcher blockRenderer, MultiBufferSource buffer, PoseStack ms, BlockState blockState, BlockEntity blockEntity) {
        if (blockState.getBlock() != Blocks.AIR) {
            var renderer = blockRenderer.getRenderer(blockEntity);
            if(renderer!=null)
                renderer.render(blockEntity,0,ms,buffer,LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }


    protected SceneElementBuilder getBuilder() {
        return (SceneElementBuilder) this.builder;
    }

    public enum SourceMode {
        DIRECT_TEXT("direct"),
        TEXT_FILE("text_file");

        final String name;

        SourceMode(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static SceneElement.SourceMode getByName(String name) {
            for(SceneElement.SourceMode i : values()) {
                if (i.getName().equals(name)) {
                    return i;
                }
            }

            return null;
        }
    }

    static float floatParser(String string){
        float x = 0;
        if (string != null) {
            String s = PlaceholderParser.replacePlaceholders(string).replace(" ", "");
            if (MathUtils.isFloat(s)) {
                x = Float.parseFloat(s);
            }
        }
        return x;
    }
}
