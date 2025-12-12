package com.fancyscenerenderer;

import com.fancyscenerenderer.commands.AllCommands;
import com.fancyscenerenderer.scenerenderer.SceneElementBuilder;
import de.keksuccino.fancymenu.customization.element.ElementRegistry;
import net.fabricmc.api.ClientModInitializer;

public class FancySceneRendererClient implements ClientModInitializer {

    public static final SceneElementBuilder WORLD_VIEWER = new SceneElementBuilder();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ElementRegistry.register(WORLD_VIEWER);

        AllCommands.register();
	}
}