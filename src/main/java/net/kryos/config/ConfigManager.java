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
import net.kryos.feature.Keybind;
import net.kryos.feature.setting.Setting;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.NumberSetting;

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

            if (feature.getKey() != null) {
                JsonObject keyNode = new JsonObject();
                keyNode.addProperty("input", feature.getKey().getInput());
                keyNode.addProperty("scancode", feature.getKey().getScancode());
                featureNode.add("key", keyNode);
            }

            JsonObject settingsNode = new JsonObject();
            for (Setting setting : feature.getSettings()) {
                settingsNode.add(setting.getName(), saveSetting(setting));
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

    private JsonObject saveSetting(Setting setting) {
        JsonObject node = new JsonObject();

        if (setting instanceof BooleanSetting bs) {
            node.addProperty("value", bs.enabled);
        } else if (setting instanceof ModeSetting ms) {
            node.addProperty("value", ms.getValue().getName());
        } else if (setting instanceof NumberSetting<?> ns) {
            node.addProperty("value", ns.getValue().doubleValue());
        }

        if (setting.hasSettings()) {
            JsonObject sub = new JsonObject();
            for (Setting child : setting.getSettings()) {
                sub.add(child.getName(), saveSetting(child));
            }
            node.add("subsettings", sub);
        }

        return node;
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

            Feature feature = Kryos.featureManager.getFeatureByName(name);
            if (feature == null)
                continue;

            JsonObject keyNode = featureNode.getAsJsonObject("key");
            if (keyNode != null) {
                int input = keyNode.get("input").getAsInt();
                int scancode = keyNode.get("scancode").getAsInt();
                feature.setKey(new Keybind(input, scancode));
            }

            feature.setEnabled(enabled);

            if (featureNode.has("settings")) {
                JsonObject settingsNode = featureNode.getAsJsonObject("settings");

                for (Setting setting : feature.getSettings()) {
                    if (settingsNode.has(setting.getName())) {
                        loadSetting(setting, settingsNode.getAsJsonObject(setting.getName()));
                    }
                }
            }
        }
    }

    private void loadSetting(Setting setting, JsonObject node) {
        if (setting instanceof BooleanSetting bs) {
            bs.enabled = node.get("value").getAsBoolean();
        } else if (setting instanceof ModeSetting ms) {
            ms.setValue(node.get("value").getAsString());
        } else if (setting instanceof NumberSetting<?> ns) {
            ns.setValueFromDouble(node.get("value").getAsDouble());
        }

        if (node.has("subsettings")) {
            JsonObject sub = node.getAsJsonObject("subsettings");
            for (Setting child : setting.getSettings()) {
                if (sub.has(child.getName())) {
                    loadSetting(child, sub.getAsJsonObject(child.getName()));
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
