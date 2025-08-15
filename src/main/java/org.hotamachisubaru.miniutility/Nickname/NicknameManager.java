package org.hotamachisubaru.miniutility.Nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacytext.LegacyComponentSerializer;
import net.minecraft.server.level.ServerPlayer;
import org.hotamachisubaru.miniutility.Miniutility;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NicknameManager {
    private final Miniutility mod;
    private final NicknameDatabase db;
    private final Map<UUID, Boolean> prefixEnabledMap = new ConcurrentHashMap<>();

    public NicknameManager(Miniutility mod, NicknameDatabase db) {
        this.mod = mod;
        this.db = db;
    }

    public String getNickname(Object playerObject) {
        if (playerObject instanceof ServerPlayer forgePlayer) {
            return db.getNickname(forgePlayer.getUUID().toString());
        }
        return "";
    }

    public void setNickname(Object playerObject, String nickname) {
        if (playerObject instanceof ServerPlayer forgePlayer) {
            db.setNickname(forgePlayer.getUUID().toString(), nickname);
            applyFormattedDisplayName(forgePlayer);
        }
    }

    public void clearNickname(Object playerObject) {
        if (playerObject instanceof ServerPlayer forgePlayer) {
            db.removeNickname(forgePlayer.getUUID().toString());
            applyFormattedDisplayName(forgePlayer);
        }
    }

    public void applyFormattedDisplayName(Object playerObject) {
        if (playerObject instanceof ServerPlayer forgePlayer) {
            String nickname = getNickname(forgePlayer);
            String display = (nickname != null && !nickname.isEmpty())
                    ? nickname
                    : forgePlayer.getName().getString();

            display = display.replace('§', '&');
            Component comp = LegacyComponentSerializer.legacyAmpersand().deserialize(display);

            forgePlayer.setCustomName((net.minecraft.network.chat.Component) comp);
            forgePlayer.setCustomNameVisible(true);
        }
    }

    public boolean togglePrefix(@NotNull UUID uniqueId) {
        boolean current = prefixEnabledMap.getOrDefault(uniqueId, true);
        prefixEnabledMap.put(uniqueId, !current);
        return !current;
    }
}
