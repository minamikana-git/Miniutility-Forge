package org.hotamachisubaru.miniutility;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "miniutility")
public class EventHandlers {
    @SubscribeEvent
    public static void onPlayerDeath(net.minecraftforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("[Miniutility] 死亡を検知しました"));
        }
    }
    // チャットイベントはバージョン依存なので1.20/1.21でイベント名が違う場合あり
}
