package org.hotamachisubaru.miniutility.Listener;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.MiniutilityLoader;
import org.hotamachisubaru.miniutility.Nickname.NicknameDatabase;
import org.hotamachisubaru.miniutility.Nickname.NicknameManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class Chat {

    private static final Map<UUID, Boolean> waitingForNickname = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> waitingForColorInput = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> waitingForExpInput = new ConcurrentHashMap<>();

    private final MiniutilityLoader plugin;
    private final NicknameDatabase nicknameDatabase;
    private final NicknameManager nicknameManager;

    public Chat(MiniutilityLoader plugin, NicknameDatabase nicknameDatabase, NicknameManager nicknameManager) {
        this.plugin = plugin;
        this.nicknameDatabase = nicknameDatabase;
        this.nicknameManager = nicknameManager;
    }

    // --- Setter/Getter ---
    public static void setWaitingForNickname(UUID uuid, boolean flag) {
        if (flag) waitingForNickname.put(uuid, true);
        else waitingForNickname.remove(uuid);
    }

    public static boolean isWaitingForNickname(UUID uuid) {
        return waitingForNickname.containsKey(uuid);
    }

    public static void setWaitingForColorInput(UUID uuid, boolean flag) {
        if (flag) waitingForColorInput.put(uuid, true);
        else waitingForColorInput.remove(uuid);
    }

    public static boolean isWaitingForColorInput(UUID uuid) {
        return waitingForColorInput.containsKey(uuid);
    }

    public static void setWaitingForExpInput(UUID uuid, boolean flag) {
        if (flag) waitingForExpInput.put(uuid, true);
        else waitingForExpInput.remove(uuid);
    }

    public static boolean isWaitingForExpInput(UUID uuid) {
        return waitingForExpInput.containsKey(uuid);
    }

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        if (event.getPlayer() instanceof FakePlayer) return;

        ServerPlayer player = event.getPlayer();
        UUID uuid = player.getUUID();
        String msg = event.getMessage();

        // ニックネーム入力待機
        if (isWaitingForNickname(uuid)) {
            event.setCanceled(true);
            String nickname = msg;
            if (nickname.isEmpty()) {
                player.sendSystemMessage(new TextComponent("ニックネームが空です。もう一度入力してください。").withStyle(style -> style.withColor(0xFF5555)));
                return;
            }
            if (nickname.length() > 16) {
                player.sendSystemMessage(new TextComponent("ニックネームは16文字以内にしてください。").withStyle(style -> style.withColor(0xFF5555)));
                setWaitingForNickname(uuid, false);
                return;
            }
            nicknameDatabase.setNickname(uuid.toString(), nickname);
            nicknameManager.applyFormattedDisplayName(player);
            player.sendSystemMessage(new TextComponent("✅ ニックネームを「" + nickname + "」に設定しました。").withStyle(style -> style.withColor(0x55FF55)));
            setWaitingForNickname(uuid, false);
            return;
        }

        // 色コード入力待機
        if (isWaitingForColorInput(uuid)) {
            event.setCanceled(true);
            if (msg.isEmpty() || msg.length() > 16) {
                player.sendSystemMessage(new TextComponent("無効な入力です。色付き表示したいニックネームを16文字以内で入力してください。").withStyle(style -> style.withColor(0xFF5555)));
                setWaitingForColorInput(uuid, false);
                return;
            }
            String translated = msg; // 例: &6ほたまち
            nicknameDatabase.setNickname(uuid.toString(), translated);
            nicknameManager.applyFormattedDisplayName(player);

            setWaitingForColorInput(uuid, false);
            player.sendSystemMessage(new TextComponent("✅ ニックネームの色を変更しました: ")
                    .append(new TextComponent(translated)).withStyle(style -> style.withColor(0x55FF55)));
            return;
        }

        // 経験値入力待機
        if (isWaitingForExpInput(uuid)) {
            event.setCanceled(true);
            try {
                int inputValue = Integer.parseInt(msg);
                if (inputValue >= 0) {
                    player.giveExperienceLevels(inputValue);
                    player.sendSystemMessage(new TextComponent("経験値レベルに +" + inputValue + " しました。").withStyle(style -> style.withColor(0x55FFFF)));
                } else {
                    int currentLevel = player.experienceLevel;
                    int target = Math.max(0, currentLevel + inputValue);
                    player.setExperienceLevels(target);
                    player.sendSystemMessage(new TextComponent("経験値レベルから " + (-inputValue) + " 減らしました。").withStyle(style -> style.withColor(0xFF5555)));
                }
            } catch (NumberFormatException e) {
                player.sendSystemMessage(new TextComponent("無効な入力です。整数（例: 10 または -5）を入力してください。").withStyle(style -> style.withColor(0xFF5555)));
            }
            setWaitingForExpInput(uuid, false);
            return;
        }

        // 通常チャット（独自Prefix+Nicknameフォーマット）
        String prefix = "";
        try {
            prefix = NicknameManager.getLuckPermsPrefix(player);
        } catch (Exception ignored) {
        }
        String displayNick = nicknameManager.getNickname(player);

        Component chat = new TextComponent("")
                .append(new TextComponent(prefix == null ? "" : prefix))
                .append(new TextComponent((displayNick == null || displayNick.isEmpty()) ? player.getGameProfile().getName() : displayNick))
                .append(new TextComponent(" > "))
                .append(new TextComponent(msg));

        event.setCanceled(true);
        player.getServer().getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(chat));
    }
}
