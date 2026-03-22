package org.aurorus.seamlesslogin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SpriteIconButton extends AbstractButton {
    private static final ResourceLocation BTN_NORMAL =
            ResourceLocation.withDefaultNamespace("widget/button");
    private static final ResourceLocation BTN_HOVER =
            ResourceLocation.withDefaultNamespace("widget/button_highlighted");

    private ResourceLocation icon;
    private final Runnable onPress;

    public SpriteIconButton(int x, int y, ResourceLocation icon, Component narration, Runnable onPress) {
        super(x, y, 20, 20, narration);
        this.icon    = icon;
        this.onPress = onPress;
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    @Override
    public void onPress() {
        onPress.run();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation bg = (isHovered() && active) ? BTN_HOVER : BTN_NORMAL;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, bg,   getX(),     getY(),     20, 20);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, getX() + 2, getY() + 2, 16, 16);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
