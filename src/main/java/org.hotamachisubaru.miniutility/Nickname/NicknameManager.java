package org.hotamachisubaru.miniutility.Nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hotamachisubaru.miniutility.MiniutilityLoader;
import org.hotamachisubaru.miniutility.util.FoliaUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NicknameManager {
    private final MiniutilityLoader plugin;
    private final NicknameDatabase db;
    private final Map<UUID, Boolean> prefixEnabledMap = new ConcurrentHashMap<>();

    public NicknameManager(MiniutilityLoader plugin, NicknameDatabase db) {
        this.plugin = plugin;
        this.db = db;
    }

    public String getNickname(Object playerObject) {
        if (playerObject instanceof Player player) {
            return db.getNickname(player.getUniqueId().toString());
        } else if (playerObject instanceof ServerPlayer forgePlayer) {
            return db.getNickname(forgePlayer.getUUID().toString());
        }
        return "";
    }

    public void setNickname(Object playerObject, String nickname) {
        if (playerObject instanceof Player player) {
            db.setNickname(player.getUniqueId().toString(), nickname);
            applyFormattedDisplayName(player);
        } else if (playerObject instanceof ServerPlayer forgePlayer) {
            db.setNickname(forgePlayer.getUUID().toString(), nickname);
            applyFormattedDisplayName(forgePlayer);
        }
    }

    public void clearNickname(Object playerObject) {
        if (playerObject instanceof Player player) {
            db.removeNickname(player.getUniqueId().toString());
            applyFormattedDisplayName(player);
        } else if (playerObject instanceof ServerPlayer forgePlayer) {
            db.removeNickname(forgePlayer.getUUID().toString());
            applyFormattedDisplayName(forgePlayer);
        }
    }

    public static String getLuckPermsPrefix(Object playerObject) {
        try {
            if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) return "";
            if (playerObject instanceof Player player) {
                net.luckperms.api.cacheddata.CachedMetaData metaData =
                        net.luckperms.api.LuckPermsProvider.get().getPlayerAdapter(Player.class).getMetaData(player);
                return metaData.getPrefix() == null ? "" : metaData.getPrefix();
            }
        } catch (Throwable e) {
            return "";
        }
        return ""; // Forge implementation of LuckPerms not supported directly in this example
    }

    public void applyFormattedDisplayName(Object playerObject) {
        boolean showPrefix;
        String prefix = "";
        String nickname = "";
        String display = "";

        if (playerObject instanceof Player player) {
            showPrefix = prefixEnabledMap.getOrDefault(player.getUniqueId(), true);
            prefix = showPrefix ? getLuckPermsPrefix(player) : "";
            nickname = getNickname(player);

            if (prefix != null && !prefix.isEmpty()) display += prefix;
            if (nickname != null && !nickname.isEmpty()) display += nickname;
            else display += player.getName();

            display = display.replace('§', '&');
            Component comp = LegacyComponentSerializer.legacyAmpersand().deserialize(display);

            FoliaUtil.runAtPlayer(plugin, player, () -> {
                player.displayName(comp);
                player.playerListName(comp);
                player.customName(comp);
                player.setCustomNameVisible(true);
            });
        } else if (playerObject instanceof ServerPlayer forgePlayer) {
            showPrefix = prefixEnabledMap.getOrDefault(forgePlayer.getUUID(), true);
            prefix = showPrefix ? getLuckPermsPrefix(forgePlayer) : "";
            nickname = getNickname(forgePlayer);

            if (prefix != null && !prefix.isEmpty()) display += prefix;
            if (nickname != null && !nickname.isEmpty()) display += nickname;
            else display += forgePlayer.getName().getString();

            display = display.replace('§', '&');
            // Forge: Modify player display name logic here if needed
        }
    }

    public boolean togglePrefix(@NotNull UUID uniqueId) {
        boolean current = prefixEnabledMap.getOrDefault(uniqueId, true);
        prefixEnabledMap.put(uniqueId, !current);
        return !current;
    }
}
