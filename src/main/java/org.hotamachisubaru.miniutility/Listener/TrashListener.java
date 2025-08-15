package org.hotamachisubaru.miniutility.Listener;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.style.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.Miniutility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class TrashListener {
    private static final Map<UUID, InventoryMenu> lastTrashBox = new HashMap<>();
    private static final Map<UUID, ItemStack[]> trashBoxCache = new HashMap<>();
    private final Miniutility mod;

    public TrashListener(Miniutility mod) {
        this.mod = mod;
    }

    // ゴミ箱GUIを開く
    public static void openTrashBox(ServerPlayer player) {
        InventoryMenu trashInventory = new InventoryMenu(player, ContainerLevelAccess.create(player.getLevel(), player.blockPosition()));
        ItemStack confirmButton = new ItemStack(Items.LIME_CONCRETE);
        confirmButton.setHoverName(Component.literal("捨てる").withStyle(Style.EMPTY.withColor(0xFF0000)));
        trashInventory.setItem(53, confirmButton);
        lastTrashBox.put(player.getUUID(), trashInventory);
        player.containerMenu = trashInventory;
        player.initMenu(trashInventory);
    }

    // 確認画面を開く
    private static void openTrashConfirm(ServerPlayer player) {
        // ゴミ箱内容を一時保存
        InventoryMenu last = lastTrashBox.get(player.getUUID());
        if (last != null) {
            trashBoxCache.put(player.getUUID(), last.getItems().toArray(new ItemStack[0]));
        }

        InventoryMenu confirmMenu = new InventoryMenu(player, ContainerLevelAccess.create(player.getLevel(), player.blockPosition()));
        confirmMenu.setItem(3, new ItemStack(Items.LIME_CONCRETE).setHoverName(Component.literal("はい").withStyle(Style.EMPTY.withColor(0x00FF00))));
        confirmMenu.setItem(5, new ItemStack(Items.RED_CONCRETE).setHoverName(Component.literal("いいえ").withStyle(Style.EMPTY.withColor(0xFF0000))));
        player.containerMenu = confirmMenu;
        player.initMenu(confirmMenu);
    }

    @SubscribeEvent
    public static void onTrashBoxClick(PlayerContainerEvent.Click event) {
        ServerPlayer player = event.getEntity() instanceof ServerPlayer ? (ServerPlayer) event.getEntity() : null;
        if (player == null || event.getSlot() == null) return;

        MutableComponent title = player.containerMenu.getTitle();
        if (title == null) return;

        String titleText = title.getString();

        if (titleText.equals("ゴミ箱")) {
            int rawSlot = event.getSlot().index;
            ItemStack item = event.getSlot().getItem();
            if (item.isEmpty()) return;

            // 捨てるボタンは絶対キャンセル
            if (rawSlot == 53 && item.is(Items.LIME_CONCRETE)) {
                event.setCanceled(true);
                openTrashConfirm(player);
                return;
            }

            // ゴミ箱上段スロット (0-53) かつ「捨てるボタン以外」
            if (rawSlot >= 0 && rawSlot < 53) {
                event.setCanceled(false);
                return;
            }

            // プレイヤーのインベントリ側（下段）
            if (rawSlot >= 54) {
                event.setCanceled(false);
                return;
            }

            // その他はキャンセル
            event.setCanceled(true);
            return;
        }

        // --- 確認画面 ---
        if (titleText.equals("本当に捨てますか？")) {
            event.setCanceled(true);
            ItemStack item = event.getSlot().getItem();
            if (item.isEmpty()) return;

            if (item.is(Items.LIME_CONCRETE)) {
                // 削除
                InventoryMenu prev = lastTrashBox.get(player.getUUID());
                if (prev != null) {
                    for (int i = 0; i < 53; i++) prev.setItem(i, ItemStack.EMPTY);
                    player.closeContainer();
                    player.sendSystemMessage(Component.literal("ゴミ箱のアイテムをすべて削除しました。").withStyle(Style.EMPTY.withColor(0x00FF00)));
                    lastTrashBox.remove(player.getUUID());
                    trashBoxCache.remove(player.getUUID());
                }
            } else if (item.is(Items.RED_CONCRETE)) {
                // 復元処理
                ItemStack[] cache = trashBoxCache.get(player.getUUID());
                if (cache != null) {
                    InventoryMenu trashInventory = new InventoryMenu(player, ContainerLevelAccess.create(player.getLevel(), player.blockPosition()));
                    // アイテムを復元（0-52のみ！）
                    for (int i = 0; i < 53; i++) {
                        trashInventory.setItem(i, (i < cache.length) ? cache[i] : ItemStack.EMPTY);
                    }
                    // 53番は必ず「捨てる」ボタン
                    ItemStack confirmButton = new ItemStack(Items.LIME_CONCRETE);
                    confirmButton.setHoverName(Component.literal("捨てる").withStyle(Style.EMPTY.withColor(0xFF0000)));
                    trashInventory.setItem(53, confirmButton);

                    lastTrashBox.put(player.getUUID(), trashInventory);
                    player.containerMenu = trashInventory;
                    player.initMenu(trashInventory);
                } else {
                    player.closeContainer();
                }
                trashBoxCache.remove(player.getUUID());
                player.sendSystemMessage(Component.literal("削除をキャンセルしました。").withStyle(Style.EMPTY.withColor(0xFFFF00)));
            }
        }
    }
}
