package com.fancyscenerenderer.scenerenderer;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.ListUtils;
import de.keksuccino.fancymenu.util.input.TextValidators;
import de.keksuccino.fancymenu.util.rendering.ui.contextmenu.v2.ContextMenu;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;



public class SceneEditorElement extends AbstractEditorElement {
    public SceneEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);

    }

    @Override
    public void init() {
        super.init();

        this.addGenericCycleContextMenuEntryTo(this.rightClickMenu, "set_mode",
                ListUtils.of(SceneElement.SourceMode.DIRECT_TEXT, SceneElement.SourceMode.TEXT_FILE),
                consumes -> (consumes instanceof SceneEditorElement),
                consumes -> ((SceneElement)consumes.element).sourceMode,
                (element1, sourceMode) -> {
                    ((SceneElement)element1.element).sourceMode = sourceMode;
                    ((SceneElement)element1.element).source = null;
                },
                (menu, entry, switcherValue) -> {
                    if (switcherValue == SceneElement.SourceMode.DIRECT_TEXT) {
                        return Component.translatable("fancymenu.elements.splash.source_mode.direct");
                    }
                    return Component.translatable("fancymenu.elements.splash.source_mode.text_file");
                });

        this.addTextResourceChooserContextMenuEntryTo(this.rightClickMenu, "set_source_file",
                        SceneEditorElement.class,
                        null,
                        consumes -> consumes.getElement().textFileSupplier,
                        (sceneEditorElement, iTextResourceSupplier) -> {
                            sceneEditorElement.getElement().textFileSupplier = iTextResourceSupplier;
                            if (iTextResourceSupplier != null) sceneEditorElement.getElement().source = iTextResourceSupplier.getSourceWithPrefix();
                        },
                        Component.translatable("fmsr.elements.scene.set_source"),
                        false, null, true, true, true)
                .setIsVisibleSupplier((menu, entry) -> ((SceneElement)this.element).sourceMode == SceneElement.SourceMode.TEXT_FILE)
                .setIcon(ContextMenu.IconFactory.getIcon("text"));

        this.addGenericStringInputContextMenuEntryTo(this.rightClickMenu, "input_direct",
                        consumes -> (consumes instanceof SceneEditorElement),
                        consumes -> ((SceneElement)consumes.element).source,
                        (element1, s) -> {
                            ((SceneElement)element1.element).source = s;
                        },
                        null, false, true, Component.translatable("fmsr.elements.scene.set_source"),
                        false, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
                .setIsVisibleSupplier((menu, entry) -> ((SceneElement)this.element).sourceMode == SceneElement.SourceMode.DIRECT_TEXT)
                .setIcon(ContextMenu.IconFactory.getIcon("text"));

        this.rightClickMenu.addSeparatorEntry("separator_set_transforms").setStackable(true);

        ContextMenu offsetMenu = new ContextMenu();
        this.rightClickMenu.addSubMenuEntry("offset", Component.translatable("fmsr.elements.scene.set_offset"), offsetMenu)
                .setStackable(true)
                .setIcon(ContextMenu.IconFactory.getIcon("move"));
        this.addGenericStringInputContextMenuEntryTo(offsetMenu, "set_offset_x",
                        element -> element.settings.isAdvancedPositioningSupported(),
                        consumes -> ((SceneElement)consumes.element).offsetXString,
                        (element1, f) -> {
                            ((SceneElement)element1.element).offsetXString = f;
                        },
                        null,false,true,Component.translatable("fmsr.elements.scene.set_x"),
                        true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
                .setStackable(true);

        this.addGenericStringInputContextMenuEntryTo(offsetMenu, "set_offset_y",
                        element -> element.settings.isAdvancedPositioningSupported(),
                        consumes -> ((SceneElement)consumes.element).offsetYString,
                        (element1, f) -> {
                            ((SceneElement)element1.element).offsetYString = f;
                        },
                        null,false,true,Component.translatable("fmsr.elements.scene.set_y"),
                        true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
                .setStackable(true);

        this.addGenericStringInputContextMenuEntryTo(offsetMenu, "set_offset_z",
                        element -> element.settings.isAdvancedPositioningSupported(),
                        consumes -> ((SceneElement)consumes.element).offsetZString,
                        (element1, f) -> {
                            ((SceneElement)element1.element).offsetZString = f;
                        },
                        null,false,true,Component.translatable("fmsr.elements.scene.set_z"),
                        true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
                .setStackable(true);

        ContextMenu rotationMenu = new ContextMenu();
        this.rightClickMenu.addSubMenuEntry("rotation", Component.translatable("fmsr.elements.scene.set_rot"), rotationMenu)
                .setStackable(true)
                .setIcon(ContextMenu.IconFactory.getIcon("reload"));
        this.addGenericStringInputContextMenuEntryTo(rotationMenu, "set_rot_x",
                element -> element.settings.isAdvancedPositioningSupported(),
                consumes -> ((SceneElement)consumes.element).rotXString,
                (element1, f) -> {
                    ((SceneElement)element1.element).rotXString = f;
                },
                null,false,true,Component.translatable("fmsr.elements.scene.set_x"),
                true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
            .setStackable(true);

        this.addGenericStringInputContextMenuEntryTo(rotationMenu, "set_rot_y",
                element -> element.settings.isAdvancedPositioningSupported(),
                        consumes -> ((SceneElement)consumes.element).rotYString,
                        (element1, f) -> {
                            ((SceneElement)element1.element).rotYString = f;
                        },
                null,false,true,Component.translatable("fmsr.elements.scene.set_y"),
                true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
            .setStackable(true);

        this.addGenericStringInputContextMenuEntryTo(rotationMenu, "set_rot_z",
                element -> element.settings.isAdvancedPositioningSupported(),
                        consumes -> ((SceneElement)consumes.element).rotZString,
                        (element1, f) -> {
                            ((SceneElement)element1.element).rotZString = f;
                        },
                null,false,true,Component.translatable("fmsr.elements.scene.set_z"),
                true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
            .setStackable(true);

        ContextMenu scaleMenu = new ContextMenu();
        this.rightClickMenu.addSubMenuEntry("scale", Component.translatable("fmsr.elements.scene.set_scale"), scaleMenu)
                .setStackable(true)
                .setIcon(ContextMenu.IconFactory.getIcon("resize"));
        this.addGenericStringInputContextMenuEntryTo(scaleMenu, "set_scale_x",
                element -> element.settings.isAdvancedPositioningSupported(),
                        consumes -> ((SceneElement)consumes.element).scaleXString,
                        (element1, f) -> {
                            ((SceneElement)element1.element).scaleXString = f;
                        },
                null,false,true,Component.translatable("fmsr.elements.scene.set_x"),
                true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
            .setStackable(true);

        this.addGenericStringInputContextMenuEntryTo(scaleMenu, "set_scale_y",
                element -> element.settings.isAdvancedPositioningSupported(),
                        consumes -> ((SceneElement)consumes.element).scaleYString,
                        (element1, f) -> {
                            ((SceneElement)element1.element).scaleYString = f;
                        },
                null,false,true,Component.translatable("fmsr.elements.scene.set_y"),
                true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
            .setStackable(true);

        this.addGenericStringInputContextMenuEntryTo(scaleMenu, "set_scale_z",
                element -> element.settings.isAdvancedPositioningSupported(),
                        consumes -> ((SceneElement)consumes.element).scaleZString,
                        (element1, f) -> {
                            ((SceneElement)element1.element).scaleZString = f;
                        },
                null,false,true,Component.translatable("fmsr.elements.scene.set_z"),
                true, null, TextValidators.NO_EMPTY_STRING_TEXT_VALIDATOR, null)
            .setStackable(true);

        this.rightClickMenu.addSeparatorEntry("separator_set_dynamic").setStackable(true);
        this.addToggleContextMenuEntryTo(this.rightClickMenu, "set_dynamic_rot",
                SceneEditorElement.class,
                consumes -> ((SceneElement)consumes.element).updateRot,
                (element1, f) -> {
                    ((SceneElement)element1.element).updateRot = f;
                },
                "fmsr.elements.scene.dynamic_rot")
            .setStackable(false);
        this.addToggleContextMenuEntryTo(this.rightClickMenu, "set_dynamic_offset",
                SceneEditorElement.class,
                consumes -> ((SceneElement)consumes.element).updateOffset,
                (element1, f) -> {
                    ((SceneElement)element1.element).updateOffset = f;
                },
                "fmsr.elements.scene.dynamic_offset")
            .setStackable(false);
        this.addToggleContextMenuEntryTo(this.rightClickMenu, "set_dynamic_scale",
                SceneEditorElement.class,
                consumes -> ((SceneElement)consumes.element).updateScale,
                (element1, f) -> {
                    ((SceneElement)element1.element).updateScale = f;
                },
                "fmsr.elements.scene.dynamic_scale")
            .setStackable(false);
    }

    public SceneElement getElement(){
        return (SceneElement) this.element;
    }

}
