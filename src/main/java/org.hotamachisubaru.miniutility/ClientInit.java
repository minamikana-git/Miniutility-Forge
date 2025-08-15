package org.hotamachisubaru.miniutility;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import org.hotamachisubaru.miniutility.Menu.MiniutilityMenu;

@Mod.EventBusSubscriber(modid = "miniutility", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {
    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("menu")
                        .executes(ctx -> {
                            Minecraft.getInstance().setScreen(new MiniutilityMenu(0, Minecraft.getInstance().player.getInventory()) {
                                @Override
                                public boolean stillValid(Player player) {
                                    return true;
                                }

                                @Override
                                public ItemStack quickMoveStack(Player player, int i) {
                                    return null;
                                }
                            });
                            return 1;
                        })
        );
    }
}
