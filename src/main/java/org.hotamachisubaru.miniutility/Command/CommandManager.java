package org.hotamachisubaru.miniutility.Command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.Menu.MiniutilityMenu;
import org.hotamachisubaru.miniutility.Miniutility;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
@Mod.EventBusSubscriber(modid = "miniutility")
public class CommandManager {

    private static Miniutility modInstance;

    // Miniutilityのインスタンスをセット（MODロード時に呼ぶ想定）
    public static void setModInstance(Miniutility mod) {
        modInstance = mod;
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("menu").executes(context -> {
                    if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                        context.getSource().sendFailure(Component.literal("プレイヤーのみ使用できます。"));
                        return 1;
                    }
                    // ForgeでGUI（Screen）を開くにはクライアントへパケット送信が必要
                    // まずはチャット通知例
                    player.sendSystemMessage(Component.literal("[Miniutility] メニューを開きます（GUI呼び出しはパケットで実装）"));
                    player.openMenu(new SimpleMenuProvider((id, inv, p) -> new MiniutilityMenu(id, inv) {
                        @Override
                        public ItemStack quickMoveStack(Player player, int i) {
                            return null;
                        }
                    }, Component.literal("メニュー")));

                    return 1;
                })
        );

        event.getDispatcher().register(
                Commands.literal("load").executes(context -> {
                    if (modInstance == null) {
                        context.getSource().sendFailure(Component.literal("Miniutility本体がロードされていません。"));
                    } else {
                        modInstance.getNicknameDatabase().reload();
                        context.getSource().sendSuccess(() -> Component.literal("ニックネームデータを再読み込みしました。"), true);
                    }
                    return 1;
                })
        );

        event.getDispatcher().register(
                Commands.literal("prefixtoggle").executes(context -> {
                    if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                        context.getSource().sendFailure(Component.literal("プレイヤーのみ実行可能です。"));
                        return 1;
                    }
                    if (modInstance == null) {
                        context.getSource().sendFailure(Component.literal("Miniutility本体がロードされていません。"));
                        return 1;
                    }
                    boolean enabled = modInstance.getNicknameManager().togglePrefix(player.getUUID());
                    context.getSource().sendSuccess(
                            (Supplier<Component>) Component.literal("Prefixの表示が " + (enabled ? "有効" : "無効")), true
                    );
                    return 1;
                })
        );
    }
}
