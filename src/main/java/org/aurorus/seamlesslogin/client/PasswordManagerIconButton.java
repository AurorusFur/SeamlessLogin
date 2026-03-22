package org.aurorus.seamlesslogin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PasswordManagerIconButton extends AbstractButton {
    private static final ResourceLocation ICON =
            ResourceLocation.fromNamespaceAndPath("seamlesslogin", "password_manager");
    private static final ResourceLocation BTN_NORMAL =
            ResourceLocation.withDefaultNamespace("widget/button");
    private static final ResourceLocation BTN_HOVER =
            ResourceLocation.withDefaultNamespace("widget/button_highlighted");

    private final Runnable onPress;

    public PasswordManagerIconButton(int x, int y, Runnable onPress) {
        super(x, y, 20, 20, Component.translatable("screen.seamlesslogin.title_short"));
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        onPress.run();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation bg = (isHovered() && active) ? BTN_HOVER : BTN_NORMAL;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, bg, getX(), getY(), 20, 20);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON, getX() + 2, getY() + 2, 16, 16);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
