package org.hotamachisubaru.miniutility.Listener;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.Miniutility;
import org.hotamachisubaru.miniutility.Nickname.NicknameManager;

@Mod.EventBusSubscriber
public class NicknameListener {
    private final NicknameManager nicknameManager;
    private final Miniutility mod;

    public NicknameListener(Miniutility mod) {
        this.mod = mod;
        this.nicknameManager = nicknameManager;
    }

    @SubscribeEvent
    public void onNicknameMenuClick(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (item.isEmpty()) return;

        Item type = item.getItem();
        if (type == net.minecraft.world.item.Items.PAPER) {
            player.sendSystemMessage(Component.literal("新しいニックネームをチャットに入力してください。").withStyle(style -> style.withColor(0x00FFFF)));
            Chat.setWaitingForNickname(player.getUUID(), true);
        } else if (type == net.minecraft.world.item.Items.NAME_TAG) {
            player.sendSystemMessage(Component.literal("色付きのニックネームをチャットで入力してください。例: &6ほたまち").withStyle(style -> style.withColor(0x00FFFF)));
            Chat.setWaitingForColorInput(player.getUUID(), true);
        } else if (type == net.minecraft.world.item.Items.BARRIER) {
            mod.getMiniutility().getNicknameDatabase().removeNickname(player.getUUID().toString());
            nicknameManager.applyFormattedDisplayName(player);
            player.sendSystemMessage(Component.literal("ニックネームをリセットしました。").withStyle(style -> style.withColor(0x00FF00)));
        } else {
            player.sendSystemMessage(Component.literal("無効な選択です。").withStyle(style -> style.withColor(0xFF0000)));
        }
        event.setCanceled(true);
    }

    public static void openNicknameMenu(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("現在、ForgeではGUIを開くことはサポートされていません。"));
    }
}
