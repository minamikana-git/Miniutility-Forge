package org.hotamachisubaru.miniutility.Listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import org.hotamachisubaru.miniutility.Miniutility;

@Mod.EventBusSubscriber
public abstract class Menu extends AbstractContainerMenu {

    private final Miniutility mod;

    public Menu(Miniutility mod) {
        super(this,mod);
        this.mod = mod;
    }

    @SubscribeEvent
    public static void handleInventoryClick(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var clickedItem = event.getItemStack();

        if (clickedItem.isEmpty() || clickedItem.getItem() == null) return;

        String title = PlainTextComponentSerializer.plainText()
                .serialize(Component.text(event.getHand().name())).trim();

        switch (title) {
            case "メニュー" -> {
                event.setCanceled(true);
                handleUtilityBox(player, clickedItem);
            }
            default -> {
            }
        }
    }

    private static void handleUtilityBox(ServerPlayer player, net.minecraft.world.item.ItemStack clickedItem) {
        var itemType = clickedItem.getItem();

        if (itemType == net.minecraft.world.item.Items.ARMOR_STAND) {
            teleportToDeathLocation(player);
        } else if (itemType == net.minecraft.world.item.Items.ENDER_CHEST) {
            ItemHandlerHelper.giveItemToPlayer(player, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ENDER_CHEST));
        } else if (itemType == net.minecraft.world.item.Items.CRAFTING_TABLE) {
            ItemHandlerHelper.giveItemToPlayer(player, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.CRAFTING_TABLE));
        } else if (itemType == net.minecraft.world.item.Items.DROPPER) {
            // Implement TrashListener equivalent for Forge here
        } else if (itemType == net.minecraft.world.item.Items.NAME_TAG) {
            // Implement NicknameListener equivalent for Forge here
        } else if (itemType == net.minecraft.world.item.Items.CREEPER_HEAD) {
            // Implement creeper protection toggle functionality here for Forge
        } else if (itemType == net.minecraft.world.item.Items.EXPERIENCE_BOTTLE) {
            // Handle experience input for Forge
        } else if (itemType == net.minecraft.world.item.Items.COMPASS) {
            var currentMode = player.gameMode.getGameModeForPlayer();
            if (currentMode.isSurvival()) {
                player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
                player.displayClientMessage(Component.text("ゲームモードをクリエイティブに変更しました。").color(NamedTextColor.GREEN), false);
            } else {
                player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
                player.displayClientMessage(Component.text("ゲームモードをサバイバルに変更しました。").color(NamedTextColor.GREEN), false);
            }
        } else {
            player.displayClientMessage(Component.text("このアイテムにはアクションが設定されていません。").color(NamedTextColor.RED), false);
        }
    }

    public static void teleportToDeathLocation(ServerPlayer player) {
        var deathLocation = player.getLastDeathLocation().orElse(null);
        if (deathLocation == null) {
            player.displayClientMessage(Component.text("死亡地点が見つかりません。").color(NamedTextColor.RED), false);
            return;
        }
        var pos = deathLocation.pos();
        player.teleportTo(player.getLevel(), pos.x(), pos.y(), pos.z(), player.getYRot(), player.getXRot());
        player.displayClientMessage(Component.text("死亡地点にワープしました。").color(NamedTextColor.GREEN), false);
    }
}
