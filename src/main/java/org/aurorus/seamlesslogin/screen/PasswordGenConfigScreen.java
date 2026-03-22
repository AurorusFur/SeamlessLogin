package org.aurorus.seamlesslogin.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.aurorus.seamlesslogin.Config;

public class PasswordGenConfigScreen extends Screen {
    private final Screen previousScreen;

    private boolean passphrase;
    private int length;
    private int wordCount;
    private boolean caps;
    private boolean numbers;
    private boolean special;

    private AbstractSliderButton lengthSlider;
    private AbstractSliderButton wordCountSlider;

    private static final int FIELD_WIDTH  = 200;
    private static final int FIELD_HEIGHT = 20;
    private static final int ROW          = 26;

    public PasswordGenConfigScreen(Screen previousScreen) {
        super(Component.translatable("screen.seamlesslogin.pwgen_title"));
        this.previousScreen = previousScreen;
        this.passphrase = Config.usePassphrase;
        this.length     = Config.passwordLength;
        this.wordCount  = Config.passphraseWordCount;
        this.caps       = Config.useCapitalLetters;
        this.numbers    = Config.useNumbers;
        this.special    = Config.useSpecialChars;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y  = height / 2 - (ROW * 5) / 2;

        // Passphrase toggle
        addRenderableWidget(Button.builder(
                passphraseLabel(),
                btn -> {
                    passphrase = !passphrase;
                    btn.setMessage(passphraseLabel());
                    updateFieldStates();
                }
        ).bounds(cx - FIELD_WIDTH / 2, y, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Password length slider (8–64)
        lengthSlider = new AbstractSliderButton(cx - FIELD_WIDTH / 2, y + ROW, FIELD_WIDTH, FIELD_HEIGHT,
                Component.translatable("screen.seamlesslogin.pwgen_length").append(": " + length),
                (length - 8.0) / 56.0) {
            @Override
            protected void updateMessage() {
                int v = (int) Math.round(8 + value * 56);
                setMessage(Component.translatable("screen.seamlesslogin.pwgen_length").append(": " + v));
            }
            @Override
            protected void applyValue() {
                length = (int) Math.round(8 + value * 56);
            }
        };
        addRenderableWidget(lengthSlider);

        // Passphrase word count slider (3–8)
        wordCountSlider = new AbstractSliderButton(cx - FIELD_WIDTH / 2, y + ROW * 2, FIELD_WIDTH, FIELD_HEIGHT,
                Component.translatable("screen.seamlesslogin.pwgen_words").append(": " + wordCount),
                (wordCount - 3.0) / 5.0) {
            @Override
            protected void updateMessage() {
                int v = (int) Math.round(3 + value * 5);
                setMessage(Component.translatable("screen.seamlesslogin.pwgen_words").append(": " + v));
            }
            @Override
            protected void applyValue() {
                wordCount = (int) Math.round(3 + value * 5);
            }
        };
        addRenderableWidget(wordCountSlider);

        // Capital letters toggle
        addRenderableWidget(Button.builder(
                toggleLabel("screen.seamlesslogin.pwgen_caps", caps),
                btn -> {
                    caps = !caps;
                    btn.setMessage(toggleLabel("screen.seamlesslogin.pwgen_caps", caps));
                }
        ).bounds(cx - FIELD_WIDTH / 2, y + ROW * 3, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Numbers toggle
        addRenderableWidget(Button.builder(
                toggleLabel("screen.seamlesslogin.pwgen_numbers", numbers),
                btn -> {
                    numbers = !numbers;
                    btn.setMessage(toggleLabel("screen.seamlesslogin.pwgen_numbers", numbers));
                }
        ).bounds(cx - FIELD_WIDTH / 2, y + ROW * 4, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Special characters toggle
        addRenderableWidget(Button.builder(
                toggleLabel("screen.seamlesslogin.pwgen_special", special),
                btn -> {
                    special = !special;
                    btn.setMessage(toggleLabel("screen.seamlesslogin.pwgen_special", special));
                }
        ).bounds(cx - FIELD_WIDTH / 2, y + ROW * 5, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Save / Cancel
        addRenderableWidget(Button.builder(
                Component.translatable("screen.seamlesslogin.save"),
                btn -> save()
        ).bounds(cx - 105, y + ROW * 6 + 8, 100, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                btn -> minecraft.setScreen(previousScreen)
        ).bounds(cx + 5, y + ROW * 6 + 8, 100, 20).build());

        updateFieldStates();
    }

    private void updateFieldStates() {
        if (lengthSlider    != null) lengthSlider.active    = !passphrase;
        if (wordCountSlider != null) wordCountSlider.active = passphrase;
    }

    private Component passphraseLabel() {
        return Component.translatable(passphrase
                ? "screen.seamlesslogin.pwgen_passphrase_on"
                : "screen.seamlesslogin.pwgen_passphrase_off");
    }

    private Component toggleLabel(String key, boolean on) {
        return Component.translatable(key).append(": ").append(
                Component.translatable(on ? "screen.seamlesslogin.on" : "screen.seamlesslogin.off"));
    }

    private void save() {
        Config.setPasswordGenConfig(passphrase, length, wordCount, caps, numbers, special);
        minecraft.setScreen(previousScreen);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int cx = width / 2;
        int y  = height / 2 - (ROW * 5) / 2;
        graphics.drawCenteredString(font, title, cx, y - 24, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
