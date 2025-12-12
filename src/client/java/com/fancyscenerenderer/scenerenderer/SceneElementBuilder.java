package com.fancyscenerenderer.scenerenderer;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SceneElementBuilder extends ElementBuilder<SceneElement, SceneEditorElement> {

    public SceneElementBuilder(){
        super("world");
    }

    @Override
    public @NotNull SceneElement buildDefaultInstance() {
        return new SceneElement(this);
    }

    @Override
    public SceneElement deserializeElement(@NotNull SerializedElement serializedElement) {
        SceneElement element = buildDefaultInstance();
        String sourceMode = serializedElement.getValue("source_mode");
        if(sourceMode!=null)
            element.sourceMode = SceneElement.SourceMode.getByName(sourceMode);
        String source = serializedElement.getValue("source");
        if(source!=null)
            element.source = source;

        element.rotXString = serializedElement.getValue("rot_x");
        element.rotYString = serializedElement.getValue("rot_y");
        element.rotZString = serializedElement.getValue("rot_z");

        element.offsetXString = serializedElement.getValue("offset_x");
        element.offsetYString = serializedElement.getValue("offset_y");
        element.offsetZString = serializedElement.getValue("offset_z");

        element.scaleXString = serializedElement.getValue("scale_x");
        element.scaleYString = serializedElement.getValue("scale_y");
        element.scaleZString = serializedElement.getValue("scale_z");

        element.updateRot = Boolean.parseBoolean(serializedElement.getValue("update_rotation"));
        element.updateOffset = Boolean.parseBoolean(serializedElement.getValue("update_offset"));
        element.updateScale = Boolean.parseBoolean(serializedElement.getValue("update_scale"));

        element.updateScene();
        element.updateTransform();
        return element;
    }

    @Override
    protected SerializedElement serializeElement(@NotNull SceneElement sceneElement, @NotNull SerializedElement serializedElement) {
        if(sceneElement.sourceMode!=null)
            serializedElement.putProperty("source_mode", sceneElement.sourceMode.name);
        if(sceneElement.source!=null)
            serializedElement.putProperty("source", sceneElement.source);

        serializedElement.putProperty("rot_x", sceneElement.rotXString);
        serializedElement.putProperty("rot_y", sceneElement.rotYString);
        serializedElement.putProperty("rot_z", sceneElement.rotZString);

        serializedElement.putProperty("offset_x", sceneElement.offsetXString);
        serializedElement.putProperty("offset_y", sceneElement.offsetYString);
        serializedElement.putProperty("offset_z", sceneElement.offsetZString);

        serializedElement.putProperty("scale_x", sceneElement.scaleXString);
        serializedElement.putProperty("scale_y", sceneElement.scaleYString);
        serializedElement.putProperty("scale_z", sceneElement.scaleZString);

        serializedElement.putProperty("update_rotation", Boolean.toString(sceneElement.updateRot));
        serializedElement.putProperty("update_offset", Boolean.toString(sceneElement.updateOffset));
        serializedElement.putProperty("update_scale", Boolean.toString(sceneElement.updateScale));
        return serializedElement;
    }

    @Override
    public @NotNull SceneEditorElement wrapIntoEditorElement(@NotNull SceneElement sceneElement, @NotNull LayoutEditorScreen layoutEditorScreen) {
        return new SceneEditorElement(sceneElement,layoutEditorScreen);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement abstractElement) {
        return Component.literal("Scene");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement abstractElement) {
        return new Component[0];
    }
}
