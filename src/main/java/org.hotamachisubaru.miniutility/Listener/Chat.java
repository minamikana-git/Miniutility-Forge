package org.hotamachisubaru.miniutility.Listener;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.server.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.Miniutility;
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

    private final Miniutility mod;
    private final NicknameDatabase nicknameDatabase;
    private final NicknameManager nicknameManager;

    public Chat(Miniutility mod, NicknameDatabase nicknameDatabase, NicknameManager nicknameManager) {
        this.mod = mod;
        this.nicknameDatabase = nicknameDatabase;
        this.nicknameManager = nicknameManager;
    }

    // --- Setter/Getter ---
    public static void setWaitingForNickname(UUID uuid, boolean flag) {
        if (flag) {
            waitingForNickname.put(uuid, true);
        } else {
            waitingForNickname.remove(uuid);
        }
    }

    public static boolean isWaitingForNickname(UUID uuid) {
        return waitingForNickname.containsKey(uuid);
    }

    public static void setWaitingForColorInput(UUID uuid, boolean flag) {
        if (flag) {
            waitingForColorInput.put(uuid, true);
        } else {
            waitingForColorInput.remove(uuid);
        }
    }

    public static boolean isWaitingForColorInput(UUID uuid) {
        return waitingForColorInput.containsKey(uuid);
    }

    public static void setWaitingForExpInput(UUID uuid, boolean flag) {
        if (flag) {
            waitingForExpInput.put(uuid, true);
        } else {
            waitingForExpInput.remove(uuid);
        }
    }

    public static boolean isWaitingForExpInput(UUID uuid) {
        return waitingForExpInput.containsKey(uuid);
    }

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player instanceof FakePlayer) {
            return;
        }

        UUID uuid = player.getUUID();
        String msg = event.getMessage();

        if (isWaitingForNickname(uuid)) {
            handleNicknameInput(event, player, uuid, msg);
            return;
        }

        if (isWaitingForColorInput(uuid)) {
            handleColorInput(event, player, uuid, msg);
            return;
        }

        if (isWaitingForExpInput(uuid)) {
            handleExpInput(event, player, uuid, msg);
            return;
        }

        handleRegularChat(event, player, msg);
    }

    private void handleNicknameInput(ServerChatEvent event, ServerPlayer player, UUID uuid, String msg) {
        event.setCanceled(true);
        if (msg.isEmpty()) {
            player.sendSystemMessage(Component.literal("ニックネームが空です。もう一度入力してください。").withStyle(style -> style.withColor(0xFF5555)));
            return;
        }
        if (msg.length() > 16) {
            player.sendSystemMessage(Component.literal("ニックネームは16文字以内にしてください。").withStyle(style -> style.withColor(0xFF5555)));
            setWaitingForNickname(uuid, false);
            return;
        }
        nicknameDatabase.setNickname(uuid.toString(), msg);
        nicknameManager.applyFormattedDisplayName(player);
        player.sendSystemMessage(Component.literal("✅ ニックネームを「" + msg + "」に設定しました。").withStyle(style -> style.withColor(0x55FF55)));
        setWaitingForNickname(uuid, false);
    }

    private void handleColorInput(ServerChatEvent event, ServerPlayer player, UUID uuid, String msg) {
        event.setCanceled(true);
        if (msg.isEmpty() || msg.length() > 16) {
            player.sendSystemMessage(Component.literal("無効な入力です。色付き表示したいニックネームを16文字以内で入力してください。").withStyle(style -> style.withColor(0xFF5555)));
            setWaitingForColorInput(uuid, false);
            return;
        }
        String translated = msg;
        nicknameDatabase.setNickname(uuid.toString(), translated);
        nicknameManager.applyFormattedDisplayName(player);

        setWaitingForColorInput(uuid, false);
        player.sendSystemMessage(Component.literal("✅ ニックネームの色を変更しました: ")
                .append(Component.literal(translated)).withStyle(style -> style.withColor(0x55FF55)));
    }

    private void handleExpInput(ServerChatEvent event, ServerPlayer player, UUID uuid, String msg) {
        event.setCanceled(true);
        try {
            int inputValue = Integer.parseInt(msg);
            if (inputValue >= 0) {
                player.giveExperienceLevels(inputValue);
                player.sendSystemMessage(Component.literal("経験値レベルに +" + inputValue + " しました。").withStyle(style -> style.withColor(0x55FFFF)));
            } else {
                int currentLevel = player.experienceLevel;
                int target = Math.max(0, currentLevel + inputValue);
                player.setExperienceLevels(target);
                player.sendSystemMessage(Component.literal("経験値レベルから " + (-inputValue) + " 減らしました。").withStyle(style -> style.withColor(0xFF5555)));
            }
        } catch (NumberFormatException e) {
            player.sendSystemMessage(Component.literal("無効な入力です。整数（例: 10 または -5）を入力してください。").withStyle(style -> style.withColor(0xFF5555)));
        }
        setWaitingForExpInput(uuid, false);
    }

    private void handleRegularChat(ServerChatEvent event, ServerPlayer player, String msg) {
        String prefix = "";
        try {
            prefix = NicknameManager.getLuckPermsPrefix(player);
        } catch (Exception ignored) {
        }
        String displayNick = nicknameManager.getNickname(player);

        MutableComponent chat = Component.literal("")
                .append(Component.literal(prefix == null ? "" : prefix))
                .append(Component.literal((displayNick == null || displayNick.isEmpty()) ? player.getGameProfile().getName() : displayNick))
                .append(Component.literal(" > "))
                .append(Component.literal(msg));

        event.setCanceled(true);
        player.server.getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(chat));
    }
}
