package org.aurorus.seamlesslogin.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SeamlessLoginConfigScreen extends Screen {
    private final Screen previousScreen;

    private static final int BTN_WIDTH  = 200;
    private static final int BTN_HEIGHT = 20;
    private static final int ROW        = 28;

    public SeamlessLoginConfigScreen(Screen previousScreen) {
        super(Component.translatable("screen.seamlesslogin.config_title"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y  = height / 2 - ROW;

        addRenderableWidget(Button.builder(
                Component.translatable("screen.seamlesslogin.login_patterns"),
                btn -> minecraft.setScreen(new PatternConfigScreen(this))
        ).bounds(cx - BTN_WIDTH / 2, y, BTN_WIDTH, BTN_HEIGHT).build());

        addRenderableWidget(Button.builder(
                Component.translatable("screen.seamlesslogin.pwgen_settings"),
                btn -> minecraft.setScreen(new PasswordGenConfigScreen(this))
        ).bounds(cx - BTN_WIDTH / 2, y + ROW, BTN_WIDTH, BTN_HEIGHT).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                btn -> minecraft.setScreen(previousScreen)
        ).bounds(cx - BTN_WIDTH / 2, y + ROW * 3, BTN_WIDTH, BTN_HEIGHT).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - ROW - 20, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
