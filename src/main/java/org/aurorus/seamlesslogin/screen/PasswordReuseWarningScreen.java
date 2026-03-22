package org.aurorus.seamlesslogin.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.aurorus.seamlesslogin.Config;

public class PasswordReuseWarningScreen extends Screen {
    private final Screen backScreen;
    private final Runnable onSave;
    private final long shownAt = System.currentTimeMillis();

    private static final int UNLOCK_DELAY_MS = 5000;

    private Checkbox understoodCheckbox;
    private Button saveButton;

    public PasswordReuseWarningScreen(Screen backScreen, Runnable onSave) {
        super(Component.translatable("screen.seamlesslogin.reuse_title"));
        this.backScreen = backScreen;
        this.onSave     = onSave;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y  = height / 2;

        // "I understand" checkbox — centered
        Component understoodLabel = Component.translatable("screen.seamlesslogin.reuse_understood");
        int cbWidth = Checkbox.getBoxSize(font) + 4 + font.width(understoodLabel);
        understoodCheckbox = Checkbox.builder(understoodLabel, font)
                .pos(cx - cbWidth / 2, y + 10)
                .selected(false)
                .build();
        addRenderableWidget(understoodCheckbox);

        // Back button
        addRenderableWidget(Button.builder(
                Component.translatable("gui.back"),
                btn -> minecraft.setScreen(backScreen)
        ).bounds(cx - 105, y + 40, 100, 20).build());

        // Save button — starts locked
        saveButton = Button.builder(
                Component.translatable("screen.seamlesslogin.save"),
                btn -> {
                    if (understoodCheckbox.selected()) Config.setSkipPasswordReuseWarning(true);
                    onSave.run();
                }
        ).bounds(cx + 5, y + 40, 100, 20).build();
        saveButton.active = false;
        addRenderableWidget(saveButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int cx = width / 2;
        int y  = height / 2;

        long elapsed   = System.currentTimeMillis() - shownAt;
        int  remaining = (int) Math.max(0, (UNLOCK_DELAY_MS - elapsed + 999) / 1000);

        if (remaining == 0 && !saveButton.active) {
            saveButton.active = true;
        }

        graphics.drawCenteredString(font, title, cx, y - 64, 0xFFFF5555);

        graphics.drawCenteredString(font,
                Component.translatable("screen.seamlesslogin.reuse_line1"), cx, y - 44, 0xFFFF5555);
        graphics.drawCenteredString(font,
                Component.translatable("screen.seamlesslogin.reuse_line2"), cx, y - 30, 0xFFFF5555);
        graphics.drawCenteredString(font,
                Component.translatable("screen.seamlesslogin.reuse_line3"), cx, y - 16, 0xFFFF5555);

        if (remaining > 0) {
            graphics.drawCenteredString(font,
                    Component.translatable("screen.seamlesslogin.reuse_countdown", remaining),
                    cx, y + 68, 0xFFAAAAAA);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
