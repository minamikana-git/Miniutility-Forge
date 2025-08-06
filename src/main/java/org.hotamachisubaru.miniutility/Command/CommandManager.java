package org.hotamachisubaru.miniutility.Command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.GUI.GUI;
import org.hotamachisubaru.miniutility.Miniutility;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class CommandManager {
    private static Miniutility mod = new Miniutility();

    public CommandManager(Miniutility mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(
                Commands.literal("menu").executes(context -> {
                    Object sender = context.getSource().getEntity();
                    if (sender instanceof ServerPlayer player) {
                        GUI.openMenu(player);
                    } else {
                        context.getSource().sendFailure(Component.literal("プレイヤーのみ使用できます。"));
                    }
                    return 1;
                })
        );

        event.getServer().getCommands().getDispatcher().register(
                Commands.literal("load").executes(context -> {
                    if (mod == null) {
                        context.getSource().sendFailure(Component.literal("Miniutility本体がロードされていません。"));
                    } else {
                        mod.getNicknameDatabase().reload();
                        context.getSource().sendSuccess(() -> Component.literal("ニックネームデータを再読み込みしました。"), true);
                    }
                    return 1;
                })
        );

        event.getServer().getCommands().getDispatcher().register(
                Commands.literal("prefixtoggle").executes(context -> {
                    Object sender = context.getSource().getEntity();
                    if (!(sender instanceof ServerPlayer player)) {
                        context.getSource().sendFailure(Component.literal("プレイヤーのみ実行可能です。"));
                        return 1;
                    }
                    if (mod == null) {
                        context.getSource().sendFailure(Component.literal("Miniutility本体がロードされていません。"));
                        return 1;
                    }
                    boolean enabled = mod.getNicknameManager().togglePrefix(player.getUUID());
                    context.getSource().sendSuccess(
                            (Supplier<Component>) Component.literal(new StringBuilder().append("Prefixの表示が ").append(enabled ? "有効" : "無効").toString()), true
                    );
                    return 1;
                })
        );
    }
}