package org.hotamachisubaru.miniutility.GUI;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;

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
                return new UtilityMenu(id, inventory);
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

            @Override
            public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                return new NicknameMenu(id, inventory);
            }
        });
    }

    // メニューアイテム生成ヘルパーメソッド
    public static ItemStack createMenuItem(Items item, String name, String lore) {
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().putString("displayName", name);
        stack.getOrCreateTag().putString("lore", lore);
        return stack;
    }

    // オーバーロード（lore無し版）も用意
    public static ItemStack createMenuItem(Items item, String name) {
        return createMenuItem(item, name, "");
    }
}
