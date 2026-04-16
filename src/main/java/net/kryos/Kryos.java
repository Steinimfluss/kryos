package net.kryos;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kryos.config.ConfigManager;
import net.kryos.event.EventBus;
import net.kryos.feature.FeatureManager;
import net.minecraft.resources.Identifier;

public class Kryos implements ModInitializer {
    public static final String MOD_ID = "kryos";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("kryos");

    public static FeatureManager featureManager;
    public static EventBus eventBus;
    public static ConfigManager configManager;

    @Override
    public void onInitialize() {
    	LOGGER.info("Starting...");
    	configManager = new ConfigManager();
    	
        featureManager = new FeatureManager();
        eventBus = new EventBus();
        
        eventBus.subscribe(featureManager);
    	
    	try {
			configManager.loadCurrent();
		} catch (Exception e) {
	    	LOGGER.error("Failed to load config: " + e);
			e.printStackTrace();
		}
        
        ClientLifecycleEvents.CLIENT_STOPPING.register(_ -> {
        	LOGGER.info("Stopping...");
        	
        	try {
				configManager.saveCurrent();
			} catch (Exception e) {
		    	LOGGER.error("Failed to save config: " + e);
				e.printStackTrace();
			}
        });
    }
    
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Kryos.MOD_ID, path);
	}
}