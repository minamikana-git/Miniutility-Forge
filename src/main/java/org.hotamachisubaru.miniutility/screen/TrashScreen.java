package org.hotamachisubaru.miniutility.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TrashScreen extends Screen {
    public TrashScreen() {
        super(Component.literal("ゴミ箱"));
    }

    @Override
    public void init() {
        // 「捨てる」ボタン
        addRenderableWidget(Button.builder(Component.literal("捨てる"), btn -> {
            this.minecraft.player.sendSystemMessage(Component.literal("アイテムを削除しました。"));
            onClose();
        }).bounds(this.width / 2 - 40, this.height / 2 - 10, 80, 20).build());

        // 「キャンセル」ボタン
        addRenderableWidget(Button.builder(Component.literal("やめる"), btn -> onClose())
                .bounds(this.width / 2 - 40, this.height / 2 + 20, 80, 20).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gfx);
        gfx.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
