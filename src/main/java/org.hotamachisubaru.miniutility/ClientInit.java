package org.hotamachisubaru.miniutility;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = "miniutility", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {
    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("menu")
                        .executes(ctx -> {
                            net.minecraft.client.Minecraft.getInstance().setScreen(new Menu());
                            return 1;
                        })
        );
    }
}
