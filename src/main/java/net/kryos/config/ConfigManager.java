package net.kryos.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.kryos.Kryos;
import net.kryos.feature.Feature;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.Setting;

public class ConfigManager {
	public void save(Path path) throws IOException {
	    Path parent = path.getParent();
	    if (parent != null) {
	        java.nio.file.Files.createDirectories(parent);
	    }

	    if (!java.nio.file.Files.exists(path)) {
	        java.nio.file.Files.createFile(path);
	    }

	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    JsonObject root = new JsonObject();
	    JsonArray featuresArray = new JsonArray();

	    for (Feature feature : Kryos.featureManager.getFeatures()) {
	        JsonObject featureNode = new JsonObject();

	        featureNode.addProperty("name", feature.name);
	        featureNode.addProperty("enabled", feature.isEnabled());
	        featureNode.addProperty("key", feature.getKey());

	        JsonObject settingsNode = new JsonObject();
	        for (Setting setting : feature.getSettings()) {

	            if (setting instanceof BooleanSetting bs) {
	                settingsNode.addProperty(setting.name, bs.enabled);
	            }

	            else if (setting instanceof ModeSetting ms) {
	                settingsNode.addProperty(setting.name, ms.getValue());
	            }

	            else if (setting instanceof NumberSetting<?> ns) {
	                settingsNode.addProperty(setting.name, ns.getValue().doubleValue());
	            }
	        }

	        featureNode.add("settings", settingsNode);
	        featuresArray.add(featureNode);
	    }

	    root.add("features", featuresArray);

	    Files.write(
	            gson.toJson(root).getBytes(StandardCharsets.UTF_8),
	            path.toFile()
	    );
	}

	
	public void load(Path path) throws IOException {
	    if (!path.toFile().exists())
	        return;

	    Gson gson = new Gson();
	    String json = Files.asByteSource(path.toFile())
	            .asCharSource(StandardCharsets.UTF_8)
	            .read();

	    JsonObject root = gson.fromJson(json, JsonObject.class);
	    if (root == null || !root.has("features"))
	        return;

	    JsonArray featuresArray = root.getAsJsonArray("features");

	    for (JsonElement element : featuresArray) {
	        JsonObject featureNode = element.getAsJsonObject();

	        String name = featureNode.get("name").getAsString();
	        boolean enabled = featureNode.get("enabled").getAsBoolean();
	        int key = featureNode.get("key").getAsInt();

	        Feature feature = Kryos.featureManager.getFeatureByName(name);
	        if (feature == null)
	            continue;

	        feature.setEnabled(enabled);
	        feature.setKey(key);

	        if (featureNode.has("settings")) {
	            JsonObject settingsNode = featureNode.getAsJsonObject("settings");

	            for (Setting setting : feature.getSettings()) {

	                if (!settingsNode.has(setting.name))
	                    continue;

	                if (setting instanceof BooleanSetting bs) {
	                    bs.enabled = settingsNode.get(setting.name).getAsBoolean();
	                }

	                else if (setting instanceof ModeSetting ms) {
	                    ms.setValue(settingsNode.get(setting.name).getAsString());
	                }

	                else if (setting instanceof NumberSetting<?> ns) {
	                    ns.setValueFromDouble(settingsNode.get(setting.name).getAsDouble());
	                }
	            }
	        }
	    }
	}

	public void loadCurrent() throws IOException {
		try {
			load(Kryos.CONFIG_PATH.resolve("current.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveCurrent() throws IOException {
		try {
			save(Kryos.CONFIG_PATH.resolve("current.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}