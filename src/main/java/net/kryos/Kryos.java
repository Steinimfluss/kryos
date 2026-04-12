package net.kryos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.kryos.event.EventBus;
import net.kryos.feature.FeatureManager;
import net.kryos.gui.ClickGui;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class Kryos implements ModInitializer {
    public static final String MOD_ID = "kryos";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static FeatureManager featureManager;
    public static EventBus eventBus;

    private boolean wasPressed = false;

    @Override
    public void onInitialize() {
        featureManager = new FeatureManager();
        eventBus = new EventBus();
        
        eventBus.subscribe(featureManager);
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean pressed = org.lwjgl.glfw.GLFW.glfwGetKey(
                    Minecraft.getInstance().getWindow().handle(),
                    org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT
            ) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

            if (pressed && !wasPressed) {
                client.setScreen(new ClickGui());
            }

            wasPressed = pressed;
        });
    }
    
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Kryos.MOD_ID, path);
	}
}