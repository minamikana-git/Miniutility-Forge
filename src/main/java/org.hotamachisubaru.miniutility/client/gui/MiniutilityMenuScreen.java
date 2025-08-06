package org.hotamachisubaru.miniutility.client.gui;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.hotamachisubaru.miniutility.Menu.MiniutilityMenu;

public class MiniutilityMenuScreen extends AbstractContainerScreen<MiniutilityMenu> {
    public MiniutilityMenuScreen(MiniutilityMenu miniutilityMenu, Inventory inv, Component title) {
        super(miniutilityMenu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166; // 3段分
    }

    @Override
    protected void renderBg(net.minecraft.client.gui.GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(gfx);
        // 必要に応じてテクスチャ描画
    }
}
