// Config.java
package org.hotamachisubaru.miniutility;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "miniutility", bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLE_DOUBLE_JUMP =
            BUILDER.comment("Enable double jump").define("enableDoubleJump", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableDoubleJump;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableDoubleJump = ENABLE_DOUBLE_JUMP.get();
    }
}
