package org.hotamachisubaru.miniutility;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "miniutility", bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec CLIENT_SPEC;
    private static final ForgeConfigSpec.BooleanValue ENABLE_DOUBLE_JUMP;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        ENABLE_DOUBLE_JUMP = builder.comment("ダブルジャンプを有効にする")
                .define("enableDoubleJump", true);
        CLIENT_SPEC = builder.build();
    }

    public static boolean enableDoubleJump;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            enableDoubleJump = ENABLE_DOUBLE_JUMP.get();
        }
    }

    public void loadDefaultConfig() {
        enableDoubleJump = true;
    }
}
