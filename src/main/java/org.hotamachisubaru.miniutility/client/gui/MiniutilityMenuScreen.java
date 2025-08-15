package org.hotamachisubaru.miniutility.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.hotamachisubaru.miniutility.Menu.MiniutilityMenu;

public class MiniutilityMenuScreen extends AbstractContainerScreen<MiniutilityMenu> implements MenuAccess<MiniutilityMenu> {

    //クライアント側
    public MiniutilityMenuScreen(MiniutilityMenu miniutilityMenu, Inventory inv, Component title) {
        super(miniutilityMenu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166; // 3段分
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(gfx);
        // 必要に応じてテクスチャ描画
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, partialTicks);
        this.renderTooltip(gfx, mouseX, mouseY);
    }
}
