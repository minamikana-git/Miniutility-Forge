package org.hotamachisubaru.miniutility.Nickname;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.util.logging.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NicknameDatabase {
    private static final Logger LOGGER = Logger.getLogger(NicknameDatabase.class.getName());
    private final File storageFile;
    private final Map<String, String> nicknameMap = new ConcurrentHashMap<>();

    public NicknameDatabase(File modConfigDir) {
        this.storageFile = new File(modConfigDir, "nicknames.json");
        load();
    }

    public void load() {
        nicknameMap.clear();
        if (storageFile.exists()) {
            try (FileReader reader = new FileReader(storageFile)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
                        nicknameMap.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            } catch (IOException | JsonParseException e) {
                LOGGER.warning("Failed to load nicknames: " + e.getMessage());
            }
        }
    }

    public void saveAll() {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, String> entry : nicknameMap.entrySet()) {
            jsonObject.add(entry.getKey(), new JsonPrimitive(entry.getValue()));
        }
        try (FileWriter writer = new FileWriter(storageFile)) {
            writer.write(jsonObject.toString());
        } catch (IOException e) {
            LOGGER.warning("Failed to save nicknames: " + e.getMessage());
        }
    }

    public String getNickname(String uuid) {
        return nicknameMap.getOrDefault(uuid, "");
    }

    public void setNickname(String uuid, String nickname) {
        nicknameMap.put(uuid, nickname);
        saveAll();
    }

    public void removeNickname(String uuid) {
        nicknameMap.remove(uuid);
        saveAll();
    }

    public void reload() {
        load();
    }
}
