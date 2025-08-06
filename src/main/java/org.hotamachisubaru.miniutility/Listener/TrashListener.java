package org.hotamachisubaru.miniutility.Listener;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.style.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.MiniutilityLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class TrashListener {
    private static final Map<UUID, InventoryMenu> lastTrashBox = new HashMap<>();
    private static final Map<UUID, ItemStack[]> trashBoxCache = new HashMap<>();
    private final MiniutilityLoader plugin;

    public TrashListener(MiniutilityLoader plugin) {
        this.plugin = plugin;
    }

    // ゴミ箱GUIを開く
    public static void openTrashBox(Player player) {
        InventoryMenu trashInventory = new InventoryMenu(player, ContainerLevelAccess.create(player.getCommandSenderWorld(), player.blockPosition()));
        ItemStack confirmButton = new ItemStack(Items.LIME_CONCRETE);
        confirmButton.setHoverName(new TextComponent("捨てる").withStyle(Style.EMPTY.withColor(0xFF0000)));
        trashInventory.setItem(53, confirmButton);
        lastTrashBox.put(player.getUUID(), trashInventory);
        player.containerMenu = trashInventory;
        player.initMenu(trashInventory);
    }

    // 確認画面を開く
    private static void openTrashConfirm(Player player) {
        // ゴミ箱内容を一時保存
        InventoryMenu last = lastTrashBox.get(player.getUUID());
        if (last != null) {
            trashBoxCache.put(player.getUUID(), last.getItems().toArray(new ItemStack[0]));
        }

        InventoryMenu confirmMenu = new InventoryMenu(player, ContainerLevelAccess.create(player.getCommandSenderWorld(), player.blockPosition()));
        confirmMenu.setItem(3, new ItemStack(Items.LIME_CONCRETE).setHoverName(new TextComponent("はい").withStyle(Style.EMPTY.withColor(0x00FF00))));
        confirmMenu.setItem(5, new ItemStack(Items.RED_CONCRETE).setHoverName(new TextComponent("いいえ").withStyle(Style.EMPTY.withColor(0xFF0000))));
        player.containerMenu = confirmMenu;
        player.initMenu(confirmMenu);
    }

    @SubscribeEvent
    public static void onTrashBoxClick(PlayerContainerEvent.Click event) {
        Player player = event.getEntity();
        if (player == null || event.getSlot() == null) return;

        String title = player.containerMenu.getTitle().getString();

        if (title.equals("ゴミ箱")) {
            int rawSlot = event.getSlot().slotNumber;
            ItemStack item = event.getSlot().getStack();
            if (item == null) return;

            // 捨てるボタンは絶対キャンセル
            if (rawSlot == 53 && item.getItem() == Items.LIME_CONCRETE) {
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
        if (title.equals("本当に捨てますか？")) {
            event.setCanceled(true);
            ItemStack item = event.getSlot().getStack();
            if (item == null) return;
            if (item.getItem() == Items.LIME_CONCRETE) {
                // 削除
                InventoryMenu prev = lastTrashBox.get(player.getUUID());
                if (prev != null) {
                    for (int i = 0; i < 53; i++) prev.setItem(i, ItemStack.EMPTY);
                    player.closeContainer();
                    player.sendMessage(new TextComponent("ゴミ箱のアイテムをすべて削除しました。").withStyle(Style.EMPTY.withColor(0x00FF00)), player.getUUID());
                    lastTrashBox.remove(player.getUUID());
                    trashBoxCache.remove(player.getUUID());
                }
            } else if (item.getItem() == Items.RED_CONCRETE) {
                // 復元処理
                ItemStack[] cache = trashBoxCache.get(player.getUUID());
                if (cache != null) {
                    InventoryMenu trashInventory = new InventoryMenu(player, ContainerLevelAccess.create(player.getCommandSenderWorld(), player.blockPosition()));
                    // アイテムを復元（0-52のみ！）
                    for (int i = 0; i < 53; i++) {
                        trashInventory.setItem(i, (i < cache.length) ? cache[i] : ItemStack.EMPTY);
                    }
                    // 53番は必ず「捨てる」ボタン
                    ItemStack confirmButton = new ItemStack(Items.LIME_CONCRETE);
                    confirmButton.setHoverName(new TextComponent("捨てる").withStyle(Style.EMPTY.withColor(0xFF0000)));
                    trashInventory.setItem(53, confirmButton);

                    lastTrashBox.put(player.getUUID(), trashInventory);
                    player.containerMenu = trashInventory;
                    player.initMenu(trashInventory);
                } else {
                    player.closeContainer();
                }
                trashBoxCache.remove(player.getUUID());
                player.sendMessage(new TextComponent("削除をキャンセルしました。").withStyle(Style.EMPTY.withColor(0xFFFF00)), player.getUUID());
            }
        }
    }
}
