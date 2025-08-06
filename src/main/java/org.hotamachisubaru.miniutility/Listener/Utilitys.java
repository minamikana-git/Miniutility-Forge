package org.hotamachisubaru.miniutility.Listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.GUI.GUI;
import org.hotamachisubaru.miniutility.MiniutilityLoader;
import org.hotamachisubaru.miniutility.Nickname.NicknameManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public class Utilitys {

    // 再ワープ防止
    private static final Set<UUID> recentlyTeleported = new HashSet<>();
    private final MiniutilityLoader plugin;
    private final NicknameManager nicknameManager;

    public Utilitys(MiniutilityLoader plugin, NicknameManager nicknameManager) {
        this.plugin = plugin;
        this.nicknameManager = nicknameManager;
    }

    @SubscribeEvent
    public void handlePlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        String title = ""; // You might need a replacement for Forge title handling

        switch (title) {
            case "メニュー" -> handleUtilityBox(player, event);
            case "ゴミ箱" -> handleTrashBox(player, event);
            case "本当に捨てますか？" -> TrashConfirm(player, event);
            case "ニックネームを変更" -> handleNicknameMenu(player, event);
            default -> { /* 何もしない */ }
        }
    }

    private void handleUtilityBox(ServerPlayer player, PlayerInteractEvent.RightClickBlock event) {
        // Add Forge-compatible implementation
        player.displayClientMessage(Component.text("メニューが選択されました。").color(NamedTextColor.GREEN), false);
    }

    private void handleTrashBox(ServerPlayer player, PlayerInteractEvent.RightClickBlock event) {
        // Add Forge-compatible implementation
        player.displayClientMessage(Component.text("ゴミ箱が選択されました。").color(NamedTextColor.GREEN), false);
    }

    private void TrashConfirm(ServerPlayer player, PlayerInteractEvent.RightClickBlock event) {
        // Add Forge-compatible implementation
        player.displayClientMessage(Component.text("アイテム削除が確認されました。").color(NamedTextColor.GREEN), false);
    }

    private void handleNicknameMenu(ServerPlayer player, PlayerInteractEvent.RightClickBlock event) {
        // Add Forge-compatible implementation
        player.displayClientMessage(Component.text("ニックネーム変更が選択されました。").color(NamedTextColor.GREEN), false);
    }

    private void teleportToDeathLocation(ServerPlayer player) {
        if (plugin.getMiniutility() == null) {
            player.displayClientMessage(Component.text("プラグイン初期化中です。").color(NamedTextColor.RED), false);
            return;
        }
        // Use Forge-compatible teleport and death location logic
    }
}
