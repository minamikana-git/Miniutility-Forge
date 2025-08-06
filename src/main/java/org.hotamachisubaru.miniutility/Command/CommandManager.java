package org.hotamachisubaru.miniutility.Command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.GUI.GUI;
import org.hotamachisubaru.miniutility.MiniutilityLoader;

@Mod.EventBusSubscriber
public class CommandManager {
    private final MiniutilityLoader plugin;

    public CommandManager(MiniutilityLoader plugin) {
        this.plugin = plugin;
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MiniutilityLoader plugin = MiniutilityLoader.getInstance();
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
                    Object miniutility = plugin.getMiniutility();
                    if (miniutility == null) {
                        context.getSource().sendFailure(Component.literal("Miniutility本体がロードされていません。"));
                    } else {
                        plugin.getMiniutility().getNicknameDatabase().reload();
                        context.getSource().sendSuccess(Component.literal("ニックネームデータを再読み込みしました。"), true);
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
                    Object miniutility = plugin.getMiniutility();
                    if (miniutility == null) {
                        context.getSource().sendFailure(Component.literal("Miniutility本体がロードされていません。"));
                        return 1;
                    }
                    Object manager = plugin.getMiniutility().getNicknameManager();
                    boolean enabled = ((org.hotamachisubaru.miniutility.NicknameManager) manager).togglePrefix(player.getUUID());
                    context.getSource().sendSuccess(
                            Component.literal("Prefixの表示が " + (enabled ? "有効" : "無効")), true
                    );
                    return 1;
                })
        );
    }
}