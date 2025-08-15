package org.hotamachisubaru.miniutility.Listener;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.hotamachisubaru.miniutility.Miniutility;

public class DeathListener {

    private final Miniutility mod;

    public DeathListener(Miniutility mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void saveDeathLocation(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel world)) return;
        BlockPos deathLoc = player.blockPosition().relative(0, 1, 0); // 対応する座標取得

        // Miniutility本体を介して保存
        if (mod.getMiniutility() != null) {
            mod.getMiniutility().setDeathLocation(player.getUUID(), deathLoc, world.dimension().location());
        }

        // 必要であればダブルチェストの設置
        world.getServer().execute(() -> {
            BlockPos chestPos1 = deathLoc;
            BlockPos chestPos2 = chestPos1.east(); // X+1側に位置決定

            // 既にチェストがある場合、処理をスキップ
            if (!world.getBlockState(chestPos1).isAir() || !world.getBlockState(chestPos2).isAir()) return;

            // 両方の位置にチェストを設置
            world.setBlockAndUpdate(chestPos1, Blocks.CHEST.defaultBlockState());
            world.setBlockAndUpdate(chestPos2, Blocks.CHEST.defaultBlockState());

            // チェストにアイテム変換
            BlockEntity blockEntity1 = world.getBlockEntity(chestPos1);
            if (blockEntity1 instanceof ChestBlockEntity chest1) {
                ItemStack[] contents = player.getInventory().items.toArray(new ItemStack[0]);
                for (int i = 0; i < Math.min(contents.length, chest1.getContainerSize()); i++) {
                    if (!contents[i].isEmpty()) {
                        chest1.setItem(i, contents[i].copy());
                    }
                }
            }
        });
    }
}
