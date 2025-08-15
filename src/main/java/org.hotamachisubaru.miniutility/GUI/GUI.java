package org.hotamachisubaru.miniutility.GUI;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;


public class GUI {

    // ユーティリティメニューを開く
    public static void openMenu(Player player) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("メニュー");
            }

            @Override
            public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                return new UtilityMenu(id, inventory, player);
            }
        });
    }

    // ニックネーム変更メニュー
    public static void nicknameMenu(Player player) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("ニックネームを変更");
            }
t
            @Override
            public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                return new NicknameMenu(id, inventory, player);
            }
        });
    }

    // メニューアイテム生成ヘルパーメソッド
    public static ItemStack createMenuItem(Items item, String name, String lore) {
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTagElement("display").putString("Name", Component.Serializer.toJson(Component.literal(name)));
        stack.getOrCreateTagElement("display").putString("Lore", Component.Serializer.toJson(Component.literal(lore)));
        return stack;
    }

    // オーバーロード（lore無し版）も用意
    public static ItemStack createMenuItem(Items item, String name) {
        return createMenuItem(item, name, "");
    }
}
