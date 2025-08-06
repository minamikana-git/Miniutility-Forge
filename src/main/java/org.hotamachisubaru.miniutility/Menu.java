package org.hotamachisubaru.miniutility;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class Menu extends Screen {
    public Menu() {
        super(Component.literal("メニュー"));
    }

    @Override
    protected void init() {
        // ボタン追加例
        this.addRenderableWidget(
                net.minecraft.client.gui.components.Button.builder(Component.literal("Hello!"), button -> {
                    // ボタンクリック時の動作
                    this.onClose();
                }).bounds(this.width / 2 - 50, this.height / 2, 100, 20).build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 背景の描画
        this.renderBackground(guiGraphics);

        // タイトルを中央に描画
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        // 既存のウィジェットなども描画
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}

