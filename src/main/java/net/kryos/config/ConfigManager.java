package net.kryos.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.kryos.Kryos;
import net.kryos.feature.Feature;
import net.kryos.setting.Setting;

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

            featureNode.addProperty("id", feature.getId());
            featureNode.addProperty("enabled", feature.isEnabled());
            Optional<Integer> key = feature.getKey();
            
            if(key.isPresent())
            	featureNode.addProperty("key", key.get());

            JsonArray settingsArray = new JsonArray();
            
            for(Setting<?> setting : feature.getSettings()) {
                JsonObject settingNode = new JsonObject();
            	settingNode.addProperty("id", setting.getId());
            	settingNode.addProperty("value", setting.getValueString());
                settingsArray.add(settingNode);
            }
            
            featureNode.add("settings", settingsArray);
            featuresArray.add(featureNode);
        }

        root.add("features", featuresArray);

        Files.write(gson.toJson(root).getBytes(StandardCharsets.UTF_8), path.toFile());
    }

    public void load(Path path) throws IOException {
        if (!path.toFile().exists())
            return;

        Gson gson = new Gson();
        String json = Files.asByteSource(path.toFile())
                .asCharSource(StandardCharsets.UTF_8)
                .read();

        JsonObject root = gson.fromJson(json, JsonObject.class);

        JsonArray featuresArray = root.getAsJsonArray("features");

        for (JsonElement featureElement : featuresArray) {
            JsonObject featureNode = featureElement.getAsJsonObject();

            String id = featureNode.get("id").getAsString();
            boolean enabled = featureNode.get("enabled").getAsBoolean();

            Feature feature = Kryos.featureManager.getFeaturesById(id).orElseThrow();

            feature.setEnabled(enabled);
            
            JsonElement keyElement = featureNode.get("key");

            if (keyElement != null && !keyElement.isJsonNull()) {
                int key = keyElement.getAsInt();
                feature.setKey(Optional.of(key));
            }

            JsonArray settingsArray = featureNode.getAsJsonArray("settings");
            for(JsonElement settingElement : settingsArray) {
                JsonObject settingNode = settingElement.getAsJsonObject();
                
                String settingId = settingNode.get("id").getAsString();
                String settingValue = settingNode.get("value").getAsString();
            	
                Optional<Setting<?>> setting = feature.getSettingById(settingId);
                
                if(setting.isPresent())
                	setting.get().setValueString(settingValue);
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
